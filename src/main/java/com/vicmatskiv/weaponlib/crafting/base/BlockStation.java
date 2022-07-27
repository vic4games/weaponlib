package com.vicmatskiv.weaponlib.crafting.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockStation extends Block {
	
	public BlockStation(String name, Material materialIn) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	

	

}
