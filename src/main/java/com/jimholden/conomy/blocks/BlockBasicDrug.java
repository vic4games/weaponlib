package com.jimholden.conomy.blocks;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.Placer;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class BlockBasicDrug extends BlockCrops implements IHasModel {
	private static final AxisAlignedBB[] CARROT_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D)};

	private ItemBlock item;
	private Item itemCrop;
	
	
	public BlockBasicDrug(String name, Material material, Item itemCrop)
	{
		super();
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		this.itemCrop = itemCrop;
		
		ModBlocks.BLOCKS.add(this);
		this.item = (ItemBlock) new ItemBlock(this).setRegistryName(this.getRegistryName());
		ModItems.ITEMS.add(item);
	}
	
	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == Blocks.FARMLAND;
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		if(worldIn.getBlockState(pos.down()).getBlock() == Blocks.FARMLAND) {
			return true;
		} else {
			return false;
		}
	}

	
	@Override
	protected Item getCrop() {
		// TODO Auto-generated method stub
		return this.itemCrop;
	}
	
	@Override
	protected Item getSeed() {
		return this.item;
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CARROT_AABB[((Integer)state.getValue(this.getAgeProperty())).intValue()];
    }
	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
		
	}


}
