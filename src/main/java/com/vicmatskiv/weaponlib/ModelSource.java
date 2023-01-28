package com.vicmatskiv.weaponlib;

import java.util.List;

import net.minecraft.client.model.ModelBase;

public interface ModelSource {

	public List<Tuple<ModelBase, String>> getTexturedModels();
	
	public CustomRenderer<?> getPostRenderer();
}
