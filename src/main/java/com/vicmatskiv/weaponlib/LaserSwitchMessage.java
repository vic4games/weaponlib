package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class LaserSwitchMessage implements IMessage {

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
