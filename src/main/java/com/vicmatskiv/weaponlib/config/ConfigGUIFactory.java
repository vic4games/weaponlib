package com.vicmatskiv.weaponlib.config;

import java.util.Set;

import com.vicmatskiv.weaponlib.config.novel.VMWModConfigGUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class ConfigGUIFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new VMWModConfigGUI(parentScreen);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}
	


}
