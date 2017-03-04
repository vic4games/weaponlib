package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.FlatSurfaceModelBox;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ViewfinderModel extends ModelBase {

	private ModelRenderer surfaceRenderer;
	private FlatSurfaceModelBox box;

	@SuppressWarnings("unchecked")
	public ViewfinderModel() {
		textureWidth = 128;
		textureHeight = 64;

		surfaceRenderer = new ModelRenderer(this, 0, 0);
		box = new FlatSurfaceModelBox(surfaceRenderer, 0, 0, 0f, 0f, 0f, 3, 3, 0, 0.0F);
		surfaceRenderer.cubeList.add(box);
		surfaceRenderer.mirror = true;
		surfaceRenderer.setRotationPoint(0F, -10F, 0F);
		surfaceRenderer.setTextureSize(100, 100);
		
		setRotation(surfaceRenderer, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		surfaceRenderer.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
