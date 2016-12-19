package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChangeTextureMessageHandler implements IMessageHandler<ChangeTextureMessage, IMessage> {
	
	private AttachmentManager attachmentManager;

	ChangeTextureMessageHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	public IMessage onMessage(ChangeTextureMessage message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(() -> {
			attachmentManager.changeTexture(player.getHeldItem(EnumHand.MAIN_HAND), player);
		});
		
		return null;
	}

}
