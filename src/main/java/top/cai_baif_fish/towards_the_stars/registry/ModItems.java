package top.cai_baif_fish.towards_the_stars.registry;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.item.BottleItem;
import top.cai_baif_fish.towards_the_stars.item.EndCakeItem;
import top.cai_baif_fish.towards_the_stars.item.TooltipItem;

import java.util.List;

/**
 * 物品注册，移植自整合包的 KubeJS 启动脚本：
 * kubejs/startup_scripts/src/registry/item.js（源环境 1.20.1 Forge + KubeJS 2001）。
 *
 * 迁移约定：
 * - 原脚本的 ctts: 命名空间统一改为本模组的 towards_the_stars:（按项目约定）。
 * - KubeJS 的 'create:sequenced_assembly' 类型在 Java 侧对应 Create 的 SequencedAssemblyItem
 *   （构造时会强制 stacksTo(1)，并自带加工进度条）。
 * - KubeJS 的 .tag(...) 不能在注册表里表达，物品标签见 data/c/tags/item/*.json；
 *   .burnTime(...) 见 ModFuelHandler。
 */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Towards_the_stars.MODID);

    // ------------------------------------------------------------------
    // 构件（rarity: rare）
    // ------------------------------------------------------------------
    public static final DeferredItem<Item> NETHER_MECHANISM = rare("nether_mechanism");
    public static final DeferredItem<Item> END_MECHANISM = rare("end_mechanism");
    public static final DeferredItem<Item> TWILIGHT_MECHANISM = rare("twilight_mechanism");

    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_NETHER_MECHANISM = assemblyRare("incomplete_nether_mechanism");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_END_MECHANISM = assemblyRare("incomplete_end_mechanism");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_TWILIGHT_MECHANISM = assemblyRare("incomplete_twilight_mechanism");

    // ------------------------------------------------------------------
    // 手锯（KubeJS 只设置了耐久，并不是工具类物品）
    // ------------------------------------------------------------------
    public static final DeferredItem<Item> STONE_SAW = durable("stone_saw", 131);
    public static final DeferredItem<Item> IRON_SAW = durable("iron_saw", 250);
    public static final DeferredItem<Item> DIAMOND_SAW = durable("diamond_saw", 1561);
    public static final DeferredItem<Item> NETHERITE_SAW = durable("netherite_saw", 2031);

    // ------------------------------------------------------------------
    // 锻造模板
    // ------------------------------------------------------------------
    public static final DeferredItem<SmithingTemplateItem> BASE_TEMPLATE = smithingTemplate("base_template", "None", "None");
    public static final DeferredItem<SmithingTemplateItem> DIAMOND_TEMPLATE = smithingTemplate("diamond_template", "Diamond Equipment", "Diamond");

    // ------------------------------------------------------------------
    // 火箭部件
    // ------------------------------------------------------------------
    public static final DeferredItem<Item> TIER_1_ROCKET_OUTERWALL = simple("tier_1_rocket_outerwall");
    public static final DeferredItem<Item> TIER_2_ROCKET_OUTERWALL = simple("tier_2_rocket_outerwall");
    public static final DeferredItem<Item> TIER_3_ROCKET_OUTERWALL = simple("tier_3_rocket_outerwall");
    public static final DeferredItem<Item> TIER_4_ROCKET_OUTERWALL = simple("tier_4_rocket_outerwall");
    public static final DeferredItem<Item> TIER_1_ROCKET_FRAME = simple("tier_1_rocket_frame");
    public static final DeferredItem<Item> TIER_2_ROCKET_FRAME = simple("tier_2_rocket_frame");
    public static final DeferredItem<Item> TIER_3_ROCKET_FRAME = simple("tier_3_rocket_frame");
    public static final DeferredItem<Item> TIER_4_ROCKET_FRAME = simple("tier_4_rocket_frame");

    // ------------------------------------------------------------------
    // 矿物材料
    //   crimson_crystal      : rarity uncommon, tag c:gems, burnTime 48000
    //   crimson_crystal_girt : tag c:dusts, burnTime 3200
    // ------------------------------------------------------------------
    public static final DeferredItem<Item> CRIMSON_CRYSTAL = ITEMS.registerSimpleItem("crimson_crystal",
            new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> CRIMSON_CRYSTAL_GIRT = simple("crimson_crystal_girt");

    // ------------------------------------------------------------------
    // 符文
    // ------------------------------------------------------------------
    public static final DeferredItem<Item> RUNE_FLAME = ITEMS.registerSimpleItem("rune_flame",
            new Item.Properties().fireResistant().stacksTo(1));

    // ------------------------------------------------------------------
    // 机壳类半成品（create:sequenced_assembly）
    // ------------------------------------------------------------------
    public static final DeferredItem<SequencedAssemblyItem> CASING_TEMPLATE = assembly("casing_template");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CASING_TEMPLATE = assembly("incomplete_casing_template");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_OSTRUM_CASING = assembly("incomplete_ostrum_casing");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CALORITE_CASING = assembly("incomplete_calorite_casing");

    // 安山机壳
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_PORTABLE_STORAGE_INTERFACE = assembly("incomplete_portable_storage_interface");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MECHANICAL_PISTON = assembly("incomplete_mechanical_piston");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_GANTRY_CARRIAGE = assembly("incomplete_gantry_carriage");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_ROPE_PULLEY = assembly("incomplete_rope_pulley");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CHAIN_CONVEYOR = assembly("incomplete_chain_conveyor");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_SCHEMATICANNON = assembly("incomplete_schematicannon");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_ROLLING_MILL = assembly("incomplete_rolling_mill");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MECHANICAL_EXTRUDER = assembly("incomplete_mechanical_extruder");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MECHANICAL_MIXER = assembly("incomplete_mechanical_mixer");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CART_ASSEMBLER = assembly("incomplete_cart_assembler");
    // 铜机壳
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_FLUID_TANK = assembly("incomplete_fluid_tank");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_HOSE_PULLEY = assembly("incomplete_hose_pulley");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_PORTABLE_FLUID_INTERFACE = assembly("incomplete_portable_fluid_interface");
    // 黄铜机壳
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CLOCKWORK_BEARING = assembly("incomplete_clockwork_bearing");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_ELEVATOR_PULLEY = assembly("incomplete_elevator_pulley");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MECHANICAL_CRAFTER = assembly("incomplete_mechanical_crafter");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_SEQUENCED_GEARSHIFT = assembly("incomplete_sequenced_gearshift");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CONTROLS = assembly("incomplete_controls");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CONTENT_OBSERVER = assembly("incomplete_content_observer");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_DISPLAY_LINK = assembly("incomplete_display_link");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_PORTABLE_ENERGY_INTERFACE = assembly("incomplete_portable_energy_interface");
    // 钢机壳
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_ETRIONIC_BLAST_FURNACE = assembly("incomplete_etrionic_blast_furnace");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_FUEL_REFINERY = assembly("incomplete_fuel_refinery");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_OXYGEN_LOADER = assembly("incomplete_oxygen_loader");
    // 戴斯机壳
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_SOLAR_PANEL = assembly("incomplete_solar_panel");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_OXYGEN_DISTRIBUTOR = assembly("incomplete_oxygen_distributor");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_GRAVITY_NORMALIZER = assembly("incomplete_gravity_normalizer");
    // 紫金机壳
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_ENERGIZER = assembly("incomplete_energizer");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_CRYO_FREEZER = assembly("incomplete_cryo_freezer");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_OXYGEN_SENSOR = assembly("incomplete_oxygen_sensor");
    // 其他
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_ANCIENT_DEBRIS = assembly("incomplete_ancient_debris");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MILLSTONE = assembly("incomplete_millstone");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_STEAM_ENGINE = assembly("incomplete_steam_engine");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_STICKER = assembly("incomplete_sticker");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_REDSTONE_CONTACT = assembly("incomplete_redstone_contact");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MAGMA_BLOCK_PRO = assembly("incomplete_magma_block_pro");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_PHANTOM_MEMBRANE = assembly("incomplete_phantom_membrane");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_MAGIC_MAP_FOCUS = assembly("incomplete_magic_map_focus");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_SMART_CHUTE = assembly("incomplete_smart_chute");
    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_PACKAGER = assembly("incomplete_packager");

    public static final DeferredItem<SequencedAssemblyItem> AMMO = assembly("ammo");

    // ------------------------------------------------------------------
    // 食物
    //   end_cake 原本在吃完后通过 GameStages(AStages) 给玩家解锁末地阶段；
    //   源脚本依赖 astages 模组的 KubeJS 集成，1.21.1 侧没有对应 API，
    //   这里只保留食物属性，阶段逻辑待有替代方案后再补。
    // ------------------------------------------------------------------
    public static final DeferredItem<SequencedAssemblyItem> END_CAKE_BASE = assembly("end_cake_base",
            new Item.Properties().food(new FoodProperties.Builder()
                    .fast()
                    .nutrition(10)
                    .saturationModifier(1.4F)
                    .build()));
    // 吃完后会给玩家解锁末地阶段（见 EndCakeItem / ModAStagesIntegration）
    public static final DeferredItem<EndCakeItem> END_CAKE = ITEMS.registerItem("end_cake",
            EndCakeItem::new, new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEdible()
                    .fast()
                    .nutrition(10)
                    .saturationModifier(1.4F)
                    .build()));
    public static final DeferredItem<Item> BLACK_BREAD = ITEMS.registerSimpleItem("black_bread",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(4.0F)
                    .effect(new MobEffectInstance(MobEffects.HUNGER, 300, 1), 0.3F)
                    .build()));

    // ------------------------------------------------------------------
    // 杂项
    // ------------------------------------------------------------------
    public static final DeferredItem<Item> STONE_ROD = simple("stone_rod");
    public static final DeferredItem<Item> DRILL_HEAD = simple("drill_head");
    public static final DeferredItem<Item> SAW_BLADE = simple("saw_blade");
    public static final DeferredItem<Item> PLOUGH = simple("plough");
    public static final DeferredItem<Item> HARVESTER = simple("harvester");
    public static final DeferredItem<Item> ROTOR = simple("rotor");
    public static final DeferredItem<Item> MAGNET = simple("magnet");
    public static final DeferredItem<Item> DIAMOND_NUGGET = simple("diamond_nugget");
    public static final DeferredItem<Item> EXTINGUISHED_BLAZE_ROD = simple("extinguished_blaze_rod");

    public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_HEART_OF_THE_DEEP = assemblyRare("incomplete_heart_of_the_deep");

    // 枪械指南：tooltip + rarity uncommon + 不可堆叠
    public static final DeferredItem<TooltipItem> GUN_BOOK = ITEMS.registerItem("gun_book",
            properties -> new TooltipItem(properties, "tooltip.towards_the_stars.gun_book"),
            new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));

    public static final DeferredItem<Item> TWILIGHT_KEY = rare("twilight_key");
    public static final DeferredItem<Item> KEY_FRAGMENTS = simple("key_fragments");
    public static final DeferredItem<Item> NETHER_BOOK = rare("nether_book");
    public static final DeferredItem<Item> BLAZING_SHARD = ITEMS.registerSimpleItem("blazing_shard",
            new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<Item> ENDER_CORE = rare("ender_core");
    public static final DeferredItem<Item> PROJECTION_TOOL = ITEMS.registerSimpleItem("projection_tool",
            new Item.Properties().rarity(Rarity.EPIC));

    // ------------------------------------------------------------------
    // 破伤药水（可投掷，投掷物实体见 entity/BottleProjectile）
    // ------------------------------------------------------------------
    public static final DeferredItem<BottleItem> BOTTLE = ITEMS.registerItem("bottle",
            BottleItem::new, new Item.Properties().stacksTo(1));

    // ------------------------------------------------------------------
    // 注册辅助
    // ------------------------------------------------------------------
    private static DeferredItem<Item> simple(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    private static DeferredItem<Item> rare(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties().rarity(Rarity.RARE));
    }

    private static DeferredItem<Item> durable(String name, int durability) {
        return ITEMS.registerSimpleItem(name, new Item.Properties().durability(durability));
    }

    private static DeferredItem<SequencedAssemblyItem> assembly(String name) {
        return assembly(name, new Item.Properties());
    }

    private static DeferredItem<SequencedAssemblyItem> assemblyRare(String name) {
        return assembly(name, new Item.Properties().rarity(Rarity.RARE));
    }

    private static DeferredItem<SequencedAssemblyItem> assembly(String name, Item.Properties properties) {
        return ITEMS.registerItem(name, SequencedAssemblyItem::new, properties);
    }

    /**
     * KubeJS 的 smithing_template 类型：只设置了 applies_to / ingredients 两段文本，
     * 槽位图标列表在 KubeJS 侧默认为空，这里保持一致。
     * upgradeDescription 是 1.21 新加的构造参数，KubeJS 没有对应项，按空文本处理。
     */
    private static DeferredItem<SmithingTemplateItem> smithingTemplate(String name, String appliesTo, String ingredients) {
        String keyBase = "item." + Towards_the_stars.MODID + "." + name;
        return ITEMS.register(name, () -> new SmithingTemplateItem(
                Component.translatableWithFallback(keyBase + ".applies_to", appliesTo),
                Component.translatableWithFallback(keyBase + ".ingredients", ingredients),
                Component.empty(),
                Component.translatableWithFallback(keyBase + ".base_slot_description", ""),
                Component.translatableWithFallback(keyBase + ".additions_slot_description", ""),
                List.<ResourceLocation>of(),
                List.<ResourceLocation>of()));
    }

    private ModItems() {
    }
}
