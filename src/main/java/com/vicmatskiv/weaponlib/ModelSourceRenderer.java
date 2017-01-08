package com.vicmatskiv.weaponlib;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public abstract class ModelSourceRenderer implements IBakedModel {
	private ModelResourceLocation resourceLocation;

	protected ModelResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	protected void setResourceLocation(ModelResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

}
