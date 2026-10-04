package top.cai_baif_fish.towards_the_stars.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;

import java.util.List;

/**
 * 创造模式物品栏，移植自 kubejs/startup_scripts/src/registry/creativetab.js。
 *
 * 脚本里只有一个标签 ctts:potion：内容是九种破伤药水，靠 CustomModelData 1..9 与
 * 各自的名称区分（编号与 item.js / misc.js 里的酿造配方一致）。
 *
 * 额外新增：ALL_ITEMS 标签列出本模组注册的全部物品，仅用于调试取用——
 * 这一项超出"同步"范围，是应要求补充的。
 */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Towards_the_stars.MODID);

    /** 与 creativetab.js 的九个变体同序，索引 + 1 即 CustomModelData。 */
    private static final List<String> BOTTLE_TIERS = List.of(
            "naga", "lich", "minoshroom", "knight_phantom", "alpha_yeti",
            "ur_ghast", "snow_queen", "warden", "wither");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> POTION = CREATIVE_MODE_TABS.register("potion",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Towards_the_stars.MODID + ".potion"))
                    .icon(() -> new ItemStack(Items.POTION))
                    .displayItems((parameters, output) -> {
                        for (int i = 0; i < BOTTLE_TIERS.size(); i++) {
                            output.accept(bottleStack(i + 1, BOTTLE_TIERS.get(i)));
                        }
                    })
                    .build());

    /** 额外新增的调试标签：遍历物品注册表列出全部内容。 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ALL_ITEMS = CREATIVE_MODE_TABS.register("all_items",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Towards_the_stars.MODID + ".all_items"))
                    .icon(() -> new ItemStack(ModItems.CRIMSON_CRYSTAL.get()))
                    .displayItems((parameters, output) -> {
                        for (DeferredHolder<Item, ? extends Item> holder : ModItems.ITEMS.getEntries()) {
                            output.accept(holder.get());
                        }
                    })
                    .build());

    /**
     * 与 ModBrewing 里产出的变体保持一致：CustomModelData + 自定义名称。
     */
    static ItemStack bottleStack(int modelData, String tier) {
        ItemStack stack = new ItemStack(ModItems.BOTTLE.get());
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(modelData));
        stack.set(DataComponents.ITEM_NAME,
                Component.translatable("item." + Towards_the_stars.MODID + ".bottle." + tier));
        return stack;
    }

    private ModCreativeTabs() {
    }
}
