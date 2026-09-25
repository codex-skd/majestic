package com.skd.majestic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.skd.astralcore.cast.SpellType;
import com.skd.astralcore.cast.cooldown.CooldownTracker;
import com.skd.astralcore.essence.EssenceApi;
import com.skd.majestic.Majestic;
import com.skd.majestic.magic.spell.MajesticSpells;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class MajesticCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("majestic")
                .then(Commands.literal("status")
                        .executes(MajesticCommand::status)));
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();

            double essence = EssenceApi.get(player);
            double capacity = EssenceApi.getCapacity(player);
            context.getSource().sendSuccess(() ->
                    Component.translatable("commands.majestic.status.essence", (int) Math.round(essence), (int) Math.round(capacity)),
                    false);

            reportSpellCooldown(context, player, "spell.majestic.starlight_bolt", MajesticSpells.STARLIGHT_BOLT.get());
            reportSpellCooldown(context, player, "spell.majestic.starlight_ward", MajesticSpells.STARLIGHT_WARD.get());
            reportSpellCooldown(context, player, "spell.majestic.starlight_reveal", MajesticSpells.STARLIGHT_REVEAL.get());
            reportSpellCooldown(context, player, "spell.majestic.starlight_surge", MajesticSpells.STARLIGHT_SURGE.get());

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.translatable("commands.majestic.error", String.valueOf(e.getMessage())));
            return 0;
        }
    }

    private static void reportSpellCooldown(CommandContext<CommandSourceStack> context, ServerPlayer player, String nameKey, SpellType<?> spellType) {
        int remaining = CooldownTracker.get(player, spellType);
        if (remaining > 0) {
            context.getSource().sendSuccess(() ->
                    Component.translatable("commands.majestic.status.cooldown", Component.translatable(nameKey), remaining),
                    false);
        } else {
            context.getSource().sendSuccess(() ->
                    Component.translatable("commands.majestic.status.no_cooldown", Component.translatable(nameKey)),
                    false);
        }
    }

    private MajesticCommand() {}
}
