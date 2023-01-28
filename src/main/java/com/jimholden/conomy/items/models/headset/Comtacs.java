package com.jimholden.conomy.items.models.headset;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class Comtacs extends ModelBiped {
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
	private final ModelRenderer bone;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer cube_r19;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer bone2;
	private final ModelRenderer cube_r22;
	private final ModelRenderer cube_r23;
	private final ModelRenderer cube_r24;
	private final ModelRenderer cube_r25;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;

	public Comtacs() {
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
		cube_r1.setRotationPoint(3.8F, -1.5F, -5.4F);
		headset.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.3665F, 1.0123F, 1.2392F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 27, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(5.2F, -1.8F, -4.3F);
		headset.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.9425F, 0.6458F, 0.6283F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 40, 16, -1.0F, 0.0F, 0.0F, 1, 2, 1, -0.2F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(5.35F, -3.2F, -1.9F);
		headset.addChild(cube_r3);
		setRotationAngle(cube_r3, -1.0472F, 0.0349F, 0.1745F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 16, -1.0F, 0.0F, 0.0F, 1, 3, 1, -0.2F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-5.15F, -5.35F, -1.0F);
		headset.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, 0.2618F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 3, 0.1114F, -3.0871F, -0.001F, 1, 3, 2, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(4.95F, -5.35F, -1.0F);
		headset.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, -0.2618F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 24, 19, -0.8886F, -3.0871F, -0.001F, 1, 3, 2, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-3.95F, -3.95F, 1.9F);
		headset.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, -0.733F, -0.1222F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 32, -1.0F, -2.0F, 0.0F, 1, 2, 1, -0.3F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(5.4F, -3.75F, 1.9F);
		headset.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, -0.733F, 0.1222F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 20, 38, -1.0F, -2.0F, 0.0F, 1, 2, 1, -0.3F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-5.6F, -5.5F, -2.45F);
		headset.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.9076F, 1.5882F, -0.1222F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 24, 0, -5.0F, -1.0F, 0.0F, 5, 1, 1, -0.3F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(5.5F, -5.5F, 2.55F);
		headset.addChild(cube_r9);
		setRotationAngle(cube_r9, -0.9076F, -1.5882F, 0.1222F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 24, 2, -5.0F, -1.0F, 0.0F, 5, 1, 1, -0.3F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-3.95F, -3.95F, -1.9F);
		headset.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, -0.733F, -0.1222F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 24, 24, -1.0F, -2.0F, 0.0F, 1, 2, 1, -0.3F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(5.4F, -3.75F, -1.9F);
		headset.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0F, -0.733F, 0.1222F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 23, 40, -1.0F, -2.0F, 0.0F, 1, 2, 1, -0.3F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-4.9F, -3.4F, -0.3F);
		headset.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.9076F, 0.0F, 0.2793F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 4, 3, 0.0F, -1.0F, 0.0F, 1, 1, 1, -0.1F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(4.9F, -3.4F, -0.3F);
		headset.addChild(cube_r13);
		setRotationAngle(cube_r13, -0.9076F, 0.0F, -0.2793F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 40, 25, -1.0F, -1.0F, 0.0F, 1, 1, 1, -0.1F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(-5.71F, -3.83F, -1.0F);
		headset.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.0F, 0.4189F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 18, 51, 0.0F, -1.0F, 0.0F, 1, 1, 2, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(5.71F, -3.83F, -1.0F);
		headset.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0F, 0.0F, -0.4189F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 18, 46, -1.0F, -1.0F, 0.0F, 1, 1, 2, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(5.25F, -2.25F, 1.85F);
		headset.addChild(bone);
		

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0F, -0.733F, 0.1222F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 27, 40, -1.0F, -2.0F, 0.0F, 1, 2, 1, -0.2F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(0.0F, 0.0F, -3.75F);
		bone.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.0F, -0.733F, 0.1222F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 31, 40, -1.0F, -2.0F, 0.0F, 1, 2, 1, -0.2F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(-0.25F, 0.5F, -1.1F);
		bone.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.0F, 0.0F, 0.1222F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 18, 32, -2.0F, -2.0F, 0.0F, 2, 2, 1, 0.0F, false));
		cube_r18.cubeList.add(new ModelBox(cube_r18, 16, 35, -2.0F, -2.0F, -2.5F, 2, 2, 1, 0.0F, false));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(0.0F, -1.5F, -0.1F);
		bone.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.8378F, 0.0F, 0.1222F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 36, 19, -2.005F, -1.0F, -1.0F, 2, 1, 1, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(0.0F, -1.5F, -3.6F);
		bone.addChild(cube_r20);
		setRotationAngle(cube_r20, -0.8378F, 0.0F, 0.1222F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 36, 23, -2.005F, -1.0F, 0.0F, 2, 1, 1, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(-0.04F, -1.18F, -2.85F);
		bone.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.0F, 0.0F, 0.1222F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 24, 4, -2.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-2.85F, -2.6F, 1.85F);
		headset.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, -0.2793F);
		

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone2.addChild(cube_r22);
		setRotationAngle(cube_r22, 0.0F, -0.733F, 0.1222F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 36, 25, -1.6355F, -2.0342F, 0.5722F, 1, 2, 1, -0.2F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(0.0F, 0.0F, -3.75F);
		bone2.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.0F, -0.733F, 0.1222F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 16, 38, -1.6355F, -2.0342F, 0.5722F, 1, 2, 1, -0.2F, false));

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(-0.25F, 0.5F, -1.1F);
		bone2.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.0F, 0.0F, 0.1222F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 30, 19, -2.0F, -2.0F, 0.0F, 2, 2, 1, 0.0F, false));
		cube_r24.cubeList.add(new ModelBox(cube_r24, 12, 32, -2.0F, -2.0F, -2.5F, 2, 2, 1, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(0.0F, -1.5F, -0.1F);
		bone2.addChild(cube_r25);
		setRotationAngle(cube_r25, 0.8378F, 0.0F, 0.1222F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 30, 22, -2.0F, -1.0F, -1.0F, 2, 1, 1, 0.0F, false));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(0.0F, -1.5F, -3.6F);
		bone2.addChild(cube_r26);
		setRotationAngle(cube_r26, -0.8378F, 0.0F, 0.1222F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 35, 21, -2.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F, false));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(-0.04F, -1.18F, -2.85F);
		bone2.addChild(cube_r27);
		setRotationAngle(cube_r27, 0.0F, 0.0F, 0.1222F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 0, 0, -2.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F, false));
		
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