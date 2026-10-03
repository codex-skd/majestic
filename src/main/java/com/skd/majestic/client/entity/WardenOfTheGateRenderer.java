package com.skd.majestic.client.entity;

import com.skd.majestic.content.entity.boss.WardenOfTheGate;
import com.skd.expeditioncore.boss.render.GeoBossRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WardenOfTheGateRenderer extends GeoBossRenderer<WardenOfTheGate> {

    public WardenOfTheGateRenderer(EntityRendererProvider.Context context) {
        super(context, new WardenOfTheGateModel());
    }
}