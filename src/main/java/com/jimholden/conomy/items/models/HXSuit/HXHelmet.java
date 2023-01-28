package com.jimholden.conomy.items.models.HXSuit;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class HXHelmet extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer hx_leftarm;
	private final ModelRenderer hx_rightarm;
	private final ModelRenderer hx_torso;
	private final ModelRenderer hx_suit_nothelmet;
	private final ModelRenderer hx_leftleg;
	private final ModelRenderer hx_rightleg;
	private final ModelRenderer hx_helmet_actual;
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
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer cube_r22;
	private final ModelRenderer cube_r23;
	private final ModelRenderer helmet_r1;
	private final ModelRenderer helmet_r2;
	private final ModelRenderer helmet_r3;
	private final ModelRenderer helmet_r4;
	private final ModelRenderer helmet_r5;
	private final ModelRenderer helmet_r6;
	private final ModelRenderer helmet_r7;
	private final ModelRenderer helmet_r8;
	private final ModelRenderer helmet_r9;
	private final ModelRenderer helmet_r10;
	private final ModelRenderer helmet_r11;
	private final ModelRenderer helmet_r12;
	private final ModelRenderer helmet_r13;
	private final ModelRenderer helmet_r14;
	private final ModelRenderer helmet_r15;
	private final ModelRenderer helmet_r16;
	private final ModelRenderer helmet_r17;
	private final ModelRenderer helmet_r18;
	private final ModelRenderer helmet_r19;
	private final ModelRenderer helmet_r20;
	private final ModelRenderer helmet_r21;
	private final ModelRenderer helmet_r22;
	private final ModelRenderer helmet_r23;
	private final ModelRenderer helmet_r24;
	private final ModelRenderer helmet_r25;
	private final ModelRenderer helmet_r26;
	private final ModelRenderer helmet_r27;
	private final ModelRenderer helmet_r28;
	private final ModelRenderer helmet_r29;
	private final ModelRenderer helmet_r30;
	private final ModelRenderer helmet_r31;
	private final ModelRenderer helmet_r32;
	private final ModelRenderer helmet_r33;
	private final ModelRenderer helmet_r34;
	private final ModelRenderer helmet_r35;

	public HXHelmet() {
		textureWidth = 128;
		textureHeight = 128;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		

		hx_leftarm = new ModelRenderer(this);
		hx_leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		

		hx_rightarm = new ModelRenderer(this);
		hx_rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		

		hx_torso = new ModelRenderer(this);
		hx_torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		hx_suit_nothelmet = new ModelRenderer(this);
		hx_suit_nothelmet.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		hx_leftleg = new ModelRenderer(this);
		hx_leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		

		hx_rightleg = new ModelRenderer(this);
		hx_rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		

		hx_helmet_actual = new ModelRenderer(this);
		hx_helmet_actual.setRotationPoint(0.0F, 0.0F, 0.0F);
		
		
		hx_helmet_actual.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(1.0F, -7.25F, 3.75F);
		hx_helmet_actual.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.2094F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 48, 9, -2.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, -3.0F, -6.1F);
		hx_helmet_actual.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.1745F, 0.2269F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 4, 73, -4.5F, -4.0F, 0.0F, 1, 4, 1, 0.0F, false));
		cube_r2.cubeList.add(new ModelBox(cube_r2, 56, 31, -4.0F, -4.0F, 0.0F, 4, 4, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, -3.0F, -6.1F);
		hx_helmet_actual.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.1745F, -0.2269F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 7, 48, 3.5F, -4.0F, 0.0F, 1, 4, 1, 0.0F, false));
		cube_r3.cubeList.add(new ModelBox(cube_r3, 58, 58, 0.0F, -4.0F, 0.0F, 4, 4, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, -6.95F, -5.4F);
		hx_helmet_actual.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.5236F, 0.2269F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 65, -4.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, -7.85F, -4.9F);
		hx_helmet_actual.addChild(cube_r5);
		setRotationAngle(cube_r5, -1.0996F, 0.2269F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 47, 61, -4.0F, -2.0F, 0.02F, 4, 2, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, -7.85F, -4.9F);
		hx_helmet_actual.addChild(cube_r6);
		setRotationAngle(cube_r6, -1.0996F, -0.2269F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 61, 50, 0.0F, -2.0F, 0.02F, 4, 2, 1, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, -6.95F, -5.4F);
		hx_helmet_actual.addChild(cube_r7);
		setRotationAngle(cube_r7, -0.5236F, -0.2269F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 65, 53, 0.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, -3.0F, -6.0F);
		hx_helmet_actual.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.5411F, -0.2266F, -0.0093F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 32, 3.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));
		cube_r8.cubeList.add(new ModelBox(cube_r8, 60, 25, 0.0F, 0.0F, 0.0F, 4, 3, 1, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, -3.0F, -6.0F);
		hx_helmet_actual.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.5411F, 0.2266F, 0.0093F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 36, 16, -4.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));
		cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 61, -4.0F, 0.0F, 0.0F, 4, 3, 1, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(4.6F, -3.0F, -4.6F);
		hx_helmet_actual.addChild(cube_r10);
		setRotationAngle(cube_r10, -0.1047F, 0.1222F, -0.1047F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 48, -1.0F, -4.0F, 0.0F, 1, 4, 1, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(4.25F, -6.95F, -4.2F);
		hx_helmet_actual.addChild(cube_r11);
		setRotationAngle(cube_r11, -0.1222F, 0.1222F, -0.4363F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 48, 56, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-4.4F, -6.85F, -3.2F);
		hx_helmet_actual.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.3316F, -0.2269F, 0.4538F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 10, 55, 0.0F, -1.0F, 0.2F, 1, 1, 1, 0.0F, false));
		cube_r12.cubeList.add(new ModelBox(cube_r12, 20, 55, 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(4.4F, -6.85F, -3.2F);
		hx_helmet_actual.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.3316F, 0.2269F, -0.4538F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 38, 54, -1.0F, -1.0F, 0.2F, 1, 1, 1, 0.0F, false));
		cube_r13.cubeList.add(new ModelBox(cube_r13, 54, 50, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(-4.25F, -6.95F, -4.2F);
		hx_helmet_actual.addChild(cube_r14);
		setRotationAngle(cube_r14, -0.1222F, -0.1222F, 0.4363F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 60, 15, 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(-4.6F, -3.0F, -4.6F);
		hx_helmet_actual.addChild(cube_r15);
		setRotationAngle(cube_r15, -0.1047F, -0.1222F, 0.1047F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 73, 0.0F, -4.0F, 0.0F, 1, 4, 1, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(4.6F, -3.0F, -4.6F);
		hx_helmet_actual.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.4014F, 0.1222F, -0.1047F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 64, 77, -1.0F, -2.0F, 1.15F, 1, 2, 1, 0.0F, false));
		cube_r16.cubeList.add(new ModelBox(cube_r16, 33, 67, -1.0F, -3.0F, 2.0F, 1, 3, 2, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(-4.6F, -3.0F, -4.6F);
		hx_helmet_actual.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.4014F, -0.1222F, 0.1047F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 76, 77, 0.0F, -2.0F, 1.15F, 1, 2, 1, 0.0F, false));
		cube_r17.cubeList.add(new ModelBox(cube_r17, 67, 61, 0.0F, -3.0F, 2.0F, 1, 3, 2, 0.0F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(4.23F, -5.2F, -1.25F);
		hx_helmet_actual.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.4014F, 0.0F, -0.1571F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 66, 31, -0.45F, -2.25F, 0.0F, 1, 3, 2, 0.0F, false));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(4.45F, -7.3F, -2.15F);
		hx_helmet_actual.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.3665F, 0.1745F, -0.5411F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 22, 45, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(-4.45F, -7.3F, -2.15F);
		hx_helmet_actual.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.3665F, -0.1745F, 0.5411F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 32, 49, 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(-4.77F, -5.2F, -1.25F);
		hx_helmet_actual.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.4014F, 0.0F, 0.1571F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 39, 67, -0.01F, -2.3F, 0.0F, 1, 3, 2, 0.0F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(4.6F, -3.0F, -4.6F);
		hx_helmet_actual.addChild(cube_r22);
		setRotationAngle(cube_r22, 0.4189F, -0.0349F, 0.2443F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 48, 54, -1.0F, 0.0F, 0.0F, 1, 3, 4, 0.0F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(-4.6F, -3.0F, -4.6F);
		hx_helmet_actual.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.4189F, 0.0349F, -0.2443F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 54, 5, 0.0F, 0.0F, 0.0F, 1, 3, 4, 0.0F, false));

		helmet_r1 = new ModelRenderer(this);
		helmet_r1.setRotationPoint(-4.25F, -1.0F, -0.75F);
		hx_helmet_actual.addChild(helmet_r1);
		setRotationAngle(helmet_r1, 0.2094F, 0.0F, -0.2793F);
		helmet_r1.cubeList.add(new ModelBox(helmet_r1, 6, 68, 0.0F, -3.0F, 0.0F, 1, 3, 2, 0.0F, false));

		helmet_r2 = new ModelRenderer(this);
		helmet_r2.setRotationPoint(-5.05F, -3.8F, -1.35F);
		hx_helmet_actual.addChild(helmet_r2);
		setRotationAngle(helmet_r2, 0.1047F, -0.0349F, 0.0698F);
		helmet_r2.cubeList.add(new ModelBox(helmet_r2, 59, 63, 0.0F, -1.0F, 0.0F, 1, 1, 2, 0.0F, false));

		helmet_r3 = new ModelRenderer(this);
		helmet_r3.setRotationPoint(-4.95F, -4.8F, -1.45F);
		hx_helmet_actual.addChild(helmet_r3);
		setRotationAngle(helmet_r3, -0.3316F, 0.0F, 0.192F);
		helmet_r3.cubeList.add(new ModelBox(helmet_r3, 30, 43, 0.0F, -4.0F, 0.0F, 1, 4, 2, 0.0F, false));

		helmet_r4 = new ModelRenderer(this);
		helmet_r4.setRotationPoint(-4.65F, -6.65F, -0.55F);
		hx_helmet_actual.addChild(helmet_r4);
		setRotationAngle(helmet_r4, -0.6981F, 0.0F, 0.192F);
		helmet_r4.cubeList.add(new ModelBox(helmet_r4, 16, 55, 0.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F, false));

		helmet_r5 = new ModelRenderer(this);
		helmet_r5.setRotationPoint(4.05F, -9.05F, -1.65F);
		hx_helmet_actual.addChild(helmet_r5);
		setRotationAngle(helmet_r5, 1.9373F, 0.2618F, -0.4712F);
		helmet_r5.cubeList.add(new ModelBox(helmet_r5, 0, 16, -1.0F, -2.0F, -1.0F, 1, 3, 1, 0.0F, false));

		helmet_r6 = new ModelRenderer(this);
		helmet_r6.setRotationPoint(-4.05F, -9.05F, -1.65F);
		hx_helmet_actual.addChild(helmet_r6);
		setRotationAngle(helmet_r6, 1.9373F, -0.2618F, 0.4712F);
		helmet_r6.cubeList.add(new ModelBox(helmet_r6, 24, 24, 0.0F, -2.0F, -1.0F, 1, 3, 1, 0.0F, false));

		helmet_r7 = new ModelRenderer(this);
		helmet_r7.setRotationPoint(4.65F, -6.65F, -0.55F);
		hx_helmet_actual.addChild(helmet_r7);
		setRotationAngle(helmet_r7, -0.6981F, 0.0F, -0.192F);
		helmet_r7.cubeList.add(new ModelBox(helmet_r7, 68, 57, -1.0F, -2.0F, -2.0F, 1, 2, 2, 0.0F, false));

		helmet_r8 = new ModelRenderer(this);
		helmet_r8.setRotationPoint(4.95F, -4.8F, -1.45F);
		hx_helmet_actual.addChild(helmet_r8);
		setRotationAngle(helmet_r8, -0.3316F, 0.0F, -0.192F);
		helmet_r8.cubeList.add(new ModelBox(helmet_r8, 10, 64, -1.0F, -4.0F, 0.0F, 1, 4, 2, 0.0F, false));

		helmet_r9 = new ModelRenderer(this);
		helmet_r9.setRotationPoint(-5.05F, -4.0F, 0.55F);
		hx_helmet_actual.addChild(helmet_r9);
		setRotationAngle(helmet_r9, 0.4189F, -0.0349F, 0.2094F);
		helmet_r9.cubeList.add(new ModelBox(helmet_r9, 38, 52, 0.0F, -3.0F, 0.0F, 1, 3, 4, 0.0F, false));

		helmet_r10 = new ModelRenderer(this);
		helmet_r10.setRotationPoint(4.55F, -5.5F, 4.25F);
		hx_helmet_actual.addChild(helmet_r10);
		setRotationAngle(helmet_r10, 0.2269F, 0.2269F, 0.2618F);
		helmet_r10.cubeList.add(new ModelBox(helmet_r10, 44, 64, -4.0F, -1.0F, -1.0F, 4, 1, 1, 0.0F, false));

		helmet_r11 = new ModelRenderer(this);
		helmet_r11.setRotationPoint(-4.55F, -5.5F, 4.25F);
		hx_helmet_actual.addChild(helmet_r11);
		setRotationAngle(helmet_r11, 0.2269F, -0.2269F, -0.2618F);
		helmet_r11.cubeList.add(new ModelBox(helmet_r11, 64, 29, 0.0F, -1.0F, -1.0F, 4, 1, 1, 0.0F, false));

		helmet_r12 = new ModelRenderer(this);
		helmet_r12.setRotationPoint(0.5F, -7.05F, 5.15F);
		hx_helmet_actual.addChild(helmet_r12);
		setRotationAngle(helmet_r12, 0.1396F, 0.0F, 0.0F);
		helmet_r12.cubeList.add(new ModelBox(helmet_r12, 28, 42, -1.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));

		helmet_r13 = new ModelRenderer(this);
		helmet_r13.setRotationPoint(-4.65F, -6.6F, 4.05F);
		hx_helmet_actual.addChild(helmet_r13);
		setRotationAngle(helmet_r13, 0.1222F, -0.2793F, 0.0349F);
		helmet_r13.cubeList.add(new ModelBox(helmet_r13, 64, 9, 0.25F, -1.6F, -1.03F, 4, 1, 1, 0.0F, false));

		helmet_r14 = new ModelRenderer(this);
		helmet_r14.setRotationPoint(-3.95F, -8.95F, 2.35F);
		hx_helmet_actual.addChild(helmet_r14);
		setRotationAngle(helmet_r14, 0.3316F, -0.0349F, 0.2443F);
		helmet_r14.cubeList.add(new ModelBox(helmet_r14, 0, 55, 0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F, false));

		helmet_r15 = new ModelRenderer(this);
		helmet_r15.setRotationPoint(3.95F, -8.95F, 2.35F);
		hx_helmet_actual.addChild(helmet_r15);
		setRotationAngle(helmet_r15, 0.3316F, 0.0349F, -0.2443F);
		helmet_r15.cubeList.add(new ModelBox(helmet_r15, 10, 55, -1.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F, false));

		helmet_r16 = new ModelRenderer(this);
		helmet_r16.setRotationPoint(3.95F, -8.95F, 2.35F);
		hx_helmet_actual.addChild(helmet_r16);
		setRotationAngle(helmet_r16, 0.0F, 0.0349F, 0.1571F);
		helmet_r16.cubeList.add(new ModelBox(helmet_r16, 24, 16, -4.0F, 0.0F, -4.0F, 4, 1, 4, 0.0F, false));

		helmet_r17 = new ModelRenderer(this);
		helmet_r17.setRotationPoint(3.65F, -9.05F, -1.65F);
		hx_helmet_actual.addChild(helmet_r17);
		setRotationAngle(helmet_r17, 0.4189F, -0.2967F, 0.1396F);
		helmet_r17.cubeList.add(new ModelBox(helmet_r17, 24, 21, -4.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F, false));

		helmet_r18 = new ModelRenderer(this);
		helmet_r18.setRotationPoint(-3.65F, -9.05F, -1.65F);
		hx_helmet_actual.addChild(helmet_r18);
		setRotationAngle(helmet_r18, 0.4189F, 0.2967F, -0.1396F);
		helmet_r18.cubeList.add(new ModelBox(helmet_r18, 54, 0, 0.0F, 0.0F, -1.0F, 4, 1, 2, 0.0F, false));

		helmet_r19 = new ModelRenderer(this);
		helmet_r19.setRotationPoint(-3.95F, -8.95F, 2.35F);
		hx_helmet_actual.addChild(helmet_r19);
		setRotationAngle(helmet_r19, 0.0F, -0.0349F, -0.1571F);
		helmet_r19.cubeList.add(new ModelBox(helmet_r19, 36, 17, 0.0F, 0.0F, -4.0F, 4, 1, 4, 0.0F, false));

		helmet_r20 = new ModelRenderer(this);
		helmet_r20.setRotationPoint(-4.05F, -8.2F, 4.05F);
		hx_helmet_actual.addChild(helmet_r20);
		setRotationAngle(helmet_r20, 1.2043F, -0.1396F, -0.1571F);
		helmet_r20.cubeList.add(new ModelBox(helmet_r20, 60, 6, 0.0F, -2.0F, -1.0F, 4, 2, 1, 0.0F, false));

		helmet_r21 = new ModelRenderer(this);
		helmet_r21.setRotationPoint(4.05F, -8.2F, 4.05F);
		hx_helmet_actual.addChild(helmet_r21);
		setRotationAngle(helmet_r21, 1.2043F, 0.1396F, 0.1571F);
		helmet_r21.cubeList.add(new ModelBox(helmet_r21, 10, 61, -4.0F, -2.0F, -1.0F, 4, 2, 1, 0.0F, false));

		helmet_r22 = new ModelRenderer(this);
		helmet_r22.setRotationPoint(4.65F, -6.6F, 4.05F);
		hx_helmet_actual.addChild(helmet_r22);
		setRotationAngle(helmet_r22, 0.1222F, 0.2793F, -0.0349F);
		helmet_r22.cubeList.add(new ModelBox(helmet_r22, 64, 0, -4.25F, -1.6F, -1.03F, 4, 1, 1, 0.0F, false));

		helmet_r23 = new ModelRenderer(this);
		helmet_r23.setRotationPoint(-4.65F, -6.4F, 4.05F);
		hx_helmet_actual.addChild(helmet_r23);
		setRotationAngle(helmet_r23, 0.1222F, -0.2793F, 0.0524F);
		helmet_r23.cubeList.add(new ModelBox(helmet_r23, 34, 52, 0.0F, -1.0F, -1.0F, 3, 1, 1, 0.0F, false));

		helmet_r24 = new ModelRenderer(this);
		helmet_r24.setRotationPoint(4.65F, -6.4F, 4.05F);
		hx_helmet_actual.addChild(helmet_r24);
		setRotationAngle(helmet_r24, 0.1222F, 0.2793F, -0.0524F);
		helmet_r24.cubeList.add(new ModelBox(helmet_r24, 28, 40, -3.0F, -1.0F, -1.0F, 3, 1, 1, 0.0F, false));

		helmet_r25 = new ModelRenderer(this);
		helmet_r25.setRotationPoint(-5.65F, -5.0F, 1.55F);
		hx_helmet_actual.addChild(helmet_r25);
		setRotationAngle(helmet_r25, 0.3491F, 0.4014F, 0.3316F);
		helmet_r25.cubeList.add(new ModelBox(helmet_r25, 18, 45, 0.0F, -2.0F, 0.0F, 1, 2, 2, 0.0F, false));

		helmet_r26 = new ModelRenderer(this);
		helmet_r26.setRotationPoint(5.65F, -5.0F, 1.55F);
		hx_helmet_actual.addChild(helmet_r26);
		setRotationAngle(helmet_r26, 0.3491F, -0.4014F, -0.3316F);
		helmet_r26.cubeList.add(new ModelBox(helmet_r26, 6, 55, -1.0F, -2.0F, 0.0F, 1, 2, 2, 0.0F, false));

		helmet_r27 = new ModelRenderer(this);
		helmet_r27.setRotationPoint(5.05F, -4.0F, 0.55F);
		hx_helmet_actual.addChild(helmet_r27);
		setRotationAngle(helmet_r27, 0.4189F, 0.0349F, -0.2094F);
		helmet_r27.cubeList.add(new ModelBox(helmet_r27, 52, 40, -1.0F, -3.0F, 0.0F, 1, 3, 4, 0.0F, false));

		helmet_r28 = new ModelRenderer(this);
		helmet_r28.setRotationPoint(5.05F, -3.8F, -1.35F);
		hx_helmet_actual.addChild(helmet_r28);
		setRotationAngle(helmet_r28, 0.1047F, 0.0349F, -0.0698F);
		helmet_r28.cubeList.add(new ModelBox(helmet_r28, 72, 33, -1.0F, -1.0F, 0.0F, 1, 1, 2, 0.0F, false));

		helmet_r29 = new ModelRenderer(this);
		helmet_r29.setRotationPoint(-4.25F, -2.1F, 0.85F);
		hx_helmet_actual.addChild(helmet_r29);
		setRotationAngle(helmet_r29, 0.4014F, 0.1745F, -0.3142F);
		helmet_r29.cubeList.add(new ModelBox(helmet_r29, 55, 50, 0.0F, -2.0F, -0.15F, 1, 2, 4, 0.0F, false));

		helmet_r30 = new ModelRenderer(this);
		helmet_r30.setRotationPoint(-4.05F, -3.7F, 4.3F);
		hx_helmet_actual.addChild(helmet_r30);
		setRotationAngle(helmet_r30, 0.1571F, -0.1222F, -0.4538F);
		helmet_r30.cubeList.add(new ModelBox(helmet_r30, 64, 43, 0.0F, -1.0F, -1.0F, 4, 1, 1, 0.0F, false));

		helmet_r31 = new ModelRenderer(this);
		helmet_r31.setRotationPoint(-4.75F, -5.5F, 4.0F);
		hx_helmet_actual.addChild(helmet_r31);
		setRotationAngle(helmet_r31, 0.1222F, -0.1536F, -0.2443F);
		helmet_r31.cubeList.add(new ModelBox(helmet_r31, 64, 36, 0.0F, 0.0F, -0.98F, 4, 1, 1, 0.0F, false));

		helmet_r32 = new ModelRenderer(this);
		helmet_r32.setRotationPoint(4.75F, -5.5F, 4.0F);
		hx_helmet_actual.addChild(helmet_r32);
		setRotationAngle(helmet_r32, 0.1222F, 0.1536F, 0.2443F);
		helmet_r32.cubeList.add(new ModelBox(helmet_r32, 64, 38, -4.0F, 0.0F, -0.98F, 4, 1, 1, 0.0F, false));

		helmet_r33 = new ModelRenderer(this);
		helmet_r33.setRotationPoint(4.05F, -3.7F, 4.3F);
		hx_helmet_actual.addChild(helmet_r33);
		setRotationAngle(helmet_r33, 0.1571F, 0.1222F, 0.4538F);
		helmet_r33.cubeList.add(new ModelBox(helmet_r33, 64, 45, -4.0F, -1.0F, -1.0F, 4, 1, 1, 0.0F, false));

		helmet_r34 = new ModelRenderer(this);
		helmet_r34.setRotationPoint(4.25F, -2.1F, 0.85F);
		hx_helmet_actual.addChild(helmet_r34);
		setRotationAngle(helmet_r34, 0.4014F, -0.1745F, 0.3142F);
		helmet_r34.cubeList.add(new ModelBox(helmet_r34, 20, 56, -1.0F, -2.0F, -0.15F, 1, 2, 4, 0.0F, false));

		helmet_r35 = new ModelRenderer(this);
		helmet_r35.setRotationPoint(4.25F, -1.0F, -0.75F);
		hx_helmet_actual.addChild(helmet_r35);
		setRotationAngle(helmet_r35, 0.2094F, 0.0F, 0.2793F);
		helmet_r35.cubeList.add(new ModelBox(helmet_r35, 45, 68, -1.0F, -3.0F, 0.0F, 1, 3, 2, 0.0F, false));
		//hx_helmet_actual.addChild(head);
		bipedBody.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedHeadwear.isHidden = true;
		bipedHead = hx_helmet_actual;
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