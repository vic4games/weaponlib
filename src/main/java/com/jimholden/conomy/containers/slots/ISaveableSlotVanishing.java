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

public class ISaveableSlotVanishing extends ISaveableSlot {

	public ISaveableSlotVanishing(IItemHandler itemHandler, int index, int xPosition, int yPosition, ISaveableItem item,
			ItemStack stack, int visibilityIndex) {
		super(itemHandler, index, xPosition, yPosition, item, stack);
		// TODO Auto-generated constructor stub
	}
	
	

}
