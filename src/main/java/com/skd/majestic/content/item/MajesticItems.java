package com.skd.majestic.content.item;

import com.skd.majestic.Majestic;
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
            // The prologue entry is read_by_default, so it has no advancement of its own and can
            // never unlock a toast by being read. This one is granted here instead, in the same pass
            // as chapter 1, so the first page a player ever reads gives them something.
            properties -> new JournalPageItem(properties,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_1"),
                    null,
                    null,
                    List.of(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/prologue")),
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

    /**
     * Found in the Vault's deep chests. Reading it opens the rebuilding chapter: the stone that was
     * missing from the Portal has been found.
     *
     * <p>Reading it revokes the locked variant of Chapter III and grants the plain one back, because
     * the lock was "the stone is missing" and the stone is no longer missing. The locked variant is
     * the requirement it revokes, so the shared read path skips the requirement check; a player who
     * holds the locked chapter (the normal state after meeting the Lantern-Bearer) reads it as
     * intended, and one whose world predates the change is not refused either.</p>
     */
    public static final DeferredItem<JournalPageItem> PORTAL_PHASE_PAGE = ITEMS.registerItem(
            "portal_phase_page",
            properties -> new JournalPageItem(properties,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3_rebuild"),
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3_locked"),
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3_locked"),
                    List.of(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3")),
                    "item.majestic.portal_phase_page.desc",
                    "book.majestic.almanac.chapter_3_rebuild"),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
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


    // The Order's stone. NO recipe, on purpose: these come out of the Watchers' vault and are the
    // only way to finish the Arcane Portal. See DESIGN_MAJESTIC_1-21-1.md.








    /**
     * Dropped by the Receptacle when the last gap closes. Reading it records that the Portal's first
     * phase is done and points at the island above the clouds.
     *
     * <p>It requires the rebuilding chapter, so it cannot be read before the stone has been found,
     * and it grants the completed-phase chapter. Arriving at the island is what opens the phase-two
     * chapter, so if the island came first the read grants that too — handled by the advancement
     * listener, not here.</p>
     */
    public static final DeferredItem<JournalPageItem> PORTAL_PHASE_TWO_PAGE = ITEMS.registerItem(
            "portal_phase_two_page",
            properties -> new JournalPageItem(properties,
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3_phase1"),
                    ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "journey/chapter_3_rebuild"),
                    "item.majestic.portal_phase_two_page.desc",
                    "book.majestic.almanac.chapter_3_phase1"),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );



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
