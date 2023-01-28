package com.jimholden.conomy.blocks.tileentity;

import com.jimholden.conomy.looting.keycards.IAccessCard;
import com.jimholden.conomy.render.tesr.SlidingDoorTESR;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityKeyDoor extends TileEntity implements IAccessCard {
	
	public long time = 0;
	public int maxTime = 75;
	
	public TileEntityKeyDoor() {
		
	}
	
	
	public EnumFacing getDoorFacing() {
		IBlockState state = this.world.getBlockState(getPos()).getActualState(this.world, getPos());
		return (EnumFacing)state.getValue(BlockDoor.FACING);
	}
	
	public EnumDoorHalf getDoorHalf() {
		IBlockState state = this.world.getBlockState(getPos()).getActualState(this.world, getPos());
		return (EnumDoorHalf)state.getValue(BlockDoor.HALF);
	}
	
	public boolean getDoorState() {
		IBlockState state = this.world.getBlockState(getPos());
		return ((Boolean)state.getValue(BlockDoor.OPEN)).booleanValue();
	}
	
	public void setOpenTimer() {
		time = System.currentTimeMillis();
	}
	
	public long getTime() {
		return System.currentTimeMillis()-time;
	}
	
	
	




}
