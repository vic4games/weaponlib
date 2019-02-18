package com.vicmatskiv.weaponlib.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.vicmatskiv.weaponlib.ModContext;

public class CustomPlayerInventoryTab extends InventoryTab {

    private ModContext clientModContext;

    public CustomPlayerInventoryTab(ModContext clientModContext, Item tabIconItem) {
        super(0, 0, 0, new ItemStack(tabIconItem));
        this.clientModContext = clientModContext;
    }

    @Override
    public void onTabClicked() {
        clientModContext.getChannel().getChannel()
                .sendToServer(new OpenCustomPlayerInventoryGuiMessage(GuiHandler.CUSTOM_PLAYER_INVENTORY_GUI_ID));

    }

    @Override
    public boolean shouldAddToList() {
        return true;
    }
}
