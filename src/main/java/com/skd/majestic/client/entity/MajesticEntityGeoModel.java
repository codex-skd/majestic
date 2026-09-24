package com.skd.majestic.client.entity;

import com.skd.majestic.Majestic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

/**
 * Shared GeckoLib model for Majestic's entities. Assets live directly under {@code geo/} and
 * {@code animations/} (this mod's convention), so the model and animation paths are overridden;
 * the texture keeps GeckoLib's defaulted {@code textures/entity/<id>.png} location.
 */
public abstract class MajesticEntityGeoModel<T extends Entity & GeoAnimatable> extends DefaultedEntityGeoModel<T> {

    private final ResourceLocation modelResource;
    private final ResourceLocation animationResource;

    protected MajesticEntityGeoModel(String id) {
        super(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, id), true);
        this.modelResource = ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "geo/" + id + ".geo.json");
        this.animationResource = ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "animations/" + id + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return this.modelResource;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return this.animationResource;
    }
}
