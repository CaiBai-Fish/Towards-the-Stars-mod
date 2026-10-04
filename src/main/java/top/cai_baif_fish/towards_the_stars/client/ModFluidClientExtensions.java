package top.cai_baif_fish.towards_the_stars.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModFluids;

/**
 * 流体的客户端渲染信息。
 *
 * 与 KubeJS 的 thickTexture 做法一致：所有熔融流体共用同一张贴图，靠 tint 上色
 * （KubeJS 用的是 kubejs:block/thick_fluid_still / _flow）。
 *
 * 颜色 = 脚本里 thickTexture / bucketColor 的入参，补上不透明的 alpha。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModFluidClientExtensions {
    private static final ResourceLocation STILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "block/thick_fluid_still");
    private static final ResourceLocation FLOWING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "block/thick_fluid_flow");

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new MoltenFluid(ModFluids.COLOR_MELTED_ANDESITE), ModFluids.MELTED_ANDESITE_TYPE.get());
        event.registerFluidType(new MoltenFluid(ModFluids.COLOR_MELTED_CALCITE), ModFluids.MELTED_CALCITE_TYPE.get());
        event.registerFluidType(new MoltenFluid(ModFluids.COLOR_MELTED_DIORITE), ModFluids.MELTED_DIORITE_TYPE.get());
        event.registerFluidType(new MoltenFluid(ModFluids.COLOR_MELTED_GRANITE), ModFluids.MELTED_GRANITE_TYPE.get());
        event.registerFluidType(new MoltenFluid(ModFluids.COLOR_MELTED_TUFF), ModFluids.MELTED_TUFF_TYPE.get());
        event.registerFluidType(new MoltenFluid(ModFluids.COLOR_END_MECHANISM_MELT), ModFluids.END_MECHANISM_MELT_TYPE.get());
        event.registerFluidType(new MoltenFluid(ModFluids.COLOR_TWILIGHT_MECHANISM_MELT), ModFluids.TWILIGHT_MECHANISM_MELT_TYPE.get());
    }

    private record MoltenFluid(int color) implements IClientFluidTypeExtensions {
        @Override
        public ResourceLocation getStillTexture() {
            return STILL_TEXTURE;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return FLOWING_TEXTURE;
        }

        @Override
        public int getTintColor() {
            return 0xFF000000 | color;
        }
    }

    private ModFluidClientExtensions() {
    }
}
