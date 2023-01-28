package com.jimholden.conomy.items.models.bodyarmor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class AnaM2TacticalVest extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer ana2;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;

	public AnaM2TacticalVest() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, true));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightarm.cubeList.add(new ModelBox(rightarm, 0, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftarm.cubeList.add(new ModelBox(leftarm, 16, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		rightleg.cubeList.add(new ModelBox(rightleg, 16, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		leftleg.cubeList.add(new ModelBox(leftleg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		ana2 = new ModelRenderer(this);
		ana2.setRotationPoint(0.0F, 0.0F, 0.0F);
		ana2.cubeList.add(new ModelBox(ana2, 28, 9, -4.0F, 2.0F, -2.5F, 8, 8, 1, 0.01F, false));
		ana2.cubeList.add(new ModelBox(ana2, 42, 25, 3.0F, 1.0F, -2.5F, 1, 1, 1, 0.01F, false));
		ana2.cubeList.add(new ModelBox(ana2, 30, 36, 3.0F, 1.75F, -2.75F, 1, 1, 1, -0.1F, false));
		ana2.cubeList.add(new ModelBox(ana2, 30, 36, 3.0F, 1.75F, 1.75F, 1, 1, 1, -0.1F, false));
		ana2.cubeList.add(new ModelBox(ana2, 30, 36, -4.0F, 1.75F, -2.75F, 1, 1, 1, -0.1F, false));
		ana2.cubeList.add(new ModelBox(ana2, 30, 36, -4.0F, 1.75F, 1.75F, 1, 1, 1, -0.1F, false));
		ana2.cubeList.add(new ModelBox(ana2, 42, 6, 3.0F, 1.0F, 1.5F, 1, 1, 1, 0.01F, false));
		ana2.cubeList.add(new ModelBox(ana2, 42, 4, -4.0F, 1.0F, 1.5F, 1, 1, 1, 0.01F, false));
		ana2.cubeList.add(new ModelBox(ana2, 42, 23, -4.0F, 1.0F, -2.5F, 1, 1, 1, 0.01F, false));
		ana2.cubeList.add(new ModelBox(ana2, 0, 40, -4.0F, -0.5F, -2.0F, 1, 1, 4, 0.01F, false));
		ana2.cubeList.add(new ModelBox(ana2, 34, 36, 3.0F, -0.5F, -2.0F, 1, 1, 4, 0.01F, false));
		ana2.cubeList.add(new ModelBox(ana2, 34, 41, -4.0F, 4.0F, -2.6F, 1, 6, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 24, 9, 3.0F, 4.0F, -2.6F, 1, 6, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 14, 32, -3.5F, 3.5F, -2.598F, 7, 1, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 0, 32, -3.0F, 3.0F, -2.6F, 6, 7, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 39, 20, -2.0F, 3.5F, -2.8F, 4, 2, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 0, 40, -2.75F, 3.5F, -2.8F, 1, 2, 1, -0.1F, false));
		ana2.cubeList.add(new ModelBox(ana2, 34, 36, 1.75F, 3.5F, -2.8F, 1, 2, 1, -0.1F, false));
		ana2.cubeList.add(new ModelBox(ana2, 32, 29, -3.0F, 6.0F, -2.9F, 6, 1, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 22, 41, -2.1F, 7.25F, -3.85F, 2, 4, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 16, 41, 0.125F, 7.25F, -3.85F, 2, 4, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 28, 18, -3.5F, 6.5F, -2.898F, 7, 1, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 31, 31, -4.0F, 7.0F, -2.9F, 8, 4, 1, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 24, 34, 3.5F, 7.0F, -2.1929F, 1, 3, 4, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 14, 34, -4.5F, 7.0F, -2.1929F, 1, 3, 4, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 32, 20, -5.5F, 7.25F, -1.6929F, 2, 4, 3, 0.0F, false));
		ana2.cubeList.add(new ModelBox(ana2, 42, 2, -5.0F, 7.0F, -2.5429F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 40, 38, 4.0F, 7.0F, -2.5429F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 42, 0, -5.0F, 9.0F, -2.5429F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 40, 36, 4.0F, 9.0F, -2.5429F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 41, 42, -5.0F, 9.0F, 1.2071F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 6, 40, 4.0F, 9.0F, 1.2071F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 6, 42, -5.0F, 7.0F, 1.2071F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 38, 41, 4.0F, 7.0F, 1.2071F, 1, 1, 1, -0.22F, false));
		ana2.cubeList.add(new ModelBox(ana2, 24, 0, -4.0F, 2.0F, 1.5F, 8, 8, 1, 0.01F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(2.3029F, 23.5F, -4.0314F);
		ana2.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, -0.7854F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, 2.0F, -16.5F, -0.4F, 1, 3, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-5.6971F, 23.5F, -4.0314F);
		ana2.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -0.7854F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 12, 16, 2.0F, -16.5F, -0.4F, 1, 3, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(2.3029F, 23.5F, -0.0314F);
		ana2.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, -0.7854F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 16, 2.0F, -16.5F, -0.4F, 1, 3, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-5.6971F, 23.5F, -0.0314F);
		ana2.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, -0.7854F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 16, 16, 2.0F, -16.5F, -0.4F, 1, 3, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-6.0F, 22.3844F, 9.0501F);
		ana2.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.7854F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 32, 27, 3.0F, -16.5F, -0.4F, 6, 1, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-0.95F, 25.0F, -4.25F);
		ana2.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, -0.2618F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 10, 41, 3.35F, -17.75F, -0.5F, 2, 4, 1, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-7.125F, 25.0F, -2.075F);
		ana2.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.2618F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 28, 41, 3.0F, -17.75F, -0.5F, 2, 4, 1, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, 18.7665F, -9.0154F);
		ana2.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.3491F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 20, 34, 3.0F, -20.5F, 0.0F, 1, 2, 1, 0.01F, false));
		cube_r8.cubeList.add(new ModelBox(cube_r8, 24, 34, -4.0F, -20.5F, 0.0F, 1, 2, 1, 0.01F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, 19.1085F, 8.0757F);
		ana2.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.3491F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 20, 0, 3.0F, -20.5F, 0.0F, 1, 2, 1, 0.01F, false));
		cube_r9.cubeList.add(new ModelBox(cube_r9, 14, 34, -4.0F, -20.5F, 0.0F, 1, 2, 1, 0.01F, false));
		
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedBody = ana2;
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