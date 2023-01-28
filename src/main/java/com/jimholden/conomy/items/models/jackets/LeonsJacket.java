package com.jimholden.conomy.items.models.jackets;

import com.jimholden.conomy.client.gui.engine.GUItil;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class LeonsJacket extends ModelBiped {
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer jacket;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer jacketleftarm;
	private final ModelRenderer jacketrightarm;

	public LeonsJacket() {
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
		jacket.cubeList.add(new ModelBox(jacket, 40, 52, -4.0F, 0.0F, -2.15F, 3, 6, 1, 0.0F, false));
		jacket.cubeList.add(new ModelBox(jacket, 58, 51, -4.0F, -0.15F, -2.0F, 3, 1, 4, 0.0F, false));
		jacket.cubeList.add(new ModelBox(jacket, 20, 16, -1.0F, -0.15F, 1.0F, 2, 1, 1, 0.0F, false));
		jacket.cubeList.add(new ModelBox(jacket, 58, 10, 1.0F, -0.15F, -2.0F, 3, 1, 4, 0.0F, false));
		jacket.cubeList.add(new ModelBox(jacket, 16, 32, 1.0F, 0.0F, -2.15F, 3, 6, 1, 0.0F, false));
		jacket.cubeList.add(new ModelBox(jacket, 0, 48, 3.15F, 0.0F, -2.0F, 1, 12, 4, 0.0F, false));
		jacket.cubeList.add(new ModelBox(jacket, 40, 16, -4.15F, 0.0F, -2.0F, 1, 12, 4, 0.0F, false));
		jacket.cubeList.add(new ModelBox(jacket, 16, 40, -4.0F, 0.0F, 1.15F, 8, 12, 1, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-1.0F, 6.0F, -2.15F);
		jacket.addChild(bone3);
		setRotationAngle(bone3, -0.0349F, 0.0F, 0.0698F);
		bone3.cubeList.add(new ModelBox(bone3, 24, 0, -3.0F, 0.0F, 0.0F, 3, 6, 1, 0.0F, false));



		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(1.0F, 6.0F, -2.15F);
		jacket.addChild(bone4);
		setRotationAngle(bone4, -0.0349F, 0.0F, -0.0698F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 0, 0.0F, 0.0F, 0.0F, 3, 6, 1, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(4.15F, 6.0F, -2.0F);
		jacket.addChild(bone);
		setRotationAngle(bone, -0.0175F, 0.0F, -0.0698F);
		bone.cubeList.add(new ModelBox(bone, 40, 59, -1.0F, 0.0F, 0.0F, 1, 6, 4, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-4.15F, 6.0F, -2.0F);
		jacket.addChild(bone2);
		setRotationAngle(bone2, -0.0175F, 0.0F, 0.0698F);
		bone2.cubeList.add(new ModelBox(bone2, 58, 0, 0.0F, 0.0F, 0.0F, 1, 6, 4, 0.0F, false));

		jacketleftarm = new ModelRenderer(this);
		jacketleftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		jacketleftarm.cubeList.add(new ModelBox(jacketleftarm, 54, 39, -1.0F, -2.0F, -2.15F, 4, 11, 1, 0.0F, false));
		jacketleftarm.cubeList.add(new ModelBox(jacketleftarm, 20, 53, -1.0F, -2.0F, 1.15F, 4, 11, 1, 0.0F, false));
		jacketleftarm.cubeList.add(new ModelBox(jacketleftarm, 30, 49, 2.15F, -2.0F, -2.0F, 1, 11, 4, 0.0F, false));
		jacketleftarm.cubeList.add(new ModelBox(jacketleftarm, 10, 49, -1.15F, -2.0F, -2.0F, 1, 11, 4, 0.0F, false));
		jacketleftarm.cubeList.add(new ModelBox(jacketleftarm, 58, 58, -1.0F, -2.15F, -2.0F, 4, 1, 4, 0.0F, false));

		jacketrightarm = new ModelRenderer(this);
		jacketrightarm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		jacketrightarm.cubeList.add(new ModelBox(jacketrightarm, 50, 27, -3.0F, -2.0F, -2.15F, 4, 11, 1, 0.0F, false));
		jacketrightarm.cubeList.add(new ModelBox(jacketrightarm, 50, 15, -3.0F, -2.0F, 1.15F, 4, 11, 1, 0.0F, false));
		jacketrightarm.cubeList.add(new ModelBox(jacketrightarm, 48, 0, 0.15F, -2.0F, -2.0F, 1, 11, 4, 0.0F, false));
		jacketrightarm.cubeList.add(new ModelBox(jacketrightarm, 48, 48, -3.15F, -2.0F, -2.0F, 1, 11, 4, 0.0F, false));
		jacketrightarm.cubeList.add(new ModelBox(jacketrightarm, 24, 16, -3.0F, -2.15F, -2.0F, 4, 1, 4, 0.0F, false));
		
		bipedLeftArm = jacketleftarm;
		bipedRightArm = jacketrightarm;
		bipedBody = jacket;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedHead.isHidden = true;
		bipedHeadwear.isHidden = true;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		
		//GUItil.initializeMultisample()
	
		super.render(entity, f, f1, f2, f3, f4, f5);
		
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}