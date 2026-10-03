package com.skd.majestic.content.event;

import com.skd.majestic.content.worldgen.StructureKeys;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

/**
 * Locates the Observatory arena piece at a given position.
 *
 * <p>The arena is identified by its jigsaw piece template ({@code majestic:observatory/arena}).
 * This is the shared lookup used both to place the altar-summoned Warden and to size its arena
 * bounds; the Warden is no longer spawned automatically on entry.
 */
public final class ObservatoryArenas {

    private static final String ARENA_TEMPLATE = "majestic:observatory/arena";

    /**
     * Returns the bounding box of the arena piece containing {@code pos}, if any.
     */
    public static Optional<BoundingBox> findArenaBox(ServerLevel level, BlockPos pos) {
        Optional<StructureStart> found = StructureKeys.findStartAt(level, pos, StructureKeys.OBSERVATORY);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        StructureStart start = found.get();

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
            return Optional.of(box);
        }
        return Optional.empty();
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

    private ObservatoryArenas() {}
}
