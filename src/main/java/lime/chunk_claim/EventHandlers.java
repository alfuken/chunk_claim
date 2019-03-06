package lime.chunk_claim;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class EventHandlers {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if (event.getWorld().isRemote) return;
        if (!ChunkClaim.disable_block_breaking) return;
        ClaimData cd = ClaimData.get(event.getPos().getX() >> 4, event.getPos().getZ() >> 4, event.getPlayer());
        if (!cd.isOwned()) return;

        if(!cd.isCitizen(event.getPlayer()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlace(BlockEvent.PlaceEvent event)
    {
        if (event.getWorld().isRemote) return;
        if (!ChunkClaim.disable_block_placing) return;
        ClaimData cd = ClaimData.get(event.getPos().getX() >> 4, event.getPos().getZ() >> 4, event.getPlayer());
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
        if (!ChunkClaim.disable_attacking_entities) return;
        if (event.getEntityPlayer().getEntityWorld().isRemote) return;
        ClaimData cd = ClaimData.get(event.getTarget().getPosition().getX() >> 4, event.getTarget().getPosition().getZ() >> 4, event.getEntityPlayer());
        if (!cd.isOwned()) return;

        if(!cd.isCitizen(event.getEntityPlayer()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (!ChunkClaim.disable_block_interaction) return;
        if (event.getEntityPlayer().getEntityWorld().isRemote) return;
        ClaimData cd = ClaimData.get(event.getPos().getX() >> 4, event.getPos().getZ() >> 4, event.getEntityPlayer());
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (!ChunkClaim.disable_item_activation) return;
        if (event.getEntityPlayer().getEntityWorld().isRemote) return;
        ClaimData cd = ClaimData.get(event.getPos().getX() >> 4, event.getPos().getZ() >> 4, event.getEntityPlayer());
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        if (!ChunkClaim.disable_block_interaction) return;
        if (event.getEntityPlayer().getEntityWorld().isRemote) return;
        ClaimData cd = ClaimData.get(event.getPos().getX() >> 4, event.getPos().getZ() >> 4, event.getEntityPlayer());
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

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        if (!ChunkClaim.disable_explosions) return;
        if (event.getWorld().isRemote) return;

        Explosion explosion = event.getExplosion();
        if (explosion.getAffectedBlockPositions().isEmpty()) return;

        EntityLivingBase exploder = explosion.getExplosivePlacedBy();
        if (exploder == null) return;

        List<BlockPos> list = new ArrayList<>(explosion.getAffectedBlockPositions());
        explosion.clearAffectedBlockPositions();

        for (BlockPos pos : list)
        {
            ClaimData cd = ClaimData.get(pos.getX() >> 4, pos.getZ() >> 4, exploder.dimension);
            if (!cd.isOwned())
            {
                explosion.getAffectedBlockPositions().add(pos);
            }
        }
    }

}
