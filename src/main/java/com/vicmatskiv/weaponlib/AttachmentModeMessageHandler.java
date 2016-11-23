package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class AttachmentModeMessageHandler implements IMessageHandler<AttachmentModeMessage, IMessage> {
	
	private AttachmentManager attachmentManager;

	public AttachmentModeMessageHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	public IMessage onMessage(AttachmentModeMessage message, MessageContext ctx) {
		if(ctx.side == Side.SERVER) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack itemStack = player.getHeldItem();
			 
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				if(((Weapon) itemStack.getItem()).getState(itemStack) != Weapon.STATE_MODIFYING) {
					attachmentManager.enterAttachmentSelectionMode(itemStack);
				} else {
					attachmentManager.exitAttachmentSelectionMode(itemStack, player);
				}
			}
		}
		
		return null;
	}
	
	

}
