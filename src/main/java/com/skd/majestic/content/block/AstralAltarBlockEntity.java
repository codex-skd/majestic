package com.skd.majestic.content.block;

import com.skd.astralcore.ritual.AltarBlockEntity;
import com.skd.astralcore.ritual.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class AstralAltarBlockEntity extends AltarBlockEntity {

    private static final String FUSE_TAG = "DetonationTicks";

    /** Ticks left on the self-destruction fuse, or 0 while the altar is stable. */
    private int fuseTicks;

    public AstralAltarBlockEntity(BlockPos pos, BlockState state) {
        super(MajesticBlocks.ASTRAL_ALTAR_BE.get(), pos, state);
    }

    @Override
    public Multiblock getExpectedMultiblock() {
        return MajesticBlocks.ALTAR_MULTIBLOCK;
    }

    /**
     * Lights the fuse. The altar destroys itself {@link AltarDetonation#FUSE_TICKS} ticks later —
     * and goes with it, so the Warden's arena can never be re-challenged.
     */
    public void beginDetonation() {
        if (this.fuseTicks > 0) {
            return;
        }
        this.fuseTicks = AltarDetonation.FUSE_TICKS;
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            AltarDetonation.arm(serverLevel, getBlockPos());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.fuseTicks <= 0 || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        AltarDetonation.tickFuse(serverLevel, getBlockPos(), this.fuseTicks);
        this.fuseTicks--;
        if (this.fuseTicks <= 0) {
            AltarDetonation.detonate(serverLevel, getBlockPos());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.fuseTicks > 0) {
            tag.putInt(FUSE_TAG, this.fuseTicks);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.fuseTicks = tag.getInt(FUSE_TAG);
    }
}
