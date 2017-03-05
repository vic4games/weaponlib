package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class CompatibleClientEventHandler {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onClientTick(TickEvent.ClientTickEvent event) {
		onCompatibleClientTick(new CompatibleClientTickEvent(event));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onRenderTickEvent(TickEvent.RenderTickEvent event) {
		onCompatibleRenerTickEvent(new CompatibleRenderTickEvent(event));
	}

	protected abstract void onCompatibleRenerTickEvent(CompatibleRenderTickEvent compatibleRenderTickEvent);

	protected abstract void onCompatibleClientTick(CompatibleClientTickEvent compatibleClientTickEvent);
}
