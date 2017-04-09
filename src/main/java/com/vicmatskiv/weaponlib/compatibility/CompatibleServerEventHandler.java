package com.vicmatskiv.weaponlib.compatibility;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public abstract class CompatibleServerEventHandler {

    public abstract String getModId();

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		onCompatibleItemToss(itemTossEvent);
	}

	protected abstract void onCompatibleItemToss(ItemTossEvent itemTossEvent);

	@SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.phase == Phase.START) {
        }
        //return ObfuscationReflectionHelper.setPrivateValue(EntityTracker.class, entityTracker, 10, "entityViewDistance", "field_72792_");
    }

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
	    if(event.getObject() instanceof EntityPlayer) {
	        ResourceLocation PLAYER_ENTITY_TRACKER = new ResourceLocation(getModId(), "PLAYER_ENTITY_TRACKER");
	        event.addCapability(PLAYER_ENTITY_TRACKER, new CompatiblePlayerEntityTrackerProvider());
	    }
	}

    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
    }

    protected abstract void onCompatibleEntityJoinWorld(CompatibleEntityJoinWorldEvent e);

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
        onCompatibleLivingDeathEvent(e);
    }

    protected abstract void onCompatibleLivingDeathEvent(LivingDeathEvent e);

    protected abstract void onCompatiblePlayerStartedTracking(CompatibleStartTrackingEvent e);

    protected abstract void onCompatiblePlayerStoppedTracking(CompatibleStopTrackingEvent e);
}
