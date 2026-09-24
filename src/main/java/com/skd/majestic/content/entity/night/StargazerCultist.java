package com.skd.majestic.content.entity.night;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * Night mob — a living cultist who stole the Order's star-lore. Keeps its distance and
 * throws a starlight bolt, releasing the projectile 10 ticks (0.5 s) into the cast
 * animation, matching the taller's impact time.
 */
public class StargazerCultist extends Monster implements RangedAttackMob, GeoEntity {

    private static final int CAST_RELEASE_TICKS = 10;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.stargazer_cultist.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.stargazer_cultist.walk");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("animation.stargazer_cultist.cast");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.stargazer_cultist.death");

    private static final int DEATH_LENGTH_TICKS = 24;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private int castTicks;
    private LivingEntity castTarget;

    public StargazerCultist(EntityType<? extends StargazerCultist> entityType, Level level) {
        super(entityType, level);

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 60, 15.0f));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.castTicks > 0) {
            this.castTicks--;
            if (this.castTicks == 0) {
                this.releaseBolt();
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        this.triggerAnim("main", "cast");
        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0f, 1.0f);
        this.castTicks = CAST_RELEASE_TICKS;
        this.castTarget = target;
    }

    private void releaseBolt() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity target = this.castTarget;
        this.castTarget = null;

        Vec3 aim = target != null && target.isAlive()
                ? new Vec3(target.getX() - this.getX(), target.getY(0.5) - this.getEyeY(), target.getZ() - this.getZ())
                : this.getLookAngle();

        StarlightBoltProjectile bolt = new StarlightBoltProjectile(serverLevel, this, aim);
        bolt.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
        serverLevel.addFreshEntity(bolt);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide()) {
            this.triggerAnim("main", "death");
        }
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= DEATH_LENGTH_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<StargazerCultist> controller =
                new AnimationController<>(this, "main", 5, this::mainController);
        controller.triggerableAnim("cast", CAST)
                .triggerableAnim("death", DEATH);
        controllers.add(controller);
    }

    private PlayState mainController(AnimationState<StargazerCultist> state) {
        state.setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
