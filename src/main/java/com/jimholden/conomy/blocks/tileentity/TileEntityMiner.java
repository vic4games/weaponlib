package com.jimholden.conomy.blocks.tileentity;

import com.jimholden.conomy.blocks.MinerBlock;
import com.jimholden.conomy.items.SoftwareFlashBase;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityMiner extends TileEntity implements ITickable{
	public int basePower;
	public int compatType;
	private String customName;
	public ItemStackHandler handler = new ItemStackHandler(1);

	public TileEntityMiner() {
	}
	
	@Override
	public void onLoad() {
		this.basePower = ((MinerBlock) this.getBlockType()).basePower;
		this.compatType = ((MinerBlock) this.getBlockType()).compatType;
		super.onLoad();
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return this.hasCustomName() ? this.customName : "container.atm";
	}

	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return this.customName != null && !this.customName.isEmpty();
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
		
	}
	
	public boolean hasFlash() {
		return !this.handler.getStackInSlot(0).isEmpty();
	}
	
	
	public int getSoftwareCompat() {
		return ((SoftwareFlashBase) this.handler.getStackInSlot(0).getItem()).getCompatIndex(this.handler.getStackInSlot(0));
		
	}
	
	public int getSoftwarePower() {
		return ((SoftwareFlashBase) this.handler.getStackInSlot(0).getItem()).getPower(this.handler.getStackInSlot(0));
		
	}
	
	
	
	public int getPower() {
		if(hasFlash()) {
			int minerCompat = this.compatType;
			int softCompat = getSoftwareCompat();
			return (basePower) + getSoftwarePower();
		} else {
			return basePower;
		}
		
	}
	
	@Override
	public void update() {
		
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
		if(compound.hasKey("CustomName", 8)) this.setCustomName(compound.getString("CustomName"));
	}
	
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Inventory", this.handler.serializeNBT());
		if(this.hasCustomName()) compound.setString("CustomName", this.customName);
		return compound;
	}
	

}
