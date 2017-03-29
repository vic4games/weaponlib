package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.ExtendedPlayerProperties;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public abstract class CompatibleServerEventHandler {

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		onCompatibleItemToss(itemTossEvent);
	}

	protected abstract void onCompatibleItemToss(ItemTossEvent itemTossEvent);
	
	@SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.phase == Phase.START) {
            onCompatibleTick(event);
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
	
	protected abstract void onCompatibleTick(ServerTickEvent event);

    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.getEntity() instanceof EntityPlayer) {
            ExtendedPlayerProperties.init((EntityPlayer) event.getEntity());
        }
    }
    
    @SubscribeEvent
    //@SideOnly(Side.SERVER)
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        onCompatibleEntityJoinWorld(new CompatibleEntityJoinWorldEvent(e));
    }

    protected abstract void onCompatibleEntityJoinWorld(CompatibleEntityJoinWorldEvent e);

    @SubscribeEvent
    public void playerStartedTracking(PlayerEvent.StartTracking e) {
        onCompatiblePlayerStartedTracking(new CompatibleStartTrackingEvent(e));
    }

    protected abstract void onCompatiblePlayerStartedTracking(CompatibleStartTrackingEvent e);
}
