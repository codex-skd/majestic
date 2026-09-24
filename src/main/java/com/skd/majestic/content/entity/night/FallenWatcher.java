package com.skd.majestic.content.entity.night;

import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.item.MajesticItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.item.ItemStack;
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
 * Night mob — an undead acolyte of the Order of Watchers. Shambles after players like a
 * zombie, burns in daylight without a helmet and still carries the Page of the Beginning.
 */
public class FallenWatcher extends Monster implements GeoEntity {

    private static final ResourceLocation CHAPTER_1 =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_1");

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.fallen_watcher.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.fallen_watcher.walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.fallen_watcher.attack");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.fallen_watcher.death");

    private static final int DEATH_LENGTH_TICKS = 24;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public FallenWatcher(EntityType<? extends FallenWatcher> entityType, Level level) {
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
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    public void aiStep() {
        if (this.isAlive() && this.isSunBurnTick() && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.igniteForSeconds(8.0f);
        }
        super.aiStep();
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
        if (this.deathTime >= DEATH_LENGTH_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    /**
     * Guarantees a Page of the Beginning for the first player who kills this watcher before
     * completing Chapter I, unless they already carry one.
     */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        if (!(damageSource.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (AdvancementHooks.has(player, CHAPTER_1)) {
            return;
        }

        ItemStack page = new ItemStack(MajesticItems.BEGINNING_PAGE.get());
        if (player.getInventory().contains(page)) {
            return;
        }
        this.spawnAtLocation(page);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<FallenWatcher> controller =
                new AnimationController<>(this, "main", 5, this::mainController);
        controller.triggerableAnim("attack", ATTACK)
                .triggerableAnim("death", DEATH);
        controllers.add(controller);
    }

    private PlayState mainController(AnimationState<FallenWatcher> state) {
        state.setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.8f;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15f, 0.8f);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
