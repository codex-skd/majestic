package com.skd.majestic.content.event;

import com.skd.expeditioncore.protection.StructureProtection;
import com.skd.majestic.content.worldgen.StructureKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Unseals a Fallen Shrine the first time a player opens one of its chests.
 *
 * <p>Structure protection keeps every generated shrine locked (no break/place/fluids/explosions)
 * until this fires. Server-side only; the interaction itself is never cancelled.
 */
public final class ShrineUnlockEvents {

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = event.getPos();
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity)) {
            return;
        }

        if (StructureProtection.unlockAt(serverLevel, pos, StructureKeys.FALLEN_SHRINE)) {
            Player player = event.getEntity();
            player.displayClientMessage(Component.translatable("majestic.shrine.unsealed"), true);
            serverLevel.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private ShrineUnlockEvents() {}
}
