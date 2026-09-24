package com.skd.majestic.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skd.majestic.content.entity.AstralConstruct;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AstralConstructRenderer extends GeoEntityRenderer<AstralConstruct> {

    public AstralConstructRenderer(EntityRendererProvider.Context context) {
        super(context, new AstralConstructModel());
    }

    @Override
    public void render(AstralConstruct entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        ResourceLocation model = this.getGeoModel().getModelResource(entity, this);
        if (!GeckoLibCache.getBakedModels().containsKey(model)) {
            // The construct's geo/animation assets are still pending from the art pipeline;
            // skip rendering instead of letting GeckoLib throw on the missing model.
            return;
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}