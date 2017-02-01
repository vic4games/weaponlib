package com.vicmatskiv.weaponlib.compatibility;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

public class CompatibleTessellator {
	
	private static Tessellator tessellator = Tessellator.instance;
	
	private static CompatibleTessellator compatibleTesselator = new CompatibleTessellator();

	public static CompatibleTessellator getInstance() {
		return compatibleTesselator;
	}

	public void startDrawingQuads() {
		tessellator.startDrawingQuads();
	}
	

	public void startDrawingParticles() {
		tessellator.startDrawing(GL11.GL_QUADS);
	}
	
	public void startDrawingLines() {
		tessellator.startDrawing(GL11.GL_LINES);
	}

	public void addVertexWithUV(double d, double e, double zLevel, float i, float j) {
		tessellator.addVertexWithUV(d, e, zLevel, i, j);
	}

	public void draw() {
		tessellator.draw();
	}

	public void setLightMap(int j, int k) {
		tessellator.setBrightness(200); // TODO: this is a hack; need to research how to translate j and k to brightness
	}

	public void setColorRgba(float red, float green, float blue, float alpha) {
		tessellator.setColorRGBA_F(red, green, blue, alpha);
	}

	public void addVertex(float x, float y, float z) {
		tessellator.addVertex(x, y, z);
	}

	public void endVertex() {
		// TODO Auto-generated method stub
		
	}

}
