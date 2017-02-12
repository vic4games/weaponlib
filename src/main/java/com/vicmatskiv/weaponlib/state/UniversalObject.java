package com.vicmatskiv.weaponlib.state;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class UniversalObject {
	
	private UUID uuid;
	
	protected UniversalObject() {
		this.uuid = UUID.randomUUID();
	}
	
	protected UUID getUuid() {
		return uuid;
	}
	
	protected abstract RegisteredUuid getTypeUuid();
	
	protected abstract void init(ByteBuf buf);
	
	public void serialize(ByteBuf buf) {
		UUID typeUuid = getTypeUuid().getValue();
		buf.writeLong(typeUuid.getMostSignificantBits());
		buf.writeLong(typeUuid.getLeastSignificantBits());
	}
	
	private static Map<UUID, Class<? extends UniversalObject>> registry = new HashMap<>();
	
	protected static RegisteredUuid register(Class<? extends UniversalObject> targetClass, String textualUuid) {
		UUID uuid = UUID.fromString(textualUuid);
		registry.put(uuid, targetClass);
		return new RegisteredUuid(uuid);
	}
	
    public static <T extends UniversalObject> T fromBytes(ByteBuf buf) {
    	long mostSigBits = buf.readLong();
		long leastSigBits = buf.readLong();
		UUID uuid = new UUID(mostSigBits, leastSigBits);
		
		@SuppressWarnings("unchecked")
		Class<T> targetClass = (Class<T>) registry.get(uuid);
		
		UniversalObject instance;
		try {
			instance = targetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot create instance of  " + targetClass);
		}
		
		instance.init(buf);
		
        return targetClass.cast(instance);
    }

}
