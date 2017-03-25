package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;

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
            ExtendedPlayerProperties properties = ExtendedPlayerProperties.getProperties((EntityPlayer) e.entity);
            if(properties != null) {
                modContext.getChannel().getChannel().sendTo(new SyncExtendedPlayerPropertiesMessage(properties),
                        (EntityPlayerMP)e.entity);
            }
        }
    }

    @Override
    protected void onCompatiblePlayerStartedTracking(StartTracking e) {
        ExtendedPlayerProperties properties = ExtendedPlayerProperties.getProperties((EntityPlayer) e.entityPlayer);
        if (properties != null) {
            if(properties.updateTrackableEntity(e.target)) {
                System.out.println("Player " + e.entityPlayer + " started tracking " + e.target);
                modContext.getChannel().getChannel().sendTo(new SyncExtendedPlayerPropertiesMessage(properties),
                        (EntityPlayerMP)e.entityPlayer);
            }
        }
    }
}
