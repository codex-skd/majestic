package com.skd.majestic.content.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The holes in the Portal, read from {@code data/majestic/majestic_portal_gaps.json}.
 *
 * <p>They are data, not geometry, on purpose. The rebuild has to know exactly which cells it fills, and
 * deriving them from the piece at runtime means a rule like "air with stone all around", which is one
 * regenerated facade away from silently matching the wrong cells. The file is checked against
 * {@code portal_01.nbt} by {@code scripts/validate_portal_gaps.py} before a release, so a piece that
 * moves its gaps breaks the build rather than the game.</p>
 *
 * <p>Offsets are relative to the piece origin in the template. Which piece that is in the world is not
 * assumed: every piece is offered and the one whose box actually contains a still-empty cell wins,
 * which double-checks the mapping instead of trusting it.</p>
 */
public final class PortalGaps {

    private static final Logger LOG = LogUtils.getLogger();
    private static final String CONFIG = "/data/majestic/majestic_portal_gaps.json";

    public static final String STAGE_OUTER = "outer";
    public static final String STAGE_DEPTHS = "depths";

    /**
     * One hole, the block that belongs in it, and the stage it belongs to: {@code outer} for the Portal's
     * arch (filled by the Receptacle) or {@code depths} for the two inner arches (filled by the
     * Receptacle of the Depths).
     */
    public record Gap(String id, BlockPos offset, Block block, String stage) {
        public Gap(String id, BlockPos offset, Block block) {
            this(id, offset, block, STAGE_OUTER);
        }
    }

    /**
     * A gap that is still open, together with the world position it resolves to.
     *
     * <p>The receptacle needs both halves at once: which block a gap wants, and where that gap is.
     * Resolving to bare positions loses the first half, so a caller holding only positions cannot tell
     * which of them is waiting for a keystone and which for a brick.</p>
     */
    public record PendingGap(Gap gap, BlockPos world) {}

    private static List<Gap> gaps = List.of();
    private static int maxPayload;
    private static boolean loaded;

    private PortalGaps() {}

    /** Gaps of this stage that have not been filled yet. */
    public static synchronized List<Gap> pending(StructureStart start, net.minecraft.world.level.Level level,
                                                 String stage) {
        if (!loaded) {
            load();
        }
        List<Gap> pending = new ArrayList<>();
        for (Gap gap : gaps) {
            if (!gap.stage().equals(stage)) {
                continue;
            }
            BlockPos world = resolve(start, level, gap);
            if (world != null) {
                pending.add(gap);
            }
        }
        return pending;
    }

    /** The stage a block is placed in: the celestial and infernal stones raise the inner arches. */
    public static String stageOf(Block block) {
        return block.builtInRegistryHolder().key().location().getPath().equals("celestial_stone")
                || block.builtInRegistryHolder().key().location().getPath().equals("infernal_stone")
                ? STAGE_DEPTHS : STAGE_OUTER;
    }

    /** The gaps of this stage still open, each with the world position it resolves to. */
    public static synchronized List<PendingGap> pendingGaps(StructureStart start,
                                                            net.minecraft.world.level.Level level, String stage) {
        if (!loaded) {
            load();
        }
        List<PendingGap> out = new ArrayList<>();
        for (Gap gap : gaps) {
            if (!gap.stage().equals(stage)) {
                continue;
            }
            BlockPos world = resolve(start, level, gap);
            if (world != null) {
                out.add(new PendingGap(gap, world));
            }
        }
        return out;
    }

    /** The gaps of this stage, but with each offset already mapped into the world. */
    public static synchronized List<BlockPos> pendingPositions(StructureStart start,
                                                               net.minecraft.world.level.Level level, String stage) {
        List<BlockPos> out = new ArrayList<>();
        for (PendingGap pending : pendingGaps(start, level, stage)) {
            out.add(pending.world());
        }
        return out;
    }

    /** How many blocks the receptacle will take before it stops asking. */
    public static synchronized int maxPayload() {
        if (!loaded) {
            load();
        }
        return maxPayload;
    }

    /**
     * Total number of gaps that want this block, straight from the data file.
     *
     * <p>The Receptacle's window uses this as its per-slot cap. It is the whole file's count and not
     * the pending one on purpose: the window only ever fills a fresh socket, and placement starts
     * only when the window closes with the exact counts, so no gap is filled while a window is open.
     * Reading it from the file also keeps the client and the server in agreement without shipping the
     * counts over the network.</p>
     */
    public static synchronized int countFor(Block block) {
        if (!loaded) {
            load();
        }
        int count = 0;
        for (Gap gap : gaps) {
            if (gap.block() == block) {
                count++;
            }
        }
        return count;
    }

    /**
     * Maps an offset from a data file onto the piece that really holds it, with the very transform
     * the gaps use: piece origin plus offset, and the cell has to fall inside that piece's box.
     *
     * <p>Shared on purpose: {@code majestic_portal_anchors.json} waits spots and
     * {@code majestic_portal_gaps.json} holes are the same kind of claim about the piece — a cell
     * named by coordinates — and two implementations of the mapping is two ways for them to drift
     * apart.</p>
     *
     * @return the world position of the cell, or null when no piece of this instance holds it
     */
    public static @Nullable BlockPos resolveOffset(StructureStart start, net.minecraft.world.level.Level level,
                                                   BlockPos offset) {
        List<BlockPos> candidates = candidates(start, offset);
        if (candidates.isEmpty()) {
            return null;
        }
        if (candidates.size() > 1) {
            LOG.warn("El ancla {} encaja en {} piezas; se usa el primero. Revisa la pieza del Portal.",
                    offset, candidates.size());
        }
        return candidates.get(0);
    }

    /** Every cell of this instance that the given offset lands in, one per piece that contains it. */
    private static List<BlockPos> candidates(StructureStart start, BlockPos offset) {
        List<BlockPos> out = new ArrayList<>();
        for (StructurePiece piece : start.getPieces()) {
            BoundingBox box = piece.getBoundingBox();
            BlockPos world;
            if (piece instanceof PoolElementStructurePiece pool) {
                // Jigsaw pieces are placed rotated: template origin + rotated offset, exactly the
                // transform StructureTemplate.placeInWorld uses (no mirror, pivot at zero).
                world = pool.getPosition().offset(
                        StructureTemplate.transform(offset, Mirror.NONE, pool.getRotation(), BlockPos.ZERO));
            } else {
                world = new BlockPos(box.minX(), box.minY(), box.minZ()).offset(offset);
            }
            if (box.isInside(world)) {
                out.add(world);
            }
        }
        return out;
    }

    /**
     * Maps a gap onto the piece that really holds it.
     *
     * @return the world position of the hole, or null when it is already filled
     */
    private static BlockPos resolve(StructureStart start, net.minecraft.world.level.Level level, Gap gap) {
        List<BlockPos> candidates = new ArrayList<>();
        for (BlockPos world : candidates(start, gap.offset())) {
            if (level.getBlockState(world).isAir()) {
                candidates.add(world);
            }
        }
        if (candidates.isEmpty()) {
            // Filled, or the piece moved. Either way there is nothing left to put here.
            return null;
        }
        if (candidates.size() > 1) {
            LOG.warn("El hueco '{}' encaja en {} piezas; se usa el primero. Revisa la pieza del Portal.",
                    gap.id(), candidates.size());
        }
        return candidates.get(0);
    }

    /** The block a gap still needs, so the receptacle can refuse the rest of the Portal's stone. */
    public static synchronized Block requiredFor(StructureStart start, net.minecraft.world.level.Level level,
                                                 net.minecraft.world.item.ItemStack stack, String stage) {
        if (!loaded) {
            load();
        }
        for (Gap gap : pending(start, level, stage)) {
            if (stack.is(gap.block().asItem())) {
                return gap.block();
            }
        }
        return null;
    }

    private static void load() {
        loaded = true;
        gaps = List.of();
        maxPayload = 0;
        try (InputStream in = PortalGaps.class.getResourceAsStream(CONFIG)) {
            if (in == null) {
                LOG.error("No se encuentra {}. El Portal no se podra terminar.", CONFIG);
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            maxPayload = root.get("max_payload").getAsInt();
            JsonArray array = root.getAsJsonArray("gaps");
            List<Gap> parsed = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject gap = element.getAsJsonObject();
                JsonArray offset = gap.getAsJsonArray("offset");
                ResourceLocation block = ResourceLocation.parse(gap.get("block").getAsString());
                Block registered = BuiltInRegistries.BLOCK.get(block);
                if (registered == null || registered.defaultBlockState().isAir()) {
                    LOG.error("El hueco '{}' pide el bloque desconocido {}. Se ignora.", gap.get("id"), block);
                    continue;
                }
                parsed.add(new Gap(gap.get("id").getAsString(),
                        new BlockPos(offset.get(0).getAsInt(), offset.get(1).getAsInt(), offset.get(2).getAsInt()),
                        registered,
                        gap.has("stage") ? gap.get("stage").getAsString() : STAGE_OUTER));
            }
            gaps = List.copyOf(parsed);
            LOG.info("Portal: {} huecos cargados, receptor de hasta {} bloques.", parsed.size(), maxPayload);
        } catch (IOException | RuntimeException e) {
            LOG.error("No se pudo leer {}. El Portal no se podra terminar.", CONFIG, e);
        }
    }
}