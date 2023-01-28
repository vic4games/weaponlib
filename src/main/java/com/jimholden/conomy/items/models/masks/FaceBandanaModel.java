// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports
package com.jimholden.conomy.items.models.masks;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class FaceBandanaModel extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer facebandana;
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

	public FaceBandanaModel() {
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

		facebandana = new ModelRenderer(this);
		facebandana.setRotationPoint(0.0F, 0.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, -3.7F, -4.7F);
		facebandana.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0524F, -0.0873F, 0.2094F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 4, 0, 3.3F, 0.01F, 0.01F, 1, 1, 1, 0.0F, false));
		cube_r1.cubeList.add(new ModelBox(cube_r1, 36, 26, 0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-3.25F, -0.65F, -4.1F);
		facebandana.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.1047F, 0.0175F, -0.0873F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 24, 19, -1.0F, 0.0F, 0.0F, 1, 1, 4, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(3.15F, -0.65F, -4.1F);
		facebandana.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.1047F, -0.0175F, 0.0873F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 32, 16, 0.0F, 0.0F, 0.0F, 1, 1, 4, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(3.15F, -2.7F, -3.5F);
		facebandana.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.2094F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 36, 16, 0.0F, 0.0F, 0.0F, 1, 2, 8, 0.0F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 20, 16, -6.4F, 0.0F, 7.03F, 7, 2, 1, 0.0F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 8, 40, -7.4F, 0.0F, 0.0F, 1, 2, 8, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.15F, -0.1F, 3.88F);
		facebandana.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.7156F, 0.576F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 0, -1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-0.25F, -0.7F, 4.28F);
		facebandana.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.4712F, -0.4014F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 3, -1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, -3.7F, -4.85F);
		facebandana.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0524F, 0.1047F, -0.2269F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 4, 3, -4.4F, 0.01F, 0.01F, 1, 1, 1, 0.0F, false));
		cube_r7.cubeList.add(new ModelBox(cube_r7, 40, 28, -4.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, -2.7F, -4.75F);
		facebandana.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.1571F, 0.1047F, -0.1047F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 24, 24, -4.3F, 0.0F, 0.01F, 1, 2, 1, 0.0F, false));
		cube_r8.cubeList.add(new ModelBox(cube_r8, 18, 40, -4.0F, 0.0F, 0.0F, 4, 2, 1, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, -2.7F, -4.75F);
		facebandana.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.1222F, -0.1047F, 0.1047F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 16, 3.3F, 0.0F, 0.01F, 1, 2, 1, 0.0F, false));
		cube_r9.cubeList.add(new ModelBox(cube_r9, 34, 21, 0.0F, 0.0F, 0.0F, 4, 2, 1, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.0F, 1.3F, -4.35F);
		facebandana.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.1222F, -0.0698F, -0.2094F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 24, 0, 0.0F, -2.0F, 0.0F, 4, 2, 1, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, 1.3F, -4.35F);
		facebandana.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.1222F, 0.0873F, 0.192F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 12, 32, -4.0F, -2.0F, 0.0F, 4, 2, 1, 0.0F, false));
		
		
		bipedHead = facebandana;
		
		bipedHeadwear.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedBody.isHidden = true;
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