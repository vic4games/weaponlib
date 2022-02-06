package com.vicmatskiv.weaponlib.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.1.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


public class ArmModel extends ModelBase {
	private final ModelRenderer righthand;

	public ArmModel() {
		textureWidth = 200;
		textureHeight = 200;

		righthand = new ModelRenderer(this);
		righthand.setRotationPoint(-4.4F, 0.7F, 27.9F);
		righthand.cubeList.add(new ModelBox(righthand, 0, 0, -0.1F, 18.8F, -27.9F, 9, 9, 33, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		righthand.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}