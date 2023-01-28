package com.jimholden.conomy.blocks.building.glass;

import com.jimholden.conomy.blocks.building.ModdedSlab;

import net.minecraft.util.BlockRenderLayer;

public class GlassSlab extends ModdedSlab {

	public GlassSlab(String name) {
		super(name);
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isDouble() {
		return false;
	}

}
