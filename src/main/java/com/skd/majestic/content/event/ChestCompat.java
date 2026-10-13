package com.skd.majestic.content.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.fml.ModList;

/**
 * Detects the loot chests our structure-based story reacts to, tolerating Lootr.
 *
 * <p>Majestic reacts to chests in two places (unsealing a Fallen Shrine, locking the Vault's
 * chests). Both used to test {@code getBlockEntity(pos) instanceof ChestBlockEntity}, which Lootr
 * breaks: Lootr replaces the chest's block-entity (and/or its block id) with its own, so a converted
 * vault or shrine chest is no longer a {@link ChestBlockEntity} and the reaction silently did
 * nothing (the shrine never unsealed; the Vault's chests were never locked).</p>
 *
 * <p>Lootr support is OPTIONAL and dependency-free. With no Lootr installed this reduces to the
 * vanilla {@link ChestBlockEntity} check, so behaviour is identical to before. When Lootr is loaded,
 * its containers are matched by the registry namespace of the block or its block-entity type —
 * this covers both the variant that gives chests their own {@code lootr:} block id and the one that
 * keeps {@code minecraft:chest} but swaps the block entity — with no compile-time dependency.</p>
 */
public final class ChestCompat {

    private static final String LOOTR_MOD_ID = "lootr";

    private ChestCompat() {}

    /** @return {@code true} for a vanilla chest, or (only when Lootr is loaded) a Lootr chest. */
    public static boolean isChestContainer(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ChestBlockEntity) {
            return true;
        }
        if (be == null || !ModList.get().isLoaded(LOOTR_MOD_ID)) {
            return false;
        }
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock());
        ResourceLocation entityTypeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType());
        return LOOTR_MOD_ID.equals(blockId.getNamespace()) || LOOTR_MOD_ID.equals(entityTypeId.getNamespace());
    }
}
