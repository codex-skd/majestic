package com.skd.majestic.content.block;

import com.mojang.serialization.MapCodec;
import com.skd.majestic.content.entity.MajesticEntities;
import com.skd.majestic.content.entity.boss.WardenOfTheGate;
import com.skd.majestic.content.event.ObservatoryArenas;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.magic.ritual.MajesticRituals;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AstralAltarBlock extends BaseEntityBlock {

    public static final MapCodec<AstralAltarBlock> CODEC = simpleCodec(AstralAltarBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0),
            Block.box(4.0, 3.0, 4.0, 12.0, 13.0, 12.0),
            Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0));

    private static final double SUMMON_MIN_DISTANCE = 5.0;
    private static final double SUMMON_MAX_DISTANCE = 6.0;
    private static final int SUMMON_DIRECTIONS = 8;
    private static final int SUMMON_MAX_LIFT = 3;

    public AstralAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        // BaseEntityBlock defaults to INVISIBLE; render the altar model like a normal block.
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AstralAltarBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(MajesticItems.STAR_FRAGMENT.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // The client cannot resolve structure pieces; it predicts success and lets the server decide.
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        Optional<BoundingBox> arenaBox = ObservatoryArenas.findArenaBox(serverLevel, pos);
        if (arenaBox.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BoundingBox box = arenaBox.get();
        boolean bossActive = serverLevel.getEntitiesOfClass(WardenOfTheGate.class, AABB.of(box))
                .stream()
                .anyMatch(Entity::isAlive);
        if (bossActive) {
            player.displayClientMessage(Component.translatable("majestic.altar.boss_active"), true);
            return ItemInteractionResult.CONSUME;
        }

        summonWarden(serverLevel, pos, box, player, stack);
        return ItemInteractionResult.sidedSuccess(false);
    }

    /**
     * Spawns the Warden five blocks from the altar (first spot that fits, then six, then a fixed
     * fallback), sized to the arena bounding box exactly as the old auto-spawn did.
     */
    private void summonWarden(ServerLevel level, BlockPos altarPos, BoundingBox box,
                              Player player, ItemStack stack) {
        WardenOfTheGate boss = MajesticEntities.WARDEN_OF_THE_GATE.get().create(level);
        if (boss == null) {
            return;
        }

        boolean placed = false;
        for (double distance = SUMMON_MIN_DISTANCE; distance <= SUMMON_MAX_DISTANCE && !placed; distance++) {
            for (int i = 0; i < SUMMON_DIRECTIONS && !placed; i++) {
                double angle = Math.PI * 2.0 * i / SUMMON_DIRECTIONS;
                int x = altarPos.getX() + (int) Math.round(Math.cos(angle) * distance);
                int z = altarPos.getZ() + (int) Math.round(Math.sin(angle) * distance);
                for (int lift = 0; lift <= SUMMON_MAX_LIFT && !placed; lift++) {
                    boss.moveTo(x + 0.5, altarPos.getY() + lift, z + 0.5, 0.0f, 0.0f);
                    placed = level.noCollision(boss);
                }
            }
        }
        if (!placed) {
            BlockPos fallback = altarPos.offset(0, 1, (int) SUMMON_MIN_DISTANCE);
            boss.moveTo(fallback.getX() + 0.5, fallback.getY(), fallback.getZ() + 0.5, 0.0f, 0.0f);
        }

        int centerX = (box.minX() + box.maxX()) / 2;
        int centerZ = (box.minZ() + box.maxZ()) / 2;
        boss.setArena(new BlockPos(centerX, box.minY() + 1, centerZ),
                box.getXSpan() / 2, box.getYSpan(), box.getZSpan() / 2);
        boss.setSummonAltar(altarPos);
        boss.finalizeSpawn(level, level.getCurrentDifficultyAt(boss.blockPosition()), MobSpawnType.EVENT, null);
        level.addFreshEntity(boss);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        level.playSound(null, altarPos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 0.8f, 1.0f);
        double px = altarPos.getX() + 0.5;
        double py = altarPos.getY() + 1.5;
        double pz = altarPos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, px, py, pz, 60, 0.6, 1.0, 0.6, 0.1);
        level.sendParticles(ParticleTypes.END_ROD, px, py, pz, 40, 0.6, 1.0, 0.6, 0.05);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AstralAltarBlockEntity altar) {
                altar.startRitual(MajesticRituals.ENGRAVE_SPELL.get(), (ServerPlayer) player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MajesticBlocks.ASTRAL_ALTAR_BE.get(),
                (lvl, pos, st, be) -> ((AstralAltarBlockEntity) be).tick());
    }
}
