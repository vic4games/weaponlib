// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

package com.jimholden.conomy.items.models.jackets;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
public class Gorka4Jacket extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer gorkabody;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer gorkarightarm;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer gorkaleftarm;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer gorkarightleg;
	private final ModelRenderer gorkaleftleg;

	public Gorka4Jacket() {
		textureWidth = 128;
		textureHeight = 128;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightarm.cubeList.add(new ModelBox(rightarm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftarm.cubeList.add(new ModelBox(leftarm, 16, 40, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		

		gorkabody = new ModelRenderer(this);
		gorkabody.setRotationPoint(0.0F, 0.0F, 0.0F);
		gorkabody.cubeList.add(new ModelBox(gorkabody, 48, 6, -4.0F, 0.0F, 1.4F, 8, 9, 1, 0.0F, false));
		gorkabody.cubeList.add(new ModelBox(gorkabody, 51, 51, -4.0F, 0.0F, -2.4F, 8, 9, 1, 0.0F, false));
		gorkabody.cubeList.add(new ModelBox(gorkabody, 0, 65, -4.0F, 10.9829F, -2.6611F, 8, 1, 1, 0.0F, false));
		gorkabody.cubeList.add(new ModelBox(gorkabody, 10, 52, 3.4F, 0.0F, -2.0F, 1, 9, 4, 0.0F, false));
		gorkabody.cubeList.add(new ModelBox(gorkabody, 0, 48, -4.4F, 0.0F, -2.0F, 1, 9, 4, 0.0F, false));
		gorkabody.cubeList.add(new ModelBox(gorkabody, 62, 43, -4.0F, 10.9829F, 1.6611F, 8, 1, 1, 0.0F, false));
		gorkabody.cubeList.add(new ModelBox(gorkabody, 72, 0, -4.6611F, 10.9829F, -2.0F, 1, 1, 4, 0.0F, false));
		gorkabody.cubeList.add(new ModelBox(gorkabody, 6, 72, 3.6611F, 10.9829F, -2.0F, 1, 1, 4, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(4.0F, 0.9901F, -1.9507F);
		gorkabody.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.1309F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 62, 17, -8.0F, 8.0F, 0.6F, 8, 2, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(2.3821F, -10.9242F, -4.2611F);
		gorkabody.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, -0.1309F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 59, 67, -1.6F, 20.0171F, 2.2611F, 1, 2, 4, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.5145F, -17.6032F, -4.2611F);
		gorkabody.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, 0.1309F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 66, 6, -1.4F, 27.0171F, 2.2611F, 1, 2, 4, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(4.0F, 0.0F, -3.25F);
		gorkabody.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.2618F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 56, 28, -8.0F, -1.0F, 0.6F, 8, 2, 1, 0.07F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(4.0F, 1.1207F, 0.9592F);
		gorkabody.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.1309F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 62, 40, -8.0F, 8.0F, -0.6F, 8, 2, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.4811F, -25.3695F, 3.8863F);
		gorkabody.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.2182F, 0.0F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 48, 0, -4.9811F, 22.5945F, -7.2113F, 9, 3, 3, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-4.0F, -1.75F, 1.5F);
		gorkabody.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.1309F, 0.0F, -0.0873F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 24, 16, -1.0F, 0.0F, -4.0F, 1, 2, 6, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(5.047F, -1.6641F, 1.5F);
		gorkabody.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.1309F, 0.0F, 0.1309F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 58, 32, -1.0F, 0.0F, -4.0F, 1, 2, 6, 0.0F, false));

		gorkarightarm = new ModelRenderer(this);
		gorkarightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 56, 16, -3.3F, -2.0F, -2.0F, 1, 8, 4, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 0, 67, -3.0F, -2.0F, 1.3F, 4, 8, 1, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 49, 66, -3.0F, -2.0F, -2.3F, 4, 8, 1, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 62, 62, -3.0F, 9.1F, -2.0F, 4, 1, 4, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 20, 56, 0.3F, -2.0F, -2.0F, 1, 8, 4, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 71, 71, -3.2F, 6.4F, -2.0F, 1, 1, 4, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 72, 22, -3.0F, 6.4F, -2.2F, 4, 1, 1, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 72, 31, -3.0F, 6.4F, 1.2F, 4, 1, 1, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 70, 45, 0.2F, 6.4F, -2.0F, 1, 1, 4, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 26, 67, -3.1F, 7.0F, -2.0F, 1, 3, 4, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 40, 32, -3.0F, 7.0F, 1.1F, 4, 3, 1, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 32, 16, -3.0F, 7.0F, -2.1F, 4, 3, 1, 0.0F, false));
		gorkarightarm.cubeList.add(new ModelBox(gorkarightarm, 66, 31, 0.1F, 7.0F, -2.0F, 1, 3, 4, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(5.0335F, 0.0485F, 1.0F);
		gorkarightarm.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 0.0F, 0.6458F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 32, 71, -0.4F, 7.0F, -3.0F, 1, 1, 4, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(3.4452F, 1.68F, 1.0F);
		gorkarightarm.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, 0.0F, 0.9512F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 71, 56, -0.4F, 7.0F, -3.0F, 1, 1, 4, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, 7.0651F, -2.775F);
		gorkarightarm.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.6109F, 0.0F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 72, 20, -3.0F, -0.6F, 1.0F, 4, 1, 1, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(0.0F, 5.3443F, 0.3176F);
		gorkarightarm.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.6109F, 0.0F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 72, 33, -3.0F, -0.6F, 1.0F, 4, 1, 1, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(-4.0F, -3.0F, -1.25F);
		gorkarightarm.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, 0.0F, -0.0873F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 62, 12, 0.9F, 0.8F, -0.75F, 4, 1, 4, 0.0F, false));

		gorkaleftarm = new ModelRenderer(this);
		gorkaleftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 39, 66, -1.0F, -2.0F, 1.3F, 4, 8, 1, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 40, 54, 2.3F, -2.0F, -2.0F, 1, 8, 4, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 52, 38, -1.3F, -2.0F, -2.0F, 1, 8, 4, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 30, 58, -1.0F, -2.0F, -2.3F, 4, 8, 1, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 12, 72, -1.0F, 6.4F, 1.2F, 4, 1, 1, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 65, 69, 2.2F, 6.4F, -2.0F, 1, 1, 4, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 65, 57, -1.2F, 6.4F, -2.0F, 1, 1, 4, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 71, 69, -1.0F, 6.4F, -2.2F, 4, 1, 1, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 12, 32, -1.0F, 7.0F, -2.1F, 4, 3, 1, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 66, 20, 2.1F, 7.0F, -2.0F, 1, 3, 4, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 14, 65, -1.1F, 7.0F, -2.0F, 1, 3, 4, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 20, 16, -1.0F, 7.0F, 1.1F, 4, 3, 1, 0.0F, false));
		gorkaleftarm.cubeList.add(new ModelBox(gorkaleftarm, 50, 61, -1.0F, 9.1F, -2.0F, 4, 1, 4, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(-2.0F, -3.0F, -1.25F);
		gorkaleftarm.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.0F, 0.0873F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 58, 46, 1.1F, 0.2F, -0.75F, 4, 1, 4, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(2.0F, 7.0651F, -2.775F);
		gorkaleftarm.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.6109F, 0.0F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 72, 7, -3.0F, -0.6F, 1.0F, 4, 1, 1, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(2.0F, 5.3443F, 0.3176F);
		gorkaleftarm.addChild(cube_r16);
		setRotationAngle(cube_r16, -0.6109F, 0.0F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 72, 5, -3.0F, -0.6F, 1.0F, 4, 1, 1, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(5.4452F, 1.68F, 1.0F);
		gorkaleftarm.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.0F, 0.0F, 0.9512F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 69, 51, -0.4F, 7.0F, -3.0F, 1, 1, 4, 0.0F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(7.0335F, 0.0485F, 1.0F);
		gorkaleftarm.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.0F, 0.0F, 0.6458F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 20, 70, -0.4F, 7.0F, -3.0F, 1, 1, 4, 0.0F, false));

		gorkarightleg = new ModelRenderer(this);
		gorkarightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		

		gorkaleftleg = new ModelRenderer(this);
		gorkaleftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		
		
		bipedLeftArm = gorkaleftarm;
		bipedRightArm = gorkarightarm;
		bipedBody = gorkabody;
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