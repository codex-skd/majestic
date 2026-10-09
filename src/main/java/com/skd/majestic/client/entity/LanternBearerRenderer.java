package com.skd.majestic.client.entity;

import com.skd.majestic.content.entity.LanternBearer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LanternBearerRenderer extends MajesticGeoRenderer<LanternBearer> {

    /** A person-sized figure hovering a little above the floor, so a narrow shadow. */
    private static final float SHADOW_RADIUS = 0.6f;

    public LanternBearerRenderer(EntityRendererProvider.Context context) {
        super(context, new LanternBearerModel(), SHADOW_RADIUS);
    }
}
