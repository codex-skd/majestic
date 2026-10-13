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
 * Shared GeckoLib renderer for this mod's own entities, mirroring what {@code majestic_bestiary} does
 * for its creatures.
 *
 * <p>Skips rendering entirely while the entity's model or animation file is not baked, so missing or
 * placeholder art degrades to an invisible entity instead of crashing GeckoLib's asset lookup. That
 * matters here: the models are built elsewhere and land in the game as a separate step, so an
 * in-between state where the code is present and the art is not is normal rather than exceptional.</p>
 *
 * <p>{@code shadowRadius} is optional. Left unset it keeps vanilla's derived radius, so adopting this
 * class never changes how an entity already looks.</p>
 */
public class MajesticGeoRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {

    private final Float shadowRadius;

    public MajesticGeoRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        this(context, model, null);
    }

    public MajesticGeoRenderer(EntityRendererProvider.Context context, GeoModel<T> model, Float shadowRadius) {
        super(context, model);
        this.shadowRadius = shadowRadius;
    }

    @Override
    protected float getShadowRadius(T entity) {
        return this.shadowRadius != null ? this.shadowRadius : super.getShadowRadius(entity);
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