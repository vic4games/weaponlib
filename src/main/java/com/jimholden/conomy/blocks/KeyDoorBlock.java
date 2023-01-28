package com.jimholden.conomy.blocks;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityKeyDoor;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemAccessCard;
import com.jimholden.conomy.items.ItemKeyDoor;
import com.jimholden.conomy.looting.keycards.IAccessCard;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.VectorUtil;
import com.jimholden.conomy.util.logging.CLInit;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeyDoorBlock extends BlockDoor implements IAccessCard {

	public ItemStack keyStack;

	public KeyDoorBlock(String name, Material materialIn) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemKeyDoor(this).setRegistryName(this.getRegistryName()));
	}
	
	
	
	@Override
	public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
		super.toggleDoor(worldIn, pos, open);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		
		
		ItemStack stack = playerIn.getHeldItem(hand);
		
		
		
		// check if the player is even using an access card
		if(!(stack.getItem() instanceof ItemAccessCard)) return false;
		
		
		
		
		
		
		ItemAccessCard card = (ItemAccessCard) stack.getItem();
		
		BlockPos cAP = card.getAccessPoint(stack);
		
		BlockPos adjPos = pos;
		if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
			adjPos = adjPos.down();
			
		}
		
		
		
		if(VectorUtil.areBlockPosEqual(card.getAccessPoint(stack), adjPos)) {
			boolean bing = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
			return bing;
			} else return false;
		
		
		//return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	

	

	
	
	
	
	

}
