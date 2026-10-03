package com.skd.majestic.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * The altar blows itself apart when the Warden it summoned dies.
 *
 * <p>The blast is hand-rolled rather than a vanilla {@code level.explode()} for three reasons:
 * the damage sphere must be an exact radius, the altar itself must vanish <em>without</em>
 * dropping, and everything else inside the sphere must drop normally.</p>
 */
public final class AltarDetonation {

    /** Radius of the destruction sphere, in blocks. */
    public static final double BLAST_RADIUS = 4.0;
    private static final double BLAST_RADIUS_SQUARED = BLAST_RADIUS * BLAST_RADIUS;

    /** Ticks from the Warden's death to the blast. Long enough to cross the arena on foot. */
    public static final int FUSE_TICKS = 100;

    /** How close a player must be to be warned. Covers the arena and a margin around it. */
    private static final double WARNING_RADIUS = 32.0;

    private static final int FADE_IN = 5;
    private static final int FADE_OUT = 10;

    /** Blast damage at the epicentre, before armour. */
    private static final float MAX_DAMAGE = 9.0f;
    private static final float MAX_KNOCKBACK = 1.4f;

    private AltarDetonation() {}

    /**
     * Finds the altar at the centre of a structure piece's bounding box and starts its fuse.
     *
     * @return the altar position, or {@code null} when the piece holds no altar
     */
    public static BlockPos findAltar(ServerLevel level, AABB box) {
        BlockPos best = null;
        double centreX = (box.minX + box.maxX) / 2.0;
        double centreZ = (box.minZ + box.maxZ) / 2.0;
        double bestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                new BlockPos(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ)),
                new BlockPos(Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ)))) {
            if (!level.getBlockState(pos).is(MajesticBlocks.ASTRAL_ALTAR.get())) {
                continue;
            }
            double dx = pos.getX() - centreX;
            double dz = pos.getZ() - centreZ;
            double distance = dx * dx + dz * dz;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = pos.immutable();
            }
        }
        return best;
    }

    /** Opens the title card and starts the countdown. Call once, when the Warden dies. */
    public static void arm(ServerLevel level, BlockPos altarPos) {
        double x = altarPos.getX() + 0.5;
        double y = altarPos.getY() + 1.0;
        double z = altarPos.getZ() + 0.5;

        for (ServerPlayer player : level.players()) {
            if (!isNear(player, altarPos)) {
                continue;
            }
            player.connection.send(new ClientboundSetTitlesAnimationPacket(FADE_IN, FUSE_TICKS + 20, FADE_OUT));
            player.connection.send(new ClientboundSetTitleTextPacket(
                    Component.translatable("majestic.altar.detonation.title")));
            player.connection.send(new ClientboundSetSubtitleTextPacket(
                    Component.translatable("majestic.altar.detonation.subtitle")));
        }

        level.playSound(null, altarPos, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.0f, 0.6f);
        level.sendParticles(ParticleTypes.SMOKE, x, y, z, 40, 0.8, 1.2, 0.8, 0.02);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 20, 0.6, 1.0, 0.6, 0.01);
    }

    /**
     * Warning pulse while the fuse burns. Called every tick by the altar's block entity.
     *
     * @param ticksLeft ticks until detonation
     */
    public static void tickFuse(ServerLevel level, BlockPos altarPos, int ticksLeft) {
        double x = altarPos.getX() + 0.5;
        double y = altarPos.getY() + 1.0;
        double z = altarPos.getZ() + 0.5;

        // The altar visibly comes apart: smoke and soul fire thicken as the fuse runs down.
        int smoke = 1 + (FUSE_TICKS - ticksLeft) / 12;
        level.sendParticles(ParticleTypes.SMOKE, x, y, z, smoke, 0.7, 1.1, 0.7, 0.02);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y + 0.4, z, 2, 0.5, 0.7, 0.5, 0.01);

        if (ticksLeft % 20 != 0 && !isFinalBeep(ticksLeft)) {
            return;
        }

        int seconds = (ticksLeft + 19) / 20;
        Component subtitle = Component.translatable("majestic.altar.detonation.countdown", seconds);
        for (ServerPlayer player : level.players()) {
            if (!isNear(player, altarPos)) {
                continue;
            }
            player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
            player.displayClientMessage(subtitle, true);
        }

        float pitch = 1.0f + (FUSE_TICKS - ticksLeft) / (float) FUSE_TICKS;
        level.playSound(null, altarPos, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.HOSTILE, 0.9f, pitch);
    }

    private static boolean isNear(ServerPlayer player, BlockPos altarPos) {
        return player.position().closerThan(Vec3.atCenterOf(altarPos), WARNING_RADIUS);
    }

    private static boolean isFinalBeep(int ticksLeft) {
        return ticksLeft <= 10 && ticksLeft % 5 == 0;
    }

    /** The blast: visuals, damage, knockback, and an exact-radius destruction sphere. */
    public static void detonate(ServerLevel level, BlockPos altarPos) {
        double x = altarPos.getX() + 0.5;
        double y = altarPos.getY() + 1.0;
        double z = altarPos.getZ() + 0.5;

        level.playSound(null, altarPos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4f, 0.7f);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 8, 1.2, 1.2, 1.2, 0.0);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 40, 1.4, 1.4, 1.4, 0.04);

        damageEntities(level, altarPos);
        breakSphere(level, altarPos);
    }

    private static void damageEntities(ServerLevel level, BlockPos altarPos) {
        Vec3 centre = Vec3.atCenterOf(altarPos);
        List<Entity> nearby = level.getEntities(null, new AABB(altarPos).inflate(BLAST_RADIUS + 1.0));

        for (Entity entity : nearby) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive()) {
                continue;
            }
            double distance = living.position().distanceTo(centre);
            if (distance > BLAST_RADIUS + 1.0) {
                continue;
            }
            // Linear falloff, so hugging the altar is what kills you.
            float damage = (float) (MAX_DAMAGE * (1.0 - distance / (BLAST_RADIUS + 1.0)));
            if (damage <= 0.0f) {
                continue;
            }
            living.hurt(level.damageSources().magic(), damage);

            Vec3 away = living.position().subtract(centre);
            if (away.lengthSqr() < 1.0E-4) {
                away = new Vec3(0.0, 1.0, 0.0);
            }
            Vec3 push = away.normalize().scale(MAX_KNOCKBACK);
            living.push(push.x, push.y + 0.35, push.z);
            living.hurtMarked = true;
        }
    }

    /**
     * Destroys every block within {@link #BLAST_RADIUS} of the altar. The altar drops nothing —
     * losing it is permanent, which is what makes the Warden's defeat final — while the rest of
     * the structure drops as normal rubble.
     */
    private static void breakSphere(ServerLevel level, BlockPos altarPos) {
        int r = (int) Math.ceil(BLAST_RADIUS);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dy * dy + dz * dz > BLAST_RADIUS_SQUARED) {
                        continue;
                    }
                    cursor.set(altarPos.getX() + dx, altarPos.getY() + dy, altarPos.getZ() + dz);
                    if (!level.isInWorldBounds(cursor)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(cursor);
                    if (state.isAir() || state.is(Blocks.BEDROCK)) {
                        continue;
                    }
                    spawnDebris(level, cursor, state);
                    boolean drops = !state.is(MajesticBlocks.ASTRAL_ALTAR.get());
                    level.destroyBlock(cursor, drops, null, 512);
                }
            }
        }
    }

    private static void spawnDebris(ServerLevel level, BlockPos pos, BlockState state) {
        ParticleOptions debris = new BlockParticleOption(ParticleTypes.BLOCK, state);
        level.sendParticles(debris, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                3, 0.3, 0.3, 0.3, 0.0);
    }
}
