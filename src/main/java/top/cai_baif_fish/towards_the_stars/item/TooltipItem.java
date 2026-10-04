package top.cai_baif_fish.towards_the_stars.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 带固定 tooltip 的物品，对应 KubeJS 的 .tooltip(Text.translate('...'))。
 */
public class TooltipItem extends Item {
    private final String tooltipKey;

    public TooltipItem(Item.Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(this.tooltipKey));
    }
}
