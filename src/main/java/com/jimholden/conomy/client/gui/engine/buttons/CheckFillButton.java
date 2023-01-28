package com.jimholden.conomy.client.gui.engine.buttons;


import java.awt.Color;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class CheckFillButton extends ConomyButton implements ICheckable{

	public BasicAnimationTimer popEffectTimer = new BasicAnimationTimer(30, 0);
	public BasicAnimationTimer fillerTimer = new BasicAnimationTimer(45, 0);
	public BasicAnimationTimer bat = new BasicAnimationTimer(15, 0);
	public double size;
	public boolean ticked = false;
	
	public Color color;
	public String stringValue;
	
	
	public void setColor(Color c) {
		this.color = c;
	}
	
	public CheckFillButton(int buttonId, String word, double x, double y, IInfoDisplay info, double size, GuiPage gp) {
		super(buttonId, x, y, info, gp);
		this.stringValue = word;
		this.size = size;
		
		this.width = 8+size*2.0;
		this.height = 4+size;
	}
	
	public CheckFillButton(int buttonId, String word, double x, double y, IInfoDisplay info, double size) {
		super(buttonId, x, y, info, null);
		this.size = size;
		this.stringValue = word;
		
		this.width = 8+size*2.0;
		this.height = 4+size;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
		
       boolean result = this.enabled && this.visible && isMouseOver();
       if(result) toggle();
       return result;
    }

	
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(!getPageVisibiity()) return;
		 if (this.visible)
	        {
			 
	
			 
			 double muV = (1 - Math.cos(bat.mcInterp() * Math.PI)) / 2;
	          muV = Math.max(0.9, muV);
			
	          //  double size = size*muV;
	            
	          
	          
	         

	            
	            
	            FontRenderer fontrenderer = mc.fontRenderer;
	           
	            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	           
	            double tX = this.x-mouseX;
	            double tY = this.y-mouseY;
	            double l = Math.sqrt(tX*tX + tY*tY);
	            
	            
	           // this.hovered = mouseX > this.x-size && mouseY-size > this.y && mouseX < this.x+size && mouseY < this.y+size;
	            this.hovered = l <= size*1.5;
	            int i = this.getHoverState(this.hovered);
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	            

	            
	            if(!this.hovered) {
	            	bat.tick();
	            } else {
	            	bat.reverseTick();
	            }
	            
	            if(checked()) {
	            	fillerTimer.tick(2);
	            } else {
	            	fillerTimer.reverseTick(2);
	            }
	            
if(fillerTimer.time != -1 && !fillerTimer.atMin()) {
	            	
	            	popEffectTimer.tick(1);
	            } else if (!checked()){
	            	popEffectTimer.reset();
	            }
	            
	            
	            double muR = (1 - Math.cos(fillerTimer.mcInterp() * Math.PI)) / 2;
	            double pop = (1 - Math.cos(popEffectTimer.mcInterp() * Math.PI)) / 2;
	            
	            
	            double scalar = size/17.6;
	            
	            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
	            
	            Minecraft.getMinecraft().getTextureManager().bindTexture(ToggleSwitch.HIGH_RES_BUTTON);
	            
	            double adjSize = size*muV;
	            
	            if(!checked()) {
	            	GUItil.renderRoundedRectangle(this.color, 1.0f, this.x-adjSize*1.75, this.y-adjSize, this.x+adjSize*1.75, this.y+adjSize, 5, 1);
		            
	            } else {
	            	
	            	 GUItil.renderRoundedRectangle(this.color, 1.0f, this.x-adjSize*1.75, this.y-adjSize, this.x+adjSize*1.75, this.y+adjSize, 5, 1);
	            	 GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
	            	GUItil.renderRoundedRectangle(this.color, 1.0f, this.x-adjSize*1.75+0.1, this.y-adjSize+0.1, this.x+adjSize*1.75-0.01, this.y+adjSize-0.01, 5, 8);
	            	 GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		            
	            }
	            
	            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
	            
	            int sColor = 0;
	            if(checked()) {
	            	sColor = 0xffffff;
	            } else {
	            	sColor = color.hashCode();
	            }
	          
	            GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, this.stringValue, this.x, this.y-2.5, sColor, (float) (size/14), true);
	           // GUItil.drawTexturedModalIcon(this.x, this.y, 154.2, 0, 19, 17.6, muV*scalar);
	            
	            GlStateManager.color(1.0f, 1.0f, 1.0f, (float) (1-pop)*0.5f);
	          //  if(checked() && popEffectTimer.time != -1) GUItil.drawTexturedModalIcon(this.x+(0.35*scalar), this.y, 174.6, 0, 13.8, 17.6, (1+pop)*scalar);
	            
	            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	            
	            
	          //  if(fillerTimer.time != -1) GUItil.drawTexturedModalIcon(this.x+(0.35*scalar), this.y, 174.6, 0, 13.8, 17.6, muR*scalar*.9);
	            
	            
	            
	            /*
	            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
	            
	            
	            GUItil.renderRoundedRectangle(Color.WHITE, 1.0, this.x-size, this.y-size, this.x+size, this.y+size, 3, 0.2);
	            //GUItil.renderCircleOutline(Color.WHITE, 1.0, this.x, this.y, size*(muV), (size-0.75)*muV);
	            
	            
	            GUItil.renderRoundedRectangle(Color.GREEN, 1.0*muR, this.x-size*0.8*muR, this.y-size*0.8*muR, this.x+size*0.8*muR, this.y+size*0.8*muR, 2*muR, 3.9*muR);
	            
	            
	            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
	            //GUItil.renderCircle(Color.WHITE, 1.0, this.x, this.y, size, size-3);
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
	public boolean checked() {
		
		return this.ticked;
	}

	@Override
	public void toggle() {
		if(this.ticked) {
			this.ticked = false;
		} else this.ticked = true;
	}

	@Override
	public void setChecked(boolean state) {
		this.ticked = state;
	}

}

