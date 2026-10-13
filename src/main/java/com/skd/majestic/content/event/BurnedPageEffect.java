package com.skd.majestic.content.event;

import com.skd.majestic.content.MajesticEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;

/**
 * A journal page that arrives after the chapter it would reveal has already been read does not
 * integrate: it burns, and what it leaves behind hangs in the air for a while.
 *
 * <p>There is no mechanical consequence on purpose. The residue effect is registered and inert, so
 * the beat is pure atmosphere and the door stays open for whatever it becomes later.</p>
 */
public final class BurnedPageEffect {

    /** Ticks the residue lasts. Long enough to read the message and notice it in the HUD. */
    private static final int RESIDUE_TICKS = 20 * 60;
    private static final int RESIDUE_AMPLIFIER = 0;

    private static final int FADE_IN = 5;
    private static final int FADE_OUT = 15;
    private static final int TITLE_TICKS = 70;

    private BurnedPageEffect() {}

    /**
     * Burns the page in the player's hand: the stack is consumed by the caller, this lights the fire,
     * the title card and the residue.
     */
    public static void burn(ServerLevel level, ServerPlayer player, Vec3 handPos) {
        level.sendParticles(ParticleTypes.FLAME, handPos.x, handPos.y, handPos.z,
                40, 0.25, 0.25, 0.25, 0.02);
        level.sendParticles(ParticleTypes.SMOKE, handPos.x, handPos.y, handPos.z,
                30, 0.3, 0.3, 0.3, 0.01);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, handPos.x, handPos.y, handPos.z,
                24, 0.35, 0.35, 0.35, 0.01);
        level.sendParticles(ParticleTypes.WHITE_ASH, handPos.x, handPos.y, handPos.z,
                20, 0.4, 0.4, 0.4, 0.0);

        level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.6f, 1.4f);
        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.PLAYERS, 0.5f, 0.7f);

        player.connection.send(new ClientboundSetTitlesAnimationPacket(FADE_IN, TITLE_TICKS, FADE_OUT));
        player.connection.send(new ClientboundSetTitleTextPacket(
                Component.translatable("majestic.page.burned.title")));
        player.connection.send(new ClientboundSetSubtitleTextPacket(
                Component.translatable("majestic.page.burned.subtitle")));

        // Holder, not the raw effect: the effect's particle colour and icon live on the registry entry.
        player.addEffect(new MobEffectInstance(
                BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(MajesticEffects.BURNED_PAGE_RESIDUE.getKey()),
                RESIDUE_TICKS, RESIDUE_AMPLIFIER, false, true));
    }
}
