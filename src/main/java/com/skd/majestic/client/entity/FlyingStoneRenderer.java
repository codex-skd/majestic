package com.skd.majestic.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skd.majestic.content.entity.FlyingStoneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws a mid-flight block of the Portal as itself: the baked model of the
 * {@link BlockState the stone carries}, slowly spinning, with no item frame or item display in
 * between so it reads as a block that has come loose rather than as something being carried.
 *
 * <p>The pose starts at the entity's position, which the flight path puts at the centre of the
 * block, so the rotation is applied first (around that centre) and the half-block offset last.</p>
 */
public class FlyingStoneRenderer extends EntityRenderer<FlyingStoneEntity> {

    private static final float SPIN_DEGREES_PER_TICK = 2.0F;

    public FlyingStoneRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FlyingStoneEntity stone, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        BlockState carried = stone.getCarried();
        if (carried.isAir()) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((stone.tickCount + partialTick) * SPIN_DEGREES_PER_TICK));
        poseStack.translate(-0.5, -0.5, -0.5);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(carried, poseStack, buffer, packedLight,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FlyingStoneEntity stone) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
