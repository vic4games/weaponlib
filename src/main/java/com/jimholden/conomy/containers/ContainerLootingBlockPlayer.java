package com.jimholden.conomy.containers;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityLootingBlock;
import com.jimholden.conomy.containers.slots.SlotLooting;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
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

public class ContainerLootingBlockPlayer extends Container {
	
	private final TileEntityLootingBlock tileentity;
	private int deviceBalance;
	//public IItemHandler handler;
	
	
	public ContainerLootingBlockPlayer(InventoryPlayer player, TileEntityLootingBlock tileentity) {
		this.tileentity = tileentity;
		//IItemHandler handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		//this.handler = handler;
		System.out.println("hi");
		
		int count = 0;
		for(int y = 0; y < 2; y++)
		{
			for(int x = 0; x < 7; x++) {
				System.out.println("new slot (" + y + "): " + count);
				//this.addSlotToContainer(new SlotLooting(tileentity, count, 4 + x*18, 21 + y*18));
				count++;
				
			}
		}
		
		//this.addSlotToContainer(new SlotItemHandler(tileentity.handler, 0, 18, 34));
	
		
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(player, x + y*9 + 9, 8 + x*18, 84 + y*18));
				
			}
		}
		
		for(int x = 0; x < 9; x++)
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
		// TODO Auto-generated method stub
		super.addListener(listener);
		//listener.sendAllWindowProperties(this, this.tileentity);
	}
	
	
	
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}
	
	
	@Override
	public Slot getSlot(int slotId) {
		return super.getSlot(slotId);
	}
	
	
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) 
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(index);
		System.out.println("index! " + index);
		if(slot != null && slot.getHasStack()) 
		{
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			
			
			
			if(index < 14) 
			{
				System.out.println("hi!");
				if(!this.mergeItemStack(stack1, 0, 9, true)) return ItemStack.EMPTY;
				slot.onSlotChange(stack1, stack);
			}
			else if(index != 3 && index != 1 && index != 0 && index != 2) 
			{		
				//Slot slot1 = (Slot)this.inventorySlots.get(index + 1);
				if(!this.mergeItemStack(stack1, 0, 3, true)) return ItemStack.EMPTY;
				slot.onSlotChange(stack1, stack);
			} 
			else if(!this.mergeItemStack(stack1, 0, 9, false)) 
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
