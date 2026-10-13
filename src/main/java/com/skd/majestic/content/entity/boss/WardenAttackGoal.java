package com.skd.majestic.content.entity.boss;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

/**
 * Chases the Warden's target like {@code MeleeAttackGoal} does, but instead of a single
 * melee swing it asks the boss to pick one of its varied attacks (sweep, slam, thrust)
 * whenever it is able to attack. All attack execution, timing and damage live in
 * {@link WardenOfTheGate}; this goal only handles movement and range checks.
 */
public class WardenAttackGoal extends Goal {

    private static final int PATH_RECALCULATE_MIN = 4;
    private static final int PATH_RECALCULATE_RANDOM = 7;

    private final WardenOfTheGate mob;
    private final double speedModifier;
    private int ticksUntilNextPathRecalculation;

    public WardenAttackGoal(WardenOfTheGate mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return isValidTarget(this.mob.getTarget());
    }

    @Override
    public boolean canContinueToUse() {
        return isValidTarget(this.mob.getTarget());
    }

    private boolean isValidTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof Player player) {
            return !player.isCreative() && !player.isSpectator();
        }
        return true;
    }

    @Override
    public void start() {
        this.ticksUntilNextPathRecalculation = 0;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }

        // While attacking, winding up, charging, stunned or frozen the boss drives its own
        // movement (and facing) and the goal must not path or look around.
        if (this.mob.isCombatBusy()) {
            this.mob.getNavigation().stop();
            return;
        }

        this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);

        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
        if (this.ticksUntilNextPathRecalculation <= 0) {
            this.ticksUntilNextPathRecalculation =
                    PATH_RECALCULATE_MIN + this.mob.getRandom().nextInt(PATH_RECALCULATE_RANDOM);
            this.mob.getNavigation().moveTo(target, this.speedModifier);
        }

        this.mob.tryPerformAttack(target);
    }
}
