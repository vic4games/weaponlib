package com.jimholden.conomy.blocks.building.glass;

import com.jimholden.conomy.blocks.building.ModdedStairs;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

public class GlassStairs extends ModdedStairs {

	public GlassStairs(String name, Material material, IBlockState modelState, boolean seeThrough) {
		super(name, material, modelState, seeThrough);
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

}
