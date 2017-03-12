package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CompatibleServerTickEvent {

    public enum Phase {
        START, END;
    }

    private ServerTickEvent event;
    
    public CompatibleServerTickEvent(ServerTickEvent event) {
        this.event = event;
    }

    public Phase getPhase() {
        return event.phase == cpw.mods.fml.common.gameevent.TickEvent.Phase.START ? Phase.START: Phase.END;
    }
}
