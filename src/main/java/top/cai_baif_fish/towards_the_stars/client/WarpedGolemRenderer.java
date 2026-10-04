package top.cai_baif_fish.towards_the_stars.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import top.cai_baif_fish.towards_the_stars.entity.WarpedGolem;

/**
 * warped_golem 的 GeckoLib 渲染器。脚本用 .sized(2, 4) 指定碰撞箱，
 * 阴影半径随之给一个匹配的大体型数值。
 */
public class WarpedGolemRenderer extends GeoEntityRenderer<WarpedGolem> {
    public WarpedGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new WarpedGolemModel());
        this.shadowRadius = 1.5F;
    }
}
