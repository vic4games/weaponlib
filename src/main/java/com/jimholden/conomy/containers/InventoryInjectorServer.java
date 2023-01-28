package com.jimholden.conomy.containers;

import javax.annotation.Nullable;

import com.jimholden.conomy.containers.slots.ISaveableSlot;
import com.jimholden.conomy.containers.slots.SpecialGearSlot;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.EnumGear;
import com.jimholden.conomy.items.ISaveableItem;
import com.jimholden.conomy.items.RigItem;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class InventoryInjectorServer {
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	  
	
	private void addSlotToContainer(Container cont, Slot slotIn)
    {
        slotIn.slotNumber = cont.inventorySlots.size();
        cont.inventorySlots.add(slotIn);
        cont.inventoryItemStacks.add(ItemStack.EMPTY);
        //return slotIn;
    }
	
	
	
	public void injectSlots(PlayerContainerEvent event) {
		if(!(event.getContainer() instanceof ContainerTrader)) {
			inventorySlots(event);
			
			addArmorSlots(event);
		}
		
		
		rigBackpackSlots(event);
	}
	
	public void inventorySlots(PlayerContainerEvent event) {
		IInvCapa capa = event.getEntityPlayer().getCapability(InvProvider.EXTRAINV, null);
		Container c = event.getContainer();
		System.out.println(capa.getHandler().serializeNBT());
		addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 1, -4, 13, EnumGear.GLASSES));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 2, -42, 13, EnumGear.HEADSET));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 3, -23, 13, EnumGear.MASK));
       // addSlotToContainer(c, new ActualBSlot(capa.getHandler(), 4, 58, 32, this));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 6, 77, 32, EnumGear.BODYARMOR));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 7, 26, 117, EnumGear.JACKET));
      //  addSlotToContainer(c,new ActualRSlot(capa.getHandler(), 5, 96, 32, this));
	}
	
	public void addArmorSlots(PlayerContainerEvent event) {
		Container c = event.getContainer();
		EntityPlayer player = event.getEntityPlayer();
		InventoryPlayer playerInv = player.inventory;
		for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            addSlotToContainer(c, new Slot(playerInv, 36 + (3 - k), 9 + k * 18, 98)
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
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
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
	}
	
	public void rigBackpackSlots(PlayerContainerEvent event) {
		IInvCapa capa = event.getEntityPlayer().getCapability(InvProvider.EXTRAINV, null);
		if(!(event.getContainer() instanceof ContainerInvExtend)) {
			Container c = event.getContainer();
			if(!capa.getStackInSlot(4).isEmpty()) {
	        	ItemStack stack = capa.getStackInSlot(4);
	        	if(stack.getItem() instanceof BackpackItem) {
	        		ItemStackHandler handler = ((BackpackItem) stack.getItem()).getInv(stack);
	        		int count = 0;
	    			for (int i = 0; i < 5; ++i)
	    	        {
	    	            for (int j = 0; j < 8; ++j)
	    	            {
	    	            	addSlotToContainer(c, new ISaveableSlot(handler, count, 150 + j * 18, 22 + i * 18, (ISaveableItem) capa.getStackInSlot(4).getItem(), capa.getStackInSlot(4)));
		    	             
	    	               // c.inventorySlots.add(new ISaveableSlot(handler, count, j * 18, 150 + i * 18, (ISaveableItem) capa.getStackInSlot(4).getItem(), capa.getStackInSlot(4)));
	    	                count += 1;
	    	                if(count > ((BackpackItem) stack.getItem()).getSize(stack)-1) {
	    	                	break;
	    	                }
	    	            }
	    	            if(count > ((BackpackItem) stack.getItem()).getSize(stack)-1) {
    	                	break;
    	                }
	    	        }
	        		//this.visibilityIndex = ((BackpackItem) stack.getItem()).getSize(stack);
	                
	        	}
	        	
	        }
			

	        
	        if(!capa.getStackInSlot(5).isEmpty()) {
	        	ItemStack stack = capa.getStackInSlot(5);
	        	if(stack.getItem() instanceof RigItem) {
	        		ItemStackHandler handler = ((RigItem) stack.getItem()).getInv(stack);
	        		//this.visibilityIndexRig = ((RigItem) stack.getItem()).getSize(stack);
	        		int countRig = 0;
	    			for (int i = 0; i < 5; ++i)
	    	        {
	    	            for (int j = 0; j < 8; ++j)
	    	            {
	    	            	addSlotToContainer(c, new ISaveableSlot(handler, countRig, 150 + j * 18, 22 + i * 18, (ISaveableItem) capa.getStackInSlot(5).getItem(), capa.getStackInSlot(5)));
	    	                //c.inventorySlots.add(new ISaveableSlot(handler, countRig, 135 + j * 18, 22 + i * 18, (ISaveableItem) capa.getStackInSlot(5).getItem(), capa.getStackInSlot(5)));
	    	                countRig += 1;
	    	                if(countRig > ((RigItem) stack.getItem()).getSize(stack)-1) {
	    	                	break;
	    	                }
	    	            }
	    	            if(countRig > ((RigItem) stack.getItem()).getSize(stack)-1) {
    	                	break;
    	                }
	    	        }
	    			
	                
	        	}
	        	
	        }
	        
	        

	        
	        
		}
	}

}
