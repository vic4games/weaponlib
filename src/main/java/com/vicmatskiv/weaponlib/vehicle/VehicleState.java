package com.vicmatskiv.weaponlib.vehicle;

import com.vicmatskiv.weaponlib.state.ManagedState;

import io.netty.buffer.ByteBuf;

public enum VehicleState implements ManagedState<VehicleState> {
    
    OFF, IDLE, STARTING_TO_DRIVE, DRIVING, STARTING_TO_SHIFT, SHIFTING, FINISHING_SHIFT, STOPPING;

    @Override
    public void init(ByteBuf buf) { 
    }

    @Override
    public void serialize(ByteBuf buf) {
    }

}
