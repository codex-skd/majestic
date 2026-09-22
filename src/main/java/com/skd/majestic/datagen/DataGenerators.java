package com.skd.majestic.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skd.majestic.Majestic;
import com.skd.majestic.content.block.MajesticBlocks;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
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
            simpleBlock(MajesticBlocks.ASTRAL_ALTAR.get());
            simpleBlock(MajesticBlocks.ASTRAL_PILLAR.get());
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
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "astral_altar"));
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "astral_pillar"));
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
            add("item.majestic.starlight_bolt_sigil", "Starlight Bolt Sigil");
            add("item.majestic.starlight_ward_sigil", "Starlight Ward Sigil");
            add("item.majestic.starlight_reveal_sigil", "Starlight Reveal Sigil");
            add("item.majestic.starlight_surge_sigil", "Starlight Surge Sigil");
            add("block.majestic.astral_altar", "Astral Altar");
            add("block.majestic.astral_pillar", "Astral Pillar");
            add("commands.majestic.status.essence", "Essence: %d/%d");
            add("commands.majestic.status.cooldown", "%s: on cooldown (%d ticks)");
            add("commands.majestic.status.no_cooldown", "%s: ready");
        }
    }

    private DataGenerators() {}
}
