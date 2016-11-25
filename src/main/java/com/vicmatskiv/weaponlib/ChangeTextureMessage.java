package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ChangeTextureMessage implements IMessage {

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
