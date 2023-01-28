package com.jimholden.conomy.items.models.headset;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class HeadsetModel extends ModelBiped {
	public ModelRenderer head = null;
	public ModelRenderer body = null;
	public ModelRenderer rightarm = null;
	public ModelRenderer leftarm = null;
	public ModelRenderer rightleg = null;
	public ModelRenderer leftleg = null;
	public ModelRenderer headset = null;
	public ModelRenderer bone10 = null;
	public ModelRenderer bone = null;
	public ModelRenderer bone18 = null;
	public ModelRenderer bone19 = null;
	public ModelRenderer bone4 = null;
	public ModelRenderer bone5 = null;
	public ModelRenderer bone6 = null;
	public ModelRenderer bone2 = null;
	public ModelRenderer bone3 = null;
	public ModelRenderer bone7 = null;
	public ModelRenderer bone8 = null;
	public ModelRenderer bone9 = null;
	public ModelRenderer bone11 = null;
	public ModelRenderer bone12 = null;
	public ModelRenderer bone13 = null;
	public ModelRenderer bone14 = null;
	public ModelRenderer bone20 = null;
	public ModelRenderer bone23 = null;
	public ModelRenderer bone21 = null;
	public ModelRenderer bone22 = null;
	public ModelRenderer bone15 = null;
	public ModelRenderer bone16 = null;
	public ModelRenderer bone17 = null;

	public HeadsetModel() {
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

		

		headset = new ModelRenderer(this);
		headset.setRotationPoint(0.0F, 0.0F, 0.0F);
		headset.cubeList.add(new ModelBox(headset, 20, 16, -4.0F, -8.5F, -1.0F, 8, 1, 2, 0.0F, false));
		
		//bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftLeg.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedBody.isHidden = true;
		
		bipedHead = headset;
		//this.bipedHead.addChild(headset);
		
		

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.2F, 0.0F, 0.0F);
		headset.addChild(bone10);
		

		bone = new ModelRenderer(this);
		bone.setRotationPoint(4.8F, -1.5F, -1.0F);
		bone10.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.0F, 0.1745F);
		bone.cubeList.add(new ModelBox(bone, 12, 32, -1.0209F, -2.1989F, 0.0F, 1, 2, 2, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 36, 19, -1.3858F, -2.5407F, -1.4F, 1, 2, 1, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 17, 35, -1.3858F, -2.5407F, 2.2F, 1, 2, 1, 0.0F, false));

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(4.8142F, -4.0407F, -2.4F);
		bone10.addChild(bone18);
		setRotationAngle(bone18, -0.6807F, 0.0F, 0.1745F);
		bone18.cubeList.add(new ModelBox(bone18, 39, 21, -0.9542F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(4.8142F, -4.0407F, 2.2F);
		bone10.addChild(bone19);
		setRotationAngle(bone19, 0.6807F, 0.0F, 0.1745F);
		bone19.cubeList.add(new ModelBox(bone19, 39, 18, -0.9542F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(5.2F, -3.5F, -1.0F);
		bone10.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0F, -0.4363F);
		bone4.cubeList.add(new ModelBox(bone4, 30, 19, -1.0209F, -2.1989F, 0.0F, 1, 2, 2, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(5.2F, -3.5F, -1.0F);
		bone10.addChild(bone5);
		setRotationAngle(bone5, -0.4189F, -1.1345F, -0.0698F);
		bone5.cubeList.add(new ModelBox(bone5, 36, 25, -1.0209F, -2.1989F, 0.0F, 1, 2, 1, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(5.2F, -3.5F, 1.0F);
		bone10.addChild(bone6);
		setRotationAngle(bone6, 0.4189F, 1.1345F, -0.0698F);
		bone6.cubeList.add(new ModelBox(bone6, 36, 22, -1.0209F, -2.1989F, -1.0F, 1, 2, 1, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(4.8F, -1.5F, -1.0F);
		bone10.addChild(bone2);
		setRotationAngle(bone2, 0.0F, -1.0647F, 0.1745F);
		bone2.cubeList.add(new ModelBox(bone2, 16, 38, -1.0209F, -2.1989F, 0.0F, 1, 2, 1, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(4.8F, -1.5F, 1.0F);
		bone10.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 1.0647F, 0.1745F);
		bone3.cubeList.add(new ModelBox(bone3, 20, 37, -1.0209F, -2.1989F, -1.0F, 1, 2, 1, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(-0.2F, 0.0F, 0.0F);
		headset.addChild(bone7);
		setRotationAngle(bone7, 0.0F, 3.1416F, 0.0F);
		

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(4.8F, -1.5F, -1.0F);
		bone7.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 0.0F, 0.1745F);
		bone8.cubeList.add(new ModelBox(bone8, 30, 0, -1.0209F, -2.1989F, 0.0F, 1, 2, 2, 0.0F, false));
		bone8.cubeList.add(new ModelBox(bone8, 0, 32, -1.3858F, -2.5407F, -1.4F, 1, 2, 1, 0.0F, false));
		bone8.cubeList.add(new ModelBox(bone8, 28, 5, -1.3858F, -2.5407F, 2.2F, 1, 2, 1, 0.0F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(4.8142F, -4.0407F, -2.4F);
		bone7.addChild(bone9);
		setRotationAngle(bone9, -0.6807F, 0.0F, 0.1745F);
		bone9.cubeList.add(new ModelBox(bone9, 38, 16, -0.9542F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(4.8142F, -4.0407F, 2.2F);
		bone7.addChild(bone11);
		setRotationAngle(bone11, 0.6807F, 0.0F, 0.1745F);
		bone11.cubeList.add(new ModelBox(bone11, 28, 19, -0.9542F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(5.2F, -3.5F, -1.0F);
		bone7.addChild(bone12);
		setRotationAngle(bone12, 0.0F, 0.0F, -0.4363F);
		bone12.cubeList.add(new ModelBox(bone12, 24, 19, -1.0209F, -2.1989F, 0.0F, 1, 2, 2, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(5.2F, -3.5F, -1.0F);
		bone7.addChild(bone13);
		setRotationAngle(bone13, -0.4189F, -1.1345F, -0.0698F);
		bone13.cubeList.add(new ModelBox(bone13, 24, 5, -1.0209F, -2.1989F, 0.0F, 1, 2, 1, 0.0F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(5.2F, -3.5F, 1.0F);
		bone7.addChild(bone14);
		setRotationAngle(bone14, 0.4189F, 1.1345F, -0.0698F);
		bone14.cubeList.add(new ModelBox(bone14, 24, 24, -1.0209F, -2.1989F, -1.0F, 1, 2, 1, 0.0F, false));

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(4.8F, -1.5F, -1.0F);
		bone7.addChild(bone20);
		setRotationAngle(bone20, 0.0F, -1.0647F, 0.1745F);
		bone20.cubeList.add(new ModelBox(bone20, 4, 5, -1.0209F, -2.1989F, 0.0F, 1, 2, 1, 0.0F, false));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(4.8F, -1.5F, 1.0F);
		bone7.addChild(bone23);
		setRotationAngle(bone23, 0.0F, 1.0647F, 0.1745F);
		bone23.cubeList.add(new ModelBox(bone23, 0, 5, -1.0209F, -2.1989F, -1.0F, 1, 2, 1, 0.0F, false));

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(4.7F, -5.25F, -1.0F);
		headset.addChild(bone21);
		setRotationAngle(bone21, 0.0F, 0.0F, -0.1222F);
		bone21.cubeList.add(new ModelBox(bone21, 24, 0, -0.8886F, -3.0871F, -0.001F, 1, 3, 2, 0.0F, false));
		bone21.cubeList.add(new ModelBox(bone21, 28, 0, -0.61F, -1.3047F, 0.5F, 1, 1, 1, 0.0F, false));

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(-4.7F, -5.25F, -1.0F);
		headset.addChild(bone22);
		setRotationAngle(bone22, 0.0F, 0.0F, 0.1222F);
		bone22.cubeList.add(new ModelBox(bone22, 0, 0, -0.1114F, -3.0871F, -0.001F, 1, 3, 2, 0.0F, false));
		bone22.cubeList.add(new ModelBox(bone22, 4, 0, -0.39F, -1.3047F, 0.5F, 1, 1, 1, 0.0F, false));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(4.7F, -3.2F, -2.3F);
		headset.addChild(bone15);
		setRotationAngle(bone15, -1.0647F, -0.1396F, 0.1222F);
		bone15.cubeList.add(new ModelBox(bone15, 0, 16, -1.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(4.9F, -1.7F, -4.9F);
		headset.addChild(bone16);
		setRotationAngle(bone16, -0.9425F, 0.6458F, 0.6283F);
		bone16.cubeList.add(new ModelBox(bone16, 18, 32, -1.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(3.5F, -1.3F, -6.2F);
		headset.addChild(bone17);
		setRotationAngle(bone17, -0.3665F, 1.0123F, 1.2392F);
		bone17.cubeList.add(new ModelBox(bone17, 39, 24, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));
		
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		/*
		head.render(f5);
		body.render(f5);
		rightarm.render(f5);
		leftarm.render(f5);
		rightleg.render(f5);
		leftleg.render(f5);
		*/
		//bipedHead.addChild(headset);
		
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
		
	}
}
