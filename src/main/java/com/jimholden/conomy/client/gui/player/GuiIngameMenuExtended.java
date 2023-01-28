package com.jimholden.conomy.client.gui.player;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;

public class GuiIngameMenuExtended extends GuiIngameMenu {
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		System.out.println("allo mes amis");
		if(keyCode == Keyboard.KEY_ESCAPE) {
			Minecraft.getMinecraft().displayGuiScreen(new DeathGui(Minecraft.getMinecraft().player));
		}
		super.keyTyped(typedChar, keyCode);
	}
}
