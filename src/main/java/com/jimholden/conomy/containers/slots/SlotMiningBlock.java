package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.items.SoftwareFlashBase;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotMiningBlock extends SlotItemHandler {

	public SlotMiningBlock(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return (stack.getItem() instanceof SoftwareFlashBase);
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return super.getItemStackLimit(stack);
	}
	
	

}
