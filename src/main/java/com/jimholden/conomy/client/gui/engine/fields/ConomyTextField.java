package com.jimholden.conomy.client.gui.engine.fields;

import java.awt.Color;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IVisibilityGUI;
import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;

import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.Margins;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.util.VectorUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import scala.reflect.internal.Trees.This;

public class ConomyTextField extends GuiTextField implements IHasInfo, IVisibilityGUI{
	
	public IInfoDisplay infoTag;
	public double x;
	public double y;
	public double width;
	public double height;
	
	public double prevCursorX;
	public double cursorX;
	
	public double mouseTimer = 0.0;
	
	public BasicAnimationTimer bat = new BasicAnimationTimer(20,0);
	public boolean animUp = false;
	
	public BasicAnimationTimer bgAnim = new BasicAnimationTimer(35, 0);
	
	
	public double baseWidth;
	
	public boolean pageVisibility = false;
	public GuiPage parentPage;
	
	public int color = 0x576574;
	

	public boolean canExpand = true;
	
	public boolean moneyValidator = false;
	
	@Override
	public void setValidator(Predicate<String> theValidator) {
		// TODO Auto-generated method stub
		super.setValidator(theValidator);
	}
	
	public ConomyTextField(int componentId, FontRenderer fontrendererObj, double x, double y, double height, double width, int maxStringLength, IInfoDisplay disp, GuiPage parentPage, boolean canExpand) {
		super(componentId, fontrendererObj, (int) x, (int) y, (int) width, (int) height);
		
		
		setMaxStringLength(maxStringLength);
		this.x = x;
		this.y = y;
		this.width = width;
		this.baseWidth = width;
		this.height = height;
		this.infoTag = disp;
		this.parentPage = parentPage;
		this.canExpand = canExpand;
		
		if(parentPage != null) {
			System.out.println("adjusting.. " + this.parentPage.getX());
			this.x += parentPage.getX();
			this.y += parentPage.getY();
		}
		
		System.out.println(this.x);

		this.infoTag.setParent(this);
		// TODO Auto-generated constructor stub
	}
	
	
	public boolean isBlank() {
		return this.getText().equals("");
	}
	
	

	private void drawSelectionBox(double startX, double startY, double endX, double endY)
    {
        if (startX < endX)
        {
        	double i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY)
        {
        	double j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.width)
        {
            endX = this.x + this.width;
        }

        if (startX > this.x + this.width)
        {
            startX = this.x + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.color(0.5F, 0.5F, 1.0F, 0.5F);
        GlStateManager.disableTexture2D();
       // GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.XOR);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)startX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)startY, 0.0D).endVertex();
        bufferbuilder.pos((double)startX, (double)startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
	
	public void changeColor(int color) {
		this.color = color;
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		boolean flag = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

		
		double vSFac = this.height/20.0;
		
		
		setFocused(true);
		setCanLoseFocus(true);

            this.setFocused(flag);
       

        if (isFocused() && flag && mouseButton == 0)
        {
            double i = mouseX - this.x;

            /*
            if (this.enableBackgroundDrawing)
            {
                i -= 4;
            }*/

         //   String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            double textWidthh = Minecraft.getMinecraft().fontRenderer.getStringWidth(getText());
			int newPos = (int) Math.round((getText().length()*(mouseX-this.x-4.3)/textWidthh)/vSFac);
            this.setCursorPosition(newPos);
            return true;
        }
        else
        {
            return false;
        }
	}
	
	
	
	private int selectionEnd;
	@Override
	public void drawTextBox() {
		
	
		
		if(!getPageVisibiity()) return;
		
		double vSFac = this.height/20.0;
		
		
		
		// get mouse coordinates
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int i1 = scaledresolution.getScaledWidth();
        int j1 = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * i1 / Minecraft.getMinecraft().displayWidth;
        int mouseY = j1 - Mouse.getY() * j1 / Minecraft.getMinecraft().displayHeight - 1;
		
        
		//super.drawTextBox();
		if(this.getVisible()) {
			
			
			if(isFocused()) {
				this.bgAnim.tick();
			} else if(!bgAnim.atMin()){
				this.bgAnim.reverseTick();
			}
			double bgMu = (1 - Math.cos(bgAnim.mcInterp() * Math.PI)) / 2;
			//System.out.println(bgMu);
			
			
				GlStateManager.disableTexture2D();
				GlStateManager.disableDepth();
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				Color orange = new Color(this.color);
				GlStateManager.color(orange.getRed()/255.0f, orange.getGreen()/255.0f, orange.getBlue()/255.0f, (float) (0.3f*bgMu));
				GL11.glRectd(x+1, y+height-1, x+width-1, y+1);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			
			
			
			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
			GUItil.renderRoundedRectangle(orange, 1.0, x, y, x+width, y+height, 2, 1);
			GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
			
			
			
			if(!getText().equals("")) {
				GUItil.drawScaledString(ClientProxy.newFontRenderer, getText(), x+4, y+(this.height - 8*vSFac) / 2, this.color, 1.0f*(float) vSFac);
				
			} else {
				GUItil.drawScaledString(ClientProxy.newFontRenderer, "_", x+4, y+(this.height - 8*vSFac) / 2, this.color, 1.0f*(float) vSFac);
				
			}
			
			
			
			
			double textWidthh = Minecraft.getMinecraft().fontRenderer.getStringWidth(getText())*vSFac;
			
			if(this.getText().length() < this.getMaxStringLength() && canExpand) {
				this.width = (textWidthh) + this.baseWidth;
			}
			
			
			if(!bat.atMax()) {
				if(this.animUp) bat.tick(2);
				
			} else if(bat.atMax()) {
				animUp = false;
			}
			
			if(!this.animUp) {
				bat.reverseTick(2);
			}
			
			double mu = (1 - Math.cos(bat.mcInterp() * Math.PI)) / 2;
	       
			
			boolean noCursor = false;
			
			
			//System.out.println(this.getCursorPosition());
			
			this.prevCursorX = this.cursorX;
			this.cursorX= x+4.3+(((textWidthh*1.15)/(double) getText().length())*getCursorPosition());
			double cY = y+(this.height - (6*vSFac));
			
			
			boolean noBoxFlag = false;
			if(isFocused()) {
				if(Mouse.isButtonDown(0)) {
					this.mouseTimer += 1;
				} else {
					if(mouseTimer < 40 && this.mouseTimer != 0.0) {
						
						setSelectionPos(getCursorPosition());
						noBoxFlag = true;
					}
					this.mouseTimer = 0;
				}
				
				if(mouseTimer > 40) {
					
					noCursor = true;
					this.cursorX = mouseX;
					cY = mouseY+5;
					
					this.cursorX = Math.max(this.cursorX, x+4);
					if(this.cursorX > x+width) {
						this.cursorX = x+width;
					}
					
					if(cY < y+15) cY = y+15;
					if(cY > y+height) cY = y+height;
					
					
					
					int end = (int) Math.round(((getText().length())*((this.cursorX-this.x-(4.3))/(textWidthh))));
					
				setSelectionPos(end);
				//if(this.selectionEnd > getText().length()) setSelectionPos(get);
					
				} else {
					
				}
			}
			
			
			if(!noCursor) {
				 if(this.getSelectedText().length() > 0) noCursor = true;
			}
			
			double coord = this.prevCursorX + (this.cursorX-this.prevCursorX)*Minecraft.getMinecraft().getRenderPartialTicks();
			
			
			double cursorWidth = 0.5;
			if(!noCursor && isFocused()) GUItil.renderRoundedRectangle(orange, 1.0, coord-0.2, cY-10-(3*mu), coord+0.2, cY+(3*mu), 0, cursorWidth);
			
			double indice = ((textWidthh/(double) getText().length()));
			
			if(getSelectionEnd() != getCursorPosition() && mouseTimer < 40 && mouseTimer != 0.0) {
				setSelectionPos(getCursorPosition());
				
			}
			
			if(getSelectionEnd() != getCursorPosition() && !noBoxFlag) {
				drawSelectionBox(x+3.5+getSelectionEnd()*indice, y+(5*vSFac), x+(getCursorPosition()+0.6)*indice, y+(18*vSFac));
				
			}
			
			GlStateManager.pushMatrix();
			
		
			
			GlStateManager.translate(width/2, height/2, 0.0);
			infoTag.renderDisplay();
			GlStateManager.popMatrix();
			
			//this.drawSelectionBox((int) x, (int) y, (int) x+20, (int) y+29);
			//GUItil.drawScaledString(Minecraft.getMinecraft().fontRenderer, "|", x+6+textWidthh, y+(this.height - 8) / 2, 0x48843, 1.0f);
			
			
		}
	}

	@Override
	public void setSelectionPos(int position) {
		super.setSelectionPos(position);
		position = MathHelper.clamp(position, 0, getText().length());
		this.selectionEnd = position;
	}
	
	@Override
	public void moveCursorBy(int num) {
		
		super.moveCursorBy(num);
	}
	
	  public String getSelectedText()
	    {
	        int i = getCursorPosition() < this.selectionEnd ? getCursorPosition() : this.selectionEnd;
	        int j = getCursorPosition() < this.selectionEnd ? this.selectionEnd : getCursorPosition();
	        return getText().substring(i, j);
	    }
	
	@Override
	public int getSelectionEnd() {
		return this.selectionEnd;
	}
	
	public ConomyTextField setMoneyValdiator() {
		this.moneyValidator = true;
		return this;
		
	}
	
	public boolean validate(char typedChar, int keyCode) {
		try {
			
			if(!moneyValidator) return true;
			if((!Character.isAlphabetic(typedChar) && typedChar != '-') || typedChar == '.') {
				
				if(typedChar == '.' && (getText().length() > 1 ? getText().charAt(getText().length()-1) == '.' : false)) return false;
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public boolean textboxKeyTyped(char typedChar, int keyCode) {
		
		if(!isFocused()) {
			
			return super.textboxKeyTyped(typedChar, keyCode);
		}
		animUp = true;
		
		boolean result = super.textboxKeyTyped(typedChar, keyCode);

		
		//if(Character.isAlphabetic(typedChar)) setSelectionPos(getCursorPosition());
		
		return result;
	}
	
	@Override
	public IInfoDisplay getDisplayElement() {
		return this.infoTag;
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getNWidth() {
		return this.width;
	}

	@Override
	public double getNHeight() {
		double vSFac = this.height/20.0;
		return this.height*(vSFac);
	}
	
	@Override
	public void updateVisibility() {
		if(this.parentPage == null) return;
		if(!this.parentPage.getVisibility()) this.pageVisibility = false;
		else this.pageVisibility = true;
	
	}

	@Override
	public void setParentPage(GuiPage gp) {
		this.parentPage = gp;
		
	}
	
	@Override
	public boolean getPageVisibiity() {
		if(this.parentPage == null) return true;
		return this.pageVisibility;
	}





	@Override
	public void setX(double x) {
		this.x = x;
	}





	@Override
	public void setY(double y) {
		this.y = y;
		
	}
	
	@Override
	public Margins getMarginHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
