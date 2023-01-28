package com.jimholden.conomy.inventory;

import akka.japi.Pair;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class InventoryUtil {
	
	public static Slot getRight(Slot s, Container c) {
		int mainSlot = s.getSlotIndex();
		
		//System.out.println("gama");
		
		for(int x = 0; x < c.inventorySlots.size(); ++x) {
			Slot s2 = c.inventorySlots.get(x);
			//System.out.println("(" + s2.xPos + ", " + s2.yPos + ") vs (" + s.xPos + " + " + s.yPos + ")");
			if(s2.xPos < s.xPos+19 && s2.xPos > s.xPos && s2.yPos == s.yPos) {
				return s2;
				//System.out.println("detected: (" + s.xPos + ", " + s.yPos + ") | (" + s2.xPos + ", " + s2.yPos + ")");
				
				
			}
		}
		return null; 
		
	
		
		
		
		
	}

}
