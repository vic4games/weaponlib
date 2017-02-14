package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.input.Mouse;

import com.vicmatskiv.weaponlib.ReloadAspect.ReloadContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

class ClientWeaponTicker extends Thread {
	
	private boolean mouseWasPressed;
	
	private AtomicBoolean running = new AtomicBoolean(true);
	private SafeGlobals safeGlobals;
	private FireManager fireManager;
	private ReloadManager reloadManager;
	private ReloadAspect reloadAspect;

	public ClientWeaponTicker(SafeGlobals safeGlobals, FireManager fireManager, ReloadManager reloadManager, ReloadAspect reloadAspect) {
		this.safeGlobals = safeGlobals;
		this.fireManager = fireManager;
		this.reloadManager = reloadManager;
		this.reloadAspect = reloadAspect;
	}

	void shutdown() {
		running.set(false);
	}
	
	public void run() {
		
		int currentItemIndex = safeGlobals.currentItemIndex.get();
		while(running.get()) {
			try {
				Weapon currentWeapon = getCurrentWeapon();
				EntityPlayer player = compatibility.getClientPlayer();

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
	
	private void update(EntityPlayer player) {
		
		ReloadContext reloadContext = reloadAspect.contextForPlayer(player);
		if(reloadContext != null) {
			reloadAspect.onUpdate(reloadContext);
		}
		
		reloadManager.update(compatibility.getHeldItemMainHand(player), player);
		fireManager.update(compatibility.getHeldItemMainHand(player), player);
	}

	private boolean isInteracting() {
		return false;
	}
	
	private Weapon getCurrentWeapon() {
		EntityPlayer player = compatibility.getClientPlayer();
		if(player == null) return null;
		ItemStack heldItem = compatibility.getHeldItemMainHand(player);
		return heldItem != null && heldItem.getItem() instanceof Weapon ? (Weapon) heldItem.getItem() : null;
	}
}
