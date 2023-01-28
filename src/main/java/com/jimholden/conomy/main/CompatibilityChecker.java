package com.jimholden.conomy.main;

import net.minecraftforge.fml.common.Loader;

public class CompatibilityChecker {
	
	public static boolean shouldNotInjectGUI() {
		return Loader.isModLoaded("multihotbar");
	}

}
