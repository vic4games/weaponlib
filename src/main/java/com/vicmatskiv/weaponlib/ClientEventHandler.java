package com.vicmatskiv.weaponlib;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventHandler {
	
	//private static final int DEFAULT_RIGHT_CLICK_TIMEOUT = 200;
	
	private Lock mainLoopLock = new ReentrantLock();
	private SafeGlobals safeGlobals;
	private Queue<Runnable> runInClientThreadQueue;
//	private ModContext modContext;
//	private long lastRightClickTimestamp;
//	private long rightClickTimeout = DEFAULT_RIGHT_CLICK_TIMEOUT;

	public ClientEventHandler(ModContext modContext, Lock mainLoopLock, SafeGlobals safeGlobals, Queue<Runnable> runInClientThreadQueue) {
		//this.modContext = modContext;
		this.mainLoopLock = mainLoopLock;
		this.safeGlobals = safeGlobals;
		this.runInClientThreadQueue = runInClientThreadQueue;
	}
	
//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent
//	public void onMouse(MouseEvent event) {
//		ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
//		if(heldItem != null && (event.getButton() == 0 || event.getButton() == 1) && heldItem.getItem() instanceof Weapon) {
//			if(event.getButton() == 1 && lastRightClickTimestamp + rightClickTimeout < System.currentTimeMillis()) {
//				lastRightClickTimestamp = System.currentTimeMillis();
//				modContext.getChannel().sendToServer(ChangeSettingsMessage.createToggleAimingMessage((Weapon) heldItem.getItem()));
//			}
//			event.setCanceled(true);
//		}
//	}


	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {		
		if(event.phase == Phase.START) {
			mainLoopLock.lock();
		} else if(event.phase == Phase.END) {
			mainLoopLock.unlock();
			processRunInClientThreadQueue();
			safeGlobals.objectMouseOver.set(Minecraft.getMinecraft().objectMouseOver);
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
