package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;
import com.vicmatskiv.weaponlib.tracking.SyncPlayerEntityTrackerMessage;

import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;

public class ServerEventHandler extends CompatibleServerEventHandler {

    private ModContext modContext;

    public ServerEventHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    protected void onCompatibleItemToss(ItemTossEvent itemTossEvent) {}

    @Override
    protected void onCompatibleEntityJoinWorld(EntityJoinWorldEvent e) {
        if(e.entity instanceof EntityPlayerMP && !e.world.isRemote) {
            System.out.println("Player " + e.entity + " joined the world");
            PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.entity);
            if(tracker != null) {
                modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                        (EntityPlayerMP)e.entity);
            }
        }
    }

    @Override
    protected void onCompatiblePlayerStartedTracking(StartTracking e) {
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.entity);
        if (tracker != null && tracker.updateTrackableEntity(e.target)) {
            System.out.println("Player " + e.entityPlayer + " started tracking " + e.target);
            modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                    (EntityPlayerMP)e.entityPlayer);
            
        }
    }

    @Override
    protected void onCompatibleTick(ServerTickEvent event) {}
}
