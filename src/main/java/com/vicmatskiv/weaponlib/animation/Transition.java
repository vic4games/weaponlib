package com.vicmatskiv.weaponlib.animation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vicmatskiv.weaponlib.RenderContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Transition {

	private BiConsumer<EntityPlayer, ItemStack> positioning;
	private Consumer<RenderContext> itemPositioning;
	private long duration;
	private long pause;
	
	public Transition(BiConsumer<EntityPlayer, ItemStack> positioning, long duration, long pause) {
		this.positioning = positioning;
		this.duration = duration;
		this.pause = pause;
	}
	
	public Transition(Consumer<RenderContext> itemPositioning, long duration, long pause) {
		this.itemPositioning = itemPositioning;
		this.duration = duration;
		this.pause = pause;
	}
	
	public Transition(BiConsumer<EntityPlayer, ItemStack> positioning, long duration) {
		this(positioning, duration, 0);
	}
	
	public Transition(Consumer<RenderContext> itemPositioning, long duration) {
		this(itemPositioning, duration, 0);
	}

	public BiConsumer<EntityPlayer, ItemStack> getPositioning() {
		return positioning;
	}
	
	public Consumer<RenderContext> getItemPositioning() {
		return itemPositioning;
	}

	public long getDuration() {
		return duration;
	}

	public long getPause() {
		return pause;
	}
}
