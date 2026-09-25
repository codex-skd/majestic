package com.skd.majestic.content.event;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.MajesticEntities;
import com.skd.majestic.content.entity.boss.MajesticArenaData;
import com.skd.majestic.content.entity.boss.WardenOfTheGate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Spawns the Warden of the Gate the first time a player steps into an Observatory arena.
 *
 * <p>Server-side only, throttled to once every 20 ticks per player. The arena is identified by
 * its jigsaw piece and marked as used in {@link MajesticArenaData}, so a structure only ever
 * spawns one boss (also across restarts). A defeated boss does not clear the mark.
 */
public final class ArenaSpawnEvents {

    private static final ResourceKey<Structure> OBSERVATORY_KEY = ResourceKey.create(
            Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "observatory"));

    private static final String ARENA_TEMPLATE = "majestic:observatory/arena";

    private static final String ARENA_DATA_ID = "majestic_arenas";

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        if (player.tickCount % 20 != 0) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        Structure observatory = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(OBSERVATORY_KEY);
        if (observatory == null) {
            return;
        }

        BlockPos pos = player.blockPosition();
        StructureStart start = level.structureManager().getStructureWithPieceAt(pos, observatory);
        if (!start.isValid()) {
            return;
        }

        for (StructurePiece piece : start.getPieces()) {
            if (!(piece instanceof PoolElementStructurePiece poolPiece)) {
                continue;
            }
            if (!isArenaPiece(poolPiece.getElement())) {
                continue;
            }
            BoundingBox box = poolPiece.getBoundingBox();
            if (!box.isInside(pos)) {
                continue;
            }
            trySpawnBoss(level, box);
            return;
        }
    }

    /**
     * Identifies the arena pool element.
     *
     * <p>{@link SinglePoolElement}'s {@code template} field is protected and has no getter in
     * 1.21.1, so the element is matched by its string form, which reads
     * {@code "Single[Left[majestic:observatory/arena]]"}.
     */
    private static boolean isArenaPiece(StructurePoolElement element) {
        if (!(element instanceof SinglePoolElement)) {
            return false;
        }
        return element.toString().contains(ARENA_TEMPLATE);
    }

    private static void trySpawnBoss(ServerLevel level, BoundingBox box) {
        long arenaKey = BlockPos.asLong(box.minX(), box.minY(), box.minZ());
        MajesticArenaData arenaData = level.getDataStorage().computeIfAbsent(MajesticArenaData.factory(), ARENA_DATA_ID);
        if (arenaData.isSpawned(arenaKey)) {
            return;
        }

        WardenOfTheGate boss = MajesticEntities.WARDEN_OF_THE_GATE.get().create(level);
        if (boss == null) {
            return;
        }

        int centerX = (box.minX() + box.maxX()) / 2;
        int centerZ = (box.minZ() + box.maxZ()) / 2;
        int y = box.minY() + 1;
        boss.moveTo(centerX + 0.5, y, centerZ + 0.5, 0.0f, 0.0f);
        while (y < box.maxY() && !level.noCollision(boss)) {
            y++;
            boss.moveTo(centerX + 0.5, y, centerZ + 0.5, 0.0f, 0.0f);
        }

        // The center sits on the arena floor, so the vertical half-extent is the full piece height:
        // players up on the ruined ring/stair seats still count as inside the arena.
        boss.setArena(new BlockPos(centerX, box.minY() + 1, centerZ),
                box.getXSpan() / 2, box.getYSpan(), box.getZSpan() / 2);
        boss.finalizeSpawn(level, level.getCurrentDifficultyAt(boss.blockPosition()), MobSpawnType.STRUCTURE, null);
        level.addFreshEntity(boss);
        arenaData.markSpawned(arenaKey);

        Majestic.LOGGER.info("Spawned Warden of the Gate at {} for observatory arena {}",
                boss.blockPosition(), arenaKey);
    }

    private ArenaSpawnEvents() {}
}
