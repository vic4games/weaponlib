package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;

import net.minecraft.item.ItemStack;

public interface CustomRenderer {

	public void render(CompatibleTransformType type, ItemStack itemStack);
}
