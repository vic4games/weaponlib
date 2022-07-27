package com.vicmatskiv.weaponlib.crafting.workbench;

import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityWorkbench extends TileEntity {
	
	private int experience, level;
	
	private ItemStackHandler mainInventory = new ItemStackHandler(27);
	
	public TileEntityWorkbench() {
		System.out.println("A new tile entity has been created!");
	}
	
	@Override
	public void onLoad() {
		System.out.println("sup bitch");
		// TODO Auto-generated method stub
		super.onLoad();
	}
	
	
	
	
	public int getExperience() {
		return this.experience;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("mainInventory", mainInventory.serializeNBT());
		return super.writeToNBT(compound);
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.mainInventory.deserializeNBT((NBTTagCompound) compound.getTag("mainInventory"));
		super.readFromNBT(compound);
	}

}
