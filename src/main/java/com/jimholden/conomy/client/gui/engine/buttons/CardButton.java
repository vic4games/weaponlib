package com.jimholden.conomy.client.gui.engine.buttons;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class CardButton extends AdvancedButton {
	
	public BasicAnimationTimer hoverTimerOne = new BasicAnimationTimer(10);
	public BasicAnimationTimer hoverTimerTwo = new BasicAnimationTimer(50);
	public BasicAnimationTimer loopingTimer = new BasicAnimationTimer(720);
	
	private ArrayList<StringElement> text = new ArrayList<>();
	private ArrayList<Double> textRoom = new ArrayList<>();

	public CardButton(Icon ico, int buttonId, FrameAlignment fa, IInfoDisplay info, int realSize, GuiPage gp) {
		super(ico, buttonId, fa, info, realSize, gp);
		// TODO Auto-generated constructor stub
	}
	
	public void addTextRow(StringElement s, double space) {
		String main = s.getString();
		int wrapWidth = 15;
		if(s.getString().length() > wrapWidth) {
			while(main.length() > wrapWidth) {
				int iP = main.lastIndexOf(" " , wrapWidth);
				this.text.add(new StringElement(main.substring(0, iP), s.align, s.color, s.scale, s.parent));
				main = main.substring(iP, main.length());
				this.textRoom.add(4.0);
			}
			if(main.length() > 0) {
				this.text.add(new StringElement(main, s.align, s.color, s.scale, s.parent));
				this.textRoom.add(space);
			}
		} else {
			this.text.add(s);
			this.textRoom.add(space);
		}
		
		
		
		
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
	            
	            
	            
	            
	            
	            
	            FontRenderer fontrenderer = mc.fontRenderer;
	            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	            double mSize = rSize-(size/rSize);
	            this.hovered = mouseX >= (this.x-(mSize)) && mouseY >= (this.y-mSize) && mouseX < this.x + mSize && mouseY < this.y + mSize;
	            
	            boolean shouldGoFlag = true;
	            if(parentPage != null) {
	            	shouldGoFlag = parentPage.hoa.atMax();
	            }
	            
	           if(this.hovered && shouldGoFlag) {
	        	   this.hoverTimerOne.tick();
	           } else {
	        	   this.hoverTimerOne.reverseTick();
	           }
	           if(this.hoverTimerOne.atMax()) {
	        	   loopingTimer.tick();
	        	   this.hoverTimerTwo.tick();
	           } else {
	        	  
	        	   this.hoverTimerTwo.reverseTick();
	           }
	           
	           if(loopingTimer.atMax()) loopingTimer.reset();
	           	
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
	           
	            Color white = new Color(1.0f, 1.0f, 1.0f);
	            
	            Color lightBlue = new Color(0x7ed6df);
	            
	            Color aGrey = new Color(0x2f3640).darker();
	            Color lGrey = new Color(0x353b48);
	            
	            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
	            
	            double mu = hoverTimerTwo.smooth();
	           
	           
	            
	            if(this.hoverTimerOne.atMax()) {
	            	
	            	double cardHeight = 70;
	            	double halfCardHeight = cardHeight/2.0;
	            	
	            	double cardWidth = 25;
	            	double halfCardWidth = cardWidth/2.0;
	            	mu = hoverTimerTwo.smooth();
	            	double lV = 360*this.loopingTimer.mcInterp();
	            	
	            	GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
	            	// inner rectangle
	 				GUItil.renderRoundedRectangle(aGrey, 1.0, this.x-size-(halfCardWidth*mu), this.y-size-(halfCardHeight*mu), this.x+size+(halfCardWidth*mu), this.y+size+(halfCardHeight*mu), 3, 50);
	            	
	            	// GL11.glRectd(this.x-size-(12.5*mu), this.y+size+(35*mu)-1, this.x+size+(12.5*mu)-1, this.y-size-(35*mu));
	 				GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
	            	 GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	             	
	            	GUItil.renderCircle(lGrey.darker(), 1.0, this.x, this.y, 17*mu);
	            	 
	            	GUItil.renderRoundedRectangle(lGrey, 1.0, this.x-size-(halfCardWidth*mu), this.y-size-(halfCardHeight*mu), this.x+size+(halfCardWidth*mu), this.y+size+(halfCardHeight*mu), 2, 1);
	            	
					
	            	 GUItil.renderHalfCircle(lightBlue, 1.0, this.x, this.y, size-(16*mu), size-(17*mu), 90+(45*mu)+lV, 270-(45*mu)+lV);
	 	            GUItil.renderHalfCircle(lightBlue, 1.0, this.x, this.y, size-(16*mu), size-(17*mu), -90+(45*mu)+lV, 90-(45*mu)+lV);
	 	            
	 	           
		  	          
	 	           if(!this.text.isEmpty()) {
	 	        	   double heightBuffer = 0;
	 	        	   for(int n = 0; n < text.size(); ++n) {
	 	        		   text.get(n).renderDisplay(mu, 1.0, -1, (this.y-(cardHeight-12)+heightBuffer));
	 	        		 // GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, s, this.x, (this.y-(cardHeight-10)+heightBuffer)*mu, 0xffffff, (float) ((size/50.0f)*mu));
	 		 	          heightBuffer += this.textRoom.get(n);
	 	        	   }
	 	           }
	 	            
	            } else {
	            	GUItil.renderRoundedRectangle(white, 1.0, this.x-size, this.y-size, this.x+size, this.y+size, size*(hoverTimerOne.mcInterp())+4, 0.5);
		            
	            }
	            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
	            
	            GL11.glPushMatrix();
	            double scale = size/rSize;
	            //GlStateManager.scale(scale, scale, scale);
	            icon.render(this.x/*+(icon.size/2.0)*/, this.y/*+(icon.size/2.0)*/, scale-(0.5*hoverTimerTwo.smooth()));
	            GL11.glPopMatrix();
	           
	            
	           
	            if(triggered()) {
	    			triggerTimer.tick();
	    		}
	    		if(!this.hoverTimerOne.atMax()) infoTag.renderDisplay(triggerTimer.smooth(), 1.0);
	        }
	}

}
