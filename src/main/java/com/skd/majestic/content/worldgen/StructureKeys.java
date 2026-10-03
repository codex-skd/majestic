package com.skd.majestic.content.worldgen;

import com.skd.expeditioncore.protection.StructureProtection;
import com.skd.majestic.Majestic;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * Every structure this mod generates, in one place.
 *
 * <p>Adding a structure is one entry in {@link #ALL}: its id, whether it starts sealed and with what
 * margin. Nothing else has to be touched — {@link #registerProtection()} is the only caller of
 * expedition_core's protection registry, so {@code Majestic} no longer grows a line per structure and
 * no key is defined twice.</p>
 *
 * <p>The margin is the slack added around each piece's bounding box when testing containment. A
 * negative margin means the structure is deliberately left unprotected: the Portal and the Vault are
 * meant to be dug into, and sealing them would defeat the point of them.</p>
 */
public final class StructureKeys {

    /**
     * One generated structure and how it starts out.
     *
     * @param key              the structure id
     * @param protectionMargin extra blocks around each piece when testing containment; negative to
     *                         leave the structure unsealed
     */
    public record Entry(ResourceKey<Structure> key, int protectionMargin) {
        public boolean isProtected() {
            return this.protectionMargin >= 0;
        }
    }

    public static final ResourceKey<Structure> FALLEN_SHRINE = key("fallen_shrine");
    public static final ResourceKey<Structure> OBSERVATORY = key("observatory");
    public static final ResourceKey<Structure> ARCANE_PORTAL = key("arcane_portal");
    public static final ResourceKey<Structure> OBSERVERS_VAULT = key("observers_vault");

    /** Every structure this mod generates. The single place to add a new one. */
    public static final List<Entry> ALL = List.of(
            new Entry(FALLEN_SHRINE, 1),
            new Entry(OBSERVATORY, 1),
            // Dug into on purpose: the two gaps in the Portal are meant to be filled by hand, and the
            // Vault's whole point is that you can excavate it and take the stone.
            new Entry(ARCANE_PORTAL, -1),
            new Entry(OBSERVERS_VAULT, -1));

    private StructureKeys() {}

    /** Declares every sealed structure to expedition_core. Called once, from {@code Majestic}. */
    public static void registerProtection() {
        for (Entry entry : ALL) {
            if (entry.isProtected()) {
                StructureProtection.protect(entry.key(), entry.protectionMargin());
            }
        }
    }

    /**
     * Finds the generated instance of {@code type} containing {@code pos}.
     *
     * <p>Shared by everything that has to know "which structure am I standing in": the Warden's arena
     * lookup, the shrine unseal and the Lantern-Bearer's trigger all need this, and each used to
     * carry its own copy of the same registry lookup and bounds walk.</p>
     *
     * @return the instance's start, or empty if the position is not inside one
     */
    public static Optional<StructureStart> findStartAt(ServerLevel level, BlockPos pos, ResourceKey<Structure> type) {
        Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(type);
        if (structure == null) {
            return Optional.empty();
        }
        StructureStart start = level.structureManager().getStructureWithPieceAt(pos, structure);
        if (start == null || !start.isValid()) {
            return Optional.empty();
        }
        return Optional.of(start);
    }

    /**
     * Whether {@code pos} falls inside any piece of {@code start}, with {@code margin} blocks of slack
     * around each piece's bounding box.
     */
    public static boolean isInsideAnyPiece(StructureStart start, BlockPos pos, int margin) {
        for (StructurePiece piece : start.getPieces()) {
            if (piece.getBoundingBox().inflatedBy(margin).isInside(pos)) {
                return true;
            }
        }
        return false;
    }

    /** The combined bounding box of every piece, used to keep spawned entities inside the structure. */
    public static BoundingBox boundsOf(StructureStart start) {
        return start.getBoundingBox();
    }

    private static ResourceKey<Structure> key(String path) {
        return ResourceKey.create(Registries.STRUCTURE,
                ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, path));
    }
}