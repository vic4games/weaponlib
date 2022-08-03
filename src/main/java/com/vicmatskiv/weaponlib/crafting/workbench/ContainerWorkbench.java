package com.vicmatskiv.weaponlib.crafting.workbench;

import java.util.ArrayList;
import java.util.List;

import com.vicmatskiv.weaponlib.compatibility.CompatibleContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerWorkbench extends CompatibleContainer {

	private EntityPlayer player;
	private InventoryPlayer inventory;
	private TileEntityWorkbench tileEntityWorkbench;

	public ContainerWorkbench(EntityPlayer player, InventoryPlayer inventory, TileEntityWorkbench workbenchTileEntity) {
		this.player = player;
		this.inventory = inventory;
		this.tileEntityWorkbench = workbenchTileEntity;

		// Output slots
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inventory, i, 40 + i * 22, 219));
		}
		
		
		
	
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return true;
	}

}
