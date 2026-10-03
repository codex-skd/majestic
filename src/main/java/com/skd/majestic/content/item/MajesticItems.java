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

import java.util.List;

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

    /** Dropped by the Warden of the Gate when it dies. Opens the chapter on the sleeping portal. */
    public static final DeferredItem<JournalPageItem> WARDEN_PAGE = ITEMS.registerItem(
            "warden_page",
            properties -> new JournalPageItem(properties,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3"),
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_2"),
                    "item.majestic.warden_page.desc",
                    "book.majestic.almanac.chapter_3"),
            new Item.Properties().stacksTo(16).rarity(Rarity.RARE)
    );

    /**
     * Given by the lantern-bearer at the portal. Reading it grants the search chapter and the
     * locked variant of the portal chapter, and revokes the original one so the Almanac swaps it
     * for the entry that carries the literal "(Locked)" — the guide book cannot rename an entry.
     */
    public static final DeferredItem<JournalPageItem> LANTERN_PAGE = ITEMS.registerItem(
            "lantern_page",
            properties -> new JournalPageItem(properties,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_2_1"),
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3"),
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3"),
                    List.of(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3_locked")),
                    "item.majestic.lantern_page.desc",
                    "book.majestic.almanac.chapter_2_1"),
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
    );

    /**
     * Found in the Observatory arena chest, set into the Vault's altar. Once placed it seals there and
     * lights the whole hall, column by column.
     */
    /**
     * The Goblin's weapon. Held by the creature as part of its model, so this is the player's copy:
     * craftable, and the trophy of having met one up close.
     */
    public static final DeferredItem<Item> GOBLIN_CLUB = ITEMS.registerItem(
            "goblin_club",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    /** The Draconid's weapon, likewise. */
    public static final DeferredItem<Item> DOUBLE_AXE = ITEMS.registerItem(
            "double_axe",
            Item::new,
            new Item.Properties().stacksTo(1)
    );

    public static final DeferredItem<Item> ILLUMINATION_STONE = ITEMS.registerItem(
            "illumination_stone",
            Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
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

    // The altar and pillar have BlockItems so they render in the creative tab, but deliberately
    // have NO recipe: the altar only ever exists inside a generated structure. Do not add one.
    public static final DeferredItem<BlockItem> ASTRAL_ALTAR = ITEMS.registerSimpleBlockItem(MajesticBlocks.ASTRAL_ALTAR);

    public static final DeferredItem<BlockItem> ASTRAL_PILLAR = ITEMS.registerSimpleBlockItem(MajesticBlocks.ASTRAL_PILLAR);

    // The Order's stone. NO recipe, on purpose: these come out of the Watchers' vault and are the
    // only way to finish the Arcane Portal. See DESIGN_MAJESTIC_1-21-1.md.
    /** BlockItem so the socket can be placed and picked up before it is lit. */
    public static final DeferredItem<BlockItem> VAULT_ALTAR = ITEMS.registerSimpleBlockItem(MajesticBlocks.VAULT_ALTAR);

    public static final DeferredItem<BlockItem> ARCANE_BRICK = ITEMS.registerSimpleBlockItem(MajesticBlocks.ARCANE_BRICK);

    public static final DeferredItem<BlockItem> RUNE_BLOCK = ITEMS.registerSimpleBlockItem(MajesticBlocks.RUNE_BLOCK);

    public static final DeferredItem<BlockItem> CARVED_KEYSTONE = ITEMS.registerSimpleBlockItem(MajesticBlocks.CARVED_KEYSTONE);

    public static final DeferredItem<DeferredSpawnEggItem> WARDEN_OF_THE_GATE_SPAWN_EGG = ITEMS.register(
            "warden_of_the_gate_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.WARDEN_OF_THE_GATE, 0x6E6E6E, 0xA8C8E8, new Item.Properties())
    );

    public static final DeferredItem<DeferredSpawnEggItem> ASTRAL_CONSTRUCT_SPAWN_EGG = ITEMS.register(
            "astral_construct_spawn_egg",
            () -> new DeferredSpawnEggItem(MajesticEntities.ASTRAL_CONSTRUCT, 0x8A8A8A, 0xD4AF6A, new Item.Properties())
    );

    private MajesticItems() {}

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
