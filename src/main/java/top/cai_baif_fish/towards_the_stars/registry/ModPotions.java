package top.cai_baif_fish.towards_the_stars.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;

/**
 * 药水注册，移植自 kubejs/startup_scripts/src/registry/potion.js。
 *
 * 原脚本用 PotionBuilder 逐条 addEffect，这里等价地写进 Potion(String, MobEffectInstance...)：
 *   dragon_breath_resistance        : dragon_breath_resistance 3600/0, regeneration 100/1, absorption 2400/0
 *   long_dragon_breath_resistance   : dragon_breath_resistance 4800/0, regeneration 160/1, absorption 3000/0
 *   strong_dragon_breath_resistance : dragon_breath_resistance 7200/0, regeneration 400/1, absorption 2400/3,
 *                                     fire_resistance 6000/0, resistance 6000/0
 *
 * 名称字符串只用于拼翻译键 item.minecraft.potion.effect.&lt;name&gt;（与 Vanilla 药水一致），
 * 显示名需要写进语言文件。
 */
public final class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, Towards_the_stars.MODID);

    public static final DeferredHolder<Potion, Potion> DRAGON_BREATH_RESISTANCE = POTIONS.register(
            "dragon_breath_resistance",
            () -> new Potion("dragon_breath_resistance",
                    new MobEffectInstance(ModEffects.DRAGON_BREATH_RESISTANCE, 3600, 0),
                    new MobEffectInstance(MobEffects.REGENERATION, 100, 1),
                    new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0)));

    public static final DeferredHolder<Potion, Potion> LONG_DRAGON_BREATH_RESISTANCE = POTIONS.register(
            "long_dragon_breath_resistance",
            () -> new Potion("long_dragon_breath_resistance",
                    new MobEffectInstance(ModEffects.DRAGON_BREATH_RESISTANCE, 4800, 0),
                    new MobEffectInstance(MobEffects.REGENERATION, 160, 1),
                    new MobEffectInstance(MobEffects.ABSORPTION, 3000, 0)));

    public static final DeferredHolder<Potion, Potion> STRONG_DRAGON_BREATH_RESISTANCE = POTIONS.register(
            "strong_dragon_breath_resistance",
            () -> new Potion("strong_dragon_breath_resistance",
                    new MobEffectInstance(ModEffects.DRAGON_BREATH_RESISTANCE, 7200, 0),
                    new MobEffectInstance(MobEffects.REGENERATION, 400, 1),
                    new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3),
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0),
                    new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0)));

    private ModPotions() {
    }
}
