package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TryFireMessageHandler implements IMessageHandler<TryFireMessage, IMessage> {
	
	private FireManager fireManager;

	TryFireMessageHandler(FireManager fireManager) {
		this.fireManager = fireManager;
	}

	@Override
	public IMessage onMessage(TryFireMessage message, MessageContext ctx) {
		if(ctx.side == Side.SERVER) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj; 
			ItemStack itemStack = player.getHeldItem();
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				if(message.isOn()) {
					mainThread.addScheduledTask(() -> {
						fireManager.tryFire(player, itemStack);
					});
				} else {
					mainThread.addScheduledTask(() -> {
						fireManager.tryStopFire(player, itemStack);
					});
				}
			}
		}
		
		return null;
	}

}
