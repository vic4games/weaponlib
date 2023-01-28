package com.jimholden.conomy.client.gui.engine.buttons;

import java.awt.Color;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;

import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;

public class RadioButton extends ConomyButton implements ICheckable{

	public BasicAnimationTimer popEffectTimer = new BasicAnimationTimer(30, 0);
	public BasicAnimationTimer fillerTimer = new BasicAnimationTimer(45, 0);
	public BasicAnimationTimer bat = new BasicAnimationTimer(45, 0);
	public double size;
	public boolean ticked = false;
	
	public RadioButton(int buttonId, double x, double y, IInfoDisplay info, double size, GuiPage gp) {
		super(buttonId, x, y, info, gp);
		this.size = size;
		
		this.width = 8+size*2.0;
		this.height = 4+size;
		
	}
	
	public RadioButton(int buttonId, double x, double y, IInfoDisplay info, double size) {
		super(buttonId, x, y, info, null);
		this.size = size;
		
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
	          muV = Math.max(0.8, muV);
			
	          //  double size = size*muV;
	            
	          
	          
	         

	            
	            
	            FontRenderer fontrenderer = mc.fontRenderer;
	           
	            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	           
	            double tX = this.x-mouseX;
	            double tY = this.y-mouseY;
	            double l = Math.sqrt(tX*tX + tY*tY);
	            
	            
	            
	            this.hovered = l <= size;
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
	            

	            
	            GlStateManager.enableAlpha();
	            GlStateManager.enableBlend();
	            GlStateManager.enableDepth();
	            GlStateManager.enableTexture2D();
	           
	            double scalar = size/17.6;
	            
	            Minecraft.getMinecraft().getTextureManager().bindTexture(ToggleSwitch.HIGH_RES_BUTTON);
	            GUItil.drawTexturedModalIcon(this.x, this.y, 33.3, 0, 18.5, 17.6, muV*scalar);
	            
	            
	            GlStateManager.color(1.0f, 1.0f, 1.0f, (float) (1-pop)*0.5f);
	            if(checked() && popEffectTimer.time != -1) GUItil.drawTexturedModalIcon(this.x, this.y, 51.7, 0, 16.3, 17.6, (1.0+pop)*scalar);
	            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	            
	            if(fillerTimer.time != -1) GUItil.drawTexturedModalIcon(this.x, this.y, 51.7, 0, 16.3, 17.6, muR*scalar*.9);
	            
	            
	            /*
	            
	            GlStateManager.disableDepth();
	            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA_SATURATE, GL11.GL_ONE);
	            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
	            GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
	            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            GUItil.renderCircleOutline(Color.WHITE, 1.0, this.x, this.y, size*(muV), (size-0.75)*muV);
	            GlStateManager.enableDepth();
	            
	            
	            GUItil.renderCircle(Color.GREEN, 1.0, (double) this.x-0.1, (double) this.y-0.1, (size/2)*muR);
	            //GUItil.renderCircleOutline(Color.GREEN, 1.0, (double) this.x-0.1, (double) this.y-0.1, (size/2)*muR, 0);
//	            /GUItil.renderCircleOutline(Color.GREEN, 0.3, (double) this.x-0.1, (double) this.y-0.1, (size+(3*muR))*muR, size+(3*popEffectTimer.mcInterp())*muR);
	            
	            
	            
	           // GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
	            //GUItil.renderCircle(Color.WHITE, 1.0, this.x, this.y, size, size-3);
	            */
	            super.drawButton(mc, mouseX, mouseY, partialTicks);
	        }
	}

	@Override
	public boolean checked() {
		
		return this.ticked;
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
    {
		
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.BUTTON_F1, 1.0F));
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
