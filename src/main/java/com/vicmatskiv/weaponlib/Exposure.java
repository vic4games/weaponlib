package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.UniversallySerializable;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface Exposure extends UniversallySerializable {

    public boolean isEffective(World world);
    
    public void update(Entity entity);
    
    public long getLastSyncTimestamp();
    
    public void updateFrom(Exposure anotherExposure);
}
