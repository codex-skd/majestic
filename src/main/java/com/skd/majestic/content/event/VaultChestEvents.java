package com.skd.majestic.content.event;

import com.skd.majestic.content.block.VaultAltarBlockEntity;
import com.skd.majestic.content.worldgen.StructureKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

/**
 * Keeps the Vault's own chests shut until its altar is lit.
 *
 * <p>The stone that lights the hall comes from the Observatory, not from here, so the lock cannot
 * deadlock: the player can always dig into the Vault (its mouth is blocked with rubble, not bedrock),
 * set the stone, and open the chests. What it does stop is walking into the pitch-dark hall and
 * stripping it before ever lighting it.</p>
 *
 * <p>Only chests belonging to a generated Vault instance are affected. Any chest anywhere else in the
 * world is untouched, including the one holding the stone.</p>
 */
public final class VaultChestEvents {

    /** How far from a chest to look for a lit altar. The hall is 37 across; this covers it generously. */
    private static final int ALTAR_SEARCH_RADIUS = 24;

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = event.getPos();
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity)) {
            return;
        }

        // Outside a Vault: not our business.
        if (StructureKeys.findStartAt(serverLevel, pos, StructureKeys.OBSERVERS_VAULT).isEmpty()) {
            return;
        }
        if (isAltarLit(serverLevel, pos)) {
            return;
        }

        // The interaction itself is never cancelled, only refused — same policy as the shrines.
        event.setCanceled(true);
        Player player = event.getEntity();
        player.displayClientMessage(Component.translatable("message.majestic.vault.sealed"), true);
        serverLevel.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.4f, 1.4f);
    }

    /** Whether any lit altar sits within the Vault's hall of this chest. */
    private static boolean isAltarLit(ServerLevel level, BlockPos chest) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -ALTAR_SEARCH_RADIUS; dx <= ALTAR_SEARCH_RADIUS; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -ALTAR_SEARCH_RADIUS; dz <= ALTAR_SEARCH_RADIUS; dz++) {
                    cursor.set(chest.getX() + dx, chest.getY() + dy, chest.getZ() + dz);
                    BlockEntity be = level.getBlockEntity(cursor);
                    if (be instanceof VaultAltarBlockEntity altar && altar.isLit()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private VaultChestEvents() {}
}