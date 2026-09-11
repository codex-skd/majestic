package com.skd.majestic.content.item;

import com.skd.astralcore.cast.CastContext;
import com.skd.astralcore.cast.CastResult;
import com.skd.astralcore.cast.Spell;
import com.skd.astralcore.cast.cooldown.CooldownTracker;
import com.skd.majestic.magic.spell.MajesticSpells;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FocusItem extends Item {

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

        var spellType = MajesticSpells.STARLIGHT_BOLT.get();

        if (CooldownTracker.isOnCooldown(serverPlayer, spellType)) {
            serverPlayer.sendSystemMessage(Component.literal("Spell is on cooldown!"));
            return InteractionResultHolder.fail(stack);
        }

        Spell spell = spellType.create();
        CastResult result = spell.execute(new CastContext(player, level, usedHand, null));

        if (result instanceof CastResult.Failure failure) {
            serverPlayer.sendSystemMessage(Component.literal(failure.reason()));
        }

        CooldownTracker.set(serverPlayer, spellType, spell.getCooldownTicks());

        return InteractionResultHolder.success(stack);
    }
}
