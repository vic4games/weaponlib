package com.jimholden.conomy.containers.slots;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotATMHandler extends SlotItemHandler {

	public UUID uuid;
	
	public SlotATMHandler(UUID uuid, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.uuid = uuid;
	}


}
