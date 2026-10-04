package top.cai_baif_fish.towards_the_stars.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import top.cai_baif_fish.towards_the_stars.registry.ModEntities;
import top.cai_baif_fish.towards_the_stars.registry.ModItems;

import java.util.List;
import java.util.Set;

/**
 * 破伤药水投掷物，移植自 kubejs/startup_scripts/src/registry/entities/bottle.js
 * （源类型 entityjs:geckolib_projectile）。
 *
 * 脚本行为与这里的对应关系：
 *   .sized(0.5, 0.5)                      -> EntityType.Builder.sized(0.5F, 0.5F)（见 ModEntities）
 *   .move(e -> e.setDeltaMovement(0, 1, 0)) -> 未迁移：该写法会清零水平速度，导致药水只会
 *                                             垂直上升（原脚本的 bug）。这里改用原版投掷物物理，
 *                                             见下方 getDefaultGravity 的说明。
 *   .onHitEntity(...)                     -> 命中生物后销毁；若命中脚本列出的 Boss 之一，
 *                                            且投掷物携带的 tier 与该 Boss 同名，就给目标加 UnProtected 标签
 *   .onHitBlock(...)                      -> 命中方块后销毁
 *   .item(item -> canThrow(true).unstackable()) -> BottleItem 的 use()（见 BottleItem）
 *
 * tier 的来源：脚本里 9 个 bottle 变体靠 CustomModelData 1..9 区分
 * （见 creativetab.js / misc.js），这里按同样的编号映射成 Boss 名。
 * 注意脚本的 Boss 列表有 10 项（多一个 hydra）而 bottle 只有 9 个变体，
 * 因此 hydra 这一项在当前数据下永远不会命中——按原样保留，不做增补。
 */
public class BottleProjectile extends ThrowableItemProjectile {
    /** 脚本里 boss_(mod, boss) 的 10 个目标；其余部分只看实体 ID 的 path。 */
    private static final Set<String> BOSS_PATHS = Set.of(
            "naga", "lich", "minoshroom", "knight_phantom", "alpha_yeti",
            "ur_ghast", "snow_queen", "hydra", "warden", "wither");

    /** CustomModelData 1..9 -> tier 名，顺序取自 creativetab.js。 */
    private static final List<String> TIERS = List.of(
            "naga", "lich", "minoshroom", "knight_phantom", "alpha_yeti",
            "ur_ghast", "snow_queen", "warden", "wither");

    private static final String UNPROTECTED_TAG = "UnProtected";

    public BottleProjectile(EntityType<? extends BottleProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BottleProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.BOTTLE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BOTTLE.get();
    }

    // 投掷物理完全交给原版 ThrowableProjectile，这里不覆盖 tick()。
    //
    // 原脚本的 .move(e -> e.setDeltaMovement(0, 1, 0)) 是整体替换速度向量，会把投掷方向的
    // 水平分量清零；我后来改成"只维持 y=0.25、保留 x/z"仍然是在 super.tick() 之后改写速度，
    // 和原版物理（阻力 0.99 + 重力 + 命中检测）互相打架，结果药水不管朝哪扔都往上飘。
    //
    // 原版投掷物（雪球、鸡蛋）的物理由 ThrowableProjectile.tick() 处理，它留出的唯一扩展点
    // 是 getDefaultGravity()：默认 0.03，值越小越飘。想要"魔法药水"的悬浮手感就覆盖它，
    // 而不是去改速度。

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide) {
            return;
        }
        breakEffect();
        if (result.getEntity() instanceof LivingEntity living) {
            String tier = this.tier();
            ResourceLocation targetId = BuiltInRegistries.ENTITY_TYPE.getKey(living.getType());
            if (tier != null && targetId != null
                    && BOSS_PATHS.contains(targetId.getPath())
                    && targetId.getPath().equals(tier)) {
                living.addTag(UNPROTECTED_TAG);
            }
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            breakEffect();
            this.discard();
        }
    }

    /**
     * 命中实体或方块时的破碎表现：玻璃碎裂音效 + 玻璃碎片粒子。
     * 只在服务端调用，音效与粒子由服务端广播给附近玩家。
     */
    private void breakEffect() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GLASS.defaultBlockState()),
                    this.getX(), this.getY(), this.getZ(), 48, 0.3D, 0.3D, 0.3D, 0.18D);
        }
    }

    /** 从投掷物保存的物品栈里读 CustomModelData，换算成 tier 名。 */
    private String tier() {
        CustomModelData modelData = this.getItem().get(DataComponents.CUSTOM_MODEL_DATA);
        if (modelData == null) {
            return null;
        }
        int index = modelData.value() - 1;
        return index >= 0 && index < TIERS.size() ? TIERS.get(index) : null;
    }
}
