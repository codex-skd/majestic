package com.skd.majestic.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Decorative pillar of the Astral Altar set. The hand-made model is narrower than a full cube,
 * so the collision shape is built from the three stacked pixel boxes that make up the pillar.
 */
public class AstralPillarBlock extends Block {

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
            Block.box(3.0, 2.0, 3.0, 13.0, 14.0, 13.0),
            Block.box(1.0, 14.0, 1.0, 15.0, 16.0, 15.0));

    public AstralPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
