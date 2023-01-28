// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports
package com.jimholden.conomy.items.models.pants;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class Gorka4Pants extends ModelBiped {
	private final ModelRenderer gorkarightleg;
	private final ModelRenderer gorkaleftleg;

	public Gorka4Pants() {
		textureWidth = 128;
		textureHeight = 128;

		gorkarightleg = new ModelRenderer(this);
		gorkarightleg.setRotationPoint(-2.0F, 12.0F, 0.0F);
		gorkarightleg.cubeList.add(new ModelBox(gorkarightleg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
		gorkarightleg.cubeList.add(new ModelBox(gorkarightleg, 24, 0, -2.25F, 1.25F, -2.25F, 2, 4, 2, 0.0F, false));

		gorkaleftleg = new ModelRenderer(this);
		gorkaleftleg.setRotationPoint(2.0F, 12.0F, 0.0F);
		gorkaleftleg.cubeList.add(new ModelBox(gorkaleftleg, 24, 24, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
		gorkaleftleg.cubeList.add(new ModelBox(gorkaleftleg, 0, 0, 0.25F, 1.25F, -2.25F, 2, 4, 2, 0.0F, false));
		
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