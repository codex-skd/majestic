package com.skd.majestic.content.entity.night;

import com.skd.majestic.content.entity.MajesticEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * The Stargazer Cultist's ranged attack: a small bolt of pale starlight.
 *
 * <p>Deals magic damage on impact, leaves an {@code END_ROD} trail, does not set fire
 * and is discarded after a short lifetime. Invisible on its own (rendered by a no-op
 * renderer) so only the trail reads in game.
 */
public class StarlightBoltProjectile extends AbstractHurtingProjectile {

    private static final float DAMAGE = 4.0f;
    private static final int LIFETIME_TICKS = 60;

    public StarlightBoltProjectile(EntityType<? extends StarlightBoltProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public StarlightBoltProjectile(Level level, LivingEntity owner, Vec3 movement) {
        super(MajesticEntities.STARLIGHT_BOLT.get(), owner, movement, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount >= LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity owner = this.getOwner();
        result.getEntity().hurt(this.damageSources().indirectMagic(this, owner), DAMAGE);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.END_ROD;
    }
}
