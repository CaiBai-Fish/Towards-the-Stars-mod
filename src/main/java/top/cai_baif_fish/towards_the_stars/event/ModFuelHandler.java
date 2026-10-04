package top.cai_baif_fish.towards_the_stars.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModBlocks;
import top.cai_baif_fish.towards_the_stars.registry.ModItems;

/**
 * 燃料值，移植自 KubeJS 的 .burnTime(...) / ItemEvents.modification：
 *   crimson_crystal       48000
 *   crimson_crystal_girt   3200
 *   crimson_crystal_block 96000
 *   createaddition:diamond_grit 1600（改的是别的 mod 的物品，用 ID 比较，避免硬依赖）
 *   minecraft:diamond           1600
 *
 * 1.21.1 里没有“在注册表项上写燃料值”的 API，Vanilla/NeoForge 的燃料值由
 * FurnaceFuelBurnTimeEvent 提供，因此统一在这里处理。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID)
public final class ModFuelHandler {
    private static final ResourceLocation CREATE_ADDITION_DIAMOND_GRIT =
            ResourceLocation.fromNamespaceAndPath("createaddition", "diamond_grit");

    @SubscribeEvent
    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(ModItems.CRIMSON_CRYSTAL.get())) {
            event.setBurnTime(48000);
            return;
        }
        if (stack.is(ModItems.CRIMSON_CRYSTAL_GIRT.get())) {
            event.setBurnTime(3200);
            return;
        }
        if (stack.is(ModBlocks.CRIMSON_CRYSTAL_BLOCK_ITEM.get())) {
            event.setBurnTime(96000);
            return;
        }
        if (stack.is(Items.DIAMOND)) {
            event.setBurnTime(1600);
            return;
        }
        if (BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(CREATE_ADDITION_DIAMOND_GRIT)) {
            event.setBurnTime(1600);
        }
    }

    private ModFuelHandler() {
    }
}
