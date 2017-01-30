package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class TryFireMessage implements CompatibleMessage {

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
