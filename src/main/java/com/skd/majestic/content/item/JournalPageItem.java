package com.skd.majestic.content.item;

import com.skd.almanaccore.guide.VellumliBridge;
import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.content.event.BurnedPageEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * One recovered page of the Order of Watchers' journal. Reading a page while carrying the
 * Almanac unlocks the corresponding journey chapter; the page is consumed unless the
 * reader is in creative mode.
 */
public class JournalPageItem extends Item {

    private static final ResourceLocation ALMANAC_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "almanac");

    /**
     * Display name of each chapter, keyed by its chapter id, so a page that arrives out of order can
     * say which chapter is missing instead of only "something comes before".
     *
     * <p>Deliberately a lookup rather than a translatable argument built from the id: a raw
     * {@code journey/chapter_1} in front of the player is worse than no answer at all.</p>
     */
    private static final java.util.Map<String, String> CHAPTER_NAMES = java.util.Map.of(
            "journey/chapter_1", "book.majestic.almanac.chapter_1",
            "journey/chapter_2", "book.majestic.almanac.chapter_2",
            "journey/chapter_3", "book.majestic.almanac.chapter_3",
            "journey/chapter_2_1", "book.majestic.almanac.chapter_2_1");

    private final ResourceLocation chapter;
    private final @Nullable ResourceLocation requiredChapter;
    private final @Nullable ResourceLocation revokedChapter;
    private final List<ResourceLocation> alsoGranted;
    private final String descriptionKey;
    private final Component chapterName;

    public JournalPageItem(Properties properties, ResourceLocation chapter, @Nullable ResourceLocation requiredChapter,
                           String descriptionKey, String chapterNameKey) {
        this(properties, chapter, requiredChapter, null, descriptionKey, chapterNameKey);
    }

    /**
     * @param revokedChapter when reading this page revokes that chapter's advancement, so the guide
     *                        book swaps one entry for its twin. Vellumli cannot rename an entry at
     *                        runtime, so a title that has to change is done by ungranting the old
     *                        entry and granting the new one: the first shows "(Locked)" and the
     *                        second carries the literal we want.
     */
    public JournalPageItem(Properties properties, ResourceLocation chapter, @Nullable ResourceLocation requiredChapter,
                           @Nullable ResourceLocation revokedChapter,
                           String descriptionKey, String chapterNameKey) {
        this(properties, chapter, requiredChapter, revokedChapter, List.of(), descriptionKey, chapterNameKey);
    }

    /**
     * A page that unlocks more than one chapter. Needed when a revoked chapter has a replacement:
     * revoking alone only makes the old entry vanish, so the twin that carries the new literal has
     * to be granted too, or the book simply loses the chapter. Granted in the same pass as the main
     * chapter and before the revoke, so a failure can never leave the player with neither.
     */
    public JournalPageItem(Properties properties, ResourceLocation chapter, @Nullable ResourceLocation requiredChapter,
                           @Nullable ResourceLocation revokedChapter, List<ResourceLocation> alsoGranted,
                           String descriptionKey, String chapterNameKey) {
        super(properties);
        this.chapter = chapter;
        this.requiredChapter = requiredChapter;
        this.revokedChapter = revokedChapter;
        this.alsoGranted = List.copyOf(alsoGranted);
        this.descriptionKey = descriptionKey;
        this.chapterName = Component.translatable(chapterNameKey);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (level.isClientSide()) {
            return InteractionResultHolder.pass(stack);
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (!hasAlmanac(serverPlayer)) {
            serverPlayer.displayClientMessage(Component.translatable("message.majestic.page.no_book"), true);
            return InteractionResultHolder.fail(stack);
        }

        // A page whose chapter is already behind the player's progress has nothing left to reveal,
        // so it burns instead of being read. Checked before the granted check below so that a page
        // the player already consumed still burns rather than answering "already read".
        if (JournalProgress.isOutdated(serverPlayer, this.chapter)) {
            stack.shrink(1);
            BurnedPageEffect.burn((ServerLevel) level, serverPlayer, handPos(serverPlayer, usedHand));
            return InteractionResultHolder.success(stack);
        }

        // A page that revokes an earlier chapter must not also require that chapter: it is gone by
        // the time the revoke happens, so the check below would always fail and a second copy of
        // the page would be refused with "needs previous" even for a player who is done with it.
        if (this.requiredChapter != null && this.revokedChapter == null
                && !AdvancementHooks.has(serverPlayer, this.requiredChapter)) {
            String nameKey = CHAPTER_NAMES.get(this.requiredChapter.getPath());
            Component message = nameKey == null
                    ? Component.translatable("message.majestic.page.needs_previous")
                    : Component.translatable("message.majestic.page.needs_chapter", Component.translatable(nameKey));
            serverPlayer.displayClientMessage(message, true);
            return InteractionResultHolder.fail(stack);
        }

        if (AdvancementHooks.has(serverPlayer, this.chapter)) {
            serverPlayer.displayClientMessage(Component.translatable("message.majestic.page.already_read"), true);
            return InteractionResultHolder.fail(stack);
        }

        AdvancementHooks.grantAll(serverPlayer, this.chapter);
        for (ResourceLocation extra : this.alsoGranted) {
            AdvancementHooks.grant(serverPlayer, extra);
        }
        if (this.revokedChapter != null) {
            // Ungranting the previous chapter lets the guide book swap its entry for the twin that
            // carries the new literal. Ordering matters: grant first, so a mid-way failure never
            // leaves the player with neither chapter.
            AdvancementHooks.revoke(serverPlayer, this.revokedChapter);
        }
        if (!serverPlayer.isCreative()) {
            stack.shrink(1);
        }
        serverPlayer.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.5f, 1.0f);
        serverPlayer.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.0f);
        serverPlayer.displayClientMessage(
                Component.translatable("message.majestic.page.new_chapter", this.chapterName), false);

        return InteractionResultHolder.success(stack);
    }

    /** Shared with the Lantern-Bearer trigger: the page is only meaningful while the book is carried. */
    public static boolean hasAlmanac(ServerPlayer player) {
        ItemStack reference = VellumliBridge.giveBookStack(ALMANAC_ID);
        return !reference.isEmpty() && player.getInventory().contains(reference);
    }

    /**
     * Where the burning page is held, so the fire lights at the hand and not at the feet. A page is
     * small; an offset off the player's own position reads as "in their fist" without needing the
     * item to be an entity in the world.
     */
    private static Vec3 handPos(ServerPlayer player, InteractionHand hand) {
        Vec3 base = player.position();
        return switch (hand) {
            case MAIN_HAND -> new Vec3(base.x, base.y + 1.0, base.z);
            case OFF_HAND -> new Vec3(base.x, base.y + 0.9, base.z);
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.descriptionKey).withStyle(ChatFormatting.GRAY));
    }
}
