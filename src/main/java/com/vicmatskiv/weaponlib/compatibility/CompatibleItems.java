package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CompatibleItems {

    public static final CompatibleItems GUNPOWDER = new CompatibleItems(Items.gunpowder) {};
    
    private Item item;
    
    private CompatibleItems(Item item) {
        this.item = item;
    }
    
    public Item getItem() {
        return item;
    }
}
