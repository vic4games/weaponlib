package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.List;

import com.vicmatskiv.weaponlib.compatibility.CompatibleItemMethods;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAmmo extends Item implements CompatibleItemMethods {
	
	private List<Weapon> compatibleWeapons = new ArrayList<>();

	public void addCompatibleWeapon(Weapon weapon) {
		compatibleWeapons.add(weapon);
	}
	
	@Override
	public void addInformation(ItemStack itemStack, List<String> info, boolean flag) {
	    info.add("Compatible guns:");
        compatibleWeapons.forEach((weapon) -> info.add(weapon.getName()));
	}
}
