package com.jimholden.conomy.containers;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityLootingBlock;
import com.jimholden.conomy.containers.slots.SlotLooting;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.looting.keycards.CLIDManager;

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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerLootingBlock extends Container {
	
	private final TileEntityLootingBlock tileentity;
	private int deviceBalance;
	
	public boolean isAdmin = false;
	//public IItemHandler handler;

	
	public ContainerLootingBlock(InventoryPlayer player, TileEntityLootingBlock tileentity, boolean admin) {
		this.tileentity = tileentity;
		//IItemHandler handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		//this.handler = handler;
		this.isAdmin = admin;
	
		
		
		
		ItemStackHandler handle = null;
		if(admin) {
			//handle = tileentity.lootboxInventory;
			
			if(tileentity.getWorld().isRemote) {
				handle = new ItemStackHandler(27);
			} else handle = CLIDManager.getHandlerFromCLID(tileentity.clid);
			
			
		} else {
			handle = tileentity.lootboxInventory;
		}
		
		int count = 0;
		
		int yCount = 2;
		int xCount = 7;
		
		if(admin) {
			yCount = 3;
			xCount = 9;
		}
		
		for(int y = 0; y < yCount; y++)
		{
			
			for(int x = 0; x < xCount; x++) {
				//System.out.println("new slot (" + y + "): " + count);
				if(admin) {
					this.addSlotToContainer(new SlotItemHandler(handle, count, 8 + x*18, 12 + y*18));
				} else {
					this.addSlotToContainer(new SlotLooting(handle, count, 22 + x*18, 21 + y*18));
				}
				
				count++;
				
			}
		}
		
		//this.addSlotToContainer(new SlotItemHandler(tileentity.handler, 0, 18, 34));
	
		
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				
				this.addSlotToContainer(new Slot(player, x + y*9 + 9, 8 + x * 18, 120+y*18));
				//this.addSlotToContainer(new Slot(player, x + y*9 + 9, 8 + x*18, 84 + y*18));
				
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			if(!admin) {
				this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 142));
			} else {
				this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 152));
			}
			
			
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
		
		if(slot != null && slot.getHasStack()) 
		{
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			

			
			if(index < 14) 
			{
				if(!this.mergeItemStack(stack1, 41, 49, true)) return ItemStack.EMPTY;
				slot.onSlotChange(stack1, stack);
			}
			else if(index > 14) 
			{		
				//Slot slot1 = (Slot)this.inventorySlots.get(index + 1);
				if(!this.mergeItemStack(stack1, 41, 49, true)) return ItemStack.EMPTY;
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
	public void putStackInSlot(int slotID, ItemStack stack) {
		super.putStackInSlot(slotID, stack);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		if(this.isAdmin) {
			CLIDManager.save();
		} else {
			this.tileentity.sendUpdates();
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return this.tileentity.isUsableByPlayer(playerIn);
	}



}
