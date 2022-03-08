package com.vicmatskiv.weaponlib.render.cam;

import org.lwjgl.util.vector.Matrix4f;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.WeaponState;
import com.vicmatskiv.weaponlib.numerical.LissajousCurve;

import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.client.renderer.GlStateManager;

public class NaturalCamera {
	
	private double x, y, z, xr, yr, zr;
	
	private Matrix4f currentMatrix;
	private Matrix4f previousMatrix;
	
	
	public void feedMatrix(Matrix4f food) {
		this.previousMatrix = this.currentMatrix;
		this.currentMatrix = food;
	//	System.out.println("Received " + food);
	}
	
	public NaturalCamera() {
		
	}
	
	public void update() {
		
		
		//System.out.println(currentMatrix);
		
		
		//System.out.println(currentMatrix.m03);
		
		//GlStateManager.translate(currentMatrix.m30, currentMatrix.m31, currentMatrix.m32);
		
		//xr = 0.01;
		if(ClientModContext.getContext().getMainHeldWeapon().getState() == WeaponState.READY) {
			yr *= 0.98;
		} else {
			yr += 0.01;
			yr = Math.min(yr, 1.0);
		}
		xr += 0.01;
		
		//System.out.println("hi-> " + currentMatrix);
		
		x = LissajousCurve.getXOffsetOnCurve(yr*0.5, 0.5, 0, 0, xr*2);
		y = LissajousCurve.getYOffsetOnCurve(yr*0.5, 0.5, Math.PI/2, 0, xr*2);
		GlStateManager.rotate((float) x, 1, 0, 0);
		GlStateManager.rotate((float) y, 0, 0, 1);
		//GlStateManager.rotate(45f, 1, 0, 0);
		//GlStateManager.rotate((float) x, 1, 0, 0);
		//GlStateManager.translate(x, y, z);
	}

}
