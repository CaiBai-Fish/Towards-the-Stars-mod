package top.cai_baif_fish.towards_the_stars.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.entity.WarpedGolem;

/**
 * warped_golem 的 GeckoLib 模型，资源文件来自整合包的 kubejs/assets/ctts：
 *   geo/entity/warped_golem.geo.json
 *   animations/entity/warped_golem.animation.json  (idle / walk / attack / death)
 *   textures/entity/warped_golem.png
 *
 * 注意 geo 文件里的 identifier 是 "geometry.unknown"（整合包原样），GeckoLib 按文件路径加载，
 * 不依赖该 identifier，所以保持原样未改。
 */
public class WarpedGolemModel extends GeoModel<WarpedGolem> {
    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "geo/entity/warped_golem.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "textures/entity/warped_golem.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(Towards_the_stars.MODID, "animations/entity/warped_golem.animation.json");

    @Override
    public ResourceLocation getModelResource(WarpedGolem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WarpedGolem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WarpedGolem animatable) {
        return ANIMATION;
    }

    /** 对应脚本的 .setRenderType('solid')。 */
    @Override
    public RenderType getRenderType(WarpedGolem animatable, ResourceLocation texture) {
        return RenderType.entitySolid(texture);
    }
}
