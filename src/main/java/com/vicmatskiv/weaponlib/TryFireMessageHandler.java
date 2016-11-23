package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class TryFireMessageHandler implements IMessageHandler<TryFireMessage, IMessage> {
	
	private FireManager fireManager;

	TryFireMessageHandler(FireManager fireManager) {
		this.fireManager = fireManager;
	}

	@Override
	public IMessage onMessage(TryFireMessage message, MessageContext ctx) {
		EntityPlayer player = null;
		if(ctx.side == Side.SERVER) {
			player = ctx.getServerHandler().playerEntity;
			ItemStack itemStack = player.getHeldItem();
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				if(message.isOn()) {
					fireManager.tryFire(player, itemStack);
				} else {
					fireManager.tryStopFire(player, itemStack);
				}
				
			}
		}
		
		return null;
	}

}
