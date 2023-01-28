package com.jimholden.conomy.medical;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ConsciousProvider implements ICapabilitySerializable<NBTBase>{
	
	@CapabilityInject(IConscious.class)
	public static final Capability<IConscious> CONSCIOUS = null;

	//private IConscious instance = CONSCIOUS.getDefaultInstance();
	
	private IConscious instance = CONSCIOUS.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capability == CONSCIOUS;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capability == CONSCIOUS ? CONSCIOUS .<T> cast(this.instance) : null;
	}
	
	@Override
	public NBTBase serializeNBT() {
		// TODO Auto-generated method stub
		return CONSCIOUS.getStorage().writeNBT(CONSCIOUS, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		// TODO Auto-generated method stub
		CONSCIOUS.getStorage().readNBT(CONSCIOUS, this.instance, null, nbt);
		
	}

	
	

}
