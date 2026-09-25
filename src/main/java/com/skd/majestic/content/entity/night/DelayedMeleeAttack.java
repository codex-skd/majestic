package com.skd.majestic.content.entity.night;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Server-side state shared by the melee night mobs. Their attack animation deals its damage
 * a few ticks after the swing starts, so scheduling the hit here keeps the hurt event in
 * sync with the animation's impact frame instead of landing on the same tick as the trigger.
 */
final class DelayedMeleeAttack {

    private @Nullable LivingEntity target;
    private int ticks;

    boolean schedule(LivingEntity newTarget, int impactTicks) {
        if (this.isPending()) {
            return false;
        }
        this.target = newTarget;
        this.ticks = impactTicks;
        return true;
    }

    boolean isPending() {
        return this.target != null;
    }

    void cancel() {
        this.target = null;
        this.ticks = 0;
    }

    /**
     * Advances the countdown; returns the stored target once the impact tick is reached
     * (clearing the pending state), or {@code null} while still waiting.
     */
    @Nullable
    LivingEntity tick() {
        if (this.target == null) {
            return null;
        }
        if (--this.ticks > 0) {
            return null;
        }
        LivingEntity impacted = this.target;
        this.cancel();
        return impacted;
    }
}
