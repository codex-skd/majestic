package com.skd.majestic.magic.spell;

import com.skd.astralcore.cast.CastContext;
import com.skd.astralcore.cast.CastResult;
import com.skd.astralcore.cast.Spell;
import com.skd.astralcore.cast.SpellType;
import com.skd.astralcore.cast.targeting.Targeting;
import com.skd.astralcore.registry.AstralRegistries;
import com.skd.almanaccore.codec.SpellDefinition;
import com.skd.almanaccore.data.AlmanacContentRegistry;
import com.skd.majestic.magic.school.Schools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public class StarlightBoltSpell implements Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("majestic", "starlight_bolt");

    private static final double DEFAULT_COST = 15.0;
    private static final int DEFAULT_COOLDOWN = 20;
    private static final float DAMAGE = 4.0f;
    private static final int BLINDNESS_DURATION = 60;

    @Override
    public SpellType<?> getType() {
        return AstralRegistries.SPELL_TYPES.get(ID);
    }

    @Override
    public double getCost() {
        Optional<SpellDefinition> def = AlmanacContentRegistry.getInstance().getSpell(ID);
        return def.map(SpellDefinition::cost).orElse(DEFAULT_COST);
    }

    @Override
    public int getCooldownTicks() {
        Optional<SpellDefinition> def = AlmanacContentRegistry.getInstance().getSpell(ID);
        return def.map(SpellDefinition::cooldown).orElse(DEFAULT_COOLDOWN);
    }

    @Override
    public ResourceLocation getSchool() {
        Optional<SpellDefinition> def = AlmanacContentRegistry.getInstance().getSpell(ID);
        return def.map(SpellDefinition::school).orElse(Schools.STARLIGHT);
    }

    @Override
    public CastResult cast(CastContext context) {
        LivingEntity caster = context.caster();
        HitResult hitResult = Targeting.raycast(caster, 20.0);

        if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
            return CastResult.failure("No target");
        }

        Entity hitEntity = ((EntityHitResult) hitResult).getEntity();
        if (!(hitEntity instanceof LivingEntity target)) {
            return CastResult.failure("No target");
        }

        target.hurt(caster.damageSources().indirectMagic(caster, caster), DAMAGE);
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_DURATION, 0, false, true));

        return CastResult.success();
    }
}
