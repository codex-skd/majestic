package com.skd.majestic.content.item;

import com.skd.expeditioncore.loot.AdvancementHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * The Order of Watchers' journal, in the order a player walks through it.
 *
 * <p>This ordering is what makes a page "out of date": a page whose chapter already sits behind the
 * player's progress has nothing left to unlock, and burning it is the point rather than a message
 * saying so. The chain follows the pages themselves — {@code beginning_page} opens Chapter I,
 * {@code shrine_page} Chapter II, {@code warden_page} Chapter III, and {@code lantern_page} finishes
 * it.</p>
 *
 * <p>Chapter III exists twice: {@code chapter_3} is granted by the Warden's page and revoked by the
 * Lantern-Bearer's, which grants {@code chapter_3_locked} in its place so the title really changes.
 * Progress counts either of them as "reached Chapter III", and {@code chapter_2_1} (the search,
 * revealed alongside the locked III) as the last step.</p>
 */
public final class JournalProgress {

    private static final List<String> CHAIN = List.of(
            "journey/chapter_1",
            "journey/chapter_2",
            "journey/chapter_3",
            "journey/chapter_2_1");

    /** Chapter III under either of its two advancements. */
    private static final List<String> CHAPTER_THREE_ALIASES = List.of(
            "journey/chapter_3",
            "journey/chapter_3_locked");

    private JournalProgress() {}

    /**
     * How far along the journal this player is: the index of the furthest chapter they hold, or
     * {@code -1} if they have read none.
     */
    public static int reach(ServerPlayer player) {
        int furthest = -1;
        for (int i = 0; i < CHAIN.size(); i++) {
            if (holds(player, CHAIN.get(i))) {
                furthest = Math.max(furthest, i);
            }
        }
        return furthest;
    }

    /**
     * True when {@code chapter} is behind what the player already holds — a stale page, with nothing
     * left to reveal.
     *
     * <p>Deliberately does not fire for the chapter the player is on the cusp of: a page whose
     * chapter is exactly where they are is still worth reading (it may not be granted yet).</p>
     */
    public static boolean isOutdated(ServerPlayer player, ResourceLocation chapter) {
        int pageIndex = indexOf(chapter);
        if (pageIndex < 0) {
            return false;
        }
        return reach(player) > pageIndex;
    }

    /** Whether the player holds this chapter under any of its advancements. */
    public static boolean holds(ServerPlayer player, String chapterPath) {
        if (CHAPTER_THREE_ALIASES.contains(chapterPath)) {
            return CHAPTER_THREE_ALIASES.stream()
                    .anyMatch(alias -> AdvancementHooks.has(player, id(alias)));
        }
        return AdvancementHooks.has(player, id(chapterPath));
    }

    private static int indexOf(ResourceLocation chapter) {
        String path = chapter.getNamespace().equals("majestic")
                ? chapter.getPath()
                : "journey/" + chapter.getPath();
        return CHAIN.indexOf(path);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("majestic", path);
    }
}
