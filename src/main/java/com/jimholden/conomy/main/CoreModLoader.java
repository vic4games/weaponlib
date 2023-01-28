package com.jimholden.conomy.main;

import java.io.File;

import java.util.Map;

import com.jimholden.conomy.Main;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@IFMLLoadingPlugin.MCVersion(value = "1.12.2")
public class CoreModLoader implements IFMLLoadingPlugin {

	public static File mcDir;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "com.jimholden.conomy.main.CoreModInjector" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(final Map<String, Object> data) {
		mcDir = (File)data.get("mcLocation");
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}


}