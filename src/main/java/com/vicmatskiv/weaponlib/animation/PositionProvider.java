package com.vicmatskiv.weaponlib.animation;

import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface PositionProvider<State> {

	public List<BiConsumer<EntityPlayer, ItemStack>> getPositioning(State state);
}
