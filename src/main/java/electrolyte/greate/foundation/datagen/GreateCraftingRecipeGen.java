package electrolyte.greate.foundation.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import electrolyte.greate.Greate;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.Cogwheels;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.EncasedFans;
import electrolyte.greate.registry.Gearboxes;
import electrolyte.greate.registry.GreateMaterials;
import electrolyte.greate.registry.GreateTiers;
import electrolyte.greate.registry.MechanicalMixers;
import electrolyte.greate.registry.MechanicalPresses;
import electrolyte.greate.registry.MechanicalPumps;
import electrolyte.greate.registry.Millstones;
import electrolyte.greate.registry.Saws;
import electrolyte.greate.registry.Shafts;
import electrolyte.greate.registry.VerticalGearboxes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GreateCraftingRecipeGen implements DataProvider {

    private final PackOutput output;

    public GreateCraftingRecipeGen(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            int tier = tierMaterial.tier();
            ItemStack plate = plate(tier);
            if (plate.isEmpty()) {
                Greate.LOGGER.warn("[Greate] datagen: 跳过 {} 的工作台配方（没有可用的板/锭）", tierMaterial.id());
                continue;
            }
            JsonObject casing = item(casing(tier));
            ItemStack shaft = new ItemStack(Shafts.ALL.get(tier).get());
            ItemStack cogwheel = new ItemStack(Cogwheels.SMALL.get(tier).get());
            ItemStack largeCogwheel = new ItemStack(Cogwheels.LARGE.get(tier).get());

            add(futures, cache, shapeless(tierMaterial.id() + "_shaft",
                    List.of(item(plate), item(plate)), shaft.copyWithCount(4)));

            add(futures, cache, shapeless(tierMaterial.id() + "_cogwheel",
                    List.of(item(shaft), item(plate), item(plate)), cogwheel));

            add(futures, cache, shapeless("large_" + tierMaterial.id() + "_cogwheel",
                    List.of(item(cogwheel), item(plate), item(plate)), largeCogwheel));

            Map<Character, JsonObject> gearboxKeys = new LinkedHashMap<>();
            gearboxKeys.put('S', item(shaft));
            gearboxKeys.put('C', casing);
            add(futures, cache, shaped(tierMaterial.id() + "_gearbox",
                    new String[]{" S ", "SCS", " S "}, gearboxKeys,
                    new ItemStack(Gearboxes.ALL.get(tier).get())));

            add(futures, cache, shaped(tierMaterial.id() + "_vertical_gearbox",
                    new String[]{"S S", " C ", "S S"}, gearboxKeys,
                    new ItemStack(VerticalGearboxes.ALL.get(tier).get())));

            Map<Character, JsonObject> millstoneKeys = new LinkedHashMap<>();
            millstoneKeys.put('A', item(cogwheel));
            millstoneKeys.put('W', tag("minecraft:wooden_slabs"));
            millstoneKeys.put('H', casing);
            millstoneKeys.put('S', item(shaft));
            add(futures, cache, shaped(tierMaterial.id() + "_millstone",
                    new String[]{" A ", "WHW", " S "}, millstoneKeys,
                    new ItemStack(Millstones.ALL.get(tier).get())));

            Map<Character, JsonObject> pressKeys = new LinkedHashMap<>();
            pressKeys.put('P', item(plate));
            pressKeys.put('S', item(shaft));
            pressKeys.put('C', casing);
            add(futures, cache, shaped(tierMaterial.id() + "_mechanical_press",
                    new String[]{"P P", "PSP", " C "}, pressKeys,
                    new ItemStack(MechanicalPresses.ALL.get(tier).get())));

            Map<Character, JsonObject> mixerKeys = new LinkedHashMap<>();
            mixerKeys.put('P', item(plate));
            mixerKeys.put('C', casing);
            mixerKeys.put('S', item(shaft));
            add(futures, cache, shaped(tierMaterial.id() + "_mechanical_mixer",
                    new String[]{" P ", "PCP", " S "}, mixerKeys,
                    new ItemStack(MechanicalMixers.ALL.get(tier).get())));

            add(futures, cache, shaped(tierMaterial.id() + "_mechanical_pump",
                    new String[]{"P P", "SCS", " P "}, mixerKeys,
                    new ItemStack(MechanicalPumps.ALL.get(tier).get())));

            add(futures, cache, shapeless(tierMaterial.id() + "_crushing_wheel",
                    List.of(item(cogwheel), item(cogwheel), item(plate), item(plate),
                            item(plate), item(plate)),
                    new ItemStack(CrushingWheels.WHEELS.get(tier).get())));

            add(futures, cache, shaped(tierMaterial.id() + "_encased_fan",
                    new String[]{" P ", "PCP", " S "}, mixerKeys,
                    new ItemStack(EncasedFans.ALL.get(tier).get())));

            Map<Character, JsonObject> sawKeys = new LinkedHashMap<>();
            sawKeys.put('P', item(plate));
            sawKeys.put('C', casing);
            sawKeys.put('S', item(shaft));
            add(futures, cache, shaped(tierMaterial.id() + "_mechanical_saw",
                    new String[]{"P P", " C ", " S "}, sawKeys,
                    new ItemStack(Saws.ALL.get(tier).get())));
        }

        for (int polymer = 0; polymer < Belts.POLYMER_IDS.length; polymer++) {
            Material material = Belts.polymerMaterial(polymer);
            if (material == null) {
                continue;
            }
            ItemStack polymerDust = ChemicalHelper.get(TagPrefix.dust, material);
            if (polymerDust.isEmpty()) {
                Greate.LOGGER.warn("[Greate] datagen: 跳过 {} 传送带连接器（没有聚合物粉）",
                        Belts.POLYMER_IDS[polymer]);
                continue;
            }
            add(futures, cache, shapeless(Belts.POLYMER_IDS[polymer] + "_belt_connector",
                    List.of(item(new ItemStack(AllItems.BELT_CONNECTOR.get())),
                            item(polymerDust), item(polymerDust),
                            item(polymerDust), item(polymerDust)),
                    new ItemStack(Belts.CONNECTOR_ITEMS.get(polymer).get())));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    // ------------------------------------------------------------------

    // ------------------------------------------------------------------

    private static ItemStack plate(int tier) {
        if (tier == 0) {
            return new ItemStack(AllItems.ANDESITE_ALLOY.get());
        }
        Material material = GreateMaterials.forTier(GreateTiers.MATERIALS.get(tier).id());
        ItemStack plate = ChemicalHelper.get(TagPrefix.plate, material);
        if (!plate.isEmpty()) {
            return plate;
        }
        return ChemicalHelper.get(TagPrefix.ingot, material);
    }

    private static net.minecraft.world.item.Item casing(int tier) {
        if (tier <= 1) {
            return AllBlocks.ANDESITE_CASING.asItem();
        }
        if (tier <= 4) {
            return AllBlocks.COPPER_CASING.asItem();
        }
        if (tier <= 7) {
            return AllBlocks.BRASS_CASING.asItem();
        }
        return AllBlocks.RAILWAY_CASING.asItem();
    }

    // ------------------------------------------------------------------

    // ------------------------------------------------------------------

    private void add(List<CompletableFuture<?>> futures, CachedOutput cache, JsonObject recipe) {
        ResourceLocation id = ResourceLocation.parse(recipe.remove("_name").getAsString());
        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(Greate.MOD_ID + "/recipe/" + id.getPath() + ".json");
        futures.add(DataProvider.saveStable(cache, recipe, path));
    }

    private static JsonObject shaped(String name, String[] pattern, Map<Character, JsonObject> keys,
                                     ItemStack result) {
        JsonObject json = base("minecraft:crafting_shaped", name);
        JsonArray patternJson = new JsonArray();
        for (String row : pattern) {
            patternJson.add(row);
        }
        json.add("pattern", patternJson);
        JsonObject keyJson = new JsonObject();
        keys.forEach((character, ingredient) -> keyJson.add(String.valueOf(character), ingredient));
        json.add("key", keyJson);
        json.add("result", result(result));
        return json;
    }

    private static JsonObject shapeless(String name, List<JsonObject> ingredients, ItemStack result) {
        JsonObject json = base("minecraft:crafting_shapeless", name);
        JsonArray array = new JsonArray();
        ingredients.forEach(array::add);
        json.add("ingredients", array);
        json.add("result", result(result));
        return json;
    }

    private static JsonObject base(String type, String name) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.addProperty("category", "misc");

        json.addProperty("_name", "crafting/" + name);
        return json;
    }

    private static JsonObject result(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("id", id(stack).toString());
        if (stack.getCount() > 1) {
            json.addProperty("count", stack.getCount());
        }
        return json;
    }

    private static JsonObject item(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", id(stack).toString());
        return json;
    }

    private static JsonObject item(net.minecraft.world.item.Item item) {
        JsonObject json = new JsonObject();
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
        return json;
    }

    private static JsonObject tag(String tag) {
        JsonObject json = new JsonObject();
        json.addProperty("tag", tag);
        return json;
    }

    private static ResourceLocation id(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    @Override
    public String getName() {
        return "Greate Crafting Recipes";
    }
}
