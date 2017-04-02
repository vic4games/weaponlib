package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class CompatibleServerEventHandler {

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		onCompatibleItemToss(itemTossEvent);
	}

	protected abstract void onCompatibleItemToss(ItemTossEvent itemTossEvent);
	
	@SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.phase == Phase.START) {
//            ObfuscationReflectionHelper.setPrivateValue(EntityTracker.class, entityTracker, 300, "entityViewDistance", "field_72792_");

        }
        
        //return ObfuscationReflectionHelper.setPrivateValue(EntityTracker.class, entityTracker, 10, "entityViewDistance", "field_72792_");
    }
	
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            ExtendedPlayerProperties.init((EntityPlayer) event.entity);
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

    public abstract String getModId();
}
