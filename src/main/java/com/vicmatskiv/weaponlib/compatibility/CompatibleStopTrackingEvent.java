package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.StopTracking;

public class CompatibleStopTrackingEvent {

    private StopTracking event;

    public CompatibleStopTrackingEvent(StopTracking event) {
        this.event = event;
    }

    public EntityPlayer getPlayer() {
<<<<<<< HEAD
        return event.getEntityPlayer();
    }

    public Entity getTarget() {
        return event.getTarget();
    }

    public Entity getEntity() {
        return event.getEntity();
=======
        return event.entityPlayer;
    }

    public Entity getTarget() {
        return event.target;
    }

    public Entity getEntity() {
        return event.entity;
>>>>>>> 152023007a3d5249eeb06ad133ca373d5ae9a05e
    }
}
