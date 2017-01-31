package com.vicmatskiv.weaponlib.compatibility;

import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class CompatibleServerEventHandler {

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		onCompatibleItemToss(itemTossEvent);
	}

	protected abstract void onCompatibleItemToss(ItemTossEvent itemTossEvent);
}
