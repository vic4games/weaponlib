package com.vicmatskiv.weaponlib.animation.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiRenderUtil {
	
	public static void drawScaledString(FontRenderer fr, String text, double x, double y, double scale, int color) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, scale);
		fr.drawStringWithShadow(text, 0, 0, color);
		GlStateManager.popMatrix();
	}

}
