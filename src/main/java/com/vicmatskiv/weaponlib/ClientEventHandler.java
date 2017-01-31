package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent.Phase;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;

import net.minecraft.client.Minecraft;

public class ClientEventHandler extends CompatibleClientEventHandler {

	private Lock mainLoopLock = new ReentrantLock();
	private SafeGlobals safeGlobals;
	private Queue<Runnable> runInClientThreadQueue;

	public ClientEventHandler(ModContext modContext, Lock mainLoopLock, SafeGlobals safeGlobals, Queue<Runnable> runInClientThreadQueue) {
		this.mainLoopLock = mainLoopLock;
		this.safeGlobals = safeGlobals;
		this.runInClientThreadQueue = runInClientThreadQueue;
	}

	public void onCompatibleClientTick(CompatibleClientTickEvent event) {		
		if(event.getPhase() == Phase.START) {
			mainLoopLock.lock();
		} else if(event.getPhase() == Phase.END) {
			mainLoopLock.unlock();
			processRunInClientThreadQueue();
			safeGlobals.objectMouseOver.set(compatibility.getObjectMouseOver());
			if(Minecraft.getMinecraft().thePlayer != null) {
				safeGlobals.currentItemIndex.set(Minecraft.getMinecraft().thePlayer.inventory.currentItem);
			}
		}
	}

	private void processRunInClientThreadQueue() {
		Runnable r;
		while((r = runInClientThreadQueue.poll()) != null) {
			r.run();
		}
	}
}
