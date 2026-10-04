package top.cai_baif_fish.towards_the_stars.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.compat.AStagesCompat;

/**
 * AStages 集成（可选依赖）。
 *
 * 迁移自 kubejs/server_scripts/src/stage.js：
 *   AStages.addRestrictionForDimension('dimensions/end', 'end', 'minecraft:the_end')
 *   AStages.addRestrictionForDimension('dimensions/nether', 'nether', 'minecraft:the_nether')
 *   AStages.addRestrictionForDimension('dimensions/twilight', 'tiwilight', 'twilightforest:twilight_forest')
 * （第三条的阶段名 'tiwilight' 是原脚本里的拼写，保持一致，否则会和已有的阶段数据对不上。）
 *
 * AStages 未安装时这里什么都不做，模组照常加载——所有对 AStages 类的引用都在
 * compat/AStagesCompat 里，只有确认模组存在后才会加载那个类。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID)
public final class ModAStagesIntegration {
    public static final String ASTAGES_MODID = "astages";

    public static final String STAGE_END = "end";
    public static final String STAGE_NETHER = "nether";
    public static final String STAGE_TWILIGHT = "tiwilight";

    /**
     * 时机很关键：AStages 的 ASimpleRestrictionManager.RESTRICTION_CACHE 是在它自己的
     * 资源重载期间才建立的，在 ServerAboutToStartEvent 调用会拿到 null 缓存并抛 NPE
     * （实测：进入世界时崩溃）。ServerStartedEvent 在所有重载监听器跑完之后触发，
     * 与 KubeJS 里 server_scripts 的执行时机相当。
     */
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (!isLoaded()) {
            return;
        }
        AStagesCompat.addDimensionRestriction("dimensions/end", STAGE_END, "minecraft:the_end");
        AStagesCompat.addDimensionRestriction("dimensions/nether", STAGE_NETHER, "minecraft:the_nether");
        AStagesCompat.addDimensionRestriction("dimensions/twilight", STAGE_TWILIGHT, "twilightforest:twilight_forest");
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(ASTAGES_MODID);
    }

    /**
     * 对应 item.js 里 end_cake 的 food.eaten 回调：
     * 既没有 end 也没有 nether 阶段、且不是创造模式的玩家，吃完后获得 end 阶段。
     */
    public static void grantEndStage(Player player) {
        if (!isLoaded() || player.isCreative()) {
            return;
        }
        if (AStagesCompat.playerHasStage(player, STAGE_END) || AStagesCompat.playerHasStage(player, STAGE_NETHER)) {
            return;
        }
        AStagesCompat.addStageToPlayer(player, STAGE_END);
        player.displayClientMessage(
                Component.translatable("message." + Towards_the_stars.MODID + ".end_unlocked"), false);
    }

    /** 供基座仪式使用：判断玩家是否已经有 nether 阶段。 */
    public static boolean hasNetherStage(Player player) {
        return isLoaded() && AStagesCompat.playerHasStage(player, STAGE_NETHER);
    }

    /**
     * 对应 events.js 里基座仪式 360 tick 后的 AStages.addStageToPlayer('nether', player)。
     * 脚本这一段没有创造模式判断，这里保持一致。
     */
    public static void grantNetherStage(Player player) {
        if (!isLoaded() || AStagesCompat.playerHasStage(player, STAGE_NETHER)) {
            return;
        }
        AStagesCompat.addStageToPlayer(player, STAGE_NETHER);
        player.displayClientMessage(
                Component.translatable("message." + Towards_the_stars.MODID + ".nether_unlocked"), false);
    }

    private ModAStagesIntegration() {
    }
}
