// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports
package com.jimholden.conomy.items.models.jackets;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class AlphaFLJacket extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer jacket;
	private final ModelRenderer hood_r1;
	private final ModelRenderer hood_r2;
	private final ModelRenderer jacket_leftarm;
	private final ModelRenderer jacket_rightarm;
	private final ModelRenderer hood;
	private final ModelRenderer hood_r3;
	private final ModelRenderer hood_r4;
	private final ModelRenderer hood_r5;
	private final ModelRenderer hood_r6;
	private final ModelRenderer hood_r7;
	private final ModelRenderer hood_r8;

	public AlphaFLJacket() {
		textureWidth = 128;
		textureHeight = 128;

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

		jacket = new ModelRenderer(this);
		jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
		jacket.cubeList.add(new ModelBox(jacket, 51, 51, -4.0F, 0.0F, -2.0F, 8, 12, 1, 0.12F, false));
		jacket.cubeList.add(new ModelBox(jacket, 48, 0, -4.0F, 0.0F, 1.0F, 8, 12, 1, 0.12F, false));
		jacket.cubeList.add(new ModelBox(jacket, 56, 13, -4.001F, 0.0F, -1.0F, 1, 12, 2, 0.12F, false));
		jacket.cubeList.add(new ModelBox(jacket, 18, 55, 3.001F, 0.0F, -1.0F, 1, 12, 2, 0.12F, false));

		hood_r1 = new ModelRenderer(this);
		hood_r1.setRotationPoint(4.0F, 1.8F, -2.15F);
		jacket.addChild(hood_r1);
		setRotationAngle(hood_r1, 0.8378F, -0.1745F, 0.0349F);
		hood_r1.cubeList.add(new ModelBox(hood_r1, 24, 19, -4.0F, -2.0F, 0.0F, 4, 3, 1, 0.0F, false));

		hood_r2 = new ModelRenderer(this);
		hood_r2.setRotationPoint(-4.0F, 1.8F, -2.15F);
		jacket.addChild(hood_r2);
		setRotationAngle(hood_r2, 0.8378F, 0.1745F, -0.0349F);
		hood_r2.cubeList.add(new ModelBox(hood_r2, 24, 0, 0.0F, -2.0F, 0.0F, 4, 3, 1, 0.0F, false));

		jacket_leftarm = new ModelRenderer(this);
		jacket_leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		jacket_leftarm.cubeList.add(new ModelBox(jacket_leftarm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 11, 4, 0.12F, false));

		jacket_rightarm = new ModelRenderer(this);
		jacket_rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		jacket_rightarm.cubeList.add(new ModelBox(jacket_rightarm, 16, 40, -3.0F, -2.0F, -2.0F, 4, 11, 4, 0.12F, false));

		hood = new ModelRenderer(this);
		hood.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		hood_r3 = new ModelRenderer(this);
		hood_r3.setRotationPoint(4.5F, -1.9F, 5.15F);
		hood.addChild(hood_r3);
		setRotationAngle(hood_r3, 0.2269F, 0.0F, 0.0F);
		hood_r3.cubeList.add(new ModelBox(hood_r3, 20, 16, -9.0F, -2.0F, -1.0F, 9, 2, 1, 0.0F, false));

		hood_r4 = new ModelRenderer(this);
		hood_r4.setRotationPoint(4.0F, -1.9F, 5.15F);
		hood.addChild(hood_r4);
		setRotationAngle(hood_r4, -0.3316F, 0.0F, 0.0F);
		hood_r4.cubeList.add(new ModelBox(hood_r4, 48, 31, -8.0F, 0.0F, -3.0F, 8, 3, 3, 0.0F, false));

		hood_r5 = new ModelRenderer(this);
		hood_r5.setRotationPoint(-3.9F, 1.4F, -2.35F);
		hood.addChild(hood_r5);
		setRotationAngle(hood_r5, 0.2094F, -0.0175F, -0.2618F);
		hood_r5.cubeList.add(new ModelBox(hood_r5, 35, 52, -0.001F, -2.0F, 0.0F, 1, 2, 7, 0.0F, false));

		hood_r6 = new ModelRenderer(this);
		hood_r6.setRotationPoint(-3.9F, 1.4F, -2.35F);
		hood.addChild(hood_r6);
		setRotationAngle(hood_r6, 0.4189F, -0.0175F, -0.2618F);
		hood_r6.cubeList.add(new ModelBox(hood_r6, 0, 48, 0.0F, -2.0F, 0.0F, 1, 2, 8, 0.0F, false));

		hood_r7 = new ModelRenderer(this);
		hood_r7.setRotationPoint(3.9F, 1.4F, -2.35F);
		hood.addChild(hood_r7);
		setRotationAngle(hood_r7, 0.4189F, 0.0175F, 0.2618F);
		hood_r7.cubeList.add(new ModelBox(hood_r7, 24, 47, -1.0F, -2.0F, 0.0F, 1, 2, 8, 0.0F, false));

		hood_r8 = new ModelRenderer(this);
		hood_r8.setRotationPoint(3.9F, 1.4F, -2.35F);
		hood.addChild(hood_r8);
		setRotationAngle(hood_r8, 0.2094F, 0.0175F, 0.2618F);
		hood_r8.cubeList.add(new ModelBox(hood_r8, 52, 37, -0.999F, -2.0F, 0.0F, 1, 2, 7, 0.0F, false));
		
		bipedLeftArm = jacket_leftarm;
		bipedRightArm = jacket_rightarm;
		bipedBody = jacket;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedHead = hood;
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