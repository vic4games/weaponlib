package com.jimholden.conomy.containers;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityCompoundMixer;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.containers.slots.CompoundMixingSlot;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCompoundMixer extends Container {
	
	private final TileEntityCompoundMixer tileentity;
	private int deviceBalance;
	//public IItemHandler handler;

		
	public ContainerCompoundMixer(InventoryPlayer player, TileEntityCompoundMixer tileentity) {
		this.tileentity = tileentity;
		
		this.addSlotToContainer(new CompoundMixingSlot(tileentity.handler, 0, 26, 54));
		this.addSlotToContainer(new CompoundMixingSlot(tileentity.handler, 1, 58, 54));
		this.addSlotToContainer(new CompoundMixingSlot(tileentity.handler, 2, 90, 54));
		this.addSlotToContainer(new CompoundMixingSlot(tileentity.handler, 3, 58, 11));
		
		
		
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(player, x + y*9 + 9, 8 + x*18, 84 + y*18));
				
			}
		}
		
		for(int x = 0; x < 8; x++)
		{
			this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 142));
			
		}
		
	}
	
	@Override
	public NonNullList<ItemStack> getInventory() {
		// TODO Auto-generated method stub
		return super.getInventory();
	}
	
	
	
	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
	}
	
	
	
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}
	
	
	@Override
	public Slot getSlot(int slotId) {
		// TODO Auto-generated method stub
		return super.getSlot(slotId);
	}
	
	
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) 
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(index);
		
		if(slot != null && slot.getHasStack()) 
		{
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			

			
			if(index == 3 || index == 1 || index == 2 || index == 0) 
			{
				if(!this.mergeItemStack(stack1, 0, 39, true)) return ItemStack.EMPTY;
				slot.onSlotChange(stack1, stack);
			}
			else if(index != 3 && index != 1 && index != 0 && index != 2) 
			{		
				//Slot slot1 = (Slot)this.inventorySlots.get(index + 1);
				if(!this.mergeItemStack(stack1, 0, 3, true)) return ItemStack.EMPTY;
				slot.onSlotChange(stack1, stack);
			} 
			else if(!this.mergeItemStack(stack1, 0, 39, false)) 
			{
				return ItemStack.EMPTY;
			}
			if(stack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();

			}
			
			if(stack1.getCount() == stack.getCount()) return ItemStack.EMPTY;
			slot.onTake(playerIn, stack1);
		}
		return stack;
	}
	
	

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return this.tileentity.isUsableByPlayer(playerIn);
	}


}
