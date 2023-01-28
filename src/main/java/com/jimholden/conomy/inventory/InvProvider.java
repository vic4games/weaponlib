package com.jimholden.conomy.inventory;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class InvProvider implements ICapabilitySerializable<NBTBase>{
	
	@CapabilityInject(IInvCapa.class)
	public static final Capability<IInvCapa> EXTRAINV = null;

	//private IConscious instance = CONSCIOUS.getDefaultInstance();
	
	private IInvCapa instance = EXTRAINV.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capability == EXTRAINV;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capability == EXTRAINV ? EXTRAINV .<T> cast(this.instance) : null;
	}
	
	@Override
	public NBTBase serializeNBT() {
		// TODO Auto-generated method stub
		return EXTRAINV.getStorage().writeNBT(EXTRAINV, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		// TODO Auto-generated method stub
		EXTRAINV.getStorage().readNBT(EXTRAINV, this.instance, null, nbt);
		
	}

	
	

}
