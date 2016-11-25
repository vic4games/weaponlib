package com.vicmatskiv.weaponlib;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.input.Mouse;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;

class ClientWeaponTicker extends Thread {
	
	private boolean mouseWasPressed;
	
	private AtomicBoolean running = new AtomicBoolean(true);
	private SafeGlobals safeGlobals;
	private FireManager fireManager;
	private ReloadManager reloadManager;

	public ClientWeaponTicker(SafeGlobals safeGlobals, FireManager fireManager, ReloadManager reloadManager) {
		this.safeGlobals = safeGlobals;
		this.fireManager = fireManager;
		this.reloadManager = reloadManager;
	}

	void shutdown() {
		running.set(false);
	}
	
	public void run() {
		
		int currentItemIndex = safeGlobals.currentItemIndex.get();
		while(running.get()) {
			try {
				Weapon currentWeapon = getCurrentWeapon();
				EntityPlayerSP player = FMLClientHandler.instance().getClientPlayerEntity();

				if(Mouse.isCreated() && Mouse.isButtonDown(0)) {
					// Capture the current item index
					currentItemIndex = safeGlobals.currentItemIndex.get();
					if(!mouseWasPressed) {
						mouseWasPressed = true;
					}
					if(currentWeapon != null && !safeGlobals.guiOpen.get() && !isInteracting()) {
						fireManager.clientTryFire(player);
					}
				} else if(mouseWasPressed || currentItemIndex != safeGlobals.currentItemIndex.get()) { // if switched item while pressing mouse down and then released
					mouseWasPressed = false;
					currentItemIndex = safeGlobals.currentItemIndex.get();
					if(currentWeapon != null) {
						fireManager.clientTryStopFire(player);
					}
				}
				
				if(currentWeapon != null) {
					update(player);
				}
				Thread.sleep(10);
			} catch(InterruptedException e) {
				break;
			}
		}
	}
	
	private void update(EntityPlayerSP player) {
		//currentWeapon.tick(player);
		reloadManager.update(player.getHeldItem(), player);
		fireManager.update(player.getHeldItem(), player);
	}

	private boolean isInteracting() {
		return false;
		/*
		MovingObjectPosition object = safeGlobals.objectMouseOver.get();
		return object.typeOfHit == MovingObjectType.BLOCK || object.typeOfHit == MovingObjectType.ENTITY;
		*/
	}
	
	private Weapon getCurrentWeapon() {
		EntityPlayer clientPlayerEntity = FMLClientHandler.instance().getClientPlayerEntity();
		if(clientPlayerEntity == null) return null;
		ItemStack heldItem = clientPlayerEntity.getHeldItem();
		return heldItem != null && heldItem.getItem() instanceof Weapon ? (Weapon) heldItem.getItem() : null;
	}
}
