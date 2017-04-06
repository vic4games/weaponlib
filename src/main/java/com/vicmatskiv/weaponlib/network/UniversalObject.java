package com.vicmatskiv.weaponlib.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

public abstract class UniversalObject implements UniversallySerializable {
	
	private UUID uuid;
	
	protected int getSerialVersion() {
		return 0;
	}
	
	protected UniversalObject() {
		this.uuid = UUID.randomUUID();
	}
	
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void init(ByteBuf buf) {
		if(getSerialVersion() != buf.readInt()) {
			throw new IndexOutOfBoundsException("Serial version mismatch"); // TODO: create custom exception
		}
		this.uuid = new UUID(buf.readLong(), buf.readLong()); // TODO: default constructor initializes UUID, init overrides it. Not very elegant.
	}
	
	public void serialize(ByteBuf buf) {
		buf.writeInt(getSerialVersion());
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
		//typeRegistry.serialize(this, buf);
	}
	
//    public static <T extends UniversalObject> T fromBytes(ByteBuf buf, TypeRegistry typeRegistry) {
//    	return typeRegistry.fromBytes(buf);
//    }

}
