package com.vicmatskiv.weaponlib.tracking;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TrackableEntity {

    private static final Logger logger = LogManager.getLogger(TrackableEntity.class);

    private Supplier<Entity> entitySupplier;
    private long startTimestamp;
    private UUID uuid;
    private int entityId;
    private long trackingDuration;
    private WeakReference<Entity> entityRef;
    private String displayName = "";
    private Supplier<World> worldSupplier;

    private TrackableEntity() {}

    public TrackableEntity(Entity entity, long startTimestamp, long trackingDuration) {
        this.uuid = entity.getPersistentID();
        this.entityId = entity.getEntityId();
        this.entitySupplier = () -> entity;
        this.startTimestamp = startTimestamp;
        this.trackingDuration = trackingDuration;
        this.worldSupplier = () -> compatibility.world(entity);
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
        this.entityId = -1;
        this.entityRef = null;
    }

    public Entity getEntity() {
        if(entityRef == null || entityRef.get() == null) {
            Entity entity = entitySupplier.get();
            if(entity != null) {
                if(entity instanceof EntityPlayer) {
                    displayName = compatibility.getDisplayName((EntityPlayer)entity);
                } else if(entity instanceof EntityLivingBase) {
                    displayName = EntityList.getEntityString(entity);
                }
                entityId = entity.getEntityId();
            }
            entityRef = new WeakReference<Entity>(entity);
        }
        return entityRef.get();
    }


    public static TrackableEntity fromBuf(ByteBuf buf, Supplier<World> world) {
        TrackableEntity te = new TrackableEntity();
        te.init(buf, world);
        return te;
    }

    public void init(ByteBuf buf, Supplier<World> worldSupplier) {
        this.worldSupplier = worldSupplier;
        uuid = new UUID(buf.readLong(), buf.readLong());
        entityId = buf.readInt();
        startTimestamp = buf.readLong();
        trackingDuration = buf.readLong();
//        if(world.isRemote) {
//            // For clients, always use entity id. Remember: entity uuid on client and server don't match.
//            logger.debug("Initializing client entity uuid {}, id {}", uuid, entityId);
//            entitySupplier = () -> world.getEntityByID(entityId);
//        } else {
//            // For server, use persistent uuid
//            logger.debug("Initializing server entity uuid {}, id {}", uuid, entityId);
//            entitySupplier = () -> getEntityByUuid(uuid, world);
//        }
        
        entitySupplier = () -> {
            World w = worldSupplier.get();
            if(w.isRemote) {
                return w.getEntityByID(entityId);
            }
            return getEntityByUuid(uuid, w);
        };
    }

    public void serialize(ByteBuf buf, Supplier<World> world) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        Entity entity = getEntity();
        int entityId = -1;
        if(entity != null) {
            entityId = entity.getEntityId();
        }
        logger.debug("Serializing server entity uuid {}, id {}", uuid, entityId);

        buf.writeInt(entityId);
        buf.writeLong(startTimestamp);
        buf.writeLong(trackingDuration);
    }

    private Entity getEntityByUuid(UUID uuid, World world) {
        if(world instanceof WorldServer) {
            return ((WorldServer)world).getEntityFromUuid(uuid);
        }
        return (Entity)world.getLoadedEntityList()
                .stream()
                //.peek(e -> {System.out.println("Examining " + ((Entity)e).getPersistentID());})
                .filter(e -> uuid.equals(((Entity)e).getPersistentID()))
                .findAny().orElse(null);
    }

    public boolean isExpired() {
        if(worldSupplier == null) {
            return true;
        }
        //Entity entity = getEntity();
        return /*(entity != null && entity.isDead) ||  */ startTimestamp + trackingDuration < worldSupplier.get().getWorldTime();
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getTrackingDuration() {
        return trackingDuration;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }
}
