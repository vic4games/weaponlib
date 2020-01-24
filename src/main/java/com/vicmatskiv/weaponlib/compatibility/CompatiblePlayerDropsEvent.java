package com.vicmatskiv.weaponlib.compatibility;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

public class CompatiblePlayerDropsEvent {
    
    private PlayerDropsEvent event;

    public CompatiblePlayerDropsEvent(PlayerDropsEvent event) {
        this.event = event;
    }
    
    public List<EntityItem> getDrops() {
        return event.getDrops();
    }

    public EntityPlayer getPlayer() {
        return event.getEntityPlayer();
    }
}
