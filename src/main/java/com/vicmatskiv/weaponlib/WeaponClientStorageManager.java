package com.vicmatskiv.weaponlib;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

class WeaponClientStorageManager {
	
	private class Key {
		UUID playerUuid;
		Weapon weapon;
		
		Key(UUID playerUuid, Weapon weapon) {
			this.playerUuid = playerUuid;
			this.weapon = weapon;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((playerUuid == null) ? 0 : playerUuid.hashCode());
			result = prime * result + ((weapon == null) ? 0 : weapon.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (playerUuid == null) {
				if (other.playerUuid != null)
					return false;
			} else if (!playerUuid.equals(other.playerUuid))
				return false;
			if (weapon == null) {
				if (other.weapon != null)
					return false;
			} else if (weapon != other.weapon)
				return false;
			return true;
		}
		
	}
	
	private Map<Key, WeaponClientStorage> weaponClientStorage = new HashMap<>();

	WeaponClientStorage getWeaponClientStorage(EntityPlayer player, Weapon weapon) {
		if(player == null) return null;
		return weaponClientStorage.computeIfAbsent(new Key(player.getPersistentID(), weapon), (w) ->
			{
				ItemStack itemStack = player.getHeldItem();
				return itemStack.getTagCompound() != null ?
						new WeaponClientStorage(Tags.getState(itemStack), 
						Tags.getAmmo(itemStack), weapon.builder.zoom, 
						Tags.getRecoil(player.getHeldItem()), weapon.builder.fireRate) : null;
			});
	}
}
