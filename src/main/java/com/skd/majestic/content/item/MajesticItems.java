package com.skd.majestic.content.item;

import com.skd.majestic.Majestic;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MajesticItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Majestic.MOD_ID);

    public static final DeferredItem<FocusItem> STARLIGHT_FOCUS = ITEMS.registerItem(
            "starlight_focus",
            properties -> new FocusItem(properties.stacksTo(1)),
            new Item.Properties()
    );

    public static final DeferredItem<Item> BLANK_PAGE = ITEMS.registerItem(
            "blank_page",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> STARLIGHT_BOLT_SIGIL = ITEMS.registerItem(
            "starlight_bolt_sigil",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> STARLIGHT_WARD_SIGIL = ITEMS.registerItem(
            "starlight_ward_sigil",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> STARLIGHT_REVEAL_SIGIL = ITEMS.registerItem(
            "starlight_reveal_sigil",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> STARLIGHT_SURGE_SIGIL = ITEMS.registerItem(
            "starlight_surge_sigil",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> STAR_FRAGMENT = ITEMS.registerItem(
            "star_fragment",
            Item::new,
            new Item.Properties()
    );

    private MajesticItems() {}

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
