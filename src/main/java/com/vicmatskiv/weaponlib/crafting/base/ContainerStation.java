package com.vicmatskiv.weaponlib.crafting.base;

import java.util.function.Supplier;

import com.vicmatskiv.weaponlib.compatibility.CompatibleContainer;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerStation extends CompatibleContainer {
	

	protected int page;
	protected Supplier<Integer> currentPageSupplier = () -> page;
	
	
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	

}
