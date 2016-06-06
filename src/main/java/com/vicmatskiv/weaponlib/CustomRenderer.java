package com.vicmatskiv.weaponlib;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public interface CustomRenderer {

	public void render(ItemRenderType type, ItemStack itemStack);
}
