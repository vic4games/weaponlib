package com.jimholden.conomy.entity.models;
// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

import com.jimholden.conomy.entity.EntityTestVes;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TestCar extends ModelBase {
	private final ModelRenderer bone5;
	private final ModelRenderer da_wheels;
	private final ModelRenderer wheel;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer wheel2;
	private final ModelRenderer bone27;
	private final ModelRenderer bone28;
	private final ModelRenderer wheel3;
	private final ModelRenderer bone34;
	private final ModelRenderer bone35;
	private final ModelRenderer wheel4;
	private final ModelRenderer bone36;
	private final ModelRenderer bone37;
	private final ModelRenderer interior;
	private final ModelRenderer bone76;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer bone84;
	private final ModelRenderer bone85;
	private final ModelRenderer bone77;
	private final ModelRenderer bone78;
	private final ModelRenderer bone79;
	private final ModelRenderer bone80;
	private final ModelRenderer bone93;
	private final ModelRenderer bone94;
	private final ModelRenderer bone81;
	private final ModelRenderer bone3;
	private final ModelRenderer bone91;
	private final ModelRenderer bone92;
	private final ModelRenderer bone86;
	private final ModelRenderer cube_r4;
	private final ModelRenderer bone87;
	private final ModelRenderer cube_r5;
	private final ModelRenderer bone88;
	private final ModelRenderer bone89;
	private final ModelRenderer bone90;
	private final ModelRenderer seat_driver;
	private final ModelRenderer bone95;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer bone96;
	private final ModelRenderer bone101;
	private final ModelRenderer bone99;
	private final ModelRenderer cube_r8;
	private final ModelRenderer bone4;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer bone100;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer seat_passenger;
	private final ModelRenderer bone102;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer bone103;
	private final ModelRenderer bone104;
	private final ModelRenderer bone105;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer bone106;
	private final ModelRenderer cube_r19;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer backseat;
	private final ModelRenderer bone108;
	private final ModelRenderer bone119;
	private final ModelRenderer bone120;
	private final ModelRenderer bone121;
	private final ModelRenderer bone122;
	private final ModelRenderer seat4;
	private final ModelRenderer bone123;
	private final ModelRenderer bone124;
	private final ModelRenderer bone125;
	private final ModelRenderer bone126;
	private final ModelRenderer bone127;
	private final ModelRenderer bone107;
	private final ModelRenderer cube_r22;
	private final ModelRenderer interior_wheel;
	private final ModelRenderer bone111;
	private final ModelRenderer cube_r23;
	private final ModelRenderer bone110;
	private final ModelRenderer cube_r24;
	private final ModelRenderer bone112;
	private final ModelRenderer cube_r25;
	private final ModelRenderer cube_r26;
	private final ModelRenderer bone116;
	private final ModelRenderer bone117;
	private final ModelRenderer bone118;
	private final ModelRenderer bone115;
	private final ModelRenderer bone114;
	private final ModelRenderer bone113;
	private final ModelRenderer bone109;
	private final ModelRenderer body_audi;
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
	private final ModelRenderer cube_r47;
	private final ModelRenderer cube_r48;
	private final ModelRenderer cube_r49;
	private final ModelRenderer cube_r50;
	private final ModelRenderer cube_r51;
	private final ModelRenderer cube_r52;
	private final ModelRenderer cube_r53;
	private final ModelRenderer cube_r54;
	private final ModelRenderer cube_r55;
	private final ModelRenderer cube_r56;
	private final ModelRenderer cube_r57;
	private final ModelRenderer cube_r58;
	private final ModelRenderer cube_r59;
	private final ModelRenderer cube_r60;
	private final ModelRenderer cube_r61;
	private final ModelRenderer bone128;
	private final ModelRenderer cube_r62;
	private final ModelRenderer cube_r63;
	private final ModelRenderer bone131;
	private final ModelRenderer cube_r64;
	private final ModelRenderer cube_r65;
	private final ModelRenderer cube_r66;
	private final ModelRenderer cube_r67;
	private final ModelRenderer cube_r68;
	private final ModelRenderer cube_r69;
	private final ModelRenderer cube_r70;
	private final ModelRenderer cube_r71;
	private final ModelRenderer cube_r72;
	private final ModelRenderer cube_r73;
	private final ModelRenderer cube_r74;
	private final ModelRenderer cube_r75;
	private final ModelRenderer cube_r76;
	private final ModelRenderer cube_r77;
	private final ModelRenderer grille;
	private final ModelRenderer cube_r78;
	private final ModelRenderer bone132;
	private final ModelRenderer cube_r79;
	private final ModelRenderer cube_r80;
	private final ModelRenderer cube_r81;
	private final ModelRenderer cube_r82;
	private final ModelRenderer bone129;
	private final ModelRenderer cube_r83;
	private final ModelRenderer cube_r84;
	private final ModelRenderer bone130;
	private final ModelRenderer cube_r85;
	private final ModelRenderer cube_r86;
	private final ModelRenderer cube_r87;

	public TestCar() {
		textureWidth = 512;
		textureHeight = 512;

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		da_wheels = new ModelRenderer(this);
		da_wheels.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone5.addChild(da_wheels);
		

		wheel = new ModelRenderer(this);
		wheel.setRotationPoint(3.0F, 0.0F, 0.0F);
		da_wheels.addChild(wheel);
		wheel.cubeList.add(new ModelBox(wheel, 366, 335, -7.0F, -22.0F, -5.0F, 7, 4, 10, 0.0F, false));
		wheel.cubeList.add(new ModelBox(wheel, 325, 211, -5.0F, -18.0F, -7.0F, 3, 14, 14, 0.0F, false));
		wheel.cubeList.add(new ModelBox(wheel, 176, 378, -7.0F, -16.0F, 7.0F, 7, 10, 4, 0.0F, false));
		wheel.cubeList.add(new ModelBox(wheel, 154, 378, -7.0F, -16.0F, -11.0F, 7, 10, 4, 0.0F, false));
		wheel.cubeList.add(new ModelBox(wheel, 366, 321, -7.0F, -4.0F, -5.0F, 7, 4, 10, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 6.0F);
		wheel.addChild(bone);
		setRotationAngle(bone, 0.7854F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 30, 378, -6.99F, -4.7071F, -0.7071F, 7, 4, 8, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 384, 236, -6.99F, -15.7782F, -7.7782F, 7, 8, 4, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -22.0F, -5.0F);
		wheel.addChild(bone2);
		setRotationAngle(bone2, -0.7854F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 378, -6.99F, -7.0711F, 7.0711F, 7, 4, 8, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 384, 224, -6.99F, 0.0F, 0.0F, 7, 8, 4, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 347, 239, -6.5F, 2.1213F, 9.1924F, 6, 4, 4, 0.0F, false));

		wheel2 = new ModelRenderer(this);
		wheel2.setRotationPoint(3.0F, 0.0F, 92.0F);
		da_wheels.addChild(wheel2);
		wheel2.cubeList.add(new ModelBox(wheel2, 366, 307, -7.0F, -22.0F, -5.0F, 7, 4, 10, 0.0F, false));
		wheel2.cubeList.add(new ModelBox(wheel2, 85, 259, -5.0F, -18.0F, -7.0F, 3, 14, 14, 0.0F, false));
		wheel2.cubeList.add(new ModelBox(wheel2, 132, 378, -7.0F, -16.0F, 7.0F, 7, 10, 4, 0.0F, false));
		wheel2.cubeList.add(new ModelBox(wheel2, 110, 378, -7.0F, -16.0F, -11.0F, 7, 10, 4, 0.0F, false));
		wheel2.cubeList.add(new ModelBox(wheel2, 366, 293, -7.0F, -4.0F, -5.0F, 7, 4, 10, 0.0F, false));

		bone27 = new ModelRenderer(this);
		bone27.setRotationPoint(0.0F, 0.0F, 6.0F);
		wheel2.addChild(bone27);
		setRotationAngle(bone27, 0.7854F, 0.0F, 0.0F);
		bone27.cubeList.add(new ModelBox(bone27, 376, 352, -6.99F, -4.7071F, -0.7071F, 7, 4, 8, 0.0F, false));
		bone27.cubeList.add(new ModelBox(bone27, 384, 212, -6.99F, -15.7782F, -7.7782F, 7, 8, 4, 0.0F, false));

		bone28 = new ModelRenderer(this);
		bone28.setRotationPoint(0.0F, -22.0F, -5.0F);
		wheel2.addChild(bone28);
		setRotationAngle(bone28, -0.7854F, 0.0F, 0.0F);
		bone28.cubeList.add(new ModelBox(bone28, 370, 374, -6.99F, -7.0711F, 7.0711F, 7, 4, 8, 0.0F, false));
		bone28.cubeList.add(new ModelBox(bone28, 384, 200, -6.99F, 0.0F, 0.0F, 7, 8, 4, 0.0F, false));
		bone28.cubeList.add(new ModelBox(bone28, 170, 334, -6.5F, 2.1213F, 9.1924F, 6, 4, 4, 0.0F, false));

		wheel3 = new ModelRenderer(this);
		wheel3.setRotationPoint(-58.0F, 0.0F, 92.0F);
		da_wheels.addChild(wheel3);
		wheel3.cubeList.add(new ModelBox(wheel3, 366, 279, -7.0F, -22.0F, -5.0F, 7, 4, 10, 0.0F, false));
		wheel3.cubeList.add(new ModelBox(wheel3, 101, 101, -5.0F, -18.0F, -7.0F, 3, 14, 14, 0.0F, false));
		wheel3.cubeList.add(new ModelBox(wheel3, 88, 378, -7.0F, -16.0F, 7.0F, 7, 10, 4, 0.0F, false));
		wheel3.cubeList.add(new ModelBox(wheel3, 0, 345, -7.0F, -16.0F, -11.0F, 7, 10, 4, 0.0F, false));
		wheel3.cubeList.add(new ModelBox(wheel3, 0, 225, -7.0F, -4.0F, -5.0F, 7, 4, 10, 0.0F, false));

		bone34 = new ModelRenderer(this);
		bone34.setRotationPoint(0.0F, 0.0F, 6.0F);
		wheel3.addChild(bone34);
		setRotationAngle(bone34, 0.7854F, 0.0F, 0.0F);
		bone34.cubeList.add(new ModelBox(bone34, 348, 370, -6.99F, -4.7071F, -0.7071F, 7, 4, 8, 0.0F, false));
		bone34.cubeList.add(new ModelBox(bone34, 384, 188, -6.99F, -15.7782F, -7.7782F, 7, 8, 4, 0.0F, false));

		bone35 = new ModelRenderer(this);
		bone35.setRotationPoint(0.0F, -22.0F, -5.0F);
		wheel3.addChild(bone35);
		setRotationAngle(bone35, -0.7854F, 0.0F, 0.0F);
		bone35.cubeList.add(new ModelBox(bone35, 318, 106, -6.99F, -7.0711F, 7.0711F, 7, 4, 8, 0.0F, false));
		bone35.cubeList.add(new ModelBox(bone35, 318, 380, -6.99F, 0.0F, 0.0F, 7, 8, 4, 0.0F, false));
		bone35.cubeList.add(new ModelBox(bone35, 102, 322, -6.5F, 2.1213F, 9.1924F, 6, 4, 4, 0.0F, false));

		wheel4 = new ModelRenderer(this);
		wheel4.setRotationPoint(-59.0F, -1.0F, 0.0F);
		da_wheels.addChild(wheel4);
		wheel4.cubeList.add(new ModelBox(wheel4, 150, 121, -7.0F, -22.0F, -5.0F, 7, 4, 10, 0.0F, false));
		wheel4.cubeList.add(new ModelBox(wheel4, 0, 73, -5.0F, -18.0F, -7.0F, 3, 14, 14, 0.0F, false));
		wheel4.cubeList.add(new ModelBox(wheel4, 323, 279, -7.0F, -16.0F, 7.0F, 7, 10, 4, 0.0F, false));
		wheel4.cubeList.add(new ModelBox(wheel4, 46, 317, -7.0F, -16.0F, -11.0F, 7, 10, 4, 0.0F, false));
		wheel4.cubeList.add(new ModelBox(wheel4, 49, 93, -7.0F, -4.0F, -5.0F, 7, 4, 10, 0.0F, false));

		bone36 = new ModelRenderer(this);
		bone36.setRotationPoint(0.0F, 0.0F, 6.0F);
		wheel4.addChild(bone36);
		setRotationAngle(bone36, 0.7854F, 0.0F, 0.0F);
		bone36.cubeList.add(new ModelBox(bone36, 80, 287, -6.99F, -4.7071F, -0.7071F, 7, 4, 8, 0.0F, false));
		bone36.cubeList.add(new ModelBox(bone36, 378, 155, -6.99F, -15.7782F, -7.7782F, 7, 8, 4, 0.0F, false));

		bone37 = new ModelRenderer(this);
		bone37.setRotationPoint(0.0F, -22.0F, -5.0F);
		wheel4.addChild(bone37);
		setRotationAngle(bone37, -0.7854F, 0.0F, 0.0F);
		bone37.cubeList.add(new ModelBox(bone37, 161, 146, -6.99F, -7.0711F, 7.0711F, 7, 4, 8, 0.0F, false));
		bone37.cubeList.add(new ModelBox(bone37, 0, 359, -6.99F, 0.0F, 0.0F, 7, 8, 4, 0.0F, false));
		bone37.cubeList.add(new ModelBox(bone37, 102, 314, -6.5F, 2.1213F, 9.1924F, 6, 4, 4, 0.0F, false));

		interior = new ModelRenderer(this);
		interior.setRotationPoint(3.0F, -4.0F, 0.0F);
		bone5.addChild(interior);
		interior.cubeList.add(new ModelBox(interior, 0, 345, -59.0F, -28.0F, 82.5F, 50, 7, 26, 0.0F, false));

		bone76 = new ModelRenderer(this);
		bone76.setRotationPoint(-63.0F, -24.7F, 5.0F);
		interior.addChild(bone76);
		setRotationAngle(bone76, -0.2443F, 0.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(29.5F, 4.0F, 8.0F);
		bone76.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.6545F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 336, 177, -30.5F, 0.344F, 4.7002F, 61, 7, 4, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 0.0F, 8.0F);
		bone76.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.7418F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 173, 0.0F, 7.296F, 0.7163F, 56, 1, 15, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(29.0F, 4.0F, 8.0F);
		bone76.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.1309F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 282, 258, -27.0F, -6.4943F, -4.6634F, 54, 10, 11, 0.0F, false));

		bone84 = new ModelRenderer(this);
		bone84.setRotationPoint(0.25F, -0.4851F, 7.879F);
		bone76.addChild(bone84);
		setRotationAngle(bone84, 0.0F, -0.192F, 0.0F);
		

		bone85 = new ModelRenderer(this);
		bone85.setRotationPoint(58.75F, -0.4851F, 7.879F);
		bone76.addChild(bone85);
		setRotationAngle(bone85, 0.0F, 0.192F, 0.0F);
		

		bone77 = new ModelRenderer(this);
		bone77.setRotationPoint(-24.0F, -21.0F, 21.25F);
		interior.addChild(bone77);
		setRotationAngle(bone77, 0.192F, 0.0F, 0.0F);
		bone77.cubeList.add(new ModelBox(bone77, 348, 352, 1.0F, -6.1385F, -10.9959F, 8, 6, 12, 0.0F, false));

		bone78 = new ModelRenderer(this);
		bone78.setRotationPoint(1.0F, -5.0F, 1.0F);
		bone77.addChild(bone78);
		setRotationAngle(bone78, 0.0F, 0.0F, -0.829F);
		bone78.cubeList.add(new ModelBox(bone78, 348, 155, -8.1606F, -0.7692F, -11.9859F, 9, 4, 12, 0.0F, false));

		bone79 = new ModelRenderer(this);
		bone79.setRotationPoint(9.0F, -5.0F, 1.0F);
		bone77.addChild(bone79);
		setRotationAngle(bone79, 0.0F, 0.0F, 0.829F);
		bone79.cubeList.add(new ModelBox(bone79, 337, 20, -0.8394F, -0.7692F, -11.9859F, 9, 4, 12, 0.0F, false));

		bone80 = new ModelRenderer(this);
		bone80.setRotationPoint(-34.0F, -19.5F, 22.0F);
		interior.addChild(bone80);
		setRotationAngle(bone80, -0.4189F, 0.0F, 0.0F);
		

		bone93 = new ModelRenderer(this);
		bone93.setRotationPoint(5.0F, -1.0F, 14.0F);
		bone80.addChild(bone93);
		setRotationAngle(bone93, 0.0F, 1.3788F, 0.0F);
		

		bone94 = new ModelRenderer(this);
		bone94.setRotationPoint(-5.0F, -1.0F, 14.0F);
		bone80.addChild(bone94);
		setRotationAngle(bone94, 0.0F, -1.3788F, 0.0F);
		

		bone81 = new ModelRenderer(this);
		bone81.setRotationPoint(-33.5F, -15.25F, 33.0F);
		interior.addChild(bone81);
		setRotationAngle(bone81, 0.0436F, 0.0F, 0.0F);
		bone81.cubeList.add(new ModelBox(bone81, 80, 259, -4.0F, -1.0F, -20.0F, 7, 8, 40, 0.0F, false));
		bone81.cubeList.add(new ModelBox(bone81, 134, 259, -4.0F, -0.8264F, 2.9848F, 7, 8, 18, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-0.625F, -2.7445F, -3.6251F);
		bone81.addChild(bone3);
		setRotationAngle(bone3, 0.4363F, 0.0F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 0, -0.375F, -0.2555F, -0.3749F, 1, 3, 1, 0.0F, false));
		bone3.cubeList.add(new ModelBox(bone3, 12, 0, -1.125F, -2.2445F, -1.1251F, 2, 2, 2, 0.0F, false));

		bone91 = new ModelRenderer(this);
		bone91.setRotationPoint(-5.0F, -1.0F, 0.0F);
		bone81.addChild(bone91);
		setRotationAngle(bone91, 0.0F, 0.0F, -1.1694F);
		bone91.cubeList.add(new ModelBox(bone91, 264, 352, -6.6093F, 0.9205F, -20.0F, 7, 5, 40, 0.0F, false));

		bone92 = new ModelRenderer(this);
		bone92.setRotationPoint(4.0F, -1.0F, 0.0F);
		bone81.addChild(bone92);
		setRotationAngle(bone92, 0.0F, 0.0F, 1.1694F);
		bone92.cubeList.add(new ModelBox(bone92, 210, 345, -0.3907F, 0.9205F, -20.0F, 7, 5, 40, 0.0F, false));

		bone86 = new ModelRenderer(this);
		bone86.setRotationPoint(-3.0F, -18.0F, 26.0F);
		interior.addChild(bone86);
		setRotationAngle(bone86, 0.0698F, -0.0698F, -0.4887F);
		

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone86.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0873F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 318, 0, -5.7616F, -1.9281F, -5.8116F, 5, 4, 62, 0.0F, false));

		bone87 = new ModelRenderer(this);
		bone87.setRotationPoint(-67.1F, -18.0F, 26.0F);
		interior.addChild(bone87);
		setRotationAngle(bone87, 0.0698F, 0.0698F, 0.4887F);
		

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone87.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, -0.0873F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 312, 192, 2.7616F, -2.9281F, -5.8116F, 5, 4, 62, 0.0F, false));

		bone88 = new ModelRenderer(this);
		bone88.setRotationPoint(-33.75F, -34.5F, 28.0F);
		interior.addChild(bone88);
		setRotationAngle(bone88, -0.4363F, 0.0F, 0.0F);
		bone88.cubeList.add(new ModelBox(bone88, 0, 101, -5.0F, -4.0F, 0.0F, 10, 4, 2, 0.0F, false));

		bone89 = new ModelRenderer(this);
		bone89.setRotationPoint(-33.75F, -45.9F, 29.7F);
		interior.addChild(bone89);
		setRotationAngle(bone89, -1.3614F, 0.0F, 0.0F);
		bone89.cubeList.add(new ModelBox(bone89, 49, 93, -2.0F, -5.3367F, 7.8252F, 4, 7, 1, 0.0F, false));

		bone90 = new ModelRenderer(this);
		bone90.setRotationPoint(-33.75F, -29.3F, 81.0F);
		interior.addChild(bone90);
		setRotationAngle(bone90, -0.0698F, 0.0F, 0.0F);
		

		seat_driver = new ModelRenderer(this);
		seat_driver.setRotationPoint(1.0F, 0.0F, 0.0F);
		interior.addChild(seat_driver);
		

		bone95 = new ModelRenderer(this);
		bone95.setRotationPoint(-27.0F, -15.0F, 33.0F);
		seat_driver.addChild(bone95);
		setRotationAngle(bone95, -0.1047F, 0.0F, 0.0F);
		bone95.cubeList.add(new ModelBox(bone95, 202, 285, 3.0F, 1.989F, 0.2091F, 10, 4, 16, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(8.0F, 3.989F, 8.2091F);
		bone95.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.3927F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 337, 0, 4.7716F, -0.8519F, -8.0F, 3, 4, 16, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(8.0F, 3.989F, 8.2091F);
		bone95.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.0F, 0.3927F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 80, 314, -7.7716F, -0.8519F, -8.0F, 3, 4, 16, 0.0F, false));

		bone96 = new ModelRenderer(this);
		bone96.setRotationPoint(-19.0F, -28.0F, 58.0F);
		seat_driver.addChild(bone96);
		setRotationAngle(bone96, -0.3491F, 0.0F, 0.0F);
		bone96.cubeList.add(new ModelBox(bone96, 366, 114, -5.0F, 5.0F, -3.0F, 10, 16, 4, 0.0F, false));

		bone101 = new ModelRenderer(this);
		bone101.setRotationPoint(-19.0F, -34.0F, 58.5F);
		seat_driver.addChild(bone101);
		setRotationAngle(bone101, -0.1396F, 0.0F, 0.0F);
		bone101.cubeList.add(new ModelBox(bone101, 0, 270, -3.0F, 1.2476F, -3.9652F, 6, 5, 4, 0.0F, false));
		bone101.cubeList.add(new ModelBox(bone101, 325, 239, -3.0F, 6.178F, -3.4701F, 6, 4, 5, 0.0F, false));

		bone99 = new ModelRenderer(this);
		bone99.setRotationPoint(-13.0F, -28.0F, 58.0F);
		seat_driver.addChild(bone99);
		setRotationAngle(bone99, -0.4014F, -0.8552F, 0.3142F);
		

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-2.0F, 1.0F, -1.5F);
		bone99.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.7854F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 83, 190, -1.7973F, -1.2056F, -2.0725F, 3, 5, 6, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-2.0F, 10.0F, -1.5F);
		bone99.addChild(bone4);
		

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone4.addChild(cube_r9);
		setRotationAngle(cube_r9, -0.3054F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 75, 75, -2.0F, -6.0F, -1.5F, 4, 10, 3, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-2.0273F, -0.3362F, -3.6812F);
		bone4.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.48F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 173, 0.0273F, 3.7658F, -0.7791F, 4, 7, 2, 0.0F, false));

		bone100 = new ModelRenderer(this);
		bone100.setRotationPoint(-25.0F, -28.0F, 58.0F);
		seat_driver.addChild(bone100);
		setRotationAngle(bone100, -0.4014F, 0.8552F, -0.3142F);
		

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(2.0F, 1.0F, -1.5F);
		bone100.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.7854F, 0.0F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 208, 20, -1.2027F, -1.2056F, -2.0725F, 3, 5, 6, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(4.0273F, 9.6638F, -5.1812F);
		bone100.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.48F, 0.0F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 150, 101, -4.0273F, 3.7658F, -0.7791F, 4, 7, 2, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(2.0F, 10.0F, -1.5F);
		bone100.addChild(cube_r13);
		setRotationAngle(cube_r13, -0.3054F, 0.0F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 49, 73, -2.0F, -6.0F, -1.5F, 4, 10, 3, 0.0F, false));

		seat_passenger = new ModelRenderer(this);
		seat_passenger.setRotationPoint(-31.0F, 0.0F, 0.0F);
		interior.addChild(seat_passenger);
		

		bone102 = new ModelRenderer(this);
		bone102.setRotationPoint(-27.0F, -15.0F, 33.0F);
		seat_passenger.addChild(bone102);
		setRotationAngle(bone102, -0.1047F, 0.0F, 0.0F);
		bone102.cubeList.add(new ModelBox(bone102, 282, 106, 3.0F, 1.989F, 0.2091F, 10, 4, 16, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(8.0F, 3.989F, 8.2091F);
		bone102.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.0F, 0.3927F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 186, 20, -7.7716F, -0.8519F, -8.0F, 3, 4, 16, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(8.0F, 3.989F, 8.2091F);
		bone102.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0F, 0.0F, -0.3927F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 312, 312, 4.7716F, -0.8519F, -8.0F, 3, 4, 16, 0.0F, false));

		bone103 = new ModelRenderer(this);
		bone103.setRotationPoint(-19.0F, -28.0F, 58.0F);
		seat_passenger.addChild(bone103);
		setRotationAngle(bone103, -0.3491F, 0.0F, 0.0F);
		bone103.cubeList.add(new ModelBox(bone103, 202, 141, -5.0F, 5.0F, -3.0F, 10, 16, 4, 0.0F, false));

		bone104 = new ModelRenderer(this);
		bone104.setRotationPoint(-19.0F, -34.0F, 58.5F);
		seat_passenger.addChild(bone104);
		setRotationAngle(bone104, -0.1396F, 0.0F, 0.0F);
		bone104.cubeList.add(new ModelBox(bone104, 218, 224, -3.0F, 1.2476F, -3.9652F, 6, 5, 4, 0.0F, false));
		bone104.cubeList.add(new ModelBox(bone104, 148, 334, -3.0F, 6.178F, -3.4701F, 6, 4, 5, 0.0F, false));

		bone105 = new ModelRenderer(this);
		bone105.setRotationPoint(-13.0F, -28.0F, 58.0F);
		seat_passenger.addChild(bone105);
		setRotationAngle(bone105, -0.4014F, -0.8552F, 0.3142F);
		

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-2.0F, 1.0F, -1.5F);
		bone105.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.7854F, 0.0F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 259, -1.7973F, -1.2056F, -2.0725F, 3, 5, 6, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(-4.0273F, 9.6638F, -5.1812F);
		bone105.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.48F, 0.0F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 121, 101, 0.0273F, 3.7658F, -0.7791F, 4, 7, 2, 0.0F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(-2.0F, 10.0F, -1.5F);
		bone105.addChild(cube_r18);
		setRotationAngle(cube_r18, -0.3054F, 0.0F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 73, -2.0F, -6.0F, -1.5F, 4, 10, 3, 0.0F, false));

		bone106 = new ModelRenderer(this);
		bone106.setRotationPoint(-25.0F, -28.0F, 58.0F);
		seat_passenger.addChild(bone106);
		setRotationAngle(bone106, -0.4014F, 0.8552F, -0.3142F);
		

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(4.0273F, 9.6638F, -5.1812F);
		bone106.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.48F, 0.0F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 101, 101, -4.0273F, 3.7658F, -0.7791F, 4, 7, 2, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(2.0F, 10.0F, -1.5F);
		bone106.addChild(cube_r20);
		setRotationAngle(cube_r20, -0.3054F, 0.0F, 0.0F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 20, 73, -2.0F, -6.0F, -1.5F, 4, 10, 3, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(2.0F, 1.0F, -1.5F);
		bone106.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.7854F, 0.0F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 134, 259, -1.2027F, -1.2056F, -2.0725F, 3, 5, 6, 0.0F, false));

		backseat = new ModelRenderer(this);
		backseat.setRotationPoint(1.0F, 0.0F, 27.0F);
		interior.addChild(backseat);
		

		bone108 = new ModelRenderer(this);
		bone108.setRotationPoint(-27.0F, -15.0F, 33.0F);
		backseat.addChild(bone108);
		setRotationAngle(bone108, -0.1047F, 0.0F, 0.0F);
		bone108.cubeList.add(new ModelBox(bone108, 126, 345, -31.0F, 1.989F, 0.2091F, 46, 4, 16, 0.0F, false));

		bone119 = new ModelRenderer(this);
		bone119.setRotationPoint(-19.0F, -28.0F, 58.0F);
		backseat.addChild(bone119);
		setRotationAngle(bone119, -0.3491F, 0.0F, 0.0F);
		bone119.cubeList.add(new ModelBox(bone119, 366, 90, -6.0F, 0.342F, -3.9397F, 12, 21, 3, 0.0F, false));

		bone120 = new ModelRenderer(this);
		bone120.setRotationPoint(-19.0F, -34.0F, 58.5F);
		backseat.addChild(bone120);
		setRotationAngle(bone120, -0.1396F, 0.0F, 0.0F);
		bone120.cubeList.add(new ModelBox(bone120, 152, 217, -6.0F, 2.1392F, -3.9903F, 10, 6, 3, 0.0F, false));
		bone120.cubeList.add(new ModelBox(bone120, 202, 161, -36.0F, 2.1392F, -3.9903F, 10, 6, 3, 0.0F, false));
		bone120.cubeList.add(new ModelBox(bone120, 243, 266, -21.0F, 3.2783F, -4.9805F, 10, 5, 3, 0.0F, false));

		bone121 = new ModelRenderer(this);
		bone121.setRotationPoint(-13.0F, -28.0F, 58.0F);
		backseat.addChild(bone121);
		setRotationAngle(bone121, -0.4014F, -0.8552F, 0.3142F);
		bone121.cubeList.add(new ModelBox(bone121, 211, 73, -4.7547F, 0.2563F, -3.6039F, 4, 16, 3, 0.0F, false));

		bone122 = new ModelRenderer(this);
		bone122.setRotationPoint(-25.0F, -28.0F, 58.0F);
		backseat.addChild(bone122);
		setRotationAngle(bone122, -0.4014F, 0.8552F, -0.3142F);
		bone122.cubeList.add(new ModelBox(bone122, 48, 287, -0.4932F, 0.2484F, -5.1668F, 4, 19, 3, 0.0F, false));

		seat4 = new ModelRenderer(this);
		seat4.setRotationPoint(-32.0F, 0.0F, -1.0F);
		backseat.addChild(seat4);
		

		bone123 = new ModelRenderer(this);
		bone123.setRotationPoint(-27.0F, -15.0F, 33.0F);
		seat4.addChild(bone123);
		setRotationAngle(bone123, -0.1047F, 0.0F, 0.0F);
		

		bone124 = new ModelRenderer(this);
		bone124.setRotationPoint(-19.0F, -28.0F, 58.0F);
		seat4.addChild(bone124);
		setRotationAngle(bone124, -0.3491F, 0.0F, 0.0F);
		bone124.cubeList.add(new ModelBox(bone124, 366, 66, -6.0F, 0.0F, -3.0F, 12, 21, 3, 0.0F, false));
		bone124.cubeList.add(new ModelBox(bone124, 0, 201, 8.0F, 0.342F, -3.9397F, 16, 21, 3, 0.0F, false));

		bone125 = new ModelRenderer(this);
		bone125.setRotationPoint(-19.0F, -34.0F, 58.5F);
		seat4.addChild(bone125);
		setRotationAngle(bone125, -0.1396F, 0.0F, 0.0F);
		

		bone126 = new ModelRenderer(this);
		bone126.setRotationPoint(-13.0F, -28.0F, 58.0F);
		seat4.addChild(bone126);
		setRotationAngle(bone126, -0.4014F, -0.8552F, 0.3142F);
		bone126.cubeList.add(new ModelBox(bone126, 202, 254, -2.7521F, -0.008F, -4.5629F, 4, 18, 3, 0.0F, false));

		bone127 = new ModelRenderer(this);
		bone127.setRotationPoint(-25.0F, -28.0F, 58.0F);
		seat4.addChild(bone127);
		setRotationAngle(bone127, -0.4014F, 0.8552F, -0.3142F);
		bone127.cubeList.add(new ModelBox(bone127, 101, 73, 0.0F, 0.0F, -3.0F, 4, 16, 3, 0.0F, false));

		bone107 = new ModelRenderer(this);
		bone107.setRotationPoint(-18.5F, -19.0F, 21.0F);
		interior.addChild(bone107);
		setRotationAngle(bone107, 0.4363F, 0.0F, 0.0F);
		

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone107.addChild(cube_r22);
		setRotationAngle(cube_r22, -0.2182F, 0.0F, 0.0F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 166, 259, -3.0F, -6.6478F, 1.9172F, 5, 5, 7, 0.0F, false));

		interior_wheel = new ModelRenderer(this);
		interior_wheel.setRotationPoint(-51.0F, -6.0F, -2.0F);
		bone5.addChild(interior_wheel);
		

		bone111 = new ModelRenderer(this);
		bone111.setRotationPoint(38.5F, -25.0F, 31.5F);
		interior_wheel.addChild(bone111);
		setRotationAngle(bone111, -1.2043F, -0.3142F, 0.0F);
		

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(1.5F, -1.3584F, 1.5664F);
		bone111.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.0F, -0.1745F, 0.0F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 41, 37, -2.4189F, -0.8208F, -1.0403F, 4, 2, 2, 0.0F, false));

		bone110 = new ModelRenderer(this);
		bone110.setRotationPoint(31.5F, -25.0F, 31.5F);
		interior_wheel.addChild(bone110);
		setRotationAngle(bone110, -1.2043F, 0.3142F, 0.0F);
		

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(-1.5F, -1.3584F, 1.5664F);
		bone110.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.0F, 0.1745F, 0.0F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 101, 110, -1.5811F, -0.8208F, -1.0403F, 4, 2, 2, 0.0F, false));

		bone112 = new ModelRenderer(this);
		bone112.setRotationPoint(37.5F, -23.0F, 29.3F);
		interior_wheel.addChild(bone112);
		setRotationAngle(bone112, -1.0123F, 0.0F, 0.0F);
		

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(-1.0F, -3.5F, 5.5F);
		bone112.addChild(cube_r25);
		setRotationAngle(cube_r25, 0.0F, 0.1309F, 0.0F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 19, 102, -3.4743F, -0.5F, -2.8916F, 1, 1, 5, 0.0F, false));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(-1.0F, -3.5F, 5.5F);
		bone112.addChild(cube_r26);
		setRotationAngle(cube_r26, 0.0F, -0.1309F, 0.0F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 117, 129, -0.5F, -0.5F, -2.5F, 1, 1, 5, 0.0F, false));

		bone116 = new ModelRenderer(this);
		bone116.setRotationPoint(34.0F, -30.0F, 30.7F);
		interior_wheel.addChild(bone116);
		setRotationAngle(bone116, 0.2269F, -0.1745F, 1.2217F);
		bone116.cubeList.add(new ModelBox(bone116, 62, 132, 0.0F, -1.0F, 0.0F, 2, 7, 2, 0.0F, false));

		bone117 = new ModelRenderer(this);
		bone117.setRotationPoint(35.5F, -18.0F, 35.3F);
		interior_wheel.addChild(bone117);
		setRotationAngle(bone117, -0.0698F, 0.4189F, -1.8151F);
		bone117.cubeList.add(new ModelBox(bone117, 150, 121, -2.0F, -1.0F, 0.0F, 2, 6, 2, 0.0F, false));

		bone118 = new ModelRenderer(this);
		bone118.setRotationPoint(34.5F, -18.0F, 35.3F);
		interior_wheel.addChild(bone118);
		setRotationAngle(bone118, -0.0698F, -0.4189F, 1.8151F);
		bone118.cubeList.add(new ModelBox(bone118, 160, 142, 0.0F, -1.0F, 0.0F, 2, 6, 2, 0.0F, false));

		bone115 = new ModelRenderer(this);
		bone115.setRotationPoint(36.0F, -30.0F, 30.7F);
		interior_wheel.addChild(bone115);
		setRotationAngle(bone115, 0.2269F, 0.1745F, -1.2217F);
		bone115.cubeList.add(new ModelBox(bone115, 101, 142, -2.0F, -1.0F, 0.0F, 2, 7, 2, 0.0F, false));

		bone114 = new ModelRenderer(this);
		bone114.setRotationPoint(29.5F, -22.0F, 33.9F);
		interior_wheel.addChild(bone114);
		setRotationAngle(bone114, 0.3665F, 0.3142F, 0.0F);
		bone114.cubeList.add(new ModelBox(bone114, 60, 35, -2.0F, -6.0F, 0.0F, 2, 10, 2, 0.0F, false));

		bone113 = new ModelRenderer(this);
		bone113.setRotationPoint(40.5F, -22.0F, 33.9F);
		interior_wheel.addChild(bone113);
		setRotationAngle(bone113, 0.3665F, -0.3142F, 0.0F);
		bone113.cubeList.add(new ModelBox(bone113, 176, 101, 0.0F, -6.0F, 0.0F, 2, 10, 2, 0.0F, false));

		bone109 = new ModelRenderer(this);
		bone109.setRotationPoint(35.5F, -27.0F, 30.0F);
		interior_wheel.addChild(bone109);
		setRotationAngle(bone109, -1.4137F, 0.0F, 0.0F);
		bone109.cubeList.add(new ModelBox(bone109, 102, 287, -3.0F, -4.0F, 2.0F, 5, 4, 4, 0.0F, false));

		body_audi = new ModelRenderer(this);
		body_audi.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone5.addChild(body_audi);
		body_audi.cubeList.add(new ModelBox(body_audi, 0, 0, -63.0F, -10.0F, 12.0F, 59, 5, 68, 0.0F, false));
		body_audi.cubeList.add(new ModelBox(body_audi, 0, 201, -60.0F, -25.0F, -26.0F, 57, 20, 38, 0.0F, false));
		body_audi.cubeList.add(new ModelBox(body_audi, 190, 201, -53.0F, -46.0F, 35.0F, 43, 4, 49, 0.0F, false));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(-60.0F, -13.5F, 49.0F);
		body_audi.addChild(cube_r27);
		setRotationAngle(cube_r27, 0.0F, 0.0F, 0.0436F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 282, 282, -4.0F, 7.5F, -37.0F, 8, 2, 68, 0.0F, false));

		cube_r28 = new ModelRenderer(this);
		cube_r28.setRotationPoint(-61.5F, -28.0F, 46.0F);
		body_audi.addChild(cube_r28);
		setRotationAngle(cube_r28, 0.0436F, 0.0F, 0.0873F);
		cube_r28.cubeList.add(new ModelBox(cube_r28, 0, 73, -0.5F, -2.0F, -34.0F, 5, 9, 91, 0.0F, false));

		cube_r29 = new ModelRenderer(this);
		cube_r29.setRotationPoint(-57.5F, -32.437F, 80.6105F);
		body_audi.addChild(cube_r29);
		setRotationAngle(cube_r29, 0.2182F, 0.0F, 0.5236F);
		cube_r29.cubeList.add(new ModelBox(cube_r29, 0, 114, -3.0F, -12.5F, -2.5F, 4, 16, 5, 0.0F, false));

		cube_r30 = new ModelRenderer(this);
		cube_r30.setRotationPoint(-57.75F, -32.437F, 51.6105F);
		body_audi.addChild(cube_r30);
		setRotationAngle(cube_r30, -0.1745F, 0.0F, 0.5236F);
		cube_r30.cubeList.add(new ModelBox(cube_r30, 282, 61, -3.0F, -12.5F, -2.5F, 4, 17, 5, 0.0F, false));

		cube_r31 = new ModelRenderer(this);
		cube_r31.setRotationPoint(-3.0F, -32.437F, 80.6105F);
		body_audi.addChild(cube_r31);
		setRotationAngle(cube_r31, 0.2182F, 0.0F, -0.5236F);
		cube_r31.cubeList.add(new ModelBox(cube_r31, 72, 132, -3.0F, -13.5F, -2.5F, 4, 17, 5, 0.0F, false));

		cube_r32 = new ModelRenderer(this);
		cube_r32.setRotationPoint(-3.0F, -32.437F, 51.6105F);
		body_audi.addChild(cube_r32);
		setRotationAngle(cube_r32, -0.1745F, 0.0F, -0.5236F);
		cube_r32.cubeList.add(new ModelBox(cube_r32, 282, 279, -3.0F, -13.5F, -2.5F, 4, 17, 5, 0.0F, false));

		cube_r33 = new ModelRenderer(this);
		cube_r33.setRotationPoint(-0.5F, -28.0F, 46.0F);
		body_audi.addChild(cube_r33);
		setRotationAngle(cube_r33, 0.0436F, 0.0F, -0.0873F);
		cube_r33.cubeList.add(new ModelBox(cube_r33, 101, 101, -4.5F, -2.0F, -34.0F, 5, 9, 91, 0.0F, false));

		cube_r34 = new ModelRenderer(this);
		cube_r34.setRotationPoint(-55.3131F, -27.6016F, 14.7915F);
		body_audi.addChild(cube_r34);
		setRotationAngle(cube_r34, 0.3927F, -0.5672F, 1.309F);
		cube_r34.cubeList.add(new ModelBox(cube_r34, 49, 73, -5.0F, 0.5F, -3.5F, 5, 4, 16, 0.0F, false));

		cube_r35 = new ModelRenderer(this);
		cube_r35.setRotationPoint(-7.6869F, -27.6016F, 14.7915F);
		body_audi.addChild(cube_r35);
		setRotationAngle(cube_r35, 0.3927F, 0.5672F, -1.309F);
		cube_r35.cubeList.add(new ModelBox(cube_r35, 46, 143, 0.0F, 0.5F, -3.5F, 5, 4, 16, 0.0F, false));

		cube_r36 = new ModelRenderer(this);
		cube_r36.setRotationPoint(-51.3131F, -35.6016F, 25.7915F);
		body_audi.addChild(cube_r36);
		setRotationAngle(cube_r36, 0.3927F, -0.4363F, 1.309F);
		cube_r36.cubeList.add(new ModelBox(cube_r36, 150, 101, -5.0F, 0.5F, -3.5F, 5, 4, 16, 0.0F, false));

		cube_r37 = new ModelRenderer(this);
		cube_r37.setRotationPoint(-53.3131F, -35.3516F, 93.2085F);
		body_audi.addChild(cube_r37);
		setRotationAngle(cube_r37, -0.3927F, 0.4363F, 1.309F);
		cube_r37.cubeList.add(new ModelBox(cube_r37, 152, 201, -5.0F, -2.5F, -13.5F, 6, 5, 27, 0.0F, false));

		cube_r38 = new ModelRenderer(this);
		cube_r38.setRotationPoint(-52.5F, -33.75F, 112.5F);
		body_audi.addChild(cube_r38);
		setRotationAngle(cube_r38, 0.0873F, 0.0436F, 1.309F);
		cube_r38.cubeList.add(new ModelBox(cube_r38, 0, 259, -1.5F, 1.0F, -10.5F, 10, 7, 21, 0.0F, false));

		cube_r39 = new ModelRenderer(this);
		cube_r39.setRotationPoint(-59.5F, -30.0F, 112.5F);
		body_audi.addChild(cube_r39);
		setRotationAngle(cube_r39, 0.0873F, 0.1745F, 0.1309F);
		cube_r39.cubeList.add(new ModelBox(cube_r39, 0, 37, -1.5F, -2.0F, -10.5F, 10, 10, 21, 0.0F, false));

		cube_r40 = new ModelRenderer(this);
		cube_r40.setRotationPoint(-60.5F, -15.0F, 113.5F);
		body_audi.addChild(cube_r40);
		setRotationAngle(cube_r40, 0.0873F, 0.1745F, 0.0F);
		cube_r40.cubeList.add(new ModelBox(cube_r40, 0, 0, -1.5F, -8.0F, -10.5F, 10, 16, 21, 0.0F, false));

		cube_r41 = new ModelRenderer(this);
		cube_r41.setRotationPoint(-11.6869F, -35.6016F, 25.7915F);
		body_audi.addChild(cube_r41);
		setRotationAngle(cube_r41, 0.3927F, 0.4363F, -1.309F);
		cube_r41.cubeList.add(new ModelBox(cube_r41, 186, 0, 0.0F, 0.5F, -3.5F, 5, 4, 16, 0.0F, false));

		cube_r42 = new ModelRenderer(this);
		cube_r42.setRotationPoint(-9.6869F, -35.3516F, 93.2085F);
		body_audi.addChild(cube_r42);
		setRotationAngle(cube_r42, -0.3927F, -0.4363F, -1.309F);
		cube_r42.cubeList.add(new ModelBox(cube_r42, 204, 74, -1.0F, -2.5F, -13.5F, 6, 5, 27, 0.0F, false));

		cube_r43 = new ModelRenderer(this);
		cube_r43.setRotationPoint(-10.5F, -33.75F, 112.5F);
		body_audi.addChild(cube_r43);
		setRotationAngle(cube_r43, 0.0873F, -0.0436F, -1.309F);
		cube_r43.cubeList.add(new ModelBox(cube_r43, 282, 282, -8.5F, 1.0F, -10.5F, 10, 7, 21, 0.0F, false));

		cube_r44 = new ModelRenderer(this);
		cube_r44.setRotationPoint(-2.5F, -30.0F, 112.5F);
		body_audi.addChild(cube_r44);
		setRotationAngle(cube_r44, 0.0873F, -0.1745F, -0.1309F);
		cube_r44.cubeList.add(new ModelBox(cube_r44, 202, 254, -8.5F, -2.0F, -10.5F, 10, 10, 21, 0.0F, false));

		cube_r45 = new ModelRenderer(this);
		cube_r45.setRotationPoint(-1.5F, -15.0F, 113.5F);
		body_audi.addChild(cube_r45);
		setRotationAngle(cube_r45, 0.0873F, -0.1745F, 0.0F);
		cube_r45.cubeList.add(new ModelBox(cube_r45, 0, 114, -8.5F, -8.0F, -10.5F, 10, 16, 21, 0.0F, false));

		cube_r46 = new ModelRenderer(this);
		cube_r46.setRotationPoint(-61.25F, -13.0F, -21.5F);
		body_audi.addChild(cube_r46);
		setRotationAngle(cube_r46, -0.0436F, 0.0F, 0.0F);
		cube_r46.cubeList.add(new ModelBox(cube_r46, 264, 352, -3.5F, -10.0F, 0.5F, 5, 18, 10, 0.0F, false));
		cube_r46.cubeList.add(new ModelBox(cube_r46, 318, 352, 57.75F, -10.0F, 0.5F, 5, 18, 10, 0.0F, false));

		cube_r47 = new ModelRenderer(this);
		cube_r47.setRotationPoint(-2.0F, -13.5F, 49.0F);
		body_audi.addChild(cube_r47);
		setRotationAngle(cube_r47, 0.0F, 0.0F, -0.0873F);
		cube_r47.cubeList.add(new ModelBox(cube_r47, 282, 73, -4.0F, 7.5F, -37.0F, 8, 2, 68, 0.0F, false));

		cube_r48 = new ModelRenderer(this);
		cube_r48.setRotationPoint(-60.8799F, -11.4535F, 46.0F);
		body_audi.addChild(cube_r48);
		setRotationAngle(cube_r48, 0.0F, 0.0F, -0.0436F);
		cube_r48.cubeList.add(new ModelBox(cube_r48, 0, 266, -3.0F, -5.5F, -34.0F, 6, 11, 68, 0.0F, false));

		cube_r49 = new ModelRenderer(this);
		cube_r49.setRotationPoint(-2.25F, -8.5F, 49.0F);
		body_audi.addChild(cube_r49);
		setRotationAngle(cube_r49, 0.0F, 0.0F, 0.0436F);
		cube_r49.cubeList.add(new ModelBox(cube_r49, 202, 266, -2.0F, -8.5F, -37.0F, 6, 11, 68, 0.0F, false));

		cube_r50 = new ModelRenderer(this);
		cube_r50.setRotationPoint(-60.0F, -18.5F, 49.0F);
		body_audi.addChild(cube_r50);
		setRotationAngle(cube_r50, 0.0F, 0.0F, 0.1745F);
		cube_r50.cubeList.add(new ModelBox(cube_r50, 202, 61, -4.0F, -9.5F, -37.0F, 6, 12, 68, 0.0F, false));

		cube_r51 = new ModelRenderer(this);
		cube_r51.setRotationPoint(-64.4803F, -30.0858F, 22.5F);
		body_audi.addChild(cube_r51);
		setRotationAngle(cube_r51, 0.0F, 0.1745F, 0.1745F);
		cube_r51.cubeList.add(new ModelBox(cube_r51, 246, 312, -3.5F, -2.0F, -2.5F, 7, 4, 5, 0.0F, false));

		cube_r52 = new ModelRenderer(this);
		cube_r52.setRotationPoint(2.4803F, -30.0858F, 22.5F);
		body_audi.addChild(cube_r52);
		setRotationAngle(cube_r52, 0.0F, -0.1745F, -0.1745F);
		cube_r52.cubeList.add(new ModelBox(cube_r52, 323, 293, -3.5F, -2.0F, -2.5F, 7, 4, 5, 0.0F, false));

		cube_r53 = new ModelRenderer(this);
		cube_r53.setRotationPoint(-2.0F, -18.5F, 49.0F);
		body_audi.addChild(cube_r53);
		setRotationAngle(cube_r53, 0.0F, 0.0F, -0.1745F);
		cube_r53.cubeList.add(new ModelBox(cube_r53, 122, 254, -2.0F, -9.5F, -37.0F, 6, 12, 68, 0.0F, false));

		cube_r54 = new ModelRenderer(this);
		cube_r54.setRotationPoint(-51.0F, -24.6107F, -27.0475F);
		body_audi.addChild(cube_r54);
		setRotationAngle(cube_r54, 0.2618F, 0.3054F, 0.0F);
		cube_r54.cubeList.add(new ModelBox(cube_r54, 0, 189, -10.0F, -1.5F, -2.0F, 18, 3, 9, 0.0F, false));

		cube_r55 = new ModelRenderer(this);
		cube_r55.setRotationPoint(-12.0F, -24.6107F, -27.0475F);
		body_audi.addChild(cube_r55);
		setRotationAngle(cube_r55, 0.2618F, -0.3054F, 0.0F);
		cube_r55.cubeList.add(new ModelBox(cube_r55, 134, 285, -8.0F, -1.5F, -2.0F, 18, 3, 9, 0.0F, false));

		cube_r56 = new ModelRenderer(this);
		cube_r56.setRotationPoint(-33.5F, -25.75F, -19.5F);
		body_audi.addChild(cube_r56);
		setRotationAngle(cube_r56, 0.2618F, 0.0F, 0.0F);
		cube_r56.cubeList.add(new ModelBox(cube_r56, 101, 142, -10.5F, -3.0F, -12.0F, 25, 3, 9, 0.0F, false));

		cube_r57 = new ModelRenderer(this);
		cube_r57.setRotationPoint(-33.5F, -26.0F, -18.0F);
		body_audi.addChild(cube_r57);
		setRotationAngle(cube_r57, 0.2618F, 0.0F, 0.0F);
		cube_r57.cubeList.add(new ModelBox(cube_r57, 202, 177, -27.5F, -3.0F, -5.0F, 59, 3, 8, 0.0F, false));

		cube_r58 = new ModelRenderer(this);
		cube_r58.setRotationPoint(-59.75F, -23.771F, -3.7935F);
		body_audi.addChild(cube_r58);
		setRotationAngle(cube_r58, 0.1309F, -0.0436F, 0.3927F);
		cube_r58.cubeList.add(new ModelBox(cube_r58, 0, 73, -3.5F, -4.5F, -19.0F, 7, 6, 35, 0.0F, false));

		cube_r59 = new ModelRenderer(this);
		cube_r59.setRotationPoint(-3.25F, -23.771F, -3.7935F);
		body_audi.addChild(cube_r59);
		setRotationAngle(cube_r59, 0.1309F, 0.0436F, -0.3927F);
		cube_r59.cubeList.add(new ModelBox(cube_r59, 101, 101, -3.5F, -4.5F, -19.0F, 7, 6, 35, 0.0F, false));

		cube_r60 = new ModelRenderer(this);
		cube_r60.setRotationPoint(-33.5F, -27.0F, -7.0F);
		body_audi.addChild(cube_r60);
		setRotationAngle(cube_r60, 0.0436F, 0.0F, 0.0F);
		cube_r60.cubeList.add(new ModelBox(cube_r60, 202, 143, -27.5F, -3.0F, -9.0F, 59, 6, 28, 0.0F, false));

		cube_r61 = new ModelRenderer(this);
		cube_r61.setRotationPoint(-31.0F, -9.5F, 101.5F);
		body_audi.addChild(cube_r61);
		setRotationAngle(cube_r61, 0.0873F, 0.0F, 0.0F);
		cube_r61.cubeList.add(new ModelBox(cube_r61, 186, 0, -27.0F, -15.5F, -21.5F, 54, 18, 43, 0.0F, false));

		bone128 = new ModelRenderer(this);
		bone128.setRotationPoint(-54.9791F, -13.0F, 123.5206F);
		body_audi.addChild(bone128);
		bone128.cubeList.add(new ModelBox(bone128, 282, 90, 12.0F, -5.0F, -1.25F, 24, 10, 6, 0.0F, false));

		cube_r62 = new ModelRenderer(this);
		cube_r62.setRotationPoint(5.0F, 0.0F, 0.0F);
		bone128.addChild(cube_r62);
		setRotationAngle(cube_r62, 0.0F, -0.1745F, 0.0F);
		cube_r62.cubeList.add(new ModelBox(cube_r62, 325, 195, -9.0F, -5.0F, -3.0F, 17, 10, 6, 0.0F, false));

		cube_r63 = new ModelRenderer(this);
		cube_r63.setRotationPoint(42.9581F, 0.0F, 0.0F);
		bone128.addChild(cube_r63);
		setRotationAngle(cube_r63, 0.0F, 0.1745F, 0.0F);
		cube_r63.cubeList.add(new ModelBox(cube_r63, 0, 317, -8.0F, -5.0F, -3.0F, 17, 10, 6, 0.0F, false));

		bone131 = new ModelRenderer(this);
		bone131.setRotationPoint(-52.9791F, -10.0F, -32.5206F);
		body_audi.addChild(bone131);
		

		cube_r64 = new ModelRenderer(this);
		cube_r64.setRotationPoint(-7.5968F, -14.0F, 10.2026F);
		bone131.addChild(cube_r64);
		setRotationAngle(cube_r64, -0.1309F, -0.4363F, 0.1309F);
		cube_r64.cubeList.add(new ModelBox(cube_r64, 41, 41, -2.5F, 1.0F, -3.5F, 5, 10, 7, 0.0F, false));

		cube_r65 = new ModelRenderer(this);
		cube_r65.setRotationPoint(-8.5968F, -5.0F, 9.2026F);
		bone131.addChild(cube_r65);
		setRotationAngle(cube_r65, 0.0F, -0.4363F, -0.1309F);
		cube_r65.cubeList.add(new ModelBox(cube_r65, 152, 201, -2.5F, 1.0F, -3.5F, 5, 9, 7, 0.0F, false));

		cube_r66 = new ModelRenderer(this);
		cube_r66.setRotationPoint(9.0082F, 4.0339F, 1.6189F);
		bone131.addChild(cube_r66);
		setRotationAngle(cube_r66, -0.0873F, 0.3491F, -0.2618F);
		cube_r66.cubeList.add(new ModelBox(cube_r66, 101, 129, -2.5F, -0.5F, -3.0F, 5, 1, 6, 0.0F, false));

		cube_r67 = new ModelRenderer(this);
		cube_r67.setRotationPoint(0.4698F, 4.5F, 6.829F);
		bone131.addChild(cube_r67);
		setRotationAngle(cube_r67, 0.0F, 0.3491F, 0.0F);
		cube_r67.cubeList.add(new ModelBox(cube_r67, 191, 201, -8.5F, -17.5F, -3.0F, 18, 17, 6, 0.0F, false));

		cube_r68 = new ModelRenderer(this);
		cube_r68.setRotationPoint(2.4698F, -1.5F, 4.829F);
		bone131.addChild(cube_r68);
		setRotationAngle(cube_r68, -0.0873F, 0.3491F, -0.1745F);
		cube_r68.cubeList.add(new ModelBox(cube_r68, 345, 211, 0.5F, -5.5F, -3.0F, 8, 6, 6, 0.0F, false));

		cube_r69 = new ModelRenderer(this);
		cube_r69.setRotationPoint(0.4698F, -1.5F, 4.829F);
		bone131.addChild(cube_r69);
		setRotationAngle(cube_r69, -0.0873F, 0.3491F, 0.0F);
		cube_r69.cubeList.add(new ModelBox(cube_r69, 348, 143, -8.5F, -5.5F, -3.0F, 18, 6, 6, 0.0F, false));

		cube_r70 = new ModelRenderer(this);
		cube_r70.setRotationPoint(0.4698F, 4.5F, 4.829F);
		bone131.addChild(cube_r70);
		setRotationAngle(cube_r70, 0.0F, 0.3491F, 0.0F);
		cube_r70.cubeList.add(new ModelBox(cube_r70, 202, 312, -8.5F, -0.5F, -3.0F, 16, 1, 6, 0.0F, false));

		cube_r71 = new ModelRenderer(this);
		cube_r71.setRotationPoint(50.5549F, -14.0F, 10.2026F);
		bone131.addChild(cube_r71);
		setRotationAngle(cube_r71, -0.1309F, 0.4363F, -0.1309F);
		cube_r71.cubeList.add(new ModelBox(cube_r71, 41, 0, -2.5F, 1.0F, -3.5F, 5, 10, 7, 0.0F, false));

		cube_r72 = new ModelRenderer(this);
		cube_r72.setRotationPoint(51.5549F, -5.0F, 9.2026F);
		bone131.addChild(cube_r72);
		setRotationAngle(cube_r72, 0.0F, 0.4363F, 0.1309F);
		cube_r72.cubeList.add(new ModelBox(cube_r72, 243, 73, -2.5F, 1.0F, -3.5F, 5, 9, 7, 0.0F, false));

		cube_r73 = new ModelRenderer(this);
		cube_r73.setRotationPoint(42.4883F, 4.5F, 6.829F);
		bone131.addChild(cube_r73);
		setRotationAngle(cube_r73, 0.0F, -0.3491F, 0.0F);
		cube_r73.cubeList.add(new ModelBox(cube_r73, 0, 287, -9.5F, -17.5F, -3.0F, 18, 17, 6, 0.0F, false));

		cube_r74 = new ModelRenderer(this);
		cube_r74.setRotationPoint(42.4883F, -1.5F, 4.829F);
		bone131.addChild(cube_r74);
		setRotationAngle(cube_r74, -0.0873F, -0.3491F, 0.0F);
		cube_r74.cubeList.add(new ModelBox(cube_r74, 243, 254, -9.5F, -5.5F, -3.0F, 18, 6, 6, 0.0F, false));

		cube_r75 = new ModelRenderer(this);
		cube_r75.setRotationPoint(40.4883F, -1.5F, 4.829F);
		bone131.addChild(cube_r75);
		setRotationAngle(cube_r75, -0.0873F, -0.3491F, 0.1745F);
		cube_r75.cubeList.add(new ModelBox(cube_r75, 60, 378, -8.5F, -5.5F, -3.0F, 8, 6, 6, 0.0F, false));

		cube_r76 = new ModelRenderer(this);
		cube_r76.setRotationPoint(42.4883F, 4.5F, 4.829F);
		bone131.addChild(cube_r76);
		setRotationAngle(cube_r76, 0.0F, -0.3491F, 0.0F);
		cube_r76.cubeList.add(new ModelBox(cube_r76, 45, 189, -7.5F, -0.5F, -3.0F, 16, 1, 6, 0.0F, false));

		cube_r77 = new ModelRenderer(this);
		cube_r77.setRotationPoint(33.9499F, 4.0339F, 1.6189F);
		bone131.addChild(cube_r77);
		setRotationAngle(cube_r77, -0.0873F, -0.3491F, 0.2618F);
		cube_r77.cubeList.add(new ModelBox(cube_r77, 244, 61, -2.5F, -0.5F, -3.0F, 5, 1, 6, 0.0F, false));

		grille = new ModelRenderer(this);
		grille.setRotationPoint(21.5F, -11.0F, 3.75F);
		bone131.addChild(grille);
		grille.cubeList.add(new ModelBox(grille, 132, 308, -11.5F, 11.5F, -4.5F, 23, 1, 6, 0.0F, false));
		grille.cubeList.add(new ModelBox(grille, 41, 114, -11.5F, -2.5F, 1.5F, 23, 17, 1, 0.0F, false));
		grille.cubeList.add(new ModelBox(grille, 80, 307, -11.5F, 9.5F, -4.25F, 23, 1, 6, 0.0F, false));
		grille.cubeList.add(new ModelBox(grille, 202, 305, -11.5F, 7.5F, -4.0F, 23, 1, 6, 0.0F, false));
		grille.cubeList.add(new ModelBox(grille, 293, 188, -11.5F, 5.5F, -3.75F, 23, 1, 6, 0.0F, false));
		grille.cubeList.add(new ModelBox(grille, 41, 259, -11.5F, 3.5F, -3.5F, 23, 1, 6, 0.0F, false));
		grille.cubeList.add(new ModelBox(grille, 186, 61, -11.5F, 1.5F, -3.25F, 23, 1, 6, 0.0F, false));
		grille.cubeList.add(new ModelBox(grille, 101, 154, -11.5F, -0.5F, -3.0F, 23, 1, 6, 0.0F, false));

		cube_r78 = new ModelRenderer(this);
		cube_r78.setRotationPoint(0.0F, 1.0F, -3.25F);
		grille.addChild(cube_r78);
		setRotationAngle(cube_r78, -0.1309F, 0.0F, 0.0F);
		cube_r78.cubeList.add(new ModelBox(cube_r78, 41, 17, -5.0F, -1.5F, -0.5F, 10, 3, 1, 0.0F, false));

		bone132 = new ModelRenderer(this);
		bone132.setRotationPoint(11.5F, 4.5F, -1.5F);
		grille.addChild(bone132);
		bone132.cubeList.add(new ModelBox(bone132, 0, 310, -23.0F, 9.5F, -4.0F, 23, 1, 6, 0.0F, false));
		bone132.cubeList.add(new ModelBox(bone132, 0, 151, -24.0F, -8.5F, -1.75F, 25, 2, 6, 0.0F, false));

		cube_r79 = new ModelRenderer(this);
		cube_r79.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone132.addChild(cube_r79);
		setRotationAngle(cube_r79, -0.1309F, 0.0F, 0.1309F);
		cube_r79.cubeList.add(new ModelBox(cube_r79, 0, 0, -1.0F, -4.5F, -3.0F, 3, 15, 6, 0.0F, false));

		cube_r80 = new ModelRenderer(this);
		cube_r80.setRotationPoint(-24.0F, -3.0F, 0.5F);
		bone132.addChild(cube_r80);
		setRotationAngle(cube_r80, -0.1309F, 0.0F, 0.3927F);
		cube_r80.cubeList.add(new ModelBox(cube_r80, 73, 93, -2.0F, -4.5F, -3.0F, 3, 4, 6, 0.0F, false));

		cube_r81 = new ModelRenderer(this);
		cube_r81.setRotationPoint(-23.0F, 0.0F, 0.0F);
		bone132.addChild(cube_r81);
		setRotationAngle(cube_r81, -0.1309F, 0.0F, -0.1309F);
		cube_r81.cubeList.add(new ModelBox(cube_r81, 0, 37, -2.0F, -4.5F, -3.0F, 3, 15, 6, 0.0F, false));

		cube_r82 = new ModelRenderer(this);
		cube_r82.setRotationPoint(1.0F, -3.0F, 0.5F);
		bone132.addChild(cube_r82);
		setRotationAngle(cube_r82, -0.1309F, 0.0F, -0.3927F);
		cube_r82.cubeList.add(new ModelBox(cube_r82, 174, 121, -1.0F, -4.5F, -3.0F, 3, 4, 6, 0.0F, false));

		bone129 = new ModelRenderer(this);
		bone129.setRotationPoint(-54.9791F, -22.0F, 121.5206F);
		body_audi.addChild(bone129);
		setRotationAngle(bone129, 0.0873F, 0.0F, 0.0F);
		bone129.cubeList.add(new ModelBox(bone129, 202, 106, 12.0F, -4.0F, -1.25F, 24, 11, 6, 0.0F, false));

		cube_r83 = new ModelRenderer(this);
		cube_r83.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone129.addChild(cube_r83);
		setRotationAngle(cube_r83, 0.0F, -0.1745F, 0.0F);
		cube_r83.cubeList.add(new ModelBox(cube_r83, 190, 233, -4.076F, -4.0F, -3.8682F, 17, 11, 6, 0.0F, false));

		cube_r84 = new ModelRenderer(this);
		cube_r84.setRotationPoint(42.9581F, 0.0F, 0.0F);
		bone129.addChild(cube_r84);
		setRotationAngle(cube_r84, 0.0F, 0.1745F, 0.0F);
		cube_r84.cubeList.add(new ModelBox(cube_r84, 282, 310, -8.0F, -4.0F, -3.0F, 17, 11, 6, 0.0F, false));

		bone130 = new ModelRenderer(this);
		bone130.setRotationPoint(-33.493F, -29.6667F, 123.104F);
		body_audi.addChild(bone130);
		setRotationAngle(bone130, 0.0873F, 0.0F, 0.0F);
		bone130.cubeList.add(new ModelBox(bone130, 127, 173, -9.486F, -4.3333F, -1.8333F, 24, 8, 6, 0.0F, false));

		cube_r85 = new ModelRenderer(this);
		cube_r85.setRotationPoint(-21.486F, -1.3333F, -0.5833F);
		bone130.addChild(cube_r85);
		setRotationAngle(cube_r85, 0.0F, -0.1745F, 0.0F);
		cube_r85.cubeList.add(new ModelBox(cube_r85, 238, 285, -3.9332F, 1.0757F, -2.971F, 9, 7, 6, 0.0F, false));
		cube_r85.cubeList.add(new ModelBox(cube_r85, 41, 266, 5.0668F, 1.0757F, -2.971F, 7, 6, 6, 0.0F, false));
		cube_r85.cubeList.add(new ModelBox(cube_r85, 152, 365, -3.076F, -2.0F, -3.8682F, 16, 7, 6, 0.0F, false));

		cube_r86 = new ModelRenderer(this);
		cube_r86.setRotationPoint(21.4721F, -1.3333F, -0.5833F);
		bone130.addChild(cube_r86);
		setRotationAngle(cube_r86, 0.0F, 0.1745F, 0.0F);
		cube_r86.cubeList.add(new ModelBox(cube_r86, 243, 89, -7.1428F, 1.0757F, -2.1028F, 7, 6, 6, 0.0F, false));
		cube_r86.cubeList.add(new ModelBox(cube_r86, 317, 66, -0.1428F, 1.0757F, -2.1028F, 9, 7, 6, 0.0F, false));
		cube_r86.cubeList.add(new ModelBox(cube_r86, 196, 365, -8.0F, -2.0F, -3.0F, 16, 7, 6, 0.0F, false));

		cube_r87 = new ModelRenderer(this);
		cube_r87.setRotationPoint(0.014F, -2.5063F, -8.7268F);
		bone130.addChild(cube_r87);
		setRotationAngle(cube_r87, -0.1309F, 0.0F, 0.0F);
		cube_r87.cubeList.add(new ModelBox(cube_r87, 101, 73, -20.5F, -4.0F, -10.0F, 45, 8, 20, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		EntityTestVes vehicle = ((EntityTestVes) entity);
		
		wheel.rotateAngleY = (float) vehicle.steerangle;
		wheel4.rotateAngleY = (float) vehicle.steerangle;
		interior.isHidden = true;
		interior_wheel.isHidden = true;
		body_audi.isHidden = true;
		bone5.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}