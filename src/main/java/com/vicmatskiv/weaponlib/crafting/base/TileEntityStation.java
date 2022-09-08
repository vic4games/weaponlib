package com.vicmatskiv.weaponlib.crafting.base;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityStation extends TileEntity implements ITickable {
	
	private int side;
	
	public TileEntityStation() {
		
	}
	
	public void setSide(int side) {
		this.side = side;
	}
	
	public int getSide() {
		return side;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
