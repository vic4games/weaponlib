package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ChangeSettingMessageHandler implements CompatibleMessageHandler<ChangeSettingsMessage, CompatibleMessage> {

	private Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier;

	public ChangeSettingMessageHandler(Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier) {
		this.entityPlayerSupplier = entityPlayerSupplier;
	}
	
	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(ChangeSettingsMessage message, CompatibleMessageContext ctx) {
		if(!ctx.isServerSide()) {
			onClientMessage(message, ctx);
		} else  {
			ctx.runInMainThread(() -> {
				onServerMessage(message, ctx);
			});
		}
		return null;
	}
	
	// TODO: Check how this works in 1.7.10
	private void onServerMessage(ChangeSettingsMessage message, CompatibleMessageContext ctx) {
		EntityPlayer player = ctx.getPlayer();
		
//		if(message.aimingChanged() && compatibility.getHeldItemMainHand(player) != null 
//				&& compatibility.getHeldItemMainHand(player).getItem() instanceof Weapon
//				&& compatibility.getHeldItemMainHand(player).getItem() == message.getWeapon()) {
//			message.getWeapon().toggleAiming(compatibility.getHeldItemMainHand(player), player);
//		}
	}

	private void onClientMessage(ChangeSettingsMessage message, CompatibleMessageContext ctx) {
		EntityPlayer player = entityPlayerSupplier.apply(ctx);
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
			Weapon targetWeapon = message.getWeapon();
			if(targetWeapon != null) {
				if(message.recoilChanged()) {
					targetWeapon.clientChangeRecoil(player, message.getRecoil());
				}
			}
		}
	}

}