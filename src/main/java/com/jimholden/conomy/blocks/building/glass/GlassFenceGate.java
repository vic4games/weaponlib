package com.jimholden.conomy.blocks.building.glass;

import com.jimholden.conomy.blocks.building.ModdedFenceGate;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.util.BlockRenderLayer;

public class GlassFenceGate extends ModdedFenceGate {

	public GlassFenceGate(String name, EnumType p_i46394_1_) {
		super(name, p_i46394_1_);
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

}
