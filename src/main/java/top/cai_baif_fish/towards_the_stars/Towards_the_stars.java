package top.cai_baif_fish.towards_the_stars;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import top.cai_baif_fish.towards_the_stars.registry.ModBlocks;
import top.cai_baif_fish.towards_the_stars.registry.ModCreativeTabs;
import top.cai_baif_fish.towards_the_stars.registry.ModEffects;
import top.cai_baif_fish.towards_the_stars.registry.ModEntities;
import top.cai_baif_fish.towards_the_stars.registry.ModFluids;
import top.cai_baif_fish.towards_the_stars.registry.ModItems;
import top.cai_baif_fish.towards_the_stars.registry.ModPotions;
import top.cai_baif_fish.towards_the_stars.registry.ModRecipes;

/**
 * 模组主类。
 *
 * 这里曾经有 MDK 模板自带的示例内容（example_block / example_item / example_tab 与
 * Config），已按用户要求移除；所有实际内容都在 registry 包下，来自整合包 KubeJS 脚本的迁移。
 */
// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Towards_the_stars.MODID)
public class Towards_the_stars {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "towards_the_stars";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Towards_the_stars(IEventBus modEventBus) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Content ported from the modpack's KubeJS scripts (startup_scripts/src/registry and data/)
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Towards_the_stars) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Towards the Stars: common setup");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Towards the Stars: server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Towards the Stars: client setup");
        }
    }
}
