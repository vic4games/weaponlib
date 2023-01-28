package com.jimholden.conomy.blocks;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RingLightBlock extends CornerRotatingBlock implements IHasModel {

	public RingLightBlock(String name, Material material) {
		super(name, material);
		setLightLevel(2.0F);
		// TODO Auto-generated constructor stub
	}
	
	
	

}
