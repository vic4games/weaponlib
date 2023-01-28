package com.jimholden.conomy.entity.models;
import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.entity.EntityRope;
import com.jimholden.conomy.items.ItemRope;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.animations.AnimationJSONTool;
import com.jimholden.conomy.util.animations.AnimationPlayer;
import com.jimholden.conomy.util.animations.AnimationState;
import com.jimholden.conomy.util.animations.EntityAnimationState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class ClimbingRopeAnchor extends ModelBase {
	private final ModelRenderer bone;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;

	public ClimbingRopeAnchor() {
		textureWidth = 16;
		textureHeight = 16;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 25.0F, 0.0F);
		
		textureWidth = 8;
		textureHeight = 8;

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.2F, -3.6F, -3.5F);
		bone.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.4363F, -0.0436F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 4, -1.0F, -1.001F, -3.0F, 2, 2, 2, 0.0F, false));

		
		textureWidth = 16;
		textureHeight = 16;
		
		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-0.3F, -2.9F, -2.55F);
		bone.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -0.1309F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 8, 0.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-0.25F, -2.4F, -3.8F);
		bone.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, -0.0611F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 10, 9, 0.0F, -1.0F, -2.05F, 1, 1, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.4F, -2.4F, -3.8F);
		bone.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, -0.2007F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 5, 5, 0.0F, -1.0F, -2.0F, 1, 1, 3, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-1.0F, -2.4F, -3.8F);
		bone.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0436F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 7, 1, 0.0F, -1.0F, -2.0F, 1, 1, 3, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-0.2F, -2.4F, -0.2F);
		bone.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, -0.2618F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 0, -1.0F, -1.0F, -2.0F, 2, 1, 3, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(1.0F, 0.25F, 0.0F);
		bone.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.1745F, 0.0F, -0.1745F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 6, 9, -1.0F, -4.0F, 0.0F, 1, 3, 1, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		//System.out.println("fuck");
		if(entity != null) {
			setRotationAngle(bone, f4* 0.017453292F, f3* 0.017453292F, 0);
		}
		
		/*
		cube_r1.isHidden = false;
		cube_r2.isHidden = true;
		cube_r3.isHidden = true;
		cube_r4.isHidden = true;
		cube_r5.isHidden = true;
		cube_r6.isHidden = true;
		
		//cube_r3.render(f5);
		
		ResourceLocation resl = new ResourceLocation(Reference.MOD_ID + ":textures/entity/ropentile.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(resl); 
		*/
		//cube_r1.isHidden = true;
	//cube_r1.setRotationPoint(0.0F, 25.0F, 0.0F);
		
		
		
		cube_r1.isHidden = true;
		cube_r2.isHidden = false;
		cube_r3.isHidden = false;
		cube_r4.isHidden = false;
		cube_r5.isHidden = false;
		cube_r6.isHidden = false;
		cube_r7.isHidden = false;
		bone.render(f5);
		if(entity != null) {
			if(!((EntityRope) entity).isRopeStackEmpty()) {
				renderRope((EntityRope) entity, f4, f3, f5);
			}
			
		}
		
      // renderRope(entity, f4, f3);
	}
	
	public void renderRope(EntityRope entity, float yaw, float pitch, float scale) {
		
		cube_r1.isHidden = false;
		cube_r2.isHidden = true;
		cube_r3.isHidden = true;
		cube_r4.isHidden = true;
		cube_r5.isHidden = true;
		cube_r6.isHidden = true;
		cube_r7.isHidden = true;
		
		ResourceLocation resLoc = ItemRope.getResourceLocationFromType(entity.getRopeType());
		if(resLoc == null) return;
		
	//	ResourceLocation resl = new ResourceLocation(Reference.MOD_ID + ":textures/entity/ropentile.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(resLoc); 
		//GlStateManager.translate(bone.offsetX, bone.offsetY, bone.offsetZ);
		bone.render(scale);

		
	}
	

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}