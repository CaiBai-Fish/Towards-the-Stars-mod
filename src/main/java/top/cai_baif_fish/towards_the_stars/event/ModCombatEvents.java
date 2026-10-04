package top.cai_baif_fish.towards_the_stars.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModBlocks;
import top.cai_baif_fish.towards_the_stars.registry.ModEffects;

import java.util.Set;

/**
 * 战斗与伤害事件，移植自 kubejs/server_scripts/src/events.js。
 *
 * 已迁移的三段：
 *   1. 熔融流体烫伤 + 高热岩浆块强化烫伤（脚本的 ServerEvents.tick 循环）
 *   2. 抗龙息药水对龙息伤害的免疫（EntityEvents.hurt）
 *   3. 十个 Boss 的"破防"机制（EntityEvents.hurt 列表形式）
 *
 * events.js 里另外两段依赖整合包特有模组，未迁移（见文件末尾说明）。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID)
public final class ModCombatEvents {
    /** 脚本用 ResourceKey.create(Registries.DAMAGE_TYPE, 'ctts:hot_floor_pro')。 */
    private static final ResourceKey<DamageType> HOT_FLOOR_PRO =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "hot_floor_pro"));

    /** 脚本的 whitelist_entity：这些实体不受熔融流体与高热岩浆块伤害。 */
    private static final Set<ResourceLocation> LAVA_WHITELIST = Set.of(
            ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "warped_golem"),
            ResourceLocation.withDefaultNamespace("magma_cube"));

    private static final ResourceLocation FROST_WALKER =
            ResourceLocation.withDefaultNamespace("frost_walker");

    private static final String UNPROTECTED_TAG = "UnProtected";

    /** 脚本 EntityEvents.hurt 列表里的十个 Boss。 */
    private static final Set<ResourceLocation> PROTECTED_BOSSES = Set.of(
            ResourceLocation.fromNamespaceAndPath("twilightforest", "naga"),
            ResourceLocation.fromNamespaceAndPath("twilightforest", "lich"),
            ResourceLocation.fromNamespaceAndPath("twilightforest", "minoshroom"),
            ResourceLocation.fromNamespaceAndPath("twilightforest", "knight_phantom"),
            ResourceLocation.fromNamespaceAndPath("twilightforest", "alpha_yeti"),
            ResourceLocation.fromNamespaceAndPath("twilightforest", "ur_ghast"),
            ResourceLocation.fromNamespaceAndPath("twilightforest", "snow_queen"),
            ResourceLocation.fromNamespaceAndPath("twilightforest", "hydra"),
            ResourceLocation.withDefaultNamespace("warden"),
            ResourceLocation.withDefaultNamespace("wither"));

    // ------------------------------------------------------------------
    // 1. 流体 / 岩浆块烫伤
    // ------------------------------------------------------------------

    /**
     * 对应脚本的 ServerEvents.tick：脚本用 `age % 20 != 0` 做节流，
     * 这里用 tickCount % 20，即每个实体每 20 tick 检查一次。
     */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity living) || living.level().isClientSide()) {
            return;
        }
        if (living.tickCount % 20 != 0 || isWhitelisted(living)) {
            return;
        }

        BlockPos pos = living.blockPosition();

        // 站在熔融流体里 -> 像岩浆一样灼伤
        if (isMoltenFluid(living.level().getBlockState(pos))) {
            // 脚本用的是 entity.lavaHurt()，该方法在 1.21 的 LivingEntity 上已经不存在，
            // 这里直接给等价的岩浆伤害（Vanilla 的岩浆伤害就是 4 点）。
            living.hurt(living.damageSources().lava(), 4.0F);
        }

        // 站在高热岩浆块上，且没有穿冰霜行者靴子 -> 3 点 hot_floor_pro 伤害
        if (living.level().getBlockState(pos.below()).is(ModBlocks.MAGMA_BLOCK_PRO.get())
                && frostWalkerLevel(living) == 0) {
            living.hurt(living.damageSources().source(HOT_FLOOR_PRO), 3.0F);
        }
    }

    /** 脚本判定：block.id 含 'ctts:melted_'（迁移后即 towards_the_stars:melted_*）。 */
    private static boolean isMoltenFluid(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id.getNamespace().equals(Towards_the_stars.MODID) && id.getPath().startsWith("melted_");
    }

    private static boolean isWhitelisted(LivingEntity living) {
        return LAVA_WHITELIST.contains(BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()));
    }

    /**
     * 脚本用 entity.getArmorSlots()[0]（靴子）的 frost_walker 附魔等级。
     * 这里按附魔的 ResourceLocation 比较，避免依赖 Enchantments 字段的具体类型。
     */
    private static int frostWalkerLevel(LivingEntity living) {
        ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
        ItemEnchantments enchantments = boots.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            boolean isFrostWalker = holder.unwrapKey()
                    .map(key -> key.location().equals(FROST_WALKER))
                    .orElse(false);
            if (isFrostWalker) {
                return enchantments.getLevel(holder);
            }
        }
        return 0;
    }

    // ------------------------------------------------------------------
    // 2 + 3. 伤害事件
    // ------------------------------------------------------------------

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        // 抗龙息药水：龙息（间接魔法伤害 + 区域效果云）完全免疫
        if (entity.hasEffect(ModEffects.DRAGON_BREATH_RESISTANCE)
                && source.is(DamageTypes.INDIRECT_MAGIC)
                && source.getDirectEntity() instanceof AreaEffectCloud
                && !isCreativePlayer(source.getEntity())) {
            event.setCanceled(true);
            return;
        }

        // Boss 破防：未被打上 UnProtected 时免疫一切伤害；
        // 打上之后也只接受玩家造成的伤害。创造模式玩家直接放行。
        if (isProtectedBoss(entity) && !isCreativePlayer(source.getEntity())) {
            boolean unprotected = entity.getTags().contains(UNPROTECTED_TAG);
            if (!unprotected || !(source.getEntity() instanceof Player)) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean isCreativePlayer(Entity entity) {
        return entity instanceof Player player && player.isCreative();
    }

    private static boolean isProtectedBoss(LivingEntity entity) {
        return PROTECTED_BOSSES.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }

    private ModCombatEvents() {
    }
}
