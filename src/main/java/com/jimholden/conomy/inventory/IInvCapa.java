package com.jimholden.conomy.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

public interface IInvCapa {
	
	
	public void deserializeNBT(NBTBase nbtBase);
	
	public ItemStackHandler getHandler();
	
	public void setStackInSlot(int slot, ItemStack stack);
	
	public ItemStack getStackInSlot(int slot);
	

}
