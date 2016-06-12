package com.vicmatskiv.weaponlib.animation;

import java.util.function.BiConsumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface PositionProvider<State> {

	public BiConsumer<EntityPlayer, ItemStack> getPositioning(State state);
}
