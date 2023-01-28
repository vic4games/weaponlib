package com.jimholden.conomy.client.gui.engine;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.buttons.ConomyButton;
import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;
import com.jimholden.conomy.client.gui.engine.display.AlignmentBuffer;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.Margins;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.client.gui.engine.elements.DisplayElement;
import com.jimholden.conomy.util.Reference;

import akka.japi.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiPage {

	public ArrayList<GuiElement> elements = new ArrayList<>();
	public AdvancedGUI parentGUI;
	
	public static final int TRANSITION_TIME = 4;
	
	public BasicAnimationTimer noa = new BasicAnimationTimer(TRANSITION_TIME);
	public BasicAnimationTimer hoa = new BasicAnimationTimer(TRANSITION_TIME);
	
	public boolean isVisible = false;
	
	public double x; 
	public double y;
	public double width;
	public double height;
	
	private AlignmentBuffer alignBuffer;
	
	private static final double DEFAULT_BUFFER = 5;
	
	
	public boolean firedTrigger = false;
	
	// advanced coloring
		public boolean fill = false;
		public int strokeColor = 0x535c68;
		public int fillColor = 0x535c68;
		public double cornerRadius = 4;
		public boolean renderBorder = true;
	
	public GuiPage(double width, double height) {
		
		this.width = width;
		this.height = height;
		
		ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
		this.x = (scaledRes.getScaledWidth_double()/2)-width;
		this.y = (scaledRes.getScaledHeight_double()/2)-height;
		
		this.alignBuffer = new AlignmentBuffer(this);
	}
	
	
	public void advancedDecor(boolean fill, int primary, int secondary, double cornerRadius, boolean renderBorder) {
		this.fill = fill;
		this.strokeColor = primary;
		this.fillColor = secondary;
		this.cornerRadius = cornerRadius;
		this.renderBorder = renderBorder;
	}
	
	
	public Vector2d getAlignment(FrameAlignment align, IHasInfo ihi) {
		Vector2d vO = alignBuffer.getBufferOffset(align);

		if(ihi.getMarginHandler() == null || ihi.getMarginHandler() == Margins.NONE) {
			double xOffset = ihi.getNWidth()+DEFAULT_BUFFER;
			double yOffset = ihi.getNHeight()+DEFAULT_BUFFER;
			alignBuffer.pushBuffer(align, ihi, xOffset, yOffset);
		}
		
		
		
	//	vO = alignBuffer.getBufferOffset(align);
		
		
		double xMargin = 0.0;
		double yMargin = 0.0;
		if(ihi.getMarginHandler() != null) {
			xMargin = ihi.getMarginHandler().getXMargin();
			yMargin = ihi.getMarginHandler().getYMargin();
			
		}
		
		//System.out.println(align + " | " + yMargin);
		
		switch(align) {
		case TOPLEFT:
			vO.x -= ihi.getNWidth()+xMargin;
			vO.y += ihi.getNHeight()+yMargin;
			break;
		case TOPCENTER:
			//vO.x -= ihi.getNWidth()+align.getXMargin();
			
			vO.y += ihi.getNHeight()+yMargin;
			break;
		case TOPRIGHT:
			vO.x -= ihi.getNWidth()+xMargin;
			vO.y += ihi.getNHeight()+yMargin;
			break;
		case CENTER:
			break;
		case BOTTOMLEFT:
			break;
		case BOTTOMCENTER:
			vO.y -= ihi.getNHeight();
			break;
		case BOTTOMRIGHT:
			vO.x -= ihi.getNWidth()/2;
			vO.y -= ihi.getNHeight();
			break;
		
		}
		
		
		
		vO.add(align.getBaseAlignment(this));
		
		return vO;
	}

	public boolean getVisibility() {
		return this.parentGUI.pages.indexOf(this) == this.parentGUI.index;
	}
	
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}
	
	public GuiElement addElement(GuiElement element) {
		this.elements.add(element);
		return element;

	}
	
	public void registerButton(ConomyButton cb) {
		cb.setParentPage(this);
		parentGUI.addToButtonList(cb);
		
	}
	
	public void registerTextField() {
		
	}
	
	public void tick() {
		noa.tick();
		if(noa.atMax()) hoa.tick();
		if(hoa.atMax() && !firedTrigger) {
			firedTrigger = true;
			for(GuiElement e : elements) {
				e.trigger();
			}
			parentGUI.fireTrigger(this);
		}
	}

	private static final ResourceLocation TEXTURES = new ResourceLocation(
			Reference.MOD_ID + ":textures/gui/kbuilder.png");

	public void renderBackground() {
	
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableBlend();
		//Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);

		
		//GUItil.renderHalfCircle(new Color(0xff6b6b), parentGUI.guiLeft, parentGUI.guiTop, 50, 49, 270, 360);
		
		GlStateManager.pushMatrix();
		
		
		
		//double mr = noa.lastPosition() + (noa.position() - noa.lastPosition())*Minecraft.getMinecraft().getRenderPartialTicks();
		double mu = (1 - Math.cos(noa.mcInterp() * Math.PI)) / 2;
		double muV = (1 - Math.cos(hoa.mcInterp() * Math.PI)) / 2;
		
		
		double  rW = this.width*mu;
		double rH = this.height*muV;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		
		double a = sr.getScaledWidth_double()/2-(rW/2);
		double b = sr.getScaledHeight_double()/2-(rH/2);
		double c = a + rW;
		double d = b + rH;
		double r = this.cornerRadius*muV;
		double thickness = 1;
		Color col = new Color(strokeColor);
		
		if(this.fill) GUItil.renderRoundedRectangle(new Color(fillColor), 1.0, a, b, c, d, r+1, r*3);
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		if(this.renderBorder) GUItil.renderRoundedRectangle(col, 1.0, a, b, c, d, r, thickness);
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		
		//GUItil.renderRoundedRectangle(col, a, b, c, d, r, 40);
		
	//	GUItil.renderRoundedRectangle(col, a, b, c, d, r, 50);
		/*
		Color col = new Color(0xfeca57);
		GL11.glColor3d(col.getRed()/255.0, col.getGreen()/255.0, col.getBlue()/255.0);
		GL11.glRectd(a+r, b+thickness, c-r, b);
		GL11.glRectd(a, d-r, a+thickness, b+r);
		GL11.glRectd(a+r, d, c-r, d-thickness);
		GL11.glRectd(c-thickness, d-r, c, b+r);
		
		
		
		GUItil.renderHalfCircle(col, a+r, b+r, r, r-thickness, 0, 90);
		GUItil.renderHalfCircle(col, a+r, d-r, r, r-thickness, 270, 360);
		GUItil.renderHalfCircle(col, c-r, d-r, r, r-thickness, 180, 270);
		GUItil.renderHalfCircle(col, c-r, b+r, r, r-thickness, 90, 180);
		*/
		/*
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		double endAng = 0;
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		
		bb.pos(a, b, 0).color(1.0f, 0f, 0f, 1f).endVertex();
		bb.pos(c, d, 0).color(1.0f, 0f, 0f, 1f).endVertex();
		bb.pos(c, b, 0).color(1.0f, 0f, 0f, 1f).endVertex();
		bb.pos(a, d, 0).color(1.0f, 0f, 0f, 1f).endVertex();
		
		
		
		t.draw();*/
		
		GlStateManager.popMatrix();
	
		GL11.glPushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
		 //parentGUI.drawTexturedModalRect(parentGUI.guiLeft, parentGUI.guiTop, 0, 0, 64, 64, 128, 128);
		GL11.glPopMatrix();
	}

	public void render() {
		if(!getVisibility()) return;
		renderBackground();

		for (GuiElement e : elements) {
			if (!e.isHidden)
				e.render();

		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

}
