package com.jimholden.conomy.blocks.tileentity;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ModdedTileEntityBase extends TileEntity implements ITickable {
	
	private String customName = "";
	
	public ModdedTileEntityBase() {
		
	}
	
	
	
	public ModdedTileEntityBase(String name) {
		this.customName = customName;
	}
	
	public String getCustomName() {
		return customName;
	}
	
	public void setCustomName(String string) {
		this.customName = customName;
	}
	
	public boolean hasCustomName() {
		return !getCustomName().equals("");
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(customName);
	}



	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	

}
