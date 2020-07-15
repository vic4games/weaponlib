package com.vicmatskiv.weaponlib.vehicle;

import com.vicmatskiv.weaponlib.state.ManagedState;

import io.netty.buffer.ByteBuf;

public enum VehicleState implements ManagedState<VehicleState> {
    
    OFF, IDLE, STARTING_TO_DRIVE, DRIVING, STOPPING;

    @Override
    public void init(ByteBuf buf) { 
    }

    @Override
    public void serialize(ByteBuf buf) {
    }

}
