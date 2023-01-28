package com.jimholden.conomy.client.gui.engine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;

public class IconSheet {
	
	public static class Icon {
		
		public ResourceLocation rl;
		public double x;
		public double y;
		public double size;
		
		public int id;
		
		Icon(ResourceLocation rl, double x, double y, double size, int id) {
			this.rl = rl;
			this.x = x;
			this.y = y;
			this.size = size;
			this.id = id;
		}
		
		public void render(double posX, double posY, double scale) {
			Minecraft.getMinecraft().renderEngine.bindTexture(rl);
			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			
		
		
		//	GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
			
			GUItil.drawTexturedModalIcon(posX, posY, x, y, size, size, scale);
			//GlStateManager.translate(-posX, -posY, 0);
			
			GlStateManager.disableAlpha();
			GlStateManager.popMatrix();
		}
		
	}
	
	public static Icon getIcon(ResourceLocation loc, int sheetSize, int icoSize, int num) {
		
		
		
		int row = (int) ((num-1)/(double) (sheetSize/icoSize))*icoSize;
		int col =(int) (((num-1)%(double) (sheetSize/icoSize)))*icoSize;
		

	
		
		return new Icon(loc, col, row, icoSize, num);
		
		//Icon ico = new Icon(loc, , y, size)
		
	}

}
