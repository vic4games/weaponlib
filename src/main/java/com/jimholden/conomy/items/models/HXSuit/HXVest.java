package com.jimholden.conomy.items.models.HXSuit;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class HXVest extends ModelBiped {
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
	private final ModelRenderer hx_vest;
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
	private final ModelRenderer cube_r24;
	private final ModelRenderer cube_r25;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;
	private final ModelRenderer cube_r28;
	private final ModelRenderer cube_r29;
	private final ModelRenderer cube_r30;
	private final ModelRenderer cube_r31;
	private final ModelRenderer cube_r32;
	private final ModelRenderer cube_r33;
	private final ModelRenderer cube_r34;
	private final ModelRenderer cube_r35;
	private final ModelRenderer cube_r36;
	private final ModelRenderer cube_r37;
	private final ModelRenderer cube_r38;
	private final ModelRenderer cube_r39;
	private final ModelRenderer cube_r40;
	private final ModelRenderer cube_r41;
	private final ModelRenderer cube_r42;
	private final ModelRenderer cube_r43;
	private final ModelRenderer cube_r44;
	private final ModelRenderer cube_r45;
	private final ModelRenderer cube_r46;
	private final ModelRenderer hx_helmet_actual;

	public HXVest() {
		textureWidth = 128;
		textureHeight = 128;

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
		

		hx_vest = new ModelRenderer(this);
		hx_vest.setRotationPoint(0.0F, 0.0F, -1.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-0.85F, 7.9F, 5.0F);
		hx_vest.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, -0.5061F, 0.2967F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 54, 48, -4.0F, 0.0F, -1.0F, 4, 1, 1, -0.3F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-2.45F, 11.25F, 4.4F);
		hx_vest.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.3316F, -0.2443F, 0.9948F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 26, 64, -4.0F, -1.0F, -1.0F, 4, 1, 1, -0.3F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(2.45F, 11.25F, 4.4F);
		hx_vest.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.3316F, 0.2443F, -0.9948F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 58, 41, 0.0F, -1.0F, -1.0F, 4, 1, 1, -0.3F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.85F, 7.9F, 5.1F);
		hx_vest.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.5061F, -0.2967F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 54, 56, 0.0F, 0.0F, -1.0F, 4, 1, 1, -0.3F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(1.5F, 7.5F, 3.2F);
		hx_vest.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.1396F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 28, 60, -3.0F, 0.0F, 0.0F, 3, 2, 2, -0.2F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(2.5F, 9.2F, 3.1F);
		hx_vest.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.3491F, 0.0F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 40, 32, -5.0F, 0.0F, 0.0F, 5, 2, 2, -0.2F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.5F, 1.15F, 4.95F);
		hx_vest.addChild(cube_r7);
		setRotationAngle(cube_r7, -0.1484F, -0.2618F, 0.0349F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 51, 68, -1.0F, 0.0F, -2.0F, 1, 3, 2, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-0.5F, 1.15F, 4.95F);
		hx_vest.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.1484F, 0.2618F, -0.0349F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 57, 68, 0.0F, 0.0F, -2.0F, 1, 3, 2, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-1.5F, 1.15F, 4.95F);
		hx_vest.addChild(cube_r9);
		setRotationAngle(cube_r9, -1.1432F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 68, 66, 1.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-1.5F, 1.15F, 4.95F);
		hx_vest.addChild(cube_r10);
		setRotationAngle(cube_r10, -0.1396F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 63, 68, 1.0F, 0.0F, -2.0F, 1, 3, 2, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(-3.4F, 1.15F, 3.4F);
		hx_vest.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.3491F, -0.4014F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 16, 36, 0.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-3.5F, 0.65F, 4.05F);
		hx_vest.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.4538F, -0.3665F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 20, 16, 0.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(3.5F, 0.65F, 4.05F);
		hx_vest.addChild(cube_r13);
		setRotationAngle(cube_r13, -0.4538F, 0.3665F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 24, 4, -2.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(3.4F, 1.15F, 3.4F);
		hx_vest.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.3491F, 0.4014F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 44, 0, -2.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(3.4F, 3.15F, 3.95F);
		hx_vest.addChild(cube_r15);
		setRotationAngle(cube_r15, -0.0873F, 0.2443F, 0.2269F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 0, -2.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-3.4F, 3.15F, 3.95F);
		hx_vest.addChild(cube_r16);
		setRotationAngle(cube_r16, -0.0873F, -0.2443F, -0.2269F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 18, 62, 0.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(1.1F, 3.65F, -2.35F);
		hx_vest.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.1396F, -0.2269F, 1.4137F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 10, 71, -1.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(-1.1F, 3.65F, -2.35F);
		hx_vest.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.1396F, 0.2269F, -1.4137F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 20, 72, 0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(-1.1F, 3.65F, -2.35F);
		hx_vest.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.2793F, 0.0F, -0.384F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 31, 72, 0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(1.1F, 3.65F, -2.35F);
		hx_vest.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.2793F, 0.0F, 0.384F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 37, 72, -1.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(-1.05F, 2.15F, -2.0F);
		hx_vest.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.2793F, 0.0F, 0.5585F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 67, 40, -3.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(0.95F, 2.15F, -2.0F);
		hx_vest.addChild(cube_r22);
		setRotationAngle(cube_r22, 0.2793F, 0.0F, -0.5585F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 70, 6, 0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(-1.7F, 2.65F, -2.25F);
		hx_vest.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.2793F, 0.2094F, -0.2094F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 54, 17, -1.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(-1.7F, 2.65F, -2.25F);
		hx_vest.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.2793F, 0.1047F, 0.5585F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 44, 54, -3.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(-2.35F, 2.15F, -1.5F);
		hx_vest.addChild(cube_r25);
		setRotationAngle(cube_r25, 0.0F, 0.2094F, -0.2793F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 72, 18, -2.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F, false));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(2.35F, 2.15F, -1.5F);
		hx_vest.addChild(cube_r26);
		setRotationAngle(cube_r26, 0.0F, -0.2094F, 0.2793F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 72, 57, 0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F, false));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(1.7F, 2.65F, -2.25F);
		hx_vest.addChild(cube_r27);
		setRotationAngle(cube_r27, 0.2793F, -0.1047F, -0.5585F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 66, 48, 0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F, false));

		cube_r28 = new ModelRenderer(this);
		cube_r28.setRotationPoint(-4.5F, 7.7F, -1.3F);
		hx_vest.addChild(cube_r28);
		setRotationAngle(cube_r28, -0.1047F, 0.1745F, -0.2618F);
		cube_r28.cubeList.add(new ModelBox(cube_r28, 40, 27, 0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r29 = new ModelRenderer(this);
		cube_r29.setRotationPoint(-3.8F, 10.6F, -1.6F);
		hx_vest.addChild(cube_r29);
		setRotationAngle(cube_r29, 0.1571F, 0.4189F, -1.0123F);
		cube_r29.cubeList.add(new ModelBox(cube_r29, 77, 32, 0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F, false));

		cube_r30 = new ModelRenderer(this);
		cube_r30.setRotationPoint(3.8F, 10.6F, -1.6F);
		hx_vest.addChild(cube_r30);
		setRotationAngle(cube_r30, 0.1571F, -0.4189F, 1.0123F);
		cube_r30.cubeList.add(new ModelBox(cube_r30, 77, 39, -1.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F, false));

		cube_r31 = new ModelRenderer(this);
		cube_r31.setRotationPoint(1.45F, 5.75F, -2.75F);
		hx_vest.addChild(cube_r31);
		setRotationAngle(cube_r31, 0.1571F, -0.1396F, 0.192F);
		cube_r31.cubeList.add(new ModelBox(cube_r31, 69, 70, 0.0F, 0.0F, 0.0F, 2, 3, 1, -0.1F, false));

		cube_r32 = new ModelRenderer(this);
		cube_r32.setRotationPoint(-1.45F, 5.75F, -2.75F);
		hx_vest.addChild(cube_r32);
		setRotationAngle(cube_r32, 0.1571F, 0.1396F, -0.192F);
		cube_r32.cubeList.add(new ModelBox(cube_r32, 70, 23, -2.0F, 0.0F, 0.0F, 2, 3, 1, -0.1F, false));

		cube_r33 = new ModelRenderer(this);
		cube_r33.setRotationPoint(-1.25F, 6.75F, -2.55F);
		hx_vest.addChild(cube_r33);
		setRotationAngle(cube_r33, 0.1571F, 0.1396F, -0.192F);
		cube_r33.cubeList.add(new ModelBox(cube_r33, 36, 62, -2.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F, false));

		cube_r34 = new ModelRenderer(this);
		cube_r34.setRotationPoint(1.25F, 6.75F, -2.55F);
		hx_vest.addChild(cube_r34);
		setRotationAngle(cube_r34, 0.1571F, -0.1396F, 0.192F);
		cube_r34.cubeList.add(new ModelBox(cube_r34, 62, 15, 0.0F, 0.0F, 0.0F, 2, 3, 2, 0.0F, false));

		cube_r35 = new ModelRenderer(this);
		cube_r35.setRotationPoint(4.5F, 7.7F, -1.3F);
		hx_vest.addChild(cube_r35);
		setRotationAngle(cube_r35, -0.1047F, -0.1745F, 0.2618F);
		cube_r35.cubeList.add(new ModelBox(cube_r35, 48, 17, -1.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r36 = new ModelRenderer(this);
		cube_r36.setRotationPoint(-0.7F, 6.4F, -2.0F);
		hx_vest.addChild(cube_r36);
		setRotationAngle(cube_r36, 0.1745F, 0.0F, 1.0123F);
		cube_r36.cubeList.add(new ModelBox(cube_r36, 16, 73, -1.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F, false));

		cube_r37 = new ModelRenderer(this);
		cube_r37.setRotationPoint(-1.75F, 5.25F, -1.65F);
		hx_vest.addChild(cube_r37);
		setRotationAngle(cube_r37, -0.1745F, 0.0F, -0.0698F);
		cube_r37.cubeList.add(new ModelBox(cube_r37, 24, 62, 0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r38 = new ModelRenderer(this);
		cube_r38.setRotationPoint(1.75F, 5.25F, -1.65F);
		hx_vest.addChild(cube_r38);
		setRotationAngle(cube_r38, -0.1745F, 0.0F, 0.0698F);
		cube_r38.cubeList.add(new ModelBox(cube_r38, 42, 62, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r39 = new ModelRenderer(this);
		cube_r39.setRotationPoint(-4.45F, 6.5F, -1.3F);
		hx_vest.addChild(cube_r39);
		setRotationAngle(cube_r39, -0.0698F, 0.192F, -1.9024F);
		cube_r39.cubeList.add(new ModelBox(cube_r39, 0, 48, -1.0F, 0.0F, 0.0F, 1, 2, 5, 0.0F, false));

		cube_r40 = new ModelRenderer(this);
		cube_r40.setRotationPoint(4.45F, 6.5F, -1.3F);
		hx_vest.addChild(cube_r40);
		setRotationAngle(cube_r40, -0.0698F, -0.192F, 1.9024F);
		cube_r40.cubeList.add(new ModelBox(cube_r40, 47, 47, 0.0F, 0.0F, 0.0F, 1, 2, 5, 0.0F, false));

		cube_r41 = new ModelRenderer(this);
		cube_r41.setRotationPoint(1.45F, 5.75F, -1.9F);
		hx_vest.addChild(cube_r41);
		setRotationAngle(cube_r41, 0.2094F, 0.0F, -0.9774F);
		cube_r41.cubeList.add(new ModelBox(cube_r41, 52, 26, 0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r42 = new ModelRenderer(this);
		cube_r42.setRotationPoint(-1.45F, 5.75F, -1.9F);
		hx_vest.addChild(cube_r42);
		setRotationAngle(cube_r42, 0.2094F, 0.0F, 0.9774F);
		cube_r42.cubeList.add(new ModelBox(cube_r42, 52, 40, -1.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r43 = new ModelRenderer(this);
		cube_r43.setRotationPoint(0.7F, 6.4F, -2.0F);
		hx_vest.addChild(cube_r43);
		setRotationAngle(cube_r43, 0.1745F, 0.0F, -1.0123F);
		cube_r43.cubeList.add(new ModelBox(cube_r43, 43, 73, 0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F, false));

		cube_r44 = new ModelRenderer(this);
		cube_r44.setRotationPoint(1.7F, 2.65F, -2.25F);
		hx_vest.addChild(cube_r44);
		setRotationAngle(cube_r44, 0.2793F, -0.2094F, 0.2094F);
		cube_r44.cubeList.add(new ModelBox(cube_r44, 0, 55, 0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		cube_r45 = new ModelRenderer(this);
		cube_r45.setRotationPoint(0.95F, 2.15F, -2.0F);
		hx_vest.addChild(cube_r45);
		setRotationAngle(cube_r45, 0.2793F, 0.0F, 0.3316F);
		cube_r45.cubeList.add(new ModelBox(cube_r45, 47, 73, 0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F, false));

		cube_r46 = new ModelRenderer(this);
		cube_r46.setRotationPoint(-0.95F, 2.15F, -2.0F);
		hx_vest.addChild(cube_r46);
		setRotationAngle(cube_r46, 0.2793F, 0.0F, -0.3316F);
		cube_r46.cubeList.add(new ModelBox(cube_r46, 51, 73, -1.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F, false));

		hx_helmet_actual = new ModelRenderer(this);
		hx_helmet_actual.setRotationPoint(0.0F, 0.0F, 0.0F);
		
		bipedBody = hx_vest;
		bipedHead.isHidden = true;
		bipedHeadwear.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedRightLeg.isHidden = true;
		
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