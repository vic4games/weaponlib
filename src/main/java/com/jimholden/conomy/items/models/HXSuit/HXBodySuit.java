package com.jimholden.conomy.items.models.HXSuit;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class HXBodySuit extends ModelBiped {
	private final ModelRenderer head;
	//private final ModelRenderer body;
	//private final ModelRenderer rightarm;
	//private final ModelRenderer leftarm;
	//private final ModelRenderer rightleg;
	//private final ModelRenderer leftleg;
	private final ModelRenderer hx_leftarm;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer leftarm_r1;
	private final ModelRenderer hx_rightarm;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer rightarm_r1;
	private final ModelRenderer hx_torso;
	private final ModelRenderer body_r1;
	private final ModelRenderer body_r2;
	private final ModelRenderer body_r3;
	private final ModelRenderer body_r4;
	private final ModelRenderer body_r5;
	private final ModelRenderer body_r6;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer body_r7;
	private final ModelRenderer body_r8;
	private final ModelRenderer body_r9;
	private final ModelRenderer body_r10;
	private final ModelRenderer body_r11;
	private final ModelRenderer hx_suit_nothelmet;
	private final ModelRenderer cube_r18;
	private final ModelRenderer cube_r19;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer cube_r22;
	private final ModelRenderer cube_r23;
	private final ModelRenderer cube_r24;
	private final ModelRenderer cube_r25;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;
	private final ModelRenderer hx_leftleg;
	private final ModelRenderer hx_rightleg;
	//private final ModelRenderer hx_vest;

	public HXBodySuit() {
		textureWidth = 128;
		textureHeight = 128;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		
		/*
		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
		*/
		//rightarm = new ModelRenderer(this);
		//rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		//rightarm.cubeList.add(new ModelBox(rightarm, 36, 36, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		/*
		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftarm.cubeList.add(new ModelBox(leftarm, 32, 0, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false)); */

		
		/*
		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		rightleg.cubeList.add(new ModelBox(rightleg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false)); */

		
		/*
		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		leftleg.cubeList.add(new ModelBox(leftleg, 24, 24, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
		*/

		hx_leftarm = new ModelRenderer(this);
		hx_leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		hx_leftarm.cubeList.add(new ModelBox(hx_leftarm, 44, 12, -1.0F, 7.5F, -2.0F, 4, 1, 4, 0.15F, false));
		hx_leftarm.cubeList.add(new ModelBox(hx_leftarm, 16, 67, 2.2F, 3.4F, -1.0F, 1, 4, 2, 0.0F, false));
		hx_leftarm.cubeList.add(new ModelBox(hx_leftarm, 54, 17, 1.2F, 9.2F, -2.0F, 2, 1, 4, -0.1F, false));
		hx_leftarm.cubeList.add(new ModelBox(hx_leftarm, 22, 45, 2.0F, 0.25F, -2.0F, 1, 6, 4, 0.1F, false));
		hx_leftarm.cubeList.add(new ModelBox(hx_leftarm, 76, 24, 0.5F, 0.75F, -2.1F, 2, 1, 1, 0.1F, false));
		hx_leftarm.cubeList.add(new ModelBox(hx_leftarm, 55, 76, 0.5F, 2.75F, -2.1F, 2, 1, 1, 0.1F, false));
		hx_leftarm.cubeList.add(new ModelBox(hx_leftarm, 32, 0, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(3.1F, -2.05F, -1.5F);
		hx_leftarm.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.0873F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 12, 32, -3.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(3.3F, -0.05F, 1.5F);
		hx_leftarm.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.1745F, 0.0F, 0.0524F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 69, 74, -0.999F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(3.3F, -0.05F, -1.5F);
		hx_leftarm.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.1745F, 0.0F, 0.0524F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 73, 74, -0.999F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(3.3F, -0.05F, -1.0F);
		hx_leftarm.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, 0.0524F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 68, 18, -1.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(3.1F, -2.05F, -1.5F);
		hx_leftarm.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, -0.0873F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 62, 63, -1.0F, 0.0F, 0.0F, 1, 2, 3, 0.0F, false));

		leftarm_r1 = new ModelRenderer(this);
		leftarm_r1.setRotationPoint(1.75F, 1.0F, 2.2F);
		hx_leftarm.addChild(leftarm_r1);
		setRotationAngle(leftarm_r1, 0.0F, 0.0F, 0.1222F);
		leftarm_r1.cubeList.add(new ModelBox(leftarm_r1, 74, 36, -1.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));

		hx_rightarm = new ModelRenderer(this);
		hx_rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		hx_rightarm.cubeList.add(new ModelBox(hx_rightarm, 40, 27, -3.0F, 7.5F, -2.0F, 4, 1, 4, 0.15F, false));
		hx_rightarm.cubeList.add(new ModelBox(hx_rightarm, 12, 45, -3.0F, 0.25F, -2.0F, 1, 6, 4, 0.1F, false));
		hx_rightarm.cubeList.add(new ModelBox(hx_rightarm, 36, 76, -2.5F, 0.75F, -2.1F, 2, 1, 1, 0.1F, false));
		hx_rightarm.cubeList.add(new ModelBox(hx_rightarm, 30, 76, -2.5F, 2.75F, -2.1F, 2, 1, 1, 0.1F, false));
		hx_rightarm.cubeList.add(new ModelBox(hx_rightarm, 0, 67, -3.2F, 3.4F, -1.0F, 1, 4, 2, 0.0F, false));
		hx_rightarm.cubeList.add(new ModelBox(hx_rightarm, 52, 26, -3.2F, 9.2F, -2.0F, 2, 1, 4, -0.1F, false));
		hx_rightarm.cubeList.add(new ModelBox(hx_rightarm, 36, 36, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-3.3F, -0.05F, -1.0F);
		hx_rightarm.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.0524F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 68, 11, 0.0F, 0.0F, 0.0F, 1, 3, 2, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-3.35F, -0.05F, -1.5F);
		hx_rightarm.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.1745F, 0.0F, -0.0524F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 61, 73, 0.049F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-3.35F, -0.05F, 1.5F);
		hx_rightarm.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.1745F, 0.0F, -0.0524F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 65, 73, 0.049F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-3.1F, -2.05F, -1.5F);
		hx_rightarm.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 0.0F, 0.0873F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 54, 63, 0.0F, 0.0F, 0.0F, 1, 2, 3, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-3.1F, -2.05F, -1.5F);
		hx_rightarm.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, 0.0F, -0.0873F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 24, 0, 0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F, false));

		rightarm_r1 = new ModelRenderer(this);
		rightarm_r1.setRotationPoint(-1.75F, 1.0F, 2.2F);
		hx_rightarm.addChild(rightarm_r1);
		setRotationAngle(rightarm_r1, 0.0F, 0.0F, -0.1222F);
		rightarm_r1.cubeList.add(new ModelBox(rightarm_r1, 26, 74, 0.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));

		hx_torso = new ModelRenderer(this);
		hx_torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		hx_torso.cubeList.add(new ModelBox(hx_torso, 50, 32, 3.0F, 6.0F, -2.0F, 1, 4, 4, 0.1F, false));
		hx_torso.cubeList.add(new ModelBox(hx_torso, 75, 72, 1.4F, 6.25F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_torso.cubeList.add(new ModelBox(hx_torso, 74, 68, -3.4F, 6.25F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_torso.cubeList.add(new ModelBox(hx_torso, 75, 70, 1.4F, 8.25F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_torso.cubeList.add(new ModelBox(hx_torso, 75, 22, -3.4F, 8.25F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_torso.cubeList.add(new ModelBox(hx_torso, 48, 18, -4.0F, 6.0F, -2.0F, 1, 4, 4, 0.1F, false));
		hx_torso.cubeList.add(new ModelBox(hx_torso, 0, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		body_r1 = new ModelRenderer(this);
		body_r1.setRotationPoint(-3.5F, 2.75F, 2.4F);
		hx_torso.addChild(body_r1);
		setRotationAngle(body_r1, 0.0175F, -0.192F, 1.3439F);
		body_r1.cubeList.add(new ModelBox(body_r1, 72, 65, -2.0F, -2.0F, -1.0F, 2, 2, 1, 0.0F, false));

		body_r2 = new ModelRenderer(this);
		body_r2.setRotationPoint(3.45F, 8.25F, 2.2F);
		hx_torso.addChild(body_r2);
		setRotationAngle(body_r2, 0.0F, 0.0F, -0.3491F);
		body_r2.cubeList.add(new ModelBox(body_r2, 73, 63, -2.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F, false));

		body_r3 = new ModelRenderer(this);
		body_r3.setRotationPoint(-3.45F, 8.25F, 2.2F);
		hx_torso.addChild(body_r3);
		setRotationAngle(body_r3, 0.0F, 0.0F, 0.3491F);
		body_r3.cubeList.add(new ModelBox(body_r3, 74, 59, 0.0F, 0.0F, -1.0F, 2, 1, 1, 0.0F, false));

		body_r4 = new ModelRenderer(this);
		body_r4.setRotationPoint(-3.5F, 2.75F, 2.4F);
		hx_torso.addChild(body_r4);
		setRotationAngle(body_r4, 0.0175F, 0.0873F, 1.3439F);
		body_r4.cubeList.add(new ModelBox(body_r4, 56, 12, 0.0F, -2.0F, -1.0F, 5, 2, 1, 0.0F, false));

		body_r5 = new ModelRenderer(this);
		body_r5.setRotationPoint(3.5F, 2.75F, 2.4F);
		hx_torso.addChild(body_r5);
		setRotationAngle(body_r5, 0.0175F, 0.192F, -1.3439F);
		body_r5.cubeList.add(new ModelBox(body_r5, 55, 73, 0.0F, -2.0F, -1.0F, 2, 2, 1, 0.0F, false));

		body_r6 = new ModelRenderer(this);
		body_r6.setRotationPoint(3.5F, 2.75F, 2.4F);
		hx_torso.addChild(body_r6);
		setRotationAngle(body_r6, 0.0175F, -0.0873F, -1.3439F);
		body_r6.cubeList.add(new ModelBox(body_r6, 60, 3, -5.0F, -2.0F, -1.0F, 5, 2, 1, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(-0.5F, 1.75F, 2.35F);
		hx_torso.addChild(cube_r11);
		setRotationAngle(cube_r11, -0.1571F, 0.0F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 1, 81, 0.0F, 0.0F, -1.0F, 1, 2, 1, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-2.75F, 2.75F, 2.75F);
		hx_torso.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.1745F, 0.0F, -0.2269F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 36, 22, 0.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(2.75F, 2.75F, 2.75F);
		hx_torso.addChild(cube_r13);
		setRotationAngle(cube_r13, -0.1745F, 0.0F, 0.2269F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 16, 40, -1.0F, 0.0F, -1.0F, 1, 3, 1, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(1.5F, 1.5F, -2.5F);
		hx_torso.addChild(cube_r14);
		setRotationAngle(cube_r14, -0.5236F, 0.1571F, -0.5411F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 71, 50, 0.0F, -1.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(-1.5F, 1.5F, -2.5F);
		hx_torso.addChild(cube_r15);
		setRotationAngle(cube_r15, -0.5236F, -0.1571F, 0.5411F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 71, 61, -3.0F, -1.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(1.25F, 5.0F, -2.2F);
		hx_torso.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0F, 0.1745F, -1.1345F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 74, 0, 0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(-1.25F, 5.0F, -2.2F);
		hx_torso.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.0F, -0.1745F, 1.1345F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 20, 76, -2.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F, false));

		body_r7 = new ModelRenderer(this);
		body_r7.setRotationPoint(-1.0F, 4.25F, -2.3F);
		hx_torso.addChild(body_r7);
		setRotationAngle(body_r7, 0.0F, 0.0873F, -0.2269F);
		body_r7.cubeList.add(new ModelBox(body_r7, 72, 4, -3.0F, -1.0F, 0.0F, 3, 1, 1, 0.0F, false));

		body_r8 = new ModelRenderer(this);
		body_r8.setRotationPoint(1.0F, 4.25F, -2.3F);
		hx_torso.addChild(body_r8);
		setRotationAngle(body_r8, 0.0F, -0.0873F, 0.2269F);
		body_r8.cubeList.add(new ModelBox(body_r8, 72, 11, 0.0F, -1.0F, 0.0F, 3, 1, 1, 0.0F, false));

		body_r9 = new ModelRenderer(this);
		body_r9.setRotationPoint(-3.7F, 6.4F, 2.2F);
		hx_torso.addChild(body_r9);
		setRotationAngle(body_r9, 0.1396F, -0.3665F, 0.2094F);
		body_r9.cubeList.add(new ModelBox(body_r9, 12, 75, 0.0F, 0.0F, -1.0F, 1, 2, 1, 0.0F, false));

		body_r10 = new ModelRenderer(this);
		body_r10.setRotationPoint(1.5F, 8.3F, 2.9F);
		hx_torso.addChild(body_r10);
		setRotationAngle(body_r10, -0.2443F, 0.0F, 0.0F);
		body_r10.cubeList.add(new ModelBox(body_r10, 0, 5, -3.0F, 0.0F, -1.0F, 3, 2, 1, 0.0F, false));

		body_r11 = new ModelRenderer(this);
		body_r11.setRotationPoint(3.7F, 6.4F, 2.2F);
		hx_torso.addChild(body_r11);
		setRotationAngle(body_r11, 0.1396F, 0.3665F, -0.2094F);
		body_r11.cubeList.add(new ModelBox(body_r11, 60, 77, -1.0F, 0.0F, -1.0F, 1, 2, 1, 0.0F, false));

		hx_suit_nothelmet = new ModelRenderer(this);
		hx_suit_nothelmet.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(1.0F, -5.75F, 3.95F);
		hx_suit_nothelmet.addChild(cube_r18);
		setRotationAngle(cube_r18, -0.3142F, 0.0F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 73, 8, -2.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F, false));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(-4.45F, -0.25F, -2.35F);
		hx_suit_nothelmet.addChild(cube_r19);
		setRotationAngle(cube_r19, -1.117F, 0.2269F, -0.4363F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 74, 41, -0.4415F, -4.001F, -0.2346F, 1, 3, 1, 0.0F, false));
		cube_r19.cubeList.add(new ModelBox(cube_r19, 25, 66, 0.0F, -7.0F, 0.0F, 1, 7, 1, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(-4.3F, -3.65F, 4.35F);
		hx_suit_nothelmet.addChild(cube_r20);
		setRotationAngle(cube_r20, 1.2915F, -0.1396F, -0.4538F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 65, 55, 0.0F, -1.0F, -1.0F, 4, 1, 1, 0.0F, false));
		cube_r20.cubeList.add(new ModelBox(cube_r20, 70, 16, -0.4619F, -0.884F, -1.9018F, 3, 1, 1, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(4.3F, -3.65F, 4.35F);
		hx_suit_nothelmet.addChild(cube_r21);
		setRotationAngle(cube_r21, 1.2915F, 0.1396F, 0.4538F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 70, 27, -2.5381F, -0.884F, -1.9018F, 3, 1, 1, 0.0F, false));
		cube_r21.cubeList.add(new ModelBox(cube_r21, 43, 66, -4.0F, -1.0F, -1.0F, 4, 1, 1, 0.0F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(3.7F, -0.95F, -4.45F);
		hx_suit_nothelmet.addChild(cube_r22);
		setRotationAngle(cube_r22, 0.192F, 0.0F, -0.1222F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 70, 31, -3.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(-0.95F, -0.65F, -4.65F);
		hx_suit_nothelmet.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.3491F, 0.0F, 0.0F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 73, 46, 0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F, false));

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(-3.7F, -0.95F, -4.45F);
		hx_suit_nothelmet.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.192F, 0.0F, 0.1222F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 71, 2, 0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(-4.45F, -1.25F, -1.35F);
		hx_suit_nothelmet.addChild(cube_r25);
		setRotationAngle(cube_r25, 0.1571F, 0.0F, -0.3142F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 58, 36, 0.0F, 0.0F, -3.0F, 1, 1, 4, 0.0F, false));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(4.45F, -1.25F, -1.35F);
		hx_suit_nothelmet.addChild(cube_r26);
		setRotationAngle(cube_r26, 0.1571F, 0.0F, 0.3142F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 58, 43, -1.0F, 0.0F, -3.0F, 1, 1, 4, 0.0F, false));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(4.45F, -0.25F, -2.35F);
		hx_suit_nothelmet.addChild(cube_r27);
		setRotationAngle(cube_r27, -1.117F, -0.2269F, 0.4363F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 8, 75, -0.5585F, -4.001F, -0.2346F, 1, 3, 1, 0.0F, false));
		cube_r27.cubeList.add(new ModelBox(cube_r27, 29, 66, -1.0F, -7.0F, 0.0F, 1, 7, 1, 0.0F, false));

		hx_leftleg = new ModelRenderer(this);
		hx_leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		hx_leftleg.cubeList.add(new ModelBox(hx_leftleg, 28, 51, 1.0F, 1.0F, -2.0F, 1, 5, 4, 0.1F, false));
		hx_leftleg.cubeList.add(new ModelBox(hx_leftleg, 36, 59, -2.0F, 10.0F, -2.0F, 4, 1, 2, 0.1F, false));
		hx_leftleg.cubeList.add(new ModelBox(hx_leftleg, 16, 40, -2.0F, 11.0F, -2.4F, 4, 1, 4, 0.1F, false));
		hx_leftleg.cubeList.add(new ModelBox(hx_leftleg, 74, 20, -1.0F, 10.99F, 1.0F, 2, 1, 1, 0.1F, false));
		hx_leftleg.cubeList.add(new ModelBox(hx_leftleg, 74, 54, -0.6F, 4.75F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_leftleg.cubeList.add(new ModelBox(hx_leftleg, 74, 52, -0.6F, 2.75F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_leftleg.cubeList.add(new ModelBox(hx_leftleg, 24, 24, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.025F, false));

		hx_rightleg = new ModelRenderer(this);
		hx_rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		hx_rightleg.cubeList.add(new ModelBox(hx_rightleg, 48, 0, -2.0F, 1.0F, -2.0F, 1, 5, 4, 0.1F, false));
		hx_rightleg.cubeList.add(new ModelBox(hx_rightleg, 58, 22, -2.0F, 10.0F, -2.0F, 4, 1, 2, 0.1F, false));
		hx_rightleg.cubeList.add(new ModelBox(hx_rightleg, 74, 48, -1.4F, 2.75F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_rightleg.cubeList.add(new ModelBox(hx_rightleg, 74, 29, -1.4F, 4.75F, -2.2F, 2, 1, 1, 0.0F, false));
		hx_rightleg.cubeList.add(new ModelBox(hx_rightleg, 36, 22, -2.0F, 11.0F, -2.4F, 4, 1, 4, 0.1F, false));
		hx_rightleg.cubeList.add(new ModelBox(hx_rightleg, 74, 13, -1.0F, 10.99F, 1.0F, 2, 1, 1, 0.1F, false));
		hx_rightleg.cubeList.add(new ModelBox(hx_rightleg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.025F, false));
		
		/*
		hx_vest = new ModelRenderer(this);
		hx_vest.setRotationPoint(0.0F, 0.0F, -1.0F);
		*/
		


		//hx_rightarm.cubeList += rightarm.cubeList;
		
		head.addChild(hx_suit_nothelmet);
		
		bipedHeadwear.isHidden = true;
		
		bipedHead = head;
		bipedLeftLeg = hx_leftleg;
		bipedRightLeg = hx_rightleg;
		bipedBody = hx_torso;
		bipedLeftArm = hx_leftarm;
		bipedRightArm = hx_rightarm;
		
		
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		
		float scale = f5;
        GlStateManager.pushMatrix();

        if (this.isChild)
        {
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            this.bipedHead.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale+0.001F);
            this.bipedLeftLeg.render(scale);
            //this.bipedHeadwear.render(scale+0.001F);
        }
        else
        {
            if (entity.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.bipedHead.render(scale);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            //this.bipedHeadwear.render(scale);
        }

        GlStateManager.popMatrix();
		//super.render(entity, f, f1, f2, f3, f4, f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}