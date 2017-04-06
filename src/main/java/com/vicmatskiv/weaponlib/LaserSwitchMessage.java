package com.vicmatskiv.weaponlib;

import java.util.Random;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class LaserSwitchMessage implements CompatibleMessage {

	private int value;

	public LaserSwitchMessage() {
		this.value = new Random().nextInt();
	}

	public void fromBytes(ByteBuf buf) {
		this.value = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.value);
	}

	
}
