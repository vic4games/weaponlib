package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAmmo extends Item {
	
	private List<Weapon> compatibleWeapons = new ArrayList<>();

	public void addCompatibleWeapon(Weapon weapon) {
		compatibleWeapons.add(weapon);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List info, boolean p_77624_4_) {
		info.add("Compatible guns:");

		compatibleWeapons.forEach((weapon) -> info.add(weapon.getName()));
	}
}
