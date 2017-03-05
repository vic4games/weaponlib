package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.input.Mouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class ClientWeaponTicker extends Thread {
	
	boolean buttonsPressed[] = new boolean[2];
	long buttonsPressedTimestamps[] = new long[2];
	
	private AtomicBoolean running = new AtomicBoolean(true);

	private ClientModContext clientModContext;

	public ClientWeaponTicker(ClientModContext clientModContext) {
		this.clientModContext = clientModContext;
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
				
				if(!Mouse.isCreated()) {
					continue;
				}
				
				if(Mouse.isButtonDown(1)) {
					if(!buttonsPressed[1]) {
						buttonsPressed[1] = true;
						buttonsPressedTimestamps[1] = System.currentTimeMillis();
						
						if(currentWeapon != null && !safeGlobals.guiOpen.get() && !isInteracting()) {
							clientModContext.runSyncTick(() -> { currentWeapon.toggleAiming();});
						}
					}
				} else if(buttonsPressed[1]) {
					buttonsPressed[1] = false;
				}

				if(Mouse.isButtonDown(0)) {
					// Capture the current item index
					currentItemIndex = safeGlobals.currentItemIndex.get();
					if(!buttonsPressed[0]) {
						buttonsPressed[0] = true;
					}
					if(currentWeapon != null && !safeGlobals.guiOpen.get() && !isInteracting()) {
						clientModContext.runSyncTick(() -> { currentWeapon.tryFire(player);});
					}
				} else if(buttonsPressed[0] || currentItemIndex != safeGlobals.currentItemIndex.get()) { // if switched item while pressing mouse down and then released
					buttonsPressed[0] = false;
					currentItemIndex = safeGlobals.currentItemIndex.get();
					if(currentWeapon != null) {
						clientModContext.runSyncTick(() -> { currentWeapon.tryStopFire(player);});
					}
				}

				update(player);
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
				((Updatable) item).update(player);
			}
		});
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
