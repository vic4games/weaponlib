package com.jimholden.conomy.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public interface ISaveableItem {

	public void ensureNBTUpdated(ItemStack stack);
	public void saveInv(ItemStack stack, ItemStackHandler handler);
	public void recalculateWeight(ItemStack stack);
}
