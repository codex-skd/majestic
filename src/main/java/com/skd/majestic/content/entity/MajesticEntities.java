package com.skd.majestic.content.entity;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.boss.WardenOfTheGate;
import com.skd.majestic.content.entity.night.FallenWatcher;
import com.skd.majestic.content.entity.night.MeteorCrawler;
import com.skd.majestic.content.entity.night.StargazerCultist;
import com.skd.majestic.content.entity.night.StarlightBoltProjectile;
import com.skd.majestic.content.entity.night.UmbralMoth;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
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

    // --- Journey I night mobs ---

    public static final DeferredHolder<EntityType<?>, EntityType<FallenWatcher>> FALLEN_WATCHER =
            ENTITIES.register("fallen_watcher", () -> EntityType.Builder
                    .of(FallenWatcher::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build("majestic:fallen_watcher"));

    public static final DeferredHolder<EntityType<?>, EntityType<StargazerCultist>> STARGAZER_CULTIST =
            ENTITIES.register("stargazer_cultist", () -> EntityType.Builder
                    .of(StargazerCultist::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build("majestic:stargazer_cultist"));

    public static final DeferredHolder<EntityType<?>, EntityType<MeteorCrawler>> METEOR_CRAWLER =
            ENTITIES.register("meteor_crawler", () -> EntityType.Builder
                    .of(MeteorCrawler::new, MobCategory.MONSTER)
                    .sized(1.2f, 0.7f)
                    .clientTrackingRange(8)
                    .build("majestic:meteor_crawler"));

    public static final DeferredHolder<EntityType<?>, EntityType<UmbralMoth>> UMBRAL_MOTH =
            ENTITIES.register("umbral_moth", () -> EntityType.Builder
                    .of(UmbralMoth::new, MobCategory.MONSTER)
                    .sized(0.9f, 0.5f)
                    .clientTrackingRange(8)
                    .build("majestic:umbral_moth"));

    public static final DeferredHolder<EntityType<?>, EntityType<StarlightBoltProjectile>> STARLIGHT_BOLT =
            ENTITIES.register("starlight_bolt", () -> EntityType.Builder
                    .<StarlightBoltProjectile>of(StarlightBoltProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("majestic:starlight_bolt"));

    private MajesticEntities() {}

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
        modEventBus.addListener(MajesticEntities::onEntityAttributeCreation);
        modEventBus.addListener(MajesticEntities::onRegisterSpawnPlacements);
    }

    private static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(WARDEN_OF_THE_GATE.get(), WardenOfTheGate.createAttributes().build());
        event.put(ASTRAL_CONSTRUCT.get(), AstralConstruct.createAttributes().build());
        event.put(FALLEN_WATCHER.get(), FallenWatcher.createAttributes().build());
        event.put(STARGAZER_CULTIST.get(), StargazerCultist.createAttributes().build());
        event.put(METEOR_CRAWLER.get(), MeteorCrawler.createAttributes().build());
        event.put(UMBRAL_MOTH.get(), UmbralMoth.createAttributes().build());
    }

    private static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FALLEN_WATCHER.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(STARGAZER_CULTIST.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(METEOR_CRAWLER.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(UMBRAL_MOTH.get(), SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, UmbralMoth::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
