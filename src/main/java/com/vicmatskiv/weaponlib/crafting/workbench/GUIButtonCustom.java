package com.vicmatskiv.weaponlib.crafting.workbench;

import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper.StringAlignment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GUIButtonCustom extends GuiButton {
	
	
	private ResourceLocation loc;
	
	private int standardU, standardV, hoveredU, hoveredV, disabledU, disabledV;

	private int standardStringColor, hoveredStringColor, disabledStringColor;
	
	private boolean isDisabled = false;

	

	private int texWidth;
	private int texHeight;

	public GUIButtonCustom(ResourceLocation resourceLoc, int buttonId, int x, int y, int widthIn, int heightIn, int imgW, int imgH, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.loc = resourceLoc;
		this.texWidth = imgW;
		this.texHeight = imgH;
	}
	
	public GUIButtonCustom withStandardState(int color, int u, int v) {
		this.standardStringColor = color;
		this.standardU = u;
		this.standardV = v;
		return this;
	}
	
	public GUIButtonCustom withHoveredState(int color, int u, int v) {
		this.hoveredStringColor = color;
		this.hoveredU = u;
		this.hoveredV = v;
		return this;
	}
	
	public GUIButtonCustom withDisabledState(int color, int u, int v) {
		this.disabledStringColor = color;
		this.disabledU = u;
		this.disabledV = v;
		return this;
	}
	
	public void setDisabled(boolean disable) {
		this.isDisabled = disable;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
		//super.drawButton(mc, mouseX, mouseY, partialTicks);
				
		
		
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.loc);
		
		int stringColor = 0;
		
		if(isDisabled) {
			GUIRenderHelper.drawTexturedRect(this.x, this.y, this.disabledU, this.disabledV, this.width, this.height, this.texWidth, this.texHeight);
			stringColor = disabledStringColor;
		} else {
			if(!hovered) {
				
				GUIRenderHelper.drawTexturedRect(this.x, this.y, this.standardU, this.standardV, this.width, this.height, this.texWidth, this.texHeight);
				stringColor = standardStringColor;
			} else {
				GUIRenderHelper.drawTexturedRect(this.x, this.y, this.hoveredU, this.hoveredV, this.width, this.height, this.texWidth, this.texHeight);
				stringColor = hoveredStringColor;
			}
		}
		
		
	
		
		
		GUIRenderHelper.drawScaledString(this.displayString, this.x + this.width/2.0 - Minecraft.getMinecraft().fontRenderer.getStringWidth(this.displayString)/2.0, this.y + this.height/2.0 - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT/2.0, 1.0, stringColor);
		GlStateManager.color(1, 1, 1);
	}
	
	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		super.drawButtonForegroundLayer(mouseX, mouseY);
	}

}
