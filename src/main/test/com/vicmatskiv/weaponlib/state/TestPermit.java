package com.vicmatskiv.weaponlib.state;

import io.netty.buffer.ByteBuf;

public class TestPermit extends UniversalObject {
	
	static RegisteredUuid uuid = register(TestPermit.class, "ff9c1e73-25f4-461e-98de-ef89f5edd73d");
	
	private int amount;
	
	public TestPermit() {
	}

	public TestPermit(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}
	
	@Override
	protected RegisteredUuid getTypeUuid() {
		return uuid;
	}

	@Override
	protected void init(ByteBuf buf) {
		amount = buf.readInt();
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(amount);
	}
}
