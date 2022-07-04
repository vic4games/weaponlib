package com.vicmatskiv.weaponlib.compatibility;


import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.command.BalancePackCommand;
import com.vicmatskiv.weaponlib.config.BalancePackManager;
import com.vicmatskiv.weaponlib.network.packets.BalancePackClient;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public abstract class CompatibleServerEventHandler {

    public abstract String getModId();

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		onCompatibleItemToss(itemTossEvent);
	}

	
	protected abstract void onCompatibleItemToss(ItemTossEvent itemTossEvent);
	
	@SubscribeEvent
    public final void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
		
		
		
 		
 		
        if(event.phase == Phase.END) {            
            int updatedFlags = CompatibleExtraEntityFlags.getFlags(event.player);
            if((updatedFlags & CompatibleExtraEntityFlags.PRONING) != 0) {
                setSize(event.player, 0.6f, 0.6f); //player.width, player.width);
            }
        }
    }
    
    protected void setSize(EntityPlayer entityPlayer, float width, float height)
    {
        if (width != entityPlayer.width || height != entityPlayer.height)
        {
            entityPlayer.width = width;
            entityPlayer.height = height;
            AxisAlignedBB axisalignedbb = entityPlayer.getEntityBoundingBox();
            entityPlayer.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entityPlayer.width, axisalignedbb.minY + (double)entityPlayer.height, axisalignedbb.minZ + (double)entityPlayer.width));
        }
    }

	@SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.phase == Phase.START) {
            onCompatibleServerTickEvent(new CompatibleServerTickEvent(event));
        }
    }
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
	    onCompatiblePlayerLoggedIn(event);
	    //System.out.println("hi");
	    getModContext().getChannel().getChannel().sendTo(new BalancePackClient(BalancePackManager.getActiveBalancePack()), (EntityPlayerMP) event.player);
	    
	}
	
    protected abstract void onCompatibleServerTickEvent(CompatibleServerTickEvent e);
    
    protected abstract void onCompatiblePlayerLoggedIn(PlayerLoggedInEvent e);


	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
	    if(event.getObject() instanceof EntityPlayer) {
	        ResourceLocation PLAYER_ENTITY_TRACKER = new ResourceLocation(getModId(), "PLAYER_ENTITY_TRACKER");
	        event.addCapability(PLAYER_ENTITY_TRACKER, new CompatiblePlayerEntityTrackerProvider());
	        
	        ResourceLocation extraFlagsResourceLocation = new ResourceLocation(getModId(), "PLAYER_ENTITY_FLAGS");
            event.addCapability(extraFlagsResourceLocation, new CompatibleExtraEntityFlags());
            
            ResourceLocation customInventoryLocation = new ResourceLocation(getModId(), "PLAYER_CUSTOM_INVENTORY");

            event.addCapability(customInventoryLocation, new CompatibleCustomPlayerInventoryCapability());
            
            ResourceLocation playerMissionsResourceLocation = new ResourceLocation(getModId(), "PLAYER_MISSIONS");
            event.addCapability(playerMissionsResourceLocation, new CompatibleMissionCapability());
	    }
	    
        ResourceLocation exposureResourceLocation = new ResourceLocation(getModId(), "EXPOSURE");
        event.addCapability(exposureResourceLocation, new CompatibleExposureCapability());
	    
	}

    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        onCompatibleEntityJoinWorld(new CompatibleEntityJoinWorldEvent(e));
    }

    protected abstract void onCompatibleEntityJoinWorld(CompatibleEntityJoinWorldEvent e);
    
    @SubscribeEvent
    public void playerDroppedItem(PlayerDropsEvent e) {
        onCompatiblePlayerDropsEvent(new CompatiblePlayerDropsEvent(e));
    }

    @SubscribeEvent
    public void playerStartedTracking(PlayerEvent.StartTracking e) {
        onCompatiblePlayerStartedTracking(new CompatibleStartTrackingEvent(e));
    }

    @SubscribeEvent
    public void playerStoppedTracking(PlayerEvent.StopTracking e) {
        //onCompatiblePlayerStoppedTracking(new CompatibleStopTrackingEvent(e));
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent e) {
        onCompatibleLivingDeathEvent(new CompatibleLivingDeathEvent(e));
    }
    
    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent e) {
        onCompatibleLivingUpdateEvent(new CompatibleLivingUpdateEvent(e));
    }
    
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        onCompatibleLivingHurtEvent(new CompatibleLivingHurtEvent(event));
    }
    
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone e) {
        onCompatiblePlayerCloneEvent(new CompatiblePlayerCloneEvent(e));
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        onCompatiblePlayerRespawnEvent(new CompatiblePlayerRespawnEvent(e));
    }
    
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract e) {
        onCompatiblePlayerInteractInteractEvent(new CompatiblePlayerEntityInteractEvent(e));
    }

//    protected abstract void onCompatibleLivingDeathEvent(LivingDeathEvent e);

    protected abstract void onCompatiblePlayerInteractInteractEvent(
            CompatiblePlayerEntityInteractEvent compatiblePlayerInteractEvent);

    protected abstract void onCompatiblePlayerStartedTracking(CompatibleStartTrackingEvent e);

    protected abstract void onCompatiblePlayerStoppedTracking(CompatibleStopTrackingEvent e);
    
    protected abstract void onCompatibleLivingUpdateEvent(CompatibleLivingUpdateEvent e);

    protected abstract void onCompatibleLivingHurtEvent(CompatibleLivingHurtEvent e);

    protected abstract void onCompatiblePlayerDropsEvent(CompatiblePlayerDropsEvent e);
    
    protected abstract void onCompatiblePlayerCloneEvent(CompatiblePlayerCloneEvent compatiblePlayerCloneEvent);

    protected abstract void onCompatiblePlayerRespawnEvent(CompatiblePlayerRespawnEvent compatiblePlayerRespawnEvent);

    protected abstract void onCompatibleLivingDeathEvent(CompatibleLivingDeathEvent event);

    public abstract ModContext getModContext();
}
