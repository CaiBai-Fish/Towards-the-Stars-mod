package top.cai_baif_fish.towards_the_stars.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModItems;
import top.cai_baif_fish.towards_the_stars.registry.ModPotions;

/**
 * 酿造配方，移植自 kubejs/startup_scripts/src/misc.js 的
 * MoreJSEvents.registerPotionBrewing 部分。
 *
 * 1.21 的酿造表在启动时由 PotionBrewing.bootstrap 一次性构建，NeoForge 为此提供了
 * RegisterBrewingRecipesEvent（game bus），通过 PotionBrewing.Builder 追加：
 *   addMix(输入药水, 材料, 输出药水)          <- MoreJS 的 addPotionBrewing
 *   addRecipe(输入 Ingredient, 材料 Ingredient, 输出 ItemStack)
 *                                             <- MoreJS 的 addCustomBrewing
 *
 * 关于可选依赖：脚本里的 twilightforest 材料在 1.21.1 整合包里不一定存在，
 * 因此按资源 ID 查找，取到空气就跳过该配方，不产生硬依赖。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID)
public final class ModBrewing {
    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        // 抗龙息药水：e.addPotionBrewing(材料, 输入, 输出)
        builder.addMix(Potions.AWKWARD, Items.GOLDEN_APPLE, ModPotions.DRAGON_BREATH_RESISTANCE);
        builder.addMix(ModPotions.DRAGON_BREATH_RESISTANCE, Items.REDSTONE, ModPotions.LONG_DRAGON_BREATH_RESISTANCE);
        builder.addMix(ModPotions.LONG_DRAGON_BREATH_RESISTANCE, Items.GOLDEN_APPLE, ModPotions.STRONG_DRAGON_BREATH_RESISTANCE);

        // 水瓶 + 龙息 -> 空的破伤药水
        //   e.addCustomBrewing('dragon_breath', Ingredient.customNBT("minecraft:potion", Potion == "minecraft:water"), 'ctts:bottle')
        // Ingredient.of(ItemStack) 在 1.21 里会带上组件谓词，正好精确匹配"水瓶"。
        builder.addRecipe(
                Ingredient.of(PotionContents.createItemStack(Items.POTION, Potions.WATER)),
                Ingredient.of(Items.DRAGON_BREATH),
                new ItemStack(ModItems.BOTTLE.get()));

        // 各 Boss 对应的破伤药水变体：材料 + 破伤药水 -> 带 CustomModelData 与名称的破伤药水
        // 编号与 creativetab.js 的 bottle 变体一致（CustomModelData 1..9），
        // 脚本只给了其中 7 个酿造配方（warden / wither 没有）。
        tierRecipe(builder, "twilightforest:steeleaf_ingot", 1, "naga");
        tierRecipe(builder, "twilightforest:naga_scale", 2, "lich");
        tierRecipe(builder, "minecraft:red_mushroom_block", 3, "minoshroom");
        tierRecipe(builder, "twilightforest:armor_shard", 4, "knight_phantom");
        tierRecipe(builder, "twilightforest:arctic_fur", 5, "alpha_yeti");
        tierRecipe(builder, "twilightforest:carminite", 6, "ur_ghast");
        tierRecipe(builder, "minecraft:fire_charge", 7, "snow_queen");
    }

    private static void tierRecipe(PotionBrewing.Builder builder, String ingredientId, int modelData, String tier) {
        Item ingredient = BuiltInRegistries.ITEM.get(ResourceLocation.parse(ingredientId));
        if (ingredient == Items.AIR) {
            // 该材料所属的模组未安装：跳过这条配方
            return;
        }
        ItemStack output = new ItemStack(ModItems.BOTTLE.get());
        output.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(modelData));
        // 对应脚本里的 display.Name（translatable，不带斜体）
        output.set(DataComponents.ITEM_NAME,
                Component.translatable("item." + Towards_the_stars.MODID + ".bottle." + tier));
        builder.addRecipe(Ingredient.of(ModItems.BOTTLE.get()), Ingredient.of(ingredient), output);
    }

    private ModBrewing() {
    }
}
