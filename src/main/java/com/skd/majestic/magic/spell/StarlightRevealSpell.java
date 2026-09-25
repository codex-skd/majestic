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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

import java.util.List;
import java.util.Optional;

public class StarlightRevealSpell implements Spell {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("majestic", "starlight_reveal");

    private static final double DEFAULT_COST = 20.0;
    private static final int DEFAULT_COOLDOWN = 100;
    private static final double RADIUS = 12.0;
    private static final int GLOWING_DURATION = 200;

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
        List<LivingEntity> nearby = Targeting.collectInSphere(context.level(), caster.position(), RADIUS, caster);

        for (LivingEntity entity : nearby) {
            if (entity instanceof Monster) {
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOWING_DURATION, 0, false, true));
            }
        }

        return CastResult.success();
    }
}
