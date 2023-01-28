package com.jimholden.conomy.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryCapability implements IInvCapa {
	
	ItemStackHandler handler = new ItemStackHandler(14);


	@Override
	public void deserializeNBT(NBTBase nbtBase) {
		handler.deserializeNBT((NBTTagCompound) nbtBase);
	}

	@Override
	public ItemStackHandler getHandler() {
		return handler;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		handler.setStackInSlot(slot, stack);
		
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		// TODO Auto-generated method stub
		return handler.getStackInSlot(slot);
	}
}
