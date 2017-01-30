package com.vicmatskiv.weaponlib.compatibility;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Pay special attention to the fact this class is stateful.
 * 
 * @author victor
 *
 */
public class CompatibleTessellator {
	
	private static Tessellator tessellator = Tessellator.getInstance();
	
	private static CompatibleTessellator compatibleTesselator = new CompatibleTessellator();

	public static CompatibleTessellator getInstance() {
		return compatibleTesselator;
	}
	
	private boolean hasColor; //Pay special attention to the fact this class is stateful.
	private boolean hasLightMap; //Pay special attention to the fact this class is stateful.
	
	private float red;
	private float green;
	private float blue;
	private float alpha;
	private int i;
	private int j;

	public void startDrawingQuads() {
		VertexBuffer vertextBuffer = tessellator.getBuffer();
		vertextBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

	}

	public void addVertexWithUV(double d, double e, double zLevel, double u, double v) {
		VertexBuffer vertextBuffer = tessellator.getBuffer();
		vertextBuffer.pos(d, e, zLevel);
		vertextBuffer.tex(u, v);
		if(hasLightMap) {
			vertextBuffer.lightmap(i, j);
		}
		if(hasColor) {
			vertextBuffer.color(red, green, blue, alpha);
		}
		vertextBuffer.endVertex();
	}

	public void draw() {
		tessellator.draw();
	}
	
	public void setLightMap(int i, int j) {
		hasLightMap = true;
		this.i = i;
		this.j = j;
	}

	public void setColorRgba(float red, float green, float blue, float alpha) {
		hasColor = true;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

}
