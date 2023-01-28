package com.jimholden.conomy.items.models.backpacks;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class OakleyMechanismBackpack extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer backpack;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer cube_r19;

	public OakleyMechanismBackpack() {
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

		backpack = new ModelRenderer(this);
		backpack.setRotationPoint(0.0F, 0.0F, 0.0F);
		backpack.cubeList.add(new ModelBox(backpack, 16, 40, -2.5F, 1.5F, 1.5F, 5, 9, 3, 0.0F, false));
		backpack.cubeList.add(new ModelBox(backpack, 24, 16, -4.0F, 9.75F, -2.0F, 8, 1, 4, 0.1F, false));
		backpack.cubeList.add(new ModelBox(backpack, 44, 12, -3.4F, -0.35F, -1.8F, 2, 2, 4, -0.1F, false));
		backpack.cubeList.add(new ModelBox(backpack, 24, 0, -2.0F, 2.15F, -2.6F, 4, 1, 1, -0.2F, false));
		backpack.cubeList.add(new ModelBox(backpack, 40, 25, 1.4F, -0.35F, -1.8F, 2, 2, 4, -0.1F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-3.3F, 6.1F, 4.0F);
		backpack.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.1571F, -0.0349F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, 0.0F, 0.0F, -2.0F, 1, 4, 2, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(3.3F, 6.1F, 4.0F);
		backpack.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -0.1571F, 0.0349F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 24, 2, -1.0F, 0.0F, -2.0F, 1, 4, 2, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-3.15F, 2.5F, 4.0F);
		backpack.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.1222F, 0.1047F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 12, 50, 0.0F, 0.0F, -2.0F, 1, 3, 2, -0.1F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(3.15F, 2.5F, 4.0F);
		backpack.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, -0.1222F, -0.1047F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 50, 50, -1.0F, 0.0F, -2.0F, 1, 3, 2, -0.1F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-2.75F, 1.5F, 3.9F);
		backpack.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.0524F, 0.1222F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 48, 0.0F, 0.0F, -2.0F, 1, 9, 2, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(2.75F, 1.5F, 3.9F);
		backpack.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.0524F, -0.1222F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 6, 48, -1.0F, 0.0F, -2.0F, 1, 9, 2, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-0.25F, 0.8F, 3.75F);
		backpack.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.7505F, 0.0F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 28, 2, -2.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F, false));
		cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 6, 0.5F, 0.0F, -1.0F, 2, 1, 1, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(2.25F, 0.8F, 3.75F);
		backpack.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.3665F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 20, 16, -2.0F, 0.0F, -2.0F, 2, 1, 2, 0.0F, false));
		cube_r8.cubeList.add(new ModelBox(cube_r8, 28, 21, -4.5F, 0.0F, -2.0F, 2, 1, 2, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(2.5F, 1.5F, 4.5F);
		backpack.addChild(cube_r9);
		setRotationAngle(cube_r9, -0.3316F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 36, 21, -5.0F, 0.0F, -3.0F, 5, 1, 3, 0.001F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-2.5F, 1.5F, 4.5F);
		backpack.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.1745F, -0.1047F, 0.0175F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 16, 1.6F, 0.001F, -0.999F, 1, 2, 1, 0.0F, false));
		cube_r10.cubeList.add(new ModelBox(cube_r10, 36, 25, 0.0F, 0.0F, -1.0F, 2, 2, 1, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(1.35F, 3.25F, 5.25F);
		backpack.addChild(cube_r11);
		setRotationAngle(cube_r11, -0.0873F, 0.192F, -0.1222F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 24, 21, 0.0F, 0.0F, -1.0F, 1, 6, 1, -0.1F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-1.35F, 3.25F, 5.25F);
		backpack.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.0873F, -0.192F, 0.1222F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 32, 40, -1.0F, 0.0F, -1.0F, 1, 6, 1, -0.1F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(2.5F, 1.5F, 4.5F);
		backpack.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.1745F, 0.1047F, -0.0175F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 32, -2.6F, 0.001F, -0.999F, 1, 2, 1, 0.0F, false));
		cube_r13.cubeList.add(new ModelBox(cube_r13, 16, 37, -2.0F, 0.0F, -1.0F, 2, 2, 1, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(2.0F, 6.5F, 5.25F);
		backpack.addChild(cube_r14);
		setRotationAngle(cube_r14, -0.0698F, 0.0F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 48, 0, -4.0F, 0.0F, -1.0F, 4, 4, 1, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(-1.5F, -0.25F, -2.2F);
		backpack.addChild(cube_r15);
		setRotationAngle(cube_r15, -0.0524F, 0.0F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 49, 18, -1.9F, -0.1F, -0.1F, 2, 4, 1, -0.1F, false));
		cube_r15.cubeList.add(new ModelBox(cube_r15, 49, 35, 2.9F, -0.1F, -0.1F, 2, 4, 1, -0.1F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-1.5F, 3.55F, -2.4F);
		backpack.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0698F, 0.0F, 0.2618F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 16, 32, -1.9F, -0.1F, -0.1F, 2, 4, 1, -0.1F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(-3.5F, 6.05F, -2.1F);
		backpack.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.0F, 0.0524F, -0.384F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 40, 31, -1.0F, 0.0F, 0.0F, 1, 1, 4, 0.1F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(3.5F, 6.05F, -2.1F);
		backpack.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.0F, -0.0524F, 0.384F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 28, 48, 0.0F, 0.0F, 0.0F, 1, 1, 4, 0.1F, false));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(1.5F, 3.55F, -2.4F);
		backpack.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.0698F, 0.0F, -0.2618F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 48, 5, -0.1F, -0.1F, -0.1F, 2, 4, 1, -0.1F, false));
		
		
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedBody = backpack;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedHead.isHidden = true;
		bipedHeadwear.isHidden = true;
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