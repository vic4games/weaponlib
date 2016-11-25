package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ChangeAttachmentMessage implements IMessage {

	private int value;

	public ChangeAttachmentMessage() {
		this.value = 0;
	}
	
	public ChangeAttachmentMessage(AttachmentCategory attachmentCategory) {
		this.value = attachmentCategory.ordinal();
	}
	
	public AttachmentCategory getAttachmentCategory() {
		return AttachmentCategory.valueOf(value);
	}

	public void fromBytes(ByteBuf buf) {
		this.value = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.value);
	}

	
}
