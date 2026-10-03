package com.skd.majestic.content.entity;

import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.event.LanternBearerTrigger;
import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.event.LanternBearerTrigger;
import com.skd.majestic.content.item.MajesticItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
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
 * The hooded spirit that waits in the ruined forecourt of the Arcane Portal and gives the player the
 * page that starts the search for the Order's stone.
 *
 * <p>Not a monster and not a mob with a brain: there are no AI goals and it never uses navigation. The
 * whole encounter is one scripted run — materialise, cross the forecourt, hold out the lantern, leave —
 * so it is driven by a small phase machine ticked by hand. That is deliberate: an NPC this narrow has
 * nothing for vanilla goal selection to do, and a scripted run can be synced to animation timings
 * (the page is handed over exactly at the {@code offer} animation's 0.8 s mark).</p>
 *
 * <p>It cannot be hurt, cannot be pushed, drops nothing and gives nothing on death. The only thing it
 * does is the handoff, which happens once per player per Portal instance.</p>
 */
public class LanternBearer extends PathfinderMob implements GeoEntity {

    /** Granted when the page actually changes hands, so it is meeting the ghost that counts. */
    private static final ResourceLocation MET_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/met_the_lantern_bearer");

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.lantern_bearer.idle");
    private static final RawAnimation FLOAT = RawAnimation.begin().thenLoop("animation.lantern_bearer.float");
    private static final RawAnimation APPEAR = RawAnimation.begin().thenPlay("animation.lantern_bearer.appear");
    private static final RawAnimation OFFER = RawAnimation.begin().thenPlay("animation.lantern_bearer.offer");
    private static final RawAnimation FADE = RawAnimation.begin().thenPlayAndHold("animation.lantern_bearer.fade");

    /** Length of {@code animation.lantern_bearer.appear}, in ticks. */
    private static final int APPEAR_TICKS = 24;
    /** Length of {@code animation.lantern_bearer.offer}, in ticks. */
    private static final int OFFER_TICKS = 30;
    /** Length of {@code animation.lantern_bearer.fade}, in ticks. */
    private static final int FADE_TICKS = 30;
    /** The page appears as the lantern flame swells, which is 0.8 s into the offer animation. */
    private static final int OFFER_HANDOFF_TICK = 16;
    /** How close it floats to the player before turning and offering, in blocks. */
    private static final double OFFER_DISTANCE = 2.2;
    /** Hover height above the player's feet that it aims for. */
    private static final double HOVER_HEIGHT = 1.1;
    private static final double APPROACH_SPEED = 0.55;
    private static final double VERTICAL_SPEED = 0.45;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private ServerPlayer target;
    private String instanceId = "";
    private Phase phase = Phase.APPEARING;
    private int phaseTicks;
    private boolean pageHandedOver;

    public LanternBearer(EntityType<? extends LanternBearer> entityType, Level level) {
        super(entityType, level);

        // A spirit of light does not fall, and never touches the floor.
        this.setNoGravity(true);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    /**
     * Points the apparition at the player it is waiting for and starts the encounter.
     *
     * @param instanceId names the Portal instance this encounter belongs to, so the trigger can record
     *                   that this player has been given the page and not spawn another ghost here
     */
    public void beginFor(ServerPlayer player, String instanceId) {
        this.target = player;
        this.instanceId = instanceId;
        this.phase = Phase.APPEARING;
        this.phaseTicks = 0;
        this.pageHandedOver = false;
        this.triggerAnim("main", "appear");
        this.sound(SoundEvents.SOUL_ESCAPE.value(), 0.5f, 1.4f);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        this.phaseTicks++;

        switch (this.phase) {
            case APPEARING -> {
                this.hold();
                if (this.phaseTicks >= APPEAR_TICKS) {
                    this.phase = Phase.APPROACHING;
                    this.phaseTicks = 0;
                    this.triggerAnim("main", "float");
                }
            }
            case APPROACHING -> this.approach();
            case OFFERING -> this.offer();
            case FADING -> {
                this.drift();
                if (this.phaseTicks >= FADE_TICKS) {
                    this.discard();
                }
            }
        }
    }

    /** Standing still in mid-air: the idle animation carries the motion, not the physics. */
    private void hold() {
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(0.0, motion.y, 0.0);
        this.setYRot(this.yRotO);
        this.yHeadRot = this.getYRot();
    }

    /** Crosses the forecourt towards the player, then stops close enough to hand something over. */
    private void approach() {
        if (this.gone()) {
            this.startLeaving();
            return;
        }

        Vec3 aim = this.hoverPoint();
        if (this.distanceToSqr(this.target) <= OFFER_DISTANCE * OFFER_DISTANCE) {
            this.phase = Phase.OFFERING;
            this.phaseTicks = 0;
            this.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
            this.triggerAnim("main", "offer");
            return;
        }

        Vec3 to = aim.subtract(this.position());
        Vec3 flat = new Vec3(to.x, 0.0, to.z);
        double flatDistance = flat.length();
        Vec3 motion = flatDistance > 1.0E-4
                ? flat.normalize().scale(APPROACH_SPEED)
                : Vec3.ZERO;

        double climb = clamp(-to.y * 0.35, -VERTICAL_SPEED, VERTICAL_SPEED);
        this.setDeltaMovement(motion.x, climb, motion.z);

        // 3.4: face the player for the whole crossing, not only once it has arrived. It used to
        // arrive backwards whenever the look control lagged a tick behind the movement.
        this.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        this.setYRot(this.getYRot());
    }

    /** Turns to face the player, hands the page over mid-animation, then leaves. */
    private void offer() {
        this.hold();

        if (!this.pageHandedOver && this.phaseTicks >= OFFER_HANDOFF_TICK) {
            this.pageHandedOver = true;
            this.handover();
        }

        if (this.phaseTicks >= OFFER_TICKS) {
            this.startLeaving();
        }
    }

    /**
     * Gives the page. The chapter swap itself belongs to the item — it grants the search chapter and
     * revokes the portal chapter when read — so this only puts the page in the player's hands. Granting
     * anything here would leave the page in their inventory afterwards, refused as already read.
     */
    private void handover() {
        ItemStack page = new ItemStack(MajesticItems.LANTERN_PAGE.get());
        if (!this.target.getInventory().add(page)) {
            this.target.drop(page, false);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getEyeY(), this.getZ(),
                    24, 0.35, 0.35, 0.35, 0.02);
        }
        this.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.7f, 0.8f);
        AdvancementHooks.grant(this.target, MET_ADVANCEMENT);
        LanternBearerTrigger.markMet(this.target, this.instanceId);
    }

    private void startLeaving() {
        this.phase = Phase.FADING;
        this.phaseTicks = 0;
        this.triggerAnim("main", "fade");
        this.sound(SoundEvents.SOUL_ESCAPE.value(), 0.4f, 0.8f);
    }

    /** Dissolving upward. */
    private void drift() {
        this.setDeltaMovement(0.0, 0.25, 0.0);
    }

    /**
     * True when the encounter cannot continue — the player logged off or died. The ghost leaves
     * rather than freezing in mid-air, and the trigger can offer it again later.
     */
    private boolean gone() {
        // A disconnected player is removed from the level, so a different one means "logged off".
        return this.target == null || !this.target.isAlive() || this.target.level() != this.level();
    }

    private Vec3 hoverPoint() {
        return this.target.position().add(0.0, HOVER_HEIGHT, 0.0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // ---- it is a ghost, not a participant ----

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    /** A sound made by this ghost, heard by everyone nearby. */
    private void sound(SoundEvent event, float volume, float pitch) {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), event, SoundSource.PLAYERS, volume, pitch);
    }

    // ---- animation ----

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<LanternBearer> controller =
                new AnimationController<>(this, "main", 5, this::mainController);
        controller.triggerableAnim("appear", APPEAR)
                .triggerableAnim("offer", OFFER)
                .triggerableAnim("fade", FADE);
        controllers.add(controller);
    }

    private PlayState mainController(AnimationState<LanternBearer> state) {
        // The one-shot animations take over while they play, so the base animation only decides what
        // to loop in between them.
        if (this.phase == Phase.APPROACHING) {
            state.setAnimation(FLOAT);
        } else if (this.phase == Phase.IDLE) {
            state.setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private enum Phase {
        IDLE,
        APPEARING,
        APPROACHING,
        OFFERING,
        FADING
    }
}