// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

package com.jimholden.conomy.items.models.jackets;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class JeepSpirit extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer jacket;
	private final ModelRenderer body_r1;
	private final ModelRenderer body_r2;
	private final ModelRenderer hood;
	private final ModelRenderer head_r1;
	private final ModelRenderer head_r2;
	private final ModelRenderer head_r3;
	private final ModelRenderer jacket_leftarm;
	private final ModelRenderer jacket_rightarm;

	public JeepSpirit() {
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
		jacket.cubeList.add(new ModelBox(jacket, 24, 0, -4.0F, 0.0F, -2.0F, 3, 7, 1, 0.12F, false));
		jacket.cubeList.add(new ModelBox(jacket, 48, 0, -4.0F, 0.0F, 1.0F, 8, 12, 1, 0.12F, false));
		jacket.cubeList.add(new ModelBox(jacket, 37, 52, -3.999F, 0.0F, -1.55F, 1, 12, 3, 0.12F, false));
		jacket.cubeList.add(new ModelBox(jacket, 29, 52, 2.999F, 0.0F, -1.45F, 1, 12, 3, 0.12F, false));
		jacket.cubeList.add(new ModelBox(jacket, 0, 0, 1.0F, 0.0F, -2.0F, 3, 7, 1, 0.12F, false));

		body_r1 = new ModelRenderer(this);
		body_r1.setRotationPoint(1.0F, 7.1F, -2.0F);
		jacket.addChild(body_r1);
		setRotationAngle(body_r1, -0.0699F, 0.0037F, -0.0697F);
		body_r1.cubeList.add(new ModelBox(body_r1, 24, 16, 0.0F, 0.0F, 0.0F, 3, 5, 1, 0.12F, false));

		body_r2 = new ModelRenderer(this);
		body_r2.setRotationPoint(-1.0F, 7.1F, -2.0F);
		jacket.addChild(body_r2);
		setRotationAngle(body_r2, -0.0699F, -0.0037F, 0.0697F);
		body_r2.cubeList.add(new ModelBox(body_r2, 16, 32, -3.0F, 0.0F, 0.0F, 3, 5, 1, 0.12F, false));

		hood = new ModelRenderer(this);
		hood.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		head_r1 = new ModelRenderer(this);
		head_r1.setRotationPoint(4.5F, -1.95F, 4.3F);
		hood.addChild(head_r1);
		setRotationAngle(head_r1, -1.117F, 0.0F, 0.0F);
		head_r1.cubeList.add(new ModelBox(head_r1, 40, 31, -9.0F, 0.0F, 0.0F, 9, 3, 2, 0.0F, false));

		head_r2 = new ModelRenderer(this);
		head_r2.setRotationPoint(-4.5F, 0.5F, -2.5F);
		hood.addChild(head_r2);
		setRotationAngle(head_r2, 0.192F, 0.0F, -0.2967F);
		head_r2.cubeList.add(new ModelBox(head_r2, 45, 45, 0.0F, -1.0F, 0.0F, 1, 2, 7, 0.0F, false));

		head_r3 = new ModelRenderer(this);
		head_r3.setRotationPoint(4.5F, 0.5F, -2.5F);
		hood.addChild(head_r3);
		setRotationAngle(head_r3, 0.192F, 0.0F, 0.2967F);
		head_r3.cubeList.add(new ModelBox(head_r3, 0, 48, -1.0F, -1.0F, 0.0F, 1, 2, 7, 0.0F, false));

		jacket_leftarm = new ModelRenderer(this);
		jacket_leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		jacket_leftarm.cubeList.add(new ModelBox(jacket_leftarm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 11, 4, 0.12F, false));

		jacket_rightarm = new ModelRenderer(this);
		jacket_rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		jacket_rightarm.cubeList.add(new ModelBox(jacket_rightarm, 16, 40, -3.0F, -2.0F, -2.0F, 4, 11, 4, 0.12F, false));
		
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