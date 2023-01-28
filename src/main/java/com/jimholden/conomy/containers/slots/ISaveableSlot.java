package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.items.ISaveableItem;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ISaveableSlot extends SlotItemHandler {
	
	private ISaveableItem item;
	private ItemStack stack;

	public ISaveableSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ISaveableItem item, ItemStack stack) {
		super(itemHandler, index, xPosition, yPosition);
		this.item = item;
		this.stack = stack;
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean isEnabled() {
		return true;
		//return 
	}
	
	
	
	
	
	
	
	@Override
	public void onSlotChanged() {
		this.item.saveInv(stack, (ItemStackHandler) this.getItemHandler());
		super.onSlotChanged();
	}
	
	
	
	
	
	/*
	@Override
	public String getSlotTexture() {
		// TODO Auto-generated method stub
		//"minecraft:items/empty_armor_slot_boots"
		//backpackslot.png
		return new ResourceLocation(Reference.MOD_ID + ":textures/items/backpackslot.png").toString();
	}

	*/
	
	/*
	@Override
	public ResourceLocation getBackgroundLocation() {
		// TODO Auto-generated method stub
		return new ResourceLocation(Reference.MOD_ID + ":textures/items/backpackslot.png");
	}
	*/
	
	

}
