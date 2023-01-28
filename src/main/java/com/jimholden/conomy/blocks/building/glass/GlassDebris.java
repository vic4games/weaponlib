package com.jimholden.conomy.blocks.building.glass;

import com.jimholden.conomy.blocks.Debris;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class GlassDebris extends Debris {

	public GlassDebris(String name, Material material) {
		super(name, material);
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

}
