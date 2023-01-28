package com.jimholden.conomy.items.models.glasses;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class SunglassesModel extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer glasses;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone3;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer bone4;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone15;
	private final ModelRenderer bone14;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;

	public SunglassesModel() {
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

		glasses = new ModelRenderer(this);
		glasses.setRotationPoint(0.0F, 0.0F, 0.0F);
		
		bipedHeadwear.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedBody.isHidden = true;
		
		bipedHead = glasses;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, -4.4F, -4.4F);
		glasses.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.0175F, 0.4363F);
		bone.cubeList.add(new ModelBox(bone, 0, 34, -0.8F, -0.8F, -0.2F, 1, 1, 1, -0.2F, false));
		bone.cubeList.add(new ModelBox(bone, 16, 32, -1.2F, -0.8F, -0.2F, 1, 1, 1, -0.2F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -4.4F, -4.4F);
		glasses.addChild(bone2);
		setRotationAngle(bone2, 0.0F, -0.0175F, -0.4363F);
		bone2.cubeList.add(new ModelBox(bone2, 20, 32, -0.2F, -0.8F, -0.2F, 1, 1, 1, -0.2F, false));
		bone2.cubeList.add(new ModelBox(bone2, 12, 32, 0.2F, -0.8F, -0.2F, 1, 1, 1, -0.2F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.6F, -2.9F, -4.4F);
		glasses.addChild(bone7);
		setRotationAngle(bone7, 0.0F, -0.0175F, -0.0524F);
		bone7.cubeList.add(new ModelBox(bone7, 24, 2, -0.2F, -0.8F, -0.18F, 3, 1, 1, -0.2F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(-0.6F, -2.9F, -4.4F);
		glasses.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 0.0175F, 0.0524F);
		bone8.cubeList.add(new ModelBox(bone8, 24, 0, -2.8F, -0.8F, -0.18F, 3, 1, 1, -0.2F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(-0.1F, -4.4F, -4.4F);
		glasses.addChild(bone5);
		setRotationAngle(bone5, 0.0F, -0.0175F, 1.1519F);
		bone5.cubeList.add(new ModelBox(bone5, 29, 21, -0.2F, -0.8F, -0.2F, 2, 1, 1, -0.2F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.1F, -4.4F, -4.4F);
		glasses.addChild(bone6);
		setRotationAngle(bone6, 0.0F, 0.0175F, -1.1519F);
		bone6.cubeList.add(new ModelBox(bone6, 29, 19, -1.8F, -0.8F, -0.2F, 2, 1, 1, -0.2F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.6F, -5.35F, -4.35F);
		glasses.addChild(bone3);
		setRotationAngle(bone3, 0.0F, -0.0175F, 0.1222F);
		bone3.cubeList.add(new ModelBox(bone3, 24, 4, -0.2F, -0.2F, -0.25F, 3, 1, 1, -0.2F, false));
		bone3.cubeList.add(new ModelBox(bone3, 24, 24, 2.4F, -0.2F, -0.25F, 1, 1, 1, -0.2F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.4F, -4.75F, -4.25F);
		glasses.addChild(bone16);
		setRotationAngle(bone16, 0.0F, -0.0175F, -0.0349F);
		bone16.cubeList.add(new ModelBox(bone16, 0, 3, -0.2F, -0.2F, -0.3F, 3, 2, 1, -0.2F, false));
		bone16.cubeList.add(new ModelBox(bone16, 0, 18, 2.1F, 0.1F, -0.3F, 1, 1, 1, -0.2F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(-0.3F, -4.75F, -4.25F);
		glasses.addChild(bone17);
		setRotationAngle(bone17, 0.0F, 0.0175F, 0.0349F);
		bone17.cubeList.add(new ModelBox(bone17, 0, 0, -2.9F, -0.2F, -0.3F, 3, 2, 1, -0.2F, false));
		bone17.cubeList.add(new ModelBox(bone17, 0, 16, -3.3F, 0.1F, -0.3F, 1, 1, 1, -0.2F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-0.6F, -5.35F, -4.4F);
		glasses.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0175F, -0.1222F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 6, -2.8F, -0.2F, -0.2F, 3, 1, 1, -0.2F, false));
		bone4.cubeList.add(new ModelBox(bone4, 24, 26, -3.4F, -0.2F, -0.2F, 1, 1, 1, -0.2F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(-2.8F, -4.95F, -4.3F);
		glasses.addChild(bone11);
		setRotationAngle(bone11, 0.0F, 0.192F, -0.1222F);
		bone11.cubeList.add(new ModelBox(bone11, 24, 20, -1.8F, -0.2F, -0.2F, 2, 1, 1, -0.2F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(2.8F, -4.95F, -4.3F);
		glasses.addChild(bone12);
		setRotationAngle(bone12, 0.0F, -0.192F, 0.1222F);
		bone12.cubeList.add(new ModelBox(bone12, 24, 6, -0.2F, -0.2F, -0.2F, 2, 1, 1, -0.2F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(4.4F, -4.75F, -3.9F);
		glasses.addChild(bone13);
		setRotationAngle(bone13, 0.0F, -1.5882F, 0.1222F);
		bone13.cubeList.add(new ModelBox(bone13, 20, 18, -0.2F, -0.2F, -0.2F, 4, 1, 1, -0.2F, false));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(4.3F, -4.75F, -0.3F);
		glasses.addChild(bone15);
		setRotationAngle(bone15, -0.9076F, 0.0F, 0.0F);
		bone15.cubeList.add(new ModelBox(bone15, 0, 32, -0.8F, -0.2F, -0.2F, 1, 1, 1, -0.2F, false));
		bone15.cubeList.add(new ModelBox(bone15, 31, 1, -8.8F, -0.2F, -0.2F, 1, 1, 1, -0.2F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(-4.4F, -4.75F, -3.9F);
		glasses.addChild(bone14);
		setRotationAngle(bone14, 0.0F, 1.5882F, -0.1222F);
		bone14.cubeList.add(new ModelBox(bone14, 20, 16, -3.8F, -0.2F, -0.2F, 4, 1, 1, -0.2F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(-3.8F, -4.65F, -4.4F);
		glasses.addChild(bone9);
		setRotationAngle(bone9, 0.0F, 0.0175F, -1.7977F);
		bone9.cubeList.add(new ModelBox(bone9, 29, 17, -1.8F, -0.2F, -0.15F, 2, 1, 1, -0.2F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(3.8F, -4.65F, -4.4F);
		glasses.addChild(bone10);
		setRotationAngle(bone10, 0.0F, -0.0175F, 1.7977F);
		bone10.cubeList.add(new ModelBox(bone10, 24, 22, -0.2F, -0.2F, -0.15F, 2, 1, 1, -0.2F, false));
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