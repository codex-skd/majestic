package com.skd.majestic.content;

import com.skd.majestic.Majestic;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Majestic's status effects.
 *
 * <p>Currently one, and it does nothing on purpose: the residue a journal page leaves behind when
 * it burns because its chapter is already past. It exists so the flavour has something to hang on —
 * visible as a soul flame curling around the player, listed in the HUD, mechanically inert.</p>
 */
public final class MajesticEffects {

    private static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Majestic.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> BURNED_PAGE_RESIDUE = EFFECTS.register(
            "burned_page_residue",
            () -> new InertResidueEffect());

    private MajesticEffects() {}

    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }

    /**
     * A deliberately effect-less status. Every gameplay hook is left at its default, so the only
     * thing it does is exist and expire; the feel of it comes from the flame particle and the tone
     * chosen in {@code MajesticEvents}.
     */
    private static class InertResidueEffect extends MobEffect {

        InertResidueEffect() {
            // NEUTRAL, white: it is neither a boon nor a harm, and the icon is meant to read as
            // scorched paper rather than as a potion.
            super(MobEffectCategory.NEUTRAL, 0x9A7B4F);
        }
    }
}
