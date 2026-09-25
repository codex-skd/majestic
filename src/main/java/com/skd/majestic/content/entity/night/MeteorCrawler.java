package com.skd.majestic.content.entity.night;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * Night mob — a low, six-legged creature grown from fallen meteorite. Inherits the
 * spider's wall climbing and AI; plays the spider's role at night.
 */
public class MeteorCrawler extends Spider implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.meteor_crawler.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.meteor_crawler.walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.meteor_crawler.attack");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.meteor_crawler.death");

    private static final int DEATH_LENGTH_TICKS = 20;
    private static final int ATTACK_IMPACT_TICKS = 5;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final DelayedMeleeAttack pendingAttack = new DelayedMeleeAttack();

    public MeteorCrawler(EntityType<? extends MeteorCrawler> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!(target instanceof LivingEntity livingTarget)) {
            return super.doHurtTarget(target);
        }
        if (!this.pendingAttack.schedule(livingTarget, ATTACK_IMPACT_TICKS)) {
            return false;
        }
        this.triggerAnim("main", "attack");
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.pendingAttack.isPending()) {
            LivingEntity target = this.pendingAttack.tick();
            if (target != null && target.isAlive() && this.isWithinMeleeAttackRange(target)) {
                super.doHurtTarget(target);
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        this.pendingAttack.cancel();
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
        AnimationController<MeteorCrawler> controller =
                new AnimationController<>(this, "main", 5, this::mainController);
        controller.triggerableAnim("attack", ATTACK)
                .triggerableAnim("death", DEATH);
        controllers.add(controller);
    }

    private PlayState mainController(AnimationState<MeteorCrawler> state) {
        state.setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.7f;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15f, 0.7f);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
