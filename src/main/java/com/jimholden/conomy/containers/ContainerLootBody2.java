package com.jimholden.conomy.containers;

import javax.annotation.Nullable;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.containers.slots.ActualBSlot;
import com.jimholden.conomy.containers.slots.ActualRSlot;
import com.jimholden.conomy.containers.slots.BackpackSlots;
import com.jimholden.conomy.containers.slots.RigSlots;
import com.jimholden.conomy.containers.slots.SlotDisabled;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.items.RigItem;
import com.jimholden.conomy.util.packets.InventoryServerPacket;
import com.jimholden.conomy.vmwcompat.VMWSlotAccess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerLootBody2 extends Container {
	
	public EntityPlayer target;
	public ItemStackHandler handler;
	//public IInvCapa capa;
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	   
	
	
	public ContainerLootBody2(InventoryPlayer player, EntityPlayer target) {
		InventoryPlayer targetInventory = target.inventory;
		this.target = target;
		System.out.println("isdebug");
		/*
		if(VMWSlotAccess.getVMWSlots(target) != null) {
			ItemStackHandler inv = VMWSlotAccess.getVMWSlots(target);
			this.addSlotToContainer(new SlotItemHandler(inv, 0, 184, -16));
			this.addSlotToContainer(new SlotItemHandler(inv, 1, 184, 2));
		}*/
		
		IInvCapa capa = target.getCapability(InvProvider.EXTRAINV, null);
		
		this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 1, 96, 13));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 2, 58, 13));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 3, 77, 13));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 4, 58, 32));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 6, 77, 32));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 7, 26, 117));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 5, 96, 32));
		
		/*
		IInvCapa capa = target.getCapability(InvProvider.EXTRAINV, null);
		ItemStackHandler handle = new ItemStackHandler(capa.getHandler().getSlots());
		for(int x = 0; x < capa.getHandler().getSlots(); ++x) {
			handle.setStackInSlot(x, capa.getStackInSlot(x).copy());
		}
		
		
		this.addSlotToContainer(new SlotItemHandler(handle, 1, 96, 13));
        this.addSlotToContainer(new SlotItemHandler(handle, 2, 58, 13));
        this.addSlotToContainer(new SlotItemHandler(handle, 3, 77, 13));
        this.addSlotToContainer(new SlotItemHandler(handle, 4, 58, 32));
        this.addSlotToContainer(new SlotItemHandler(handle, 6, 77, 32));
        this.addSlotToContainer(new SlotItemHandler(handle, 7, 26, 117));
        this.addSlotToContainer(new SlotItemHandler(handle, 5, 96, 32));
        */
		
		//this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 1, 96, 13));

        
        /*
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				Slot s = new Slot(targetInventory, x + y*9 + 9, 8 + x*18, -16 + y*18);
				this.addSlotToContainer(new SlotDisabled(s));
				
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			Slot s = new Slot(targetInventory, x, 8, 41 + x * 18);
			this.addSlotToContainer(new SlotDisabled(s));
			
		}
		*/

		for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlotToContainer(new Slot(target.inventory, 36 + (3 - k), 9 + k * 18, 98)
            {
            	
            
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
                 * fuel.
                 */
                public boolean isItemValid(ItemStack stack)
                {
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, target);
                }
                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    ItemStack itemstack = this.getStack();
                    return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
                }
                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }
		
		
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(player, x + y*9 + 9, 8 + x*18, 74 + y*18));
				
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			this.addSlotToContainer(new Slot(player, x, -320 +  x * 18, 161 ));
			
		}
		
		/*
		for(int x = 0; x < 3; x++)
		{
			this.addSlotToContainer(new Slot(player, x, -311 +  x * 19, 136 ));
			
		} */
		
		for(int x = 0; x < 9; x++)
		{
			this.addSlotToContainer(new Slot(targetInventory, x, -47 +  x * 18, 161 ));
			
		}
		
		/*
		for(int x = 0; x < 3; x++)
		{
			this.addSlotToContainer(new Slot(targetInventory, x, 9 +  x * 19, 136 ));
			
		} */
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
		
		/*
		for(int n = 0; n < this.handler.getSlots(); ++n) {
			Main.NETWORK.sendToServer(new InventoryServerPacket(n, this.handler.getStackInSlot(n), this.target.getEntityId()));
		} */
		
		return super.slotClick(slotId, dragType, clickTypeIn, player);
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
	
	


}
