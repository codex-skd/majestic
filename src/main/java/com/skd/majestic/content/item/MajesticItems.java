package com.skd.majestic.content.item;

import com.skd.majestic.Majestic;
import com.skd.majestic.content.block.MajesticBlocks;
import com.skd.majestic.content.entity.MajesticEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
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

    public static final DeferredItem<JournalPageItem> BEGINNING_PAGE = ITEMS.registerItem(
            "beginning_page",
            properties -> new JournalPageItem(properties,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_1"),
                    null,
                    "item.majestic.beginning_page.desc",
                    "book.majestic.almanac.chapter_1"),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<JournalPageItem> SHRINE_PAGE = ITEMS.registerItem(
            "shrine_page",
            properties -> new JournalPageItem(properties,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_2"),
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_1"),
                    "item.majestic.shrine_page.desc",
                    "book.majestic.almanac.chapter_2"),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
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

    public static final DeferredItem<Item> ALTAR_BLUEPRINT_T2 = ITEMS.registerItem(
            "altar_blueprint_t2",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> ASTRAL_DUST = ITEMS.registerItem(
            "astral_dust",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<BlockItem> ASTRAL_ALTAR = ITEMS.registerSimpleBlockItem(MajesticBlocks.ASTRAL_ALTAR);

    public static final DeferredItem<BlockItem> ASTRAL_PILLAR = ITEMS.registerSimpleBlockItem(MajesticBlocks.ASTRAL_PILLAR);

    public static final DeferredItem<Item> ETHER_LENS = ITEMS.registerItem(
            "ether_lens",
            Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<DeferredSpawnEggItem> WARDEN_OF_THE_GATE_SPAWN_EGG = ITEMS.register(
            "warden_of_the_gate_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.WARDEN_OF_THE_GATE, 0x6E6E6E, 0xA8C8E8, new Item.Properties())
    );

    public static final DeferredItem<DeferredSpawnEggItem> ASTRAL_CONSTRUCT_SPAWN_EGG = ITEMS.register(
            "astral_construct_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.ASTRAL_CONSTRUCT, 0x8A8A8A, 0xD4AF6A, new Item.Properties())
    );

    public static final DeferredItem<DeferredSpawnEggItem> FALLEN_WATCHER_SPAWN_EGG = ITEMS.register(
            "fallen_watcher_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.FALLEN_WATCHER, 0x2E3D66, 0xD4AF6A, new Item.Properties())
    );

    public static final DeferredItem<DeferredSpawnEggItem> STARGAZER_CULTIST_SPAWN_EGG = ITEMS.register(
            "stargazer_cultist_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.STARGAZER_CULTIST, 0x1F2A4A, 0xA8C8E8, new Item.Properties())
    );

    public static final DeferredItem<DeferredSpawnEggItem> METEOR_CRAWLER_SPAWN_EGG = ITEMS.register(
            "meteor_crawler_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.METEOR_CRAWLER, 0x2B2B30, 0xA8C8E8, new Item.Properties())
    );

    public static final DeferredItem<DeferredSpawnEggItem> UMBRAL_MOTH_SPAWN_EGG = ITEMS.register(
            "umbral_moth_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.UMBRAL_MOTH, 0x4A4458, 0xA8C8E8, new Item.Properties())
    );

    private MajesticItems() {}

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
