package com.jimholden.conomy.containers;

import javax.annotation.Nullable;

import com.jimholden.conomy.client.gui.NewInventory;
import com.jimholden.conomy.client.gui.player.GuiLootBody;
import com.jimholden.conomy.client.gui.player.GuiTrader;
import com.jimholden.conomy.containers.CustomInventory.IContainerInventoryCustom;
import com.jimholden.conomy.containers.slots.ActualBSlot;
import com.jimholden.conomy.containers.slots.ActualRSlot;
import com.jimholden.conomy.containers.slots.ISaveableSlot;
import com.jimholden.conomy.containers.slots.SpecialGearSlot;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.EnumGear;
import com.jimholden.conomy.items.ISaveableItem;
import com.jimholden.conomy.items.RigItem;
import com.jimholden.conomy.render.RenderTool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class InventoryInjectorClient {
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final ScaledResolution scaledresolution = new ScaledResolution(mc);
	private static FontRenderer fontRenderer = null;
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	private int nVal;
	private int zLevel;
	
	public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color);
    }
	
	private void addSlotToContainer(Container cont, Slot slotIn)
    {
        slotIn.slotNumber = cont.inventorySlots.size();
        cont.inventorySlots.add(slotIn);
        cont.inventoryItemStacks.add(ItemStack.EMPTY);
        //return slotIn;
    }
	private void addInventorySlot(GuiContainer gc, Container cont, Slot slotOld)
    {
		Slot slotIn = new Slot(slotOld.inventory, slotOld.getSlotIndex(), slotOld.xPos, slotOld.yPos);
        slotIn.slotNumber = cont.inventorySlots.size();
        //System.out.println("HEIGHT: " + gc.getGuiTop() + " | " + gc.getGuiLeft());
        slotIn.xPos -= 135;
        slotIn.yPos -= 25;
        cont.inventorySlots.add(slotIn);
        cont.inventoryItemStacks.add(ItemStack.EMPTY);
        //return slotIn;
    }
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }
	
	public void drawForeground(GuiScreenEvent.DrawScreenEvent event) {
		int n = 0;
		GuiContainer gc = (GuiContainer) event.getGui();
		IInvCapa capa = mc.player.getCapability(InvProvider.EXTRAINV, null);
		if(event.getGui() instanceof GuiLootBody) {
			n -= 360;
		} else if(event.getGui() instanceof GuiTrader) {
			n += 60;// 180 + j * 18 + n, 88 + i * 18,
		}
		 //FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		
		//fr.drawString("hi", 0, 0, 0);
		
		if(!capa.getStackInSlot(4).isEmpty()) {
        	ItemStack stack = capa.getStackInSlot(4);
        	if(stack.getItem() instanceof BackpackItem) {
        		drawString(mc.fontRenderer, TextFormatting.YELLOW + stack.getDisplayName(), gc.getGuiLeft()+180+n, gc.getGuiTop()+80, 0);
        		
        	}
        }
		if(!capa.getStackInSlot(5).isEmpty()) {
        	ItemStack stack = capa.getStackInSlot(5);
        	if(stack.getItem() instanceof RigItem) {
        		drawString(mc.fontRenderer, TextFormatting.YELLOW + stack.getDisplayName(), gc.getGuiLeft()+180+n, gc.getGuiTop()-28, 0);
        	}
        }
		
		
	}
	
	
	public void drawInventory(GuiScreenEvent.BackgroundDrawnEvent event) {
		
			int n = 0;
			if(event.getGui() instanceof GuiLootBody) {
				n -= nVal;
			}
			GuiContainer gc = (GuiContainer) event.getGui();
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        
	        
	        //System.out.println(scaledresolution.getScaledWidth());
	        float x = 0 / 255.0F * scaledresolution.getScaledWidth();
	        
	        //System.out.println(this.guiLeft + " | " + this.guiTop);
	        int i = gc.getGuiLeft()-70 + n;
	        int j = gc.getGuiTop();
	      
	        
	       // 180 + j * 18 + n, 88 + i * 18,
	        
	        
	        GlStateManager.pushMatrix();
	        //GlStateManager.enableAlpha();
	        GlStateManager.enableBlend();
	        //GlStateManager.scale(1.36F, 1.36F, 1.36F);
	        
	        
	        
	        this.mc.getTextureManager().bindTexture(NewInventory.GRAY_IMAGE);
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
	        this.drawTexturedModalRect(i-50,j, 0, 0, 120, 160);
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        
	        
	        GlStateManager.disableBlend();
	        GlStateManager.enableBlend();
	        this.mc.getTextureManager().bindTexture(NewInventory.INVENTORY_BACKGROUND);
	        this.drawTexturedModalRect(i-50,j, 0, 0, 125, 160);
	        
	        GlStateManager.enableAlpha();
	        this.mc.getTextureManager().bindTexture(GuiTestContainer.BACKPACK_SLOT);
	        this.drawTexturedModalRect(i+10,j+60, 32, 0, 18, 18);
	        this.drawTexturedModalRect(i+48,j+60, 32, 0, 18, 18);
	        GlStateManager.disableAlpha();
	        
	        
	        GlStateManager.popMatrix();
	        NewInventory.drawEntityOnScreen((int) (i-23), j + 80, 30, (float)(i + 51), (float)(j + 75 - 50), this.mc.player);
	}
	
	public void slotInjection(GuiOpenEvent event) {
		nVal = 200;
		if(!(event.getGui() instanceof GuiTrader)) {
			inventorySlots(event);
			
			addArmorSlots(event);
		}
		
		
		backpackRigSlotsClient(event);
	}
	
	public void inventorySlots(GuiOpenEvent event) {
		IInvCapa capa = this.mc.player.getCapability(InvProvider.EXTRAINV, null);
		Container c = ((GuiContainer) event.getGui()).inventorySlots;
		int n = 0;
		if(event.getGui() instanceof GuiLootBody) {
			n -= nVal;
		}
		
		int clusterX = -60+n;
        int clusterY = 3+n;
        
        /*
         *  this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 1, clusterX+38, clusterY+19, EnumGear.GLASSES));
        //this.addSlotToContainer(new SlotItemHandler(capa.getHandler(), 1, 96, 13));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 2, clusterX, clusterY+19, EnumGear.HEADSET));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 3, clusterX+19, clusterY, EnumGear.MASK));
        this.addSlotToContainer(new ActualBSlot(capa.getHandler(), 4, clusterX, clusterY+57, this));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 6, clusterX+38, clusterY+38, EnumGear.BODYARMOR));
        this.addSlotToContainer(new SpecialGearSlot(capa.getHandler(), 7, clusterX, clusterY+38, EnumGear.JACKET));
        this.addSlotToContainer(new ActualRSlot(capa.getHandler(), 5, clusterX+38, clusterY+57, this));
        
        
         */
        
		//System.out.println(capa.getHandler().serializeNBT());
		addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 1, clusterX+38, clusterY+19, EnumGear.GLASSES));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 2, clusterX, clusterY+19, EnumGear.HEADSET));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 3, clusterX+19, clusterY, EnumGear.MASK));
        //addSlotToContainer(c, new ActualBSlot(capa.getHandler(), 4, -24, 32, this));
        //addSlotToContainer(c,new SlotItemHandler(capa.getHandler(), 4, -24, 32));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 6, clusterX+38, clusterY+38, EnumGear.BODYARMOR));
        addSlotToContainer(c,new SpecialGearSlot(capa.getHandler(), 7, clusterX, clusterY+38, EnumGear.JACKET));
 //       addSlotToContainer(c,new ActualRSlot(capa.getHandler(), 5, -43, 32, this));
      //  addSlotToContainer(c,new SlotItemHandler(capa.getHandler(), 5, -43, 32));
	}
	
	public void addArmorSlots(GuiOpenEvent event) {
		int n = 0;
		if(event.getGui() instanceof GuiLootBody) {
			n -= nVal;
		}
		IInvCapa capa = this.mc.player.getCapability(InvProvider.EXTRAINV, null);
		Container c = ((GuiContainer) event.getGui()).inventorySlots;
		EntityPlayer player = this.mc.player;
		InventoryPlayer playerInv = this.mc.player.inventory;
		
		int clusterX = -60+n;
        int clusterY = 3+n;
		for (int k = 0; k < 4; ++k)
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            addSlotToContainer(c, new Slot(playerInv, 36 + (3 - k), clusterX+19, clusterY+19+k*19)
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
	
	
	public void backpackRigSlotsClient(GuiOpenEvent event) {
		int n = 0;
		if(event.getGui() instanceof GuiLootBody) {
			n -= 360;
		} else if(event.getGui() instanceof GuiTrader) {
			n += 60;
		}
		IInvCapa capa = this.mc.player.getCapability(InvProvider.EXTRAINV, null);
		if(event.getGui() instanceof GuiContainer && !(event.getGui() instanceof GuiContainerCreative) && !(event.getGui() instanceof NewInventory)) {
			Container c = ((GuiContainer) event.getGui()).inventorySlots;
			
			GuiContainer gc = (GuiContainer) event.getGui();
			//System.out.println(gc.getGuiLeft());
			//GuiWrenchTool.setGuiContainerLeft(gc, gc.getGuiLeft()+100);
			//System.out.println(gc.getGuiLeft());
			if(!capa.getStackInSlot(4).isEmpty()) {
	        	ItemStack stack = capa.getStackInSlot(4);
	        	if(stack.getItem() instanceof BackpackItem) {
	        		ItemStackHandler handler = ((BackpackItem) stack.getItem()).getInv(stack);
	        		int count = 0;
	    			for (int i = 0; i < 5; ++i)
	    	        {
	    	            for (int j = 0; j < 8; ++j)
	    	            {
	    	            	//addSlotToContainer(c, new ISaveableSlot(handler, count, j * 18, 150 + i * 18, (ISaveableItem) capa.getStackInSlot(4).getItem(), capa.getStackInSlot(4)));
			    	          
	    	            	addSlotToContainer(c, new ISaveableSlot(handler, count, 180 + j * 18 + n, 88 + i * 18, (ISaveableItem) capa.getStackInSlot(4).getItem(), capa.getStackInSlot(4)));
	    	             //   c.inventorySlots.add(new ISaveableSlot(handler, count, j * 18, 150 + i * 18, (ISaveableItem) capa.getStackInSlot(4).getItem(), capa.getStackInSlot(4)));
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
	    	            	addSlotToContainer(c, new ISaveableSlot(handler, countRig, 180 + j * 18 + n, -20 + i * 18, (ISaveableItem) capa.getStackInSlot(5).getItem(), capa.getStackInSlot(5)));
	    	               // c.inventorySlots.add(new ISaveableSlot(handler, countRig, 135 + j * 18, 22 + i * 18, (ISaveableItem) capa.getStackInSlot(5).getItem(), capa.getStackInSlot(5)));
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
