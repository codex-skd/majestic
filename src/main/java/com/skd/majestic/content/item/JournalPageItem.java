package com.skd.majestic.content.item;

import com.skd.almanaccore.guide.VellumliBridge;
import com.skd.expeditioncore.loot.AdvancementHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    private final ResourceLocation chapter;
    private final @Nullable ResourceLocation requiredChapter;
    private final String descriptionKey;
    private final Component chapterName;

    public JournalPageItem(Properties properties, ResourceLocation chapter, @Nullable ResourceLocation requiredChapter,
                           String descriptionKey, String chapterNameKey) {
        super(properties);
        this.chapter = chapter;
        this.requiredChapter = requiredChapter;
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

        if (this.requiredChapter != null && !AdvancementHooks.has(serverPlayer, this.requiredChapter)) {
            serverPlayer.displayClientMessage(Component.translatable("message.majestic.page.needs_previous"), true);
            return InteractionResultHolder.fail(stack);
        }

        if (AdvancementHooks.has(serverPlayer, this.chapter)) {
            serverPlayer.displayClientMessage(Component.translatable("message.majestic.page.already_read"), true);
            return InteractionResultHolder.fail(stack);
        }

        AdvancementHooks.grantAll(serverPlayer, this.chapter);
        if (!serverPlayer.isCreative()) {
            stack.shrink(1);
        }

        serverPlayer.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.5f, 1.0f);
        serverPlayer.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.0f);
        serverPlayer.displayClientMessage(
                Component.translatable("message.majestic.page.new_chapter", this.chapterName), false);

        return InteractionResultHolder.success(stack);
    }

    private static boolean hasAlmanac(ServerPlayer player) {
        ItemStack reference = VellumliBridge.giveBookStack(ALMANAC_ID);
        return !reference.isEmpty() && player.getInventory().contains(reference);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.descriptionKey).withStyle(ChatFormatting.GRAY));
    }
}
