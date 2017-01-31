package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public class ServerEventHandler extends CompatibleServerEventHandler {
	
	private AttachmentManager attachmentManager;

	public ServerEventHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	protected void onCompatibleItemToss(ItemTossEvent itemTossEvent) {
		ItemStack itemStack = compatibility.getItemStack(itemTossEvent);
		Item item = itemStack.getItem();
		if(!(item instanceof Weapon)) {
			return; 
		}
		
		if(Weapon.isModifying(itemStack)) {
			attachmentManager.exitAttachmentSelectionMode(itemStack, compatibility.getPlayer(itemTossEvent));
		}
	}

}
