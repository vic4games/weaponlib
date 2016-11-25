package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ChangeAttachmentMessageHandler implements IMessageHandler<ChangeAttachmentMessage, IMessage> {
	
	private AttachmentManager attachmentManager;

	public ChangeAttachmentMessageHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	public IMessage onMessage(ChangeAttachmentMessage message, MessageContext ctx) {
		EntityPlayer player = null;
		if(ctx.side == Side.SERVER) {
			player = ctx.getServerHandler().playerEntity;
			attachmentManager.changeAttachment(message.getAttachmentCategory(), player.getHeldItem(), player);
		}
		
		return null;
	}

}
