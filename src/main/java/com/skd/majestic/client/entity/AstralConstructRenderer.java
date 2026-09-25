package com.skd.majestic.client.entity;

import com.skd.majestic.content.entity.AstralConstruct;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AstralConstructRenderer extends GeoEntityRenderer<AstralConstruct> {

    public AstralConstructRenderer(EntityRendererProvider.Context context) {
        super(context, new AstralConstructModel());
    }
}
