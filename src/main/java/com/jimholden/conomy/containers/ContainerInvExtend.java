package com.jimholden.conomy.containers;

import javax.annotation.Nullable;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.containers.CustomInventory.IContainerInventoryCustom;
import com.jimholden.conomy.containers.slots.ActualBSlot;
import com.jimholden.conomy.containers.slots.ActualRSlot;
import com.jimholden.conomy.containers.slots.BackpackSlots;
import com.jimholden.conomy.containers.slots.CustomInvSlot;
import com.jimholden.conomy.containers.slots.RigSlots;
import com.jimholden.conomy.containers.slots.SpecialGearSlot;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.EnumGear;
import com.jimholden.conomy.items.GlassesItem;
import com.jimholden.conomy.items.RigItem;
import com.jimholden.conomy.util.packets.InventoryServerPacket;
import com.jimholden.conomy.util.packets.OpenInventoryServerPacket;
import com.jimholden.conomy.util.packets.medical.RecalculateWeight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerInvExtend extends Container implements IContainerInventoryCustom {
	
	public InventoryPlayer inventory;
	
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    /** The crafting matrix inventory. */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    public EntityPlayer player;
    public ItemStackHandler handlerBackpack;
    public ItemStackHandler rigHandler;
    
    public ItemStackHandler handlerEmpty;
    public ItemStackHandler rigEmpty;
    public boolean isBackpack = false;
    
    public boolean hasDisplay = false;
    
    public IInvCapa capa;
    
    public int adjustToScale(int num, float scale) {
    	return ((int) (num/scale));
    }
    
    
    public int visibilityIndex = 0;
    public int visibilityIndexRig = 0;
    
    
    public ItemStack fixVicItem(ItemStack stack) {
    	NBTTagCompound comp = stack.getTagCompound();
    	int size = comp.getInteger("size");
    	ItemStackHandler injector = new ItemStackHandler(size);
    	comp.setTag("inventory", injector.serializeNBT());
    	stack.setTagCompound(comp);
    	return stack;
    }
    
    
    public ContainerInvExtend(InventoryPlayer playerInv, boolean par2, EntityPlayer player)
    {
    	
    	
    	
    	//System.out.println("INITIAL: " + this.conguiLeft + " | " + this.guiTop);
    	this.player = player;
    	this.capa = player.getCapability(InvProvider.EXTRAINV, null);
        //this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 154, 28));

        //ItemStackHandler hndler = new ItemStackHandler(1);
        //hndler.setStackInSlot(0, new ItemStack(ModItems.OPENDIME));
        //this.addSlotToContainer(new SlotItemHandler(hndler, 0, 50, -50));
        
        IInvCapa capa = player.getCapability(InvProvider.EXTRAINV, null);
        float slotScale = 1.5F;
         
        
        int clusterX = 60;
        int clusterY = 3;
        
        
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 1, clusterX+38, clusterY+19, EnumGear.GLASSES));
        //this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 1, 96, 13));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 2, clusterX, clusterY+19, EnumGear.HEADSET));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 3, clusterX+19, clusterY, EnumGear.MASK));
        this.addSlotToContainer(new ActualBSlot(capa.getHandler(), 4, clusterX, clusterY+57, this));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 6, clusterX+38, clusterY+38, EnumGear.BODYARMOR));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 7, clusterX, clusterY+38, EnumGear.JACKET));
        this.addSlotToContainer(new ActualRSlot(capa.getHandler(), 5, clusterX+38, clusterY+57, this));
        
        
        /*
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 1, 96, 13, EnumGear.GLASSES));
        //this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 1, 96, 13));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 2, 58, 13));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 3, 77, 13));
        this.addSlotToContainer(new ActualBSlot(capa.getHandler(), 4, 58, 32, this));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 6, 77, 32));
        this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 7, 26, 117));
        this.addSlotToContainer(new ActualRSlot(capa.getHandler(), 5, 96, 32, this));
        */
        
        //this.addSlotToContainer(new CustomInvSlot(player, capa.getHandler(), 2, 58, 13, slotScale));
        //this.addSlotToContainer(new CustomInvSlot(player, capa.getHandler(), 3, 77, 13, slotScale));
        
        //this.addSlotToContainer(new CustomInvSlot(player, capa.getHandler(), 2, coordAdjustX(-200), coordAdjustY(-40), slotScale));
        //this.addSlotToContainer(new CustomInvSlot(player, capa.getHandler(), 3, adjustToScale(-60, slotScale), adjustToScale(15, slotScale), slotScale));
        
        
        //this.addSlotToContainer(new CustomInvSlot(player, capa.getHandler(), 4, 58, 32, slotScale));
        
        if(!capa.getStackInSlot(4).isEmpty()) {
        	ItemStack stack = capa.getStackInSlot(4);
        	if(stack.getItem() instanceof BackpackItem) {
        		this.handlerBackpack = ((BackpackItem) stack.getItem()).getInv(stack);
        		this.visibilityIndex = ((BackpackItem) stack.getItem()).getSize(stack);
                
        	}
        	
        }
        
        if(!capa.getStackInSlot(5).isEmpty()) {
        	ItemStack stack = capa.getStackInSlot(5);
        	if(stack.getItem() instanceof RigItem) {
        		this.rigHandler = ((RigItem) stack.getItem()).getInv(stack);
        		this.visibilityIndexRig = ((RigItem) stack.getItem()).getSize(stack);
                
        	}
        	
        }
        
        this.handlerEmpty = new ItemStackHandler(40);
        int count = 0;
		for (int i = 0; i < 5; ++i)
        {
            for (int j = 0; j < 8; ++j)
            {
            	
                this.addSlotToContainer(new BackpackSlots(this.handlerEmpty, count, 135 + j * 18, 110 + i * 18, this));
                count += 1;
            }
        }
        
        
        this.rigEmpty = new ItemStackHandler(40);
        int countRig = 0;
		for (int i = 0; i < 5; ++i)
        {
            for (int j = 0; j < 8; ++j)
            {
            	
                this.addSlotToContainer(new RigSlots(this.rigEmpty, countRig, 135 + j * 18, -22 + i * 18, this));
                countRig += 1;
            }
        }
		
		
		/*
		for(int x = 0; x < 4; ++x) {
			this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 10+x, 135 + x * 18, 106));
			
		}
        */
		/*
		for(int x = 0; x < 3; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 9 + x * 19, 136));
			
		}*/
		
		for(int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInv, x, 135 + x * 18, 80));
			
		}
        
        
        
        
        /*
        
        if(!capa.getStackInSlot(4).isEmpty()) {
        	ItemStack stack = capa.getStackInSlot(4);
        	if(stack.getItem() instanceof BackpackItem) {
        		this.handlerBackpack = ((BackpackItem) stack.getItem()).getInv(stack);
        		int count = 0;
        		for (int i = 0; i < 4; ++i)
                {
                    for (int j = 0; j < 5; ++j)
                    {
                    	
                        this.addSlotToContainer(new BackpackSlots(this.handlerEmpty, count, 98 + j * 18, 18 + i * 18, this));
                        count += 1;
                    }
                }
        		this.hasDisplay = true;
                
        	}
        	
        }
        */
        
        /*
        
		this.inventorySlots.get(0).isEnabled();
		
        if(!capa.getStackInSlot(4).isEmpty()) {
        	
        	ItemStack stack = capa.getStackInSlot(4);
        	if(stack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_LIST) != null) {
        		System.out.println("Detected VMW");
        		if(!player.world.isRemote) {
        			stack = fixVicItem(stack);
            		capa.setStackInSlot(4, stack);
        		}
        		
        		
        	}
        	if(stack.getTagCompound().getInteger("size") != 0 && stack.getTagCompound().getTag("inventory") != null) {
        		isBackpack = true;
        		int size = stack.getTagCompound().getInteger("size");
        		ItemStackHandler handler2 = new ItemStackHandler();
        		handler2.deserializeNBT((NBTTagCompound) stack.getTagCompound().getTag("inventory"));
        		this.handlerBackpack = handler2;
        		
        
        		
        		
        	}
        }
        */
        /*
        if(!capa.getStackInSlot(4).isEmpty()) {
        	ItemStack sk = capa.getStackInSlot(4);
            NBTTagCompound compound = sk.getTagCompound();
            if(compound.getTag("ItemInventory") != null) {
            	System.out.println("da inv: ");
            	
            	NBTBase tag = compound.getTag("ItemInventory");
            	ItemStackHandler hdnler = new ItemStackHandler();
            	hdnler.deserializeNBT((NBTTagCompound) tag);

            	for(int x = 0; x < hdnler.getSlots(); x++) {
            		System.out.println(hdnler.getStackInSlot(x));
            	}
            	//System.out.println(hdnler.toStrin);
            	
            }
        }
        */
        
		
        
        /*
        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }
        */

        for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlotToContainer(new Slot(playerInv, 36 + (3 - k), clusterX+19, clusterY+19+k * 19)
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

        /*
        
        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInv, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }
        */
		
        /*
        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInv, i1, 8 + i1 * 18, 142));
        }
	*/
        
    }

    public void saveRig() {
    	if(this.player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(5).getItem() instanceof RigItem) {
    		ItemStack stack = this.player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(5);
    		if(stack.getItem() instanceof RigItem) {
    			RigItem backpack = (RigItem) stack.getItem();
    			if(this.rigHandler != null) {
    				backpack.saveInv(stack, this.rigHandler);
    			}
    			
    		}
    	}
    }
    
    
    public void saveBackpack() {
    	if(this.player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(4).getItem() instanceof BackpackItem) {
    		ItemStack stack = this.player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(4);
    		if(stack.getItem() instanceof BackpackItem) {
    			BackpackItem backpack = (BackpackItem) stack.getItem();
    			if(this.handlerBackpack != null) {
    				backpack.saveInv(stack, this.handlerBackpack);
    			}
    			
    		}
    	}
    }
    
    
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
    	saveBackpack();
    	//System.out.println("fuc");
    	
    	if(player.world.isRemote) {
    		Main.NETWORK.sendToServer(new RecalculateWeight(player.getEntityId()));
    	}
    	
    	//System.out.println("Hi:!" + player.world.isRemote);
    	//
    	
    	//System.out.println("yo clcikers: " + clickTypeIn + " | " + dragType + " | " +;
    	
    	//boolean flag = false;
    	
    	try {
    		if(this.getSlot(slotId) instanceof SpecialGearSlot) {
    			if(this.getSlot(slotId).getStack().getItem() instanceof BackpackItem && clickTypeIn == ClickType.PICKUP) {
    				this.visibilityIndex = 0;
    			}
    			
    			
    		}
    		if(this.getSlot(slotId) instanceof SpecialGearSlot) {
    			if(this.getSlot(slotId).getStack().getItem() instanceof RigItem && clickTypeIn == ClickType.PICKUP) {
    				this.visibilityIndexRig = 0;
    			}
    			
    			
    		}
    		
    		
    	} catch (Exception e) {
    		
    	}
    	/*
    	try {
    		/*
    		if(this.getSlot(slotId) instanceof BackpackSlots) {
    			System.out.println("flag2!");
    			saveBackpack();
    			if(!hasDisplay) {
    				System.out.println("tsskk");
    				addTheSlots();
    			}
    			
    		}
    		
    		
    		if(this.getSlot(slotId) instanceof SlotItemHandler) {
        		SlotItemHandler slot = (SlotItemHandler) this.getSlot(slotId);
        		if(slot.getSlotIndex() == 4) {
        			if(slot.getStack().getItem() instanceof BackpackItem) {
        				this.visibilityIndex = ((BackpackItem) slot.getStack().getItem()).getSize(slot.getStack());
        			}
        		}
        		
        	}
    	} catch(Exception e){
    		
    	}
    	*/
    	
    	return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
    
    
    

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.slotChangedCraftingGrid(this.player.world, this.player, this.craftMatrix, this.craftResult);
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.craftResult.clear();

        if (!playerIn.world.isRemote)
        {
            this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
        }
    }
    
    
    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
    	super.putStackInSlot(slotID, stack);
    	
    	
    	
    	
    	
    	
    	/*
    	
    	try {
    		
    		System.out.println("putting!");
    		if(this.getSlot(slotID) instanceof SlotItemHandler) {
    			if(this.getSlot(slotID).getSlotIndex() == 4) {
    				System.out.println("flag2!");
        			saveBackpack();
        			if(!hasDisplay) {
        				System.out.println("tsskk");
        				addTheSlots();
        			}
    			}
    			
    			
    		}
    		
    		
    		if(this.getSlot(slotID) instanceof SlotItemHandler) {
    			//Main.NETWORK.sendToServer(new OpenInventoryServerPacket());
        		SlotItemHandler slot = (SlotItemHandler) this.getSlot(slotID);
        		if(slot.getSlotIndex() == 4) {
        			if(slot.getStack().getItem() instanceof BackpackItem) {
        				this.visibilityIndex = ((BackpackItem) slot.getStack().getItem()).getSize(slot.getStack());
        			}
        		}
        		
        	}
    		super.putStackInSlot(slotID, stack);
    	} catch(Exception e) {
    		
    	}
    	
    	*/
    	
    	
    	
    	/*
    	try {
    		if(this.getSlot(slotID) instanceof SlotItemHandler) {
    			if(this.getSlot(slotID).getSlotIndex() == 4) {
    				ItemStack stackCam = this.getSlot(slotID).getStack();
    				if(stackCam.getItem() instanceof BackpackItem) {
    					this.visibilityIndex = ((BackpackItem) stackCam.getItem()).getSize(stackCam);
    				}
    				//ItemStack stackerOO = this.getSlot(slotID).getStack();
    				//if()
    			}
    		}
    	} catch (Exception e) {
    		
    	}
    	*/
    	
    	
    }
    
    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    
    @Override
    public void addListener(IContainerListener listener) {
   
    	super.addListener(listener);
    }
    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 1 && index < 5)
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 5 && index < 9)
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack())
            {
                int i = 8 - entityequipmentslot.getIndex();

                if (!this.mergeItemStack(itemstack1, i, i + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !((Slot)this.inventorySlots.get(45)).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 45, 46, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 9 && index < 36)
            {
                if (!this.mergeItemStack(itemstack1, 36, 45, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 36 && index < 45)
            {
                if (!this.mergeItemStack(itemstack1, 9, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 9, 45, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }
    
    
    

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
    	
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

}
