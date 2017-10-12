package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import com.vicmatskiv.weaponlib.network.UniversalObject;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SpreadableExposure extends UniversalObject {
    
    public static interface Listener {
        public void onUpdate(SpreadableExposure exposure);
    }
    
    private static final float MIN_EFFECTIVE_TOTAL_DOSE = 0.01f;

    public static enum BlackoutPhase {NONE, ENTER, DARK, EXIT};
    
    public class Blackout {
        
        private long duration = 2000;
        private long enterDuration = 1000;
        private long exitDuration = 500;
        private long startTime;
        private long minIntervalBetweenBlackouts = 10000;
        private Random random = new Random();
        
        public float getProgress() {
            return MiscUtils.clamp((float)(System.currentTimeMillis() - startTime) / duration, 0f, 1f);
        }
        
        public float getEnterProgress() {
            return MiscUtils.clamp((float)(System.currentTimeMillis() - startTime) / enterDuration, 0f, 1f);
        }
        
        public float getExitProgress() {
            return MiscUtils.clamp((float)(System.currentTimeMillis() - (startTime + duration - exitDuration)) / exitDuration, 0f, 1f);
        }
        
        public BlackoutPhase getPhase() {
            long currentTime = System.currentTimeMillis();
            BlackoutPhase phase = BlackoutPhase.NONE;
            if(currentTime >= startTime && currentTime < startTime + enterDuration) {
                phase = BlackoutPhase.ENTER;
            } else if(currentTime >= startTime + enterDuration && currentTime < startTime + duration - exitDuration) {
                phase = BlackoutPhase.DARK;
            } else if(currentTime >= startTime + duration - exitDuration && currentTime < startTime + duration) {
                phase = BlackoutPhase.EXIT;
            }
            return phase;
        }
        
        public void update() {
            if(getPhase() != BlackoutPhase.NONE) {
                return;
            }
            if(totalDose > 0.7f && random.nextFloat() < totalDose * totalDose
                    && startTime + duration + minIntervalBetweenBlackouts < System.currentTimeMillis()) {
                startTime = System.currentTimeMillis();
                exitDuration = 300;
                duration = enterDuration + exitDuration + 500 + (long)(random.nextFloat() * totalDose * 2000L);
            }
        }
    }

    private static final int DEFAULT_IMPACT_DELAY = 5000;
    private static final float DEFAULT_DECAY_FACTOR = 0.999f;

    private long firstExposureTimestamp;
    private float totalDose;
    private float lastDose;
    private Map<UUID, Float> cycleDoseMap = new HashMap<>();
    private long firstExposureImpactDelay = DEFAULT_IMPACT_DELAY;
    
    private float decayFactor = DEFAULT_DECAY_FACTOR;
    
    private long startCycleTimestamp;
    private long lastSyncTimestamp;
    private long lastApplyTimestamp;
    private float entityImpactRate = 0.5f;
    private long cycleLengthMillis = 500;
    private int tickCount;
    private Function<Float, Float> absorbFunction = dose -> dose * Math.min(0.2f, 0.2f / totalDose);
    
    private Blackout blackout = new Blackout();
    
    private Collection<Listener> listeners = new LinkedHashSet<>();
    
    public SpreadableExposure() {
        this.firstExposureTimestamp = System.currentTimeMillis();
    }
    
    public long getFirstExposureTimestamp() {
        return firstExposureTimestamp;
    }
    
    public float getTotalDose() {
        return totalDose;
    }
    
    public float getLastDose() {
        return lastDose;
    }
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
    
    /*
     * Spreadable can be applied to an entity only once per cycle
     */
    public void apply(EntitySpreadable spreadable, Entity entity, float dose) {
//        if(System.currentTimeMillis() - startCycleTimestamp > cycleLengthMillis) {
//            startCycleTimestamp = System.currentTimeMillis();
//            cycleDoseMap.clear();
//        }
        Float currentSourceDose = cycleDoseMap.get(spreadable.getUniqueID());
        if(currentSourceDose == null) {
            //Proceed with updates only if the source was not applied in the current cycle
            cycleDoseMap.put(spreadable.getUniqueID(), dose);
            lastDose = 0f;
            cycleDoseMap.forEach((k, v) -> lastDose += v);
            
            Function<Float, Float> absorbFunction = null;
            if(entity instanceof EntityLivingBase) {
                ItemStack helmet = compatibility.getHelmet((EntityLivingBase) entity);
                if(helmet != null && helmet.getItem() instanceof ExposureProtection) {
                    absorbFunction = ((ExposureProtection)helmet.getItem()).getAbsorbFunction(spreadable);
                }
            }
            if(absorbFunction == null) {
                absorbFunction = this.absorbFunction;
            }
            
            Float absorbedDose = absorbFunction.apply(dose);
            this.totalDose += absorbedDose;
        }
    }
    
    public void setLastSyncTimestamp(long lastSyncTimestamp) {
        this.lastSyncTimestamp = lastSyncTimestamp;
    }
    
    public long getLastSyncTimestamp() {
        return lastSyncTimestamp;
    }
    
    public void updateFrom(SpreadableExposure other) {
        this.firstExposureTimestamp = other.firstExposureTimestamp;
        this.totalDose = other.totalDose;
        this.lastDose = other.lastDose;
    }

    @Override
    public void init(ByteBuf buf) {
        super.init(buf);
        firstExposureTimestamp = buf.readLong();
        totalDose = buf.readFloat();
        lastDose = buf.readFloat();
    }
    
    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(firstExposureTimestamp);
        buf.writeFloat(totalDose);
        buf.writeFloat(lastDose);
    }

    public void update(Entity entity) {
        
        if(System.currentTimeMillis() - startCycleTimestamp > cycleLengthMillis) {
            startCycleTimestamp = System.currentTimeMillis();
            cycleDoseMap.clear();
        }
        
//        boolean result = true;

        if(firstExposureTimestamp > 0) {
            if(firstExposureTimestamp + firstExposureImpactDelay < System.currentTimeMillis()) {
                if(entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLiving = (EntityLivingBase)entity;
                    applyToEntity(entityLiving);
                }
            }
            
            totalDose *= decayFactor;
            
//            if(totalDose < 0.01f) {
//                result = false;
//            }
        }
    }
    
    public void applyToEntity(EntityLivingBase entityLiving) {
        
        if(totalDose > MIN_EFFECTIVE_TOTAL_DOSE && System.currentTimeMillis() - lastApplyTimestamp >= 1000f / entityImpactRate) { 
            // TODO: configure min total dose, possibly per entity?
            //TODO: is it possible to control health per entity type?
            boolean isCreative = false;
            if(entityLiving instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityLiving;
                isCreative= player.capabilities.isCreativeMode;
            }
            if(!isCreative) {
                if(entityLiving instanceof EntityPlayer) {
                    entityLiving.setHealth(entityLiving.getHealth() - totalDose);
                } else {
                    entityLiving.attackEntityFrom(compatibility.genericDamageSource(), totalDose);
                }
                
            }
            
            lastApplyTimestamp = System.currentTimeMillis();
        }
    }
    
    public void nextCycle() {
        startCycleTimestamp = System.currentTimeMillis();
        cycleDoseMap.clear();
    }
    
    public void incrementTickCount() {
        tickCount++;
    }
    
    public int getTickCount() {
        return tickCount;
    }
    
    public Blackout getBlackout() {
        return blackout;
    }

    public boolean isEffective() {
        return getLastDose() > 0f || getTotalDose() > MIN_EFFECTIVE_TOTAL_DOSE;
    }
}
