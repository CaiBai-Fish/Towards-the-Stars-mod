package top.cai_baif_fish.towards_the_stars.compat;

import com.alessandro.astages.api.holder.AHolder;
import com.alessandro.astages.api.util.AStagesUtils;
import com.alessandro.astages.engine.ASimpleRestrictionManager;
import com.alessandro.astages.engine.store.ASimpleRestrictionTypes;
import net.minecraft.world.entity.player.Player;

/**
 * AStages 兼容层。
 *
 * AStages 在本项目里是**可选依赖**（build.gradle 里是 compileOnly），
 * 所以所有对 AStages 类的引用都集中在这一个类里，
 * 调用方必须先通过 ModAStagesIntegration.isLoaded() 判断，避免未安装时
 * 触发 NoClassDefFoundError。
 *
 * 这些调用等价于 KubeJS 的 AStages.*（对照 astages 的 KubeJS 桥接实现确认过）：
 *   AStages.addRestrictionForDimension(id, stage, dimension)
 *       -> ASimpleRestrictionManager.addRestriction(ASimpleRestrictionTypes.DIMENSION, id, stage, dimension)
 *   AStages.playerHasStage(stage, player)
 *       -> AStagesUtils.hasStage(AHolder.player(player), stage)
 *   AStages.addStageToPlayer(stage, player)
 *       -> AStagesUtils.addStage(AHolder.player(player), stage, false, false, false)
 *          （后三个布尔值取自 KubeJS 桥接的字节码，均为 false）
 */
public final class AStagesCompat {
    public static void addDimensionRestriction(String id, String stage, String dimension) {
        ASimpleRestrictionManager.addRestriction(ASimpleRestrictionTypes.DIMENSION, id, stage, dimension);
    }

    public static boolean playerHasStage(Player player, String stage) {
        return AStagesUtils.hasStage(AHolder.player(player), stage);
    }

    public static void addStageToPlayer(Player player, String stage) {
        AStagesUtils.addStage(AHolder.player(player), stage, false, false, false);
    }

    private AStagesCompat() {
    }
}
