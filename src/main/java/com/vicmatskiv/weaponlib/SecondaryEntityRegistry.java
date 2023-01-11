package com.vicmatskiv.weaponlib;

import java.util.HashMap;


import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public class SecondaryEntityRegistry {
	
	public static HashMap<String, Class<? extends Entity>> map = new HashMap<>();
	public static HashMap<Integer, Item> pickupMap = new HashMap<>();

}
