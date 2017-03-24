package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.event.entity.item.ItemTossEvent;

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
}
