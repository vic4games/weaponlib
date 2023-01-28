package com.jimholden.conomy.tabs;

import com.jimholden.conomy.init.ModBlocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BuildingTab extends CreativeTabs
{
	public BuildingTab() 
	{
		super("buildingtab");
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(Item.getItemFromBlock(ModBlocks.MOSSYPLANKSOAK));//ItemInit.COPPER_INGOT);
	}
}