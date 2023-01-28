package com.jimholden.conomy.client.gui.engine;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;

import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GUItil {

	public static final Color WHITE = Color.WHITE;
	
	public static final ResourceLocation MRC = new ResourceLocation(Reference.MOD_ID + ":textures/gui/mrclogo.png");
	public static final Icon MRC_ICON = IconSheet.getIcon(MRC, 256, 256, 1);
	
	
	public static void performScissor(double x, double y, double width, double height) {
		Minecraft mc = Minecraft.getMinecraft();

		// https://forums.minecraftforge.net/topic/19745-172-scaling-glscissor/

		int intY = (int) y;
		int intX = (int) x;

		int intW = (int) width;
		int intH = (int) height;

		int scaleFactor = 1;

		int k = mc.gameSettings.guiScale;

		if (k == 0) {
			k = 1000;
		}

		while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
				&& mc.displayHeight / (scaleFactor + 1) >= 240) {
			++scaleFactor;
		}

		GL11.glScissor(0, mc.displayHeight - (intY + intH) * scaleFactor, (intW + intX) * scaleFactor,
				intH * scaleFactor);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
	
	public static void endScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public static void renderMRC(double x, double y, int color, double mrc, double size) {

		double aSize = size/50;
		
		String mrcString = mrc + "";
		double offset = (double) -ClientProxy.newFontRenderer.getStringWidth(mrcString);
		MRC_ICON.render(x+offset/(2/size)-(7*size), y+size*3.5, aSize);
		drawScaledCenteredString(ClientProxy.newFontRenderer, mrcString, x+size*4, y, color, (float) size);
	}

	public static void renderRoundedRectangle(Color col, double alpha, double a, double b, double c, double d,
			double cornerRadius, double thickness) {
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		GL11.glColor4d(col.getRed() / 255.0, col.getGreen() / 255.0, col.getBlue() / 255.0, alpha);

		// GL11.glRectd(a + cornerRadius-5, b + thickness, c - cornerRadius+20, b-2);

		GL11.glRectd(a + cornerRadius, b + thickness, c - cornerRadius, b);
		GL11.glRectd(a, d - cornerRadius, a + thickness, b + cornerRadius);
		GL11.glRectd(a + cornerRadius, d, c - cornerRadius, d - thickness);
		GL11.glRectd(c - thickness, d - cornerRadius, c, b + cornerRadius);

		GUItil.renderHalfCircle(col, alpha, a + cornerRadius, b + cornerRadius, cornerRadius, cornerRadius - thickness,
				0, 90);
		GUItil.renderHalfCircle(col, alpha, a + cornerRadius, d - cornerRadius, cornerRadius, cornerRadius - thickness,
				270, 360);
		GUItil.renderHalfCircle(col, alpha, c - cornerRadius, d - cornerRadius, cornerRadius, cornerRadius - thickness,
				180, 270);
		GUItil.renderHalfCircle(col, alpha, c - cornerRadius, b + cornerRadius, cornerRadius, cornerRadius - thickness,
				90, 180);
	}

	public static void renderRectangle(Color c, double alpha, double x, double y, double width, double height) {
		GlStateManager.disableTexture2D();
		// GlStateManager.disableDepth();
		// GlStateManager.enableAlpha();
		// GlStateManager.enableBlend();
		GL11.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, alpha);
		GL11.glRectd(x, y + height, x + width, y);

	}
	
	public static void renderWavyRectangle(Color c, double alpha, double x, double y, double width, double height) {
	
		
		GlStateManager.disableTexture2D();
		// GlStateManager.disableDepth();
		// GlStateManager.enableAlpha();
		// GlStateManager.enableBlend();
		
		float red = c.getRed() / 255.0f;
		float blue = c.getBlue() / 255.0f;
		float green = c.getGreen() / 255.0f;
		float alp = (float) alpha;
		
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		
		double subdivisions = 8;
		double period = 1;
		double amp = 0.25;
		double phase = ((Minecraft.getMinecraft().player.ticksExisted%36)/36.0)*2*Math.PI;
		
		double halfWidth = width/2;
		double marker = halfWidth*Math.sin(period*phase)+halfWidth;
		
		
		double eye = 0;
		for(double i = 0; i <= width; i += width/subdivisions) {
			double basis = Math.sin(0.15*(marker-i));
			bb.pos(x+i, y-amp*(basis)+1, 0).color(red, green, blue, alp).endVertex();
			bb.pos(x+i, y+height, 0).color(red, green, blue, alp*0.5f).endVertex();
			eye = i;
		}
		if(eye != width) {
			double basis = Math.sin(0.15*(marker-width));
			bb.pos(x+width, y-amp*(basis)+1, 0).color(red, green, blue, alp).endVertex();
			bb.pos(x+width, y+height, 0).color(red, green, blue, alp*0.5f).endVertex();
		}
		/*
		bb.pos(x, y, 0).color(red, green, blue, alp).endVertex();
		bb.pos(x, y+height, 0).color(red, green, blue, alp).endVertex();
		bb.pos(x+width, y, 0).color(red, green, blue, alp).endVertex();
		*/
		
		/* POLY
		bb.pos(x, y+height, 0).color(red, green, blue, alp).endVertex();
		
		bb.pos(x+width, y+height, 0).color(red, green, blue, alp).endVertex();
		bb.pos(x+width, y, 0).color(red, green, blue, alp).endVertex();
		bb.pos(x, y, 0).color(red, green, blue, alp).endVertex();
		*/
		/*
		double r = Minecraft.getMinecraft().player.ticksExisted%360;
		r = Math.toRadians(r);
		double s = 19;
		double h = 0.5;
	
		
		double subdivisions = 18;
		
		for(double i = 1; i > 0; i -= 1/subdivisions) {
			System.out.println(i);
			bb.pos(x+width*i, y+Math.sin(s*i+r), 0).color(red, green, blue, alp).endVertex();
			
		}
		*/
		
		
		
		t.draw();
		
		//GL11.glRectd(x, y + height, x + width, y);

	}
	
	public static boolean multisample = false;
	public static int multisampleFBO = 0;
	public static int multiampleTexFBO = 0;
	public static int mRes = 0;
	
	public static void bindMultisample() {
		GL30.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, multisampleFBO);
	}
	
	public static void bindMinecraft() {
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
	}
	
	public static int getWidthTimesHeight() {
		return Minecraft.getMinecraft().displayWidth*Minecraft.getMinecraft().displayHeight;
	}
	
	public static void setupMultisampleBuffer() {
		
		if(multisample && getWidthTimesHeight() == mRes) return;
		System.out.println("Recalculating MSAA buffer...");
		mRes = getWidthTimesHeight();
		
		multisampleFBO  = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, multisampleFBO);
		multiampleTexFBO = GL11.glGenTextures();
		
		int width = Minecraft.getMinecraft().displayWidth;
		int height = Minecraft.getMinecraft().displayHeight;
		
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, multiampleTexFBO);
		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, 4, GL11.GL_RGBA8, width, height, false);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D_MULTISAMPLE, multiampleTexFBO, 0);
		multisample = true;
		
	}
	
	public static void initializeMultisample() {
		int gWidth = Minecraft.getMinecraft().displayWidth;
    	int gHeight = Minecraft.getMinecraft().displayHeight;
    	setupMultisampleBuffer();
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, multisampleFBO);
        GL30.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

        bindMultisample();
	}
	
	public static void unapplyMultisample() {
		int gWidth = Minecraft.getMinecraft().displayWidth;
    	int gHeight = Minecraft.getMinecraft().displayHeight;
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, multisampleFBO);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
        GL30.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

	}
	
	public static void renderProgressBar(Color c, String identity, double x, double y, double width, double height, double current, double max) {
		GlStateManager.enableBlend();
        
		GUItil.renderRectangle(new Color(0xf7f1e3), 0.5, x-1, y-1, width+2, height+2);
		GUItil.renderRectangle(new Color(0xf7f1e3).darker().darker().darker(), 0.9, x, y, width, height);
        
       // GUItil.renderRectangle(c.darker(), 0.7, x, y, width*(current/max), height);
        //GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        if(current != 0) {
        	/*
        	int gWidth = Minecraft.getMinecraft().displayWidth;
        	int gHeight = Minecraft.getMinecraft().displayHeight;
        	setupMultisampleBuffer();*/
        	/*
        	GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, multisampleFBO);
            GL30.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	
            bindMultisample();*/
            
        	
        //	initializeMultisample();
        	GL11.glShadeModel(GL11.GL_SMOOTH);
        	GUItil.renderWavyRectangle(c, 0.8, x, y, width*current/max, height);
    		//unapplyMultisample();
        	//GUItil.renderWavyRectangle(c, 0.8, x, y, width*(current/(double)max), height);
         /*
    		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, multisampleFBO);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
            GL30.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	*/
    		
    		
    		//  GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        }
        
        GlStateManager.enableTexture2D();
        GUItil.drawScaledString(ClientProxy.newFontRenderer, identity, x+width+2, y-1.5, c.hashCode(), 0.5f, true);
        
        GUItil.drawScaledString(ClientProxy.newFontRenderer, current + "/" + max, x+width+2, y+2, 0xffffff, 0.5f, true);
       
		
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        
	}
	

	
	public static void renderCircle(Color c, double alpha, double x, double y, double radius) {
		renderPolygon(c, 22, alpha, x, y, radius);
	}
	
	public static void renderPolygon(Color c, int sides, double alpha, double x, double y, double radius) {
		renderPolygon(c, sides, 0.0, alpha, x, y, radius);
	}

	public static void renderPolygon(Color c, int sides, double angularOffset, double alpha, double x, double y, double radius) {
//https://forums.minecraftforge.net/topic/37625-189draw-a-simple-circle/
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		Tessellator t = Tessellator.getInstance();
		BufferBuilder b = t.getBuffer();
		float red = c.getRed() / 255.0f;
		float blue = c.getBlue() / 255.0f;
		float green = c.getGreen() / 255.0f;

		b.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		b.pos(x, y, 0).color(red, green, blue, (float) alpha).endVertex();

		
		for (int i = 0; i <= sides; i++) {
			double angle = (Math.PI * 2 * i / sides) + Math.toRadians(180) + angularOffset;
			b.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).color(red, green, blue, (float) alpha)
					.endVertex();
		}
		t.draw();

	}

	public static void renderCircleOutline(Color c, double alpha, double x, double y, double outerRadius,
			double innerRadius) {
		renderHalfCircle(c, alpha, x, y, outerRadius, innerRadius, 0, 360);
	}

	public static void renderHalfCircle(Color c, double alpha, double x, double y, double outerRadius,
			double innerRadius, double beginAngle, double finishAngle) {

		// System.out.println("fuck");

		float red = c.getRed() / 255.0f;
		float blue = c.getBlue() / 255.0f;
		float green = c.getGreen() / 255.0f;
		// float alpha = c.getAlpha()/255.0f;

		GL11.glPushMatrix();

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA_SATURATE, GL11.GL_ONE);

		// GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		/*
		 * GL11.glEnable(GL13.GL_MULTISAMPLE);
		 * GL11.glHint(NVMultisampleFilterHint.GL_MULTISAMPLE_FILTER_HINT_NV,
		 * GL11.GL_NICEST);
		 * System.out.println(GL11.glGetInteger(GL13.GL_SAMPLE_BUFFERS));
		 */
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		double endAng = 0;
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		for (double a = beginAngle; a < finishAngle; a += 12) {
			double cos = -Math.cos(Math.toRadians(a)) * outerRadius;
			double sin = -Math.sin(Math.toRadians(a)) * outerRadius;

			double cosI = -Math.cos(Math.toRadians(a)) * innerRadius;
			double sinI = -Math.sin(Math.toRadians(a)) * innerRadius;

			bb.pos(x + cos, y + sin, 0).color(red, green, blue, (float) alpha).endVertex();
			bb.pos(x + cosI, y + sinI, 0).color(red, green, blue, (float) alpha).endVertex();

			endAng = a;
		}

		if (endAng != finishAngle) {
			double cos = -Math.cos(Math.toRadians(finishAngle)) * outerRadius;
			double sin = -Math.sin(Math.toRadians(finishAngle)) * outerRadius;

			double cosI = -Math.cos(Math.toRadians(finishAngle)) * innerRadius;
			double sinI = -Math.sin(Math.toRadians(finishAngle)) * innerRadius;

			bb.pos(x + cos, y + sin, 0).color(red, green, blue, (float) alpha).endVertex();
			bb.pos(x + cosI, y + sinI, 0).color(red, green, blue, (float) alpha).endVertex();
		}

		t.draw();

		// GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GlStateManager.disableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GL11.glPopMatrix();
	}

	public static void drawTexturedModalIcon(double x, double y, double textureX, double textureY, double width,
			double height, double scale) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;

		double widthL = width * scale;
		double heightL = height * scale;

		// width *= scale;
		// height *= scale;

		// width /= 2;
		// height /= 2;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x - widthL), (double) (y + heightL), (double) 0)
				.tex((double) ((float) (textureX + 0) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + widthL), (double) (y + heightL), (double) 0)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + widthL), (double) (y - heightL), (double) 0)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x - widthL), (double) (y - heightL), (double) 0)
				.tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		tessellator.draw();
	}

	public static void drawTexturedModalRect(double x, double y, double textureX, double textureY, double width,
			double height, double scale) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height * scale), (double) 0)
				.tex((double) ((float) (textureX + 0) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width * scale), (double) (y + height * scale), (double) 0)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width * scale), (double) (y + 0), (double) 0)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) 0)
				.tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		tessellator.draw();
	}

	/**
	 * Renders the specified text to the screen, center-aligned. Args : renderer,
	 * string, x, y, color
	 */
	public static void drawCenteredString(FontRenderer fontRendererIn, String text, double x, double y, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y,
				color);
	}

	public static void drawScaledCenteredString(FontRenderer fontRendererIn, String text, double x, double y, int color,
			float scale) {
		drawScaledCenteredString(fontRendererIn, text, x, y, color, scale, true);
	}

	public static void drawScaledString(FontRenderer fontRendererIn, String text, double x, double y, int color,
			float scale) {
		drawScaledString(fontRendererIn, text, x, y, color, scale, true);
	}

	public static void drawScaledCenteredString(FontRenderer fontRendererIn, String text, double x, double y, int color,
			float scale, boolean shadow) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		double width = fontRendererIn.getStringWidth(text) / 2;
		drawString(fontRendererIn, text, (int) (((x) / scale) - width), (int) ((y / scale)), color, shadow);
		GL11.glPopMatrix();
		GlStateManager.color(1.0f, 1.0f, 1.0f);
	}

	public static void drawScaledString(FontRenderer fontRendererIn, String text, double x, double y, int color,
			float scale, boolean shadow) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		drawString(fontRendererIn, text, (int) (x / scale), (int) (y / scale), color, shadow);
		GL11.glPopMatrix();
	}

	public static void drawString(FontRenderer fontRendererIn, String text, double x, double y, int color,
			boolean shadow) {
		if (shadow) {
			fontRendererIn.drawStringWithShadow(text, (float) x, (float) y, color);
		} else {
			fontRendererIn.drawString(text, (float) x, (float) y, color, false);
		}

	}

}
