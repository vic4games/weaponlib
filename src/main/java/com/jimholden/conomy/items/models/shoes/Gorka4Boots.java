// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports
package com.jimholden.conomy.items.models.shoes;

import com.jimholden.conomy.items.RigItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import scala.reflect.internal.Trees.This;

public class Gorka4Boots extends ModelBiped {
	private final ModelRenderer leftarm;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;
	private final ModelRenderer gorkarightleg;
	private final ModelRenderer cube_r1;
	private final ModelRenderer gorkaleftleg;
	private final ModelRenderer cube_r2;

	public Gorka4Boots() {
		textureWidth = 128;
		textureHeight = 128;

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.0F);
		

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		

		gorkarightleg = new ModelRenderer(this);
		gorkarightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		gorkarightleg.cubeList.add(new ModelBox(gorkarightleg, 0, 61, -2.0F, 10.0F, -2.75F, 4, 2, 1, 0.0F, false));
		gorkarightleg.cubeList.add(new ModelBox(gorkarightleg, 28, 52, -2.0F, 9.85F, -2.0F, 4, 2, 4, 0.17F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-1.0F, 10.7071F, -3.4571F);
		gorkarightleg.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.7854F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 65, 67, -1.0F, -2.0F, 0.0F, 4, 1, 1, 0.0F, false));

		gorkaleftleg = new ModelRenderer(this);
		gorkaleftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		gorkaleftleg.cubeList.add(new ModelBox(gorkaleftleg, 6, 48, -2.0F, 10.0F, -2.75F, 4, 2, 1, 0.0F, false));
		gorkaleftleg.cubeList.add(new ModelBox(gorkaleftleg, 48, 32, -2.0F, 9.85F, -2.0F, 4, 2, 4, 0.17F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-1.0F, 10.7071F, -3.4571F);
		gorkaleftleg.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.7854F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 40, 52, -1.0F, -2.0F, 0.0F, 4, 1, 1, 0.0F, false));
		
		bipedLeftArm.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedBody.isHidden = true;
		bipedLeftLeg = gorkaleftleg;
		bipedRightLeg = gorkarightleg;
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