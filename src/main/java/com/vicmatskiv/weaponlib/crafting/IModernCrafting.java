package com.vicmatskiv.weaponlib.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IModernCrafting {
	
	public CraftingEntry[] getModernRecipe();
	public Item getItem();
	public CraftingGroup getCraftingGroup();

}
