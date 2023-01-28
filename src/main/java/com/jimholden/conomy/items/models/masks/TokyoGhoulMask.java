package com.jimholden.conomy.items.models.masks;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class TokyoGhoulMask extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer mask;
	private final ModelRenderer mask_r1;
	private final ModelRenderer mask_r2;
	private final ModelRenderer mask_r3;
	private final ModelRenderer mask_r4;
	private final ModelRenderer mask_r5;

	public TokyoGhoulMask() {
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

		mask = new ModelRenderer(this);
		mask.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(mask);
		

		mask_r1 = new ModelRenderer(this);
		mask_r1.setRotationPoint(-3.1F, -5.4F, -4.1F);
		mask.addChild(mask_r1);
		setRotationAngle(mask_r1, 0.0F, 0.1047F, 0.0F);
		mask_r1.cubeList.add(new ModelBox(mask_r1, 0, 16, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		mask_r2 = new ModelRenderer(this);
		mask_r2.setRotationPoint(-1.3F, -5.6F, -4.3F);
		mask.addChild(mask_r2);
		setRotationAngle(mask_r2, 0.0F, -0.0524F, -0.2793F);
		mask_r2.cubeList.add(new ModelBox(mask_r2, 20, 16, 0.02F, -0.02F, 0.0F, 5, 1, 1, 0.0F, false));

		mask_r3 = new ModelRenderer(this);
		mask_r3.setRotationPoint(5.4F, -1.7F, -2.1F);
		mask.addChild(mask_r3);
		setRotationAngle(mask_r3, 0.0F, -0.1745F, 0.2967F);
		mask_r3.cubeList.add(new ModelBox(mask_r3, 0, 0, -2.0F, 0.0F, -1.0F, 2, 2, 2, -0.2F, false));

		mask_r4 = new ModelRenderer(this);
		mask_r4.setRotationPoint(-5.4F, -1.7F, -2.1F);
		mask.addChild(mask_r4);
		setRotationAngle(mask_r4, 0.0F, 0.1745F, -0.2967F);
		mask_r4.cubeList.add(new ModelBox(mask_r4, 0, 4, 0.0F, 0.0F, -1.0F, 2, 2, 2, -0.2F, false));

		mask_r5 = new ModelRenderer(this);
		mask_r5.setRotationPoint(-3.8F, -5.9F, -4.3F);
		mask.addChild(mask_r5);
		setRotationAngle(mask_r5, 0.0F, 0.1047F, -0.1396F);
		mask_r5.cubeList.add(new ModelBox(mask_r5, 24, 0, 0.0F, 0.0F, 0.0F, 3, 3, 1, -0.2F, false));
		
		bipedHead = head;
		bipedHeadwear.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedBody.isHidden = true;
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