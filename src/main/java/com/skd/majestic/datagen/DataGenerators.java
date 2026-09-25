package com.skd.majestic.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.block.MajesticBlocks;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import com.skd.almanaccore.guide.VellumliBridge;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.nio.file.Path;
import java.util.List;
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

    private static class SpellJsonProvider implements DataProvider {
        private final PackOutput output;

        SpellJsonProvider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            Path dir = output.getOutputFolder().resolve("data/majestic/almanac/spell");

            List<SpellData> spells = List.of(
                    new SpellData("starlight_bolt", "majestic:starlight", 1, 15.0, "touch", 20),
                    new SpellData("starlight_ward", "majestic:starlight", 1, 25.0, "self", 200),
                    new SpellData("starlight_reveal", "majestic:starlight", 1, 20.0, "area", 100),
                    new SpellData("starlight_surge", "majestic:starlight", 1, 10.0, "self", 300)
            );

            List<CompletableFuture<?>> futures = new java.util.ArrayList<>();
            for (SpellData spell : spells) {
                JsonObject json = new JsonObject();
                json.addProperty("school", spell.school);
                json.addProperty("tier", spell.tier);
                json.addProperty("cost", spell.cost);
                json.addProperty("cast_type", spell.castType);
                json.addProperty("cooldown", spell.cooldown);

                JsonObject unlock = new JsonObject();
                unlock.addProperty("type", "none");
                unlock.addProperty("value", "majestic:none");
                json.add("unlock", unlock);

                json.add("effects", new JsonArray());

                Path file = dir.resolve(spell.name + ".json");
                futures.add(DataProvider.saveStable(cache, json, file));
            }
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        }

        @Override
        public String getName() {
            return "Majestic Spell JSONs";
        }

        private record SpellData(String name, String school, int tier, double cost, String castType, int cooldown) {}
    }

    private static class RitualJsonProvider implements DataProvider {
        private final PackOutput output;

        RitualJsonProvider(PackOutput output) {
            this.output = output;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            Path dir = output.getOutputFolder().resolve("data/majestic/almanac/ritual");

            JsonObject json = new JsonObject();
            json.addProperty("altar_tier", 1);
            json.add("inputs", new JsonArray());
            json.addProperty("essence", 20.0);
            json.addProperty("duration", 60);

            JsonObject risk = new JsonObject();
            risk.addProperty("chance", 0.0);
            risk.addProperty("penalty", "none");
            json.add("risk", risk);

            json.add("outputs", new JsonArray());

            Path file = dir.resolve("engrave_spell.json");
            return DataProvider.saveStable(cache, json, file);
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

            JsonObject json = new JsonObject();
            JsonObject position = new JsonObject();
            position.addProperty("x", 0);
            position.addProperty("y", 0);
            json.add("position", position);
            json.add("requirements", new JsonArray());
            json.add("unlocks", new JsonArray());

            Path file = dir.resolve("first_light.json");
            return DataProvider.saveStable(cache, json, file);
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
            simpleBlockWithItem(MajesticBlocks.ASTRAL_ALTAR.get(), cubeAll(MajesticBlocks.ASTRAL_ALTAR.get()));
            simpleBlockWithItem(MajesticBlocks.ASTRAL_PILLAR.get(), cubeAll(MajesticBlocks.ASTRAL_PILLAR.get()));
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
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "altar_blueprint_t2"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "astral_dust"));

            withExistingParent("warden_of_the_gate_spawn_egg", mcLoc("item/template_spawn_egg"));
            withExistingParent("astral_construct_spawn_egg", mcLoc("item/template_spawn_egg"));
            withExistingParent("fallen_watcher_spawn_egg", mcLoc("item/template_spawn_egg"));
            withExistingParent("stargazer_cultist_spawn_egg", mcLoc("item/template_spawn_egg"));
            withExistingParent("meteor_crawler_spawn_egg", mcLoc("item/template_spawn_egg"));
            withExistingParent("umbral_moth_spawn_egg", mcLoc("item/template_spawn_egg"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "ether_lens"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "beginning_page"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "shrine_page"));
        }
    }

    private static class MajesticRecipes extends RecipeProvider {
        MajesticRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void buildRecipes(RecipeOutput output) {
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
            add("item.majestic.star_fragment", "Star Fragment");
            add("item.majestic.altar_blueprint_t2", "Tier 2 Altar Blueprint");
            add("item.majestic.astral_dust", "Astral Dust");
            add("item.majestic.ether_lens", "Ether Lens");
            add("item.majestic.warden_of_the_gate_spawn_egg", "Warden of the Gate Spawn Egg");
            add("item.majestic.astral_construct_spawn_egg", "Astral Construct Spawn Egg");
            add("item.majestic.fallen_watcher_spawn_egg", "Fallen Watcher Spawn Egg");
            add("item.majestic.stargazer_cultist_spawn_egg", "Stargazer Cultist Spawn Egg");
            add("item.majestic.meteor_crawler_spawn_egg", "Meteor Crawler Spawn Egg");
            add("item.majestic.umbral_moth_spawn_egg", "Umbral Moth Spawn Egg");
            add("entity.majestic.warden_of_the_gate", "Warden of the Gate");
            add("entity.majestic.astral_construct", "Astral Construct");
            add("entity.majestic.fallen_watcher", "Fallen Watcher");
            add("entity.majestic.stargazer_cultist", "Stargazer Cultist");
            add("entity.majestic.meteor_crawler", "Meteor Crawler");
            add("entity.majestic.umbral_moth", "Umbral Moth");
            add("entity.majestic.starlight_bolt", "Starlight Bolt");
            add("advancements.majestic.journey.root.title", "The Arcane Journey");
            add("advancements.majestic.journey.root.description", "Begin your journey toward the light");
            add("advancements.majestic.journey.fallen_shrine.title", "Ruins of the Order");
            add("advancements.majestic.journey.fallen_shrine.description", "Find a Fallen Shrine");
            add("advancements.majestic.journey.observatory.title", "Where Stars Were Read");
            add("advancements.majestic.journey.observatory.description", "Reach the Observatory");
            add("advancements.majestic.warden_of_the_gate.title", "Beyond the Gate");
            add("advancements.majestic.warden_of_the_gate.description", "Defeat the Warden of the Gate in the Observatory");
            add("itemGroup.majestic.magic", "Majestic: Magic");
            add("itemGroup.majestic.world", "Majestic: World");
            add("book.majestic.almanac.name", "The Almanac");
            add("book.majestic.almanac.landing_text", "A guide to the arcane arts of starlight.");
            add("book.majestic.almanac.chapter_1", "I. The Fallen Shrines");
            add("book.majestic.almanac.chapter_2", "II. The Observatory");
            add("message.majestic.page.no_book", "You need the Almanac with you to read this page.");
            add("message.majestic.page.already_read", "The Almanac already holds this page.");
            add("message.majestic.page.needs_previous", "This page makes no sense yet. Something comes before it.");
            add("message.majestic.page.new_chapter", "A new chapter unfolds in the Almanac: %s");
            add("message.majestic.focus.no_spell", "No spell recorded on this focus.");
            add("message.majestic.focus.cooldown", "Spell is on cooldown!");
            add("hud.majestic.essence", "Essence: %d/%d");
            add("spell.majestic.starlight_bolt", "Starlight Bolt");
            add("spell.majestic.starlight_ward", "Starlight Ward");
            add("spell.majestic.starlight_reveal", "Starlight Reveal");
            add("spell.majestic.starlight_surge", "Starlight Surge");
            add("commands.majestic.error", "Error: %s");
            add("commands.majestic.status.essence", "Essence: %d/%d");
            add("commands.majestic.status.cooldown", "%s: on cooldown (%d ticks)");
            add("commands.majestic.status.no_cooldown", "%s: ready");
        }
    }

    private DataGenerators() {}
}
