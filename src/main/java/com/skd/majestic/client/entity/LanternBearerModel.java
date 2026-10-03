package com.skd.majestic.client.entity;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.LanternBearer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

/**
 * GeckoLib model for the Lantern-Bearer. Assets live directly under {@code geo/} and
 * {@code animations/} (this mod's convention), so the model and animation paths are overridden;
 * the texture keeps GeckoLib's defaulted {@code textures/entity/} location.
 */
public class LanternBearerModel extends DefaultedEntityGeoModel<LanternBearer> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "geo/lantern_bearer.geo.json");

    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "animations/lantern_bearer.animation.json");

    public LanternBearerModel() {
        super(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "lantern_bearer"), true);
    }

    @Override
    public ResourceLocation getModelResource(LanternBearer animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(LanternBearer animatable) {
        return ANIMATION;
    }
}