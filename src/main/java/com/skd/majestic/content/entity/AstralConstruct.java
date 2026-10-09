package com.skd.majestic.content.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
 * The Warden's summoned minion (Boss I phase 1). Also usable as a normal
 * structure guardian when not summoned: summoned constructs drop no loot or XP.
 */
public class AstralConstruct extends Monster implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.astral_construct.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.astral_construct.walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.astral_construct.attack");
    private static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("animation.astral_construct.spawn");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.astral_construct.death");

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private boolean summoned;
    private boolean spawnAnimationPlayed;
    private int spawnFreezeTicks;

    public AstralConstruct(EntityType<? extends AstralConstruct> entityType, Level level) {
        super(entityType, level);

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public boolean isSummoned() {
        return this.summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        // Summoned constructs rise out of the ground with a short spawn animation, frozen in place.
        if (this.summoned && !this.spawnAnimationPlayed) {
            this.spawnAnimationPlayed = true;
            this.triggerAnim("main", "spawn");
            this.spawnFreezeTicks = 20;
        }

        if (this.spawnFreezeTicks > 0) {
            this.spawnFreezeTicks--;
            this.getNavigation().stop();
            Vec3 delta = this.getDeltaMovement();
            this.setDeltaMovement(0.0, delta.y, 0.0);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<AstralConstruct> controller =
                new AnimationController<>(this, "main", 5, this::mainController);
        controller.triggerableAnim("attack", ATTACK)
                .triggerableAnim("spawn", SPAWN)
                .triggerableAnim("death", DEATH);
        controllers.add(controller);
    }

    private PlayState mainController(AnimationState<AstralConstruct> state) {
        state.setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.triggerAnim("main", "attack");
        return super.doHurtTarget(target);
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
        if (this.deathTime >= 24 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected boolean shouldDropLoot() {
        return !this.summoned;
    }

    @Override
    protected int getBaseExperienceReward() {
        return this.summoned ? 0 : super.getBaseExperienceReward();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 1.4f;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.STONE_STEP, 0.15f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Summoned", this.summoned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.summoned = tag.getBoolean("Summoned");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
