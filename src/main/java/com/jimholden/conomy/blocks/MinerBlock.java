package com.jimholden.conomy.blocks;

import java.util.Random;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityMiner;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.util.Reference;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MinerBlock extends Block3D implements ITileEntityProvider{
	protected static final AxisAlignedBB BOUND = new AxisAlignedBB(0.1, 0, 0.1, 0.75, 0.4, 0.75);
	public int compatType;
	public int basePower;
	//private AxisAlignedBB BOUND = null;
	//public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	public MinerBlock(String name, Material material, int compatType, int basePower) {
		super(name, material);
		this.compatType = compatType;
		this.basePower = basePower;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 3;
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		super.breakBlock(worldIn, pos, state);
	}
	
	
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		// TODO Auto-generated method stub
		return Item.getItemFromBlock(ModBlocks.BASE_MINER);
	}
	
	@Override
	public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		// TODO Auto-generated method stub
		return super.canBeConnectedTo(world, pos, facing);
	}
	
	
	
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		// TODO Auto-generated method stub
		return BOUND;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!playerIn.isSneaking() && !worldIn.isRemote) {
			playerIn.openGui(Main.instance, Reference.GUI_MININGBLOCK, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}


	
	public static void setState(boolean active, World worldIn, BlockPos pos) 
	{
		IBlockState state = worldIn.getBlockState(pos);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		System.out.println("Set da tile entity1");
		
		if(tileentity != null) 
		{
			System.out.println("Set da tile entity");
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		// TODO Auto-generated method stub
		int power = 5 + (int)(Math.random() * ((100 - 5) + 1));
		System.out.println("Made a tile entity!");
		return new TileEntityMiner();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
