package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackSlots extends SlotItemHandler {
	
	private ContainerInvExtend cont;

	public BackpackSlots(IItemHandler itemHandler, int index, int xPosition, int yPosition, ContainerInvExtend cont) {
		super(itemHandler, index, xPosition, yPosition);
		this.cont = cont;
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean isEnabled() {
		if(this.cont.visibilityIndex > this.getSlotIndex()) {
			return true;
		} else {
			return false;
		}
		//return 
	}
	
	
	@Override
	public IItemHandler getItemHandler() {
		if(this.cont.visibilityIndex > this.getSlotIndex()) {
			return this.cont.handlerBackpack;
			
		} else {
			return super.getItemHandler();
		}
	}
	
	
	
	
	
	@Override
	public void onSlotChanged() {
		this.cont.saveBackpack();
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
