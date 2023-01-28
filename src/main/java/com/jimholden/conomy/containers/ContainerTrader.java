package com.jimholden.conomy.containers;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityBrickPacker;
import com.jimholden.conomy.blocks.tileentity.TileEntityChemExtractor;
import com.jimholden.conomy.blocks.tileentity.TileEntityCompoundMixer;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.blocks.tileentity.TileEntityPillPress;
import com.jimholden.conomy.client.gui.player.GuiTrader;
import com.jimholden.conomy.containers.slots.ChemMixingInputSlot;
import com.jimholden.conomy.containers.slots.ChemOutputSlot;
import com.jimholden.conomy.containers.slots.CompInputSlot;
import com.jimholden.conomy.containers.slots.CompoundMixingSlot;
import com.jimholden.conomy.containers.slots.CuttingAgentSlot;
import com.jimholden.conomy.containers.slots.EconomySlot;
import com.jimholden.conomy.containers.slots.PackingMatInputSlot;
import com.jimholden.conomy.entity.EntityTrader;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.InventoryUtility;

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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerTrader extends Container {
	
	
	public EntityTrader trader;
	
	public EconomySlot econSlot;
	
	public ItemStackHandler stacker = new ItemStackHandler(1);
		
	public ContainerTrader(InventoryPlayer player, EntityTrader tileEntity) {
			this.trader = tileEntity;
			
			this.econSlot = new EconomySlot(new ItemStackHandler(1), 0, 42, 122);
			addSlotToContainer(this.econSlot);
			
			initializeHotbar(player);
		
		
		
	
		
		
		
	}
	
	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		System.out.println("merge");
		return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
	}
	

	
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		System.out.println("merge");
		return super.canMergeSlot(stack, slotIn);
	}
	
	public void initializeHotbar(InventoryPlayer player) {
		for(int x = 0; x < 9; x++)
		{
			this.addSlotToContainer(new Slot(player, x, 3 + x * 18, 157));
			
		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		if(!this.stacker.getStackInSlot(0).isEmpty()) {
			InventoryUtility.putItemInPlayerInventory(playerIn, this.stacker.getStackInSlot(0));
			
		}
		if(playerIn.world.isRemote) {
			if(Minecraft.getMinecraft().currentScreen instanceof GuiTrader) {
				GuiTrader traderGUI = (GuiTrader) Minecraft.getMinecraft().currentScreen;
				System.out.println(traderGUI.stackHandler.getStackInSlot(0));
			}
		}
		super.onContainerClosed(playerIn);
	}
	
	@Override
	public NonNullList<ItemStack> getInventory() {
		// TODO Auto-generated method stub
		return super.getInventory();
	}
	

	
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
	
		if(slotId == -1) {
			if(stacker.getStackInSlot(0).isEmpty()) {
				this.stacker.setStackInSlot(0, player.inventory.getItemStack());
			//	toSellCount += this.player.inventory.getItemStack().getCount();
				player.inventory.setItemStack(ItemStack.EMPTY);
			} else {
				if(ItemStack.areItemsEqual(stacker.getStackInSlot(0), player.inventory.getItemStack())) {
					stacker.getStackInSlot(0).grow(player.inventory.getItemStack().getCount());
					player.inventory.setItemStack(ItemStack.EMPTY);
				}
			}
		}
		if(getSlot(slotId) != null && getSlot(slotId) instanceof EconomySlot) {
			EconomySlot es = (EconomySlot) getSlot(slotId);
			ItemStack playerStack = player.inventory.getItemStack();
			if(es.getItemHandler().getStackInSlot(0).isEmpty()) {
				if(playerStack.isEmpty()) {
					es.getItemHandler().insertItem(0, ItemStack.EMPTY, false);
				} else {
					if(es.isItemValid(playerStack)) {
						es.putStack(playerStack.copy());
					}
				}
				
				
			} else {
				if(playerStack.isEmpty()) {
					es.getItemHandler().insertItem(0, ItemStack.EMPTY, false);
				}
				
				
			}
			return ItemStack.EMPTY;
		} else {
			return super.slotClick(slotId, dragType, clickTypeIn, player);
		}
		//return super.slotClick(slotId, dragType, clickTypeIn, player);
		
	}
	
	@Override
	public void putStackInSlot(int slotID, ItemStack stack) {
		System.out.println("yos");
		super.putStackInSlot(slotID, stack);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slotIn) {
		return false;
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
		if(slotId == -999 || slotId == -1) return null;
		return super.getSlot(slotId);
	}
	
	
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) 
	{
		if(1+1==2) return ItemStack.EMPTY;
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(index);
		
		if(slot != null && slot.getHasStack()) 
		{
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			

			
			if(index == 1 || index == 2 || index == 0) 
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
		return true;
	}


}
