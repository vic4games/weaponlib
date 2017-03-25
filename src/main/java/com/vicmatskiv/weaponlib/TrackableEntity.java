package com.vicmatskiv.weaponlib;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.entity.Entity;

public class TrackableEntity {

    private Supplier<Entity> entitySupplier;
    private long startTimestamp;
    private UUID uuid;

    public TrackableEntity(Entity entity, long startTimestamp) {
        this.uuid = entity.getPersistentID();
        this.entitySupplier = () -> entity;
        this.startTimestamp = startTimestamp;
    }
    
    public TrackableEntity(UUID uuid, Supplier<Entity> entitySupplier, long startTimestamp) {
        this.uuid = uuid;
        this.entitySupplier = entitySupplier;
        this.startTimestamp = startTimestamp;
    }
    
    public UUID getUuid() {
        if(uuid != null) {
            return uuid;
        }
        Entity entity = getEntity();
        return entity != null ? entity.getPersistentID() : null;
    }
    
    public void setEntitySupplier(Supplier<Entity> entitySupplier) {
        this.entitySupplier = entitySupplier;
    }

    public Entity getEntity() {
        return entitySupplier.get();
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }
    
//    @Override
//    public String toString() {
//        return "Trackable " + entitySupplier.get();
//    }
}
