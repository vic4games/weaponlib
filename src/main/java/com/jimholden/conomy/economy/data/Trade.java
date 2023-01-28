package com.jimholden.conomy.economy.data;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class Trade {
	
	public int tradeID = 0;
	public int baseStock = 0;
	public int currentStock = 0;
	public ItemStack itemStack = ItemStack.EMPTY;
	public double basePrice = 0;
	
	public Trade(int id, int base, int current, double basePrice, ItemStack stack) {
		this.tradeID = id;
		this.baseStock = base;
		this.currentStock = current;
		this.basePrice = basePrice;
		this.itemStack = stack;
	}
	
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound comp = new NBTTagCompound();
		comp.setInteger("id", this.tradeID);
		comp.setInteger("base", this.baseStock);
		comp.setInteger("current", this.currentStock);
		comp.setDouble("basePrice", this.basePrice);
		
		comp.setTag("itemStack", itemStack.serializeNBT());
		
		return comp;
		
	}
	
	public static Trade readNBT(NBTTagCompound comp) {
		ItemStack stack = new ItemStack(comp.getCompoundTag("itemStack"));
		int baseStock = comp.getInteger("base");
		int currentStock = comp.getInteger("current");
		double basePrice = comp.getDouble("basePrice");
		int id = comp.getInteger("id");
		return new Trade(id, baseStock, currentStock, basePrice, stack);
	}

}
