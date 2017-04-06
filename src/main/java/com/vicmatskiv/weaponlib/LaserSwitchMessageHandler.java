package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class LaserSwitchMessageHandler implements CompatibleMessageHandler<LaserSwitchMessage, CompatibleMessage> {

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(LaserSwitchMessage message, CompatibleMessageContext ctx) {
		if(ctx.isServerSide()) {
			EntityPlayer player = ctx.getPlayer();
			ItemStack itemStack = compatibility.getHeldItemMainHand(player);
			
			if(itemStack != null && itemStack.getItem() instanceof Weapon) {
				ctx.runInMainThread(() -> {
					Weapon.toggleLaser(itemStack);
				});
			}
		}
		
		return null;
	}
	
	

}
