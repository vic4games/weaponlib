package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.model.ModelBiped;

public class CompatibleModelBiped extends ModelBiped {
	
	protected ModelBiped delegate;
	
	protected CompatibleModelBiped(ModelBiped delegate) {
		this.delegate = delegate;
	}

	@Override
	public void renderEars(float p_78110_1_) {
		super.renderEars(p_78110_1_);
	}
	
	@Override
	public void renderCloak(float p_78111_1_) {
		super.renderCloak(p_78111_1_);
	}
}
