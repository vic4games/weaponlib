package com.vicmatskiv.weaponlib;

import net.minecraft.item.ItemStack;

public interface IItemRenderer {

	void renderItem(ItemRenderType type, ItemStack item, Object[] data);

}
