package com.jimholden.conomy.client.gui.engine.buttons;

import java.awt.Color;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.display.DisplayElement;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ToggleSwitch extends ConomyButton implements ICheckable, IHasInfo {

	public static final ResourceLocation HIGH_RES_BUTTON = new ResourceLocation(
			Reference.MOD_ID + ":textures/gui/highresgui.png");

	public BasicAnimationTimer popEffectTimer = new BasicAnimationTimer(20, 0);
	public BasicAnimationTimer fillerTimer = new BasicAnimationTimer(15, 0);
	public BasicAnimationTimer bat = new BasicAnimationTimer(45, 0);
	public double size;
	public boolean ticked = false;
	
	public IInfoDisplay info;
	
	public ToggleSwitch(int buttonId, int x, int y, IInfoDisplay de, double size, GuiPage gp) {
		super(buttonId, x, y, de, gp);
		
	
		this.size = size;
		
		
	
		
		this.width = 20*size;
		this.height = 6;
		
		
		this.info = de;
		
		de.setParent(this);
	}

	public ToggleSwitch(int buttonId, int x, int y, IInfoDisplay de, double size) {
		super(buttonId, x, y, de, null);
		
	
		this.size = size;
		
		
	
		
		this.width = 20*size;
		this.height = 6;
		
		
		this.info = de;
		
		de.setParent(this);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {

		boolean result = this.enabled && this.visible && isMouseOver();
		if (result)
			toggle();
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
			double scalar = 0.2;
			double tX = (this.x- (15 * scalar) + ((29 * scalar) * muR)) - mouseX;
			double tY = this.y - mouseY;
			double l = Math.sqrt(tX * tX + tY * tY);

			this.hovered = l <= (size*10);
			int i = this.getHoverState(this.hovered);
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

			if (checked()) {
				fillerTimer.tick(3);
			} else {
				fillerTimer.reverseTick(3);
			}

			if (fillerTimer.atMax()) {
				popEffectTimer.tick(5);
			} else {
				popEffectTimer.reset();
			}

			muR = (1 - Math.cos(fillerTimer.mcInterp() * Math.PI)) / 2;
			// System.out.println(muR);

			
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			// GlStateManager.enab

			GlStateManager.enableTexture2D();

			Minecraft.getMinecraft().getTextureManager().bindTexture(ToggleSwitch.HIGH_RES_BUTTON);

			Minecraft.getMinecraft().getTextureManager().getTexture(ToggleSwitch.HIGH_RES_BUTTON).setBlurMipmap(true,
					true);
			//GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
			// GL11.GL_LINEAR);
			// GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
			// GL11.GL_LINEAR);

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
			// ystem.out.println("yo");

			GUItil.drawTexturedModalIcon(this.x, this.y, 68, 0, 32, 17.6, scalar);
			GUItil.drawTexturedModalIcon(this.x - ((32 * scalar) * (1 - muR)), this.y, 101.8, 0, 31 * muR, 17.6,
					scalar);
			GUItil.drawTexturedModalIcon(this.x - (15 * scalar) + ((29 * scalar) * muR), this.y, 135, 0, 17.9, 17.5,
					mu2 * scalar);

			
			 super.drawButton(mc, mouseX, mouseY, partialTicks);
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

		}
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
    {
		
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.BUTTON_F1, 1.0F));
    }

	@Override
	public boolean checked() {

		return this.ticked;
	}

	@Override
	public void toggle() {
		if (this.ticked) {
			this.ticked = false;
		} else
			this.ticked = true;
	}

	@Override
	public void setChecked(boolean state) {
		this.ticked = state;
	}



}
