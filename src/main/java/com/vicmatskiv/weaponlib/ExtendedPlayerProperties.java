package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayerProperties implements IExtendedEntityProperties {
    
    private static final Logger logger = LogManager.getLogger(ExtendedPlayerProperties.class);
    
    private static final String PROPERTY_NAME = ExtendedPlayerProperties.class.getName();
    
    private static final String TAG_CONTENT = "content";
    
    public static final ExtendedPlayerProperties getProperties(EntityPlayer player) {
        return (ExtendedPlayerProperties) player.getExtendedProperties(PROPERTY_NAME);
    }
    
    public static final void register(EntityPlayer player) {
        ExtendedPlayerProperties properties = new ExtendedPlayerProperties();
        properties.init(player, compatibility.world(player));
        player.registerExtendedProperties(PROPERTY_NAME, properties);
    }
    
    public static final void set(EntityPlayer player, ExtendedPlayerProperties properties) {
        ExtendedPlayerProperties existingProperties = getProperties(player);
        if(existingProperties != null) {
            existingProperties.copyFrom(properties);
        } else {
            player.registerExtendedProperties(PROPERTY_NAME, properties);
        }
    }

    private World world;
    private Map<UUID, TrackableEntity> trackableEntities = new LinkedHashMap<>();
    
    private WeakHashMap<UUID, Entity> loadedEntitiesByUuid = new WeakHashMap<>();

    private long trackingDuration = 1000 * 600;
    
    ExtendedPlayerProperties() {}
    
    @Override
    public void init(Entity entity, World world) {
        this.world = world;
    }
    
    public void addTrackableEntity(TrackableEntity te) {
        trackableEntities.put(te.getEntity().getPersistentID(), te);
    }
    
    public boolean updateTrackableEntity(Entity entity) {
        TrackableEntity te = trackableEntities.get(entity.getPersistentID());
        if(te != null) {
            te.setEntitySupplier(() -> entity);
            return true;
        }
        return false;
    }
    
    public Collection<TrackableEntity> getTrackableEntitites() {
//        for(Iterator<Entry<UUID, TrackableEntity>> it = trackableEntities.entrySet().iterator(); it.hasNext();) {
//            TrackableEntity te = it.next().getValue();
//            Entity entity = te.getEntity();
//            if(entity == null) {
//                logger.trace("Removing entity {} from the list of tracked entities", te.getUuid());
//                it.remove();
//            }
//        }
        return Collections.unmodifiableCollection(trackableEntities.values());
    }
    
    public TrackableEntity getTrackableEntity(int index) {
        Collection<TrackableEntity> values = trackableEntities.values();
        int i = 0;
        TrackableEntity result = null;
        for(Iterator<TrackableEntity> it = values.iterator(); it.hasNext(); i++) {
            TrackableEntity te = it.next();
            if(i == index) {
                result = te;
                break;
            }
        }
        return result;
    }
    
    private void copyFrom(ExtendedPlayerProperties properties) {
        this.trackableEntities = properties.trackableEntities;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setByteArray(TAG_CONTENT, toByteArray());
        compound.setTag(PROPERTY_NAME, tagCompound);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound tagCompound = compound.getCompoundTag(PROPERTY_NAME);
        if(tagCompound != null) {
            byte[] content = tagCompound.getByteArray(TAG_CONTENT);
            init(content);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Entity getEntityByUuid(UUID uuid) {
        return loadedEntitiesByUuid.computeIfAbsent(uuid, u -> (Entity)world.getLoadedEntityList()
                .stream()
                //.peek(e -> {System.out.println("Examining " + ((Entity)e).getPersistentID());})
                .filter(e -> u.equals(((Entity)e).getPersistentID()))
                .findAny().orElse(null));
    }
    
    private byte[] toByteArray() {
        ByteBuf buf = Unpooled.buffer();
        serialize(buf);
        return buf.array();
    }
    
    public void serialize(ByteBuf buf) {
        serializeTrackableEntities(buf, false);
    }
    
    public void serializeForClient(ByteBuf buf) {
        serializeTrackableEntities(buf, true);
    }
    
    private void init(byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            return;
        }
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        init(buf, false);
    }
    
    public static ExtendedPlayerProperties deserialize(ByteBuf buf) {
        ExtendedPlayerProperties properties = new ExtendedPlayerProperties();
        properties.init(buf, false);
        return properties;
    }
    
    public static ExtendedPlayerProperties deserializeForClient(ByteBuf buf) {
        ExtendedPlayerProperties properties = new ExtendedPlayerProperties();
        properties.init(compatibility.clientPlayer(), compatibility.world(compatibility.clientPlayer()));
        properties.init(buf, true);
        return properties;
    }

    private void init(ByteBuf buf, boolean forClient) {
        int trackableEntitiesSize = buf.readInt();
        for(int i = 0; i < trackableEntitiesSize; i++) {
            try {
                TrackableEntity te = deserializeTrackableEntity(buf, forClient);
                if(te != null) {
                    trackableEntities.put(te.getUuid(), te);
                }
            } catch(RuntimeException e) {
                logger.error("Failed to deserialize trackable entity {}", e.toString(), e);
            }
        }
    }
    
    private void serializeTrackableEntities(ByteBuf buf, boolean forClient) {
        buf.writeInt(trackableEntities.size());
        for(TrackableEntity te: trackableEntities.values()) {
            serializeTrackableEntity(te, buf, forClient);
        }
    }
    
    private void serializeTrackableEntity(TrackableEntity trackableEntity, ByteBuf buf, boolean forClient) {
        if(forClient) {
            Entity entity = trackableEntity.getEntity();
            int entityId = entity != null ? trackableEntity.getEntity().getEntityId() : -1;
            logger.debug("Serializing entity {} with id {}", trackableEntity.getEntity(), entityId);
            buf.writeInt(entityId);
            buf.writeLong(trackableEntity.getStartTimestamp());
        } else {
            UUID entityUuid = trackableEntity.getUuid();
            logger.debug("Serializing entity {} with uuid {} ", trackableEntity.getEntity(), entityUuid);
            buf.writeLong(entityUuid.getMostSignificantBits());
            buf.writeLong(entityUuid.getLeastSignificantBits());
            buf.writeLong(trackableEntity.getStartTimestamp());
        }
        
    }
    
    private TrackableEntity deserializeTrackableEntity(ByteBuf buf, boolean forClient) {
        if(forClient) {
            int entityId = buf.readInt();
            logger.debug("Deserializing entity {}", entityId);
            long timestamp = buf.readLong();
            return new TrackableEntity(null, () -> world.getEntityByID(entityId), timestamp);
        } else {
            UUID uuid = new UUID(buf.readLong(), buf.readLong());
            logger.debug("Deserializing entity with uuid {} ", uuid);
            long timestamp = buf.readLong();
            if(timestamp + trackingDuration > System.currentTimeMillis()) {
                return new TrackableEntity(uuid, () -> getEntityByUuid(uuid), timestamp);
            } else {
                logger.debug("Removed expired trackabe entity {}", uuid);
                return null;
            }
        }
        
        
    }

 

   
}
