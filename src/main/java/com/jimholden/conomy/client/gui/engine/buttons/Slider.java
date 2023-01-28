package com.jimholden.conomy.client.gui.engine.buttons;

import java.awt.Color;

import javax.vecmath.Vector2d;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Slider extends ConomyButton implements IProgress {

	public static final ResourceLocation HIGH_RES_BUTTON = new ResourceLocation(
			Reference.MOD_ID + ":textures/gui/highresgui.png");

	public BasicAnimationTimer popEffectTimer = new BasicAnimationTimer(20, 0);
	public BasicAnimationTimer fillerTimer = new BasicAnimationTimer(15, 0);
	public BasicAnimationTimer bat = new BasicAnimationTimer(45, 0);
	public double size;
	
	public double originalMouseX;
	
	
	public double value = 0.0;

	public Slider(int buttonId, double x, double y, IInfoDisplay info, double size, GuiPage gp) {
		super(buttonId, x, y, info, gp);
		this.size = size;
		
		this.width = 10+size*11;
		this.height = 0.5+size/1.5;
	 
	}
	
	public Slider(int buttonId, double x, double y, IInfoDisplay info, double size) {
		super(buttonId, x, y, info, null);
		this.size = size;
		
		this.width = 10+size*11;
		this.height = 0.5+size/1.5;
	 
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		
		this.originalMouseX = mouseX;
		boolean result = this.enabled && this.visible && isMouseOver();
		//if (result)
			//toggle();
		return result;
	}

	public static boolean mipmap = false;

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(!getPageVisibiity()) return;
		if (this.visible) {

			
			 
			
			 
			double muV = (1 - Math.cos(bat.mcInterp() * Math.PI)) / 2;

			double mu2 = Math.max(0.9, 1 - muV) * 1.05;
			muV = Math.max(0.9, muV);

			// double size = size*muV;

			FontRenderer fontrenderer = mc.fontRenderer;

			// mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			/*
			 * GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			 * 
			 * GlStateManager.enableAlpha(); GlStateManager.enableBlend();
			 * GlStateManager.enableDepth(); GlStateManager.enableTexture2D();
			 * 
			 * Minecraft.getMinecraft().getTextureManager().bindTexture(HIGH_RES_BUTTON);
			 * GUItil.drawTexturedModalIcon(this.x, this.y, 31.4, 0, 17.6, 17.6, muV*0.4);
			 * GUItil.drawTexturedModalIcon(this.x, this.y, 48.75, 0, 17.6, 17.6, muV*0.4);
			 */
			
			
			
			double muR = (1 - Math.cos(fillerTimer.mcInterp() * Math.PI)) / 2;
			
			double scalar = size/17.6;
			
			double ninety = 95*scalar;
			double slidingInterp = (ninety + -ninety*getProgress()*2);
			
			double tX = (this.x- slidingInterp) - mouseX;
			double tY = this.y - mouseY;
			double l = Math.sqrt(tX * tX + tY * tY);

			this.hovered = (l <= (size*2)) || (this.hovered && Mouse.isButtonDown(0));
			int i = this.getHoverState(this.hovered);
			
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
			
			if(this.hovered && Mouse.isButtonDown(0)) {
				double newValue = (mouseX-this.x+ninety)/(2*ninety);
				
				double change = 0.0;
				if(newValue > 1.0) {
					setProgress(1.0);
				} else if (newValue < 0.0) {
					setProgress(0.0);
				} else {
					setProgress(newValue);
				}
			}
			
			
			// GlStateManager.enableBlend();
			// GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
			// GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
			// GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			// GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
			// GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

			if (!this.hovered) {
				bat.tick();
			} else {
				bat.reverseTick();
			}

			/*
			if (checked()) {
				fillerTimer.tick(3);
			} else {
				fillerTimer.reverseTick(3);
			}*/

			if (fillerTimer.atMax()) {
				popEffectTimer.tick(5);
			} else {
				popEffectTimer.reset();
			}

			muR = (1 - Math.cos(fillerTimer.mcInterp() * Math.PI)) / 2;
			// System.out.println(muR);

			// GUItil.drawScaledCenteredString(fontrenderer, "%" + ((int) (this.getProgress()*100)), this.x, this.y-5, 0xffffff, (float) size/15.0f);
	           
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			// GlStateManager.enab

			GlStateManager.enableTexture2D();

			Minecraft.getMinecraft().getTextureManager().bindTexture(Slider.HIGH_RES_BUTTON);

			Minecraft.getMinecraft().getTextureManager().getTexture(Slider.HIGH_RES_BUTTON).setBlurMipmap(true,
					true);
			//GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
			// GL11.GL_LINEAR);
			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
			// GL11.GL_LINEAR);

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
			// ystem.out.println("yo");

			GUItil.drawTexturedModalIcon(this.x, this.y, 0, 19.5, 100, 4, scalar);
			
			GUItil.drawTexturedModalIcon(this.x - slidingInterp, this.y, 135, 0, 17.9, 17.5,
					mu2 * scalar*0.4);

			/// if(fillerTimer.time != -1) GUItil.drawTexturedModalIcon(this.x, this.y,
			/// 48.85, 0, 17.6, 17.6, muR*scalar*.9);

			/*
			 * GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
			 * 
			 * 
			 * GUItil.renderRoundedRectangle(Color.WHITE, 1.0, this.x-size, this.y-size,
			 * this.x+size, this.y+size, 3, 0.2); //GUItil.renderCircleOutline(Color.WHITE,
			 * 1.0, this.x, this.y, size*(muV), (size-0.75)*muV);
			 * 
			 * 
			 * GUItil.renderRoundedRectangle(Color.GREEN, 1.0*muR, this.x-size*0.8*muR,
			 * this.y-size*0.8*muR, this.x+size*0.8*muR, this.y+size*0.8*muR, 2*muR,
			 * 3.9*muR);
			 * 
			 * 
			 * GL11.glDisable(GL11.GL_POLYGON_SMOOTH); //GUItil.renderCircle(Color.WHITE,
			 * 1.0, this.x, this.y, size, size-3);
			 */
			
			
			 super.drawButton(mc, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
    {
		
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.BUTTON_F1, 1.0F));
    }


	@Override
	public double getProgress() {
		return this.value;
	}

	@Override
	public void setProgress(double v) {
		this.value = v;
	}

}
