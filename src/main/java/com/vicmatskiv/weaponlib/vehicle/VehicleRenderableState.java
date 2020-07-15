package com.vicmatskiv.weaponlib.vehicle;

public enum VehicleRenderableState implements RenderState {
    OFF(false), IDLE(false), PREPARED_TO_DRIVE(false), STOPPING(false), DRIVING(true), 
    //TURN_LEFT(true), TURN_RIGHT(true)
    ;
    
    private boolean continous;
    
    private VehicleRenderableState(boolean continous) {
        this.continous = continous;
    }
    
    public boolean isContinous() {
        return continous;
    }
}
