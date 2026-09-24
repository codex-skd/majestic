package com.skd.majestic.content.entity.boss;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Level-persisted record of which Observatory arenas have already spawned their Warden.
 * Keyed by the arena piece's bounding-box minimum corner ({@link net.minecraft.core.BlockPos#asLong}),
 * so a structure only ever hosts a single boss even across restarts.
 */
public class MajesticArenaData extends SavedData {

    private static final String SPAWNED_KEY = "Spawned";

    private final Set<Long> spawnedArenas = new HashSet<>();

    public MajesticArenaData() {}

    public boolean isSpawned(long arenaKey) {
        return this.spawnedArenas.contains(arenaKey);
    }

    public void markSpawned(long arenaKey) {
        this.spawnedArenas.add(arenaKey);
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLongArray(SPAWNED_KEY, this.spawnedArenas.stream().mapToLong(Long::longValue).toArray());
        return tag;
    }

    public static MajesticArenaData load(CompoundTag tag, HolderLookup.Provider registries) {
        MajesticArenaData data = new MajesticArenaData();
        for (long arenaKey : tag.getLongArray(SPAWNED_KEY)) {
            data.spawnedArenas.add(arenaKey);
        }
        return data;
    }

    public static SavedData.Factory<MajesticArenaData> factory() {
        return new SavedData.Factory<>(MajesticArenaData::new, MajesticArenaData::load);
    }
}
