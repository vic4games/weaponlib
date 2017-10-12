package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.inventory.EntityEquipmentSlot;

public enum CompatibleEntityEquipmentSlot {
    HEAD(0), CHEST(1), FEET(3), MAIN_HAND(4);

    private int slot;

    private CompatibleEntityEquipmentSlot(int slot) {
        this.slot = slot;
    }

    public EntityEquipmentSlot getSlot() {
        EntityEquipmentSlot result = null;
        switch(slot) {
        case 0: result = EntityEquipmentSlot.HEAD; break;
        case 1: result = EntityEquipmentSlot.CHEST; break;
        case 3: result = EntityEquipmentSlot.FEET; break;
        case 4: result = EntityEquipmentSlot.MAINHAND; break;
        }
        return result;
    }
}

