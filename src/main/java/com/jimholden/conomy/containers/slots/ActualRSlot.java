package com.jimholden.conomy.containers.slots;

import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.RigItem;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ActualRSlot extends SlotItemHandler {

	private ContainerInvExtend cont;
	
	public ActualRSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ContainerInvExtend cont) {
		super(itemHandler, index, xPosition, yPosition);
		this.cont = cont;
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean isItemValid(ItemStack stack) {
		// TODO Auto-generated method stub
		return stack.getItem() instanceof RigItem;
	}
	
	@Override
	public void onSlotChanged() {
		if(this.getStack().getItem() instanceof RigItem) {
			//System.out.println("fuck: " + this.getStack().getItem());
			//System.out.println("GAMER: " + ((BackpackItem) this.getStack().getItem()).getSize(this.getStack()));
			
			//System.out.println(this.cont.visibilityIndex);
			
			//System.out.println("hi, fuck you");
			
			int newIndex = ((RigItem) this.getStack().getItem()).getSize(this.getStack());
			//System.out.println(newIndex);
			this.cont.visibilityIndexRig = newIndex;
			this.cont.rigHandler = ((RigItem) this.getStack().getItem()).getInv(this.getStack());
			
			//System.out.println(this.cont.visibilityIndex);
			
		}
		super.onSlotChanged();
	}
	

}
