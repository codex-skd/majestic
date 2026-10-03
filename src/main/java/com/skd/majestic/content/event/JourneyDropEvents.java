package com.skd.majestic.content.event;

import com.skd.expeditioncore.loot.AdvancementHooks;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majesticbestiary.registry.BestiaryEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

/**
 * Guarantees the Page of the Beginning for newcomers: the first player to kill one of the
 * bestiary's Undead before completing Chapter I receives the page, unless they already carry one.
 *
 * <p>Replaces the old per-entity death-loot hook now that the Undead are the night mob that
 * carries the Order's journal page.
 */
public final class JourneyDropEvents {

    private static final ResourceLocation CHAPTER_1 =
            ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_1");

    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (entity.getType() != BestiaryEntities.UNDEAD.get()) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (AdvancementHooks.has(player, CHAPTER_1)) {
            return;
        }

        ItemStack page = new ItemStack(MajesticItems.BEGINNING_PAGE.get());
        if (player.getInventory().contains(page)) {
            return;
        }
        event.getDrops().add(new ItemEntity(entity.level(),
                entity.getX(), entity.getY() + 0.25, entity.getZ(), page));
    }

    private JourneyDropEvents() {}
}
