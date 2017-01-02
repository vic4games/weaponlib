package com.vicmatskiv.weaponlib;

import net.minecraft.client.model.ModelBase;

public class ItemMagazine extends ItemAttachment<Weapon> {

	public ItemMagazine(String modId, AttachmentCategory category, ModelBase attachment, String textureName,
			String crosshair) {
		super(modId, category, attachment, textureName, crosshair);
	}

	public ItemMagazine(String modId, AttachmentCategory category, ModelBase attachment, String textureName,
			String crosshair, com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> apply,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> remove) {
		super(modId, category, attachment, textureName, crosshair, apply, remove);
	}

	public ItemMagazine(String modId, AttachmentCategory category, String crosshair,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> apply,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> remove) {
		super(modId, category, crosshair, apply, remove);
	}

	public ItemMagazine(String modId, AttachmentCategory category, String crosshair) {
		super(modId, category, crosshair);
	}
}
