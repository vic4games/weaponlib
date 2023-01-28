package com.jimholden.conomy.items.models.rigs;

import com.jimholden.conomy.items.RigItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import scala.reflect.internal.Trees.This;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class AtlasT7 extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer vest;
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
	private final ModelRenderer vestleftarm;
	private final ModelRenderer vestrightarm;

	public AtlasT7() {
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

		vest = new ModelRenderer(this);
		vest.setRotationPoint(0.0F, 0.0F, 0.0F);
		vest.cubeList.add(new ModelBox(vest, 16, 40, -3.5F, 2.5F, -2.75F, 7, 8, 1, 0.0F, false));
		vest.cubeList.add(new ModelBox(vest, 47, 47, 2.2F, 6.85F, -2.75F, 2, 3, 5, 0.1F, false));
		vest.cubeList.add(new ModelBox(vest, 27, 47, -4.2F, 6.85F, -2.75F, 2, 3, 5, 0.1F, false));
		vest.cubeList.add(new ModelBox(vest, 24, 20, -2.5F, 10.1F, -3.1F, 5, 3, 1, -0.2F, false));
		vest.cubeList.add(new ModelBox(vest, 8, 48, -1.0F, 12.7F, -3.1F, 2, 3, 1, -0.2F, false));
		vest.cubeList.add(new ModelBox(vest, 36, 16, -3.5F, 1.75F, 1.3F, 7, 9, 1, 0.0F, false));
		vest.cubeList.add(new ModelBox(vest, 24, 0, -2.5F, 9.65F, 1.8F, 5, 3, 1, -0.2F, false));
		vest.cubeList.add(new ModelBox(vest, 48, 0, -3.0F, 3.5F, 1.8F, 6, 7, 1, -0.1F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-3.3F, 1.75F, 2.65F);
		vest.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0175F, 0.0524F, -0.2443F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 24, 4, 0.0F, 0.0F, -1.0F, 2, 3, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-3.3F, 1.85F, 2.65F);
		vest.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.2618F, 0.0873F, -0.1396F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 52, 19, 0.0F, -2.0F, -1.0F, 2, 2, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(3.3F, 1.75F, 2.65F);
		vest.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0175F, -0.0524F, 0.2443F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 12, 32, -2.0F, 0.0F, -1.0F, 2, 3, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(3.3F, 1.85F, 2.65F);
		vest.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.2618F, -0.0873F, 0.1396F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 52, 22, -2.0F, -2.0F, -1.0F, 2, 2, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-1.5F, 2.5F, -2.65F);
		vest.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.1571F, -0.0349F, -0.1571F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 36, 26, -2.001F, -2.7F, 0.049F, 2, 1, 1, 0.0F, false));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 52, 41, -2.0F, -2.0F, 0.05F, 2, 2, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.5F, 2.5F, -2.65F);
		vest.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.1222F, 0.1222F, 0.9425F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 16, 0.0F, -2.0F, 0.0F, 1, 2, 1, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-0.5F, 2.5F, -2.65F);
		vest.addChild(cube_r7);
		setRotationAngle(cube_r7, -0.1222F, -0.1222F, -0.9425F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 24, 24, -1.0F, -2.0F, 0.0F, 1, 2, 1, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(1.5F, 2.5F, -2.65F);
		vest.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.1571F, 0.0349F, 0.1571F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 6, 0.001F, -2.7F, 0.049F, 2, 1, 1, 0.0F, false));
		cube_r8.cubeList.add(new ModelBox(cube_r8, 52, 38, 0.0F, -2.0F, 0.05F, 2, 2, 1, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-2.3F, 12.9F, -2.9F);
		vest.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 0.0F, -0.5236F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 31, 16, -0.2F, 2.2F, -0.199F, 1, 1, 1, -0.2F, false));
		cube_r9.cubeList.add(new ModelBox(cube_r9, 18, 32, -0.2F, -0.2F, -0.199F, 2, 3, 1, -0.2F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(2.3F, 12.9F, -2.9F);
		vest.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, 0.0F, 0.5236F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 32, -0.8F, 2.2F, -0.199F, 1, 1, 1, -0.2F, false));
		cube_r10.cubeList.add(new ModelBox(cube_r10, 16, 36, -1.8F, -0.2F, -0.199F, 2, 3, 1, -0.2F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(2.5F, 6.3F, -3.1F);
		vest.addChild(cube_r11);
		setRotationAngle(cube_r11, -0.0349F, 0.0F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 52, 14, -5.0F, 0.0F, 0.0F, 5, 4, 1, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-3.9F, -0.2F, -1.95F);
		vest.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 0.0698F, -0.0524F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 12, 49, 0.0F, 0.0F, -0.05F, 2, 1, 4, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(3.9F, -0.2F, -1.95F);
		vest.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, -0.0698F, 0.0524F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 37, 52, -2.0F, 0.0F, -0.05F, 2, 1, 4, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(1.5F, 2.6F, -2.05F);
		vest.addChild(cube_r14);
		setRotationAngle(cube_r14, 1.2915F, -0.7854F, -1.1694F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 20, 49, 0.0F, -2.0F, 0.0F, 2, 2, 1, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(-1.5F, 2.6F, -2.05F);
		vest.addChild(cube_r15);
		setRotationAngle(cube_r15, 1.2915F, 0.7854F, 1.1694F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 26, 49, -2.0F, -2.0F, 0.0F, 2, 2, 1, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-1.5F, 2.1F, 2.2F);
		vest.addChild(cube_r16);
		setRotationAngle(cube_r16, -0.2793F, 0.0F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 0, 0.0F, -2.0F, -1.0F, 3, 2, 1, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(-1.5F, 2.6F, -2.05F);
		vest.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.7505F, 0.0F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 3, 0.0F, -2.0F, 0.0F, 3, 2, 1, 0.0F, false));

		vestleftarm = new ModelRenderer(this);
		vestleftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		vestleftarm.cubeList.add(new ModelBox(vestleftarm, 0, 48, 1.0F, -2.0F, -2.0F, 2, 6, 4, 0.2F, false));
		vestleftarm.cubeList.add(new ModelBox(vestleftarm, 48, 26, -1.4F, -2.35F, -1.5F, 4, 1, 3, 0.0F, false));
		vestleftarm.cubeList.add(new ModelBox(vestleftarm, 48, 32, -1.0F, 2.0F, -2.001F, 2, 2, 4, 0.2F, false));

		vestrightarm = new ModelRenderer(this);
		vestrightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		vestrightarm.cubeList.add(new ModelBox(vestrightarm, 40, 26, -3.0F, -2.0F, -2.0F, 2, 6, 4, 0.2F, false));
		vestrightarm.cubeList.add(new ModelBox(vestrightarm, 48, 8, -1.0F, 2.0F, -2.001F, 2, 2, 4, 0.2F, false));
		vestrightarm.cubeList.add(new ModelBox(vestrightarm, 20, 16, -2.6F, -2.35F, -1.5F, 4, 1, 3, 0.0F, false));
		
		
		bipedLeftArm = vestleftarm;
		bipedRightArm  = vestrightarm;
		bipedBody = vest;
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