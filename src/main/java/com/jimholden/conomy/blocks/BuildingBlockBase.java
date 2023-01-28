package com.jimholden.conomy.blocks;

import com.jimholden.conomy.init.ModBlocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BuildingBlockBase extends BetterBlockBase {
	protected static final AxisAlignedBB BOUND = new AxisAlignedBB(-1, 0, -1, 2, 1, 2);

	public BuildingBlockBase(String name, Material material) {
		super(name, material);
		// TODO Auto-generated constructor stub
	} 
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		// TODO Auto-generated method stub
		return BOUND;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		return BOUND;
	}
	
	
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		if(worldIn.getBlockState(pos.down()).getBlock() != ModBlocks.FADEDWHITEBRICK)
		{
			return false;
		} else {
			return true;
		}
	}

}
