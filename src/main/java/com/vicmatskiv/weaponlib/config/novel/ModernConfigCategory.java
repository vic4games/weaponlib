package com.vicmatskiv.weaponlib.config.novel;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ModernConfigCategory extends DummyCategoryElement {

	private String registryKey;
	
	public ModernConfigCategory(String name, String registryKey, List<IConfigElement> childElements) {
		super(name, registryKey, childElements);
		this.registryKey = registryKey + ".name";
	}
	
	public String getCategoryName() {
		return this.name;
	}
	
	@Override
	public String getName() {
		return I18n.format(registryKey);
	}

}
