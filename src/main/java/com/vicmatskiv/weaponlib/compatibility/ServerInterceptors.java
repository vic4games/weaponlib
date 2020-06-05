package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.mission.Missions;
import com.vicmatskiv.weaponlib.mission.ObtainItemAction;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;

public class ServerInterceptors {
    
    public static void onSlotContentChange(EntityPlayerMP player, InventoryPlayer inventory) {
        ModContext modContext = CommonModContext.getContext();
        if(modContext != null) {
            Missions.update(player, new ObtainItemAction(), modContext);
        }
    }
}
