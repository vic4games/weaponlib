package com.jimholden.conomy.items.models.headset;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class Plantronics800HD extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer headset;
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

	public Plantronics800HD() {
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

		headset = new ModelRenderer(this);
		headset.setRotationPoint(0.0F, 0.0F, 0.0F);
		headset.cubeList.add(new ModelBox(headset, 20, 16, -4.0F, -8.5F, -1.0F, 8, 1, 2, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(4.9F, -1.56F, -3.6F);
		headset.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.9425F, 0.2269F, 0.2443F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 28, -1.0F, 0.0F, 0.0F, 1, 2, 1, -0.2F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(4.92F, -1.56F, -3.55F);
		headset.addChild(cube_r2);
		setRotationAngle(cube_r2, -1.117F, 0.2269F, 0.2443F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 40, 31, -1.0F, 0.0F, 0.0F, 1, 2, 1, -0.2F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(5.2F, -2.7F, -1.15F);
		headset.addChild(cube_r3);
		setRotationAngle(cube_r3, -1.117F, 0.0349F, 0.1745F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 40, 16, -1.0F, 0.0F, 0.0F, 1, 3, 1, -0.2F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-4.95F, -6.6F, -1.0F);
		headset.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, 0.4538F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 16, 38, 0.0F, -2.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-5.35F, -6.6F, 0.4F);
		headset.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, 0.1571F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 24, 24, 0.2F, -0.28F, -0.2F, 1, 3, 1, -0.2F, false));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 12, 32, 0.201F, 0.72F, -1.3F, 1, 1, 2, -0.2F, false));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 16, 0.2F, -0.28F, -1.6F, 1, 3, 1, -0.2F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(4.95F, -6.6F, 0.2F);
		headset.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.1571F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 26, 40, -0.801F, 0.8F, -1.2F, 1, 1, 2, -0.2F, false));
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 32, -0.8F, -0.2F, 0.0F, 1, 3, 1, -0.2F, false));
		cube_r6.cubeList.add(new ModelBox(cube_r6, 32, 40, -0.8F, -0.2F, -1.4F, 1, 3, 1, -0.2F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-5.35F, -4.05F, -1.0F);
		headset.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.0F, -0.0698F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 36, 19, 0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-5.35F, -3.85F, 1.8F);
		headset.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.5061F, 0.0F, -0.0698F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 31, 0, -0.15F, -0.2F, -0.8F, 1, 2, 1, -0.2F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-5.35F, -4.05F, 1.8F);
		headset.addChild(cube_r9);
		setRotationAngle(cube_r9, -1.2566F, -0.0349F, 0.1396F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 24, 4, -0.15F, -0.2F, -2.8F, 1, 1, 3, -0.2F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(5.25F, -4.05F, 1.8F);
		headset.addChild(cube_r10);
		setRotationAngle(cube_r10, -1.2566F, -0.0349F, -0.1396F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 31, 20, -0.8F, -0.2F, -2.8F, 1, 1, 3, -0.2F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(5.25F, -3.85F, 1.8F);
		headset.addChild(cube_r11);
		setRotationAngle(cube_r11, -0.5061F, 0.0F, 0.2443F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 40, 23, -0.8F, -0.2F, -0.8F, 1, 2, 1, -0.2F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(5.35F, -4.05F, -1.0F);
		headset.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 0.0F, 0.2443F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 36, 24, -1.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(4.95F, -6.6F, -1.0F);
		headset.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, 0.0F, -0.4538F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 20, 40, -1.0F, -2.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(-5.0F, -4.5F, -1.5F);
		headset.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.0F, -0.8203F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 24, 0, 0.0F, 0.0F, 0.0F, 2, 1, 3, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(5.0F, -4.5F, -1.5F);
		headset.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0F, 0.0F, 0.8203F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 24, 19, -2.0F, 0.0F, 0.0F, 2, 1, 3, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-5.0F, -4.5F, -1.5F);
		headset.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0F, 0.0F, -0.1222F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 0, 0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(5.0F, -4.5F, -1.5F);
		headset.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.0F, 0.0F, 0.1222F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 16, 32, -1.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F, false));
		
		
		bipedHeadwear.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedBody.isHidden = true;
		
		bipedHead = headset;
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