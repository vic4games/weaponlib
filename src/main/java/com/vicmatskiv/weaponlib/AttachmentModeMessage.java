package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Random;

public class AttachmentModeMessage implements IMessage {

	private int value;

	public AttachmentModeMessage() {
		this.value = new Random().nextInt();
	}

	public void fromBytes(ByteBuf buf) {
		this.value = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.value);
	}

	
}
