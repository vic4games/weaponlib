package com.vicmatskiv.weaponlib.crafting.items;

import com.vicmatskiv.weaponlib.CommonModContext;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;



public class CraftingItem extends Item {
	
	// The item that this is turned into as scrapped, can be null.
	private Item recoveryScrap;
	
	// The percentage (0.0 - 1.0) of material that will be recovered.
	private double recoveryPercentage;
	
	
	public CraftingItem(String name, String modID, Item recoveryScrap, double recoveryPercentage, CreativeTabs tab) {
		setUnlocalizedName(modID + "_" + name);
		setMaxStackSize(64);
		setCreativeTab(tab);
		
		this.recoveryPercentage = recoveryPercentage;
		this.recoveryScrap = recoveryScrap;
	}
	
	public boolean turnsIntoScrap() {
		return recoveryScrap != null;
	}
	
	public double getRecoveryPercentage() {
		return this.recoveryPercentage;
	}
	
	public Item getRecoveryScrap() {
		return this.recoveryScrap;
	}
	
	
	

}
