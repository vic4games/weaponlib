package com.vicmatskiv.weaponlib.inventory;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vicmatskiv.weaponlib.ItemStorage;
import com.vicmatskiv.weaponlib.ItemVest;
import com.vicmatskiv.weaponlib.compatibility.CompatibleContainer;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityEquipmentSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class CustomPlayerInventoryContainer extends CompatibleContainer {
    
    @SuppressWarnings("unused")
    private CustomPlayerInventory customPlayerInventory;

    private int customSlotStartIndex;
    private int customSlotEndIndex;
    private int armorSlotStartIndex;
    private int armorSlotEndIndex;
    private int standardInventorySlotStartIndex;
    private int standardInventorySlotEndIndex;
    private int hotbarSlotStartIndex;
    private int hotbarSlotEndIndex;
    
    private List<Slot> customSlots;
    
    public CustomPlayerInventoryContainer(EntityPlayer player, InventoryPlayer inventoryPlayer,
            CustomPlayerInventory customPlayerInventory) {
        
        this.customPlayerInventory = customPlayerInventory;
        
        this.customSlots = createCustomSlots(customPlayerInventory);
        customSlots.forEach(slot -> addSlotToContainer(slot));
        
        this.customSlotStartIndex = 0;
        this.customSlotEndIndex = customSlotStartIndex + customSlots.size() - 1;
        
        List<Slot> armorSlots = createArmorSlots(player, inventoryPlayer);
        armorSlots.forEach(slot -> addSlotToContainer(slot));
        
        this.armorSlotStartIndex = customPlayerInventory.getSizeInventory();
        this.armorSlotEndIndex = armorSlotStartIndex + armorSlots.size() - 1;
        
        List<Slot> standardInventorySlots = createStandardInventorySlots(inventoryPlayer);
        standardInventorySlots.forEach(slot -> addSlotToContainer(slot));
        
        this.standardInventorySlotStartIndex = armorSlotEndIndex + 1;
        this.standardInventorySlotEndIndex = standardInventorySlotStartIndex + standardInventorySlots.size() - 1;
        
        List<Slot> hotbarSlots = createHotbarSlots(inventoryPlayer);
        hotbarSlots.forEach(slot -> addSlotToContainer(slot));
        
        this.hotbarSlotStartIndex = standardInventorySlotEndIndex + 1;
        this.hotbarSlotEndIndex = hotbarSlotStartIndex + hotbarSlots.size() - 1;
    }

    protected List<Slot> createCustomSlots(CustomPlayerInventory inventoryCustom) {
        return Arrays.asList(
                new CustomSlot(ItemStorage.class, inventoryCustom, 0, 80, 8),
                new CustomSlot(ItemVest.class, inventoryCustom, 1, 80, 26));
    }

    protected List<Slot> createHotbarSlots(InventoryPlayer inventoryPlayer) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            slots.add(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
        return slots;
    }

    protected List<Slot> createStandardInventorySlots(InventoryPlayer inventoryPlayer) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                slots.add(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        return slots;
    }

    protected List<Slot> createArmorSlots(EntityPlayer player, InventoryPlayer inventoryPlayer) {
        List<Slot> slots = new ArrayList<>();
        int i;
        for (i = 0; i < 4; ++i) {
            slots.add(new ArmorSlot(player, inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i,
                    8, 8 + i * 18, CompatibleEntityEquipmentSlot.valueOf(i)));
        }
        return slots;
    }

    /**
     * This should always return true, since custom inventory can be accessed
     * from anywhere
     */
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or
     * you will crash when someone does that. Basically the same as every other
     * container I make, since I define the same constant indices for all of
     * them
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // Either armor slot or custom item slot was clicked
            if (slotIndex < standardInventorySlotStartIndex) {
                // try to place in player inventory / action bar
                if (!this.mergeItemStack(itemstack1, standardInventorySlotStartIndex, hotbarSlotEndIndex + 1, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            // Item is in inventory / hotbar, try to place either in custom or
            // armor slots
            else {
                // if item is our custom item                
                if (customSlots.stream().anyMatch(s -> s.isItemValid(itemstack1))) {
                    if (!this.mergeItemStack(itemstack1, customSlotStartIndex, customSlotEndIndex + 1, false)) {
                        return null;
                    }
                }
                // if item is armor
                else if (itemstack1.getItem() instanceof ItemArmor) {
                    CompatibleEntityEquipmentSlot type = compatibility.getArmorType((ItemArmor) itemstack1.getItem());//((ItemArmor) itemstack1.getItem()).armorType;
                    
                    if (!this.mergeItemStack(itemstack1, armorSlotStartIndex + type.ordinal(), armorSlotStartIndex + type.ordinal() + 1, false)) {
                        return null;
                    }
                }
                // item in player's inventory, but not in action bar
                else if (slotIndex >= standardInventorySlotStartIndex && slotIndex < hotbarSlotStartIndex) {
                    // place in action bar
                    if (!this.mergeItemStack(itemstack1, hotbarSlotStartIndex, hotbarSlotEndIndex + 1, false)) {
                        return null;
                    }
                }
                // item in action bar - place in player inventory
                else if (slotIndex >= hotbarSlotStartIndex && slotIndex < hotbarSlotEndIndex + 1) {
                    if (!this.mergeItemStack(itemstack1, standardInventorySlotStartIndex, standardInventorySlotEndIndex + 1, false)) {
                        return null;
                    }
                }
            }

            if (compatibility.getStackSize(itemstack1) == 0) {
                slot.putStack(compatibility.stackForEmptySlot());
            } else {
                slot.onSlotChanged();
            }

            if (compatibility.getStackSize(itemstack1) == compatibility.getStackSize(itemstack)) {
                return null;
            }

            onTakeFromSlot(slot, player, itemstack1);
        }

        return itemstack;
    }
}
