package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.ReloadMessage.Type;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ReloadMessageHandler implements CompatibleMessageHandler<ReloadMessage, CompatibleMessage> {
	
	private Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier;
	private ReloadManager reloadManager;

	public ReloadMessageHandler(ReloadManager reloadManager, Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier) {
		this.reloadManager = reloadManager;
		this.entityPlayerSupplier = entityPlayerSupplier;
	}
	
	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(ReloadMessage message, CompatibleMessageContext ctx) {
		if(ctx.isServerSide()) {
			EntityPlayer player = entityPlayerSupplier.apply(ctx);
			ItemStack itemStack = compatibility.getHeldItemMainHand(player);
			
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				ctx.runInMainThread(() -> {
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

	private void onClientMessage(ReloadMessage message, CompatibleMessageContext ctx) {
		EntityPlayer player = entityPlayerSupplier.apply(ctx);
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
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
					compatibility.runInMainClientThread(() -> {
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
					compatibility.runInMainClientThread(() -> {
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
