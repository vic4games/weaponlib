package com.vicmatskiv.weaponlib.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IModernCrafting {
	
	public ItemStack[] getModernRecipe();
	public Item getItem();
	public CraftingGroup getCraftingGroup();

}
