package com.jimholden.conomy.items.models.bodyarmor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class BodyArmorModelThree extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer bodyarmor;

	public BodyArmorModelThree() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightarm.cubeList.add(new ModelBox(rightarm, 36, 36, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftarm.cubeList.add(new ModelBox(leftarm, 32, 0, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		rightleg.cubeList.add(new ModelBox(rightleg, 16, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		leftleg.cubeList.add(new ModelBox(leftleg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		bodyarmor = new ModelRenderer(this);
		bodyarmor.setRotationPoint(0.0F, 0.0F, 0.0F);
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 31, 31, -3.5F, 1.9F, -2.1F, 7, 9, 1, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 26, 32, -4.1F, 5.9F, -2.101F, 1, 5, 1, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 28, 11, -4.101F, 5.9F, -1.9F, 1, 5, 4, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 22, 32, 3.1F, 5.9F, -2.101F, 1, 5, 1, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 0, 32, 3.101F, 5.9F, -1.9F, 1, 5, 4, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 24, 11, -3.5F, -0.1F, -2.1F, 2, 2, 1, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 12, 16, 1.5F, -0.1F, -2.1F, 2, 2, 1, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 32, 20, -3.5F, -0.11F, -1.9F, 2, 1, 4, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 10, 32, 1.5F, -0.11F, -1.9F, 2, 1, 4, 0.0F, false));
		bodyarmor.cubeList.add(new ModelBox(bodyarmor, 24, 0, -3.5F, 0.89F, 1.1F, 7, 10, 1, 0.0F, false));
		
		
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedBody = bodyarmor;
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