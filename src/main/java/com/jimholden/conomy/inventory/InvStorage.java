package com.jimholden.conomy.inventory;

import com.jimholden.conomy.capabilities.ICredit;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InvStorage implements IStorage<IInvCapa>{


	@Override
	public NBTBase writeNBT(Capability<IInvCapa> capability, IInvCapa instance, EnumFacing side) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("inventory", instance.getHandler().serializeNBT());
		return compound;
		//return new NBTTagInt(instance.isDowned());
	}

	@Override
	public void readNBT(Capability<IInvCapa> capability, IInvCapa instance, EnumFacing side, NBTBase nbt) {
		instance.deserializeNBT(((NBTTagCompound)nbt).getTag("inventory"));
		
	}

}
