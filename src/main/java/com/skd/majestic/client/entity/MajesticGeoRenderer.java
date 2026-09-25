package com.skd.majestic.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * GeckoLib entity renderer that skips rendering entirely while the entity's baked model or
 * animation file is not present. The art for Majestic's newest mobs ships later, and
 * GeckoLib's model/animation lookup crashes on a missing asset, so the renderer degrades to
 * "invisible entity" instead.
 */
public class MajesticGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {

    public MajesticGeoRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        ResourceLocation modelResource = this.getGeoModel().getModelResource(entity, this);
        if (!GeckoLibCache.getBakedModels().containsKey(modelResource)) {
            return;
        }
        ResourceLocation animationResource = this.getGeoModel().getAnimationResource(entity);
        if (!GeckoLibCache.getBakedAnimations().containsKey(animationResource)) {
            return;
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
