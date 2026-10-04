package top.cai_baif_fish.towards_the_stars.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

/**
 * 扭曲傀儡，移植自：
 *   kubejs/startup_scripts/src/registry/entities/warped_golem.js（注册与属性）
 *   kubejs/server_scripts/src/entity.js（AI 目标与目标选择）
 *
 * 逐项对应关系：
 *   .mobCategory('monster')         -> MobCategory.MONSTER（见 ModEntities）
 *   .sized(2, 4)                    -> EntityType.Builder.sized(2, 4)（见 ModEntities）
 *   .clientTrackingRange(32)        -> Builder.clientTrackingRange(32)
 *   .updateInterval(2)              -> Builder.updateInterval(2)
 *   .immuneTo('minecraft:lava')     -> Builder.immuneTo(Blocks.LAVA)
 *   .fireImmune(true)               -> Builder.fireImmune()
 *   .isPersistenceRequired(true)    -> isPersistenceRequired()
 *   .shouldDespawnInPeaceful(false) -> shouldDespawnInPeaceful()
 *   .isPushable(false)              -> isPushable()
 *   .setDeathSound(generic.death)   -> getDeathSound()
 *   .setSoundVolume(1.5)            -> getSoundVolume()
 *   .experienceReward(20)           -> getBaseExperienceReward()（1.21 中 getExperienceReward 已是 final）
 *   .addAnimationController('controller', 1, e -> e.moving ? play('walk') : nothing)
 *                                   -> registerControllers()，transitionTicks = 1
 *   .spawnPlacement(on_ground, motion_blocking_no_leaves, () -> true)
 *                                   -> RegisterSpawnPlacementsEvent（见 ModEntityAttributes）
 *   .biomeSpawn(['#minecraft:is_nether'], 10, 2, 5)
 *                                   -> data/towards_the_stars/neoforge/biome_modifier/warped_golem_spawn.json
 *
 * AI 参数全部取自 entity.js 的 addGoalSelectors / addGoals：
 *   floatSwim(1)                              -> FloatGoal(1)
 *   meleeAttack(1, 0.5, true)                 -> MeleeAttackGoal(1, 0.5, 跟随目标即使看不见)
 *   customGoal('follow_target', 1, ...)       -> FollowPlayerGoal(1)
 *   leapAtTarget(3, 1.2)                      -> LeapAtTargetGoal(3, 1.2)
 *   waterAvoidingRandomStroll(5, 0.4, 0.8)    -> WaterAvoidingRandomStrollGoal(5, 0.4, 0.8)
 *   lookAtEntity(6, Player, 8, 0.8, true)     -> LookAtPlayerGoal(6, Player, 8, 0.8, true)
 *   randomLookAround(7)                       -> RandomLookAroundGoal(7)
 *   hurtByTarget(1, [Player], true, [Player]) -> HurtByTargetGoal(1) + setAlertOthers(WarpedGolem)
 *   nearestAttackableTarget(2, Player, 8, false, false)
 *                                             -> NearestAttackableTargetGoal(2, Player, 8, false, false, 恒真)
 *
 * entity.js 里还有两处 removeGoal(PanicGoal)：Vanilla 的 Monster 本来就不会注册 PanicGoal
 * （那是 EntityJS 的默认行为），所以这里无需做任何移除。
 *
 * 三项没有直接对应物、按原样保留说明：
 *   .modelSize(2, 2)              GeckoLib 渲染器没有等价项；geo 模型本身就按约 4 格高建模
 *                                 （visible_bounds_height = 3.5），因此不做额外缩放。
 *   .repositionEntityAfterLoad()  EntityJS 特有行为，1.21.1 侧无对应 API。
 *   .canBreatheUnderwater(false)  Monster 的默认值就是 false，无需覆盖。
 *   .saves(true) / .setSummonable(true) 分别是默认行为与"不调用 noSummon()"。
 */
public class WarpedGolem extends Monster implements GeoEntity {
    /** 对应 addAnimationController 回调里的 event.thenPlay('walk')。 */
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WarpedGolem(EntityType<? extends WarpedGolem> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 13.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.5D, true));
        this.goalSelector.addGoal(1, new FollowPlayerGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 1.2F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4D, 0.8F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F, 0.8F, true));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(WarpedGolem.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 8, false, false, entity -> true));
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.5F;
    }

    @Override
    protected int getBaseExperienceReward() {
        return 20;
    }

    // ------------------------------------------------------------------
    // GeckoLib
    // ------------------------------------------------------------------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 1, this::walkController));
    }

    /**
     * 对应脚本的控制器回调：移动时播放 walk，其余情况不改动画（回调仍然返回 true = CONTINUE）。
     */
    private PlayState walkController(AnimationState<WarpedGolem> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * 对应 entity.js 的 e.customGoal('follow_target', 1, mob -&gt; true, mob -&gt; true, true, ...)：
     * 每 tick 扫描自身碰撞箱外扩 5 格内、距离 20 格以内的非创造模式玩家，并导航过去（速度 0.5）。
     * 目标结束（stop）时停止导航，对应脚本里 mob -&gt; mob.getNavigation().stop()。
     */
    private static class FollowPlayerGoal extends Goal {
        private static final double SEARCH_INFLATE = 5.0D;
        private static final double MAX_DISTANCE = 20.0D;
        private static final double SPEED = 0.5D;

        private final PathfinderMob mob;

        FollowPlayerGoal(PathfinderMob mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return true;
        }

        @Override
        public void tick() {
            AABB area = this.mob.getBoundingBox().inflate(SEARCH_INFLATE);
            for (Player player : this.mob.level().getEntitiesOfClass(Player.class, area)) {
                if (player.isCreative() || player.distanceTo(this.mob) >= MAX_DISTANCE) {
                    continue;
                }
                this.mob.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), SPEED);
            }
        }

        @Override
        public void stop() {
            this.mob.getNavigation().stop();
        }
    }
}
