package com.jimholden.conomy.tabs;

import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LootableBlocksTab extends CreativeTabs
{
	public LootableBlocksTab() 
	{
		super("lootableBlocksTab");
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(ModBlocks.LOOTCRATE);//ItemInit.COPPER_INGOT);
	}
}