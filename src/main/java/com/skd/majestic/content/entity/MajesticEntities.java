package com.skd.majestic.content.entity;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.boss.WardenOfTheGate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MajesticEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Majestic.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<WardenOfTheGate>> WARDEN_OF_THE_GATE =
            ENTITIES.register("warden_of_the_gate", () -> EntityType.Builder
                    .of(WardenOfTheGate::new, MobCategory.MONSTER)
                    .sized(1.0f, 2.5f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build("majestic:warden_of_the_gate"));

    public static final DeferredHolder<EntityType<?>, EntityType<AstralConstruct>> ASTRAL_CONSTRUCT =
            ENTITIES.register("astral_construct", () -> EntityType.Builder
                    .of(AstralConstruct::new, MobCategory.MONSTER)
                    .sized(0.8f, 1.4f)
                    .clientTrackingRange(8)
                    .build("majestic:astral_construct"));

    private MajesticEntities() {}

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        modEventBus.addListener(MajesticEntities::onEntityAttributeCreation);
    }

    private static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(WARDEN_OF_THE_GATE.get(), WardenOfTheGate.createAttributes().build());
        event.put(ASTRAL_CONSTRUCT.get(), AstralConstruct.createAttributes().build());
    }
}
