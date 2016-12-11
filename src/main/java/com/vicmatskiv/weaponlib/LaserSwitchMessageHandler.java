package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class LaserSwitchMessageHandler implements IMessageHandler<LaserSwitchMessage, IMessage> {

	@Override
	public IMessage onMessage(LaserSwitchMessage message, MessageContext ctx) {
		if(ctx.side == Side.SERVER) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack itemStack = player.getHeldItem();
			
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
				mainThread.addScheduledTask(() -> {
					Weapon.toggleLaser(itemStack);
				});
			}
		}
		
		return null;
	}
	
	

}
