package com.vicmatskiv.weaponlib.inventory;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CustomSlot extends Slot {
    
    private Class<?> itemClass;
    
    public CustomSlot(Class<?> itemClass, IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
        this.itemClass = itemClass;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return itemClass.isInstance(stack.getItem());//stack.getItem() instanceof ItemStorage;
    }
    
    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        if(compatibility.isClientSide()) {
            SlotChangeHandler.onGuiSlotChanged();
        }
    }
    
    private static class SlotChangeHandler {
        private static void onGuiSlotChanged() {
            CustomPlayerInventoryGuiContainer container = CustomPlayerInventoryGuiContainer.getClickedGuiContainer();
            if(container != null) {           
                container.initGui();
            }
        }
    }
    
}