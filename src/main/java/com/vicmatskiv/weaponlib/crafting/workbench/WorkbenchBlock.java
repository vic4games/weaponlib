package com.vicmatskiv.weaponlib.crafting.workbench;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class WorkbenchBlock extends Block {

	public WorkbenchBlock(String name, Material materialIn) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);

	
	}

}
