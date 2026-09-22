package com.skd.majestic.content.block;

import com.skd.astralcore.ritual.AltarBlockEntity;
import com.skd.astralcore.ritual.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class AstralAltarBlockEntity extends AltarBlockEntity {

    private static final TagKey<Block> PILLAR_TAG = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("majestic", "altar_pillar"));

    private static final Multiblock MULTIBLOCK = new Multiblock(Map.of(
            new BlockPos(2, 0, 0), PILLAR_TAG,
            new BlockPos(-2, 0, 0), PILLAR_TAG,
            new BlockPos(0, 0, 2), PILLAR_TAG,
            new BlockPos(0, 0, -2), PILLAR_TAG
    ));

    public AstralAltarBlockEntity(BlockPos pos, BlockState state) {
        super(MajesticBlocks.ASTRAL_ALTAR_BE.get(), pos, state);
    }

    @Override
    public Multiblock getExpectedMultiblock() {
        return MULTIBLOCK;
    }
}
