package com.vicmatskiv.weaponlib.crafting;

import java.util.ArrayList;
import java.util.HashMap;

import com.vicmatskiv.weaponlib.Weapon;

public class CraftingRegistry {
	
	private static ArrayList<Weapon> craftingRegistry = new ArrayList<>();
	
	public static ArrayList<Weapon> getCraftingRegistry() {
		return craftingRegistry;
	}
	
	public static void register(Weapon weapon) {
		craftingRegistry.add(weapon);
	}

}
