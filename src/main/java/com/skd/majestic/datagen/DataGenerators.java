package com.skd.majestic.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skd.majestic.Majestic;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
        }

        if (event.includeClient()) {
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
            Path dir = output.getOutputFolder().resolve("data/majestic/spells");

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

    private static class MajesticItemModels extends ItemModelProvider {
        MajesticItemModels(PackOutput output, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
            super(output, Majestic.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            basicItem(ResourceLocation.fromNamespaceAndPath(Majestic.MOD_ID, "starlight_focus"));
        }
    }

    private static class MajesticLang extends LanguageProvider {
        MajesticLang(PackOutput output) {
            super(output, Majestic.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("item.majestic.starlight_focus", "Starlight Focus");
            add("commands.majestic.status.essence", "Essence: %d/%d");
            add("commands.majestic.status.cooldown", "%s: on cooldown (%d ticks)");
            add("commands.majestic.status.no_cooldown", "%s: ready");
        }
    }

    private DataGenerators() {}
}
