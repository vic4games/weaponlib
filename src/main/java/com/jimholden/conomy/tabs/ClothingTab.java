package com.jimholden.conomy.tabs;

import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ClothingTab extends CreativeTabs
{
	public ClothingTab() 
	{
		super("clothingTab");
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(ModItems.GHOSTFACEMASK);//ItemInit.COPPER_INGOT);
	}
}