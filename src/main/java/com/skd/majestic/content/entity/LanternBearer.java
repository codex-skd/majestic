package com.skd.majestic.content.entity;

import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.event.LanternBearerTrigger;
import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.event.LanternBearerTrigger;
import com.skd.majestic.content.item.MajesticItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

/**
 * The hooded spirit that waits in front of a column of the Arcane Portal and gives the player the
 * page that starts the search for the Order's stone.
 *
 * <p>Not a monster and not a mob with a brain: there are no AI goals and it never uses navigation. The
 * whole encounter is one scripted run — materialise at the column, wait, glide over when the player
 * comes within five blocks, hold out the lantern, drift away — so it is driven by a small phase
 * machine ticked by hand. That is deliberate: an NPC this narrow has nothing for vanilla goal
 * selection to do, and a scripted run can be synced to animation timings (the page is handed over
 * exactly at the {@code offer} animation's 0.8 s mark).</p>
 *
 * <p>Waiting in the open, in front of a pillar, instead of appearing at arm's length is the whole
 * point of the change: the player sees it arrive, sees it watching, and walks up to it, rather than
 * the encounter being over before they have turned around. Where it waits is data — see
 * {@code majestic_portal_anchors.json} — so moving a pillar moves the ghost with it.</p>
 *
 * <p>It cannot be hurt, cannot be pushed, drops nothing and gives nothing on death. The only thing it
 * does is the handoff, which happens once per player per Portal instance, and it survives a save and
 * reload in the middle of the wait: the encounter, the phase and who it is waiting for are all
 * written down.</p>
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
    /**
     * Length of the leaving, in ticks: three seconds of drifting back and up while the fade plays,
     * rather than a spirit that pops out of existence the instant it is done.
     */
    private static final int FADE_TICKS = 60;
    /** The page appears as the lantern flame swells, which is 0.8 s into the offer animation. */
    private static final int OFFER_HANDOFF_TICK = 16;
    /** How close it glides to the player before it stops, in blocks: about in front of their face. */
    private static final double OFFER_DISTANCE = 2.0;
    /** How far away it still notices the player while waiting, in blocks. */
    private static final double ENGAGE_DISTANCE = 5.0;
    /** How far from the Portal the player may go before the waiting starts to count, in blocks. */
    private static final double LEAVE_DISTANCE = 48.0;
    /** Ticks outside {@link #LEAVE_DISTANCE} before it gives up waiting: thirty seconds. */
    private static final int LEAVE_GRACE_TICKS = 600;
    /** A spirit drifts at half walking pace; it is not hurrying. */
    private static final double APPROACH_SPEED = 0.25;
    private static final double VERTICAL_SPEED = 0.3;
    /** Backwards and upwards, per tick, once the handoff is done. */
    private static final double LEAVE_BACK_SPEED = 0.08;
    private static final double LEAVE_UP_SPEED = 0.12;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private ServerPlayer target;
    /** The player this encounter belongs to, kept as an id so a reload can find them again. */
    private @Nullable UUID targetId;
    private String instanceId = "";
    private Phase phase = Phase.APPEARING;
    private int phaseTicks;
    private boolean pageHandedOver;
    /** Ticks spent with the player outside the Portal while waiting. */
    private int awayTicks;
    /** Which way "away from the player" was when it started leaving. */
    private Vec3 leaveDirection = new Vec3(0.0, 0.0, -1.0);

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
        this.targetId = player.getUUID();
        this.instanceId = instanceId;
        this.phase = Phase.APPEARING;
        this.phaseTicks = 0;
        this.pageHandedOver = false;
        this.awayTicks = 0;
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
                    // It has arrived at its column: now it waits, and the encounter only moves on
                    // when the player comes to it.
                    this.phase = Phase.WAITING;
                    this.phaseTicks = 0;
                    this.triggerAnim("main", "float");
                }
            }
            case WAITING -> this.waitInPlace();
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

    /**
     * The waiting phase: it stands in front of the column, turns slowly to keep the player in sight,
     * throws a few sparks so it is not invisible in the dark, and only leaves for good if the player
     * walks away from the Portal for half a minute.
     *
     * <p>A player who is not online is not "gone": it keeps waiting, so a logout mid-encounter is a
     * pause rather than the end of it.</p>
     */
    private void waitInPlace() {
        // No motion, but free to turn: unlike hold(), this does not freeze the yaw.
        this.setDeltaMovement(Vec3.ZERO);

        ServerPlayer player = currentTarget();
        if (player == null) {
            return;
        }

        if (this.distanceTo(player) > LEAVE_DISTANCE) {
            if (++this.awayTicks >= LEAVE_GRACE_TICKS) {
                // Left behind. No markMet, so the trigger can offer the encounter again next time.
                this.discard();
                return;
            }
        } else {
            this.awayTicks = 0;
        }

        this.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ());

        if (this.tickCount % 12 == 0 && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getEyeY(), this.getZ(),
                    1, 0.25, 0.4, 0.25, 0.0);
            if (this.tickCount % 48 == 0) {
                serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(),
                        1, 0.15, 0.3, 0.15, 0.0);
            }
        }

        if (this.distanceTo(player) <= ENGAGE_DISTANCE && this.hasLineOfSight(player)) {
            this.phase = Phase.APPROACHING;
            this.phaseTicks = 0;
            this.triggerAnim("main", "float");
        }
    }

    /**
     * The player this encounter belongs to, or null when they are not in this level right now.
     *
     * <p>The direct reference goes stale on a reload — the player is another entity, resolved on
     * its own — so the id is the truth and the reference is only a cache.</p>
     */
    private @Nullable ServerPlayer currentTarget() {
        if (this.target != null && this.target.isAlive() && this.target.level() == this.level()) {
            return this.target;
        }
        this.target = null;
        if (this.targetId == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(this.targetId);
        if (player != null && player.isAlive() && player.level() == this.level()) {
            this.target = player;
            return player;
        }
        return null;
    }

    /** Standing still in mid-air: the idle animation carries the motion, not the physics. */
    private void hold() {
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(0.0, motion.y, 0.0);
        this.setYRot(this.yRotO);
        this.yHeadRot = this.getYRot();
    }

    /**
     * Glides from the column to a point about two blocks in front of the player's face, then stops
     * and turns to them.
     *
     * <p>Arrival is measured against that point, not against the player's feet: a spirit at eye
     * height two blocks away is further than two blocks from the floor they stand on, and
     * measuring the wrong thing is how it used to sail past without ever offering.</p>
     */
    private void approach() {
        ServerPlayer player = currentTarget();
        if (player == null) {
            this.startLeaving();
            return;
        }

        Vec3 aim = hoverPoint(player);
        if (this.position().distanceToSqr(aim) <= OFFER_DISTANCE * OFFER_DISTANCE) {
            this.phase = Phase.OFFERING;
            this.phaseTicks = 0;
            this.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ());
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
        this.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ());
    }

    /** Turns to face the player, hands the page over mid-animation, then leaves. */
    private void offer() {
        // Stands still but keeps free to turn: holding the yaw would lock it facing away.
        this.setDeltaMovement(Vec3.ZERO);
        ServerPlayer player = currentTarget();
        if (player == null) {
            this.startLeaving();
            return;
        }
        this.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ());

        if (!this.pageHandedOver && this.phaseTicks >= OFFER_HANDOFF_TICK) {
            this.pageHandedOver = true;
            this.handover(player);
        }

        if (this.phaseTicks >= OFFER_TICKS) {
            this.startLeaving();
        }
    }

    /**
     * Gives the page and the Illumination Stone. The chapter swap itself belongs to the item — it grants
     * the search chapter and revokes the portal chapter when read — so this only puts the page in the
     * player's hands. Granting anything here would leave the page in their inventory afterwards, refused
     * as already read.
     *
     * <p>The stone is handed over here rather than only lying in the Observatory chest because it is
     * the one thing the player must not leave without: it is what wakes the Watchers' Vault, and a
     * player who misses the chest would have no way back to it. The chest keeps its copy as well, so
     * finding it there is a bonus and not the only path.</p>
     */
    private void handover(ServerPlayer player) {
        ItemStack page = new ItemStack(MajesticItems.LANTERN_PAGE.get());
        if (!player.getInventory().add(page)) {
            player.drop(page, false);
        }
        ItemStack stone = new ItemStack(MajesticItems.ILLUMINATION_STONE.get());
        if (!player.getInventory().add(stone)) {
            player.drop(stone, false);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getEyeY(), this.getZ(),
                    24, 0.35, 0.35, 0.35, 0.02);
        }
        this.sound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.7f, 0.8f);
        AdvancementHooks.grant(player, MET_ADVANCEMENT);
        LanternBearerTrigger.markMet(player, this.instanceId);
    }

    private void startLeaving() {
        this.phase = Phase.FADING;
        this.phaseTicks = 0;
        // Which way is "away from the player" is decided once, here: the drift must not swing around
        // if the player walks while the spirit is dissolving.
        ServerPlayer player = currentTarget();
        Vec3 away = player == null
                ? new Vec3(0.0, 0.0, -1.0)
                : this.position().subtract(player.position());
        Vec3 flat = new Vec3(away.x, 0.0, away.z);
        this.leaveDirection = flat.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, -1.0) : flat.normalize();
        this.triggerAnim("main", "fade");
        this.sound(SoundEvents.SOUL_ESCAPE.value(), 0.4f, 0.8f);
    }

    /** Drifting back and up, slowly, for as long as the fade plays: it does not vanish on the spot. */
    private void drift() {
        this.setDeltaMovement(this.leaveDirection.x * LEAVE_BACK_SPEED, LEAVE_UP_SPEED,
                this.leaveDirection.z * LEAVE_BACK_SPEED);
    }

    /** The point it glides to: right in front of the player's face, not at their feet. */
    private Vec3 hoverPoint(ServerPlayer player) {
        return new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
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
        // to loop in between them: floating while it waits at its column and while it glides over.
        if (this.phase == Phase.WAITING || this.phase == Phase.APPROACHING) {
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

    // ---- persistence ----

    /**
     * What is written down is the encounter, not the player: the instance it belongs to, how far
     * through the run it is, and who it is waiting for, as an id. Without that a reload mid-wait
     * would drop a ghost that knows nothing about where it was or whom it was waiting for, and the
     * trigger would start a second one on the next tick.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Instance", this.instanceId);
        tag.putString("Phase", this.phase.name());
        tag.putInt("PhaseTicks", this.phaseTicks);
        tag.putBoolean("HandedOver", this.pageHandedOver);
        tag.putInt("AwayTicks", this.awayTicks);
        if (this.targetId != null) {
            tag.putUUID("Target", this.targetId);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.instanceId = tag.getString("Instance");
        this.phase = phaseOf(tag.getString("Phase"));
        this.phaseTicks = tag.getInt("PhaseTicks");
        this.pageHandedOver = tag.getBoolean("HandedOver");
        this.awayTicks = tag.getInt("AwayTicks");
        this.targetId = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        // Deliberately not resolved here: on load the player may not be in the level yet, and the
        // waiting phase is exactly the one that carries on when they are not there.
        this.target = null;
    }

    private static Phase phaseOf(String name) {
        for (Phase phase : Phase.values()) {
            if (phase.name().equals(name)) {
                return phase;
            }
        }
        // Anything unrecognised becomes waiting: the ghost keeps its post and carries on.
        return Phase.WAITING;
    }

    private enum Phase {
        IDLE,
        APPEARING,
        WAITING,
        APPROACHING,
        OFFERING,
        FADING
    }
}