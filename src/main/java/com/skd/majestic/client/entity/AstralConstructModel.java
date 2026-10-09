package com.skd.majestic.client.entity;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.AstralConstruct;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

/**
 * GeckoLib model for the Astral Construct. Assets live directly under {@code geo/} and
 * {@code animations/} (this mod's convention), so the model and animation paths are overridden;
 * the texture keeps GeckoLib's defaulted {@code textures/entity/} location.
 */
public class AstralConstructModel extends DefaultedEntityGeoModel<AstralConstruct> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "geo/astral_construct.geo.json");

    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "animations/astral_construct.animation.json");

    public AstralConstructModel() {
        super(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "astral_construct"), true);
    }

    @Override
    public ResourceLocation getModelResource(AstralConstruct animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(AstralConstruct animatable) {
        return ANIMATION;
    }
}