package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public class ServerEventHandler extends CompatibleServerEventHandler {
	
	private ModContext modContext;

	public ServerEventHandler(ModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	protected void onCompatibleItemToss(ItemTossEvent itemTossEvent) {
		ItemStack itemStack = compatibility.getItemStack(itemTossEvent);
		if(itemStack != null) {
			PlayerItemInstance<?> instance = Tags.getInstance(itemStack);
			if(instance != null) {
				// Making client remove the instance from the instance registry
				modContext.getChannel().getChannel().sendTo(new ItemTossMessage(instance.getItemInventoryIndex()), 
						(EntityPlayerMP) compatibility.getPlayer(itemTossEvent));
			}
		}
	}

}
