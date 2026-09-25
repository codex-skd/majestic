package com.skd.majestic.magic.ritual;

import com.skd.astralcore.registry.AstralRegistries;
import com.skd.astralcore.ritual.RitualType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MajesticRituals {
    private static final DeferredRegister<RitualType<?>> RITUAL_TYPES =
            DeferredRegister.create(AstralRegistries.RITUAL_TYPE_KEY, "majestic");

    public static final DeferredHolder<RitualType<?>, RitualType<EngraveSpellRitual>> ENGRAVE_SPELL =
            RITUAL_TYPES.register("engrave_spell", () -> new RitualType<>(
                    ResourceLocation.fromNamespaceAndPath("majestic", "engrave_spell"), EngraveSpellRitual::new));

    private MajesticRituals() {}

    public static void register(IEventBus modEventBus) {
        RITUAL_TYPES.register(modEventBus);
    }
}
