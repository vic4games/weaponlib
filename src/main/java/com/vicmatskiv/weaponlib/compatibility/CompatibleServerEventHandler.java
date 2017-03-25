package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.ExtendedPlayerProperties;
import com.vicmatskiv.weaponlib.SyncExtendedPlayerPropertiesMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;

public abstract class CompatibleServerEventHandler {

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		onCompatibleItemToss(itemTossEvent);
	}

	protected abstract void onCompatibleItemToss(ItemTossEvent itemTossEvent);
	
	@SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.phase == Phase.START) {
//            WorldServer worldServer = MinecraftServer.getServer().worldServerForDimension(0);
//            EntityTracker entityTracker = worldServer.getEntityTracker();
            /*
             * Private obfuscated field names can be found here:
             * ~/.gradle/caches/minecraft/net/minecraftforge/<version>/unpacked/conf/
             */
//            int entityViewDistance = ObfuscationReflectionHelper.getPrivateValue(EntityTracker.class, entityTracker, "entityViewDistance", "field_72792_");
//            System.out.println("Entity view distance: " + entityViewDistance);
//            ObfuscationReflectionHelper.setPrivateValue(EntityTracker.class, entityTracker, 300, "entityViewDistance", "field_72792_");

        }
        
        //return ObfuscationReflectionHelper.setPrivateValue(EntityTracker.class, entityTracker, 10, "entityViewDistance", "field_72792_");
    }
	
	@SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            ExtendedPlayerProperties.register((EntityPlayer) event.entity);
        }
    }
    
    @SubscribeEvent
    //@SideOnly(Side.SERVER)
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        onCompatibleEntityJoinWorld(e);
    }

    protected abstract void onCompatibleEntityJoinWorld(EntityJoinWorldEvent e);

    @SubscribeEvent
    public void playerStartedTracking(PlayerEvent.StartTracking e) {
        onCompatiblePlayerStartedTracking(e);
    }

    protected abstract void onCompatiblePlayerStartedTracking(StartTracking e);
}
