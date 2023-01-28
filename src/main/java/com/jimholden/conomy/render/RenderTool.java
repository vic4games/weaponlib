package com.jimholden.conomy.render;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderTool {
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static final ResourceLocation HEALTH_GUI = new ResourceLocation(Reference.MOD_ID + ":textures/gui/healthgui.png");
	public static void drawCenteredString(FontRenderer fontRendererIn, String text, float f, float g, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(f - fontRendererIn.getStringWidth(text) / 2), (float)g, color);
    }
	
	/**
     * Draws a textured rectangle at the current z-value.
     */
    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
    	float zLevel = -5.0F;
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double) zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double) zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double) zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double) zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }
	
	public static void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color);
    }
	
	public static void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, int color, float scale) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		drawString(fontRendererIn, text, (int) (x/scale), (int) (y/scale), color);
		GL11.glPopMatrix();
	}
	
	public static void drawTexturedModalRectScaledFloat(float x, float y, int textureX, int textureY, float width, float height, float scaled)
    {
		float zLevel = -5.0F;
		GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        
        x = ((float) x/scaled);
        y = ((float) y/scaled);
        /*
        Vec3d vec1 = new Vec3d((double)(x + 0), (double)(y + height), (double)zLevel);
        vec1 = divide(vec1).scale(scaled);
        Vec3d vec2 = new Vec3d((double)(x + width), (double)(y + height), (double)zLevel);
        vec2 = divide(vec2).scale(scaled);
        Vec3d vec3 = new Vec3d((double)(x + width), (double)(y + 0), (double)zLevel);
        vec3 = divide(vec3).scale(scaled);
        Vec3d vec4 = new Vec3d((double)(x + 0), (double)(y + 0), (double)zLevel);
        vec4 = divide(vec4).scale(scaled);
        
        
        
        bufferbuilder.pos(vec1.x, vec1.y, vec1.z).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(vec2.x, vec2.y, vec2.z).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(vec3.x, vec3.y, vec3.z).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos(vec4.x, vec4.y, vec4.z).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
       	*/
        
        GlStateManager.scale(scaled, scaled, scaled);
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        tessellator.draw();
        GlStateManager.popMatrix();
        
        
    }
	
	public static void renderProgress(float x, float y, float scale, float current, float max, int iconX, float redColor, float blueColor, float greenColor) {
		//Color.HSBtoRGB(hue, saturation, brightness)
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
		RenderTool.drawTexturedModalRectScaledFloat(x-2, y-2, 0, 22, 83, 11, scale);
		
		RenderTool.drawTexturedModalRectScaledFloat(x, y, 0, 0, 80, 8, scale);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.color(redColor, greenColor, blueColor);
    	RenderTool.drawTexturedModalRectScaledFloat(x, y, 0, 8, (80*(current/max)), 8, scale);
    	GlStateManager.color(1.0F, 1.0F, 1.0F);
    	RenderTool.drawTexturedModalRectScaledFloat(x+2, y+1, iconX, 16, 6, 6, scale);
    	drawScaledString(mc.fontRenderer, current + "/" + max, (int) x+10, (int) y+3, 0xFFFFFFFF, 0.5F);
    	mc.getTextureManager().bindTexture(HEALTH_GUI);
    	GlStateManager.popMatrix();
	}

}
