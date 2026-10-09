package com.skd.majestic.content.block;

import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.content.worldgen.StructureKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Optional;

/**
 * The Portal taking its own stone back.
 *
 * <p>Where each block belongs is {@link PortalGaps}' business, and this class is the moment: choose
 * the cell, then put the block in it. The block entity does the choosing of <em>when</em> — the
 * column of light, the flight, the pause between stones — because only it holds the queue, and the
 * flying stone calls {@link #land} when it arrives. When the last cell closes, the first phase of
 * Act I ends — achievement, particles, and the page that says what is on the other side.</p>
 *
 * <p>A block the Portal does not want is never accepted and never voided. The receptacle keeps it, so a
 * player who miscounts loses nothing but time.</p>
 */
public final class PortalRebuild {

    private static final ResourceLocation PHASE_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/portal_phase_one");

    /** How close a player must be to be the one credited with closing the Portal. */
    private static final double CREDIT_RADIUS = 32.0;

    private PortalRebuild() {}

    /**
     * The cell a block belongs in, and nothing else.
     *
     * <p>Split out of {@link #placeOne} so the flight knows where it is going before the block
     * moves: the flying stone needs the target while the block is still in the air, and only the
     * landing may put it in the world.</p>
     *
     * <p>The cell is chosen by the gap's own block, not by what the world happens to hold there:
     * every pending gap is air by definition, so matching against the block state can never work.</p>
     *
     * @return the first still-open gap that wants this block, or empty when the Portal refuses it
     */
    public static Optional<PortalGaps.PendingGap> chooseGap(ServerLevel level, BlockPos from, ItemStack stack) {
        Optional<StructureStart> portal = findPortal(level, from);
        if (portal.isEmpty()) {
            return Optional.empty();
        }
        String stage = stageOfSocket(level, from);
        for (PortalGaps.PendingGap pending : PortalGaps.pendingGaps(portal.get(), level, stage)) {
            if (stack.is(pending.gap().block().asItem())) {
                return Optional.of(pending);
            }
        }
        return Optional.empty();
    }

    /**
     * The landing: the block goes into its cell, the cell bursts, and the Portal is checked while
     * whoever closed it is still standing there.
     *
     * @param from   a position inside the Portal (the socket, or the gap itself), used to find it
     * @param target the cell the block belongs in
     * @param block  the block that cell has been waiting for
     */
    public static void land(ServerLevel level, BlockPos from, BlockPos target, Block block) {
        level.setBlockAndUpdate(target, block.defaultBlockState());
        level.sendParticles(ParticleTypes.END_ROD, target.getX() + 0.5, target.getY() + 0.5,
                target.getZ() + 0.5, 12, 0.25, 0.25, 0.25, 0.02);

        // Check the Portal now, while whoever closed the last cell is standing there.
        Optional<StructureStart> portal = findPortal(level, from);
        String stage = PortalGaps.stageOf(block);
        if (portal.isPresent() && PortalGaps.pendingPositions(portal.get(), level, stage).isEmpty()) {
            if (PortalGaps.STAGE_DEPTHS.equals(stage)) {
                closeDepths(level, target);
            } else {
                close(level, target);
            }
        }
    }

    /**
     * Puts one block into its cell, there and then. The fallback for a flight that never arrived:
     * same cell, same burst, no journey.
     *
     * @return true when the block found its home; false when nothing accepted it, and the caller must
     *         keep holding it
     */
    public static boolean placeOne(ServerLevel level, BlockPos from, ItemStack stack) {
        Optional<PortalGaps.PendingGap> pending = chooseGap(level, from, stack);
        if (pending.isEmpty()) {
            return false;
        }
        land(level, from, pending.get().world(), pending.get().gap().block());
        return true;
    }

    /** Which stage the socket at {@code pos} serves: the Receptacle of the Depths raises the inner arches. */
    public static String stageOfSocket(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(com.skd.majesticcore.content.OrderFurniture.RECEPTACLE_DEPTHS.get())
                ? PortalGaps.STAGE_DEPTHS : PortalGaps.STAGE_OUTER;
    }

    /** The last inner-arch cell is set: the arches stand, and whoever finished them hears it. */
    private static void closeDepths(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                120, 3.0, 3.0, 3.0, 0.3);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                120, 3.0, 3.0, 3.0, 0.1);
        ServerPlayer player = playerOf(level, pos);
        if (player != null) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.majestic.portal.arches_complete"), false);
        }
    }

    /** The last cell is shut: the first phase of Act I is over. */
    private static void close(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                200, 2.0, 2.0, 2.0, 0.8);

        ServerPlayer player = playerOf(level, pos);
        if (player == null) {
            return;
        }
        // grantAll rather than grant: this advancement's criterion is named "portal_whole", not the
        // "trigger" that AdvancementHooks.grant assumes, so grant would quietly award nothing.
        AdvancementHooks.grantAll(player, PHASE_ADVANCEMENT);
    }

    /**
     * The receptacle has given up everything it held and takes itself apart, dropping the page that
     * points at the island where it stood. The page is dropped rather than handed over, so the player
     * has to pick it up: it is the Receptacle's last act, not a reward screen.
     */
    public static void evaporate(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                24, 0.4, 0.4, 0.4, 0.03);
        // Only the Portal's own receptacle leaves the page that points at the island; the Receptacle of the
        // Depths just goes, its work is the inner arches.
        if (PortalGaps.STAGE_OUTER.equals(stageOfSocket(level, pos))) {
            Block.popResource(level, pos, new ItemStack(MajesticItems.PORTAL_PHASE_TWO_PAGE.get()));
        }
        level.removeBlock(pos, false);
    }

    private static Optional<StructureStart> findPortal(ServerLevel level, BlockPos pos) {
        return StructureKeys.findStartAt(level, pos, StructureKeys.ARCANE_PORTAL);
    }

    private static ServerPlayer playerOf(ServerLevel level, BlockPos pos) {
        Player nearest = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                CREDIT_RADIUS, false);
        return nearest instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }
}