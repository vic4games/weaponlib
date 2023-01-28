package com.jimholden.conomy.blocks.building;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
 
public class ModdedStairs extends BlockStairs implements IHasModel {
	
	
	public boolean seeThrough = false;

	public ModdedStairs(String name, Material material, IBlockState modelState, boolean seeThrough) {
		super(modelState);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		
		
		
		this.seeThrough = seeThrough;
		
		
		this.fullBlock = false;
		this.translucent = true;
		this.useNeighborBrightness = true;
		
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
	
	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		if(seeThrough) return false;
		return true;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	

	
	@Override
	public BlockRenderLayer getBlockLayer() {
		if(seeThrough) return BlockRenderLayer.TRANSLUCENT;
		else return super.getBlockLayer();
	}
	

	@Override
	public int getLightOpacity(IBlockState state) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
		
	}

}
