package com.skd.majestic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.skd.astralcore.cast.SpellType;
import com.skd.astralcore.cast.cooldown.CooldownTracker;
import com.skd.astralcore.essence.EssenceApi;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.event.LanternBearerTrigger;
import com.skd.majestic.magic.spell.MajesticSpells;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class MajesticCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("majestic")
                .then(Commands.literal("status")
                        .executes(MajesticCommand::status))
                .then(Commands.literal("lantern")
                        .executes(MajesticCommand::lantern))
                .then(Commands.literal("bible")
                        .executes(MajesticCommand::bible)));
    }

    /**
     * Developer command: hands the player a Bestiary Bible directly.
     *
     * <p>The Bestiary Bible is granted once per player on first login by majestic_terrain, so a test
     * world created before that grant existed never received it and cannot fill the Construction
     * Altar's second book slot. This hands the item over and bypasses the once-per-login flag so the
     * altar can be tested.</p>
     */
    private static int bible(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            if (!ModList.get().isLoaded("majestic_terrain")) {
                context.getSource().sendFailure(
                        Component.literal("majestic_terrain is not loaded; no Bestiary Bible to give."));
                return 0;
            }

            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath("majestic_terrain", "bestiary_bible")));
            if (stack.isEmpty()) {
                context.getSource().sendFailure(Component.literal("Bestiary Bible item not found."));
                return 0;
            }

            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
            context.getSource().sendSuccess(() -> Component.literal("Gave the Bestiary Bible."), false);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Bestiary Bible command failed: " + e.getMessage()));
            return 0;
        }
    }

    /**
     * Developer command: forces the Lantern-Bearer to materialise at the nearest Arcane Portal,
     * bypassing the Chapter III / Almanac / carried-page gates so the encounter can be tested from
     * creative with zero advancements.
     */
    private static int lantern(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            LanternBearerTrigger.ForceResult result = LanternBearerTrigger.forceSpawn(player);
            if (result.success()) {
                context.getSource().sendSuccess(() -> Component.literal(result.reason()), false);
                return 1;
            }
            context.getSource().sendFailure(Component.literal(result.reason()));
            return 0;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Lantern-Bearer command failed: " + e.getMessage()));
            return 0;
        }
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
