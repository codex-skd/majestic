package com.skd.majestic.magic;

import net.minecraft.network.chat.Component;

/**
 * Failure reasons travel from a {@code Spell} to the player as a plain {@code String} inside
 * {@code CastResult.failure(String)}. Majestic's own spells pass a translation key; spells that
 * return free-form text still render, but untranslated.
 */
public final class CastMessages {

    public static final String SPELL_NO_TARGET = "message.majestic.spell.no_target";
    public static final String SPELL_NO_EFFECT = "message.majestic.spell.no_effect";

    private CastMessages() {}

    /** Renders a {@code CastResult} reason, treating anything key-shaped as translatable. */
    public static Component reason(String reason) {
        if (reason == null || reason.isEmpty()) {
            return Component.empty();
        }
        return looksLikeKey(reason) ? Component.translatable(reason) : Component.literal(reason);
    }

    /** A translation key looks like {@code namespace:path} — a colon, no whitespace. */
    private static boolean looksLikeKey(String text) {
        int colon = text.indexOf(':');
        return colon > 0 && colon < text.length() - 1
                && text.indexOf(' ') < 0
                && text.indexOf('\n') < 0;
    }
}
