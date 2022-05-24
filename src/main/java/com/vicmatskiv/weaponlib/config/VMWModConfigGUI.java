package com.vicmatskiv.weaponlib.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.vicmatskiv.weaponlib.CommonModContext;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import scala.actors.threadpool.Arrays;

public class VMWModConfigGUI extends GuiConfig {

	public VMWModConfigGUI(GuiScreen parentScreen) {
		super(parentScreen,
				getElements(),
				"mw",
				false,
				false,
				"Finally... a config!");
		
	}
	
	public static List<IConfigElement> getElements() {
		List<IConfigElement> list = new ArrayList<>();
		
		List<IConfigElement> renderItemList = new ArrayList<IConfigElement>();
		
		
		
		renderItemList.add(new DummyConfigElement("Enable Film Grain", true, ConfigGuiType.BOOLEAN, "gaysd"));
		
		DummyCategoryElement dummy = new DummyCategoryElement("bro", "randar", renderItemList);
		list.add(dummy);
		return list;
	}
	
	@Override
	public void initGui() {
		// TODO Auto-generated method stub
		super.initGui();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
	
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	
	@Override
	protected void actionPerformed(GuiButton button) {
		// TODO Auto-generated method stub
		super.actionPerformed(button);
	}

}
