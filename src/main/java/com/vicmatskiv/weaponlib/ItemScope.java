package com.vicmatskiv.weaponlib;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemScope extends ItemAttachment<Weapon> {
	
	private final int DEFAULT_MAX_STACK_SIZE = 1;
		
	public static final class Builder extends AttachmentBuilder<Weapon> {
		
		private float minZoom;
		private float maxZoom;
		private boolean isOpticalZoom;
		private BiConsumer<EntityPlayer, ItemStack> viewfinderPositioning;
		
		public Builder withZoomRange(float minZoom, float maxZoom) {
			this.minZoom = minZoom;
			this.maxZoom = maxZoom;
			return this;
		}
		
		public Builder withOpticalZoom() {
			this.isOpticalZoom = true;
			return this;
		}
		
		public Builder withViewfinderPositioning(BiConsumer<EntityPlayer, ItemStack> viewfinderPositioning) {
			this.viewfinderPositioning = viewfinderPositioning;
			return this;
		}
		
		@Override
		protected ItemAttachment<Weapon> createAttachment(ModContext modContext) {
			if(isOpticalZoom) {
				if(viewfinderPositioning == null) {
					viewfinderPositioning = (p, s) -> {
	                    GL11.glScalef(1.1f, 1.1f, 1.1f);
	                    GL11.glTranslatef(0.1f, 0.4f, 0.6f);
	                };
				}
				withPostRender(new ViewfinderRenderer(modContext, viewfinderPositioning));
			}
			
			ItemScope itemScope = new ItemScope(this);
			itemScope.modContext = modContext;
			
			return itemScope;
		}
	}
	
	private ModContext modContext;
	private Builder builder;
	
	private ItemScope(Builder builder) {
		super(builder.modId, AttachmentCategory.SCOPE, builder.model, builder.textureName, null, 
				(a, t, p) -> {
					float zoom = builder.minZoom + (builder.maxZoom - builder.minZoom) / 2f;
					t.changeZoom(p, zoom);
				}, (a, t, p) -> {
					t.changeZoom(p, 1f);
				});
		this.builder = builder;
		setMaxStackSize(DEFAULT_MAX_STACK_SIZE);
	}
	
	public float getMinZoom() {
		return builder.minZoom;
	}
	
	public float getMaxZoom() {
		return builder.maxZoom;
	}
	
	public boolean isOptical() {
		return builder.isOpticalZoom;
	}
}
