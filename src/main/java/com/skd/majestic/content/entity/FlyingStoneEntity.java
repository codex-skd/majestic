package com.skd.majestic.content.entity;

import com.skd.majestic.content.block.PortalRebuild;
import com.skd.majesticcore.content.block.ReceptacleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * One block of the Portal on its way from the Receptacle to the gap it belongs in.
 *
 * <p>The rebuild used to place a block every five ticks with no visible journey, which read as a
 * machine counting rather than as the Portal taking its stone back. This is the journey: the block
 * rises out of the Receptacle's mouth, then flies a quadratic B&eacute;zier arc to its cell,
 * leaving a trail of end-rod, sparks and reversed portal behind it. It has no AI, no gravity and no
 * collision — the path is the whole of its motion — and it is never saved to disk
 * ({@link #shouldBeSaved()}): if it disappears before landing, the block entity notices on its next
 * tick and puts the block in itself, so a chunk unload can never eat the player's stone.</p>
 *
 * <p>Registered as {@code majestic:flying_stone} and drawn by {@code FlyingStoneRenderer} from the
 * {@link BlockState} it carries in its synced entity data.</p>
 */
public class FlyingStoneEntity extends Entity {

    /** The one thing it carries: which block it is going to place. */
    private static final EntityDataAccessor<BlockState> CARRIED =
            SynchedEntityData.defineId(FlyingStoneEntity.class, EntityDataSerializers.BLOCK_STATE);

    /** Length of the rise out of the Receptacle's mouth, in ticks. */
    public static final int RISE_TICKS = 20;
    /** Length of the arc to the gap, in ticks. */
    public static final int ARC_TICKS = 40;
    /** How high the block climbs out of the mouth before it starts its arc. */
    private static final double RISE_HEIGHT = 3.0;
    /** How far above the straight line the arc's control point sits. */
    private static final double ARC_LIFT = 4.0;

    /** Where the flight started: the Receptacle's mouth. */
    private Vec3 origin = Vec3.ZERO;
    /** The cell this block is going into. */
    private BlockPos target = BlockPos.ZERO;
    /** The Receptacle that sent it, so it can report the landing. */
    private BlockPos socket = BlockPos.ZERO;
    private int age;

    public FlyingStoneEntity(EntityType<? extends FlyingStoneEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /** Hands the stone its flight plan. Call before the entity is added to the level. */
    public void flyFrom(BlockPos socket, Vec3 origin, BlockPos target, BlockState carried) {
        this.socket = socket.immutable();
        this.origin = origin;
        this.target = target.immutable();
        this.setPos(origin.x, origin.y, origin.z);
        this.entityData.set(CARRIED, carried);
    }

    public BlockState getCarried() {
        return this.entityData.get(CARRIED);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CARRIED, Blocks.AIR.defaultBlockState());
    }

    /** Never written to disk: a stone that is not flying any more has already placed its block. */
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() || this.isRemoved()) {
            return;
        }
        this.age++;

        if (this.age <= RISE_TICKS) {
            double t = (double) this.age / RISE_TICKS;
            this.setPos(this.origin.x, this.origin.y + RISE_HEIGHT * t, this.origin.z);
            trail(ParticleTypes.END_ROD, 2);
            return;
        }

        int arcAge = this.age - RISE_TICKS;
        if (arcAge >= ARC_TICKS) {
            land();
            return;
        }

        Vec3 from = this.origin.add(0.0, RISE_HEIGHT, 0.0);
        Vec3 to = new Vec3(this.target.getX() + 0.5, this.target.getY() + 0.5, this.target.getZ() + 0.5);
        Vec3 control = from.lerp(to, 0.5).add(0.0, ARC_LIFT, 0.0);
        double t = (double) arcAge / ARC_TICKS;
        double u = 1.0 - t;
        this.setPos(
                u * u * from.x + 2.0 * u * t * control.x + t * t * to.x,
                u * u * from.y + 2.0 * u * t * control.y + t * t * to.y,
                u * u * from.z + 2.0 * u * t * control.z + t * t * to.z);

        ServerLevel level = (ServerLevel) this.level();
        level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY(), this.getZ(),
                2, 0.05, 0.05, 0.05, 0.0);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY(), this.getZ(),
                2, 0.05, 0.05, 0.05, 0.05);
    }

    private void trail(ParticleOptions particle, int count) {
        ((ServerLevel) this.level()).sendParticles(particle, this.getX(), this.getY(), this.getZ(),
                count, 0.03, 0.03, 0.03, 0.0);
    }

    /** The block is in its cell: burst, sound, and tell the Receptacle so it lets go of the stone. */
    private void land() {
        ServerLevel level = (ServerLevel) this.level();
        PortalRebuild.land(level, this.socket, this.target, getCarried().getBlock());
        level.playSound(null, this.target, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.8f, 1.2f);
        if (level.getBlockEntity(this.socket) instanceof ReceptacleBlockEntity receptacle) {
            receptacle.onStoneArrived(getUUID());
        }
        this.discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Nothing to read: the flight plan is set by the block entity that spawns it, and the entity
        // is never saved in the first place.
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // Nothing to save.
    }
}
