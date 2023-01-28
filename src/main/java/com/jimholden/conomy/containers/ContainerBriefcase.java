package com.jimholden.conomy.containers;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.containers.slots.BriefcaseSlot;
import com.jimholden.conomy.containers.slots.SlotLooting;
import com.jimholden.conomy.items.ItemChemicalBriefcase;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.vmwcompat.VMWSlotAccess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBriefcase extends Container {
	
	private ItemStack briefcaseItem;
	private ItemStackHandler briefcase;
	
	
	
	public ContainerBriefcase(EntityPlayer player, int hand) {
		
		ItemStack stack = null;
		if(hand == 0) {
			stack = player.getHeldItemMainhand();
		} else {
			stack = player.getHeldItemOffhand();
		}
		
		this.briefcase = ((ItemChemicalBriefcase) stack.getItem()).getInv(stack);
		this.briefcaseItem = stack;
		InventoryPlayer playerInv = player.inventory;
		
		
		int count = 0;
		for(int y = 0; y < 4; y++)
		{
			for(int x = 0; x < 9; x++) {
				this.addSlotToContainer(new BriefcaseSlot(briefcase, count, 8 + x*18, -5 + y*18));
				count++;
				
			}
		}
		
		/*
		
		for(int y = 0; y < 4; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new SlotItemHandler(briefcase, x + y*9 + 9, 8 + x*18, -16 + y*18));
				
			}
		}

		*/
		
		
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(playerInv, x + y*9 + 9, 7 + x*18, 74 + y*18));
				
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 131));
			
		}
		
	}
	
	@Override
	public NonNullList<ItemStack> getInventory() {
		// TODO Auto-generated method stub
		return super.getInventory();
	}
	
	@Override
	public void addListener(IContainerListener listener) {
		// TODO Auto-generated method stub
		super.addListener(listener);
		//listener.sendAllWindowProperties(this, this.tileentity);
	}
	
	
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		saveBriefcase();
		if(this.getSlot(slotId).getStack() == this.briefcaseItem) {
			System.out.println("fuck fuck fuck fuck");
			
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
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
			
			if(index == 3) 
			{
				if(!this.mergeItemStack(stack1, 4, 40, true)) return ItemStack.EMPTY;
				slot.onSlotChange(stack1, stack);
			}
			else if(index != 2 && index != 1 && index != 0) 
			{		
				Slot slot1 = (Slot)this.inventorySlots.get(index + 1);
			} 
			else if(!this.mergeItemStack(stack1, 4, 40, false)) 
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
		return true;
	}
	
	public void saveBriefcase() {
		((ItemChemicalBriefcase) this.briefcaseItem.getItem()).saveInv(this.briefcaseItem, this.briefcase);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		saveBriefcase();
		super.onContainerClosed(playerIn);
	}
	
	


}
