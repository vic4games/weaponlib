package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ChangeTextureMessageHandler implements IMessageHandler<ChangeTextureMessage, IMessage> {

	@Override
	public IMessage onMessage(ChangeTextureMessage message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		EntityPlayerMP playermp = ctx.getServerHandler().playerEntity;
		ItemStack itemStack = player.getHeldItem();
		
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
			if(((Weapon) itemStack.getItem()).getState(itemStack) == Weapon.STATE_MODIFYING) {
				((Weapon) itemStack.getItem()).changeTexture(itemStack, playermp);
			}
		}
		return null;
	}

}
