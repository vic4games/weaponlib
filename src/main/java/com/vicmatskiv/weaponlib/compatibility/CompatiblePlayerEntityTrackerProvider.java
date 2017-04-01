package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;

import net.minecraft.entity.player.EntityPlayer;

public class CompatiblePlayerEntityTrackerProvider {

    public static PlayerEntityTracker getTracker(EntityPlayer player) {
        ExtendedPlayerProperties extendedProperties = ExtendedPlayerProperties.getProperties(player);
        PlayerEntityTracker tracker = null;
        if(extendedProperties != null) {
            tracker = extendedProperties.getTracker();
        }
        return tracker;
    }

    public static void setTracker(EntityPlayer clientPlayer, PlayerEntityTracker tracker) {
        ExtendedPlayerProperties properties = ExtendedPlayerProperties.getProperties(compatibility.clientPlayer());
        properties.setTracker(tracker);
    }

    public static void register(ModContext commonModContext) {
        // TODO Auto-generated method stub
        
    }

}
