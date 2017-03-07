package com.vicmatskiv.weaponlib;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.model.FlatModel;

import net.minecraft.client.model.ModelBase;

public class ItemSkin extends ItemAttachment<Weapon> {
	
	public static final class Builder extends AttachmentBuilder<Weapon> {

		@Override
		protected ItemAttachment<Weapon> createAttachment(ModContext modContext) {
			return new ItemSkin(modId, AttachmentCategory.SKIN, model, textureName, null, null, null);
		}
		
		@Override
		public <V extends ItemAttachment<Weapon>> V build(ModContext modContext, Class<V> target) {
			model = new FlatModel();
			if(inventoryPositioning == null) {
				withInventoryPositioning((itemStack) -> {
					GL11.glRotatef(20F, 1f, 0f, 0f);
					GL11.glRotatef(-45F, 0f, 1f, 0f);
					GL11.glRotatef(0F, 0f, 0f, 1f);
					GL11.glTranslatef(-0.6f, -0.6f, 0F);
					GL11.glScaled(15F, 15F, 15f);
				});
			}
			return super.build(modContext, target);
		}
	}
	
	public ItemSkin(String modId, AttachmentCategory category, ModelBase model, String textureName, String crosshair,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> apply,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> remove) {
		super(modId, category, model, textureName, crosshair, apply, remove);
	}

	public String getTextureName() {
		return textureName;
	}
}