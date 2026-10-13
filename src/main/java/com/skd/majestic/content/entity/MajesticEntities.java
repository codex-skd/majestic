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

    /**
     * The Act I guide ghost. {@code noSave} was removed when it started waiting at a column: the
     * waiting phase is meant to survive a save and reload (instance, phase and who it is waiting
     * for are all in its NBT), and a ghost that is never written to disk cannot carry any of that.
     * Being MISC keeps it out of every mob-spawn cap, and MISC entities are never removed by the
     * server's entity limit.
     */
    public static final DeferredHolder<EntityType<?>, EntityType<LanternBearer>> LANTERN_BEARER =
            ENTITIES.register("lantern_bearer", () -> EntityType.Builder
                    .of(LanternBearer::new, MobCategory.MISC)
                    .sized(0.6f, 1.9f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build("majestic:lantern_bearer"));

    /**
     * A block of the Portal mid-flight, from the Receptacle to its gap. Not a mob and never saved
     * ({@code shouldBeSaved()} is false): it exists for the three seconds of one journey, and the
     * block entity that sent it puts the block in by hand if the stone never comes back.
     */
    public static final DeferredHolder<EntityType<?>, EntityType<FlyingStoneEntity>> FLYING_STONE =
            ENTITIES.register("flying_stone", () -> EntityType.Builder
                    .of(FlyingStoneEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build("majestic:flying_stone"));

    private MajesticEntities() {}

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        modEventBus.addListener(MajesticEntities::onEntityAttributeCreation);
    }

    private static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(WARDEN_OF_THE_GATE.get(), WardenOfTheGate.createAttributes().build());
        event.put(ASTRAL_CONSTRUCT.get(), AstralConstruct.createAttributes().build());
        event.put(LANTERN_BEARER.get(), LanternBearer.createAttributes().build());
    }
}
