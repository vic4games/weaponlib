package com.vicmatskiv.weaponlib.compatibility;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CompatibleItemMethods {

    public void addInformation(ItemStack itemStack, List<String> info,  boolean flag);
    
    public default void addInformation(ItemStack itemStack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addInformation(itemStack, tooltip, true);
    }
}
