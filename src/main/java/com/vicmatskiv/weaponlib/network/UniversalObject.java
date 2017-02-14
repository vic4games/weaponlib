package com.vicmatskiv.weaponlib.network;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.buffer.ByteBuf;

public abstract class UniversalObject {
	
	private static final String SHA1PRNG_ALG = "SHA1PRNG";

	private AtomicReference<UUID> typeUuidRef = new AtomicReference<>();
	
	private UUID uuid;
	
	protected UniversalObject() {
		this.uuid = UUID.randomUUID();
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public final UUID getTypeUuid() {
		UUID typeUuid = typeUuidRef.updateAndGet(current -> current != null ? current : createUuid());
		typeRegistry.putIfAbsent(typeUuid, getClass());
		return typeUuid;
	}
	
	protected UUID createUuid() {
		try {
			SecureRandom random = SecureRandom.getInstance(SHA1PRNG_ALG);
			random.setSeed(getClass().getName().getBytes());
			return new UUID(random.nextLong(), random.nextLong());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return UUID.fromString(getClass().getName());
		}
		
	}

	public boolean init(ByteBuf buf) {
		this.uuid = new UUID(buf.readLong(), buf.readLong()); // TODO: default constructor initializes UUID, init overrides it. Not very elegant.
		return true;
	}
	
	public void serialize(ByteBuf buf) {
		UUID typeUuid = getTypeUuid();
		buf.writeLong(typeUuid.getMostSignificantBits());
		buf.writeLong(typeUuid.getLeastSignificantBits());
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}
	
	private static ConcurrentMap<UUID, Class<? extends UniversalObject>> typeRegistry = new ConcurrentHashMap<>();
	
    public static <T extends UniversalObject> T fromBytes(ByteBuf buf) {
    	long mostSigBits = buf.readLong();
		long leastSigBits = buf.readLong();
		UUID typeUuid = new UUID(mostSigBits, leastSigBits);
		
		@SuppressWarnings("unchecked")
		Class<T> targetClass = (Class<T>) typeRegistry.get(typeUuid);
		
		UniversalObject instance;
		try {
			instance = targetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create instance of  " + targetClass);
		}
		
		instance.init(buf);
		
        return targetClass.cast(instance);
    }

}
