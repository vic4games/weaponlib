package com.vicmatskiv.weaponlib.tracking;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerEntityTrackerProvider;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlayerEntityTracker {

    private static final Logger logger = LogManager.getLogger(PlayerEntityTracker.class);

    public static final PlayerEntityTracker getTracker(EntityPlayer player) {
        return CompatiblePlayerEntityTrackerProvider.getTracker(player);
    }

    private World world;
    private Map<UUID, TrackableEntity> trackableEntities = new LinkedHashMap<>();

    public PlayerEntityTracker(World world) {
        this.world = world;
    }

    public PlayerEntityTracker() {}

    void init(World world) {
        this.world = world;
    }

    public void addTrackableEntity(TrackableEntity te) {
        update();
        trackableEntities.put(te.getEntity().getPersistentID(), te);
    }

    public boolean updateTrackableEntity(Entity entity) {
        update();
        TrackableEntity te = trackableEntities.get(entity.getPersistentID());
        if(te != null) {
            te.setEntitySupplier(() -> entity);
            return true;
        }
        return false;
    }

    public Collection<TrackableEntity> getTrackableEntitites() {
        //update();
        return Collections.unmodifiableCollection(trackableEntities.values());
    }

    public void update() {
        for(Iterator<TrackableEntity> it = trackableEntities.values().iterator(); it.hasNext();) {
            TrackableEntity te = it.next();
            if(te.isExpired()) {
                it.remove();
            }
        }
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

    public void serialize(ByteBuf buf) {
        update();
        buf.writeInt(trackableEntities.size());
        for(TrackableEntity te: trackableEntities.values()) {
            te.serialize(buf, world);
        }
    }

    private void init(ByteBuf buf) {
        int trackableEntitiesSize = buf.readInt();
        for(int i = 0; i < trackableEntitiesSize; i++) {
            try {
                TrackableEntity te = TrackableEntity.fromBuf(buf, world);
                trackableEntities.put(te.getUuid(), te);
            } catch(RuntimeException e) {
                logger.error("Failed to deserialize trackable entity {}", e.toString(), e);
            }
        }
    }

    public byte[] toByteArray() {
        ByteBuf buf = Unpooled.buffer();
        serialize(buf);
        return buf.array();
    }

    public static PlayerEntityTracker fromByteArray(byte[] bytes, World world) {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        PlayerEntityTracker tracker = new PlayerEntityTracker(world);
        if(bytes != null && bytes.length > 0) {
            tracker.init(buf);
        } else {
            logger.warn("Cannot deserialize tracker from empty byte array");
        }
        return tracker;
    }

    public static PlayerEntityTracker fromBuf(ByteBuf buf, World world) {
        PlayerEntityTracker tracker = new PlayerEntityTracker(world);
        tracker.init(buf);
        return tracker;
    }


}
