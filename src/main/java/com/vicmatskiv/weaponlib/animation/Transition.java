package com.vicmatskiv.weaponlib.animation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Transition<Context> {

	private BiConsumer<EntityPlayer, ItemStack> positioning;
	private Consumer<Context> itemPositioning;
	private long duration;
	private long pause;
	
	public Transition(BiConsumer<EntityPlayer, ItemStack> positioning, long duration, long pause) {
		this.positioning = positioning;
		this.duration = duration;
		this.pause = pause;
	}
	
	public Transition(Consumer<Context> itemPositioning, long duration, long pause) {
		this.itemPositioning = itemPositioning;
		this.duration = duration;
		this.pause = pause;
	}
	
	public Transition(BiConsumer<EntityPlayer, ItemStack> positioning, long duration) {
		this(positioning, duration, 0);
	}
	
	public Transition(Consumer<Context> itemPositioning, long duration) {
		this(itemPositioning, duration, 0);
	}

	public BiConsumer<EntityPlayer, ItemStack> getPositioning() {
		return positioning;
	}
	
	public Consumer<Context> getItemPositioning() {
		return itemPositioning;
	}

	public long getDuration() {
		return duration;
	}

	public long getPause() {
		return pause;
	}
}
