package com.skd.majestic.content.event;

import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.LanternBearer;
import com.skd.majestic.content.entity.MajesticEntities;
import com.skd.majestic.content.item.JournalPageItem;
import com.skd.majestic.content.item.JournalProgress;
import com.skd.majestic.content.block.PortalGaps;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.content.worldgen.StructureKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;

/**
 * Spawns the Lantern-Bearer at the centre of the Arcane Portal's rune plaza when a player walks in,
 * and waits there for them.
 *
 * <p>The trigger is deliberately narrow. It fires once per player per Portal instance, and only for a
 * player who is actually following the book: they have to have reached Chapter III (the Warden's page)
 * and be carrying the Almanac. Without those gates the ghost would interrupt a player who has not got
 * that far, or hand over a page they cannot read. A player who already carries the unread page counts
 * as having been given it.</p>
 *
 * <p>Being met is recorded when the page is actually handed over, not when the ghost appears, so a
 * player who walks away mid-encounter — or a server restart — leaves the encounter available instead of
 * silently losing it. The single-ghost check below searches the whole Portal, because the plaza centre
 * is regularly further from where a player enters than any per-position radius would reach.</p>
 */
public final class LanternBearerTrigger {

    /** Checked once a second per player: this cannot afford to be per-tick. */
    private static final int CHECK_INTERVAL_TICKS = 20;
    /** Blocks of slack around a Portal piece that still count as "walking into it". */
    private static final int ENTRANCE_MARGIN = 2;
    /** Slack around the whole Portal when looking for a ghost that is already waiting. */
    private static final int ACTIVE_SEARCH_MARGIN = 16;
    /** How far above the cell's floor the ghost's feet stand when it materialises. */
    private static final double STAND_HEIGHT = 0.1;
    /** Chunks searched around the player by the developer force-spawn: four chunks is 64 blocks. */
    private static final int FORCE_SEARCH_CHUNK_RADIUS = 4;
    /**
     * Local offset, in the unrotated Portal piece, of the rune plaza's centre: the cell above the
     * carved keystone that sits at the middle of the floor (piece-local y=0). The piece is 41x31x41,
     * so (20, 1, 14) is its middle. The offset is mapped through {@link PortalGaps#resolveOffset},
     * the same rotation-aware transform the gaps and anchors use, so a regenerated piece moves the
     * ghost with it instead of leaving a second, hand-rolled coordinate mapping to drift.
     */
    private static final BlockPos CENTRE_OFFSET = new BlockPos(20, 1, 14);

    private static final ResourceLocation SEARCH_CHAPTER =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_2_1");

    // Stored under Player.PERSISTED_NBT_TAG so it survives death, like the Almanac flag.
    private static final String MET_KEY = "majestic.lantern_bearer_met";

    /**
     * Outcome of a force-spawn requested through {@code /majestic lantern}: whether a ghost was placed
     * and an English reason the caller can print verbatim.
     */
    public record ForceResult(boolean success, String reason) {}

    /** How a materialisation attempt ended, so both the normal trigger and the force path can report. */
    private enum Placement {
        PLACED,
        /** The plaza centre or the cell above it is no longer air. */
        PLAZA_BLOCKED,
        /** The entity type produced no instance, or the level refused it. */
        ENTITY_FAILED
    }

    private LanternBearerTrigger() {}

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % CHECK_INTERVAL_TICKS != 0) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;

        String name = player.getName().getString();

        // The book gates: Chapter III reached, still carrying the Almanac, and not already given it.
        if (!JournalProgress.holds(player, "journey/chapter_3")) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: missing chapter_3.", name);
            return;
        }
        if (AdvancementHooks.has(player, SEARCH_CHAPTER)) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: already has chapter_2_1.", name);
            return;
        }
        if (!JournalPageItem.hasAlmanac(player)) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: no Almanac.", name);
            return;
        }

        // Carrying an unread page counts as already having been given it. The advancement alone is not
        // enough: a player who is still holding the very page it handed over was visited again.
        if (player.getInventory().contains(new ItemStack(MajesticItems.LANTERN_PAGE.get()))) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: already carrying the page.", name);
            return;
        }

        Optional<StructureStart> found = findPortal(level, player.blockPosition());
        if (found.isEmpty()) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: not inside an Arcane Portal.", name);
            return;
        }

        String instanceId = instanceId(found.get());
        if (hasMet(player, instanceId)) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: already met at {}.", name, instanceId);
            return;
        }
        // One ghost per Portal, not one per player position: it waits at the plaza centre now, which
        // is usually further than any radius from where the player walks in, and two ghosts would
        // hand the page over twice. The whole structure counts as "already running".
        if (!level.getEntitiesOfClass(LanternBearer.class,
                AABB.of(StructureKeys.boundsOf(found.get()).inflatedBy(ACTIVE_SEARCH_MARGIN))).isEmpty()) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: one is already waiting at {}.", name, instanceId);
            return;
        }

        Placement placement = spawn(level, player, found.get(), instanceId);
        if (placement != Placement.PLACED) {
            Majestic.LOGGER.debug("Lantern-Bearer not spawned for {}: {} at {}.", name, placement, instanceId);
        }
    }

    /**
     * Developer escape hatch: materialises the Lantern-Bearer at the nearest Arcane Portal's plaza
     * centre, ignoring every story gate (Chapter III, the Almanac, the carried page, already met).
     *
     * <p>Only the physical conditions can fail it: no Portal found within range, a plaza cell that is
     * no longer air, or the level refusing the entity. Used by {@code /majestic lantern} so the
     * encounter can be tested from creative with zero advancements.</p>
     *
     * @return whether a ghost was placed, with an English reason for the caller to report
     */
    public static ForceResult forceSpawn(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return new ForceResult(false, "The Lantern-Bearer can only be forced on a server level.");
        }

        Optional<StructureStart> found = findNearestPortal(level, player.blockPosition());
        if (found.isEmpty()) {
            return new ForceResult(false, "No Arcane Portal within range.");
        }

        String instanceId = instanceId(found.get());
        Placement placement = spawn(level, player, found.get(), instanceId);
        return switch (placement) {
            case PLACED -> new ForceResult(true, "Lantern-Bearer forced at the Arcane Portal.");
            case PLAZA_BLOCKED -> new ForceResult(false,
                    "The Portal's plaza centre is blocked by other blocks.");
            case ENTITY_FAILED -> new ForceResult(false, "Could not materialise the Lantern-Bearer.");
        };
    }

    /**
     * Called by the ghost the moment the page actually changes hands. Recorded here rather than on
     * spawn so an interrupted encounter can be retried.
     */
    public static void markMet(ServerPlayer player, String instanceId) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        ListTag met = persisted.getList(MET_KEY, Tag.TAG_STRING);
        if (met.contains(instanceId)) {
            return;
        }
        met.add(net.minecraft.nbt.StringTag.valueOf(instanceId));
        persisted.put(MET_KEY, met);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }

    /**
     * Finds the Portal instance containing {@code pos}.
     *
     * @return the start of the enclosing instance, or empty when the position is not inside a Portal
     */
    private static Optional<StructureStart> findPortal(ServerLevel level, BlockPos pos) {
        return StructureKeys.findStartAt(level, pos, StructureKeys.ARCANE_PORTAL)
                .filter(start -> StructureKeys.isInsideAnyPiece(start, pos, ENTRANCE_MARGIN));
    }

    /**
     * Finds the nearest Arcane Portal whose start lies within {@link #FORCE_SEARCH_CHUNK_RADIUS}
     * chunks of the player, ranked by the centre of the structure's bounding box.
     *
     * <p>Uses the same {@link StructureKeys#findStartAt} lookup as the normal trigger, probed once per
     * chunk at the player's own height, so it hits the Portal the developer is standing in or beside
     * without walking the whole generation registry by hand.</p>
     */
    private static Optional<StructureStart> findNearestPortal(ServerLevel level, BlockPos origin) {
        int baseChunkX = origin.getX() >> 4;
        int baseChunkZ = origin.getZ() >> 4;
        StructureStart nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int dx = -FORCE_SEARCH_CHUNK_RADIUS; dx <= FORCE_SEARCH_CHUNK_RADIUS; dx++) {
            for (int dz = -FORCE_SEARCH_CHUNK_RADIUS; dz <= FORCE_SEARCH_CHUNK_RADIUS; dz++) {
                BlockPos probe = new BlockPos(((baseChunkX + dx) << 4) + 8, origin.getY(),
                        ((baseChunkZ + dz) << 4) + 8);
                Optional<StructureStart> found =
                        StructureKeys.findStartAt(level, probe, StructureKeys.ARCANE_PORTAL);
                if (found.isEmpty()) {
                    continue;
                }
                double distance = distanceSqr(origin, boundsCentre(StructureKeys.boundsOf(found.get())));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = found.get();
                }
            }
        }
        return Optional.ofNullable(nearest);
    }

    /** The centre of a structure's combined bounding box, used to rank Portals by distance. */
    private static BlockPos boundsCentre(BoundingBox bounds) {
        return new BlockPos((bounds.minX() + bounds.maxX()) / 2,
                (bounds.minY() + bounds.maxY()) / 2,
                (bounds.minZ() + bounds.maxZ()) / 2);
    }

    private static double distanceSqr(BlockPos a, BlockPos b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Materialises the ghost at the centre of the Portal's rune plaza, waiting.
     *
     * <p>The spot is fixed by the piece — the cell above the carved keystone at the middle of the
     * floor — and mapped into the world with the rotation-aware transform the gaps use. The cell is
     * checked to still be open before it is used, so a player who built over the plaza gets no ghost
     * walled into solid stone.</p>
     *
     * @return how the attempt ended, for the trigger's diagnostics and the force-spawn report
     */
    private static Placement spawn(ServerLevel level, ServerPlayer player, StructureStart start, String instanceId) {
        BlockPos spot = PortalGaps.resolveOffset(start, level, CENTRE_OFFSET);
        if (spot == null || !level.getBlockState(spot).isAir() || !level.getBlockState(spot.above()).isAir()) {
            return Placement.PLAZA_BLOCKED;
        }

        LanternBearer ghost = MajesticEntities.LANTERN_BEARER.get().create(level);
        if (ghost == null) {
            return Placement.ENTITY_FAILED;
        }
        // Feet just off the floor of the cell, the same hover it keeps while it waits.
        ghost.moveTo(spot.getX() + 0.5, spot.getY() + STAND_HEIGHT, spot.getZ() + 0.5,
                player.getYRot(), 0.0f);
        if (level.addFreshEntity(ghost)) {
            ghost.beginFor(player, instanceId);
            return Placement.PLACED;
        }
        return Placement.ENTITY_FAILED;
    }

    private static boolean hasMet(ServerPlayer player, String instanceId) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persisted.getList(MET_KEY, Tag.TAG_STRING).contains(instanceId);
    }

    /** Same scheme {@code expedition_core} uses to name a concrete structure instance. */
    private static String instanceId(StructureStart start) {
        return StructureKeys.ARCANE_PORTAL.location() + "@" + start.getChunkPos().toLong();
    }
}
