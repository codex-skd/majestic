package com.skd.majestic.content.event;

import com.skd.almanaccore.guide.EntryGate;
import com.skd.almanaccore.guide.VellumliBridge;
import com.skd.astralcore.event.NodeUnlockedEvent;
import com.skd.expeditioncore.loot.AdvancementHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class MajesticEvents {

    private static final ResourceLocation FIRST_LIGHT_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "first_light");

    private static final ResourceLocation ALMANAC_BOOK_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "almanac");

    public static void onNodeUnlocked(NodeUnlockedEvent event) {
        if (!event.getNodeId().equals(FIRST_LIGHT_ID)) return;

        AdvancementHooks.grantAll(event.getPlayer(), EntryGate.advancementIdFor(FIRST_LIGHT_ID));

        ItemStack book = VellumliBridge.giveBookStack(ALMANAC_BOOK_ID);
        if (!book.isEmpty()) {
            event.getPlayer().getInventory().add(book);
        }
    }

    private MajesticEvents() {}
}
