package com.jimholden.conomy.client.gui.engine;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.buttons.ToggleSwitch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class CustomFontRenderer extends FontRenderer {
	
	private boolean hasDoneMipMaps = false;

	public CustomFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn,
			boolean unicode) {
		super(gameSettingsIn, location, textureManagerIn, unicode);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	protected float renderDefaultChar(int ch, boolean italic) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		if(!hasDoneMipMaps) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(this.locationFontTexture);
			Minecraft.getMinecraft().getTextureManager().getTexture(this.locationFontTexture).setBlurMipmap(true, true);
			this.hasDoneMipMaps = true;
		}
		return super.renderDefaultChar(ch, italic);
	}

	
}
