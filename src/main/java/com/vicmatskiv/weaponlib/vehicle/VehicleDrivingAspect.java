package com.vicmatskiv.weaponlib.vehicle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleExtraEntityFlags;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

public class VehicleDrivingAspect implements Aspect<VehicleState, EntityVehicle> {
    
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(VehicleDrivingAspect.class);
    
    private static final Set<VehicleState> allowedAutoUpdateFromStates = new HashSet<>(
            Arrays.asList(
                    VehicleState.IDLE, 
                    VehicleState.STARTING_TO_DRIVE,
                    VehicleState.DRIVING,
                    VehicleState.STOPPING));

    private static Predicate<EntityVehicle> hasMinimalSpeed = vehicle -> vehicle.getSpeed() > 0.01;
    
    private static Predicate<EntityVehicle> movingForwardOrBackward = vehicle -> {
        return vehicle.isSteeredForward() || vehicle.isSteeredBackward();
    };
        
    
    private static Predicate<EntityVehicle> speedupTimeoutExpired = vehicle ->
        System.currentTimeMillis() >= 300 + vehicle.getStateUpdateTimestamp();

//    @SuppressWarnings("unused")
//    private ModContext modContext;

    private StateManager<VehicleState, ? super EntityVehicle> stateManager;
    
    public VehicleDrivingAspect(/*CommonModContext modContext*/) {
        //this.modContext = modContext;
        this.setStateManager(new StateManager<>((s1, s2) -> s1 == s2));
    }

    @Override
    public void setPermitManager(PermitManager permitManager) {}

    @Override
    public void setStateManager(StateManager<VehicleState, ? super EntityVehicle> stateManager) {
        this.stateManager = stateManager;

        stateManager

            .in(this).change(VehicleState.IDLE).to(VehicleState.STARTING_TO_DRIVE)
            .when(hasMinimalSpeed.and(movingForwardOrBackward))
            .automatic() 
        
            .in(this).change(VehicleState.STARTING_TO_DRIVE).to(VehicleState.DRIVING)
            .when(speedupTimeoutExpired)
            .automatic()
            
            .in(this).change(VehicleState.DRIVING).to(VehicleState.STOPPING)
            .when(hasMinimalSpeed.negate())
            .automatic()
            
            .in(this).change(VehicleState.STOPPING).to(VehicleState.IDLE)
            .when(speedupTimeoutExpired)
            .automatic()
        ;
    }

    void onUpdate(EntityVehicle vehicle) {
        stateManager.changeStateFromAnyOf(this, vehicle, allowedAutoUpdateFromStates); // triggers "auto" state transitions
    }
}
