package com.skd.majestic.content.event;

import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.LanternBearer;
import com.skd.majestic.content.entity.MajesticEntities;
import com.skd.majestic.content.item.JournalPageItem;
import com.skd.majestic.content.item.JournalProgress;
import com.skd.majestic.content.worldgen.StructureKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import com.skd.majestic.content.item.MajesticItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;

/**
 * Spawns the Lantern-Bearer when a player walks into the Arcane Portal.
 *
 * <p>The trigger is deliberately narrow. It fires once per player per Portal instance, and only for a
 * player who is actually following the book: they have to have reached Chapter III (the Warden's page)
 * and be carrying the Almanac. Without those gates the ghost would interrupt a player who has not got
 * that far, or hand over a page they cannot read.</p>
 *
 * <p>Being met is recorded when the page is actually handed over, not when the ghost appears, so a
 * player who walks away mid-encounter — or a server restart — leaves the encounter available instead of
 * silently losing it. A second ghost is suppressed by the active-entity check below.</p>
 */
public final class LanternBearerTrigger {

    /** Checked once a second per player: this cannot afford to be per-tick. */
    private static final int CHECK_INTERVAL_TICKS = 20;
    /** Blocks of slack around a Portal piece that still count as "walking into it". */
    private static final int ENTRANCE_MARGIN = 2;
    /** How far off the player the ghost materialises, so it arrives instead of standing there. */
    private static final double SPAWN_DISTANCE = 8.0;
    /** A ghost already this close is the same encounter; do not start a second one. */
    private static final double ACTIVE_SEARCH_RADIUS = 24.0;

    private static final ResourceLocation SEARCH_CHAPTER =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_2_1");

    // Stored under Player.PERSISTED_NBT_TAG so it survives death, like the Almanac flag.
    private static final String MET_KEY = "majestic.lantern_bearer_met";

    private LanternBearerTrigger() {}

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % CHECK_INTERVAL_TICKS != 0) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;

        // The book gates: Chapter III reached, still carrying the Almanac, and not already given it.
        if (!JournalProgress.holds(player, "journey/chapter_3")) return;
        if (AdvancementHooks.has(player, SEARCH_CHAPTER)) return;
        if (!JournalPageItem.hasAlmanac(player)) return;

        // Carrying an unread page counts as already having been given it. The advancement alone is not
        // enough: a player who is still holding the very page it handed over was visited again.
        if (player.getInventory().contains(new ItemStack(MajesticItems.LANTERN_PAGE.get()))) {
            return;
        }

        Optional<StructureStart> found = findPortal(level, player.blockPosition());
        if (found.isEmpty()) return;

        String instanceId = instanceId(found.get());
        if (hasMet(player, instanceId)) return;
        if (!level.getEntitiesOfClass(LanternBearer.class,
                player.getBoundingBox().inflate(ACTIVE_SEARCH_RADIUS)).isEmpty()) {
            return;
        }

        spawn(level, player, found.get(), instanceId);
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

    private static void spawn(ServerLevel level, ServerPlayer player, StructureStart start, String instanceId) {
        // Materialise at the edge of vision, but keep the ghost inside the Portal's forecourt rather
        // than out in the open where it would look like an ordinary mob wandering past.
        double angle = player.getRandom().nextFloat() * (Math.PI * 2.0);
        Vec3 wanted = player.position().add(
                Math.cos(angle) * SPAWN_DISTANCE,
                0.0,
                Math.sin(angle) * SPAWN_DISTANCE);

        BoundingBox forecourt = StructureKeys.boundsOf(start).inflatedBy(ENTRANCE_MARGIN + 2);
        Vec3 at = new Vec3(
                clamp(wanted.x, forecourt.minX(), forecourt.maxX()),
                player.getY() + 1.1,
                clamp(wanted.z, forecourt.minZ(), forecourt.maxZ()));

        LanternBearer ghost = MajesticEntities.LANTERN_BEARER.get().create(level);
        if (ghost == null) {
            return;
        }
        ghost.moveTo(at.x, at.y, at.z, player.getYRot(), 0.0f);
        if (level.addFreshEntity(ghost)) {
            ghost.beginFor(player, instanceId);
        }
    }

    private static boolean hasMet(ServerPlayer player, String instanceId) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persisted.getList(MET_KEY, Tag.TAG_STRING).contains(instanceId);
    }

    /** Same scheme {@code expedition_core} uses to name a concrete structure instance. */
    private static String instanceId(StructureStart start) {
        return StructureKeys.ARCANE_PORTAL.location() + "@" + start.getChunkPos().toLong();
    }

    private static double clamp(double value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}