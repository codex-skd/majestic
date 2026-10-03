package com.skd.majestic.content.item;

import com.skd.astralcore.cast.CastContext;
import com.skd.astralcore.cast.CastResult;
import com.skd.astralcore.cast.Spell;
import com.skd.astralcore.cast.SpellType;
import com.skd.astralcore.cast.cooldown.CooldownTracker;
import com.skd.astralcore.registry.AstralRegistries;
import com.skd.astralcore.research.ResearchApi;
import com.skd.majestic.content.component.MajesticDataComponents;
import com.skd.majestic.magic.CastMessages;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FocusItem extends Item {

    private static final ResourceLocation DEFAULT_SPELL_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "starlight_bolt");

    private static final ResourceLocation FIRST_LIGHT_ID =
            ResourceLocation.fromNamespaceAndPath("majestic", "first_light");

    public FocusItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack stack = player.getItemInHand(usedHand);

        // Resolve spell from recorded component
        ResourceLocation spellId = stack.getOrDefault(MajesticDataComponents.RECORDED_SPELL.get(), DEFAULT_SPELL_ID);
        SpellType<?> spellType = AstralRegistries.SPELL_TYPES.get(spellId);

        if (spellType == null) {
            serverPlayer.sendSystemMessage(Component.translatable("message.majestic.focus.no_spell"));
            return InteractionResultHolder.fail(stack);
        }

        if (CooldownTracker.isOnCooldown(serverPlayer, spellType)) {
            serverPlayer.sendSystemMessage(Component.translatable("message.majestic.focus.cooldown"));
            return InteractionResultHolder.fail(stack);
        }

        Spell spell = spellType.create();
        CastResult result = spell.execute(new CastContext(player, level, usedHand, null));

        if (result instanceof CastResult.Failure failure) {
            serverPlayer.sendSystemMessage(CastMessages.reason(failure.reason()));
        } else if (result instanceof CastResult.InsufficientEssence) {
            serverPlayer.sendSystemMessage(Component.translatable("message.majestic.focus.no_essence"));
        } else if (result instanceof CastResult.OnCooldown cooldown) {
            serverPlayer.sendSystemMessage(Component.translatable("message.majestic.focus.cooldown_ticks", cooldown.remainingTicks()));
        }

        if (result.isSuccess()) {
            CooldownTracker.set(serverPlayer, spellType, spell.getCooldownTicks());
            // First Light is earned by actually casting, not by swinging at air.
            ResearchApi.unlock(serverPlayer, FIRST_LIGHT_ID);
        }

        return result.isSuccess()
                ? InteractionResultHolder.success(stack)
                : InteractionResultHolder.fail(stack);
    }
}
