package com.jimholden.conomy.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CreditStorage implements IStorage<ICredit>{

	@Override
	public NBTBase writeNBT(Capability<ICredit> capability, ICredit instance, EnumFacing side) {
		// TODO Auto-generated method stub
		return new NBTTagInt(instance.getBalance());
	}

	@Override
	public void readNBT(Capability<ICredit> capability, ICredit instance, EnumFacing side, NBTBase nbt) {
		instance.set(((NBTPrimitive) nbt).getInt());
		
	}

}
