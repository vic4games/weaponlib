package com.jimholden.conomy.client.gui;

import com.jimholden.conomy.client.gui.engine.AdvancedGUI;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.client.gui.engine.buttons.AdvancedButton;
import com.jimholden.conomy.client.gui.engine.buttons.Alignment;
import com.jimholden.conomy.client.gui.engine.buttons.CheckButton;
import com.jimholden.conomy.client.gui.engine.buttons.RadioButton;
import com.jimholden.conomy.client.gui.engine.buttons.SimpleButton;
import com.jimholden.conomy.client.gui.engine.buttons.Slider;
import com.jimholden.conomy.client.gui.engine.buttons.ToggleSwitch;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.client.gui.engine.fields.ConomyTextField;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class AdvancedTestGUI extends AdvancedGUI {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/generalicosheet.png");

	public AdvancedTestGUI(EntityPlayer p) {
		super(p);
	}
	
	@Override
	public void initGui() {
		
		super.initGui();
		
		GuiPage main = writePage(250, 100);
		
		SimpleButton helloWorld = new SimpleButton(0, FrameAlignment.CENTER, new StringElement("Hello", Alignment.CENTER, 0xffffff, 0.5), 5, main);
		SimpleButton helloWorld2 = new SimpleButton(0, FrameAlignment.CENTER, new StringElement("Bro", Alignment.CENTER, 0xffffff, 0.5), 5, main);
		
		main.registerButton(helloWorld);
		main.registerButton(helloWorld2);
	
		
	}
	
	@Override
	public void buildGUI() {
		
	
	}
	
	

}
