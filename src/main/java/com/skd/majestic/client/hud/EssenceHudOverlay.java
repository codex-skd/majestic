package com.skd.majestic.client.hud;

import com.skd.astralcore.essence.EssenceApi;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.item.MajesticItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = Majestic.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class EssenceHudOverlay {

    private static final ResourceLocation ESSENCE_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "essence_overlay");

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ESSENCE_OVERLAY, (guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui) return;

            if (!mc.player.getMainHandItem().is(MajesticItems.STARLIGHT_FOCUS.get())
                    && !mc.player.getOffhandItem().is(MajesticItems.STARLIGHT_FOCUS.get())) {
                return;
            }

            double essence = EssenceApi.get(mc.player);
            double capacity = EssenceApi.getCapacity(mc.player);
            String text = String.format("Essence: %d/%d", (int) Math.round(essence), (int) Math.round(capacity));

            guiGraphics.drawString(mc.font, text, 4, 4, 0xFFFFFF, true);
        });
    }

    private EssenceHudOverlay() {}
}
