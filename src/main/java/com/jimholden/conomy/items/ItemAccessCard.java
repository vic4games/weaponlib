package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.blocks.tileentity.TileEntityKeyDoor;
import com.jimholden.conomy.looting.keycards.IAccessCard;
import com.jimholden.conomy.util.logging.CLInit;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAccessCard extends ItemBase {
	
	

	public ItemAccessCard(String name) {
		super(name);
	}
	
	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setBoolean("hasAP", false);
			compound.setLong("ap", new BlockPos(0, 0, 0).toLong());
			stack.setTagCompound(compound);
		}
	}
	
	public boolean hasAccessPoint(ItemStack stack) {
		ensureNBTUpdated(stack);
		return stack.getTagCompound().getBoolean("hasAP");
	}
	
	public BlockPos getAccessPoint(ItemStack stack) {
		ensureNBTUpdated(stack);
		return BlockPos.fromLong(stack.getTagCompound().getLong("ap"));
	}
	
	public void setAccessPoint(ItemStack stack, BlockPos ap) {
		ensureNBTUpdated(stack);
		stack.getTagCompound().setLong("ap", ap.toLong());
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		ItemStack stack = player.getHeldItem(hand);
		
		System.out.println("fuck!");
		
		Block block = worldIn.getBlockState(pos).getBlock();
		if(block instanceof IAccessCard) {
			if(player.isCreative() && player.isSneaking()) {
				if(!worldIn.isRemote) {
					setAccessPoint(stack, pos);
					player.sendMessage(CLInit.SB_NORMAL.newMessage("Access Card", "You have set this access card operable block to open with this keycard."));
				}
			}
		}
		
		
		
		
		
		
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("A keycard.");
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

}
