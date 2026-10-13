package com.skd.majestic.magic.spell;

import com.skd.astralcore.cast.CastContext;
import com.skd.astralcore.cast.CastResult;
import com.skd.astralcore.cast.Spell;
import com.skd.astralcore.cast.SpellType;
import com.skd.astralcore.registry.AstralRegistries;
import com.skd.almanaccore.codec.SpellDefinition;
import com.skd.almanaccore.data.AlmanacContentRegistry;
import com.skd.majestic.magic.school.Schools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class StarlightSurgeSpell implements Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("majestic", "starlight_surge");

    private static final double DEFAULT_COST = 10.0;
    private static final int DEFAULT_COOLDOWN = 300;

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
        caster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, true));
        return CastResult.success();
    }
}
