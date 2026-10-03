package com.skd.majestic.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.skd.almanaccore.codec.CastType;
import com.skd.almanaccore.codec.RitualDefinition;
import com.skd.almanaccore.codec.ResearchNodeDefinition;
import com.skd.almanaccore.codec.SpellDefinition;
import com.skd.almanaccore.guide.VellumliBridge;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.block.MajesticBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Majestic.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGenerators {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var existingFileHelper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();

        if (event.includeServer()) {
            generator.addProvider(true, new SpellJsonProvider(packOutput));
            generator.addProvider(true, new RitualJsonProvider(packOutput));
            generator.addProvider(true, new ResearchNodeJsonProvider(packOutput));
            generator.addProvider(true, new MajesticRecipes(packOutput, lookupProvider));
        }

        if (event.includeClient()) {
            generator.addProvider(true, new MajesticBlockStates(packOutput, existingFileHelper));
            generator.addProvider(true, new MajesticItemModels(packOutput, existingFileHelper));
            generator.addProvider(true, new MajesticLang(packOutput));
        }
    }

    /**
     * Writes the almanac JSON through almanac_core's own {@code CODEC}s, so the schema is owned by
     * the library instead of this file: a field added upstream lands in the datapack automatically
     * and a field renamed upstream fails the build instead of being silently dropped.
     */
    private static class SpellJsonProvider implements DataProvider {
        private final PackOutput output;

        SpellJsonProvider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            Path dir = output.getOutputFolder().resolve("data/majestic/almanac/spell");
            ResourceLocation school = ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "starlight");

            // Effects stay empty: SpellEffect.CODEC has no real encode() upstream, so any effect
            // written through the codec would come out blank. Behaviour lives in the Java spells.
            List<SpellEntry> spells = List.of(
                    new SpellEntry("starlight_bolt", CastType.TOUCH, 1, 15.0, 20),
                    new SpellEntry("starlight_ward", CastType.SELF, 1, 25.0, 200),
                    new SpellEntry("starlight_reveal", CastType.AREA, 1, 20.0, 100),
                    new SpellEntry("starlight_surge", CastType.SELF, 1, 10.0, 300)
            );

            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (SpellEntry entry : spells) {
                SpellDefinition definition = new SpellDefinition(school, entry.tier, entry.cost,
                        entry.castType, entry.cooldown,
                        new SpellDefinition.Unlock("none",
                                ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "none")),
                        List.of(), Optional.empty());

                Optional<JsonElement> encoded = SpellDefinition.CODEC
                        .encodeStart(JsonOps.INSTANCE, definition)
                        .resultOrPartial(Majestic.LOGGER::error);
                if (encoded.isEmpty()) {
                    return CompletableFuture.failedFuture(
                            new IllegalStateException("Could not encode spell " + entry.name));
                }
                futures.add(DataProvider.saveStable(cache, encoded.get(), dir.resolve(entry.name + ".json")));
            }
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        }

        private record SpellEntry(String name, CastType castType, int tier, double cost, int cooldown) {}

        @Override
        public String getName() {
            return "Majestic Spell JSONs";
        }
    }

    private static class RitualJsonProvider implements DataProvider {
        private final PackOutput output;

        RitualJsonProvider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            Path dir = output.getOutputFolder().resolve("data/majestic/almanac/ritual");

            // Inputs stay empty on purpose: astral_core's RitualContext.inputs() is always empty
            // (pedestal scanning is unimplemented), so EngraveSpellRitual matches and consumes
            // reagents from the player's inventory itself.
            RitualDefinition definition = new RitualDefinition(
                    1,
                    List.of(),
                    20.0,
                    60,
                    new RitualDefinition.RitualRiskDef(0.0, "none"),
                    List.of());

            Optional<JsonElement> encoded = RitualDefinition.CODEC
                    .encodeStart(JsonOps.INSTANCE, definition)
                    .resultOrPartial(Majestic.LOGGER::error);
            if (encoded.isEmpty()) {
                return CompletableFuture.failedFuture(
                        new IllegalStateException("Could not encode ritual engrave_spell"));
            }
            return DataProvider.saveStable(cache, encoded.get(), dir.resolve("engrave_spell.json"));
        }

        @Override
        public String getName() {
            return "Majestic Ritual JSONs";
        }
    }

    private static class ResearchNodeJsonProvider implements DataProvider {
        private final PackOutput output;

        ResearchNodeJsonProvider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            Path dir = output.getOutputFolder().resolve("data/majestic/almanac/research_node");

            ResearchNodeDefinition definition = new ResearchNodeDefinition(
                    Optional.of("starlight"),
                    List.of(),
                    List.of(),
                    new ResearchNodeDefinition.TreePosition(0, 0));

            Optional<JsonElement> encoded = ResearchNodeDefinition.CODEC
                    .encodeStart(JsonOps.INSTANCE, definition)
                    .resultOrPartial(Majestic.LOGGER::error);
            if (encoded.isEmpty()) {
                return CompletableFuture.failedFuture(
                        new IllegalStateException("Could not encode research node first_light"));
            }
            return DataProvider.saveStable(cache, encoded.get(), dir.resolve("first_light.json"));
        }

        @Override
        public String getName() {
            return "Majestic Research Node JSONs";
        }
    }

    private static class MajesticBlockStates extends BlockStateProvider {
        MajesticBlockStates(PackOutput output, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
            super(output, Majestic.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            // Hand-made block models live in src/main/resources; only the blockstate is generated.
            simpleBlock(MajesticBlocks.ASTRAL_ALTAR.get(),
                    models().getExistingFile(modLoc("block/astral_altar")));
            simpleBlock(MajesticBlocks.ASTRAL_PILLAR.get(),
                    models().getExistingFile(modLoc("block/astral_pillar")));

            // The Order's stone. arcane_brick and rune_block are plain cubes, so their model comes
            // from cubeAll. carved_keystone has three different faces (top/side/bottom), which
            // cubeAll cannot express, so its model is hand-made next to the altar's and only the
            // blockstate is generated.
            simpleBlock(MajesticBlocks.ARCANE_BRICK.get(), models().cubeAll("arcane_brick", modLoc("block/arcane_brick")));
            simpleBlock(MajesticBlocks.RUNE_BLOCK.get(), models().cubeAll("rune_block", modLoc("block/rune_block")));
            simpleBlock(MajesticBlocks.CARVED_KEYSTONE.get(),
                    models().getExistingFile(modLoc("block/carved_keystone")));
        }
    }

    private static class MajesticItemModels extends ItemModelProvider {
        MajesticItemModels(PackOutput output, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
            super(output, Majestic.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "starlight_focus"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "blank_page"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "starlight_bolt_sigil"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "starlight_ward_sigil"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "starlight_reveal_sigil"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "starlight_surge_sigil"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "star_fragment"));

            withExistingParent("warden_of_the_gate_spawn_egg", mcLoc("item/template_spawn_egg"));
            withExistingParent("astral_construct_spawn_egg", mcLoc("item/template_spawn_egg"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "beginning_page"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "shrine_page"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "warden_page"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "lantern_page"));
            // Textures delivered in workshop beta.57, so the model can finally be generated.
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "illumination_stone"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "goblin_club"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "double_axe"));
        }
    }

    private static class MajesticRecipes extends RecipeProvider {
        MajesticRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void buildRecipes(RecipeOutput output) {
            // The two Order-era weapons, craftable so the player can own a copy of what the bestiary
            // carries. The creatures hold these as part of their models, not as items.

            // A wooden club: a shaft with a banded head. Reads as "wood" first, which is the point.
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MajesticItems.GOBLIN_CLUB.get())
                    .pattern("PDP")
                    .pattern("PDP")
                    .pattern(" S ")
                    .define('P', Items.DARK_OAK_PLANKS)
                    .define('D', Items.IRON_INGOT)
                    .define('S', Items.STICK)
                    .unlockedBy("has_iron", has(Items.IRON_INGOT))
                    .save(output);

            // A double axe: two heads on one haft, so it is two iron over a stick.
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MajesticItems.DOUBLE_AXE.get())
                    .pattern(" I ")
                    .pattern("SI ")
                    .pattern(" I ")
                    .define('I', Items.IRON_INGOT)
                    .define('S', Items.STICK)
                    .unlockedBy("has_iron", has(Items.IRON_INGOT))
                    .save(output);

            // The guide book is a Vellumli book item carrying the majestic:almanac book component.
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC,
                            VellumliBridge.giveBookStack(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "almanac")))
                    .pattern("LLL")
                    .pattern("LBL")
                    .pattern("LLL")
                    .define('L', Items.LAPIS_LAZULI)
                    .define('B', Items.BOOK)
                    .unlockedBy("has_book", has(Items.BOOK))
                    .save(output, ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "almanac"));
        }
    }

    /**
     * The Almanac's opening page, in the voice of a Watcher's field notebook. It lives here rather
     * than as a book entry on purpose: Vellumli's index lists every entry in the book regardless of
     * category, so the only way to keep the prologue out of the table of contents is to make it the
     * landing text — the page you always see, which is never listed and can never be locked.
     */
    private static final String LANDING_TEXT_EN =
            "$(o)When the sun sinks and the stars wake,$(br)those who once kept watch begin to walk again.$(br)"
            + "Hooded, their lanterns dark, the undead wander the night,$(br)"
            + "still clutching the words they could not protect.$(br2)"
            + "Take the first page from the undead who walk after dark,$(br)and the road to the light will open.$()$(br2)"
            + "$(o)A recovered page, once read, becomes part of this Almanac and reveals the next step of the journey. "
            + "At the end of every chapter lie the Order's words — and behind every word, a place.$()";

    private static class MajesticLang extends LanguageProvider {
        MajesticLang(PackOutput output) {
            super(output, Majestic.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("item.majestic.starlight_focus", "Starlight Focus");
            add("item.majestic.blank_page", "Blank Page");
            add("item.majestic.beginning_page", "Page of the Beginning");
            add("item.majestic.beginning_page.desc", "A torn page from the Order of Watchers' journal.");
            add("item.majestic.shrine_page", "Shrine Page");
            add("item.majestic.shrine_page.desc", "The next page of the Order's journal.");
            add("item.majestic.starlight_bolt_sigil", "Starlight Bolt Sigil");
            add("item.majestic.starlight_ward_sigil", "Starlight Ward Sigil");
            add("item.majestic.starlight_reveal_sigil", "Starlight Reveal Sigil");
            add("item.majestic.starlight_surge_sigil", "Starlight Surge Sigil");
            add("block.majestic.astral_altar", "Astral Altar");
            add("block.majestic.astral_pillar", "Astral Pillar");
            add("block.majestic.arcane_brick", "Order's Stone");
            add("block.majestic.rune_block", "Rune Block");
            add("block.majestic.carved_keystone", "Carved Keystone");
            add("item.majestic.star_fragment", "Star Fragment");
            add("item.majestic.warden_of_the_gate_spawn_egg", "Warden of the Gate Spawn Egg");
            add("item.majestic.astral_construct_spawn_egg", "Astral Construct Spawn Egg");
            add("entity.majestic.warden_of_the_gate", "Warden of the Gate");
            add("entity.majestic.astral_construct", "Astral Construct");
            add("advancements.majestic.journey.root.title", "The Arcane Journey");
            add("advancements.majestic.journey.root.description", "Begin your journey toward the light");
            add("advancements.majestic.journey.fallen_shrine.title", "Ruins of the Order");
            add("advancements.majestic.journey.fallen_shrine.description", "Find a Fallen Shrine");
            add("advancements.majestic.journey.observatory.title", "Where Stars Were Read");
            add("advancements.majestic.journey.observatory.description", "Reach the Observatory");
            add("advancements.majestic.warden_of_the_gate.title", "Beyond the Gate");
            add("advancements.majestic.warden_of_the_gate.description", "Defeat the Warden of the Gate in the Observatory");
            add("advancements.majestic.bestiary.undead.title", "Undead");
            add("advancements.majestic.bestiary.undead.description", "Slay an Undead that roams the night");
            add("advancements.majestic.bestiary.goblin.title", "Goblin");
            add("advancements.majestic.bestiary.goblin.description", "Slay a Goblin");
            add("advancements.majestic.bestiary.wolf.title", "Dire Wolf");
            add("advancements.majestic.bestiary.wolf.description", "Slay a Dire Wolf");
            add("advancements.majestic.bestiary.satyr.title", "Satyr");
            add("advancements.majestic.bestiary.satyr.description", "Slay a Satyr");
            add("advancements.majestic.bestiary.gnome.title", "Gnome");
            add("advancements.majestic.bestiary.gnome.description", "Slay a Gnome");
            add("advancements.majestic.bestiary.elf.title", "Elf");
            add("advancements.majestic.bestiary.elf.description", "Slay an Elf");
            add("advancements.majestic.bestiary.arcane_mage.title", "Arcane Mage");
            add("advancements.majestic.bestiary.arcane_mage.description", "Slay an Arcane Mage");
            add("advancements.majestic.bestiary.draconid.title", "Draconid");
            add("advancements.majestic.bestiary.draconid.description", "Slay a Draconid");
            add("advancements.majestic.bestiary.warden_of_the_gate.title", "Warden of the Gate");
            add("advancements.majestic.bestiary.warden_of_the_gate.description", "Slay the Warden of the Gate");
            add("advancements.majestic.bestiary.astral_construct.title", "Astral Construct");
            add("advancements.majestic.bestiary.astral_construct.description", "Destroy an Astral Construct");
            add("itemGroup.majestic.magic", "Majestic: Magic");
            add("itemGroup.majestic.world", "Majestic: World");
            add("book.majestic.almanac.name", "The Almanac");
            add("book.majestic.almanac.landing_text", LANDING_TEXT_EN);
            add("book.majestic.almanac.act_1", "Act I — Awakening");
            add("book.majestic.almanac.act_1.desc", "Where the Order's story begins, page by page.");
            add("book.majestic.almanac.chapter_1", "I. The Fallen Shrines");
            add("book.majestic.almanac.chapter_2", "II. The Observatory");
            add("book.majestic.almanac.chapter_3", "III. The Sleeping Portal");
            add("book.majestic.almanac.chapter_2_1", "2.1. The Search");
            add("item.majestic.warden_page", "Warden's Page");
            add("item.majestic.warden_page.desc", "Carved into stone, not written. The Order had no paper left.");
            add("item.majestic.lantern_page", "Lantern-Bearer's Page");
            add("item.majestic.lantern_page.desc", "A loose leaf, handed to you by a light that should not still be burning.");
            add("message.majestic.page.no_book", "You need the Almanac with you to read this page.");
            add("message.majestic.page.already_read", "The Almanac already holds this page.");
            add("entity.majestic.lantern_bearer", "The Lantern-Bearer");
            add("block.majestic.vault_altar", "Vault Altar");
            add("book.majestic.almanac.structures", "Structures");
            add("book.majestic.almanac.structures.desc", "The places the Order left behind, revealed as you set foot in them.");
            add("message.majestic.vault.sealed", "The Order's chests will not open until the Stone of Illumination rests on the altar.");
            add("item.majestic.illumination_stone", "Stone of Illumination");
            add("item.majestic.goblin_club", "Goblin Club");
            add("item.majestic.double_axe", "Double Axe");
            add("advancements.majestic.journey.in_observers_vault.title", "The Watchers' Vault");
            add("advancements.majestic.journey.in_observers_vault.description", "Go down where the Order kept what it had not yet spent.");
            add("advancements.majestic.journey.in_arcane_portal.title", "The Arcane Portal");
            add("advancements.majestic.journey.in_arcane_portal.description", "The work left unfinished, and sealed by people who never learned to open it again.");
            add("advancements.majestic.journey.met_the_lantern_bearer.title", "The Lantern-Bearer");
            add("advancements.majestic.journey.met_the_lantern_bearer.description", "A spirit still carrying the one lantern that never went out.");
            add("message.majestic.page.needs_previous", "This page makes no sense yet. Something comes before it.");
            add("message.majestic.page.needs_chapter", "You need to read this first: %s");
            add("message.majestic.page.new_chapter", "A new chapter unfolds in the Almanac: %s");
            add("majestic.page.burned.title", "TOO LATE");
            add("majestic.page.burned.subtitle", "The page crumbles. Something is left in the air.");
            add("effect.majestic.burned_page_residue", "Ashen Residue");
            add("message.majestic.focus.no_spell", "No spell recorded on this focus.");
            add("message.majestic.focus.cooldown", "Spell is on cooldown!");
            add("message.majestic.focus.cooldown_ticks", "Spell is on cooldown (%d ticks).");
            add("message.majestic.focus.no_essence", "Not enough essence.");
            add("message.majestic.spell.no_target", "No target.");
            add("message.majestic.spell.no_effect", "The spell has nothing to work on.");
            add("hud.majestic.essence", "Essence: %d/%d");
            add("spell.majestic.starlight_bolt", "Starlight Bolt");
            add("spell.majestic.starlight_ward", "Starlight Ward");
            add("spell.majestic.starlight_reveal", "Starlight Reveal");
            add("spell.majestic.starlight_surge", "Starlight Surge");
            add("commands.majestic.error", "Error: %s");
            add("commands.majestic.status.essence", "Essence: %d/%d");
            add("commands.majestic.status.cooldown", "%s: on cooldown (%d ticks)");
            add("commands.majestic.status.no_cooldown", "%s: ready");
            add("majestic.shrine.unsealed", "The shrine's seal fades.");
            add("majestic.observatory.unsealed", "The Observatory is free of its warden.");
            add("majestic.altar.boss_active", "The Warden already stands in this arena.");
            add("majestic.altar.detonation.title", "THE ALTAR IS BREAKING");
            add("majestic.altar.detonation.subtitle", "Something inside it is going wrong");
            add("majestic.altar.detonation.countdown", "Get away!  %s");
            add("majestic.warden.retreat", "The Warden laughs and fades into the stars...");
        }
    }

    private DataGenerators() {}
}
