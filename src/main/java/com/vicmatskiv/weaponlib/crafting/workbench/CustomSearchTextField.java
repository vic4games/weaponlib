package com.vicmatskiv.weaponlib.crafting.workbench;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class CustomSearchTextField extends GuiTextField {

	public CustomSearchTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width,
			int par6Height) {
		super(componentId, fontrendererObj, x, y, par5Width, par6Height);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void drawTextBox() {
		// TODO Auto-generated method stub
		//super.drawTextBox();
		
		if(getText().length() == 0 && !isFocused()) {
			drawString(Minecraft.getMinecraft().fontRenderer, "Search Items...", this.x + 3, this.y + 2, 0xFFFFFF);
			
		} else if(isFocused() && getText().length() == 0) {
			if(System.currentTimeMillis()%1000 < 500) {
				drawString(Minecraft.getMinecraft().fontRenderer, "_", this.x + 3, this.y + 2, 0xFFFFFF);
				
			}
			
		} else if(getText().length() != 0) {
			drawString(Minecraft.getMinecraft().fontRenderer, getText(), this.x + 3, this.y + 2, 0xFFFFFF);
			if(System.currentTimeMillis()%1000 < 500) {	
				drawString(Minecraft.getMinecraft().fontRenderer, "_", this.x + 3 + Minecraft.getMinecraft().fontRenderer.getStringWidth(getText()), this.y + 2, 0xFFFFFF);
			}
			
			
			
		}
		
		
		
	}

}
