package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class CompatibleRenderTickEvent {
	
	public enum Phase {
        START, END;
    }
	
	private RenderTickEvent event;

	public CompatibleRenderTickEvent(RenderTickEvent event) {
		this.event = event;
	}
	
	public Phase getPhase() {
		return event.phase == TickEvent.Phase.START ? Phase.START : Phase.END;
	}

	public float getRenderTickTime() {
		return event.renderTickTime;
	}

}
