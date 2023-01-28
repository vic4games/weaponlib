package com.jimholden.conomy.items.models.headset;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class CatEars extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer cat_ears;
	private final ModelRenderer cat_ears_r1;
	private final ModelRenderer cat_ears_r2;
	private final ModelRenderer cat_ears_r3;
	private final ModelRenderer cat_ears_r4;
	private final ModelRenderer cat_ears_r5;
	private final ModelRenderer cat_ears_r6;
	private final ModelRenderer cat_ears_r7;
	private final ModelRenderer cat_ears_r8;
	private final ModelRenderer cat_ears_r9;
	private final ModelRenderer cat_ears_r10;

	public CatEars() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightarm.cubeList.add(new ModelBox(rightarm, 36, 36, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftarm.cubeList.add(new ModelBox(leftarm, 32, 0, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		rightleg.cubeList.add(new ModelBox(rightleg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		leftleg.cubeList.add(new ModelBox(leftleg, 24, 24, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		cat_ears = new ModelRenderer(this);
		cat_ears.setRotationPoint(0.0F, 0.0F, 0.0F);
		cat_ears.cubeList.add(new ModelBox(cat_ears, 24, 16, -4.0F, -8.0F, 0.6F, 8, 1, 2, 0.1F, false));
		cat_ears.cubeList.add(new ModelBox(cat_ears, 24, 16, -4.0F, -7.0F, 0.6F, 1, 3, 2, 0.1F, false));
		cat_ears.cubeList.add(new ModelBox(cat_ears, 24, 16, 3.0F, -7.0F, 0.6F, 1, 3, 2, 0.1F, false));

		cat_ears_r1 = new ModelRenderer(this);
		cat_ears_r1.setRotationPoint(-0.85F, -7.55F, 1.6F);
		cat_ears.addChild(cat_ears_r1);
		setRotationAngle(cat_ears_r1, -0.0698F, 0.0F, -0.6632F);
		cat_ears_r1.cubeList.add(new ModelBox(cat_ears_r1, 0, 16, -1.0F, -3.0F, -1.0F, 1, 3, 1, 0.0F, false));

		cat_ears_r2 = new ModelRenderer(this);
		cat_ears_r2.setRotationPoint(4.35F, -8.84F, 1.8F);
		cat_ears.addChild(cat_ears_r2);
		setRotationAngle(cat_ears_r2, 0.0F, 0.0873F, 1.7453F);
		cat_ears_r2.cubeList.add(new ModelBox(cat_ears_r2, 0, 6, 0.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F, false));

		cat_ears_r3 = new ModelRenderer(this);
		cat_ears_r3.setRotationPoint(-4.35F, -8.84F, 1.8F);
		cat_ears.addChild(cat_ears_r3);
		setRotationAngle(cat_ears_r3, 0.0F, -0.0873F, -1.7453F);
		cat_ears_r3.cubeList.add(new ModelBox(cat_ears_r3, 20, 16, -2.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F, false));

		cat_ears_r4 = new ModelRenderer(this);
		cat_ears_r4.setRotationPoint(-3.65F, -9.54F, 1.8F);
		cat_ears.addChild(cat_ears_r4);
		setRotationAngle(cat_ears_r4, -0.0524F, 0.0F, -0.8029F);
		cat_ears_r4.cubeList.add(new ModelBox(cat_ears_r4, 20, 18, -1.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F, false));

		cat_ears_r5 = new ModelRenderer(this);
		cat_ears_r5.setRotationPoint(3.65F, -9.54F, 1.8F);
		cat_ears.addChild(cat_ears_r5);
		setRotationAngle(cat_ears_r5, -0.0524F, 0.0F, 0.8029F);
		cat_ears_r5.cubeList.add(new ModelBox(cat_ears_r5, 24, 2, 0.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F, false));

		cat_ears_r6 = new ModelRenderer(this);
		cat_ears_r6.setRotationPoint(-2.75F, -9.9F, 1.8F);
		cat_ears.addChild(cat_ears_r6);
		setRotationAngle(cat_ears_r6, -0.0524F, 0.0F, -0.384F);
		cat_ears_r6.cubeList.add(new ModelBox(cat_ears_r6, 24, 0, -1.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F, false));

		cat_ears_r7 = new ModelRenderer(this);
		cat_ears_r7.setRotationPoint(2.75F, -9.9F, 1.8F);
		cat_ears.addChild(cat_ears_r7);
		setRotationAngle(cat_ears_r7, -0.0524F, 0.0F, 0.384F);
		cat_ears_r7.cubeList.add(new ModelBox(cat_ears_r7, 24, 4, 0.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F, false));

		cat_ears_r8 = new ModelRenderer(this);
		cat_ears_r8.setRotationPoint(-2.35F, -9.45F, 1.8F);
		cat_ears.addChild(cat_ears_r8);
		setRotationAngle(cat_ears_r8, 0.4538F, 0.0F, -0.2793F);
		cat_ears_r8.cubeList.add(new ModelBox(cat_ears_r8, 0, 0, -2.0F, 0.0F, -1.0F, 2, 2, 1, 0.0F, false));

		cat_ears_r9 = new ModelRenderer(this);
		cat_ears_r9.setRotationPoint(2.35F, -9.45F, 1.8F);
		cat_ears.addChild(cat_ears_r9);
		setRotationAngle(cat_ears_r9, 0.4538F, 0.0F, 0.2793F);
		cat_ears_r9.cubeList.add(new ModelBox(cat_ears_r9, 0, 3, 0.0F, 0.0F, -1.0F, 2, 2, 1, 0.0F, false));

		cat_ears_r10 = new ModelRenderer(this);
		cat_ears_r10.setRotationPoint(0.85F, -7.55F, 1.6F);
		cat_ears.addChild(cat_ears_r10);
		setRotationAngle(cat_ears_r10, -0.0698F, 0.0F, 0.6632F);
		cat_ears_r10.cubeList.add(new ModelBox(cat_ears_r10, 24, 24, 0.0F, -3.0F, -1.0F, 1, 3, 1, 0.0F, false));
		
		bipedHeadwear.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedBody.isHidden = true;
		
		bipedHead = cat_ears;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}