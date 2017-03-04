package com.vicmatskiv.weaponlib.state;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
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
	public void init(ByteBuf buf) {
		super.init(buf);
		amount = buf.readInt();
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(amount);
	}
}
