package com.vicmatskiv.weaponlib.render.cam;

import net.minecraft.client.renderer.GlStateManager;

public class NaturalCamera {
	
	private double x, y, z, xr, yr, zr;
	
	public NaturalCamera() {
		
	}
	
	public void update() {
		x -= 0.01;
		GlStateManager.rotate(45f, 1, 0, 0);
		//GlStateManager.rotate((float) x, 1, 0, 0);
		//GlStateManager.translate(x, y, z);
	}

}
