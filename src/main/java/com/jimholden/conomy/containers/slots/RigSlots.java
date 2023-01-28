package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.containers.ContainerInvExtend;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RigSlots extends SlotItemHandler {
	
	private ContainerInvExtend cont;

	public RigSlots(IItemHandler itemHandler, int index, int xPosition, int yPosition, ContainerInvExtend cont) {
		super(itemHandler, index, xPosition, yPosition);
		this.cont = cont;
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean isEnabled() {
		if(this.cont.visibilityIndexRig > this.getSlotIndex()) {
			return true;
		} else {
			return false;
		}
		//return 
	}
	
	
	@Override
	public IItemHandler getItemHandler() {
		if(this.cont.visibilityIndexRig > this.getSlotIndex()) {
			return this.cont.rigHandler;
			
		} else {
			return super.getItemHandler();
		}
	}
	
	
	
	
	
	@Override
	public void onSlotChanged() {
		this.cont.saveRig();
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