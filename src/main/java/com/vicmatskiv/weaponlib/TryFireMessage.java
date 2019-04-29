package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class TryFireMessage implements CompatibleMessage {

	private boolean on;
	private boolean isBurst;

	public TryFireMessage() {}
	
	public TryFireMessage(boolean on, boolean isBurst) {
		this.on = on;
		this.isBurst = isBurst;
	}
	
	public boolean isOn() {
		return on;
	}
	
	public boolean isBurst() {
        return isBurst;
    }

	public void fromBytes(ByteBuf buf) {
		this.on = buf.readBoolean();
		this.isBurst = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(on);
		buf.writeBoolean(isBurst);
	}

	
}
