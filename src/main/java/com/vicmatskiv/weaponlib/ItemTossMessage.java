package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class ItemTossMessage implements CompatibleMessage {

	private int slot; 
	
	public ItemTossMessage() {
	}
	
	public ItemTossMessage(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	public void fromBytes(ByteBuf buf) {
		this.slot = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
	}
}