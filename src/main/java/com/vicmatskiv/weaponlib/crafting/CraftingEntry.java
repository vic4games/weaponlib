package com.vicmatskiv.weaponlib.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CraftingEntry {
	
	private Item item;
	private int count;
	private String oreDictionary;
	
	public CraftingEntry(Item i, int c) {
		this.item = i;
		this.count = c;
	}
	
	public CraftingEntry(Block i, int c) {
		this.item = Item.getItemFromBlock(i);
		this.count = c;
	}
	 
	public CraftingEntry(Item dismantle, String oreDictionary, int count) {
		this.item = dismantle;
		this.oreDictionary = oreDictionary;
		this.count = count;
	}
	
	public CraftingEntry(Block dismantle, String oreDictionary, int count) {
		this.item = Item.getItemFromBlock(dismantle);
		this.oreDictionary = oreDictionary;
		this.count = count;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public String getOreDictionaryEntry() {
		return this.oreDictionary;
	}
	
	public boolean isOreDictionary() {
		return this.oreDictionary != null && this.oreDictionary.length() != 0;
	}

}
