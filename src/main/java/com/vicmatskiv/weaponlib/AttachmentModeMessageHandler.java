package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class AttachmentModeMessageHandler implements IMessageHandler<AttachmentModeMessage, IMessage> {
	
	private AttachmentManager attachmentManager;

	public AttachmentModeMessageHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	public IMessage onMessage(AttachmentModeMessage message, MessageContext ctx) {
		if(ctx.side == Side.SERVER) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(() -> {
				attachmentManager.toggleServerAttachmentSelectionMode(player.getHeldItem(), player);
			});
			
		}
		
		return null;
	}
	
	

}
