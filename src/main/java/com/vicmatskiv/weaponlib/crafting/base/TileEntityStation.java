package com.vicmatskiv.weaponlib.crafting.base;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTileEntity;

import net.minecraft.tileentity.TileEntity;

public class TileEntityStation extends TileEntity {
	
	private int side;
	
	public TileEntityStation() {
		
	}
	
	public void setSide(int side) {
		this.side = side;
	}
	
	public int getSide() {
		return side;
	}
	
	
	

}
