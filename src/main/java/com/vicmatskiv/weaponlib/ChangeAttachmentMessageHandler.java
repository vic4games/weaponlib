package com.vicmatskiv.weaponlib;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

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
