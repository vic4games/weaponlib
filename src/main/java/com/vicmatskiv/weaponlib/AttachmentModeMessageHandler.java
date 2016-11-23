package com.vicmatskiv.weaponlib;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class AttachmentModeMessageHandler implements IMessageHandler<AttachmentModeMessage, IMessage> {
	
	private AttachmentManager attachmentManager;

	public AttachmentModeMessageHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	public IMessage onMessage(AttachmentModeMessage message, MessageContext ctx) {
		if(ctx.side == Side.SERVER) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			attachmentManager.toggleServerAttachmentSelectionMode(player.getHeldItem(), player);
		}
		
		return null;
	}
	
	

}
