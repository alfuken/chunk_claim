package lime.chunk_claim;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class EventHandlers {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if (!ChunkClaim.disable_block_breaking || event.getWorld().isRemote || event.getPlayer().isCreative()) return;

        ClaimData cd = ClaimData.get(event.getPos(), event.getPlayer());
        if (!cd.isOwned()) return;

        if(!cd.isCitizen(event.getPlayer()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMobSpawning(LivingSpawnEvent.CheckSpawn event) {
        if (!ChunkClaim.disable_entity_spawning || event.getEntity().getEntityWorld().isRemote) return;

        ClaimData cd = ClaimData.get(event.getEntity());
        if (!cd.isOwned()) return;

        event.setCanceled(true);
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlace(BlockEvent.PlaceEvent event)
    {
        if (event.getWorld().isRemote || !ChunkClaim.disable_block_placing || event.getPlayer().isCreative()) return;

        ClaimData cd = ClaimData.get(event.getPos(), event.getPlayer());
        if (!cd.isOwned()) return;

        if(!cd.isCitizen(event.getPlayer()))
        {
            event.setCanceled(true);
            if(event.getPlayer() instanceof EntityPlayerMP)
            {
                ((EntityPlayerMP)event.getPlayer()).sendContainerToPlayer(event.getPlayer().inventoryContainer);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAttacked(AttackEntityEvent event)
    {
        if (!ChunkClaim.disable_attacking_entities || event.getEntityPlayer().getEntityWorld().isRemote || event.getEntityPlayer().isCreative()) return;

        ClaimData cd = ClaimData.get(event.getTarget().getPosition(), event.getEntityPlayer());
        if (!cd.isOwned()) return;

        if(!cd.isCitizen(event.getEntityPlayer()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (!ChunkClaim.disable_entity_interaction) return;
        validatePlayerInteractEvent(event, event.getTarget().getPosition());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (!ChunkClaim.disable_block_interaction) return;
        validatePlayerInteractEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (!ChunkClaim.disable_item_activation) return;
        validatePlayerInteractEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        if (!ChunkClaim.disable_block_interaction) return;
        validatePlayerInteractEvent(event);
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        if (!ChunkClaim.disable_explosions || event.getWorld().isRemote) return;

        Explosion explosion = event.getExplosion();
        if (explosion.getAffectedBlockPositions().isEmpty()) return;

        EntityLivingBase exploder = explosion.getExplosivePlacedBy();
        if (exploder == null) return;

        List<BlockPos> list = new ArrayList<>(explosion.getAffectedBlockPositions());
        explosion.clearAffectedBlockPositions();

        for (BlockPos pos : list)
        {
            ClaimData cd = ClaimData.get(pos, exploder.dimension);
            if (!cd.isOwned())
            {
                explosion.getAffectedBlockPositions().add(pos);
            }
        }
    }

    static void validatePlayerInteractEvent(PlayerInteractEvent event)
    {
        validatePlayerInteractEvent(event, event.getPos());
    }

    static void validatePlayerInteractEvent(PlayerInteractEvent event, BlockPos pos)
    {
        if (event.getEntityPlayer().getEntityWorld().isRemote || event.getEntityPlayer().isCreative()) return;

        ClaimData cd = ClaimData.get(pos, event.getEntityPlayer());
        if (!cd.isOwned()) return;

        if(!cd.isCitizen(event.getEntityPlayer()))
        {
            event.setCanceled(true);
            if(event.getEntityPlayer() instanceof EntityPlayerMP)
            {
                ((EntityPlayerMP)event.getEntityPlayer()).sendContainerToPlayer(event.getEntityPlayer().inventoryContainer);
            }
        }

    }

}
