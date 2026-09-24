package com.skd.majestic.client.entity;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.MajesticEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Majestic.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class MajesticEntityRenderers {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MajesticEntities.WARDEN_OF_THE_GATE.get(), WardenOfTheGateRenderer::new);
        event.registerEntityRenderer(MajesticEntities.ASTRAL_CONSTRUCT.get(), AstralConstructRenderer::new);

        event.registerEntityRenderer(MajesticEntities.FALLEN_WATCHER.get(),
                context -> new MajesticGeoRenderer<>(context, new FallenWatcherModel()));
        event.registerEntityRenderer(MajesticEntities.STARGAZER_CULTIST.get(),
                context -> new MajesticGeoRenderer<>(context, new StargazerCultistModel()));
        event.registerEntityRenderer(MajesticEntities.METEOR_CRAWLER.get(),
                context -> new MajesticGeoRenderer<>(context, new MeteorCrawlerModel()));
        event.registerEntityRenderer(MajesticEntities.UMBRAL_MOTH.get(),
                context -> new MajesticGeoRenderer<>(context, new UmbralMothModel()));

        event.registerEntityRenderer(MajesticEntities.STARLIGHT_BOLT.get(), NoopRenderer::new);
    }

    private MajesticEntityRenderers() {}
}
