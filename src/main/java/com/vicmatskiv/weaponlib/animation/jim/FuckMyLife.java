package com.vicmatskiv.weaponlib.animation.jim;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData.BlockbenchTransition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class FuckMyLife {

	public TreeMap<Float, BlockbenchTransition> bbMap = new TreeMap<>();
	public float timer = 0.0f;
	public float max = 2.0f;

	public static FuckMyLife instance = new FuckMyLife();

	public void position(float time, float max, boolean invert) {
		//if(1+1==2) return;
		time = FuckMyLife.instance.timer;
		if (FuckMyLife.instance.timer >= max) {

			time = 0;
		}

		if (FuckMyLife.instance.timer > (max + 3f)) {
			FuckMyLife.instance.timer = 0f;
		}

		float hitler = bbMap.floorEntry(time).getKey();
		float adolflol = 0.0f;
		try {
			adolflol = bbMap.ceilingEntry(time).getKey();
		} catch (Exception e) {
			adolflol = bbMap.floorKey(time);
		}

		// System.out.println(hitler + " | " + time + " | " + adolflol + " | " +
		// ((time-hitler)/(adolflol-hitler)));
		// System.out.println();
		// System.out.println((time-hitler)/(adolflol-hitler));

		Vec3d prevTrans = bbMap.floorEntry(time).getValue().getTranslation();
		Vec3d nextTrans = null;
		try {
			nextTrans = bbMap.ceilingEntry(time).getValue().getTranslation();

		} catch (Exception e) {

		}

		if (nextTrans == null)
			nextTrans = prevTrans;

		Vec3d prevRot = bbMap.floorEntry(time).getValue().getRotation();
		Vec3d nextRot = null;
		try {
			nextRot = bbMap.ceilingEntry(time).getValue().getRotation();
		} catch (Exception e) {

		}

		if (nextRot == null)
			nextRot = prevRot;

		// System.out.println(prevRot + " | " + nextRot);

		// System.out.println(prevTrans + " | " + nextTrans);

		float leDelta = (time - hitler) / (adolflol - hitler);
		if (Double.isNaN(leDelta))
			leDelta = 0.0f;
		Vec3d iTrans = MatrixHelper.lerpVectors(prevTrans, nextTrans, leDelta);
		Vec3d iRot = MatrixHelper.lerpVectors(prevRot, nextRot, leDelta);

		/*
		 * float slap = invert ? 10f : 20f; if(invert) {
		 * GL11.glTranslated(iTrans.x/slap, -iTrans.y/slap, iTrans.z/slap);
		 * 
		 * } else { GL11.glTranslated(-iTrans.y/slap, -iTrans.z/slap, -iTrans.x/slap);
		 * 
		 * }
		 */
		// Z, Y, X

		double mul = 1 / 10.0;
		// if(invert) mul = 0.0175;

		// GL11.glTranslated(-(-17.6f*mul), -(18.55f*mul), (-43.3f*mul));
		GL11.glTranslated(iTrans.x * mul, -iTrans.y * mul, iTrans.z * mul);

		// +Z, -Y, -X
		/*
		 * GL11.glRotated(57.7232f, 0, 0, 1); GL11.glRotated(26.1991f, 0, 1, 0);
		 * GL11.glRotated(0f, 1, 0, 0);
		 */
		if (invert) {
			GlStateManager.translate(1.0, 0.1, -0.5);
		}

		/*
		 * if(invert) { iRot = new Vec3d(19.8143, -27.5635, 11.6252); }
		 */
		if (invert) {

			/*
			 * GL11.glRotated(0f, 0, 0, 1); GL11.glRotated(0f, 0, 1, 0); GL11.glRotated(0f,
			 * 1, 0, 0);
			 */

			GL11.glRotated(57.7232f, 0, 0, 1);
			GL11.glRotated(23.6991f, 0, 1, 0);
			GL11.glRotated(8.1997f, 1, 0, 0);

		} else {

		}

		
		if(invert) {
			GL11.glRotated(iRot.z, 0, 0, 1);
			GL11.glRotated(iRot.y, 0, 1, 0);
			GL11.glRotated(iRot.x, 1, 0, 0);

		} else {
			
			double ticks = (45)*(Minecraft.getMinecraft().player.ticksExisted%40)/40.0;
			
			double xOff = -0.1;
			double yOff = -1;
			double zOff = 0;
			
			GlStateManager.translate(xOff, yOff, zOff);
			GL11.glRotated(iRot.z, 0, 0, 1);
			GL11.glRotated(iRot.y, 0, 1, 0);
			GL11.glRotated(iRot.x, 1, 0, 0);

			GlStateManager.translate(-xOff, -yOff, -zOff);
			
			
		}
		
		if (invert) {
			// GlStateManager.translate(0.7, 0.4, -0.7);

			GlStateManager.scale(3.7, 3.7, 3.7);
		}

		/*
		 * GL11.glRotated(-28.5649f, 0, 0, 1); GL11.glRotated(-25.9687f, 0, 1, 0);
		 * GL11.glRotated(19.9531f, 1, 0, 0);
		 */

	}

}
