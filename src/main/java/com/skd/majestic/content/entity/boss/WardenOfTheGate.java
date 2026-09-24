package com.skd.majestic.content.entity.boss;

import com.skd.expeditioncore.boss.ArenaLock;
import com.skd.expeditioncore.boss.BossBarController;
import com.skd.expeditioncore.boss.BossEncounter;
import com.skd.expeditioncore.boss.BossEncounterData;
import com.skd.expeditioncore.boss.BossLootTable;
import com.skd.expeditioncore.boss.render.GeoBossEntity;
import com.skd.expeditioncore.config.ExpeditionConfig;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.entity.AstralConstruct;
import com.skd.majestic.content.entity.MajesticEntities;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * Boss I — the Warden of the Gate (Observatory arena, Act II).
 *
 * <p>Two phases: phase 1 summons {@link AstralConstruct} minions; below 50% health the boss
 * transitions to phase 2 and covers the arena with line-of-sight light pulses. Uses
 * {@code expedition_core}'s {@link BossEncounter} for phase tracking, {@link BossBarController}
 * for the bar and {@link BossLootTable} for the per-participant reward roll.
 */
public class WardenOfTheGate extends Monster implements GeoBossEntity {

    private static final ResourceLocation SCALING_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "boss_scaling");

    private static final BossLootTable LOOT_TABLE = new BossLootTable(
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "boss/warden_of_the_gate")));

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.warden_of_the_gate.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.warden_of_the_gate.walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.warden_of_the_gate.attack");
    private static final RawAnimation SUMMON = RawAnimation.begin().thenPlay("animation.warden_of_the_gate.summon");
    private static final RawAnimation PHASE_TRANSITION =
            RawAnimation.begin().thenPlay("animation.warden_of_the_gate.phase_transition");
    private static final RawAnimation DEATH =
            RawAnimation.begin().thenPlayAndHold("animation.warden_of_the_gate.death");

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final BossEncounter encounter;
    private final BossBarController bossBar;

    private final List<UUID> minionIds = new ArrayList<>();
    private final Set<UUID> participants = new HashSet<>();

    private BlockPos arenaCenter = BlockPos.ZERO;
    private int arenaHalfX = 12;
    private int arenaHalfY = 6;
    private int arenaHalfZ = 12;
    private boolean arenaInitialized;
    private ArenaLock arenaLock;

    private int summonCooldown;
    private int summonSpawnCountdown = -1;
    private int freezeTicks;
    private int invulnerableTicks;
    private int ticksWithoutPlayer;
    private int pulseCooldown = 140;
    private int pulseTelegraph;
    private boolean phaseTwo;

    public WardenOfTheGate(EntityType<? extends WardenOfTheGate> entityType, Level level) {
        super(entityType, level);

        this.xpReward = 200;
        this.setPersistenceRequired();

        this.encounter = BossEncounter.builder(this)
                .phase(1.0f, mob -> ((WardenOfTheGate) mob).enterPhaseOne())
                .phase(0.5f, mob -> ((WardenOfTheGate) mob).enterPhaseTwo())
                .build();
        BossEncounter.attach(this, this.encounter);

        this.bossBar = new BossBarController(
                Component.translatable("entity.majestic.warden_of_the_gate"),
                BossEvent.BossBarColor.BLUE,
                BossEvent.BossBarOverlay.NOTCHED_10);

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    // --- Arena ---

    /** Sets the arena bounds, normally read from the Observatory arena piece's bounding box. */
    public void setArena(BlockPos center, int halfX, int halfY, int halfZ) {
        this.arenaCenter = center.immutable();
        this.arenaHalfX = halfX;
        this.arenaHalfY = halfY;
        this.arenaHalfZ = halfZ;
        this.arenaInitialized = true;
        this.arenaLock = null;
        this.restrictTo(this.arenaCenter, Math.max(halfX, halfZ));
    }

    private void ensureArenaInitialized() {
        if (this.arenaInitialized) {
            return;
        }
        this.arenaCenter = this.blockPosition();
        this.arenaHalfX = 12;
        this.arenaHalfY = 6;
        this.arenaHalfZ = 12;
        this.arenaInitialized = true;
        this.arenaLock = null;
        this.restrictTo(this.arenaCenter, Math.max(this.arenaHalfX, this.arenaHalfZ));
    }

    private ArenaLock arenaLock() {
        if (this.arenaLock == null) {
            Vec3 center = new Vec3(this.arenaCenter.getX() + 0.5, this.arenaCenter.getY(), this.arenaCenter.getZ() + 0.5);
            this.arenaLock = new ArenaLock(center, this.arenaHalfX, this.arenaHalfY, this.arenaHalfZ, 2.0,
                    this::resetEncounter);
        }
        return this.arenaLock;
    }

    // --- Ticking / encounter state machine ---

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        ServerLevel level = (ServerLevel) this.level();
        ensureArenaInitialized();

        if (!isEngaged()) {
            tryEngage(level);
        }

        if (!isEngaged()) {
            return;
        }

        this.encounter.tick(this);
        updateBossBar(level);
        updateParticipants(level);
        handleFreeze();

        if (checkEscape(level)) {
            return;
        }

        if (this.phaseTwo) {
            tickPulses(level);
        } else {
            tickSummoning(level);
        }
    }

    private boolean isEngaged() {
        BossEncounterData data = BossEncounter.getData(this);
        return data != null && data.engaged();
    }

    private void tryEngage(ServerLevel level) {
        LivingEntity target = this.getTarget();
        if (target instanceof Player && arenaLock().contains(target.position())) {
            engage(level);
        }
    }

    private void engage(ServerLevel level) {
        int playerCount = countPlayersInArena(level);
        applyScaling(Attributes.MAX_HEALTH,
                ExpeditionConfig.scalingMultiplier(playerCount, ExpeditionConfig.SERVER.bossHealthMultiplierPerExtraPlayer.get()));
        applyScaling(Attributes.ATTACK_DAMAGE,
                ExpeditionConfig.scalingMultiplier(playerCount, ExpeditionConfig.SERVER.bossDamageMultiplierPerExtraPlayer.get()));
        this.setHealth(this.getMaxHealth());
        this.encounter.engage(this);
        this.participants.clear();
        this.ticksWithoutPlayer = 0;
        this.summonCooldown = 100;
        this.bossBar.setVisible(true);
    }

    private void applyScaling(Holder<Attribute> attribute, double multiplier) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(SCALING_MODIFIER_ID);
        // Permanent so it is saved with the entity: a world reload mid-fight keeps the scaled health.
        instance.addPermanentModifier(new AttributeModifier(SCALING_MODIFIER_ID, multiplier - 1.0,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private void removeScaling(Holder<Attribute> attribute) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(SCALING_MODIFIER_ID);
        }
    }

    private void enterPhaseOne() {
        // Phase 1 is the default summoner behaviour; the summon timer is set when engaging.
    }

    private void enterPhaseTwo() {
        this.phaseTwo = true;
        this.summonSpawnCountdown = -1;
        this.freezeTicks = 26;
        this.invulnerableTicks = 26;
        this.pulseCooldown = 140;
        this.pulseTelegraph = 0;
        this.triggerAnim("main", "phase_transition");
    }

    private void updateBossBar(ServerLevel level) {
        this.bossBar.setProgress(Mth.clamp(this.getHealth() / this.getMaxHealth(), 0.0f, 1.0f));
        if (this.tickCount % 10 != 0) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (arenaLock().contains(player.position())) {
                this.bossBar.addPlayer(player);
            } else {
                this.bossBar.removePlayer(player);
            }
        }
    }

    private void updateParticipants(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (arenaLock().contains(player.position())) {
                this.participants.add(player.getUUID());
            }
        }
    }

    private void handleFreeze() {
        if (this.invulnerableTicks > 0) {
            this.invulnerableTicks--;
        }
        if (this.freezeTicks > 0) {
            this.freezeTicks--;
            this.getNavigation().stop();
            Vec3 delta = this.getDeltaMovement();
            this.setDeltaMovement(0.0, delta.y, 0.0);
        }
    }

    /** Returns true when the encounter was reset because every player left the arena. */
    private boolean checkEscape(ServerLevel level) {
        boolean anyInside = false;
        for (ServerPlayer player : level.players()) {
            if (arenaLock().contains(player.position())) {
                anyInside = true;
                break;
            }
        }

        if (anyInside) {
            this.ticksWithoutPlayer = 0;
            return false;
        }

        this.ticksWithoutPlayer++;
        if (this.ticksWithoutPlayer >= ExpeditionConfig.SERVER.arenaEscapeGraceTicks.get()) {
            resetEncounter();
            return true;
        }
        return false;
    }

    private void resetEncounter() {
        removeScaling(Attributes.MAX_HEALTH);
        removeScaling(Attributes.ATTACK_DAMAGE);
        this.setHealth(this.getMaxHealth());
        discardMinions();
        this.setTarget(null);
        this.ticksWithoutPlayer = 0;
        this.freezeTicks = 0;
        this.invulnerableTicks = 0;
        this.phaseTwo = false;
        this.summonCooldown = 0;
        this.summonSpawnCountdown = -1;
        this.pulseCooldown = 140;
        this.pulseTelegraph = 0;
        this.participants.clear();

        BossEncounterData data = BossEncounter.getData(this);
        if (data != null) {
            data.setEngaged(false);
            data.setCurrentPhase(-1);
        }

        this.bossBar.removeAllPlayers();
        this.bossBar.setVisible(false);
    }

    private int countPlayersInArena(ServerLevel level) {
        int count = 0;
        for (ServerPlayer player : level.players()) {
            if (arenaLock().contains(player.position())) {
                count++;
            }
        }
        return Math.max(1, count);
    }

    // --- Phase 1: summoning ---

    private void tickSummoning(ServerLevel level) {
        if (this.summonSpawnCountdown > 0) {
            this.summonSpawnCountdown--;
            if (this.summonSpawnCountdown == 0) {
                spawnMinions(level);
            }
            return;
        }

        if (this.summonCooldown > 0) {
            this.summonCooldown--;
        }

        if (this.summonCooldown == 0) {
            if (countLiveMinions(level) < 3) {
                startSummon(level);
            }
            this.summonCooldown = 300;
        }
    }

    private void startSummon(ServerLevel level) {
        this.triggerAnim("main", "summon");
        this.freezeTicks = 28;
        this.summonSpawnCountdown = 12;
        level.playSound(null, this.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.5, this.getZ(), 20, 0.6, 0.8, 0.6, 0.04);
        level.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.0, this.getZ(), 12, 0.6, 0.6, 0.6, 0.02);
    }

    private void spawnMinions(ServerLevel level) {
        for (int i = 0; i < 2; i++) {
            spawnMinion(level);
        }
    }

    private void spawnMinion(ServerLevel level) {
        for (int attempt = 0; attempt < 12; attempt++) {
            double angle = this.random.nextDouble() * (Math.PI * 2.0);
            double distance = 3.0 + this.random.nextDouble() * 2.0;
            int x = Mth.floor(this.getX() + Math.cos(angle) * distance);
            int z = Mth.floor(this.getZ() + Math.sin(angle) * distance);
            BlockPos ground = findGround(level, x, z);
            if (ground == null || !arenaLock().containsStrict(Vec3.atCenterOf(ground))) {
                continue;
            }

            AstralConstruct minion = MajesticEntities.ASTRAL_CONSTRUCT.get().create(level);
            if (minion == null) {
                continue;
            }
            minion.moveTo(ground.getX() + 0.5, ground.getY(), ground.getZ() + 0.5,
                    this.random.nextFloat() * 360.0f, 0.0f);
            if (!level.noCollision(minion)) {
                continue;
            }

            minion.setSummoned(true);
            minion.setTarget(this.getTarget());
            level.addFreshEntity(minion);
            this.minionIds.add(minion.getUUID());
            level.sendParticles(ParticleTypes.END_ROD, minion.getX(), minion.getY() + 0.5, minion.getZ(),
                    12, 0.3, 0.6, 0.3, 0.02);
            level.sendParticles(ParticleTypes.SOUL, minion.getX(), minion.getY() + 0.5, minion.getZ(),
                    8, 0.3, 0.6, 0.3, 0.02);
            return;
        }
    }

    @Nullable
    private BlockPos findGround(ServerLevel level, int x, int z) {
        int startY = Math.min(level.getMaxBuildHeight() - 2, this.blockPosition().getY() + 3);
        for (int y = startY; y > level.getMinBuildHeight(); y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP)
                    && level.isEmptyBlock(pos.above())
                    && level.isEmptyBlock(pos.above(2))) {
                return pos.above();
            }
        }
        return null;
    }

    private int countLiveMinions(ServerLevel level) {
        int count = 0;
        Iterator<UUID> iterator = this.minionIds.iterator();
        while (iterator.hasNext()) {
            Entity entity = level.getEntity(iterator.next());
            if (entity instanceof AstralConstruct construct && construct.isAlive()) {
                count++;
            } else {
                iterator.remove();
            }
        }
        return count;
    }

    private void discardMinions() {
        if (this.level() instanceof ServerLevel level) {
            for (UUID id : this.minionIds) {
                Entity entity = level.getEntity(id);
                if (entity instanceof AstralConstruct construct) {
                    construct.discard();
                }
            }
        }
        this.minionIds.clear();
    }

    // --- Phase 2: light pulses ---

    private void tickPulses(ServerLevel level) {
        if (this.pulseTelegraph > 0) {
            this.pulseTelegraph--;
            if (this.pulseTelegraph % 2 == 0) {
                level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 0.2, this.getZ(),
                        3, 1.5, 0.1, 1.5, 0.05);
            }
            if (this.pulseTelegraph == 0) {
                executePulse(level);
            }
            return;
        }

        if (this.pulseCooldown > 0) {
            this.pulseCooldown--;
        }

        if (this.pulseCooldown == 0) {
            this.pulseTelegraph = 30;
            this.pulseCooldown = 140;
            this.freezeTicks = Math.max(this.freezeTicks, 30);
            level.playSound(null, this.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.0f, 1.0f);
            this.triggerAnim("main", "summon");
        }
    }

    private void executePulse(ServerLevel level) {
        level.playSound(null, this.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.2f, 1.0f);
        level.sendParticles(ParticleTypes.FLASH, this.getX(), this.getEyeY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);

        Vec3 from = this.getEyePosition();
        for (ServerPlayer player : level.players()) {
            if (!arenaLock().contains(player.position())) {
                continue;
            }
            if (this.distanceTo(player) > 24.0) {
                continue;
            }
            BlockHitResult hit = level.clip(new ClipContext(from, player.getEyePosition(),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS) {
                continue;
            }
            player.hurt(this.damageSources().indirectMagic(this, this), 8.0f);
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60), this);
        }
    }

    // --- Combat / death ---

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (this.invulnerableTicks > 0) {
            return false;
        }
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.triggerAnim("main", "attack");
        return super.doHurtTarget(target);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        if (this.level().isClientSide()) {
            return;
        }

        this.encounter.defeat(this);
        discardMinions();
        this.bossBar.removeAllPlayers();
        this.bossBar.setVisible(false);
        this.triggerAnim("main", "death");

        if (this.level() instanceof ServerLevel level) {
            distributeLoot(level);
        }
    }

    private void distributeLoot(ServerLevel level) {
        if (this.participants.isEmpty()) {
            return;
        }
        for (UUID id : this.participants) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
            if (player == null) {
                continue;
            }
            for (ItemStack stack : LOOT_TABLE.rollLoot(level, this)) {
                if (stack.isEmpty()) {
                    continue;
                }
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
        }
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 36 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (!this.level().isClientSide()) {
            this.bossBar.dispose();
        }
    }

    // --- Entity flags ---

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        ensureArenaInitialized();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    // --- Animations ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<WardenOfTheGate> controller =
                new AnimationController<>(this, "main", 5, this::mainController);
        controller.triggerableAnim("attack", ATTACK)
                .triggerableAnim("summon", SUMMON)
                .triggerableAnim("phase_transition", PHASE_TRANSITION)
                .triggerableAnim("death", DEATH);
        controllers.add(controller);
    }

    private PlayState mainController(AnimationState<WardenOfTheGate> state) {
        state.setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // --- Sounds ---

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0f, 1.0f);
    }

    // --- Persistence ---

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ArenaInitialized", this.arenaInitialized);
        tag.putInt("ArenaX", this.arenaCenter.getX());
        tag.putInt("ArenaY", this.arenaCenter.getY());
        tag.putInt("ArenaZ", this.arenaCenter.getZ());
        tag.putInt("ArenaHalfX", this.arenaHalfX);
        tag.putInt("ArenaHalfY", this.arenaHalfY);
        tag.putInt("ArenaHalfZ", this.arenaHalfZ);
        tag.putBoolean("PhaseTwo", this.phaseTwo);
        tag.put("Minions", writeUuidList(this.minionIds));
        tag.put("Participants", writeUuidList(this.participants));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("ArenaInitialized")) {
            this.arenaInitialized = tag.getBoolean("ArenaInitialized");
            this.arenaCenter = new BlockPos(tag.getInt("ArenaX"), tag.getInt("ArenaY"), tag.getInt("ArenaZ"));
            this.arenaHalfX = tag.getInt("ArenaHalfX");
            this.arenaHalfY = tag.getInt("ArenaHalfY");
            this.arenaHalfZ = tag.getInt("ArenaHalfZ");
            this.arenaLock = null;
            this.restrictTo(this.arenaCenter, Math.max(this.arenaHalfX, this.arenaHalfZ));
        }

        this.phaseTwo = tag.getBoolean("PhaseTwo");

        this.minionIds.clear();
        ListTag minions = tag.getList("Minions", Tag.TAG_COMPOUND);
        for (int i = 0; i < minions.size(); i++) {
            this.minionIds.add(minions.getCompound(i).getUUID("Id"));
        }

        this.participants.clear();
        ListTag participantsTag = tag.getList("Participants", Tag.TAG_COMPOUND);
        for (int i = 0; i < participantsTag.size(); i++) {
            this.participants.add(participantsTag.getCompound(i).getUUID("Id"));
        }
    }

    private static ListTag writeUuidList(Iterable<UUID> ids) {
        ListTag list = new ListTag();
        for (UUID id : ids) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Id", id);
            list.add(entry);
        }
        return list;
    }
}
