package com.jimholden.conomy.tabs;

import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DrugTab extends CreativeTabs
{
	public DrugTab() 
	{
		super("drugTab");
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(ModItems.POWDER);//ItemInit.COPPER_INGOT);
	}
}