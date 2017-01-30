package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.renderer.Tessellator;

public class CompatibleTessellator {
	
	private static Tessellator tesselator = Tessellator.instance;
	
	private static CompatibleTessellator compatibleTesselator = new CompatibleTessellator();

	public static CompatibleTessellator getInstance() {
		return compatibleTesselator;
	}

	public void startDrawingQuads() {
		tesselator.startDrawingQuads();
	}

	public void addVertexWithUV(double d, double e, double zLevel, float i, float j) {
		tesselator.addVertexWithUV(d, e, zLevel, i, j);
	}

	public void draw() {
		tesselator.draw();
	}

	public void setLightMap(int j, int k) {
		tesselator.setBrightness(200); // TODO: this is a hack; need to research how to translate j and k to brightness
	}

	public void setColorRgba(float red, float green, float blue, float alpha) {
		tesselator.setColorRGBA_F(red, green, blue, alpha);
	}
}
