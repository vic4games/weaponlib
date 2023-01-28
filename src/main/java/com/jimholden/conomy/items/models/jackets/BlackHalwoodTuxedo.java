// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports
package com.jimholden.conomy.items.models.jackets;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class BlackHalwoodTuxedo extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer tuxedo_jacket;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer tux_leftarm;
	private final ModelRenderer tux_rightarm;

	public BlackHalwoodTuxedo() {
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

		tuxedo_jacket = new ModelRenderer(this);
		tuxedo_jacket.setRotationPoint(0.0F, 0.0F, 0.0F);
		tuxedo_jacket.cubeList.add(new ModelBox(tuxedo_jacket, 32, 52, 2.0F, 0.0F, -2.0F, 2, 12, 1, 0.12F, false));
		tuxedo_jacket.cubeList.add(new ModelBox(tuxedo_jacket, 48, 0, -4.0F, 0.0F, 1.0F, 8, 12, 1, 0.12F, false));
		tuxedo_jacket.cubeList.add(new ModelBox(tuxedo_jacket, 6, 48, 3.001F, 0.0F, -1.0F, 1, 12, 2, 0.12F, false));
		tuxedo_jacket.cubeList.add(new ModelBox(tuxedo_jacket, 0, 48, -4.001F, 0.0F, -1.0F, 1, 12, 2, 0.12F, false));
		tuxedo_jacket.cubeList.add(new ModelBox(tuxedo_jacket, 51, 51, -4.0F, 0.0F, -2.0F, 2, 12, 1, 0.12F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-0.8F, 5.1F, -2.45F);
		tuxedo_jacket.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.0175F, 0.1745F, 0.0524F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -3.0F, 0.0F, 0.0F, 3, 7, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.8F, 5.1F, -2.45F);
		tuxedo_jacket.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.0175F, -0.1745F, -0.0524F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 24, 0, 0.0F, 0.0F, 0.0F, 3, 7, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-1.5F, 0.2F, -2.2F);
		tuxedo_jacket.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.0524F, 0.1571F, -0.1571F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 24, 16, -2.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(1.5F, 0.2F, -2.2F);
		tuxedo_jacket.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.0524F, -0.1571F, 0.1571F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 30, 16, 0.0F, 0.0F, 0.0F, 2, 5, 1, 0.0F, false));

		tux_leftarm = new ModelRenderer(this);
		tux_leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		tux_leftarm.cubeList.add(new ModelBox(tux_leftarm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 11, 4, 0.12F, false));

		tux_rightarm = new ModelRenderer(this);
		tux_rightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		tux_rightarm.cubeList.add(new ModelBox(tux_rightarm, 16, 40, -3.0F, -2.0F, -2.0F, 4, 11, 4, 0.12F, false));
		
		bipedLeftArm = tux_leftarm;
		bipedRightArm = tux_rightarm;
		bipedBody = tuxedo_jacket;
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