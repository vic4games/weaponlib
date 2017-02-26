package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;

import net.minecraftforge.event.entity.item.ItemTossEvent;

public class ServerEventHandler extends CompatibleServerEventHandler {
	
	@SuppressWarnings("unused")
	private ModContext modContext;

	public ServerEventHandler(ModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	protected void onCompatibleItemToss(ItemTossEvent itemTossEvent) {
		//
	}

}
