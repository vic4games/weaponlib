package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.model.FlatModel;

import net.minecraft.client.model.ModelBase;

public class ItemSkin extends ItemAttachment<Weapon> {
	
	public static final class Builder extends AttachmentBuilder<Weapon> {
		private List<String> textureVariants = new ArrayList<>();
		
		public Builder withTextureVariant(String... textureVariantNames) {
			for(String s: textureVariantNames) {
				this.textureVariants.add(stripFileExtension(s.toLowerCase(), ".png"));
			}
			return this;
		}

		@Override
		protected ItemAttachment<Weapon> createAttachment(ModContext modContext) {
			ItemSkin skin = new ItemSkin(getModId(), AttachmentCategory.SKIN, getModel(), getTextureName(), null, null, null);
			skin.textureVariants = this.textureVariants;
			return skin;
		}
		
		@Override
		public <V extends ItemAttachment<Weapon>> V build(ModContext modContext, Class<V> target) {
			this.model = new FlatModel();
			if(textureVariants.isEmpty()) {
				textureVariants.add(getTextureName());
			} else if(getTextureName() == null) {
				this.textureName = textureVariants.get(0);
			}
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
	
	private List<String> textureVariants;
	
	public ItemSkin(String modId, AttachmentCategory category, ModelBase model, String textureName, String crosshair,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> apply,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> remove) {
		super(modId, category, model, textureName, crosshair, apply, remove);
	}

	public String getTextureName() {
		return textureName;
	}
	
	public int getTextureVariantIndex(String name) {
		return textureVariants.indexOf(name);
	}
	
	public String getTextureVariant(int textureIndex) {
		return textureIndex >= 0 && textureIndex < textureVariants.size() ? textureVariants.get(textureIndex) : null;
	}
}