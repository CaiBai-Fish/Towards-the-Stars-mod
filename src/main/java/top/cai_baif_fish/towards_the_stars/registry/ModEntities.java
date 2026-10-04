package top.cai_baif_fish.towards_the_stars.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.entity.BottleProjectile;
import top.cai_baif_fish.towards_the_stars.entity.WarpedGolem;

/**
 * 实体类型注册，移植自 kubejs/startup_scripts/src/registry/entities/。
 */
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Towards_the_stars.MODID);

    /**
     * .sized(0.5, 0.5)：实体碰撞箱 0.5x0.5。
     * 脚本没有指定 clientTrackingRange / updateInterval，这里沿用 Vanilla 投掷物
     * （雪球、鸡蛋）的 4 / 10，属于同尺寸投掷物的常规取值。
     */
    public static final DeferredHolder<EntityType<?>, EntityType<BottleProjectile>> BOTTLE =
            ENTITY_TYPES.register("bottle", () -> EntityType.Builder
                    .<BottleProjectile>of(BottleProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("bottle"));

    /**
     * .mobCategory('monster') -> MONSTER；.sized(2, 4)；.clientTrackingRange(32)；.updateInterval(2)；
     * .immuneTo('minecraft:lava') -> immuneTo(Blocks.LAVA)；.fireImmune(true) -> fireImmune()。
     * 属性、生成放置与生物群系生成分别见 ModEntityAttributes 与
     * data/towards_the_stars/neoforge/biome_modifier/warped_golem_spawn.json。
     */
    public static final DeferredHolder<EntityType<?>, EntityType<WarpedGolem>> WARPED_GOLEM =
            ENTITY_TYPES.register("warped_golem", () -> EntityType.Builder
                    .<WarpedGolem>of(WarpedGolem::new, MobCategory.MONSTER)
                    .sized(2.0F, 4.0F)
                    .clientTrackingRange(32)
                    .updateInterval(2)
                    .fireImmune()
                    .immuneTo(Blocks.LAVA)
                    .build("warped_golem"));

    private ModEntities() {
    }
}
