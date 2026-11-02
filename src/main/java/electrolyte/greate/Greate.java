package electrolyte.greate;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mojang.logging.LogUtils;
import electrolyte.greate.foundation.datagen.GreateCraftingRecipeGen;
import electrolyte.greate.foundation.datagen.GreateCrushingRecipeGen;
import electrolyte.greate.foundation.datagen.GreateGtMachineRecipeGen;
import electrolyte.greate.foundation.datagen.GreateMillingRecipeGen;
import electrolyte.greate.foundation.datagen.GreateMixingRecipeGen;
import electrolyte.greate.foundation.datagen.GreatePressingRecipeGen;
import electrolyte.greate.foundation.datagen.GreateSawingRecipeGen;
import electrolyte.greate.infrastructure.config.GreateConfig;
import electrolyte.greate.registry.GreateRecipeTypes;
import electrolyte.greate.registry.Shafts;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

/**
 * Mod entry point.
 *
 * <p>Registration order matters: GT materials must exist before the tiered registries query
 * them, otherwise tiers are skipped with a warning instead of failing loudly.</p>
 */
@Mod(Greate.MOD_ID)
public class Greate {

    public static final String MOD_ID = "greate_remake";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static GTRegistrate GT_REGISTRATE;

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GREATE_TAB =
            CREATIVE_TABS.register("greate_remake", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.greate_remake"))
                    .icon(() -> new ItemStack(Shafts.ALL.isEmpty()
                            ? Items.IRON_INGOT
                            : Shafts.ALL.get(0).get().asItem()))
                    .displayItems(new GreateDisplayItems())
                    .build());

    public Greate(IEventBus modEventBus, ModContainer modContainer) {
        GT_REGISTRATE = GTRegistrate.create(MOD_ID);

        GT_REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

        modContainer.registerConfig(ModConfig.Type.SERVER, GreateConfig.SPEC);
        CREATIVE_TABS.register(modEventBus);
        GreateRecipeTypes.register(modEventBus);
        modEventBus.addListener(this::gatherData);

        LOGGER.info("[Greate] remake skeleton loaded (Create 6.0.10 / GTCEu 7.0.2)");

    }

    private void gatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            event.getGenerator().addProvider(true,
                    new GreateMillingRecipeGen(event.getGenerator().getPackOutput()));
            event.getGenerator().addProvider(true,
                    new GreatePressingRecipeGen(event.getGenerator().getPackOutput()));
            event.getGenerator().addProvider(true,
                    new GreateMixingRecipeGen(event.getGenerator().getPackOutput()));
            event.getGenerator().addProvider(true,
                    new GreateCrushingRecipeGen(event.getGenerator().getPackOutput()));
            event.getGenerator().addProvider(true,
                    new GreateSawingRecipeGen(event.getGenerator().getPackOutput()));

            event.getGenerator().addProvider(true,
                    new GreateCraftingRecipeGen(event.getGenerator().getPackOutput()));

            event.getGenerator().addProvider(true,
                    new GreateGtMachineRecipeGen(event.getGenerator().getPackOutput(), event.getLookupProvider()));
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
