package com.vicmatskiv.weaponlib.animation.jim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.Transform;
import com.vicmatskiv.weaponlib.animation.Transition;
import com.vicmatskiv.weaponlib.animation.DebugPositioner.Position;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class AnimationData {

	public TreeMap<Float, BlockbenchTransition> bbTransition = new TreeMap<>();

	public static final float PACE = 750f;
	
	public ArrayList<Float> timestamps = new ArrayList<>();

	public TreeMap<Float, Vec3d> rotationKeyframes = new TreeMap<>();
	public TreeMap<Float, Vec3d> translateKeyframes = new TreeMap<>();

	private boolean isNull = false;
	private int fakeTransitions = 0;
	private long fTLength;
	
	protected AnimationData(ArrayList<Float> arrayList) {
		this.isNull = true;
		this.fakeTransitions = arrayList.size();
		this.fTLength = (long) (arrayList.get(arrayList.size()-1)/arrayList.size());
	}
	
	public AnimationData(JsonObject obj) {

		// initialize a timestamp array and the key arrays
		ArrayList<Float> timestamps = new ArrayList<>();
		/*
		 * rotationKeyframes = new TreeMap<>(); translateKeyframes = new TreeMap<>();
		 */

		// load up the rotation keyframes
		if(obj.has("rotation")) {
			JsonObject rotation = obj.get("rotation").getAsJsonObject();
			for (Entry<String, JsonElement> i : rotation.entrySet()) {
				JsonArray ar = i.getValue().getAsJsonArray();
				float time = Float.parseFloat(i.getKey());
				Vec3d rotationVector = new Vec3d(ar.get(0).getAsDouble(), ar.get(1).getAsDouble(), ar.get(2).getAsDouble());
				if (!timestamps.contains(time))
					timestamps.add(time);
				rotationKeyframes.put(time, rotationVector);
			}
		}
		

		// load up the translation keyframes
		if(obj.has("position") && obj.get("position").isJsonObject()) {
			JsonObject translate = obj.get("position").getAsJsonObject();
			for (Entry<String, JsonElement> i : translate.entrySet()) {
				JsonArray ar = i.getValue().getAsJsonArray();
				float time = Float.parseFloat(i.getKey());
				Vec3d translationVector = new Vec3d(ar.get(0).getAsDouble(), ar.get(1).getAsDouble(),
						ar.get(2).getAsDouble());
				if (!timestamps.contains(time))
					timestamps.add(time);
				translateKeyframes.put(time, translationVector);
			}
		} else if(!obj.get("position").isJsonObject()) {
			
			JsonArray ar = obj.get("position").getAsJsonArray();
			Vec3d translationVector = new Vec3d(ar.get(0).getAsDouble(), ar.get(1).getAsDouble(),
					ar.get(2).getAsDouble());
			translateKeyframes.put(0f, translationVector);
		}
		

		// sort the timestamp array
		Collections.sort(timestamps);
		this.timestamps = timestamps;

		// System.out.println(timestamps);

		// bake
		for (int i = 0; i < timestamps.size(); ++i) {
			float f = timestamps.get(i);
			Vec3d rotationKey = null;
			Vec3d translationKey = null;

			
			float timeDelta = 0;
			if (i != 0) {

				timeDelta = (f - timestamps.get(i - 1)) * PACE;
				// System.out.println("Delta for " + f + ": " +timeDelta);
			} else {
				timeDelta = (timestamps.get(i + 1) - f) * PACE;
			}
			
			
			// timeDelta = 210f;
			try {
				if (!rotationKeyframes.containsKey(f)) {

					/*
					 * if(rotationKeyframes.ceilingKey(f) == null) { // The other keyframes extend
					 * farther rotationKey = rotationKeyframes.get(rotationKeyframes.floorKey(f)); }
					 * else if(rotationKeyframes.floorKey(f) == null) { // There is no keyframe
					 * before this rotationKey =
					 * rotationKeyframes.get(rotationKeyframes.ceilingKey(f)); } else { // Otherwise
					 * just interpolate a new one float fromDelta = rotationKeyframes.floorKey(f);
					 * float toDeltaDelta = rotationKeyframes.ceilingKey(f); float alpha = (f -
					 * fromDelta)/(toDeltaDelta-fromDelta);
					 * 
					 * Vec3d beforeKey = rotationKeyframes.floorEntry(f).getValue(); Vec3d afterKey
					 * = rotationKeyframes.ceilingEntry(f).getValue(); if(afterKey == null) afterKey
					 * = beforeKey; rotationKey = MatrixHelper.lerpVectors(beforeKey, afterKey,
					 * alpha); }
					 */
					rotationKey = buildKeyframe(rotationKeyframes, f);
					rotationKeyframes.put(f, rotationKey);
				} else {
					rotationKey = rotationKeyframes.get(f);
				}

				if (!translateKeyframes.containsKey(f)) {

					/*
					 * if(translateKeyframes.ceilingKey(f) == null) { // The other keyframes extend
					 * farther translationKey =
					 * translateKeyframes.get(translateKeyframes.floorKey(f)); } else
					 * if(translateKeyframes.floorKey(f) == null) { // There is no keyframe before
					 * this translationKey =
					 * translateKeyframes.get(translateKeyframes.ceilingKey(f)); } else { float
					 * fromDelta = translateKeyframes.floorKey(f); float toDeltaDelta =
					 * translateKeyframes.ceilingKey(f); float alpha = (f -
					 * fromDelta)/(toDeltaDelta-fromDelta);
					 * 
					 * 
					 * Vec3d beforeKey = translateKeyframes.floorEntry(f).getValue(); Vec3d afterKey
					 * = translateKeyframes.ceilingEntry(f).getValue(); if(afterKey == null)
					 * afterKey = beforeKey; translationKey = MatrixHelper.lerpVectors(beforeKey,
					 * afterKey, alpha); }
					 */

					
					translationKey = buildKeyframe(translateKeyframes, f);
					translateKeyframes.put(f, translationKey);
				} else {

					translationKey = translateKeyframes.get(f);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// System.out.println(f + " | " + new BlockbenchTransition(timeDelta,
			// rotationKey, translationKey));
			this.bbTransition.put(f, new BlockbenchTransition(timeDelta, rotationKey, translationKey));

		}

	}
	
	public float getDelta(TreeMap<Float, Vec3d> map, float f) {
		if(map.floorKey(f) != null) {
			return (f-map.floorKey(f))*PACE;
		} else if(map.ceilingKey(f) != null) {
			return (f-map.ceilingKey(f))*PACE;
		} else {
			return 100f;
		}
		
	}

	public Vec3d buildKeyframe(TreeMap<Float, Vec3d> keyList, float timestamp) {
		// Keylist is empty, just return a zero vector
		
		if (keyList.isEmpty())
			return Vec3d.ZERO;
		if (keyList.ceilingKey(timestamp) == null) {
			// There is no keyframe ahead, so there's nothing to interpolate
			// between. Just grab the last keyframe and use that.
			
			return keyList.get(keyList.floorKey(timestamp));
		} else if (keyList.floorKey(timestamp) == null) {
			// There is no keyframe before this. Just grab the next one and use
			// that.
			
			
			return keyList.get(keyList.ceilingKey(timestamp));
		} else {
			// Otherwise just interpolate a new one
			
			float fromDelta = keyList.floorKey(timestamp);
			float toDeltaDelta = keyList.ceilingKey(timestamp);
			float alpha = (timestamp - fromDelta) / (toDeltaDelta - fromDelta);

			Vec3d beforeKey = keyList.floorEntry(timestamp).getValue();
			Vec3d afterKey = keyList.ceilingEntry(timestamp).getValue();
			if (afterKey == null)
				afterKey = beforeKey;
			return MatrixHelper.lerpVectors(beforeKey, afterKey, alpha);
		}
	}

	public void bakeKeyframes(float timeStamp) {
	
		// Build keyframes
		Vec3d rotation = buildKeyframe(rotationKeyframes, timeStamp);
		Vec3d translation = buildKeyframe(translateKeyframes, timeStamp);
		
		
		float timeDelta = getDelta(rotationKeyframes, timeStamp);
		
		
		// Put keyframes in list (for debugging and testing)
		rotationKeyframes.put(timeStamp, rotation);
		translateKeyframes.put(timeStamp, translation);
		
	
		// Build a new BlockBench transition
		getBbTransition().put(timeStamp, new BlockbenchTransition(timeDelta, rotation, translation));
		

	}

	/*
	 * GL11.glScalef(3.5f, 3.5f, 4.5f);
	 * 
	 * GlStateManager.translate(0.2, 0.05, -0.1);
	 * 
	 * GL11.glRotated(57.7232f, 0, 0, 1); GL11.glRotated(26.1991f, 0, 1, 0);
	 * GL11.glRotated(0f, 1, 0, 0);
	 */

	@SuppressWarnings("unchecked")
	public List<Transition<RenderContext<RenderableState>>> getTransitionList() {
		List<Transition<RenderContext<RenderableState>>> transitionList = new ArrayList<>();
		for (Entry<Float, BlockbenchTransition> bb : this.bbTransition.entrySet()) {
			transitionList.add((Transition<RenderContext<RenderableState>>) bb.getValue().createVMWTransition());
		}
		return transitionList;

	}

	@SuppressWarnings("unchecked")
	public List<Transition<RenderContext<RenderableState>>> getTransitionList(Transform initial, double divisor) {
		List<Transition<RenderContext<RenderableState>>> transitionList = new ArrayList<>();
		
		

		
		//System.out.println("---Begin--");
		if(!isNull) {
			for (Entry<Float, BlockbenchTransition> bb : this.bbTransition.entrySet()) {
				//System.out.print("\n" + bb.getKey() + " [" + ((int) bb.getValue().getTimestamp()) + "] " + bb.getValue().getTranslation() + bb.getValue().getRotation());
				
				transitionList.add((Transition<RenderContext<RenderableState>>) bb.getValue().createVMWTransition(initial, divisor));
			}
		} else {
			for(int i = 0; i < this.fakeTransitions; ++i) {
				transitionList.add(new Transition<>((renderContext) -> {}, this.fTLength));
			}
		}
		
		
		
		/*
		System.out.println("---End--");
		System.out.println("Exporting transition list... " + transitionList.size());
		*/
		return transitionList;

	}

	public static class BlockbenchTransition {

		private float timestamp;
		private Vec3d rotation;
		private Vec3d translation;

		public BlockbenchTransition(float timestamp, Vec3d rotation, Vec3d translation) {
			this.timestamp = timestamp;
			this.rotation = rotation;
			this.translation = translation;
		}

		public void directTransform() {
			GL11.glTranslated(translation.x / 25f, translation.y / 25f, translation.z / 25f);

			// Z, Y, X

			GL11.glRotated(rotation.x, 1, 0, 0);

			GL11.glRotated(rotation.y, 0, 1, 0);
			GL11.glRotated(rotation.z, 0, 0, 1);

			GL11.glScaled(1, 1, 1);
		}

		public Transition<?> createVMWTransition() {
			return new Transition<>((rc) -> {

				double mul = 0.01;
				GL11.glTranslated(-translation.x * mul, -translation.y * mul, translation.z * mul);

				// +Z, -Y, -X

				GL11.glRotated(rotation.z, 0, 0, 1);
				GL11.glRotated(rotation.y, 0, 1, 0);
				GL11.glRotated(rotation.x, 1, 0, 0);
			}, (int) timestamp);

		}

		public Transition<?> createVMWTransition(Transform t, double divisor) {
			return new Transition<>((rc) -> {

				// Transform Multiplier (12x as small)
				double mul = 1 / divisor;
				
				
				
				//if(divisor == 5) mul = 0.0000000;
				
				// Original object positioning
				GlStateManager.translate(t.getPositionX(), t.getPositionY(), t.getPositionZ());

				// Animation translation
				GL11.glTranslated(translation.x * mul, -translation.y * mul, translation.z * mul);

				// Offset rotation point
				GlStateManager.translate(t.getRotationPointX(), t.getRotationPointY(), t.getRotationPointZ());

				// Original object rotation (+Z, -Y, -X)
				GL11.glRotated(t.getRotationZ(), 0, 0, 1);
				GL11.glRotated(t.getRotationY(), 0, 1, 0);
				GL11.glRotated(t.getRotationX(), 1, 0, 0);

				// Animation rotation
				
				GL11.glRotated(rotation.z, 0, 0, 1);
				GL11.glRotated(rotation.y, 0, 1, 0);
				GL11.glRotated(rotation.x, 1, 0, 0);

				// Revert rotation point
				GlStateManager.translate(-t.getRotationPointX(), -t.getRotationPointY(), -t.getRotationPointZ());

				// Original object scale
					GlStateManager.scale(t.getScaleX(), t.getScaleY(), t.getScaleZ());

				
			}, (int) timestamp);

		}

		public void showDebugCode() {
			System.out
					.println("GL11.glTranslated(" + translation.x + ", " + translation.y + ", " + translation.z + ");");
			System.out.println("GL11.glRotated(" + rotation.z + ", 0, 0, 1);");
			System.out.println("GL11.glRotated(" + rotation.y + ", 0, 1, 0);");
			System.out.println("GL11.glRotated(" + rotation.x + ", 1, 0, 0);");
			System.out.println("GL11.glScaled(1, 1, 1);");
		}

		@Override
		public String toString() {
			return "[(" + this.timestamp + ") " + this.rotation + " > " + this.translation + "]";
		}

		public float getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(float timestamp) {
			this.timestamp = timestamp;
		}

		public Vec3d getRotation() {
			return rotation;
		}

		public void setRotation(Vec3d rotation) {
			this.rotation = rotation;
		}

		public Vec3d getTranslation() {
			return translation;
		}

		public void setTranslation(Vec3d translation) {
			this.translation = translation;
		}

	}

	public TreeMap<Float, BlockbenchTransition> getBbTransition() {
		return bbTransition;
	}

	public void setBbTransition(TreeMap<Float, BlockbenchTransition> bbTransition) {
		this.bbTransition = bbTransition;
	}

	public ArrayList<Float> getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(ArrayList<Float> timestamps) {
		this.timestamps = timestamps;
	}

}