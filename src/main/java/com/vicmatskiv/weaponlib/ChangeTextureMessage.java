package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class ChangeTextureMessage implements CompatibleMessage {

	private int value;

	public ChangeTextureMessage() {
		this.value = 0;
	}

	public void fromBytes(ByteBuf buf) {
		this.value = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.value);
	}

	
}
