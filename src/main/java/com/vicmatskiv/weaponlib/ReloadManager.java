package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.Weapon.State;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class ReloadManager {
	
	private ModContext modContext;
	
	ReloadManager(ModContext modContext) {
		this.modContext = modContext;
	}

	@SideOnly(Side.CLIENT)
	void initiateReload(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
		
		if (storage.getState() != State.RELOAD_REQUESTED && storage.getState() != State.RELOAD_CONFIRMED
				&& storage.getCurrentAmmo().get() < weapon.builder.ammoCapacity) {
			storage.getReloadingStopsAt().set(player.worldObj.getTotalWorldTime() + Weapon.MAX_RELOAD_TIMEOUT_TICKS);
			storage.setState(State.RELOAD_REQUESTED);
			modContext.getChannel().sendToServer(new ReloadMessage(weapon));
		}
	}

	@SideOnly(Side.CLIENT)
	void completeReload(ItemStack itemStack, EntityPlayer player, int ammo, boolean quietly) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
			
		if (storage.getState() == State.RELOAD_REQUESTED) {
			storage.getCurrentAmmo().set(ammo);
			if (ammo > 0 && !quietly) {
				storage.setState(State.RELOAD_CONFIRMED);
				long reloadingStopsAt = player.worldObj.getTotalWorldTime() + weapon.builder.reloadingTimeout;
				storage.getReloadingStopsAt().set(reloadingStopsAt);
				player.playSound(weapon.builder.reloadSound, 1.0F, 1.0F);
			} else {
				storage.setState(State.READY);
			}
		}
	}

	//@SideOnly(Side.SERVER)
	void reload(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		if (itemStack.stackTagCompound != null && !player.isSprinting()) {
			if (player.inventory.consumeInventoryItem(weapon.builder.ammo)) {
				Tags.setAmmo(itemStack, weapon.builder.ammoCapacity);
				modContext.getChannel().sendTo(new ReloadMessage(weapon, weapon.builder.ammoCapacity), (EntityPlayerMP) player);
				player.worldObj.playSoundToNearExcept(player, weapon.builder.reloadSound, 1.0F, 1.0F);
			} else {
				Tags.setAmmo(itemStack, 0);
				modContext.getChannel().sendTo(new ReloadMessage(weapon, 0), (EntityPlayerMP) player);
			}
		}
	}
	
	void update(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if(storage == null) {
			return;
		}
		if(storage.getState() == State.RELOAD_REQUESTED || storage.getState() == State.RELOAD_CONFIRMED) {
			long totalWorldTime = player.worldObj.getTotalWorldTime();
			if(storage.getReloadingStopsAt().get() <= totalWorldTime) {
				storage.setState(State.READY);
			}
		}
	}
}
