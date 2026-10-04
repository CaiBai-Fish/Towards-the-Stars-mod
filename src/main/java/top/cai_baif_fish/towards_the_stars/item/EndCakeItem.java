package top.cai_baif_fish.towards_the_stars.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.cai_baif_fish.towards_the_stars.event.ModAStagesIntegration;

/**
 * 末地蛋糕。
 *
 * 食物属性与 item.js 一致，额外在吃完时触发脚本里的 food.eaten 回调：
 * 既没有 end 也没有 nether 阶段、且非创造模式的玩家会获得 end 阶段。
 * AStages 未安装时该回调自动变成空操作。
 */
public class EndCakeItem extends Item {
    public EndCakeItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            ModAStagesIntegration.grantEndStage(player);
        }
        return result;
    }
}
