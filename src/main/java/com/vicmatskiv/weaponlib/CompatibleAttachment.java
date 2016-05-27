package com.vicmatskiv.weaponlib;

import java.util.function.Consumer;

import net.minecraft.client.model.ModelBase;

public class CompatibleAttachment<T> {

	private ItemAttachment<T> attachment;
	private Consumer<ModelBase> positioning;
	private boolean isDefault;

	public CompatibleAttachment(ItemAttachment<T> attachment, Consumer<ModelBase> positioning) {
		this(attachment, positioning, false);
	}
	
	public CompatibleAttachment(ItemAttachment<T> attachment, Consumer<ModelBase> positioning, boolean isDefault) {
		this.attachment = attachment;
		this.positioning = positioning;
		this.isDefault = isDefault;
	}

	public ItemAttachment<T> getAttachment() {
		return attachment;
	}

	public Consumer<ModelBase> getPositioning() {
		return positioning;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
}
