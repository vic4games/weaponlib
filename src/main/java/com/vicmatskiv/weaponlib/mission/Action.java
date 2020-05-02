package com.vicmatskiv.weaponlib.mission;

import com.vicmatskiv.weaponlib.network.UniversalObject;

import net.minecraft.entity.player.EntityPlayer;

public abstract class Action extends UniversalObject {

    public abstract int matches(Action anotherAction, EntityPlayer player);
    
    public Object getResult(EntityPlayer player) {
        return null;
    }
    
    public boolean isTransient() {
        return true;
    }

    public boolean quantityMatches(EntityPlayer player, int requiredQuantity) {
        return false;
    }
}
