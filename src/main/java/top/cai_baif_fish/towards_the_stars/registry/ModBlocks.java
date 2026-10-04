package top.cai_baif_fish.towards_the_stars.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.item.TooltipBlockItem;

/**
 * 方块注册，移植自整合包的 KubeJS 启动脚本：
 * kubejs/startup_scripts/src/registry/block.js（源环境 1.20.1 Forge + KubeJS 2001）。
 *
 * 关于 casing 方块：原脚本用的是 .material('metal')，但该 API 在 KubeJS 2001 已被废弃，
 * 启动日志里有 4 条一模一样的警告（logs/kubejs/startup.log#28-31）：
 *   blockBuilder.material(string) is no longer supported! Use .soundType(SoundType) and .mapColor(MapColor) instead!
 * 也就是说 material('metal') 被整行忽略，方块实际拿到的是 KubeJS 默认值：
 *   hardness 1.5 / resistance 3.0 / SoundType.WOOD / 无挖掘要求。
 * 这里严格按实际生效的语义移植，不额外加 needs_*_tool 之类的要求。
 */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Towards_the_stars.MODID);

    // ------------------------------------------------------------------
    // 机壳
    // ------------------------------------------------------------------
    public static final DeferredBlock<Block> STEEL_CASING = casing("steel_casing");
    public static final DeferredBlock<Block> DESH_CASING = casing("desh_casing");
    public static final DeferredBlock<Block> OSTRUM_CASING = casing("ostrum_casing");
    public static final DeferredBlock<Block> CALORITE_CASING = casing("calorite_casing");

    public static final DeferredItem<BlockItem> STEEL_CASING_ITEM = ModItems.ITEMS.registerSimpleBlockItem(STEEL_CASING);
    public static final DeferredItem<BlockItem> DESH_CASING_ITEM = ModItems.ITEMS.registerSimpleBlockItem(DESH_CASING);
    public static final DeferredItem<BlockItem> OSTRUM_CASING_ITEM = ModItems.ITEMS.registerSimpleBlockItem(OSTRUM_CASING);
    public static final DeferredItem<BlockItem> CALORITE_CASING_ITEM = ModItems.ITEMS.registerSimpleBlockItem(CALORITE_CASING);

    // ------------------------------------------------------------------
    // 控制核心：脚本只写了 .hardness(50)，resistance 仍是 KubeJS 默认的 3.0
    // （高硬度但抗爆很低，这是原脚本的实际行为，未擅自改动）
    // ------------------------------------------------------------------
    public static final DeferredBlock<Block> CONTROL_CORE = BLOCKS.registerSimpleBlock("control_core",
            BlockBehaviour.Properties.of()
                    .strength(50.0F, 3.0F)
                    .sound(SoundType.WOOD));

    public static final DeferredItem<BlockItem> CONTROL_CORE_ITEM = ModItems.ITEMS.registerSimpleBlockItem(CONTROL_CORE);

    // ------------------------------------------------------------------
    // 矿物
    // ------------------------------------------------------------------
    public static final DeferredBlock<Block> CRIMSON_CRYSTAL_ORE = BLOCKS.registerSimpleBlock("crimson_crystal_ore",
            BlockBehaviour.Properties.of()
                    .strength(20.0F, 1400.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD));

    public static final DeferredItem<BlockItem> CRIMSON_CRYSTAL_ORE_ITEM = ModItems.ITEMS.registerSimpleBlockItem(CRIMSON_CRYSTAL_ORE);

    public static final DeferredBlock<Block> CRIMSON_CRYSTAL_BLOCK = BLOCKS.registerSimpleBlock("crimson_crystal_block",
            BlockBehaviour.Properties.of()
                    .strength(1.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD));

    // 该方块的物品在脚本里额外设了 rarity uncommon 与 burnTime 96000（后者见 ModFuelHandler）
    public static final DeferredItem<BlockItem> CRIMSON_CRYSTAL_BLOCK_ITEM = ModItems.ITEMS.registerSimpleBlockItem(
            "crimson_crystal_block", CRIMSON_CRYSTAL_BLOCK, new Item.Properties().rarity(Rarity.UNCOMMON));

    // ------------------------------------------------------------------
    // 高热岩浆块：石质音效、jumpFactor 0.6、不允许刷怪、物品带 tooltip
    // ------------------------------------------------------------------
    public static final DeferredBlock<Block> MAGMA_BLOCK_PRO = BLOCKS.registerSimpleBlock("magma_block_pro",
            BlockBehaviour.Properties.of()
                    .strength(1.5F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .jumpFactor(0.6F)
                    .isValidSpawn((state, level, pos, entityType) -> false));

    public static final DeferredItem<TooltipBlockItem> MAGMA_BLOCK_PRO_ITEM = ModItems.ITEMS.registerItem(
            "magma_block_pro",
            properties -> new TooltipBlockItem(MAGMA_BLOCK_PRO.get(), properties, "tooltip.towards_the_stars.magma_block_pro"),
            new Item.Properties());

    // ------------------------------------------------------------------
    // 注册辅助：KubeJS 的方块默认带同名 BlockItem
    // ------------------------------------------------------------------
    private static DeferredBlock<Block> casing(String name) {
        return BLOCKS.registerSimpleBlock(name, BlockBehaviour.Properties.of()
                .strength(1.5F, 3.0F)
                .sound(SoundType.WOOD));
    }

    private ModBlocks() {
    }
}
