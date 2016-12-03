package com.vicmatskiv.weaponlib;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation")
public interface CustomRenderer {

	public void render(TransformType type, ItemStack itemStack);
}
