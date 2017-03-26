package com.vicmatskiv.weaponlib.tracking;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class TrackableEntity {

    private Supplier<Entity> entitySupplier;
    private long startTimestamp;
    private UUID uuid;
    private int entityId;
    private long trackingDuration;
    private WeakReference<Entity> entityRef;
    
    private TrackableEntity() {}

    public TrackableEntity(Entity entity, long startTimestamp, long trackingDuration) {
        this.uuid = entity.getPersistentID();
        this.entityId = entity.getEntityId();
        this.entitySupplier = () -> entity;
        this.startTimestamp = startTimestamp;
        this.trackingDuration = trackingDuration;
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
        if(entityRef == null || entityRef.get() == null) {
            Entity entity = entitySupplier.get();
            if(entity != null) {
                entityId = entity.getEntityId();
            }
            entityRef = new WeakReference<Entity>(entity);
        }
        return entityRef.get();
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }
    
    public static TrackableEntity fromBuf(ByteBuf buf, World world) {
        TrackableEntity te = new TrackableEntity();
        te.init(buf, world);
        return te;
    }
    
    public void init(ByteBuf buf, World world) {
        uuid = new UUID(buf.readLong(), buf.readLong());
        entityId = buf.readInt();
        startTimestamp = buf.readLong();
        trackingDuration = buf.readLong();
        if(world.isRemote) {
            // For clients, always use entity id. Remember: entity uuid on client and server don't match.
            entitySupplier = () -> world.getEntityByID(entityId);
        } else {
            // For server, use persistent uuid
            entitySupplier = () -> getEntityByUuid(uuid, world);
        }
    }
    
    public void serialize(ByteBuf buf, World world) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        Entity entity = getEntity();
        if(entity != null) {
            buf.writeInt(entity.getEntityId());
        } else {
            buf.writeInt(-1);
        }
        buf.writeLong(startTimestamp);
        buf.writeLong(trackingDuration);
    }
    
    @SuppressWarnings("unchecked")
    private Entity getEntityByUuid(UUID uuid, World world) {
        return (Entity)world.getLoadedEntityList()
                .stream()
                //.peek(e -> {System.out.println("Examining " + ((Entity)e).getPersistentID());})
                .filter(e -> e.equals(((Entity)e).getPersistentID()))
                .findAny().orElse(null);
    }

    public boolean isExpired() {
        return startTimestamp + trackingDuration < System.currentTimeMillis();
    }
}
