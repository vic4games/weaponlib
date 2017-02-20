package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.input.Mouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class ClientWeaponTicker extends Thread {
	
	private boolean mouseWasPressed;
	
	private AtomicBoolean running = new AtomicBoolean(true);
//	private SafeGlobals safeGlobals;
//	private FireManager fireManager;
	private ReloadManager reloadManager;
	private WeaponReloadAspect reloadAspect;
	private FireAspect fireAspect;
	private ClientModContext clientModContext;

	public ClientWeaponTicker(ClientModContext clientModContext, FireManager fireManager, ReloadManager reloadManager/*, WeaponReloadAspect reloadAspect*/) {
		this.clientModContext = clientModContext;
//		this.fireManager = fireManager;
		this.reloadManager = reloadManager;
		//this.reloadAspect = reloadAspect;
	}

	void shutdown() {
		running.set(false);
	}
	
	public void run() {
		SafeGlobals safeGlobals = clientModContext.getSafeGlobals();
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
//						fireManager.clientTryFire(player);
						clientModContext.runSyncTick(() -> { currentWeapon.tryFire(player);});
						
					}
				} else if(mouseWasPressed || currentItemIndex != safeGlobals.currentItemIndex.get()) { // if switched item while pressing mouse down and then released
					mouseWasPressed = false;
					currentItemIndex = safeGlobals.currentItemIndex.get();
					if(currentWeapon != null) {
//						fireManager.clientTryStopFire(player);
//						fireAspect.onFireButtonRelease(extendedState);
						
						clientModContext.runSyncTick(() -> { currentWeapon.tryStopFire(player);}
					);
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
		
		clientModContext.runSyncTick(() -> {
			Item item = getHeldItemMainHand();
			if(item instanceof Updatable) {
				((Updatable) item).updateMainHeldItemForPlayer(player);
			}
		});
		
		reloadManager.update(compatibility.getHeldItemMainHand(player), player);
//		fireManager.update(compatibility.getHeldItemMainHand(player), player);
	}

	private boolean isInteracting() {
		return false;
	}
	
	private Weapon getCurrentWeapon() {
		Item item = getHeldItemMainHand();
		return item instanceof Weapon ? (Weapon) item : null;
	}
	
	private Item getHeldItemMainHand() {
		EntityPlayer player = compatibility.getClientPlayer();
		if(player == null) return null;
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		return itemStack != null ? itemStack.getItem() : null;
	}
}
