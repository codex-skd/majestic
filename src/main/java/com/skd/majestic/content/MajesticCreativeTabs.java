package com.skd.majestic.content;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.item.MajesticItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Majestic's creative tabs (design J3: magic / world / relics). The relics tab is added once
 * relics exist. The guide book joins the magic tab through its own {@code book.json}
 * ({@code "creative_tab": "majestic:magic"}), so it is not listed here.
 */
public final class MajesticCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Majestic.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGIC = TABS.register("magic", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.majestic.magic"))
                    .icon(() -> MajesticItems.STARLIGHT_FOCUS.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(MajesticItems.STARLIGHT_FOCUS.get());
                        output.accept(MajesticItems.BLANK_PAGE.get());
                        output.accept(MajesticItems.BEGINNING_PAGE.get());
                        output.accept(MajesticItems.SHRINE_PAGE.get());
                        output.accept(MajesticItems.WARDEN_PAGE.get());
                        output.accept(MajesticItems.LANTERN_PAGE.get());
                        // The two Portal pages sit with the other pages: they are what closes phase
                        // one and what opens phase two, and leaving them out made them unobtainable
                        // outside of the Receptacle and the sky island.
                        output.accept(MajesticItems.PORTAL_PHASE_PAGE.get());
                        output.accept(MajesticItems.PORTAL_PHASE_TWO_PAGE.get());
                        output.accept(MajesticItems.GOBLIN_CLUB.get());
                        output.accept(MajesticItems.DOUBLE_AXE.get());
                        output.accept(MajesticItems.ILLUMINATION_STONE.get());
                        output.accept(MajesticItems.STARLIGHT_BOLT_SIGIL.get());
                        output.accept(MajesticItems.STARLIGHT_WARD_SIGIL.get());
                        output.accept(MajesticItems.STARLIGHT_REVEAL_SIGIL.get());
                        output.accept(MajesticItems.STARLIGHT_SURGE_SIGIL.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WORLD = TABS.register("world", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.majestic.world"))
                    .icon(() -> MajesticItems.STAR_FRAGMENT.get().getDefaultInstance())
                    .withTabsBefore(MAGIC.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(MajesticItems.STAR_FRAGMENT.get());
                        output.accept(MajesticItems.WARDEN_OF_THE_GATE_SPAWN_EGG.get());
                        output.accept(MajesticItems.ASTRAL_CONSTRUCT_SPAWN_EGG.get());
                    })
                    .build());

    private MajesticCreativeTabs() {}

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
        modEventBus.addListener(MajesticCreativeTabs::onBuildContents);
    }

    private static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(MajesticItems.WARDEN_OF_THE_GATE_SPAWN_EGG.get());
            event.accept(MajesticItems.ASTRAL_CONSTRUCT_SPAWN_EGG.get());
        }
    }
}
