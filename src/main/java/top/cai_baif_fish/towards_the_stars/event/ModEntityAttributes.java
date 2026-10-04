package top.cai_baif_fish.towards_the_stars.event;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.entity.WarpedGolem;
import top.cai_baif_fish.towards_the_stars.registry.ModEntities;

/**
 * 实体属性与生成放置。
 *
 * 对应 warped_golem.js 的两处：
 *   EntityJSEvents.createAttributes: max_health 40 / attack_damage 13  -> EntityAttributeCreationEvent
 *   .spawnPlacement('on_ground', 'motion_blocking_no_leaves', () => true)
 *       -> RegisterSpawnPlacementsEvent（1.21 已移除 SpawnPlacements.register 静态方法）
 *
 * 生成谓词按脚本原样返回 true（不做光照/方块限制）。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEntityAttributes {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WARPED_GOLEM.get(), WarpedGolem.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.WARPED_GOLEM.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> true,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private ModEntityAttributes() {
    }
}
