package com.skd.majestic.content.block;

import com.skd.majestic.Majestic;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MajesticBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Majestic.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Majestic.MOD_ID);

    public static final DeferredBlock<Block> ASTRAL_ALTAR = BLOCKS.registerBlock(
            "astral_altar",
            AstralAltarBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)
    );

    public static final DeferredBlock<Block> ASTRAL_PILLAR = BLOCKS.registerBlock(
            "astral_pillar",
            Block::new,
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AstralAltarBlockEntity>> ASTRAL_ALTAR_BE =
            BLOCK_ENTITIES.register("astral_altar",
                    () -> BlockEntityType.Builder.of(AstralAltarBlockEntity::new, ASTRAL_ALTAR.get()).build(null));

    public static final TagKey<Block> ALTAR_PILLAR = TagKey.create(Registries.BLOCK,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("majestic", "altar_pillar"));

    private MajesticBlocks() {}

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
    }
}
