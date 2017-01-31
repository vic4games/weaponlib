package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public abstract class CompatibleServerEventHandler {

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		onCompatibleItemToss(itemTossEvent);
	}

	protected abstract void onCompatibleItemToss(ItemTossEvent itemTossEvent);
}
