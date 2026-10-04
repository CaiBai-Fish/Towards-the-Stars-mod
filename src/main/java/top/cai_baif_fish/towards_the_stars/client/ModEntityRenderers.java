package top.cai_baif_fish.towards_the_stars.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModEntities;

/**
 * 实体渲染器注册。
 *
 * bottle 用 Vanilla 的 ThrownItemRenderer：投掷物直接渲染它保存的物品栈
 * （也就是带 CustomModelData 的破伤药水），因此不需要额外的模型资源。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BOTTLE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.WARPED_GOLEM.get(), WarpedGolemRenderer::new);
    }

    private ModEntityRenderers() {
    }
}
