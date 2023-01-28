package com.jimholden.conomy.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

/**
 * ONLY FOR DEBUGGING PURPOSES, THIS IS NOT
 * AN EFFICIENT OR GOOD WAY OF RENDERING!
 * 
 * @author Jim Holden
 *
 */
public class ConomyDebugRender {
	
	public static final Vec3d RED = new Vec3d(1.0, 0.0, 0.0);
	public static final Vec3d WHITE = new Vec3d(1.0, 1.0, 1.0);
	public static final Vec3d GREEN = new Vec3d(0.0, 1.0, 0.0);
	public static final Vec3d BLUE = new Vec3d(0.0, 0.0, 1.0);
	public static final Vec3d BLACK = new Vec3d(0, 0, 0);
	public static final Vec3d PURPLE = new Vec3d(1, 0, 1);
	public static final Vec3d YELLOW = new Vec3d(1, 1, 0);
	public static final Vec3d CYAN = new Vec3d(0, 1, 0);
	
	public static void initLines() {
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
	}
	
	public static void renderLine(Vec3d a, Vec3d b) {
		renderLine(a, b, WHITE);
	}
	
	public static void renderLine(Vec3d a, Vec3d b, Vec3d color) {
	
		GlStateManager.color((float) color.x, (float) color.y, (float) color.z, 1.0f);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(a.x, a.y, a.z);
		GL11.glVertex3d(b.x, b.y, b.z);
		GL11.glEnd();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public static void deInitLines() {
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

}
