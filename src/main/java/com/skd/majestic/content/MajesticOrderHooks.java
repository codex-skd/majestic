package com.skd.majestic.content;

import com.skd.astralcore.ritual.RitualType;
import com.skd.vellumli.api.VellumliAPI;
import com.skd.vellumli.common.item.VellumliDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.content.block.PortalGaps;
import com.skd.majestic.content.block.PortalRebuild;
import com.skd.majestic.content.entity.FlyingStoneEntity;
import com.skd.majestic.content.entity.MajesticEntities;
import com.skd.majestic.content.entity.boss.WardenOfTheGate;
import com.skd.majestic.content.event.ObservatoryArenas;
import com.skd.majestic.content.item.JournalPageItem;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.content.worldgen.StructureKeys;
import com.skd.majestic.magic.ritual.MajesticRituals;
import com.skd.majesticcore.api.OrderHooks;
import com.skd.majesticcore.content.OrderFurniture;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Majestic's side of the Order's blocks, which live in majestic_core.
 *
 * <p>The altars and the receptacle are placeable blocks in the library; what they do is the story:
 * the astral altar wakes the Warden of the Gate, the construction altar opens the next chapter of
 * the Almanac, and the receptacle rebuilds the Arcane Portal. Everything those blocks used to do
 * inline is here, installed into {@link OrderHooks} while the mod loads.</p>
 */
public final class MajesticOrderHooks {

    private static final ResourceLocation CHAPTER_3_PHASE2 =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/chapter_3_phase2");
    private static final ResourceLocation CHAPTER_3_PHASE2_1 =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/chapter_3_phase2_1");

    private static final ResourceLocation CHAPTER_3_PHASE2_2 =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/chapter_3_phase2_2");
    private static final ResourceLocation CHAPTER_3_PHASE2_3 =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/chapter_3_phase2_3");
    private static final ResourceLocation CHAPTER_3_PHASE2_4 =
            ResourceLocation.fromNamespaceAndPath("majestic", "journey/chapter_3_phase2_4");

    /** The Bible of the Beasts lives in majestic_terrain; it is matched by id so there is no compile dependency. */
    private static final ResourceLocation BESTIARY_BIBLE =
            ResourceLocation.fromNamespaceAndPath("majestic_terrain", "bestiary_bible");

    private static final double SUMMON_MIN_DISTANCE = 5.0;
    private static final double SUMMON_MAX_DISTANCE = 6.0;
    private static final int SUMMON_DIRECTIONS = 8;
    private static final int SUMMON_MAX_LIFT = 3;

    private MajesticOrderHooks() {}

    public static void install() {
        OrderHooks.setAstralAltar(new AstralAltar());
        OrderHooks.setVaultAltar(new VaultAltar());
        OrderHooks.setConstructionAltar(new ConstructionAltar());
        OrderHooks.setRebuild(new Rebuild());
    }

    // ------------------------------------------------------------------ astral altar

    private static final class AstralAltar implements OrderHooks.AstralAltar {
        @Override
        public ItemInteractionResult useItem(ItemStack stack, Level level, BlockPos pos, Player player,
                                             InteractionHand hand) {
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

        @Override
        public @Nullable RitualType<?> ritual() {
            return MajesticRituals.ENGRAVE_SPELL.get();
        }

        /**
         * Spawns the Warden five blocks from the altar (first spot that fits, then six, then a fixed
         * fallback), sized to the arena bounding box exactly as the old auto-spawn did.
         */
        private static void summonWarden(ServerLevel level, BlockPos altarPos, BoundingBox box,
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
    }

    // ------------------------------------------------------------------ vault altar

    private static final class VaultAltar implements OrderHooks.VaultAltar {
        @Override
        public boolean isStone(ItemStack stack) {
            return stack.is(MajesticItems.ILLUMINATION_STONE.get());
        }

        @Override
        public ItemStack newStone() {
            return new ItemStack(MajesticItems.ILLUMINATION_STONE.get());
        }
    }

    // ------------------------------------------------------------------ construction altar

    private static final class ConstructionAltar implements OrderHooks.ConstructionAltar {
        @Override
        public boolean isBook(ItemStack stack) {
            return JournalPageItem.isAlmanac(stack);
        }

        @Override
        public boolean isSecondBook(ItemStack stack) {
            return !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(BESTIARY_BIBLE);
        }

        /**
         * Opens the book standing on the altar for the player, the way a Vellumli book item does when it
         * is used: the book id travels in the stack's Vellumli component.
         */
        @Override
        public void readBook(Player player, ItemStack book) {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            ResourceLocation bookId = book.get(VellumliDataComponents.BOOK);
            if (bookId == null) {
                serverPlayer.displayClientMessage(Component.translatable("message.majestic.altar.book_unreadable"), true);
                return;
            }
            VellumliAPI.get().openBookGUI(serverPlayer, bookId);
        }

        /**
         * The altar only advances the story where the story puts it. If the altar stands inside the
         * Portal and the reader already holds {@code journey/chapter_3_phase2}, setting the Almanac grants
         * {@code journey/chapter_3_phase2_1} (silently) and {@code journey/chapter_3_phase2_2}, which is
         * the one announced. Anywhere else the book simply rests there. Idempotent.
         */
        @Override
        public void onBookPlaced(Level level, BlockPos pos, Player player) {
            if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (StructureKeys.findStartAt(serverLevel, pos, StructureKeys.ARCANE_PORTAL).isEmpty()) {
                return;
            }
            if (!AdvancementHooks.has(serverPlayer, CHAPTER_3_PHASE2)) {
                return;
            }
            AdvancementHooks.grant(serverPlayer, CHAPTER_3_PHASE2_1);
            reveal(serverPlayer, CHAPTER_3_PHASE2_2, "book.majestic.almanac.chapter_3_phase2_2");
        }

        /**
         * The Bible of the Beasts has been set on the right-hand place: the two deep dungeons are revealed.
         * Only inside the Portal and only for a reader who already holds "The Bible?". Idempotent.
         */
        @Override
        public void onSecondBookPlaced(Level level, BlockPos pos, Player player) {
            if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
                return;
            }
            if (StructureKeys.findStartAt(serverLevel, pos, StructureKeys.ARCANE_PORTAL).isEmpty()
                    || !AdvancementHooks.has(serverPlayer, CHAPTER_3_PHASE2_2)) {
                return;
            }
            boolean firstTime = !AdvancementHooks.has(serverPlayer, CHAPTER_3_PHASE2_3);
            reveal(serverPlayer, CHAPTER_3_PHASE2_3, "book.majestic.almanac.chapter_3_phase2_3");
            reveal(serverPlayer, CHAPTER_3_PHASE2_4, "book.majestic.almanac.chapter_3_phase2_4");
            if (firstTime) {
                // The lectern hands over the socket that comes back for the inner arches.
                ItemStack socket = new ItemStack(OrderFurniture.RECEPTACLE_DEPTHS_ITEM.get());
                if (!serverPlayer.getInventory().add(socket)) {
                    serverPlayer.drop(socket, false);
                }
                serverPlayer.displayClientMessage(
                        Component.translatable("message.majestic.altar.depths_receptacle"), false);
            }
        }

        /** Grants a chapter and announces it exactly like a page would; nothing happens if it is already held. */
        private static void reveal(ServerPlayer player, ResourceLocation chapter, String titleKey) {
            if (AdvancementHooks.has(player, chapter)) {
                return;
            }
            AdvancementHooks.grant(player, chapter);
            player.displayClientMessage(Component.translatable("message.majestic.page.new_chapter",
                    Component.translatable(titleKey)), false);
        }
    }

    // ------------------------------------------------------------------ receptacle

    private static final class Rebuild implements OrderHooks.Rebuild {
        private static @Nullable StructureStart portalAt(ServerLevel level, BlockPos pos) {
            return StructureKeys.findStartAt(level, pos, StructureKeys.ARCANE_PORTAL).orElse(null);
        }

        @Override
        public boolean hasTarget(ServerLevel level, BlockPos receptacle) {
            return portalAt(level, receptacle) != null;
        }

        @Override
        public int capacityFor(Block block) {
            return PortalGaps.countFor(block);
        }

        @Override
        public int pendingCount(ServerLevel level, BlockPos receptacle, Block block) {
            StructureStart portal = portalAt(level, receptacle);
            if (portal == null) {
                return 0;
            }
            int count = 0;
            for (PortalGaps.Gap gap : PortalGaps.pending(portal, level, PortalRebuild.stageOfSocket(level, receptacle))) {
                if (gap.block() == block) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public boolean isComplete(ServerLevel level, BlockPos receptacle) {
            StructureStart portal = portalAt(level, receptacle);
            return portal != null
                    && PortalGaps.pendingPositions(portal, level, PortalRebuild.stageOfSocket(level, receptacle)).isEmpty();
        }

        @Override
        public Optional<OrderHooks.Target> chooseTarget(ServerLevel level, BlockPos receptacle, ItemStack stone) {
            return PortalRebuild.chooseGap(level, receptacle, stone)
                    .map(gap -> new OrderHooks.Target(gap.world(), gap.gap().block().defaultBlockState()));
        }

        @Override
        public boolean placeDirectly(ServerLevel level, BlockPos receptacle, ItemStack stone) {
            return PortalRebuild.placeOne(level, receptacle, stone);
        }

        @Override
        public @Nullable UUID launch(ServerLevel level, BlockPos receptacle, Vec3 mouth,
                                     OrderHooks.Target target) {
            FlyingStoneEntity stone = MajesticEntities.FLYING_STONE.get().create(level);
            if (stone == null) {
                return null;
            }
            stone.flyFrom(receptacle, mouth, target.world(), target.state());
            return level.addFreshEntity(stone) ? stone.getUUID() : null;
        }

        @Override
        public void finish(ServerLevel level, BlockPos receptacle) {
            PortalRebuild.evaporate(level, receptacle);
        }
    }
}
