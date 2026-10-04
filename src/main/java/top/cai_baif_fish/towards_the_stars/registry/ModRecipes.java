package top.cai_baif_fish.towards_the_stars.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.recipe.SawingRecipe;
import top.cai_baif_fish.towards_the_stars.recipe.SawingRecipeSerializer;

/**
 * 配方序列化器注册。
 *
 * 目前只有 sawing 一种：对应 KubeJS 的 kubejs:shapeless + ingredient action（锯子掉耐久）。
 */
public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Towards_the_stars.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SawingRecipe>> SAWING =
            RECIPE_SERIALIZERS.register("sawing", SawingRecipeSerializer::new);

    private ModRecipes() {
    }
}
