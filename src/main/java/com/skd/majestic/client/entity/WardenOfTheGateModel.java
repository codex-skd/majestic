package com.skd.majestic.client.entity;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.boss.WardenOfTheGate;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

/**
 * GeckoLib model for the Warden of the Gate. Assets live directly under {@code geo/} and
 * {@code animations/} (this mod's convention), so the model and animation paths are overridden;
 * the texture keeps GeckoLib's defaulted {@code textures/entity/} location.
 */
public class WardenOfTheGateModel extends DefaultedEntityGeoModel<WardenOfTheGate> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "geo/warden_of_the_gate.geo.json");

    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "animations/warden_of_the_gate.animation.json");

    public WardenOfTheGateModel() {
        super(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "warden_of_the_gate"), true);
    }

    @Override
    public ResourceLocation getModelResource(WardenOfTheGate animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(WardenOfTheGate animatable) {
        return ANIMATION;
    }
}