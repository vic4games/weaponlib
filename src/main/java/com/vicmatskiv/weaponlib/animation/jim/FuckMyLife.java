package com.vicmatskiv.weaponlib.animation.jim;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.Transform;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData.BlockbenchTransition;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
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
		
		
		
		
		
		Transform t = ClientModContext.getContext().getMainHeldWeapon().getWeapon().getRenderer().getWeaponRendererBuilder().firstPersonLeftHandTransform;
		t.withRotationPoint(1.06, 0.12, -0.35);
		//System.out.println(ClientModContext.getContext().getMainHeldWeapon());

		double mul = 1 / 13.0;
		// if(invert) mul = 0.0175;
		//GL11.glTranslated(t.getPositionX(), -t.getPositionY() * mul, t.getPositionZ() * mul);
		
		
		// GL11.glTranslated(-(-17.6f*mul), -(18.55f*mul), (-43.3f*mul));
		GL11.glTranslated(iTrans.x * mul, -iTrans.y * mul, iTrans.z * mul);
		// Animation translation
		
		
		
		// Offset rotation point
		GlStateManager.translate(t.getRotationPointX(), t.getRotationPointY(), t.getRotationPointZ());

		/*
		DebugRenderer.setupBasicRender();
	
		AnimationModeProcessor.getInstance().renderCross();
		DebugRenderer.destructBasicRender();
		*/
		
		// Original object rotation (+Z, -Y, -X)
		
		GL11.glRotated(t.getRotationZ(), 0, 0, 1);
		GL11.glRotated(iRot.z, 0, 0, 1);
		GL11.glRotated(t.getRotationY(), 0, 1, 0);
		GL11.glRotated(iRot.y, 0, 1, 0);
		GL11.glRotated(t.getRotationX(), 1, 0, 0);
		GL11.glRotated(iRot.x, 1, 0, 0);
		
		
		/*
		
		float mct = 30f*((Minecraft.getMinecraft().player.ticksExisted%20)/20f);
		GlStateManager.rotate(mct, 0, 0, 1);
		*/
		// Animation rotation
		
		
		
		
		
		
		

		// Revert rotation point
		GlStateManager.translate(-t.getRotationPointX(), -t.getRotationPointY(), -t.getRotationPointZ());

		
		
		
		// Original object scale
		//	GlStateManager.scale(t.getScaleX(), t.getScaleY(), t.getScaleZ());

			
		

	}
	
	public void position(float time, float max, boolean invert, ModelRenderer ml) {
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
		
		
		
		
		
		Transform t = ClientModContext.getContext().getMainHeldWeapon().getWeapon().getRenderer().getWeaponRendererBuilder().firstPersonLeftHandTransform;
		t.withRotationPoint(1.06, 0.12, -0.35);
		//System.out.println(ClientModContext.getContext().getMainHeldWeapon());

		double mul = 1 / 13.0;
		// if(invert) mul = 0.0175;
		//GL11.glTranslated(t.getPositionX(), -t.getPositionY() * mul, t.getPositionZ() * mul);
		
		
		ml.offsetX = (float) (iTrans.x * mul);
		ml.offsetY = (float) (-iTrans.y * mul);
		ml.offsetZ = (float) (iTrans.z * mul);
		// GL11.glTranslated(-(-17.6f*mul), -(18.55f*mul), (-43.3f*mul));
		//GL11.glTranslated(iTrans.x * mul, -iTrans.y * mul, iTrans.z * mul);
		// Animation translation
		
		
		
		// Offset rotation point
		GlStateManager.translate(t.getRotationPointX(), t.getRotationPointY(), t.getRotationPointZ());

		/*
		DebugRenderer.setupBasicRender();
	
		AnimationModeProcessor.getInstance().renderCross();
		DebugRenderer.destructBasicRender();
		*/
		
		// Original object rotation (+Z, -Y, -X)
		
		/*
		GL11.glRotated(t.getRotationZ(), 0, 0, 1);
		GL11.glRotated(t.getRotationY(), 0, 1, 0);
		GL11.glRotated(t.getRotationX(), 1, 0, 0);
		*/
		
		
		ml.rotateAngleZ = (float) (Math.toRadians(t.getRotationZ()) + Math.toRadians(iRot.z));
		ml.rotateAngleY = (float) (Math.toRadians(t.getRotationY()) + Math.toRadians(iRot.y));
		ml.rotateAngleX = (float) (Math.toRadians(t.getRotationX()) + Math.toRadians(iRot.x));
		
		
		/*
		
		float mct = 30f*((Minecraft.getMinecraft().player.ticksExisted%20)/20f);
		GlStateManager.rotate(mct, 0, 0, 1);
		*/
		// Animation rotation
		/*
		GL11.glRotated(iRot.z, 0, 0, 1);
		GL11.glRotated(iRot.y, 0, 1, 0);
		GL11.glRotated(iRot.x, 1, 0, 0);
		*/
		
		

		// Revert rotation point
		GlStateManager.translate(-t.getRotationPointX(), -t.getRotationPointY(), -t.getRotationPointZ());

		
		
		
		// Original object scale
		//	GlStateManager.scale(t.getScaleX(), t.getScaleY(), t.getScaleZ());

			
		

	}

}
