package com.vicmatskiv.weaponlib;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ChangeSettingMessageHandler implements IMessageHandler<ChangeSettingsMessage, IMessage> {

	private Function<MessageContext, EntityPlayer> entityPlayerSupplier;

	public ChangeSettingMessageHandler(Function<MessageContext, EntityPlayer> entityPlayerSupplier) {
		this.entityPlayerSupplier = entityPlayerSupplier;
	}
	
	@Override
	public IMessage onMessage(ChangeSettingsMessage message, MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			onClientMessage(message, ctx);
		}
		return null;
	}
	
	private void onClientMessage(ChangeSettingsMessage message, MessageContext ctx) {
		EntityPlayer player = entityPlayerSupplier.apply(ctx);
		ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
			Weapon targetWeapon = message.getWeapon();
			if(targetWeapon != null) {
				targetWeapon.clientChangeRecoil(player, message.getRecoil());
			}
			
		}
	}

}