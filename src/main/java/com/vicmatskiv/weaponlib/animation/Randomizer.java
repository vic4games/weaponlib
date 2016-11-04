package com.vicmatskiv.weaponlib.animation;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class Randomizer {
	
	private Random random = new Random();
	
	private Matrix4f beforeMatrix;
	private Matrix4f afterMatrix;
	private Matrix4f currentMatrix;
	private long startTime;
	private long interval = 3000;
	private float amplitude = 0.04f;
	
	public Randomizer() {
		this.currentMatrix = getMatrixForPositioning(() -> {});
		reset();
	}
	
	public void setInterval(long interval) {
		
		if(interval != this.interval) {
			if(interval == 0) {
				// Stop
				afterMatrix = beforeMatrix = currentMatrix = getMatrixForPositioning(() -> {});
			} else {
				reset();
			}
		}
		this.interval = interval;
	}
	
	public void setAmplitude(float amplitude) {
		if(amplitude != this.amplitude) {
			if(amplitude == 0) {
				// Stop
				afterMatrix = beforeMatrix = currentMatrix = getMatrixForPositioning(() -> {});
			} else {
				reset();
			}
		}
		this.amplitude = amplitude;
	}
	
	public void reset() {
		beforeMatrix = currentMatrix;
		afterMatrix = createRandom();
		startTime = System.currentTimeMillis();
	}

	private Matrix4f createRandom() {
		Runnable c = () -> {

			float xRandomOffset = amplitude * (random.nextFloat() - 0.5f) * 2;
			float yRandomOffset = amplitude * (random.nextFloat() - 0.5f) * 2;
			float zRandomOffset = amplitude * (random.nextFloat() - 0.5f) * 2;
			GL11.glTranslatef(xRandomOffset, yRandomOffset, zRandomOffset);
		};
		return getMatrixForPositioning(c);
	}

	private Matrix4f getMatrixForPositioning(Runnable position) {
		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		position.run();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
		buf.rewind();
		Matrix4f matrix = new Matrix4f();
		matrix.load(buf);  
		GL11.glPopMatrix();
		return matrix;
	}
	
	public void update() {
		
		if(interval == 0 || amplitude == 0) {
			return;
		}
		
		long currentTime = System.currentTimeMillis();
		
		float progress = (float)(currentTime - startTime) / interval;
		if(progress >= 1) {
			reset();
			progress = 0f;
		}
		
		/*
		 * 
		 * progress = (endTime - startTime) / duration
		 * 
		 * current = start + (end - start) * progress = start * (1 - progress)  + end * progress;
		 */
		
		//float progress = (float)(System.currentTimeMillis() - startTime) / (float)duration;
		Matrix4f currentTransformMatrix;
		{
			// Load current matrix
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
			buf.rewind();
			currentTransformMatrix = new Matrix4f();
			currentTransformMatrix.load(buf); 
		}
		
		Matrix4f m1 = scale(beforeMatrix, 1 - progress); //start * (1 - progress)
		Matrix4f m2 = scale(afterMatrix, progress);
		
		currentMatrix = Matrix4f.add(m1, m2, null);
		
		Matrix4f composite = Matrix4f.mul(currentTransformMatrix, currentMatrix, null);
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		composite.store(buf);
		
		buf.rewind();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadMatrix(buf);
	}
	
	private Matrix4f scale(Matrix4f m, float factor) {
		Matrix4f result = new Matrix4f();
		
		result.m00 = m.m00 * factor;
		result.m01 = m.m01 * factor;
		result.m02 = m.m02 * factor;
		result.m03 = m.m03 * factor;
		
		result.m10 = m.m10 * factor;
		result.m11 = m.m11 * factor;
		result.m12 = m.m12 * factor;
		result.m13 = m.m13 * factor;

		result.m20 = m.m20 * factor;
		result.m21 = m.m21 * factor;
		result.m22 = m.m22 * factor;
		result.m23 = m.m23 * factor;
		
		result.m30 = m.m30 * factor;
		result.m31 = m.m31 * factor;
		result.m32 = m.m32 * factor;
		result.m33 = m.m33 * factor;
		
		return result;
	}
}
