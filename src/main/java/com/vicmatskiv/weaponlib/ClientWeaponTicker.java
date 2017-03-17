package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.input.Mouse;

import com.vicmatskiv.weaponlib.melee.ItemMelee;

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
				
				
				if(!Mouse.isCreated()) {
					continue;
				}
				
				if(Mouse.isButtonDown(1)) {
					if(!buttonsPressed[1]) {
						buttonsPressed[1] = true;
						buttonsPressedTimestamps[1] = System.currentTimeMillis();
						
						if(!safeGlobals.guiOpen.get() && !isInteracting()) {
							clientModContext.runSyncTick(this::onRightButtonDown);
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
					if(!safeGlobals.guiOpen.get() && !isInteracting()) {
						clientModContext.runSyncTick(this::onLeftButtonDown);
					}
				} else if(buttonsPressed[0] || currentItemIndex != safeGlobals.currentItemIndex.get()) { // if switched item while pressing mouse down and then released
					buttonsPressed[0] = false;
					currentItemIndex = safeGlobals.currentItemIndex.get();
					clientModContext.runSyncTick(this::onLeftButtonUp);
				}

				clientModContext.runSyncTick(this::onTick);
				Thread.sleep(10);
			} catch(InterruptedException e) {
				break;
			}
		}
	}

    private void onLeftButtonUp() {
        EntityPlayer player = compatibility.getClientPlayer();
        Item item = getHeldItemMainHand(player);
        if(item instanceof Weapon) {
            ((Weapon) item).tryStopFire(player);
        }
    }

    private void onLeftButtonDown() {
        EntityPlayer player = compatibility.getClientPlayer();
        Item item = getHeldItemMainHand(player);
        if(item instanceof Weapon) {
            ((Weapon) item).tryFire(player);
        } else if(item instanceof ItemMelee) {
            ((ItemMelee) item).attack(player);
        }
    }

    private void onRightButtonDown() {
        EntityPlayer player = compatibility.getClientPlayer();
        Item item = getHeldItemMainHand(player);
        if(item instanceof Weapon) {
            ((Weapon) item).toggleAiming();
        }
    }
	
	private void onTick() {
	    EntityPlayer player = compatibility.getClientPlayer();
	    Item item = getHeldItemMainHand(player);
        if(item instanceof Updatable) {
            ((Updatable) item).update(player);
        }
	}

	private boolean isInteracting() {
		return false;
	}
	
	private Item getHeldItemMainHand(EntityPlayer player) {
		if(player == null) return null;
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		return itemStack != null ? itemStack.getItem() : null;
	}
}
