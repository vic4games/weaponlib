package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKeyDoor extends ItemDoor implements IHasModel {

	public Block block;
	
	public ItemKeyDoor(Block block) {
		super(block);
		this.block = block;
	}
	
	
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		if (facing != EnumFacing.UP) return EnumActionResult.FAIL;

		ItemStack itemStack = player.getHeldItem(hand);
		IBlockState state = worldIn.getBlockState(pos);
		if (!state.getBlock().isReplaceable(worldIn, pos)) pos = pos.up();


		if (!player.canPlayerEdit(pos, facing, itemStack) || !player.canPlayerEdit(pos.up(), facing, itemStack))
			return EnumActionResult.FAIL;

		if (!block.canPlaceBlockAt(worldIn, pos))
			return EnumActionResult.FAIL;

		placeDoor(worldIn, pos, EnumFacing.fromAngle(player.rotationYaw), block, false);
		itemStack.shrink(1);
		block.onBlockPlacedBy(worldIn, pos, worldIn.getBlockState(pos), player, itemStack);
		return EnumActionResult.SUCCESS;
	}



	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
