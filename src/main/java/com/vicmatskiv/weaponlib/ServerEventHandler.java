package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityJoinWorldEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleStartTrackingEvent;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;
import com.vicmatskiv.weaponlib.tracking.SyncPlayerEntityTrackerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerEventHandler extends CompatibleServerEventHandler {

    private ModContext modContext;

    public ServerEventHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    protected void onCompatibleItemToss(ItemTossEvent itemTossEvent) {}

    @Override
    protected void onCompatibleEntityJoinWorld(CompatibleEntityJoinWorldEvent e) {
        if(e.getEntity() instanceof EntityPlayerMP && !e.getWorld().isRemote) {
            System.out.println("Player " + e.getEntity() + " joined the world");
            PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.getEntity());
            if(tracker != null) {
                modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                        (EntityPlayerMP)e.getEntity());
            }
        }
    }

    @Override
    protected void onCompatiblePlayerStartedTracking(CompatibleStartTrackingEvent e) {
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.getEntity());
        if (tracker != null && tracker.updateTrackableEntity(e.getTarget())) {
            System.out.println("Player " + e.getPlayer() + " started tracking " + e.getTarget());
            modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                    (EntityPlayerMP)e.getPlayer());
            
        }
    }

    @Override
    protected void onCompatibleTick(ServerTickEvent event) {}
}
