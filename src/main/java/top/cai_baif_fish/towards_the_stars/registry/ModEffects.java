package top.cai_baif_fish.towards_the_stars.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;

/**
 * 状态效果注册，移植自 kubejs/startup_scripts/src/registry/effect.js：
 *   e.create('ctts:dragon_breath_resistance').beneficial().createObject()
 *
 * 颜色取 KubeJS 的 MobEffectBuilder 默认值 0xFFFFFF（该字段的初始值经字节码反编译确认），
 * 原脚本没有指定颜色。
 */
public final class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Towards_the_stars.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> DRAGON_BREATH_RESISTANCE =
            MOB_EFFECTS.register("dragon_breath_resistance",
                    () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));

    /**
     * Minecraft 的 MobEffect 构造函数是 protected，模组侧必须提供子类。
     */
    public static class SimpleMobEffect extends MobEffect {
        public SimpleMobEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    private ModEffects() {
    }
}
