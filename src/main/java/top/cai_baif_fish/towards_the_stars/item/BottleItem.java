package top.cai_baif_fish.towards_the_stars.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.cai_baif_fish.towards_the_stars.entity.BottleProjectile;

/**
 * 破伤药水物品：可投掷，对应 KubeJS 里 bottle 的 .item(item =&gt; item.canThrow(true).unstackable())。
 * 投掷逻辑与 Vanilla 的雪球一致（消耗 1 个、创造模式不消耗），只是初速度调低了。
 */
public class BottleItem extends Item {
    /**
     * 投掷初速度。原版雪球/鸡蛋是 1.5，药水调低到 1.0：
     * 抛得更近、弧线更明显，方便瞄准 Boss。想更远就调大。
     */
    private static final float THROW_VELOCITY = 1.0F;

    public BottleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            BottleProjectile projectile = new BottleProjectile(level, player);
            projectile.setItem(stack.copy());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, THROW_VELOCITY, 1.0F);
            level.addFreshEntity(projectile);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
