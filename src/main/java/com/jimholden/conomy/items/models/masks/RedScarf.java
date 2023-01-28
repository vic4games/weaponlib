package com.jimholden.conomy.items.models.masks;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class RedScarf extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer scarf;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;

	public RedScarf() {
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

		scarf = new ModelRenderer(this);
		scarf.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-0.35F, 1.45F, -3.25F);
		scarf.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.0524F, 0.0349F, 0.2094F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -3.0F, 0.0F, 0.0F, 3, 6, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.4F, 1.8F, -3.5F);
		scarf.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.7505F, -0.0175F, 0.192F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 40, 25, -5.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-5.7F, 0.1F, -2.4F);
		scarf.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.2094F, 0.1571F, -0.3491F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 36, 16, 0.0F, 0.0F, 0.0F, 2, 2, 7, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(4.3F, -2.3F, 4.4F);
		scarf.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0873F, 0.0175F, -0.0698F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 20, 16, -9.0F, 0.0F, -1.0F, 9, 2, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(5.9F, -1.1F, -2.5F);
		scarf.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.1396F, -0.1571F, 0.192F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 40, -2.0F, 0.0F, 0.0F, 2, 2, 7, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-0.3F, 2.6F, -3.5F);
		scarf.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.9076F, 0.0524F, -0.4538F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 24, 19, 0.0F, 0.0F, 0.0F, 6, 2, 2, 0.0F, false));
		
		bipedHead = scarf;
		
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