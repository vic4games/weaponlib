package com.vicmatskiv.weaponlib.vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.vehicle.HierarchicalPartRenderer.SinglePart;

public class VehicleRendererBuilder extends HierarchicalRendererBuilder<VehiclePart, VehicleRenderableState> {
    
    private static List<VehiclePart> allParts = Arrays.asList(
            VehiclePart.MAIN, 
            VehiclePart.STEERING_WHEEL, 
            VehiclePart.LEFT_HAND, 
            VehiclePart.RIGHT_HAND,
            VehiclePart.FRONT_LEFT_CONTROL_ARM,
            VehiclePart.FRONT_RIGHT_CONTROL_ARM,
            VehiclePart.FRONT_LEFT_WHEEL,
            VehiclePart.FRONT_RIGHT_WHEEL,
            VehiclePart.REAR_LEFT_WHEEL,
            VehiclePart.REAR_RIGHT_WHEEL
            );
    
    private static BiConsumer<MultipartRenderStateManager<VehicleRenderableState, SinglePart, PartRenderContext<VehicleRenderableState>>, PartRenderContext<VehicleRenderableState>> DEFAULT_CONTINOUS_STATE_SETTER = 
            (stateManager, renderContext) -> {
                stateManager.setContinousState(renderContext.getState(), true, false, false);
            };
    
    private static Function<PartRenderContext<VehicleRenderableState>, Float> DEFAULT_TURN_PROGRESS_PROVIDER = 
            context -> 0.5f + (float)((EntityVehicle)context.getEntity()).getLastYawDelta()/20f;
            
    private static Function<PartRenderContext<VehicleRenderableState>, Float> DEFAULT_WHEEL_TURN_PROGRESS_PROVIDER = 
            context -> (float)Math.abs(((EntityVehicle)context.getEntity()).getWheelRotationAngle()) / 360f; 
            
    public VehicleRendererBuilder withMainModelProvider(Supplier<VehicleModel> mainModelProvider) {
        VehicleModel model = mainModelProvider.get();
        withPartRenderer(VehiclePart.MAIN, renderContext -> {
            model.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        return this;
    }
    
    public VehicleRendererBuilder withSteeringWheelModelProvider(Supplier<VehicleModel> steeringWheelModelProvider) {
        VehicleModel steeringWheelModel = steeringWheelModelProvider.get();
        withPartRenderer(VehiclePart.STEERING_WHEEL, renderContext -> {
            steeringWheelModel.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        return this;
    }
    
    public VehicleRendererBuilder withWheelModelProvider(Supplier<VehicleModel> mainModelProvider) {
        VehicleModel frontLeftWheelModel = mainModelProvider.get();
        withPartRenderer(VehiclePart.FRONT_LEFT_WHEEL, renderContext -> {
            frontLeftWheelModel.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        
        VehicleModel frontRightWheelModel = mainModelProvider.get();
        withPartRenderer(VehiclePart.FRONT_RIGHT_WHEEL, renderContext -> {
            frontRightWheelModel.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        
        VehicleModel rearLeftWheelModel = mainModelProvider.get();
        withPartRenderer(VehiclePart.REAR_LEFT_WHEEL, renderContext -> {
            rearLeftWheelModel.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        
        VehicleModel rearRightWheelModel = mainModelProvider.get();
        withPartRenderer(VehiclePart.REAR_RIGHT_WHEEL, renderContext -> {
            rearRightWheelModel.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        return this;
    }
    
    public VehicleRendererBuilder withControlArmModelProvider(Supplier<VehicleModel> controlArmModelProvider) {
        VehicleModel frontLeftControlArmModel = controlArmModelProvider.get();
        withPartRenderer(VehiclePart.FRONT_LEFT_CONTROL_ARM, renderContext -> {
            frontLeftControlArmModel.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        
        VehicleModel frontRightControlArmModel = controlArmModelProvider.get();
        withPartRenderer(VehiclePart.FRONT_RIGHT_CONTROL_ARM, renderContext -> {
            frontRightControlArmModel.render((EntityVehicle) renderContext.getEntity(), 0.0625f);
        });
        return this;
    }
    
    @Override
    public HierarchicalRendererBuilder<VehiclePart, VehicleRenderableState> withPartPosition(VehiclePart part, 
            Consumer<PartRenderContext<VehicleRenderableState>> positionFunction, 
            VehicleRenderableState...states) {
        if(states.length == 0) {
            withPartPosition(part, positionFunction, 
                    VehicleRenderableState.IDLE,
                    VehicleRenderableState.PREPARED_TO_DRIVE,
                    VehicleRenderableState.DRIVING,
//                    VehicleRenderableState.TURN_LEFT,
//                    VehicleRenderableState.TURN_RIGHT,
                    VehicleRenderableState.STOPPING);
        } else {
            super.withPartPosition(part, positionFunction, states);
        }
        
        return this;
    }
    
    @Override
    protected void prebuild() {
        
        withInitialState(VehicleRenderableState.IDLE);

        withPartStateSetter(VehiclePart.STEERING_WHEEL, DEFAULT_CONTINOUS_STATE_SETTER);
          
        withPartStateSetter(VehiclePart.FRONT_LEFT_WHEEL, DEFAULT_CONTINOUS_STATE_SETTER);
        
        withPartStateSetter(VehiclePart.FRONT_RIGHT_WHEEL, DEFAULT_CONTINOUS_STATE_SETTER);
        
        withPartStateSetter(VehiclePart.REAR_LEFT_WHEEL, DEFAULT_CONTINOUS_STATE_SETTER);
        
        withPartStateSetter(VehiclePart.REAR_RIGHT_WHEEL, DEFAULT_CONTINOUS_STATE_SETTER);
        
        withPartStateSetter(VehiclePart.FRONT_LEFT_CONTROL_ARM, DEFAULT_CONTINOUS_STATE_SETTER);
        
        withPartStateSetter(VehiclePart.FRONT_RIGHT_CONTROL_ARM, DEFAULT_CONTINOUS_STATE_SETTER);

        withPartRenderer(VehiclePart.LEFT_HAND, StatefulRenderers.createLeftHandRenderer(null, c -> c.getEntity().getControllingPassenger()));
        withPartRenderer(VehiclePart.RIGHT_HAND, StatefulRenderers.createRightHandRenderer(null, c -> c.getEntity().getControllingPassenger()));
        
        for(VehiclePart part: allParts) {
            PartConfiguration partConfiguration = partConfigurations.computeIfAbsent(part, p -> new PartConfiguration());
            
            if(partConfiguration.currentProgressProvider == null) {
                if(part instanceof VehiclePart.Wheel) {
                    partConfiguration.currentProgressProvider = DEFAULT_WHEEL_TURN_PROGRESS_PROVIDER;
                } else {
                    partConfiguration.currentProgressProvider = DEFAULT_TURN_PROGRESS_PROVIDER;
                }
            }
            
            for(VehicleRenderableState state: VehicleRenderableState.values()) {
                List<TransitionDescriptor> transitionDescriptors = partConfiguration.transitionDescriptors.computeIfAbsent(state, k -> new ArrayList<>());
                
                if(part == VehiclePart.LEFT_HAND && state == VehicleRenderableState.PREPARED_TO_DRIVE
                        && transitionDescriptors.isEmpty()) {
                    List<TransitionDescriptor> drivingTransitionDescriptors = partConfiguration.transitionDescriptors.get(VehicleRenderableState.DRIVING);
                    transitionDescriptors.add(new TransitionDescriptor(drivingTransitionDescriptors.get(0).positionFunction, animationDuration));
                } else if(part == VehiclePart.LEFT_HAND && state == VehicleRenderableState.STOPPING
                        && transitionDescriptors.isEmpty()) {
                    List<TransitionDescriptor> idleTransitionDescriptors = partConfiguration.transitionDescriptors.get(VehicleRenderableState.IDLE);
                    transitionDescriptors.add(new TransitionDescriptor(idleTransitionDescriptors.get(0).positionFunction, animationDuration));
                } else if(part == VehiclePart.RIGHT_HAND && state == VehicleRenderableState.PREPARED_TO_DRIVE
                        && transitionDescriptors.isEmpty()) {
                    List<TransitionDescriptor> drivingTransitionDescriptors = partConfiguration.transitionDescriptors.get(VehicleRenderableState.DRIVING);
                    transitionDescriptors.add(new TransitionDescriptor(drivingTransitionDescriptors.get(0).positionFunction, animationDuration));
                } else if(part == VehiclePart.RIGHT_HAND && state == VehicleRenderableState.STOPPING
                        && transitionDescriptors.isEmpty()) {
                    List<TransitionDescriptor> idleTransitionDescriptors = partConfiguration.transitionDescriptors.get(VehicleRenderableState.IDLE);
                    transitionDescriptors.add(new TransitionDescriptor(idleTransitionDescriptors.get(0).positionFunction, animationDuration));
                }
                
                if(transitionDescriptors == null || transitionDescriptors.isEmpty()) {
                    withPartPosition(part, state, c -> {});
                }
            }
        }
    }
}
