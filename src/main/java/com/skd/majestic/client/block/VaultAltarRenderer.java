package com.skd.majestic.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skd.majestic.content.block.VaultAltarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws the Illumination Stone floating above the Vault's altar.
 *
 * <p>Copied from the floating tome in {@code workhand_tools}' ChunkAnchor: the item is held by the
 * block entity rather than being a block of its own, and drawn with a pose that lifts it clear of the
 * socket, bobs, spins and tilts. Being an item rather than a block is also why the workshop only owes a
 * texture for it.</p>
 */
public class VaultAltarRenderer implements BlockEntityRenderer<VaultAltarBlockEntity> {

    private static final float HOVER_HEIGHT = 1.4F;
    private static final float BOB = 0.06F;
    private static final float SPIN = 2.0F;
    private static final float TILT = 20.0F;
    private static final float SCALE = 0.6F;

    public VaultAltarRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(VaultAltarBlockEntity altar, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack stone = altar.getStone();
        if (stone == null || stone.isEmpty()) {
            return;
        }

        float time = (Minecraft.getInstance().level.getGameTime() + partialTick) / 20.0F;

        poseStack.pushPose();
        poseStack.translate(0.5F, HOVER_HEIGHT, 0.5F);
        poseStack.translate(0.0F, BOB * Mth.sin(time * 1.6F), 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(time * SPIN));
        poseStack.mulPose(Axis.XP.rotationDegrees(TILT));
        poseStack.scale(SCALE, SCALE, SCALE);

        // Full bright: the stone is a light source, so it must not be shaded by the vault's darkness.
        Minecraft.getInstance().getItemRenderer().renderStatic(stone, ItemDisplayContext.FIXED, 0xF000F0,
                packedOverlay, poseStack, buffer, altar.getLevel(), (int) altar.getBlockPos().getY());

        poseStack.popPose();
    }
}