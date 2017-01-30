package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.Minecraft;

public final class CompatibilityProvider {
	
	public static Compatibility compatibility = initCompatibility();

	private static Compatibility initCompatibility() {
		String compatibilityClassName = Compatibility.class.getName() + "1_7_10";
		
		Minecraft.getMinecraft();
		
		try {
			return (Compatibility) Class.forName(compatibilityClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find compatible implementation class " + compatibilityClassName);
		}
	}
}
