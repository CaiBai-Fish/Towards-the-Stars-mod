package top.cai_baif_fish.towards_the_stars.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import top.cai_baif_fish.towards_the_stars.Towards_the_stars;
import top.cai_baif_fish.towards_the_stars.registry.ModItems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 基座仪式，移植自 kubejs/server_scripts/src/events.js 里
 * BlockEvents.rightClicked('supplementaries:pedestal', ...) 那一段。
 *
 * 脚本行为：主手拿着下界之书右键 Supplementaries 基座，且玩家还没有 nether 阶段时，
 *   1. 立刻开始 circle1 粒子环（i 从 240 起、每 tick +8，到 1680 结束，共 180 tick）
 *   2. 30 tick 后再叠加 circle2 升腾粒子环（i 从 60 起、每 tick +10，到 1500 结束，共 144 tick）
 *   3. 80 tick 时把基座重新 setblock 一次（重置方块状态）
 *   4. 360 tick 时授予 nether 阶段并提示
 *
 * KubeJS 的 scheduleInTicks / scheduleRepeatingInTicks 在 1.21.1 侧没有对应 API，
 * 这里把它还原成一个"从触发时刻开始计数的 tick 计数器"：粒子形态只取决于当前是第几 tick，
 * 因此不需要通用调度器，逻辑与其他三个时间点也保持一一对应。
 *
 * 基座方块按资源 ID 比较，不引入 Supplementaries 依赖；该方块不存在时右键自然不触发。
 */
@EventBusSubscriber(modid = Towards_the_stars.MODID)
public final class ModPedestalRitual {
    private static final ResourceLocation PEDESTAL_ID =
            ResourceLocation.fromNamespaceAndPath("supplementaries", "pedestal");

    private static final double RADIUS = 5.0D;
    /** 脚本里最后一次 scheduleInTicks 的延迟。 */
    private static final int TOTAL_TICKS = 360;

    private static final List<Ritual> ACTIVE = new ArrayList<>();

    private static final class Ritual {
        private final ServerLevel level;
        private final BlockPos pos;
        private final UUID playerId;
        private int age;

        Ritual(ServerLevel level, BlockPos pos, UUID playerId) {
            this.level = level;
            this.pos = pos;
            this.playerId = playerId;
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!PEDESTAL_ID.equals(BuiltInRegistries.BLOCK.getKey(level.getBlockState(event.getPos()).getBlock()))) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.NETHER_BOOK.get())) {
            return;
        }
        if (ModAStagesIntegration.hasNetherStage(player)) {
            return;
        }
        // 脚本每次都新起一段动画，这里保持一致（没有做"同位置去重"）。
        ACTIVE.add(new Ritual(level, event.getPos().immutable(), player.getUUID()));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (ACTIVE.isEmpty()) {
            return;
        }
        List<Ritual> keep = new ArrayList<>(ACTIVE.size());
        Iterator<Ritual> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            Ritual ritual = iterator.next();
            tickRitual(ritual);
            ritual.age++;
            if (ritual.age <= TOTAL_TICKS) {
                keep.add(ritual);
            }
        }
        ACTIVE.clear();
        ACTIVE.addAll(keep);
    }

    private static void tickRitual(Ritual ritual) {
        int t = ritual.age;
        double centerX = ritual.pos.getX() + 0.5D;
        double centerZ = ritual.pos.getZ() + 0.5D;

        // 1. 内环：i 从 240 起、每 tick +8，到 1680 结束（180 tick）
        if (t < 180) {
            int i = 240 + t * 8;
            for (int n = 8; n > 0; n--) {
                double innerRadians = (-i + n * 2) * (Math.PI / 180.0);
                double innerRadius = RADIUS * Math.pow(i / 3600.0D, 2);
                if (innerRadius >= 3.0D) {
                    innerRadius = 3.0D;
                }
                double innerX = Math.cos(innerRadians) * innerRadius;
                double innerZ = Math.sin(innerRadians) * innerRadius;
                ritual.level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        centerX + innerX, ritual.pos.getY() + 1.5D, centerZ + innerZ,
                        4, 0.0D, 0.0D, 0.0D, 0.01D);
            }
        }

        // 2. 外环：30 tick 后开始，i 从 60 起、每 tick +10，到 1500 结束（144 tick）
        if (t >= 30) {
            int i = 60 + (t - 30) * 10;
            if (i < 1500) {
                for (int n = 0; n < 10; n++) {
                    double radians = (i + n * 2) * (Math.PI / 180.0);
                    double xOffset = Math.cos(radians) * RADIUS;
                    double zOffset = Math.sin(radians) * RADIUS;
                    double rise = i / 680.0D;
                    if (rise > 1.5D) {
                        rise = 1.5D;
                    }
                    ritual.level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                            centerX + xOffset, ritual.pos.getY() + rise, centerZ + zOffset,
                            5, 0.0D, 0.0D, 0.0D, 0.06D);
                }
            }
        }

        // 3. 80 tick：重新放置一次基座（脚本用的是 setblock）
        if (t == 80) {
            Block pedestal = BuiltInRegistries.BLOCK.get(PEDESTAL_ID);
            if (pedestal != Blocks.AIR) {
                ritual.level.setBlockAndUpdate(ritual.pos, pedestal.defaultBlockState());
            }
        }

        // 4. 360 tick：授予 nether 阶段
        if (t == TOTAL_TICKS) {
            ServerPlayer player = ritual.level.getServer().getPlayerList().getPlayer(ritual.playerId);
            if (player != null) {
                ModAStagesIntegration.grantNetherStage(player);
            }
        }
    }

    private ModPedestalRitual() {
    }
}
