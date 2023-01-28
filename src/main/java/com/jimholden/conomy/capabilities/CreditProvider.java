package com.jimholden.conomy.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CreditProvider implements ICapabilitySerializable<NBTBase>{
	
	@CapabilityInject(ICredit.class)
	public static final Capability<ICredit> CREDIT_CAP = null;
	
	private ICredit instance = CREDIT_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capability == CREDIT_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capability == CREDIT_CAP ? CREDIT_CAP .<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		// TODO Auto-generated method stub
		return CREDIT_CAP.getStorage().writeNBT(CREDIT_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		// TODO Auto-generated method stub
		CREDIT_CAP.getStorage().readNBT(CREDIT_CAP, this.instance, null, nbt);
		
	}
	
	
	

}
