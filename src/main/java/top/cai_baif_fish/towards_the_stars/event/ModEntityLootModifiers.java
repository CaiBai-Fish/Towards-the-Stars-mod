package top.cai_baif_fish.towards_the_stars.event;

import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModItems;

/**
 * 实体掉落表的追加部分，移植自 kubejs/server_scripts/src/loot/entity_loot.js 里
 * 所有 e.modifyEntity(...) 的用法（在原表上追加 pool）。
 *
 * 脚本里 e.addEntity(...) 的 4 项是整表替换，用数据包表达更直接，见：
 *   data/minecraft/loot_table/entities/villager.json
 *   data/minecraft/loot_table/entities/warden.json
 *   data/ad_astra/loot_table/entities/lunarian.json
 *   data/towards_the_stars/loot_table/entities/warped_golem.json
 *
 * 1.21.1 的 API 变化（照抄 1.20.1 的写法会编译失败）：
 *   抢夺概率条件由 random_chance_with_looting(chance, looting_multiplier) 改为
 *   random_chance_with_enchanted_bonus。代码侧对应
 *   LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, chance, boost)，
 *   数据包侧的字段也换成了 enchantment + unenchanted_chance + enchanted_chance。
 *
 * hotfloorpro：脚本用的是伤害类型标签 minecraft:hotfloorpro，整合包里没有这个标签文件，
 * 而产出该伤害的 tick 逻辑在 misc.js 里本来就是注释掉的，所以这些条件目前不会成立——
 * 按原样保留，等该机制在 Java 侧实现后再生效。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID)
public final class ModEntityLootModifiers {
    private static final TagKey<DamageType> HOT_FLOOR_PRO =
            TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("hotfloorpro"));

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        HolderLookup.Provider registries = event.getRegistries();
        LootTable table = event.getTable();

        // 末影人：钥匙碎片（0-2 个，40% + 每级抢夺 4%，需玩家击杀）
        //          + supplementaries:enderman_head（被 hotfloorpro 击杀时 20%）
        if (isEntity(name, "enderman")) {
            table.addPool(pool(1)
                    .add(LootItem.lootTableItem(ModItems.KEY_FRAGMENTS.get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))
                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, 0.4F, 0.04F))
                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                    .build());
            addHeadPool(table, "supplementaries:enderman_head", 0.2F);
        }

        // 末影龙：钥匙碎片（需玩家击杀）
        if (isEntity(name, "ender_dragon")) {
            table.addPool(pool(1)
                    .add(LootItem.lootTableItem(ModItems.KEY_FRAGMENTS.get()))
                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                    .build());
        }

        // 苦力怕：苦力怕头颅（hotfloorpro 击杀时 20%）
        if (isEntity(name, "creeper")) {
            addHeadPool(table, "minecraft:creeper_head", 0.2F);
        }

        // 猪灵：猪灵头颅（hotfloorpro 击杀时必定掉落，脚本没有加概率条件）
        if (isEntity(name, "piglin")) {
            addHeadPool(table, "minecraft:piglin_head", -1.0F);
        }

        // 骷髅：骷髅头颅（hotfloorpro）+ 灵魂沙（12% + 每级抢夺 2.5%，需玩家击杀）
        if (isEntity(name, "skeleton")) {
            addHeadPool(table, "minecraft:skeleton_skull", -1.0F);
            table.addPool(soulSandPool(registries).build());
        }

        // 凋零骷髅：凋零骷髅头颅（hotfloorpro）
        if (isEntity(name, "wither_skeleton")) {
            addHeadPool(table, "minecraft:wither_skeleton_skull", -1.0F);
        }

        // 僵尸 / 僵尸村民 / 溺尸：灵魂沙
        if (isEntity(name, "zombie") || isEntity(name, "zombie_villager") || isEntity(name, "drowned")) {
            table.addPool(soulSandPool(registries).build());
        }
    }

    private static LootPool.Builder pool(float rolls) {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(rolls));
    }

    /** 灵魂沙：12% + 每级抢夺 2.5%，且需要玩家击杀。 */
    private static LootPool.Builder soulSandPool(HolderLookup.Provider registries) {
        return pool(1)
                .add(LootItem.lootTableItem(Items.SOUL_SAND))
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, 0.12F, 0.025F))
                .when(LootItemKilledByPlayerCondition.killedByPlayer());
    }

    /**
     * 头颅类掉落：条件是被 minecraft:hotfloorpro 伤害类型标签击杀。
     * chance &lt; 0 表示脚本没有指定概率（必定掉落）。
     * 物品按 ID 查找，取到空气（提供该头颅的模组未安装）时整条跳过。
     */
    private static void addHeadPool(LootTable table, String itemId, float chance) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item == Items.AIR) {
            return;
        }
        LootPool.Builder builder = pool(1)
                .add(LootItem.lootTableItem(item))
                .when(DamageSourceCondition.hasDamageSource(
                        DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(HOT_FLOOR_PRO))));
        if (chance >= 0.0F) {
            builder.when(LootItemRandomChanceCondition.randomChance(chance));
        }
        table.addPool(builder.build());
    }

    private static boolean isEntity(ResourceLocation lootTableName, String entityPath) {
        return lootTableName.equals(ResourceLocation.withDefaultNamespace("entities/" + entityPath));
    }

    private ModEntityLootModifiers() {
    }
}
