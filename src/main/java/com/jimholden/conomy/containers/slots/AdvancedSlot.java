package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.inventory.InventoryUtil;
import com.jimholden.conomy.items.ItemFlag;

import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AdvancedSlot extends Slot
{
	public final Slot ref;
	public Container cont;
	
	public AdvancedSlot(Slot s, Container cont)
	{
		super(s.inventory, s.slotNumber, s.xPos, s.yPos);
		this.ref = s;
		this.slotNumber = s.slotNumber;
		this.cont = cont;
	}

	@Override
	public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
    {
		
		this.ref.onSlotChange(p_75220_1_, p_75220_2_);
		
    }

    @Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
    {
        return this.ref.onTake(thePlayer, stack);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
    	System.out.println("sided ness");
    	
        //return !InventoryUtil.getRight(this.ref, this.cont);
    	return this.ref.isItemValid(stack);
    }

    @Override
	public ItemStack getStack()
    {
        return this.ref.getStack();
    }

    @Override
	public boolean getHasStack()
    {
        return this.ref.getHasStack();
    }
    
    @Override
	public void putStack(ItemStack stack)
    {
    	this.ref.putStack(stack);
    }

    @Override
	public void onSlotChanged()
    {
    	
        this.ref.onSlotChanged();
		
    }
    
    @Override
	public int getSlotStackLimit()
    {
        return this.ref.getSlotStackLimit();
    }

    @Override
	public int getItemStackLimit(ItemStack stack)
    {
        return this.ref.getItemStackLimit(stack);
    }

    @Override
	@Nullable
    @SideOnly(Side.CLIENT)
    public String getSlotTexture()
    {
        return this.ref.getSlotTexture();
    }

    @Override
	public ItemStack decrStackSize(int amount)
    {
        return this.ref.decrStackSize(amount);
    }

    
    @Override
	public boolean isHere(IInventory inv, int slotIn)
    {
        return this.ref.isHere(inv, slotIn);
    }

    @Override
	public boolean canTakeStack(EntityPlayer playerIn)
    {
        return this.ref.canTakeStack(playerIn);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public boolean isEnabled()
    {
    	if(!(this.ref.getStack().getItem() instanceof ItemFlag)) {
    		return this.ref.isEnabled();
    	} else {
    		return false;
    	}
         
    }

    @Override
	@SideOnly(Side.CLIENT)
    public net.minecraft.util.ResourceLocation getBackgroundLocation()
    {
        return this.ref.getBackgroundLocation();
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void setBackgroundLocation(net.minecraft.util.ResourceLocation texture)
    {
        this.ref.setBackgroundLocation(texture);
    }

    
    @Override
	public void setBackgroundName(String name)
    {
       	this.ref.setBackgroundName(name);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public net.minecraft.client.renderer.texture.TextureAtlasSprite getBackgroundSprite()
    {
        return this.ref.getBackgroundSprite();
    }

    @Override
	public int getSlotIndex()
    {
        return this.ref.getSlotIndex();
    }
    
    @Override
	public boolean isSameInventory(Slot other)
    {
        return this.ref.isSameInventory(other);
    }
}


