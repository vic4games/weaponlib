package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.TypeRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerWeaponState extends PlayerItemState<WeaponState> {
	
	static {
		TypeRegistry.getInstance().register(PlayerWeaponState.class);
	}

	public PlayerWeaponState() {
		super();
	}

	public PlayerWeaponState(EntityPlayer player, ItemStack itemStack) {
		super(player, itemStack);
	}

	public PlayerWeaponState(EntityPlayer player) {
		super(player);
	}
}
