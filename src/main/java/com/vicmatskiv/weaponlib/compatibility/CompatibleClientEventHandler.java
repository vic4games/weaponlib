package com.vicmatskiv.weaponlib.compatibility;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CompatibleClientEventHandler {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onClientTick(TickEvent.ClientTickEvent event) {
		onCompatibleClientTick(new CompatibleClientTickEvent(event));
	}

	protected abstract void onCompatibleClientTick(CompatibleClientTickEvent compatibleClientTickEvent);
}
