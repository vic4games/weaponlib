package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public abstract class CompatibleWeaponKeyInputHandler {
	
	@SubscribeEvent
    public final void onKeyInput(InputEvent.KeyInputEvent event) {
		onCompatibleKeyInput();
	}

	protected abstract void onCompatibleKeyInput();
}
