package top.cai_baif_fish.towards_the_stars.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;

import java.util.function.Supplier;

/**
 * 流体注册，移植自 kubejs/startup_scripts/src/registry/fluid.js：
 *   melted_andesite / melted_calcite / melted_diorite / melted_granite / melted_tuff
 *       -> thickTexture(颜色) + bucketColor(颜色)，即：流体 + 流体方块 + 桶
 *   end_mechanism_melt / twilight_mechanism_melt
 *       -> thickTexture(颜色).noBucket().noBlock()，即：只有流体本身
 *
 * 颜色全部取自脚本里 thickTexture/bucketColor 的参数。
 * 源脚本没有指定任何物理属性（KubeJS 的 thickTexture 只影响渲染），这里的数值按
 * "熔融岩石/金属"的常规设定给出：粘稠、不能游泳、不能划船、不会自行生成水源。
 *
 * 渲染在 ModFluidClientExtensions 里通过 IClientFluidTypeExtensions 提供
 * （一张通用贴图 + 各自 tint，与 KubeJS 的 thick_fluid 贴图做法一致）。
 *
 * 注册表处理顺序由原版保证为 FLUID -> BLOCK -> ITEM，所以 LiquidBlock 引用 Source、
 * 桶物品引用 Fluid 都取得到已注册的实例。
 */
public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Towards_the_stars.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Towards_the_stars.MODID);

    // 脚本里写死的颜色
    public static final int COLOR_MELTED_ANDESITE = 0x88888C;
    public static final int COLOR_MELTED_CALCITE = 0xEDF2F1;
    public static final int COLOR_MELTED_DIORITE = 0xABACAD;
    public static final int COLOR_MELTED_GRANITE = 0xA26D57;
    public static final int COLOR_MELTED_TUFF = 0x696D6E;
    public static final int COLOR_END_MECHANISM_MELT = 0x60986E;
    public static final int COLOR_TWILIGHT_MECHANISM_MELT = 0x60B443;

    // ------------------------------------------------------------------
    // melted_andesite
    // ------------------------------------------------------------------
    public static final DeferredHolder<FluidType, FluidType> MELTED_ANDESITE_TYPE = fluidType("melted_andesite");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MELTED_ANDESITE = FLUIDS.register(
            "melted_andesite", () -> new BaseFlowingFluid.Source(meltedAndesiteProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> MELTED_ANDESITE_FLOWING = FLUIDS.register(
            "flowing_melted_andesite", () -> new BaseFlowingFluid.Flowing(meltedAndesiteProperties()));
    public static final DeferredHolder<Block, LiquidBlock> MELTED_ANDESITE_BLOCK = ModBlocks.BLOCKS.register(
            "melted_andesite", () -> new LiquidBlock(MELTED_ANDESITE.get(), liquidBlockProperties()));
    public static final DeferredItem<BucketItem> MELTED_ANDESITE_BUCKET =
            bucket("melted_andesite_bucket", MELTED_ANDESITE);

    // ------------------------------------------------------------------
    // melted_calcite
    // ------------------------------------------------------------------
    public static final DeferredHolder<FluidType, FluidType> MELTED_CALCITE_TYPE = fluidType("melted_calcite");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MELTED_CALCITE = FLUIDS.register(
            "melted_calcite", () -> new BaseFlowingFluid.Source(meltedCalciteProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> MELTED_CALCITE_FLOWING = FLUIDS.register(
            "flowing_melted_calcite", () -> new BaseFlowingFluid.Flowing(meltedCalciteProperties()));
    public static final DeferredHolder<Block, LiquidBlock> MELTED_CALCITE_BLOCK = ModBlocks.BLOCKS.register(
            "melted_calcite", () -> new LiquidBlock(MELTED_CALCITE.get(), liquidBlockProperties()));
    public static final DeferredItem<BucketItem> MELTED_CALCITE_BUCKET =
            bucket("melted_calcite_bucket", MELTED_CALCITE);

    // ------------------------------------------------------------------
    // melted_diorite
    // ------------------------------------------------------------------
    public static final DeferredHolder<FluidType, FluidType> MELTED_DIORITE_TYPE = fluidType("melted_diorite");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MELTED_DIORITE = FLUIDS.register(
            "melted_diorite", () -> new BaseFlowingFluid.Source(meltedDioriteProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> MELTED_DIORITE_FLOWING = FLUIDS.register(
            "flowing_melted_diorite", () -> new BaseFlowingFluid.Flowing(meltedDioriteProperties()));
    public static final DeferredHolder<Block, LiquidBlock> MELTED_DIORITE_BLOCK = ModBlocks.BLOCKS.register(
            "melted_diorite", () -> new LiquidBlock(MELTED_DIORITE.get(), liquidBlockProperties()));
    public static final DeferredItem<BucketItem> MELTED_DIORITE_BUCKET =
            bucket("melted_diorite_bucket", MELTED_DIORITE);

    // ------------------------------------------------------------------
    // melted_granite
    // ------------------------------------------------------------------
    public static final DeferredHolder<FluidType, FluidType> MELTED_GRANITE_TYPE = fluidType("melted_granite");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MELTED_GRANITE = FLUIDS.register(
            "melted_granite", () -> new BaseFlowingFluid.Source(meltedGraniteProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> MELTED_GRANITE_FLOWING = FLUIDS.register(
            "flowing_melted_granite", () -> new BaseFlowingFluid.Flowing(meltedGraniteProperties()));
    public static final DeferredHolder<Block, LiquidBlock> MELTED_GRANITE_BLOCK = ModBlocks.BLOCKS.register(
            "melted_granite", () -> new LiquidBlock(MELTED_GRANITE.get(), liquidBlockProperties()));
    public static final DeferredItem<BucketItem> MELTED_GRANITE_BUCKET =
            bucket("melted_granite_bucket", MELTED_GRANITE);

    // ------------------------------------------------------------------
    // melted_tuff
    // ------------------------------------------------------------------
    public static final DeferredHolder<FluidType, FluidType> MELTED_TUFF_TYPE = fluidType("melted_tuff");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MELTED_TUFF = FLUIDS.register(
            "melted_tuff", () -> new BaseFlowingFluid.Source(meltedTuffProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> MELTED_TUFF_FLOWING = FLUIDS.register(
            "flowing_melted_tuff", () -> new BaseFlowingFluid.Flowing(meltedTuffProperties()));
    public static final DeferredHolder<Block, LiquidBlock> MELTED_TUFF_BLOCK = ModBlocks.BLOCKS.register(
            "melted_tuff", () -> new LiquidBlock(MELTED_TUFF.get(), liquidBlockProperties()));
    public static final DeferredItem<BucketItem> MELTED_TUFF_BUCKET =
            bucket("melted_tuff_bucket", MELTED_TUFF);

    // ------------------------------------------------------------------
    // end_mechanism_melt：noBucket + noBlock
    // ------------------------------------------------------------------
    public static final DeferredHolder<FluidType, FluidType> END_MECHANISM_MELT_TYPE = fluidType("end_mechanism_melt");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> END_MECHANISM_MELT = FLUIDS.register(
            "end_mechanism_melt", () -> new BaseFlowingFluid.Source(endMechanismMeltProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> END_MECHANISM_MELT_FLOWING = FLUIDS.register(
            "flowing_end_mechanism_melt", () -> new BaseFlowingFluid.Flowing(endMechanismMeltProperties()));

    // ------------------------------------------------------------------
    // twilight_mechanism_melt：noBucket + noBlock
    // ------------------------------------------------------------------
    public static final DeferredHolder<FluidType, FluidType> TWILIGHT_MECHANISM_MELT_TYPE = fluidType("twilight_mechanism_melt");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> TWILIGHT_MECHANISM_MELT = FLUIDS.register(
            "twilight_mechanism_melt", () -> new BaseFlowingFluid.Source(twilightMechanismMeltProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> TWILIGHT_MECHANISM_MELT_FLOWING = FLUIDS.register(
            "flowing_twilight_mechanism_melt", () -> new BaseFlowingFluid.Flowing(twilightMechanismMeltProperties()));



    private static BaseFlowingFluid.Properties endMechanismMeltProperties() {
        return properties(END_MECHANISM_MELT_TYPE, END_MECHANISM_MELT, END_MECHANISM_MELT_FLOWING, null, null);
    }

    private static BaseFlowingFluid.Properties meltedAndesiteProperties() {
        return properties(MELTED_ANDESITE_TYPE, MELTED_ANDESITE, MELTED_ANDESITE_FLOWING, MELTED_ANDESITE_BLOCK, MELTED_ANDESITE_BUCKET);
    }

    private static BaseFlowingFluid.Properties meltedCalciteProperties() {
        return properties(MELTED_CALCITE_TYPE, MELTED_CALCITE, MELTED_CALCITE_FLOWING, MELTED_CALCITE_BLOCK, MELTED_CALCITE_BUCKET);
    }

    private static BaseFlowingFluid.Properties meltedDioriteProperties() {
        return properties(MELTED_DIORITE_TYPE, MELTED_DIORITE, MELTED_DIORITE_FLOWING, MELTED_DIORITE_BLOCK, MELTED_DIORITE_BUCKET);
    }

    private static BaseFlowingFluid.Properties meltedGraniteProperties() {
        return properties(MELTED_GRANITE_TYPE, MELTED_GRANITE, MELTED_GRANITE_FLOWING, MELTED_GRANITE_BLOCK, MELTED_GRANITE_BUCKET);
    }

    private static BaseFlowingFluid.Properties meltedTuffProperties() {
        return properties(MELTED_TUFF_TYPE, MELTED_TUFF, MELTED_TUFF_FLOWING, MELTED_TUFF_BLOCK, MELTED_TUFF_BUCKET);
    }

    private static BaseFlowingFluid.Properties twilightMechanismMeltProperties() {
        return properties(TWILIGHT_MECHANISM_MELT_TYPE, TWILIGHT_MECHANISM_MELT, TWILIGHT_MECHANISM_MELT_FLOWING, null, null);
    }

    // ------------------------------------------------------------------
    // 辅助
    // ------------------------------------------------------------------

    private static DeferredHolder<FluidType, FluidType> fluidType(String name) {
        return FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create()
                .descriptionId("fluid." + Towards_the_stars.MODID + "." + name)
                .density(3000)
                .viscosity(6000)
                .canDrown(false)
                .canSwim(false)
                .supportsBoating(false)
                .canConvertToSource(false)));
    }

    private static BaseFlowingFluid.Properties properties(Supplier<? extends FluidType> type,
                                                          Supplier<? extends Fluid> source,
                                                          Supplier<? extends Fluid> flowing,
                                                          Supplier<? extends LiquidBlock> block,
                                                          Supplier<? extends Item> bucket) {
        BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(type, source, flowing);
        if (block != null) {
            properties.block(block);
        }
        if (bucket != null) {
            properties.bucket(bucket);
        }
        return properties;
    }

    private static BlockBehaviour.Properties liquidBlockProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WATER)
                .replaceable()
                .noCollission()
                .strength(100.0F)
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid()
                .sound(SoundType.EMPTY);
    }

    private static DeferredItem<BucketItem> bucket(String name, Supplier<? extends Fluid> fluid) {
        return ModItems.ITEMS.register(name, () -> new BucketItem(fluid.get(),
                new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    }

    private ModFluids() {
    }
}
