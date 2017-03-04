package com.vicmatskiv.weaponlib;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CompatibleAttachment<T> {

	private ItemAttachment<T> attachment;
	private Consumer<ModelBase> modelPositioning;
	private BiConsumer<EntityPlayer, ItemStack> positioning;
	private boolean isDefault;
	
	public CompatibleAttachment(ItemAttachment<T> attachment, BiConsumer<EntityPlayer, ItemStack> positioning, Consumer<ModelBase> modelPositioning, boolean isDefault) {
		this.attachment = attachment;
		this.positioning = positioning;
		this.modelPositioning = modelPositioning;
		this.isDefault = isDefault;
	}

	public CompatibleAttachment(ItemAttachment<T> attachment, Consumer<ModelBase> positioning) {
		this(attachment, null, positioning, false);
	}
	
	public CompatibleAttachment(ItemAttachment<T> attachment, Consumer<ModelBase> positioning, boolean isDefault) {
		this.attachment = attachment;
		this.modelPositioning = positioning;
		this.isDefault = isDefault;
	}

	public ItemAttachment<T> getAttachment() {
		return attachment;
	}

	public Consumer<ModelBase> getModelPositioning() {
		return modelPositioning;
	}
	
	public BiConsumer<EntityPlayer, ItemStack> getPositioning() {
		return positioning;
	}

	public boolean isDefault() {
		return isDefault;
	}
}
