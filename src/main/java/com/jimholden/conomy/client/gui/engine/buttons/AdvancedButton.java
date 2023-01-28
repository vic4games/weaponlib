package com.jimholden.conomy.client.gui.engine.buttons;

import java.awt.Color;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.client.gui.engine.display.DisplayElement;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiUtils;

public class AdvancedButton extends ConomyButton {
	
	
	public IconSheet.Icon icon;
	
	public BasicAnimationTimer bat = new BasicAnimationTimer(20, 0);
	public int rSize;

	public AdvancedButton(IconSheet.Icon ico, int buttonId, FrameAlignment fa, IInfoDisplay info, int realSize, GuiPage gp) {
		super(buttonId, fa, info, gp);
		this.icon = ico;
		this.rSize = realSize;
		
		
		this.width = rSize;
		this.height = rSize;
		
		setupAlignment(fa);

	}
	
	@Override
	public void setupAlignment(FrameAlignment x) {
		super.setupAlignment(x);
		/*
		if(x == FrameAlignment.CENTER) {
			Vector2d vO = parentPage.getAlignment(x, this);
			
			this.x = vO.x-this.width/1.5;
			this.y = vO.y;
		}*/
		//Vector2d vO = parentPage.getAlignment(x, this);
		
		//this.x = vO.x-this.width/1.5;
		//this.y = vO.y;
		
		

	}
	
	
	
	public AdvancedButton(IconSheet.Icon ico, int buttonId, double x, double y, IInfoDisplay info, int realSize, GuiPage gp) {
		super(buttonId, x, y, info, gp);
		this.icon = ico;
		this.rSize = realSize;
		

	}
	
	public AdvancedButton(IconSheet.Icon ico, int buttonId, double x, double y, IInfoDisplay info, int realSize) {
		super(buttonId, x, y, info, null);
		this.icon = ico;
		this.rSize = realSize;
		
		
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        return this.enabled && this.visible && isMouseOver();
    }

	
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		
		if(!getPageVisibiity()) return;
	
		
		//GlStateManager.scale(sn, sn, sn);
		 if (this.visible)
	        {
			 
			
			 double tF = this.triggerTimer.smooth();
			 
			 double muV = (1 - Math.cos(bat.mcInterp() * Math.PI)) / 2;
	
	          muV = Math.max(0.8, muV);
			
	            double size = rSize*muV*tF;
	            
	            
	            this.width = this.rSize*muV;
	            this.height = (this.rSize*1.1)*muV;
	            
	            GL11.glPushMatrix();
	            double scale = size/rSize;
	            //GlStateManager.scale(scale, scale, scale);
	            icon.render(this.x/*+(icon.size/2.0)*/, this.y/*+(icon.size/2.0)*/, scale);
	            GL11.glPopMatrix();
	            
	            
	            FontRenderer fontrenderer = mc.fontRenderer;
	            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	            double mSize = rSize-(size/rSize);
	            this.hovered = mouseX >= (this.x-(mSize)) && mouseY >= (this.y-mSize) && mouseX < this.x + mSize && mouseY < this.y + mSize;
	            int i = this.getHoverState(this.hovered);
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	            
	            
	            GlStateManager.pushMatrix();
	            
	            GUItil.drawScaledCenteredString(fontrenderer, this.displayString, this.x, this.y+(size*0.7), 0xffffff, (float) size/40.0f);
	            GlStateManager.popMatrix();
	            
	            
	            
	            
	            if(!this.hovered) {
	            	bat.tick();
	            } else {
	            	bat.reverseTick();
	            }
	            GUItil.renderRoundedRectangle(new Color(1.0f, 1.0f, 1.0f), 1.0, this.x-size, this.y-size, this.x+size, this.y+size, 4*muV*tF, 0.5);
	            
	            if(triggered()) {
	    			triggerTimer.tick();
	    		}
	    		infoTag.renderDisplay(triggerTimer.smooth(), 1.0);
	        }
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
    {
		
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.BUTTON_F1, 1.0F));
    }



}
