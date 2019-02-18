package com.vicmatskiv.weaponlib.inventory;

import com.vicmatskiv.weaponlib.compatibility.CompatibleBlocks;

import net.minecraft.item.ItemStack;

public class StandardPlayerInventoryTab extends InventoryTab {
    public StandardPlayerInventoryTab() {
        super(0, 0, 0, new ItemStack(CompatibleBlocks.CRAFTING_TABLE.getBlock()));
    }

    @Override
    public void onTabClicked() {
        InventoryTabs.getInstance().openInventoryGui();
    }

    @Override
    public boolean shouldAddToList() {
        return true;
    }
}
