package com.vicmatskiv.weaponlib;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientEventHandler {
	
	private Lock mainLoopLock = new ReentrantLock();
	private SafeGlobals safeGlobals;

	public ClientEventHandler(Lock mainLoopLock, SafeGlobals safeGlobals) {
		this.mainLoopLock = mainLoopLock;
		this.safeGlobals = safeGlobals;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {		
		if(event.phase == Phase.START) {
			mainLoopLock.lock();
		} else if(event.phase == Phase.END) {
			mainLoopLock.unlock();
			safeGlobals.objectMouseOver.set(Minecraft.getMinecraft().objectMouseOver);
			if(Minecraft.getMinecraft().thePlayer != null) {
				safeGlobals.currentItemIndex.set(Minecraft.getMinecraft().thePlayer.inventory.currentItem);
			}
		}
	}

}
