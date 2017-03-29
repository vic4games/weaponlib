package com.vicmatskiv.weaponlib.compatibility;

import net.minecraftforge.common.capabilities.Capability;

public class CompatibleCapability<T> {
    
    private Capability<T> capability;

    public CompatibleCapability(Capability<T> capability) {
        this.capability = capability;
    }
    
    public Capability<T> getCapability() {
        return capability;
    }

}
