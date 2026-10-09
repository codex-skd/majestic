package com.skd.majestic.content.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The Portal's waiting spots, read from {@code data/majestic/majestic_portal_anchors.json}.
 *
 * <p>Same idea as {@link PortalGaps} and for the same reason: where the Lantern-Bearer stands while
 * it waits is a claim about the piece (\"in front of the east pillar\"), and a claim stated as
 * coordinates in data can be checked against {@code portal_01.nbt} by
 * {@code scripts/validate_portal_anchors.py} before a release. Stated as geometry at runtime it
 * could only be checked by walking into the game.</p>
 *
 * <p>The mapping onto the world is {@link PortalGaps#resolveOffset}: the same transform the gaps
 * use, so a spot and a hole can never disagree about where a cell is.</p>
 */
public final class PortalAnchors {

    private static final Logger LOG = LogUtils.getLogger();
    private static final String CONFIG = "/data/majestic/majestic_portal_anchors.json";

    private static List<BlockPos> lanternSpots = List.of();
    private static boolean loaded;

    private PortalAnchors() {}

    /**
     * The cells in front of the Portal's columns where the Lantern-Bearer waits, in world
     * coordinates. The cell is the one the ghost's feet stand in; the floor is the cell below it.
     *
     * @return every spot that resolves into this Portal instance, in file order
     */
    public static synchronized List<BlockPos> lanternBearerSpots(StructureStart start, Level level) {
        if (!loaded) {
            load();
        }
        List<BlockPos> out = new ArrayList<>();
        for (BlockPos offset : lanternSpots) {
            BlockPos world = PortalGaps.resolveOffset(start, level, offset);
            if (world != null) {
                out.add(world);
            }
        }
        return out;
    }

    private static void load() {
        loaded = true;
        lanternSpots = List.of();
        try (InputStream in = PortalAnchors.class.getResourceAsStream(CONFIG)) {
            if (in == null) {
                LOG.error("No se encuentra {}. El farolero no tendra donde esperar.", CONFIG);
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            lanternSpots = List.copyOf(readOffsets(root.getAsJsonArray("lantern_bearer_spots")));
            LOG.info("Portal: {} puntos de espera del farolero cargados.", lanternSpots.size());
        } catch (IOException | RuntimeException e) {
            LOG.error("No se pudo leer {}. El farolero no tendra donde esperar.", CONFIG, e);
        }
    }

    private static List<BlockPos> readOffsets(JsonArray array) {
        List<BlockPos> out = new ArrayList<>();
        if (array == null) {
            return out;
        }
        for (JsonElement element : array) {
            JsonArray offset = element.getAsJsonArray();
            if (offset.size() != 3) {
                LOG.error("El punto de espera {} no son 3 componentes; se ignora.", offset);
                continue;
            }
            out.add(new BlockPos(offset.get(0).getAsInt(), offset.get(1).getAsInt(), offset.get(2).getAsInt()));
        }
        return out;
    }
}
