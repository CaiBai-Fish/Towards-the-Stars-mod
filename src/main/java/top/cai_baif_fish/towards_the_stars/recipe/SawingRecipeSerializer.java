package top.cai_baif_fish.towards_the_stars.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * SawingRecipe 的序列化器。
 *
 * 直接复用 Vanilla 无序配方的 MapCodec / StreamCodec，再用 xmap / map 包一层，
 * 于是 JSON 与网络同步格式都和 crafting_shapeless 完全一致，
 * 不需要自己维护一份编解码逻辑（也就不会有字段名对不上的风险）。
 */
public class SawingRecipeSerializer implements RecipeSerializer<SawingRecipe> {
    private static final ShapelessRecipe.Serializer BASE = new ShapelessRecipe.Serializer();

    private static final MapCodec<SawingRecipe> CODEC =
            BASE.codec().xmap(SawingRecipe::new, SawingRecipe::base);

    private static final StreamCodec<RegistryFriendlyByteBuf, SawingRecipe> STREAM_CODEC =
            ShapelessRecipe.Serializer.STREAM_CODEC.map(SawingRecipe::new, SawingRecipe::base);

    @Override
    public MapCodec<SawingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SawingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
