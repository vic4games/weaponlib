package com.vicmatskiv.weaponlib.state;

import com.vicmatskiv.weaponlib.network.RegisteredUuid;
import com.vicmatskiv.weaponlib.network.UniversalObject;

import io.netty.buffer.ByteBuf;

public class TestPermit extends UniversalObject {

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
	public boolean init(ByteBuf buf) {
		super.init(buf);
		amount = buf.readInt();
		return true;
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(amount);
	}
}
