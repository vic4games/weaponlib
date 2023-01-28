package com.jimholden.conomy.entity.models;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;


// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class HogModel extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer rotation;
	private final ModelRenderer body_sub_1;
	private final ModelRenderer body_sub_2;
	private final ModelRenderer head;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer leg1;
	private final ModelRenderer leg2;
	private final ModelRenderer leg3;
	private final ModelRenderer leg4;

	public HogModel() {
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 11.0F, 2.0F);
		

		rotation = new ModelRenderer(this);
		rotation.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(rotation);
		setRotationAngle(rotation, 1.5708F, 0.0F, 0.0F);
		

		body_sub_1 = new ModelRenderer(this);
		body_sub_1.setRotationPoint(0.0F, 6.0F, 0.0F);
		rotation.addChild(body_sub_1);
		body_sub_1.cubeList.add(new ModelBox(body_sub_1, 0, 0, -4.5F, -16.0F, -7.0F, 9, 16, 7, 0.0F, false));

		body_sub_2 = new ModelRenderer(this);
		body_sub_2.setRotationPoint(0.0F, -13.0F, 0.0F);
		rotation.addChild(body_sub_2);
		setRotationAngle(body_sub_2, -0.7854F, 0.0F, 0.0F);
		

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 12.0F, -6.0F);
		head.cubeList.add(new ModelBox(head, 32, 0, -4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 23, -4.0F, 0.0F, -8.0F, 8, 4, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 32, 33, -2.0F, 0.0F, -9.0F, 4, 3, 1, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 16, 48, -4.0F, 2.0F, -8.75F, 2, 1, 1, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 10, 48, 2.0F, 2.0F, -8.75F, 2, 1, 1, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 26, 48, 3.0F, 1.0F, -8.75F, 1, 1, 1, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 22, 48, -4.0F, 1.0F, -8.75F, 1, 1, 1, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(4.5F, -3.0F, -4.5F);
		head.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.0F, -0.2182F);
		bone.cubeList.add(new ModelBox(bone, 8, 43, -1.5F, -2.0F, 0.5F, 3, 4, 1, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-4.5F, -3.0F, -4.5F);
		head.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, 0.2182F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 43, -1.5F, -2.0F, 0.5F, 3, 4, 1, 0.0F, false));

		leg1 = new ModelRenderer(this);
		leg1.setRotationPoint(3.0F, 18.0F, 7.0F);
		leg1.cubeList.add(new ModelBox(leg1, 16, 33, -2.5F, 0.0F, -2.5F, 4, 6, 4, 0.0F, true));
		leg1.cubeList.add(new ModelBox(leg1, 16, 43, -2.5F, 5.0F, -3.25F, 4, 1, 1, 0.0F, true));

		leg2 = new ModelRenderer(this);
		leg2.setRotationPoint(-3.0F, 18.0F, 7.0F);
		leg2.cubeList.add(new ModelBox(leg2, 0, 33, -1.5F, 0.0F, -2.5F, 4, 6, 4, 0.0F, false));
		leg2.cubeList.add(new ModelBox(leg2, 16, 45, -1.5F, 5.0F, -3.25F, 4, 1, 1, 0.0F, false));

		leg3 = new ModelRenderer(this);
		leg3.setRotationPoint(3.0F, 18.0F, -5.0F);
		leg3.cubeList.add(new ModelBox(leg3, 36, 23, -2.5F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));
		leg3.cubeList.add(new ModelBox(leg3, 0, 48, -2.5F, 5.0F, -2.75F, 4, 1, 1, 0.0F, true));

		leg4 = new ModelRenderer(this);
		leg4.setRotationPoint(-3.0F, 18.0F, -5.0F);
		leg4.cubeList.add(new ModelBox(leg4, 20, 23, -1.5F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));
		leg4.cubeList.add(new ModelBox(leg4, 26, 43, -1.5F, 5.0F, -2.75F, 4, 1, 1, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
		head.render(f5);
		leg1.render(f5);
		leg2.render(f5);
		leg3.render(f5);
		leg4.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		this.head.rotateAngleX = headPitch * 0.017453292F;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
       // this.body.rotateAngleX = ((float)Math.PI / 2F);
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}
}