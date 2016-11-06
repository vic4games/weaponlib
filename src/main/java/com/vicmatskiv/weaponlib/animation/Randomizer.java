package com.vicmatskiv.weaponlib.animation;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

final class Randomizer {
	
	private Random random = new Random();
	
	private Matrix4f beforeMatrix;
	private Matrix4f afterMatrix;
	private Matrix4f currentMatrix;
	private long startTime;
	private float rate = 0.25f;
	private float amplitude = 0.04f;
	
	// Valid bias range: from 
	private float xbias = 0f;
	private float ybias = 0f;
	private float zbias = 0f;
	
	public Randomizer() {
		this.currentMatrix = getMatrixForPositioning(() -> {});
		next();
	}
	
	private boolean reconfigure(float rate, float amplitude) {
		if(rate == this.rate && amplitude == this.amplitude) {
			return false;
		}
		
		boolean reconfigured = false;
		if(rate != this.rate || amplitude != this.amplitude) {
			if(rate == 0f && amplitude == 0f) {
				// Stop
				afterMatrix = beforeMatrix = currentMatrix = getMatrixForPositioning(() -> {});
			} else {
				reconfigured = true;
			}
		}
		
		this.rate = rate;
		this.amplitude = amplitude;
		
		if(reconfigured) {
			next();
		}
		
		return reconfigured;
	}
	
	private void next() {
		beforeMatrix = currentMatrix;
		afterMatrix = createRandomMatrix();
		startTime = System.currentTimeMillis();
	}

	private Matrix4f createRandomMatrix() {
		Runnable c = () -> {
			float xRandomOffset = amplitude * ((random.nextFloat() - 0.5f) * 2 + xbias);
			float yRandomOffset = amplitude * ((random.nextFloat() - 0.5f) * 2 + ybias);
			float zRandomOffset = amplitude * ((random.nextFloat() - 0.5f) * 2 + zbias);
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
	
	public void update(float rate, float amplitude) {
		reconfigure(rate, amplitude);
		
		if(rate == 0f || amplitude == 0f) {
			return;
		}
		
		long currentTime = System.currentTimeMillis();
		
		// long interval = (long) (1000f / rate);
		// E.g: current time 10, start time 0, rate 25 ( 1 iteration per 40ms), progress = (10 - 0) * 25 / 1000 = 0.25
		float progress = (currentTime - startTime) * rate / 1000;
				
		if(progress >= 1) {
			next();
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
