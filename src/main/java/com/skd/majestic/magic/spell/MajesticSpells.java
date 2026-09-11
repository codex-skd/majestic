package com.skd.majestic.magic.spell;

import com.skd.astralcore.cast.SpellType;
import com.skd.astralcore.registry.AstralRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MajesticSpells {
    private static final DeferredRegister<SpellType<?>> SPELL_TYPES =
            DeferredRegister.create(AstralRegistries.SPELL_TYPE_KEY, "majestic");

    // NOTE: these are DeferredHolder<SpellType<?>, SpellType<T>> — NOT resolved SpellType<T>
    // instances. The underlying astral_core:spell_type registry isn't bound until well after
    // this class is first touched (which happens inside Majestic's constructor, itself inside
    // FMLConstructModEvent — too early to resolve a DeferredHolder's value). Always call
    // `.get()` on these at actual usage time (e.g. when a player casts, or a command runs),
    // never eagerly during static initialisation — doing so throws
    // "Registry not present for DeferredHolder{...}: astral_core:spell_type".
    public static final DeferredHolder<SpellType<?>, SpellType<StarlightBoltSpell>> STARLIGHT_BOLT =
            SPELL_TYPES.register("starlight_bolt",
                    () -> new SpellType<>(ResourceLocation.fromNamespaceAndPath("majestic", "starlight_bolt"), StarlightBoltSpell::new));

    public static final DeferredHolder<SpellType<?>, SpellType<StarlightWardSpell>> STARLIGHT_WARD =
            SPELL_TYPES.register("starlight_ward",
                    () -> new SpellType<>(ResourceLocation.fromNamespaceAndPath("majestic", "starlight_ward"), StarlightWardSpell::new));

    public static final DeferredHolder<SpellType<?>, SpellType<StarlightRevealSpell>> STARLIGHT_REVEAL =
            SPELL_TYPES.register("starlight_reveal",
                    () -> new SpellType<>(ResourceLocation.fromNamespaceAndPath("majestic", "starlight_reveal"), StarlightRevealSpell::new));

    public static final DeferredHolder<SpellType<?>, SpellType<StarlightSurgeSpell>> STARLIGHT_SURGE =
            SPELL_TYPES.register("starlight_surge",
                    () -> new SpellType<>(ResourceLocation.fromNamespaceAndPath("majestic", "starlight_surge"), StarlightSurgeSpell::new));

    private MajesticSpells() {}

    public static void register(IEventBus modEventBus) {
        SPELL_TYPES.register(modEventBus);
    }
}
