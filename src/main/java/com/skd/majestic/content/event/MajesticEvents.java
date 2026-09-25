package com.skd.majestic.content.event;

import com.skd.almanaccore.guide.EntryGate;
import com.skd.almanaccore.guide.VellumliBridge;
import com.skd.astralcore.event.NodeUnlockedEvent;
import com.skd.expeditioncore.loot.AdvancementHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class MajesticEvents {

    private static final ResourceLocation FIRST_LIGHT_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "first_light");

    private static final ResourceLocation ALMANAC_BOOK_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "almanac");

    // Stored under Player.PERSISTED_NBT_TAG so it survives death: the guide book is given once per player.
    private static final String RECEIVED_ALMANAC_KEY = "majestic.received_almanac";

    public static void onNodeUnlocked(NodeUnlockedEvent event) {
        if (!event.getNodeId().equals(FIRST_LIGHT_ID)) return;

        AdvancementHooks.grantAll(event.getPlayer(), EntryGate.advancementIdFor(FIRST_LIGHT_ID));
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (persisted.getBoolean(RECEIVED_ALMANAC_KEY)) return;

        ItemStack book = VellumliBridge.giveBookStack(ALMANAC_BOOK_ID);
        if (!book.isEmpty() && !player.getInventory().add(book)) {
            player.drop(book, false);
        }

        persisted.putBoolean(RECEIVED_ALMANAC_KEY, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }

    private MajesticEvents() {}
}
