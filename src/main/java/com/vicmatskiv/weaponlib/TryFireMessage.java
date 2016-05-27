package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class TryFireMessage implements IMessage {

	private boolean on;

	public TryFireMessage() {}
	
	public TryFireMessage(boolean on) {
		this.on = on;
	}
	
	public boolean isOn() {
		return on;
	}

	public void fromBytes(ByteBuf buf) {
		this.on = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(on);
	}

	
}
