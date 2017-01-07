package com.vicmatskiv.weaponlib;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class ModelSourceRenderer implements IBakedModel {
	private ModelResourceLocation resourceLocation;

	protected ModelResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	protected void setResourceLocation(ModelResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

}
