package com.skd.majestic.content.event;

import com.skd.almanaccore.guide.EntryGate;
import com.skd.almanaccore.guide.VellumliBridge;
import com.skd.astralcore.event.NodeUnlockedEvent;
import com.skd.expeditioncore.loot.AdvancementHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class MajesticEvents {

    private static final ResourceLocation FIRST_LIGHT_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "first_light");

    private static final ResourceLocation ALMANAC_BOOK_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "almanac");

    private static final ResourceLocation IN_SKY_ISLAND_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/in_sky_island");

    private static final ResourceLocation CHAPTER_3_PHASE1_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/chapter_3_phase1");

    private static final ResourceLocation CHAPTER_3_PHASE2_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/chapter_3_phase2");

    private static final ResourceLocation GOT_CONSTRUCTION_ALTAR_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/got_construction_altar");

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

    /**
     * Opens the phase-two chapter when the player both knows the first phase is done and has stood on
     * the sky island.
     *
     * <p>Those are two separate moments — reading the page the Receptacle left, and arriving at the
     * island — and either can come first, so both are watched. Entering the Vault no longer touches
     * the chapters at all: it only earns the Vault achievement, and the Portal's own page does the
     * rest. Granting is idempotent and guarded, so re-firing never duplicates the message.</p>
     */
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ResourceLocation earned = event.getAdvancement().id();

        if (earned.equals(IN_SKY_ISLAND_ID) && AdvancementHooks.has(player, CHAPTER_3_PHASE1_ID)) {
            unlockPhaseTwo(player);
        } else if (earned.equals(CHAPTER_3_PHASE1_ID) && AdvancementHooks.has(player, IN_SKY_ISLAND_ID)) {
            unlockPhaseTwo(player);
        } else if (earned.equals(GOT_CONSTRUCTION_ALTAR_ID)) {
            // Picking the altar up is not self-explanatory: the playtest stopped right here, with no
            // idea where the thing goes. The page that says it is gated behind this same advancement,
            // so the chat line is what points at the book before the player thinks to open it.
            player.displayClientMessage(Component.translatable("message.majestic.page.new_note"), false);
        }
    }

    /** Grants the phase-two chapter with the same "new chapter" message the pages use. */
    private static void unlockPhaseTwo(ServerPlayer player) {
        if (AdvancementHooks.has(player, CHAPTER_3_PHASE2_ID)) return;
        AdvancementHooks.grantAll(player, CHAPTER_3_PHASE2_ID);
        player.displayClientMessage(
                Component.translatable("message.majestic.page.new_chapter",
                        Component.translatable("book.majestic.almanac.chapter_3_phase2")),
                false);
    }

    private MajesticEvents() {}
}
