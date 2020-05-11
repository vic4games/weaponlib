package com.vicmatskiv.weaponlib.mission;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class EntityMissionOfferingSyncMessage implements CompatibleMessage {

    private Map<String, List<UUID>> entityOfferings;

    public EntityMissionOfferingSyncMessage() {
        this.entityOfferings = new HashMap<>();
    }
    
    public EntityMissionOfferingSyncMessage(Map<String, List<UUID>> entityOfferings) {
        this.entityOfferings = entityOfferings;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int entityCount = buf.readInt();
        for(int i = 0; i < entityCount; i++) {            
            int entityNameBytesLen = buf.readInt();
            byte[] entityNameBytes = new byte[entityNameBytesLen];
            buf.readBytes(entityNameBytes);
            String entityName = new String(entityNameBytes);
            
            int offeringCount = buf.readInt();
            List<UUID> offerings = new ArrayList<>();
            for(int j = 0; j < offeringCount; j++) {
                offerings.add(new UUID(buf.readLong(), buf.readLong()));
            }
            entityOfferings.put(entityName, offerings);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityOfferings.size());
        for(Entry<String, List<UUID>> entry: entityOfferings.entrySet()) {            
            byte[] nameBytes = entry.getKey().getBytes(Charset.defaultCharset());
            buf.writeInt(nameBytes.length);
            buf.writeBytes(nameBytes);
            
            List<UUID> offerings = entry.getValue();
            buf.writeInt(offerings.size());
            for(UUID offering: offerings) {
                buf.writeLong(offering.getMostSignificantBits());
                buf.writeLong(offering.getLeastSignificantBits());
            }
        }
    }
    
    public Map<String, List<UUID>> getEntityOfferings() {
        return entityOfferings;
    }
}
