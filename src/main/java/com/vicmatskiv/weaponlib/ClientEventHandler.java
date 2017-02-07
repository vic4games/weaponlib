package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent.Phase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

public class ClientEventHandler extends CompatibleClientEventHandler {

	private Lock mainLoopLock = new ReentrantLock();
	private SafeGlobals safeGlobals;
	private Queue<Runnable> runInClientThreadQueue;
	private long renderEndNanoTime;
	
	
	private ClientModContext modContext;

	public ClientEventHandler(ClientModContext modContext, Lock mainLoopLock, SafeGlobals safeGlobals, Queue<Runnable> runInClientThreadQueue) {
		this.modContext = modContext;
		this.mainLoopLock = mainLoopLock;
		this.safeGlobals = safeGlobals;
		this.runInClientThreadQueue = runInClientThreadQueue;
        this.renderEndNanoTime = System.nanoTime();
	}

	public void onCompatibleClientTick(CompatibleClientTickEvent event) {		
		if(event.getPhase() == Phase.START) {
			mainLoopLock.lock();
		} else if(event.getPhase() == Phase.END) {
			mainLoopLock.unlock();
			processRunInClientThreadQueue();
			safeGlobals.objectMouseOver.set(compatibility.getObjectMouseOver());
			if(compatibility.clientPlayer() != null) {
				safeGlobals.currentItemIndex.set(compatibility.clientPlayer().inventory.currentItem);
			}
		}
	}

	private void processRunInClientThreadQueue() {
		Runnable r;
		while((r = runInClientThreadQueue.poll()) != null) {
			r.run();
		}
	}
	
	static int counter = 0;
	
	@SubscribeEvent
	public final void onRenderTickEvent(TickEvent.RenderTickEvent event) {
		
		if(event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().renderViewEntity != null) {
			
			long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
			
			if(Weapon.isZoomed(null, Minecraft.getMinecraft().thePlayer.getHeldItem())) {
				modContext.getFramebuffer().bindFramebuffer(true);
				modContext.getSecondWorldRenderer().updateRenderer();
				modContext.getSecondWorldRenderer().renderWorld(event.renderTickTime, p_78471_2_);
				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
			}
				
			
			this.renderEndNanoTime = System.nanoTime();
		}		
	}
}
