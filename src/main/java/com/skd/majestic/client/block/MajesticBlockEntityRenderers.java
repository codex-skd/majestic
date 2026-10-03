package com.skd.majestic.client.block;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.block.MajesticBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Majestic.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class MajesticBlockEntityRenderers {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MajesticBlocks.VAULT_ALTAR_BE.get(), VaultAltarRenderer::new);
    }

    private MajesticBlockEntityRenderers() {}
}
