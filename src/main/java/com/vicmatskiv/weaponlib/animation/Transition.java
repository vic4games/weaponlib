package com.vicmatskiv.weaponlib.animation;

import java.util.function.BiConsumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Transition {

	private BiConsumer<EntityPlayer, ItemStack> positioning;
	private long duration;
	private long pause;
	
	public Transition(BiConsumer<EntityPlayer, ItemStack> positioning, long duration, long pause) {
		this.positioning = positioning;
		this.duration = duration;
		this.pause = pause;
	}
	
	public Transition(BiConsumer<EntityPlayer, ItemStack> positioning, long duration) {
		this(positioning, duration, 0);
	}

	public BiConsumer<EntityPlayer, ItemStack> getPositioning() {
		return positioning;
	}

	public long getDuration() {
		return duration;
	}

	public long getPause() {
		return pause;
	}
}
