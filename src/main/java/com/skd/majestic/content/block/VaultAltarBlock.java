package com.skd.majestic.content.block;

import com.skd.majestic.content.item.MajesticItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * The Vault's receptacle: an empty socket that the Illumination Stone is set into.
 *
 * <p>Deliberately its own block rather than a reuse of the Astral Altar. The two share no behaviour —
 * the Astral Altar runs a multiblock ritual and is meant to be carried around — and piling unrelated
 * uses onto one block is how it ends up with rules nobody documented.</p>
 *
 * <p>Once the stone is in, it cannot be taken back out. That is the point of the act's second half:
 * otherwise a player could light the Vault, take the reward and un-light it.</p>
 */
public class VaultAltarBlock extends BaseEntityBlock {

    private static final MapCodec<VaultAltarBlock> CODEC = simpleCodec(VaultAltarBlock::new);

    public VaultAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaultAltarBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(MajesticItems.ILLUMINATION_STONE.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(level.getBlockEntity(pos) instanceof VaultAltarBlockEntity altar) || altar.hasStone()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        altar.setStone(new ItemStack(MajesticItems.ILLUMINATION_STONE.get()));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        altar.activate(level, pos);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                                BlockHitResult hit) {
        // Left alone, it does nothing at all. The only thing this block does is hold the stone.
        return InteractionResult.PASS;
    }

    /**
     * Only ticks once the stone is in, so an empty socket sitting in a chunk costs nothing.
     */
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                             BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, MajesticBlocks.VAULT_ALTAR_BE.get(),
                (lvl, pos, st, altar) -> VaultAltarBlockEntity.serverTick(lvl, pos, st, altar));
    }
}