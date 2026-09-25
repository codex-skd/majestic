package com.skd.majestic.magic.ritual;

import com.skd.astralcore.essence.EssenceApi;
import com.skd.astralcore.research.ResearchApi;
import com.skd.astralcore.registry.AstralRegistries;
import com.skd.astralcore.ritual.Ritual;
import com.skd.astralcore.ritual.RitualContext;
import com.skd.astralcore.ritual.RitualResult;
import com.skd.astralcore.ritual.Multiblock;
import com.skd.astralcore.ritual.RitualType;
import com.skd.almanaccore.codec.RitualDefinition;
import com.skd.almanaccore.data.AlmanacContentRegistry;
import com.skd.majestic.content.component.MajesticDataComponents;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.magic.spell.MajesticSpells;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public class EngraveSpellRitual implements Ritual {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("majestic", "engrave_spell");

    private static final double DEFAULT_ESSENCE_COST = 20.0;

    private static final TagKey<Block> PILLAR_TAG = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("majestic", "altar_pillar"));

    private static final Multiblock MULTIBLOCK = new Multiblock(Map.of(
            new net.minecraft.core.BlockPos(2, 0, 0), PILLAR_TAG,
            new net.minecraft.core.BlockPos(-2, 0, 0), PILLAR_TAG,
            new net.minecraft.core.BlockPos(0, 0, 2), PILLAR_TAG,
            new net.minecraft.core.BlockPos(0, 0, -2), PILLAR_TAG
    ));

    @Override
    public RitualType<?> getType() {
        return AstralRegistries.RITUAL_TYPES.get(ID);
    }

    @Override
    public Multiblock getMultiblock() {
        return MULTIBLOCK;
    }

    @Override
    public double getEssenceCost() {
        return AlmanacContentRegistry.getInstance().getRitual(ID)
                .map(RitualDefinition::essence)
                .orElse(DEFAULT_ESSENCE_COST);
    }

    @Override
    public int getDurationTicks() {
        return 60;
    }

    @Override
    public RitualResult run(RitualContext context) {
        ServerPlayer player = context.player();
        if (player == null) {
            return RitualResult.failure("No player");
        }

        // Check focus in main hand BEFORE consuming anything
        ItemStack focusStack = player.getMainHandItem();
        if (!focusStack.is(MajesticItems.STARLIGHT_FOCUS.get())) {
            return RitualResult.failure("Hold your focus in your main hand");
        }

        // Determine target spell from sigil in off hand
        ItemStack offHand = player.getOffhandItem();
        ResourceLocation spellId = resolveSpellId(offHand);
        if (spellId == null) {
            return RitualResult.failure("Hold a spell sigil in your off-hand");
        }

        // Check research
        if (!ResearchApi.has(player, ResourceLocation.fromNamespaceAndPath("majestic", "first_light"))) {
            return RitualResult.failure("The first light has not been researched");
        }

        // Check blank page
        if (player.getInventory().countItem(MajesticItems.BLANK_PAGE.get()) <= 0) {
            return RitualResult.failure("Missing a blank page");
        }

        // Consume essence
        if (!EssenceApi.consume(player, getEssenceCost())) {
            return RitualResult.insufficientEssence();
        }

        // Consume one blank page and one sigil
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).is(MajesticItems.BLANK_PAGE.get())) {
                inv.removeItem(i, 1);
                break;
            }
        }
        offHand.shrink(1);

        // Engrave the spell onto the focus
        focusStack.set(MajesticDataComponents.RECORDED_SPELL.get(), spellId);

        return RitualResult.success(List.of());
    }

    private static ResourceLocation resolveSpellId(ItemStack stack) {
        if (stack.is(MajesticItems.STARLIGHT_BOLT_SIGIL.get())) {
            return MajesticSpells.STARLIGHT_BOLT.getId();
        } else if (stack.is(MajesticItems.STARLIGHT_WARD_SIGIL.get())) {
            return MajesticSpells.STARLIGHT_WARD.getId();
        } else if (stack.is(MajesticItems.STARLIGHT_REVEAL_SIGIL.get())) {
            return MajesticSpells.STARLIGHT_REVEAL.getId();
        } else if (stack.is(MajesticItems.STARLIGHT_SURGE_SIGIL.get())) {
            return MajesticSpells.STARLIGHT_SURGE.getId();
        }
        return null;
    }
}
