package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class EconomySlot extends SlotItemHandler {
	
	public boolean enable = true;

	public EconomySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		// TODO Auto-generated constructor stub
	}

	
	public void setEnabled(boolean b) {
		this.enable = b;
	}
	
	@Override
	public void putStack(ItemStack stack) {
		super.putStack(stack);
	}
	
	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		
		return true;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if(stack.getItem() instanceof LedgerBase || stack.getItem() instanceof OpenDimeBase) {
			return true;
		}
		return false;
	}
	
	
	
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.enable;
	}
	

}
