package com.skd.majestic.content.block;

import com.skd.astralcore.ritual.Multiblock;
import com.skd.majestic.Majestic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

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
                    .noOcclusion()
                    .lightLevel(state -> 7)
    );

    public static final DeferredBlock<Block> ASTRAL_PILLAR = BLOCKS.registerBlock(
            "astral_pillar",
            AstralPillarBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
    );

    // --- The Order's stone: the three blocks that rebuild the Arcane Portal ---
    // All three are plain full blocks so the player can place them freely. They deliberately have
    // NO recipe: they are found in the Watchers' vault and are the only way to finish the Portal.

    /**
     * The Vault's socket. Emits no light of its own: the Illumination Stone set into it does, which
     * is why it has no light level here.
     */
    public static final DeferredBlock<Block> VAULT_ALTAR = BLOCKS.registerBlock(
            "vault_altar",
            VaultAltarBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(4.5f, 7.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
    );

    public static final DeferredBlock<Block> ARCANE_BRICK = BLOCKS.registerSimpleBlock("arcane_brick",
            BlockBehaviour.Properties.of().strength(3.0f, 6.0f).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> RUNE_BLOCK = BLOCKS.registerSimpleBlock("rune_block",
            BlockBehaviour.Properties.of().strength(3.0f, 6.0f).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> CARVED_KEYSTONE = BLOCKS.registerSimpleBlock("carved_keystone",
            BlockBehaviour.Properties.of().strength(4.5f, 7.0f).requiresCorrectToolForDrops());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VaultAltarBlockEntity>> VAULT_ALTAR_BE =
            BLOCK_ENTITIES.register("vault_altar",
                    () -> BlockEntityType.Builder.of(VaultAltarBlockEntity::new, VAULT_ALTAR.get()).build(null));

    /** Every block the Portal's two missing voussoirs can be rebuilt from. */
    public static final TagKey<Block> PORTAL_STONE = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "portal_stone"));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AstralAltarBlockEntity>> ASTRAL_ALTAR_BE =
            BLOCK_ENTITIES.register("astral_altar",
                    () -> BlockEntityType.Builder.of(AstralAltarBlockEntity::new, ASTRAL_ALTAR.get()).build(null));

    /** Pillars accepted by the T1 altar multiblock. Single source of truth for every consumer. */
    public static final TagKey<Block> ALTAR_PILLAR = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "altar_pillar"));

    /**
     * T1 altar shape: the altar block at the centre plus one astral pillar at each cardinal offset.
     * Shared by {@link AstralAltarBlockEntity} (structure validation) and
     * {@code EngraveSpellRitual} (ritual requirement) so the two can never drift apart.
     */
    public static final Multiblock ALTAR_MULTIBLOCK = new Multiblock(Map.of(
            new BlockPos(2, 0, 0), ALTAR_PILLAR,
            new BlockPos(-2, 0, 0), ALTAR_PILLAR,
            new BlockPos(0, 0, 2), ALTAR_PILLAR,
            new BlockPos(0, 0, -2), ALTAR_PILLAR
    ));

    private MajesticBlocks() {}

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
    }
}
