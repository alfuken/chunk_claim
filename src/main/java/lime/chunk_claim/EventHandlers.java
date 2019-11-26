package lime.chunk_claim;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber
public class EventHandlers {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!Configuration.COMMON.disable_block_breaking.get() || event.getWorld().isRemote() || event.getPlayer().isCreative())
            return;

        ClaimData cd = ClaimData.get(event.getPos(), event.getPlayer());
        if (!cd.isOwned()) return;

        if (!cd.isCitizen(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMobSpawning(LivingSpawnEvent.CheckSpawn event) {
        if (!Configuration.COMMON.disable_entity_spawning.get() || event.getEntity().getEntityWorld().isRemote) return;

        ClaimData cd = ClaimData.get(event.getEntity());
        if (!cd.isOwned()) return;

        event.setCanceled(true);
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        PlayerEntity playerEntity = null;
        if (event.getEntity() instanceof PlayerEntity) {
            playerEntity = (PlayerEntity) event.getEntity();
        }

        if (playerEntity == null) {
            return;
        }

        if (event.getWorld().isRemote() || !Configuration.COMMON.disable_block_placing.get() || playerEntity.isCreative())
            return; // CHECK

        ClaimData cd = ClaimData.get(event.getPos(), playerEntity);
        if (!cd.isOwned()) return;

        if (!cd.isCitizen(playerEntity)) {
            event.setCanceled(true);
            if (playerEntity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) playerEntity).sendContainerToPlayer(playerEntity.container);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAttacked(AttackEntityEvent event) {
        if (!Configuration.COMMON.disable_attacking_entities.get() || event.getPlayer().getEntityWorld().isRemote || event.getPlayer().isCreative())
            return;

        ClaimData cd = ClaimData.get(event.getTarget().getPosition(), event.getPlayer());
        if (!cd.isOwned()) return;

        if (!cd.isCitizen(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!Configuration.COMMON.disable_entity_interaction.get()) return;
        validatePlayerInteractEvent(event, event.getTarget().getPosition());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!Configuration.COMMON.disable_block_interaction.get()) return;
        validatePlayerInteractEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!Configuration.COMMON.disable_item_activation.get()) return;
        validatePlayerInteractEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (!Configuration.COMMON.disable_block_interaction.get()) return;
        validatePlayerInteractEvent(event);
    }

@SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!Configuration.COMMON.disable_explosions.get() || event.getWorld().isRemote) return;

        Explosion explosion = event.getExplosion();
        if (explosion.getAffectedBlockPositions().isEmpty()) return;

        List<BlockPos> list = new ArrayList<>(explosion.getAffectedBlockPositions());

        explosion.clearAffectedBlockPositions();

        for (BlockPos pos : list) {
            ClaimData cd = ClaimData.get(pos, event.getWorld().getDimension().getType().getId());
            if (!cd.isOwned()) {
                explosion.getAffectedBlockPositions().add(pos);
            }
        }
    }

    static void validatePlayerInteractEvent(PlayerInteractEvent event) {
        validatePlayerInteractEvent(event, event.getPos());
    }

    static void validatePlayerInteractEvent(PlayerInteractEvent event, BlockPos pos) {
        if (event.getPlayer().getEntityWorld().isRemote || event.getPlayer().isCreative()) return;

        ClaimData cd = ClaimData.get(pos, event.getPlayer());
        if (!cd.isOwned()) return;

        if (!cd.isCitizen(event.getPlayer())) {
            event.setCanceled(true);
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) event.getPlayer()).sendContainerToPlayer(event.getPlayer().container);
            }
        }

    }

}
