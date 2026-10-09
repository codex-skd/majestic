package com.skd.majestic.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * Lets the sky island's waterfalls fall.
 *
 * <p>The island's template holds four vertical columns of falling water that end at the island's lowest
 * layer. Blocks placed by world generation never get a fluid tick, so the water just stops there instead
 * of continuing down through the air. When a freshly generated chunk holds part of the island, every
 * water block with nothing solid under it gets a tick scheduled and vanilla fluid flow does the rest.</p>
 */
public final class SkyIslandWater {

    private SkyIslandWater() {}

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        ChunkAccess chunk = event.getChunk();
        for (var entry : chunk.getAllStarts().entrySet()) {
            if (!level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE)
                    .getKey(entry.getKey()).equals(StructureKeys.SKY_ISLAND.location())) {
                continue;
            }
            StructureStart start = entry.getValue();
            if (start.isValid()) {
                scheduleWater(level, chunk, start.getBoundingBox());
            }
        }
    }

    private static void scheduleWater(ServerLevel level, ChunkAccess chunk, BoundingBox box) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = Math.max(box.minX(), chunkPos.getMinBlockX());
        int maxX = Math.min(box.maxX(), chunkPos.getMaxBlockX());
        int minZ = Math.max(box.minZ(), chunkPos.getMinBlockZ());
        int maxZ = Math.min(box.maxZ(), chunkPos.getMaxBlockZ());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    pos.set(x, y, z);
                    if (!chunk.getFluidState(pos).is(Fluids.WATER)
                            && !chunk.getFluidState(pos).is(Fluids.FLOWING_WATER)) {
                        continue;
                    }
                    BlockPos below = pos.below();
                    if (!chunk.getBlockState(below).isSolid()
                            && chunk.getFluidState(below).isEmpty()) {
                        level.scheduleTick(pos.immutable(), Fluids.WATER, 1);
                    }
                }
            }
        }
    }
}
