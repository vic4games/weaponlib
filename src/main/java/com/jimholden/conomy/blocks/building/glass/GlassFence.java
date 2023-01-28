package com.jimholden.conomy.blocks.building.glass;

import com.jimholden.conomy.blocks.building.ModdedFence;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class GlassFence extends ModdedFence {

	public GlassFence(String name, Material materialIn, MapColor mapColorIn) {
		super(name, materialIn, mapColorIn);
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

}
