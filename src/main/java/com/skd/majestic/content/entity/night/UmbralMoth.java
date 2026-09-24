package com.skd.majestic.content.entity.night;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * Night mob — a large moth drawn to starlight. Inherits the phantom's swoop AI, flying
 * and daylight burning. Its size is kept fixed at the registered dimensions by never
 * scaling the phantom size away from 0.
 */
public class UmbralMoth extends Phantom implements GeoEntity {

    private static final float ATTACK_DAMAGE = 3.0f;

    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("animation.umbral_moth.fly");
    private static final RawAnimation GLIDE = RawAnimation.begin().thenLoop("animation.umbral_moth.glide");
    private static final RawAnimation SWOOP = RawAnimation.begin().thenPlay("animation.umbral_moth.swoop");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.umbral_moth.death");

    private static final int DEATH_LENGTH_TICKS = 20;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public UmbralMoth(EntityType<? extends UmbralMoth> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    /**
     * Spawns like any other monster (dark areas) even though a phantom is not a
     * {@link Monster}; mirrors {@link Monster#checkMonsterSpawnRules}.
     */
    public static boolean checkSpawnRules(EntityType<UmbralMoth> type, ServerLevelAccessor level,
                                          MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || Monster.isDarkEnoughToSpawn(level, pos, random))
                && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        // Phantom#setPhantomSize(0) would otherwise leave the attack damage at 6.
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);
        return data;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.triggerAnim("main", "swoop");
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
        if (this.deathTime >= DEATH_LENGTH_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<UmbralMoth> controller =
                new AnimationController<>(this, "main", 5, this::mainController);
        controller.triggerableAnim("swoop", SWOOP)
                .triggerableAnim("death", DEATH);
        controllers.add(controller);
    }

    private PlayState mainController(AnimationState<UmbralMoth> state) {
        state.setAnimation(state.isMoving() ? FLY : GLIDE);
        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 1.3f;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
