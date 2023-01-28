package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.items.BackpackItem;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ActualBSlot extends SlotItemHandler {

	private ContainerInvExtend cont;
	
	public ActualBSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ContainerInvExtend cont) {
		super(itemHandler, index, xPosition, yPosition);
		this.cont = cont;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		// TODO Auto-generated method stub
		return stack.getItem() instanceof BackpackItem;
	}
	
	@Override
	public void onSlotChanged() {
		if(this.getStack().getItem() instanceof BackpackItem) {
			//System.out.println("fuck: " + this.getStack().getItem());
			//System.out.println("GAMER: " + ((BackpackItem) this.getStack().getItem()).getSize(this.getStack()));
			
			//System.out.println(this.cont.visibilityIndex);
			
			//System.out.println("hi, fuck you");
			
			int newIndex = ((BackpackItem) this.getStack().getItem()).getSize(this.getStack());
			//System.out.println(newIndex);
			this.cont.visibilityIndex = newIndex;
			this.cont.handlerBackpack = ((BackpackItem) this.getStack().getItem()).getInv(this.getStack());
			
			//System.out.println(this.cont.visibilityIndex);
			
		}
		super.onSlotChanged();
	}
	

}
