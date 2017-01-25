package com.vicmatskiv.weaponlib;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.ReloadMessage.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReloadMessageHandler implements IMessageHandler<ReloadMessage, IMessage> {
	
	private Function<MessageContext, EntityPlayer> entityPlayerSupplier;
	private ReloadManager reloadManager;

	public ReloadMessageHandler(ReloadManager reloadManager, Function<MessageContext, EntityPlayer> entityPlayerSupplier) {
		this.reloadManager = reloadManager;
		this.entityPlayerSupplier = entityPlayerSupplier;
	}
	
	@Override
	public IMessage onMessage(ReloadMessage message, MessageContext ctx) {
		if(ctx.side == Side.SERVER) {
			EntityPlayer player = entityPlayerSupplier.apply(ctx);
			ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
			
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
				mainThread.addScheduledTask(() -> {
					if(message.getType() == Type.LOAD) {
						reloadManager.reload(itemStack, player);
					} else {
						reloadManager.unload(itemStack, message.getAmmo(), player);
					}
				});
			} else if(itemStack != null && itemStack.getItem() instanceof ItemMagazine) {
				reloadManager.reload(itemStack, player);
			}
		} else {
			onClientMessage(message, ctx);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void onClientMessage(ReloadMessage message, MessageContext ctx) {
		EntityPlayer player = entityPlayerSupplier.apply(ctx);
		ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
		if(itemStack != null) {
			
			Weapon targetWeapon = message.getWeapon();
			ItemMagazine targetMagazine = message.getMagazine();
			
			if(message.getType() == Type.LOAD) {
				ItemStack targetStack;
				if(message.getWeapon() != null) {
					
					if(itemStack.getItem() == targetWeapon) {
						/*
						 * if currently held item is the weapon in the message, use it
						 */
						targetStack = itemStack;
					} else {
						/*
						 * if currently held item is not the weapon in the message, try finding 
						 * item stack in the player inventory
						 */
						targetStack = WorldHelper.itemStackForItem(targetWeapon, s -> true, player);
					}
					Minecraft.getMinecraft().addScheduledTask(() -> {
						reloadManager.completeReload(targetStack, player, message.getMagazine(), message.getAmmo(), 
								itemStack.getItem() != targetWeapon);
					});
				} else if(targetMagazine != null) {
					if(itemStack.getItem() == targetMagazine) {
						/*
						 * if currently held item is the magazine in the message, use it
						 */
						targetStack = itemStack;
					} else {
						/*
						 * if currently held item is not the magazine in the message, try finding 
						 * item stack in the player inventory
						 */
						targetStack = WorldHelper.itemStackForItem(targetMagazine, s -> true, player);
					}
					Minecraft.getMinecraft().addScheduledTask(() -> {
						reloadManager.completeReload(targetStack, player, targetMagazine, message.getAmmo(), 
								itemStack.getItem() != targetMagazine);
					});
				}
			} else {
				reloadManager.completeUnload(itemStack, player);
			}
		}
	}
}
