package com.skd.majestic.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the Illumination Stone and, once set, lights the Watchers' Vault a column at a time.
 *
 * <p>The lighting is done with real blocks rather than a fake light value: vanilla light does not
 * propagate from an arbitrary source you move around, so the only way to make a column actually emit
 * light is to put something that emits it there. A soul lantern on top of each column reads correctly
 * in a hall full of deepslate, and it survives a chunk unload.</p>
 *
 * <p>Columns are found by scanning for tall vertical runs of the Order's masonry, then lit one at a
 * time from the altar outwards, so the light visibly travels instead of appearing all at once.</p>
 */
public class VaultAltarBlockEntity extends BlockEntity {

    /** What counts as a column: the Order's masonry, stacked at least this tall. */
    private static final int MIN_COLUMN_HEIGHT = 3;
    /** How far from the altar the columns are looked for. The Vault's hall is 37 across. */
    private static final int SCAN_RADIUS = 18;
    /** One column lit per this many ticks, so the light spreads instead of blinking on. */
    private static final int TICKS_PER_COLUMN = 6;

    @Nullable
    private ItemStack stone = null;
    private boolean activated;
    private final List<BlockPos> pendingColumns = new ArrayList<>();
    private int spreadTimer;

    public VaultAltarBlockEntity(BlockPos pos, BlockState state) {
        super(MajesticBlocks.VAULT_ALTAR_BE.get(), pos, state);
    }

    /** Whether the stone is in and the lighting has been triggered. Drives the chest lock. */
    public boolean isLit() {
        return this.activated && this.hasStone();
    }

    public boolean hasStone() {
        return this.stone != null && !this.stone.isEmpty();
    }

    @Nullable
    public ItemStack getStone() {
        return this.stone;
    }

    public void setStone(ItemStack stack) {
        this.stone = stack;
        setChanged();
    }

    /**
     * Lights the Vault. Scans once, then doles the columns out over time.
     */
    public void activate(Level level, BlockPos pos) {
        if (this.activated) {
            return;
        }
        this.activated = true;
        this.pendingColumns.clear();
        this.pendingColumns.addAll(findColumns(level, pos));
        setChanged();
    }

    /** One soul lantern on top of every column of the Order's masonry. */
    private static List<BlockPos> findColumns(Level level, BlockPos altar) {
        List<BlockPos> tops = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                cursor.set(altar.getX() + dx, altar.getY(), altar.getZ() + dz);
                if (!isColumnStone(level.getBlockState(cursor))) {
                    continue;
                }
                // Walk up while the stack continues; the top of the run is where the lantern goes.
                int top = cursor.getY();
                while (isColumnStone(level.getBlockState(cursor.setY(top + 1)))) {
                    top++;
                }
                if (top - altar.getY() + 1 >= MIN_COLUMN_HEIGHT) {
                    tops.add(new BlockPos(cursor.getX(), top + 1, cursor.getZ()));
                }
            }
        }
        // Nearest the altar first, so the light reads as travelling outwards from it.
        tops.sort((a, b) -> a.distSqr(altar) < b.distSqr(altar) ? -1 : 1);
        return tops;
    }

    private static boolean isColumnStone(BlockState state) {
        return state.is(com.skd.majestic.content.block.MajesticBlocks.ARCANE_BRICK.get())
                || state.is(com.skd.majestic.content.block.MajesticBlocks.RUNE_BLOCK.get());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VaultAltarBlockEntity altar) {
        if (!altar.activated || altar.pendingColumns.isEmpty()) {
            return;
        }
        if (++altar.spreadTimer < TICKS_PER_COLUMN) {
            return;
        }
        altar.spreadTimer = 0;

        BlockPos next = altar.pendingColumns.remove(0);
        if (!level.getBlockState(next).isAir()) {
            return;
        }
        level.setBlockAndUpdate(next, net.minecraft.world.level.block.Blocks.SOUL_LANTERN.defaultBlockState());

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, next.getX() + 0.5, next.getY() + 0.5,
                    next.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.01);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.stone != null) {
            tag.put("Stone", this.stone.save(registries));
        }
        tag.putBoolean("Activated", this.activated);
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.stone = tag.contains("Stone")
                ? ItemStack.parse(registries, tag.getCompound("Stone")).orElse(null)
                : null;
        this.activated = tag.getBoolean("Activated");
    }
}