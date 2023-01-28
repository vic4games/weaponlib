package com.jimholden.conomy.client.gui.engine.buttons;

import java.awt.Color;
import java.util.function.Supplier;

import javax.vecmath.Vector2d;

import org.lwjgl.input.Mouse;
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

public class LedgerButton extends ConomyButton implements ICheckable{

	public BasicAnimationTimer popEffectTimer = new BasicAnimationTimer(30, 0);
	public BasicAnimationTimer fillerTimer = new BasicAnimationTimer(45, 0);
	public BasicAnimationTimer bat = new BasicAnimationTimer(45, 0);
	
	public BasicAnimationTimer pulseTimer = new BasicAnimationTimer(360);
	public double size;
	public boolean ticked = false;
	
	public Supplier<Boolean> ledgerIsOn;
	
	public boolean backTicking = false;
	
	public double progress = 0.0;
	public double angle = 0.0;
	
	
	public LedgerButton(int buttonId, FrameAlignment align, IInfoDisplay disp, double size, GuiPage gp) {
		super(buttonId, align, disp, gp);
		
		
		
		
		this.size = size;
		
		this.width = 5+size*4;
		this.height = 4+size;
		
		setupAlignment(align);
	}
	
	
	
	@Override
	public void setupAlignment(FrameAlignment x) {
		super.setupAlignment(x);
		
		
		
		//System.out.println("Framing: " + this.x + " | " + this.y);
	}
	
	public LedgerButton(int buttonId, Supplier<Boolean> ledgerIsOn, double x, double y, IInfoDisplay disp, double size, GuiPage gp) {
		super(buttonId, x, y, disp, gp);
		this.size = size;
		this.ledgerIsOn = ledgerIsOn;
		
		this.width = 5+size*4;
		this.height = 4+size;
		
		//System.out.println("Bro: " + this.x + " | " + this.y);
	}
	
	public LedgerButton(int buttonId, double x, double y, IInfoDisplay disp, double size) {
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

	            
	            
	            FontRenderer fontrenderer = mc.fontRenderer;
	           
	            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	           
	            double tX = this.x-mouseX;
	            double tY = this.y-mouseY;
	            double l = Math.sqrt(tX*tX + tY*tY);
	            
	            
	            double dX = mouseX-this.x;
	            double dY = mouseY-this.y;
	            
	        
	            
	           
	            
	            if(!Mouse.isButtonDown(1)) this.hovered = l <= size;
	           
	            int i = this.getHoverState(this.hovered);
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	            

	            
	            if(this.hovered) {
	            
	            	bat.tick();
	            
	            } else {
	            	bat.reverseTick();
	            }
	             
	            if(!fillerTimer.atMin() && fillerTimer.time != -1) {
	            	fillerTimer.tick();
	            	if(fillerTimer.atMax()) fillerTimer.reset();
	            	
	            }
	            
	            if(popEffectTimer.time != -1 && !popEffectTimer.atMin() && !backTicking) {
	            	popEffectTimer.tick();
	            	if(popEffectTimer.atMax()) backTicking = true;
	            } else if(backTicking == true && popEffectTimer.time != -1 && !popEffectTimer.atMin()){
	            	popEffectTimer.reverseTick();
	            	
	            	
	            } else {
	            	backTicking = false;
	            }
	            if(this.pulseTimer.atMax()) this.pulseTimer.reset();
	            this.pulseTimer.tick();
	            
	            
	            
	            double bater = (((1 - Math.cos(bat.mcInterp() * Math.PI)) / 2)*0.25)+0.75;
	            double muR = (1 - Math.cos(fillerTimer.mcInterp() * Math.PI)) / 2;
	            double pop = (1 - Math.cos(popEffectTimer.mcInterp() * Math.PI)) / 2;
	            pop = Math.min(pop, 0.1);
	            

	            
	            GlStateManager.enableAlpha();
	            GlStateManager.enableBlend();
	            GlStateManager.enableDepth();
	            GlStateManager.enableTexture2D();
	           
	            double scalar = size/17.6;
	            
	            Color niceScreenGray = new Color(0x2f3640).darker().darker();
	            
	       
	            if(this.hovered) {
	            	niceScreenGray = niceScreenGray.darker();
	            }
	          
	            Color niceRed = new Color(0xe84118);
	            Color niceLime = new Color(0x4cd137);
	            GUItil.renderCircle(niceScreenGray, 1.0, this.x, this.y, this.size);
	            
	            boolean flag = true;
	           // GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
	            
	            GUItil.renderCircleOutline(niceScreenGray.darker(), 1.0, this.x, this.y, this.size-1.75, this.size-3.25);
	           
	  	         
	            boolean holdFlag = ((Mouse.isButtonDown(1) && this.hovered) || progress > 0) && this.ledgerIsOn.get();
	            if(holdFlag && Mouse.isButtonDown(1)) {
	            	angle = Math.atan2(dY, dX)*180/Math.PI + 180;
	            }
		          
	  	           
	  	           double sin = Math.sin(0.5*Math.toRadians(this.pulseTimer.time));
	  	           if(sin < 0 ) sin = 0;
	  	           
	  	          this.progress = angle/360.0;
	  	         if(holdFlag) {
	  	        	GlStateManager.shadeModel(GL11.GL_SMOOTH);
	  	        	GUItil.renderHalfCircle(Color.YELLOW, 1.0, this.x, this.y, this.size-2, this.size-3, 0, angle);
	  	        	
	  	        	GUItil.renderCircleOutline(Color.YELLOW, 1.0*sin, this.x, this.y, this.size*0.5, this.size*0.5-0.5);
	  	        	
	  	         }
	  	         
	  	         if(!fillerTimer.atMin()) {
	  	        	GUItil.renderHalfCircle(Color.GREEN, 1.0, this.x, this.y, this.size*1.5, this.size*1.5-2, 0, 360*fillerTimer.smooth());
	  	        	
	  	         }
	  	         
	  	           /*
	  	           if(!bat.atMin()) {
	  	        	  int radioOptions = 5;
	  	        	  double spacing = 5;
	  	        	 
	  	        	  double deg = 360/(double) radioOptions;
	  	        	  
	  	        	  for(int b = 0; b < 6; b++) {
	  	        		 
	  	        		  double start = -deg/2;
	  	        		  double initial = start + deg*b;
	  	        		  
	  	        		GUItil.renderHalfCircle(niceScreenGray.darker(), 1.0, this.x, this.y, 30, this.size+2, initial+spacing, initial + deg);
	  	        	  }
	  	        	   
	  	        	 GUItil.renderHalfCircle(niceScreenGray.darker(), 1.0, this.x, this.y, 30, this.size+2, -30, 30);
	  	        	
	  	        	 GUItil.renderHalfCircle(niceScreenGray.darker(), 1.0, this.x, this.y, this.size*0.5, this.size*0.5-5, 0, bat.mcInterp()*360);
	  	           }*/
	  	        if(!holdFlag) {
	  	        	if(this.ledgerIsOn.get()) {
		            	 GUItil.renderCircleOutline(niceLime, sin, this.x, this.y, this.size-2, this.size-3);
		  	           
		            } else {
		            	 GUItil.renderCircleOutline(niceRed, sin, this.x, this.y, this.size-2, this.size-3);
		  	           
		            }
	  	        }
	            
	         //   GL11.glDisable(GL11.GL_POLYGON_SMOOTH);

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
