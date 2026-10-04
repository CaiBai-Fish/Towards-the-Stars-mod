package top.cai_baif_fish.towards_the_stars.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModRecipes;

/**
 * "锯切"配方：等价的 KubeJS 写法是
 *   e.recipes.kubejs.shapeless(...).damageIngredient('ctts:saws') 之类
 * 导出的 JSON 里表现为 type = kubejs:shapeless 加上一段 kubejs:actions：
 *   { "type": "damage", "filter_ingredient": { "tag": "ctts:saws" }, "damage": 1 }
 *
 * KubeJS 的做法是注册自己的配方类型（ShapelessKubeJSRecipe / ShapedKubeJSRecipe），
 * 在合成格剩余物阶段调用 IngredientAction（DamageAction.transform(ItemStack, int, CraftingContainer)）
 * 把匹配的材料替换成扣过耐久的副本。这里用同样的位置实现：
 *
 *   - Recipe#getRemainingItems(CraftingInput) 就是"合成后留在合成格里的东西"，
 *     原版无序配方默认留空（材料被消耗），我们把锯子换成扣 1 点耐久的副本。
 *   - 复用 Vanilla 的 ShapelessRecipe 做匹配与产物计算，因此配方的 JSON 字段
 *     与 crafting_shapeless 完全一致，只是 type 换成 towards_the_stars:sawing。
 *   - getType() 走 CraftingRecipe 的默认实现（返回 RecipeType.CRAFTING），
 *     这样工作台查找配方时仍然能在 CRAFTING 分类下找到它们。
 *
 * 耐久耗尽时该槽位留空，等价于工具被用坏。
 */
public class SawingRecipe implements CraftingRecipe {
    /** 对应 data/towards_the_stars/tags/item/saws.json。 */
    public static final TagKey<Item> SAWS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "saws"));

    /** 与脚本里 "damage": 1 一致（38 个木板/机壳配方都是 1）。 */
    private static final int DAMAGE_PER_USE = 1;

    private final ShapelessRecipe base;

    public SawingRecipe(ShapelessRecipe base) {
        this.base = base;
    }

    public ShapelessRecipe base() {
        return base;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.base.matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return this.base.assemble(input, registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return this.base.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.base.getResultItem(registries);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.base.getIngredients();
    }

    @Override
    public CraftingBookCategory category() {
        return this.base.category();
    }

    @Override
    public String getGroup() {
        return this.base.getGroup();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAWING.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = this.base.getRemainingItems(input);
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty() || !stack.is(SAWS) || !stack.isDamageableItem()) {
                continue;
            }
            ItemStack damaged = stack.copy();
            int nextDamage = damaged.getDamageValue() + DAMAGE_PER_USE;
            if (nextDamage >= damaged.getMaxDamage()) {
                remaining.set(slot, ItemStack.EMPTY);
            } else {
                damaged.setDamageValue(nextDamage);
                remaining.set(slot, damaged);
            }
        }
        return remaining;
    }
}
