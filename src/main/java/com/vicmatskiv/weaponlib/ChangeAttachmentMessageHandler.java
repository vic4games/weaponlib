package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

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
			ItemStack itemStack = player.getHeldItem();
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				if(((Weapon) itemStack.getItem()).getState(itemStack) == Weapon.STATE_MODIFYING) {
					attachmentManager.changeAttachment(message.getAttachmentCategory(), itemStack, player);
				}
			}
		}
		
		return null;
	}

}
