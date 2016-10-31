package com.vicmatskiv.weaponlib;

import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.FMLClientHandler;

class ClientWeaponTicker extends Thread {
	
	private boolean mouseWasPressed;
	
	private AtomicBoolean running = new AtomicBoolean(true);
	private SafeGlobals safeGlobals;

	public ClientWeaponTicker(SafeGlobals safeGlobals) {
		this.safeGlobals = safeGlobals;
	}

	void shutdown() {
		running.set(false);
	}
	
	public void run() {
		
		while(running.get()) {
			try {
				Weapon currentWeapon = getCurrentWeapon();
				EntityClientPlayerMP player = FMLClientHandler.instance().getClientPlayerEntity();
				if(Mouse.isCreated() && Mouse.isButtonDown(0)) {
					if(!mouseWasPressed) {
						mouseWasPressed = true;
					}
					if(currentWeapon != null && !safeGlobals.guiOpen.get() && !isInteracting()) {
						currentWeapon.clientTryFire(player);
					}
				} else if(mouseWasPressed) {
					mouseWasPressed = false;
					if(currentWeapon != null) {
						currentWeapon.clientTryStopFire(player);
					}
				}
				
				if(currentWeapon != null) {
					currentWeapon.tick(player);
				}
				Thread.sleep(10);
			} catch(InterruptedException e) {
				break;
			}
		}
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