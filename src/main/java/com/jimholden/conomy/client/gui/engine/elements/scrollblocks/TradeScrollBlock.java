package com.jimholden.conomy.client.gui.engine.elements.scrollblocks;

import java.awt.Color;

import javax.vecmath.Vector2d;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.buttons.Slider;
import com.jimholden.conomy.client.gui.engine.elements.ScrollBlock;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.economy.TradeUtility;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.economy.data.Trade;
import com.jimholden.conomy.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TradeScrollBlock extends ScrollBlock implements Comparable<TradeScrollBlock> {

	public Trade a;
	public boolean left = false;
	public boolean right = false;
	public boolean buy = false;
	
	public int purchaseQuantity = 1;
	
	public boolean buyMenu = true;
	
	public TradeScrollBlock(Trade a, double height) {
		super(height);
		this.a = a;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void renderScroll(double x, double y, double width, double yOffset) {
		
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int i1 = scaledresolution.getScaledWidth();
        int j1 = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * i1 / Minecraft.getMinecraft().displayWidth;
        int mouseY = j1 - Mouse.getY() * j1 / Minecraft.getMinecraft().displayHeight - 1;
		
        Vector2d rightArrowVector = new Vector2d(x+82, y+6+yOffset);
        Vector2d leftArrowVector = new Vector2d(x+69, y+6+yOffset);
        
        Vector2d buyVector = new Vector2d(x+115, y+4+yOffset);
        
        Vector2d mouseVector = new Vector2d(mouseX, mouseY);
       
        rightArrowVector.sub(mouseVector);
        leftArrowVector.sub(mouseVector);
        buyVector.sub(mouseVector);
       
        right = rightArrowVector.length() < 2;
        left = leftArrowVector.length() < 2;
        buy = buyVector.length() < 4;
        
        double alphaR = 1.0;
        double alphaL = 1.0;
        double alphaB = 1.0;
        if(right) {
        	alphaR = Math.abs(Math.sin(0.3*Minecraft.getMinecraft().player.ticksExisted));
        }
        if(left) {
        	alphaL = Math.abs(Math.sin(0.3*Minecraft.getMinecraft().player.ticksExisted));
        }
        if(this.buy) {
        	alphaB = Math.abs(Math.sin(0.3*Minecraft.getMinecraft().player.ticksExisted));
        }
        
        
        
        
		double scale = 0.8;
		double money = (new TradeUtility()).getCurrentPrice(a.basePrice, a.baseStock, a.currentStock, 0.01)*purchaseQuantity;
		String mString = Interchange.formatUSD(money);
		
		if((ClientProxy.newFontRenderer.getStringWidth(mString)*scale) > width) {
			//double b = (ClientProxy.newFontRenderer.getStringWidth(mString)*scale)/width;
			scale /= (ClientProxy.newFontRenderer.getStringWidth(mString)*scale)/width;
		}
		
		//System.out.println( + " | " + width);
		
		
		float zLevel = 200F;
		
		RenderHelper.enableGUIStandardItemLighting();
		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
		
		
		itemRender.renderItemAndEffectIntoGUI(a.itemStack, (int) x, (int) y);
		RenderHelper.disableStandardItemLighting();
	
	
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		GlStateManager.pushMatrix();
		GUItil.renderPolygon(Color.WHITE, 3, -Math.toRadians(90), alphaR, x+82, y+6, 2);
		
		GUItil.renderPolygon(Color.WHITE, 3, Math.toRadians(90), alphaL, x+69, y+6, 2);
		
		GlStateManager.enableTexture2D();
		GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, this.purchaseQuantity + "", x+76, y+3.5, 0xffffff, 0.8f);
		GlStateManager.popMatrix();
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GlStateManager.enableTexture2D();
		
		
	//	GlStateManager.translate(0.0F, 0.0F, 32.0F);
      //  zLevel = 200.0F;
       
    //    net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
      //  if (font == null) font = ClientProxy.newFontRenderer;
     //   ((Object) itemRender).renderItemAndEffectIntoGUI(stack, x, y);
      //  itemRender.renderItemOverlayIntoGUI(font, stack, x, y, "21");
		
		
		String display = a.itemStack.getDisplayName();
		float stringScale = 0.6f;
		if(ClientProxy.newFontRenderer.getStringWidth(display) > 72) {
			stringScale /= ClientProxy.newFontRenderer.getStringWidth(display)/72;
		}
		
      //  System.out.println(a.itemStack.getDisplayName() + " | " + ();
		GUItil.drawScaledString(ClientProxy.newFontRenderer, display, x+14, y+2, 0xffffff, stringScale);
		//GUItil.drawScaledString(ClientProxy.newFontRenderer, a.getAccountType().toString(), x+1.3, y+5, 0xe84118, 0.4f);
		GUItil.drawScaledString(ClientProxy.newFontRenderer, mString, x+width-((ClientProxy.newFontRenderer.getStringWidth(mString)*scale)), y+13, 0x4cd137, (float) scale);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(Slider.HIGH_RES_BUTTON);
		Color oran = Color.ORANGE;
		GlStateManager.color(oran.getRed()/255.0f, oran.getGreen()/255.0f, oran.getBlue()/255.0f, (float) alphaB);
		
		//GUItil.drawTexturedModalIcon(x+95, y+6, 135, 0, 17.9, 17.5, 0.2);
		
		
		GUItil.drawTexturedModalIcon(x+115, y+6, 0, 0, 31.5, 17.6, 0.25);
		GlStateManager.enableTexture2D();
		GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, "BUY", x+115, y+4, 0xffffff, 0.6f);
		//GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		
		super.renderScroll(x, y, width, yOffset);
	}
	
	@Override
	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
	
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(right) {
			this.purchaseQuantity++;
		} 
		if(left) {
			this.purchaseQuantity--;
		}
		
		if(purchaseQuantity < 1) {
			purchaseQuantity = 1;
		}
		
		if(purchaseQuantity > 99) {
			purchaseQuantity = 99;
		}
		
		
	}

	@Override
	public int compareTo(TradeScrollBlock o) {
		if(a.basePrice > o.a.basePrice) {
			return 1;
		} else if(a.basePrice == o.a.basePrice) {
			return 0;
		} else {
			return -1;
		}
	
	}
	

}
