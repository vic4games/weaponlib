package com.jimholden.conomy.items.models.helmet;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;



public class AtlynHelmet extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer altyn;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;

	public AtlynHelmet() {
		textureWidth = 128;
		textureHeight = 128;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 25, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightarm.cubeList.add(new ModelBox(rightarm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftarm.cubeList.add(new ModelBox(leftarm, 36, 37, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		rightleg.cubeList.add(new ModelBox(rightleg, 32, 0, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		leftleg.cubeList.add(new ModelBox(leftleg, 24, 25, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		altyn = new ModelRenderer(this);
		altyn.setRotationPoint(0.0F, 0.0F, 0.0F);
		altyn.cubeList.add(new ModelBox(altyn, 50, 26, -5.0F, -7.0F, -2.0F, 1, 5, 6, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 24, 16, -5.0F, -7.0F, -3.0F, 1, 4, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 24, 0, 4.0F, -7.0F, -3.0F, 1, 4, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 28, 0, -5.25F, -5.0F, -3.0F, 1, 2, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 24, 25, 4.25F, -5.0F, -3.0F, 1, 2, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 28, 3, -5.0F, -7.0F, -4.0F, 1, 2, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 0, 25, 4.0F, -7.0F, -4.0F, 1, 2, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 46, 47, 4.0F, -7.0F, -2.0F, 1, 5, 6, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 0, 51, -4.0F, -7.0F, -5.0F, 8, 2, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 52, 18, -4.0F, -5.975F, -5.25F, 8, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 48, 0, -4.5F, -7.0F, -6.675F, 9, 6, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 18, 51, -3.5F, -5.5F, -7.175F, 7, 3, 1, 0.3F, false));
		altyn.cubeList.add(new ModelBox(altyn, 52, 16, -4.0F, -0.75F, -4.25F, 8, 1, 1, 0.01F, false));
		altyn.cubeList.add(new ModelBox(altyn, 32, 18, -5.0F, -6.0F, -5.0F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 4, 22, 4.0F, -6.0F, -5.0F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 24, 5, -4.75F, -7.0F, -4.75F, 1, 2, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 20, 25, 3.75F, -7.0F, -4.75F, 1, 2, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 48, 7, -4.0F, -7.0F, 4.0F, 8, 5, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 52, 40, -4.0F, -2.975F, 4.25F, 8, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 4, 16, -4.75F, -7.0F, 3.75F, 1, 5, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 0, 16, 3.75F, -7.0F, 3.75F, 1, 5, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 32, 16, -5.0F, -2.975F, 4.0F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 31, 2, 4.0F, -2.975F, 4.0F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 32, 20, -4.5F, -8.0F, 3.5F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 28, 6, 3.5F, -8.0F, 3.5F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 32, 0, -4.5F, -8.0F, -4.5F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 0, 22, 3.5F, -8.0F, -4.5F, 1, 1, 1, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 0, 16, -4.0F, -9.0F, -4.0F, 8, 1, 8, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 10, 41, -5.25F, -2.975F, -2.0F, 1, 1, 6, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 24, 16, 4.25F, -2.975F, -2.0F, 1, 1, 6, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 0, 54, 4.2F, -7.0F, -6.0F, 1, 6, 3, 0.0F, false));
		altyn.cubeList.add(new ModelBox(altyn, 31, 52, -5.25F, -7.0F, -6.0F, 1, 6, 3, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-3.8358F, -5.9F, -6.0F);
		altyn.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, -0.7854F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 4, 0, -0.9642F, -1.1F, 0.0F, 1, 6, 1, 0.01F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(5.1642F, -5.9F, -6.0F);
		altyn.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -0.7854F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -0.9642F, -1.1F, 0.0F, 1, 6, 1, 0.01F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(4.25F, -3.4F, 0.0F);
		altyn.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.7854F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 40, 32, -1.0F, -1.1F, -5.0F, 1, 1, 4, 0.0F, false));
		cube_r3.cubeList.add(new ModelBox(cube_r3, 52, 42, -8.5F, -1.1F, -5.0F, 1, 1, 4, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-3.0F, -6.7872F, 9.5127F);
		altyn.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.4363F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 48, 13, -1.0F, -4.1F, -5.0F, 8, 2, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-3.0F, -2.9837F, -1.356F);
		altyn.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.4363F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 48, 37, -1.0F, -4.1F, -5.0F, 8, 2, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(5.8875F, -5.0968F, 3.0F);
		altyn.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.4363F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 41, -1.0F, -4.1F, -7.0F, 1, 2, 8, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-4.9812F, -4.6741F, 3.0F);
		altyn.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.0F, 0.4363F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 18, 41, -1.0F, -4.1F, -7.0F, 1, 2, 8, 0.0F, false));
		
		
		bipedBody.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedHeadwear.isHidden = true;
		bipedHead = altyn;
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