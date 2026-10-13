package com.skd.majestic.client.entity;

import com.skd.majestic.content.entity.AstralConstruct;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AstralConstructRenderer extends MajesticGeoRenderer<AstralConstruct> {

    public AstralConstructRenderer(EntityRendererProvider.Context context) {
        super(context, new AstralConstructModel());
    }
}
