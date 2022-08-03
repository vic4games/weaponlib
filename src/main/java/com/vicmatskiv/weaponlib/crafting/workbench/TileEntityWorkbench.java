package com.vicmatskiv.weaponlib.crafting.workbench;

import java.time.Duration;
import java.time.Instant;

import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityWorkbench extends TileEntity {

	
	public Instant craftingStart;
	public Duration craftingLength;
	private ItemStackHandler mainInventory = new ItemStackHandler(27);
	
	public TileEntityWorkbench() {
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
	}
	
	
	
	public double getProgress() {
		
		if(craftingStart == null || craftingLength == null) return 0.0;

		return Duration.between(craftingStart, Instant.now()).toMillis()/((double) craftingLength.toMillis());
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		// TODO Auto-generated method stub
		return super.getUpdateTag();
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		// TODO Auto-generated method stub
		System.out.println("yo");
		return super.getUpdatePacket();
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		super.handleUpdateTag(tag);
	}
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("mainInventory", mainInventory.serializeNBT());
		if(craftingStart != null) compound.setString("craftingStart", craftingStart.toString());
		return super.writeToNBT(compound);
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.mainInventory.deserializeNBT((NBTTagCompound) compound.getTag("mainInventory"));
		if(compound.hasKey("craftingStart")) this.craftingStart = Instant.parse(compound.getString("craftingStart"));
		super.readFromNBT(compound);
	}

}
