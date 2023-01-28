package com.jimholden.conomy.containers.slots;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class AdvItemHandler extends ItemStackHandler {
	
	public AdvItemHandler(int size)
    {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }
	
	public void shrinkStackInSlot(int index) {
		ItemStack stack = getStackInSlot(index);
		if(stack.getCount() > 1) {
			stack.shrink(1);
			setStackInSlot(index, stack);
		} else {
			stack = ItemStack.EMPTY;
			setStackInSlot(index, stack);
		}
	}

}
