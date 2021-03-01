package com.vicmatskiv.weaponlib.vehicle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.vicmatskiv.weaponlib.EntityClassFactory;
import com.vicmatskiv.weaponlib.EntityConfiguration;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;

import net.minecraft.entity.Entity;

public class EntityVehicleConfiguration implements EntityConfiguration {

    public static class Seat {

    }

    private static final double DEFAULT_BACKWARD_DECCELERATION_FACTOR = 0.94;
    private static final double DEFAULT_FORWARD_DECCELERATION_FACTOR = 0.96;
    private static final double DEFAULT_ACCELERATION_INCREMENT = 0.02;
    private static final double DEFAULT_MIN_VELOCITY_THRESHOLD = 0.01;
    private static final double DEFAULT_ON_GROUND_FRICTION_FACTOR = 0.97;
    private static final double DEFAULT_IN_AIR_FRICTION_FACTOR = 0.99;
    private static final double DEFAULT_UNRIDDEN_DECCELERATION = 0.9;
    private static final double DEFAULT_IN_WATER_DECCELERATION = 0.95;
    private static final int DEFAULT_TRACKING_RANGE = 100;
    private static final int DEFAULT_UPDATE_FREQUENCY = 3;

    public static class Builder {

        private Class<? extends Entity> baseClass = EntityVehicle.class;
        private String name;
        private Supplier<Integer> entityIdSupplier;
        private boolean spawnEgg;
        private int primaryEggColor;
        private int secondaryEggColor;
        
        private StatefulRenderer<VehicleRenderableState> renderer;

        private String enterSound;
        private String exitSound;
        private String idleSound;
        private String runSound;

        private double backwardDeccelerationFactor = DEFAULT_BACKWARD_DECCELERATION_FACTOR;
        private double forwardDeccelerationFactor = DEFAULT_FORWARD_DECCELERATION_FACTOR;
        private double accelerationIncrement = DEFAULT_ACCELERATION_INCREMENT;
        private double minVelocityThreshold = DEFAULT_MIN_VELOCITY_THRESHOLD;
        private double onGroundFrictionFactor = DEFAULT_ON_GROUND_FRICTION_FACTOR;
        private double inAirFrictionFactor = DEFAULT_IN_AIR_FRICTION_FACTOR;

        private double unriddenDecceleration = DEFAULT_UNRIDDEN_DECCELERATION;
        private double inWaterDecceleration = DEFAULT_IN_WATER_DECCELERATION;

        private int trackingRange = DEFAULT_TRACKING_RANGE;
        private int updateFrequency = DEFAULT_UPDATE_FREQUENCY;
        private boolean sendVelocityUpdates = true;

        private Function<Double, Double> speedThreshold = s -> 1.5 * s + 0.07;

        private double handlingFactor = 5.0;
        private Function<Double, Double> handling = s -> 0.075 * s * s * handlingFactor + 0.08 * s * handlingFactor;
        private Function<Double, Double> offGroundHandling = s -> 0.015 * s * s * handlingFactor + 0.016 * s * handlingFactor;

        private List<Seat> seats = new ArrayList<>();

        private List<VehiclePart> installedParts = new ArrayList<>();

        private VehicleSuspensionStrategy suspensionStrategy  = new VehicleSuspensionStrategy.StepSuspensionStrategy(
                0.01f, 10f, 0.01f,
                0.1f, 7f, 0.01f,
                0.3f, 5f, 0.05f);

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withBaseClass(Class<? extends Entity> baseClass) {
            this.baseClass = baseClass;
            return this;
        }

        public Builder withEntityIdSupplier(Supplier<Integer> entityIdSupplier) {
            this.entityIdSupplier = entityIdSupplier;
            return this;
        }

        public Builder withSpawnEgg(int primaryEggColor, int secondaryEggColor) {
            this.spawnEgg = true;
            this.primaryEggColor = primaryEggColor;
            this.secondaryEggColor = secondaryEggColor;
            return this;
        }

        public Builder withEnterSound(String enterSound) {
            this.enterSound = enterSound.toLowerCase();
            return this;
        }

        public Builder withExitSound(String exitSound) {
            this.exitSound = exitSound.toLowerCase();
            return this;
        }

        public Builder withIdleSound(String idleSound) {
            this.idleSound = idleSound.toLowerCase();
            return this;
        }

        public Builder withRunSound(String runSound) {
            this.runSound = runSound.toLowerCase();
            return this;
        }

        public Builder withHandling(Function<Double, Double> handling) {
            this.handling = handling;
            return this;
        }

        public Builder withOffGroundHandling(Function<Double, Double> offGroundHandling) {
            this.offGroundHandling = offGroundHandling;
            return this;
        }

        public Builder withHandlingFactor(double handlingFactor) {
            this.handlingFactor = handlingFactor;
            return this;
        }

        public Builder withBackwardDeccelerationFactor(double backwardDeccelerationFactor) {
            this.backwardDeccelerationFactor = backwardDeccelerationFactor;
            return this;
        }

        public Builder withForwardDeccelerationFactor(double forwardDeccelerationFactor) {
            this.forwardDeccelerationFactor = forwardDeccelerationFactor;
            return this;
        }

        public Builder withAccelerationIncrement(double accelerationIncrement) {
            this.accelerationIncrement = accelerationIncrement;
            return this;
        }

        public Builder withMinVelocityThreshold(double minVelocityThreshold) {
            this.minVelocityThreshold = minVelocityThreshold;
            return this;
        }

        public Builder withOnGroundFrictionFactor(double onGroundFrictionFactor) {
            this.onGroundFrictionFactor = onGroundFrictionFactor;
            return this;
        }

        public Builder withInAirFrictionFactor(double inAirFrictionFactor) {
            this.inAirFrictionFactor = inAirFrictionFactor;
            return this;
        }

        public Builder withUnriddenDecceleration(double unriddenDecceleration) {
            this.unriddenDecceleration = unriddenDecceleration;
            return this;
        }

        public Builder withInWaterDecceleration(double inWaterDecceleration) {
            this.inWaterDecceleration = inWaterDecceleration;
            return this;
        }

        public Builder withSpeedThreshold(Function<Double, Double> speedThreshold) {
            this.speedThreshold = speedThreshold;
            return this;
        }

        public Builder withSeat() {
            this.seats.add(new Seat());
            return this;
        }

        public Builder withRenderer(StatefulRenderer<VehicleRenderableState> hierarchicalRenderer) {
            this.renderer = hierarchicalRenderer;
            return this;
        }

        public EntityVehicleConfiguration build(ModContext context) {
            int modEntityId = entityIdSupplier.get();
            String entityName = name != null ? name : baseClass.getSimpleName() + "Ext" + modEntityId;

            EntityVehicleConfiguration configuration = new EntityVehicleConfiguration(this);

            configuration.enterSound = context.registerSound(enterSound);
            configuration.exitSound = context.registerSound(exitSound);
            configuration.idleSound = context.registerSound(idleSound);
            configuration.runSound = context.registerSound(runSound);

            Class<? extends Entity> entityClass = EntityClassFactory.getInstance()
                    .generateEntitySubclass(baseClass, modEntityId, configuration);

            compatibility.registerModEntity(entityClass, entityName, 
                    modEntityId, context.getMod(), context.getModId(), trackingRange, updateFrequency, sendVelocityUpdates);

            ItemVehicle itemVehicle = new ItemVehicle(EntityVehicle.Type.OAK, entityClass);
            compatibility.registerItem(context.getModId(), itemVehicle, entityName);
            
//            if(spawnEgg) {
//                compatibility.registerEgg(context, entityClass, entityName, primaryEggColor, secondaryEggColor);
//            }

            if(compatibility.isClientSide()) {                
                RendererRegistration.registerRenderableEntity(context, entityClass, renderer);
            }
            return new EntityVehicleConfiguration(this);
        }

        private static class RendererRegistration {
            /*
             * This method is wrapped into a static class to facilitate conditional client-side only loading
             */
            private static void registerRenderableEntity(ModContext modContext, 
                    Class<? extends Entity> entityClass, 
                    StatefulRenderer<VehicleRenderableState> renderer) {
                modContext.registerRenderableEntity(entityClass, new RenderVehicle2(renderer));
            }
        }
    }

    private CompatibleSound enterSound;
    private CompatibleSound exitSound;
    private CompatibleSound idleSound;
    private CompatibleSound runSound;
    private Builder builder;

    public EntityVehicleConfiguration(Builder builder) {
        this.builder = builder;
    }

    public CompatibleSound getEnterSound() {
        return enterSound;
    }

    public CompatibleSound getExitSound() {
        return exitSound;
    }

    public CompatibleSound getIdleSound() {
        return idleSound;
    }

    public CompatibleSound getRunSound() {
        return runSound;
    }

    public Function<Double, Double> getHandling() {
        return builder.handling;
    }

    public Function<Double, Double> getOffGroundHandling() {
        return builder.offGroundHandling;
    }

    public double getBackwardDeccelerationFactor() {
        return builder.backwardDeccelerationFactor;
    }

    public double getForwardDeccelerationFactor() {
        return builder.forwardDeccelerationFactor;
    }

    public double getAccelerationIncrement() {
        return builder.accelerationIncrement;
    }

    public double getMinVelocityThreshold() {
        return builder.minVelocityThreshold;
    }

    public double getOnGroundFrictionFactor() {
        return builder.onGroundFrictionFactor;
    }

    public double getInAirFrictionFactor() {
        return builder.inAirFrictionFactor;
    }

    public double getUnriddenDecceleration() {
        return builder.unriddenDecceleration;
    }

    public double getInWaterDecceleration() {
        return builder.inWaterDecceleration;
    }

    public Function<Double, Double> getSpeedThreshold() {
        return builder.speedThreshold;
    }

    public VehicleSuspensionStrategy getSuspensionStrategy() {
        return builder.suspensionStrategy;
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(builder.seats);
    }

    public List<VehiclePart> getParts() {
        return builder.installedParts;
    }
}
