package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class LaserSwitchMessageHandler implements IMessageHandler<LaserSwitchMessage, IMessage> {

	@Override
	public IMessage onMessage(LaserSwitchMessage message, MessageContext ctx) {
		if(ctx.side == Side.SERVER) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack itemStack = player.getHeldItem();
			
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				Weapon.toggleLaser(itemStack);
			}
		}
		
		return null;
	}
	
	

}
