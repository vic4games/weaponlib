package com.vicmatskiv.weaponlib;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.vicmatskiv.weaponlib.Weapon.State;

public class FireManager {
	
	private static final float FLASH_X_OFFSET_ZOOMED = 0f;
	private static final float FLASH_X_OFFSET_NORMAL = 0.1f;
	
	private ModContext modContext;
	private Random random = new Random();

	FireManager(ModContext modContext) {
		this.modContext = modContext;
	}

	void clientTryFire(EntityPlayer player) {
		ItemStack itemStack = player.getHeldItem();
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);

		if(storage == null) return;
		
		if(storage.getState() == State.PAUSED) {
			storage.setEjectSpentRoundStartedAt(System.currentTimeMillis());
			storage.setState(State.EJECT_SPENT_ROUND);
			modContext.runSyncTick(() -> {
				player.playSound(weapon.builder.ejectSpentRoundSound, 1F, 1F);
			});
			return;
		}
		
		boolean readyToShootAccordingToFireRate = System.currentTimeMillis() - storage.getLastShotFiredAt() >= 50f / weapon.builder.fireRate;
		if(!player.isSprinting() 
				&& (storage.getState() == State.READY || storage.getState() == State.SHOOTING)
				&& readyToShootAccordingToFireRate
				&& storage.getShots() < weapon.builder.maxShots
				&& storage.getCurrentAmmo().getAndAccumulate(0, (current, ignore) -> current > 0 ? current - 1 : 0) > 0) {
			
			storage.setState(State.SHOOTING);
			modContext.getChannel().sendToServer(new TryFireMessage(true));
			ItemStack heldItem = player.getHeldItem();
			
			modContext.runSyncTick(() -> {
				player.playSound(modContext.getAttachmentManager().isSilencerOn(heldItem) ? weapon.builder.silencedShootSound : weapon.builder.shootSound, 1F, 1F);
			});
			
			player.rotationPitch = player.rotationPitch - storage.getRecoil();						
			float rotationYawFactor = -1.0f + random.nextFloat() * 2.0f;
			player.rotationYaw = player.rotationYaw + storage.getRecoil() * rotationYawFactor;
			
			if(weapon.builder.flashIntensity > 0) {
				EffectManager.getInstance().spawnFlashParticle(player, weapon.builder.flashIntensity,
						Weapon.isZoomed(itemStack) ? FLASH_X_OFFSET_ZOOMED : FLASH_X_OFFSET_NORMAL);
			}
			
			EffectManager.getInstance().spawnSmokeParticle(player);
			
			storage.setLastShotFiredAt(System.currentTimeMillis());
			
			storage.addShot();
			
		}
	}

	void tryFire(EntityPlayer player, ItemStack itemStack) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		Weapon weapon = (Weapon) itemStack.getItem();
		int currentAmmo = Tags.getAmmo(itemStack);
		if(currentAmmo > 0) {
			if(!Weapon.isZoomed(itemStack)) {
				Tags.setAimed(itemStack, true);
			}
			Tags.setAmmo(itemStack, currentAmmo - 1);
			for(int i = 0; i < weapon.builder.pellets; i++) {
				player.worldObj.spawnEntityInWorld(weapon.builder.spawnEntityWith.apply(weapon, player));
			}
			player.worldObj.playSoundToNearExcept(player, modContext.getAttachmentManager().isSilencerOn(itemStack) ? weapon.builder.silencedShootSound : weapon.builder.shootSound, 1.0F, 1.0F);
		} else {
			System.err.println("Invalid state: attempted to fire a weapon without ammo");
		}
	}
	
	void tryStopFire(EntityPlayer player, ItemStack itemStack) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		if(!Weapon.isZoomed(itemStack)) {
			Tags.setAimed(itemStack, false);
		}
	}

	void clientTryStopFire(EntityPlayer player) {
		ItemStack itemStack = player.getHeldItem();
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if(storage == null) return;
		if(storage.getState() == State.SHOOTING) {
			storage.resetShots();
			if(weapon.ejectSpentRoundRequired()) {
				storage.setState(State.PAUSED);
			} else {
				storage.setState(State.READY);
			}
//			if(storage.getLastShotFiredAt() + weapon.builder.pumpTimeoutMilliseconds <= System.currentTimeMillis()) {
//				storage.setState(State.READY);
//			} else {
//				storage.setState(State.EJECT_SPENT_ROUND_REQUIRED);
//			}
			modContext.getChannel().sendToServer(new TryFireMessage(false));
		}
	}

	void update(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if(storage == null) return;
		
		if(storage.getState() == State.EJECT_SPENT_ROUND && storage.getEjectSpentRoundStartedAt() + weapon.builder.pumpTimeoutMilliseconds <= System.currentTimeMillis()) {
			storage.setState(State.READY);
		}
	}

}
