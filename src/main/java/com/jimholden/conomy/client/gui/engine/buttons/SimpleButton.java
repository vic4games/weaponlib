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
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;

public class SimpleButton extends ConomyButton implements ICheckable{

	public BasicAnimationTimer popEffectTimer = new BasicAnimationTimer(30, 0);
	public BasicAnimationTimer fillerTimer = new BasicAnimationTimer(45, 0);
	public BasicAnimationTimer bat = new BasicAnimationTimer(45, 0);
	public double size;
	public boolean ticked = false;
	
	public Color buttonColor = Color.RED;
	
	public boolean backTicking = false;
	
	
	public SimpleButton(int buttonId, FrameAlignment align, IInfoDisplay disp, double size, GuiPage gp) {
		super(buttonId, align, disp, gp);
		
		
		
		
		this.size = size;
		
		this.width = 5+size*4;
		this.height = 4+size;
		
		setupAlignment(align);
	}
	
	public void setButtonColor(Color c) {
		this.buttonColor = c;
	}
	
	@Override
	public void setupAlignment(FrameAlignment x) {
		super.setupAlignment(x);
		
		
		
		//System.out.println("Framing: " + this.x + " | " + this.y);
	}
	
	public SimpleButton(int buttonId, double x, double y, IInfoDisplay disp, double size, GuiPage gp) {
		super(buttonId, x, y, disp, gp);
		this.size = size;
		
		this.width = 5+size*4;
		this.height = 4+size;
		
		//System.out.println("Bro: " + this.x + " | " + this.y);
	}
	
	public SimpleButton(int buttonId, double x, double y, IInfoDisplay disp, double size) {
		super(buttonId, x, y, disp, null);
		this.size = size;
		
		this.width = 5+size*4;
		this.height = 4+size;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
		
       boolean result = this.enabled && this.visible && isMouseOver();
       if(result) popEffectTimer.tick();
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
	            
	          
	         // this.popEffectTimer = new BasicAnimationTimer(10, 0);
	         

	            
	            
	            FontRenderer fontrenderer = mc.fontRenderer;
	           
	            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	           // GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
	           
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

	            
	            if(popEffectTimer.time != -1 && !popEffectTimer.atMin() && !backTicking) {
	            	popEffectTimer.tick();
	            	if(popEffectTimer.atMax()) backTicking = true;
	            } else if(backTicking == true && popEffectTimer.time != -1 && !popEffectTimer.atMin()){
	            	popEffectTimer.reverseTick();
	            	
	            	
	            } else {
	            	backTicking = false;
	            }
	            
	            
	            double bater = (((1 - Math.cos(bat.mcInterp() * Math.PI)) / 2)*0.25)+0.75;
	            double muR = (1 - Math.cos(fillerTimer.mcInterp() * Math.PI)) / 2;
	            double pop = (1 - Math.cos(popEffectTimer.mcInterp() * Math.PI)) / 2;
	            pop = Math.min(pop, 0.1);
	            

	            
	            GlStateManager.enableAlpha();
	            GlStateManager.enableBlend();
	            GlStateManager.enableDepth();
	            GlStateManager.enableTexture2D();
	           
	            double scalar = size/17.6;
	            
	            
	            Minecraft.getMinecraft().getTextureManager().bindTexture(ToggleSwitch.HIGH_RES_BUTTON);
	            Minecraft.getMinecraft().getTextureManager().getTexture(ToggleSwitch.HIGH_RES_BUTTON).setBlurMipmap(true,
						true);
	            
	           Color cm = this.buttonColor;
	            
	            float ar = cm.getRed()/255.0f;
	            float ag = cm.getGreen()/255.0f;
	            float ab = cm.getBlue()/255.0f;
	            
	            	GlStateManager.color((float) (ar*bater), (float) (ag*bater), (float) (ab*bater));
	            	//GlStateManager.color((float) (0.5f*bater), (float) (0.5f*bater), (float) (0.2f*bater));
	 	           
	           
	            	
	            GUItil.drawTexturedModalIcon(this.x+getFailOffset(), this.y, 0, 0, 31.5, 17.6, (1-pop)*scalar);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	            
	            //GlStateManager.color(1.0f, 1.0f, 1.0f, (float) (1-pop)*0.5f);
	            //if(checked() && popEffectTimer.time != -1) GUItil.drawTexturedModalIcon(this.x, this.y, 0, 0, 31.5, 17.6, (1.0+pop)*scalar);
	           // GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	            
	           // if(fillerTimer.time != -1) GUItil.drawTexturedModalIcon(this.x, this.y, 51.7, 0, 16.3, 17.6, muR*scalar*.9);
	            
	            
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
	            
	            GlStateManager.pushMatrix();
	            GlStateManager.translate(getFailOffset(), 0.0, 0.0);
	            
	            if(failTimer.atMax()) failTimer.reset();
	    		
	    		if(!failTimer.atMin()) {
	    			
	    			failTimer.tick();
	    			
	    		}
	    		
	    		if(triggered()) {
	    			triggerTimer.tick();
	    		}
	    		infoTag.renderDisplay(1.0-pop, 1.0);
	    		GlStateManager.popMatrix();
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
