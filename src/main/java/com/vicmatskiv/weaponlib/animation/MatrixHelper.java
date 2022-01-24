package com.vicmatskiv.weaponlib.animation;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

import akka.japi.Pair;
import net.minecraft.util.math.Vec3d;

public class MatrixHelper {
	
	
	/*
	 * BEEZER lmao
	 */
	
	public static void scaleFloatBuffer(FloatBuffer matrix, Vec3d scale) {
		matrix.put(0, (float) (matrix.get(0)*scale.x));
		matrix.put(4, (float) (matrix.get(4)*scale.x));
		matrix.put(8, (float) (matrix.get(8)*scale.x));
		matrix.put(12, (float) (matrix.get(12)*scale.x));
		
		matrix.put(1, (float) (matrix.get(1)*scale.y));
		matrix.put(5, (float) (matrix.get(5)*scale.y));
		matrix.put(9, (float) (matrix.get(9)*scale.y));
		matrix.put(13, (float) (matrix.get(13)*scale.y));
		
		matrix.put(2, (float) (matrix.get(2)*scale.z));
		matrix.put(6, (float) (matrix.get(6)*scale.z));
		matrix.put(10, (float) (matrix.get(10)*scale.z));
		matrix.put(14, (float) (matrix.get(14)*scale.z));
	}
	
	public static Vec3d extractScale(Matrix4f mat) {
		
	
		/*
		Vec3d a = new Vec3d(mat.m00, mat.m01, mat.m02);
		Vec3d b = new Vec3d(mat.m10, mat.m11, mat.m12);
		Vec3d c = new Vec3d(mat.m20, mat.m21, mat.m22);
		*/
		
		Vec3d a = new Vec3d(mat.m00, mat.m10, mat.m20);
		Vec3d b = new Vec3d(mat.m01, mat.m11, mat.m21);
		Vec3d c = new Vec3d(mat.m02, mat.m12, mat.m22);
		
		Vec3d scale = new Vec3d(a.lengthVector(), b.lengthVector(), c.lengthVector());
		
		
		mat.m00 /= (float) scale.x;
		mat.m10 /= (float) scale.x;
		mat.m20 /= (float) scale.x;
		
		mat.m01/= (float) scale.y;
		mat.m11/= (float) scale.y;
		mat.m21 /= (float) scale.y;
		
		mat.m02/= (float) scale.z;
		mat.m12 /= (float) scale.z;
		mat.m22 /= (float) scale.z;
		
		return scale;
	}
	
	public static Vec3d restoreScale(Matrix4f mat, Vec3d scale) {
		
	
		mat.m00 *= (float) scale.x;
		mat.m10 *= (float) scale.x;
		mat.m20 *= (float) scale.x;
		
		mat.m01 *= (float) scale.y;
		mat.m11 *= (float) scale.y;
		mat.m21 *= (float) scale.y;
		
		mat.m02 *= (float) scale.z;
		mat.m12 *= (float) scale.z;
		mat.m22 *= (float) scale.z;
		
		return scale;
	}
	
	public static Vec3d lerpVectors(Vec3d a, Vec3d b, float t) {
		return new Vec3d(solveLerp((float) a.x, (float) b.x, t),
						 solveLerp((float) a.y, (float) b.y, t),
						 solveLerp((float) a.z, (float) b.z, t));
	}
	
	public static Matrix4f buildTranslation(float x, float y, float z) {
		return build(1f, 0f, 0f, x,
				0f, 1f, 0f, y,
				0f, 0f, 1f, z,
				0f, 0f, 0f, 1.0f);
	}
	
	public static Matrix4f build(float m00, float m10, float m20, float m30,
			float m01, float m11, float m21, float m31, 
			float m02, float m12, float m22, float m32, 
			float m03, float m13, float m23, float m33) {
		
		Matrix4f m = new Matrix4f();
		
		m.m00 = m00;
		m.m01 = m01;
		m.m02 = m02;
		m.m03 = m03;
		
		m.m10 = m10;
		m.m11 = m11;
		m.m12 = m12;
		m.m13 = m13;
		
		m.m20 = m20;
		m.m21 = m21;
		m.m22 = m22;
		m.m23 = m23;
		
		m.m30 = m30;
		m.m31 = m31;
		m.m32 = m32;
		m.m33 = m33;
		
		return m;
		
		
	}
	
	/**
	 * Thank you Drillgon for donating this method :)
	 * @param v0
	 * @param v1
	 * @param t
	 * @return
	 */
	public static Quaternion slerp(Quaternion v0, Quaternion v1, float t) {
			// Only unit quaternions are valid rotations.
		    // Normalize to avoid undefined behavior.
			//Drillgon200: Any quaternions loaded from blender should be normalized already
		    //v0.normalise();
		    //v1.normalise();

		    // Compute the cosine of the angle between the two vectors.
		    double dot = Quaternion.dot(v0, v1);

		    // If the dot product is negative, slerp won't take
		    // the shorter path. Note that v1 and -v1 are equivalent when
		    // the negation is applied to all four components. Fix by 
		    // reversing one quaternion.
		    if (dot < 0.0f) {
		        v1 = new Quaternion(-v1.x, -v1.y, -v1.z, -v1.w);
		        dot = -dot;
		    }

		    final double DOT_THRESHOLD = 0.9999999;
		    if (dot > DOT_THRESHOLD) {
		        // If the inputs are too close for comfort, linearly interpolate
		        // and normalize the result.
		        Quaternion result = new Quaternion(v0.x + t*v1.x, 
		        								v0.y + t*v1.y, 
		        								v0.z + t*v1.z, 
		        								v0.w + t*v1.w);
		        result.normalise();
		        return result;
		    }

		    // Since dot is in range [0, DOT_THRESHOLD], acos is safe
		    double theta_0 = Math.acos(dot);        // theta_0 = angle between input vectors
		    double theta = theta_0*t;          // theta = angle between v0 and result
		    double sin_theta = Math.sin(theta);     // compute this value only once
		    double sin_theta_0 = Math.sin(theta_0); // compute this value only once

		    float s0 = (float) (Math.cos(theta) - dot * sin_theta / sin_theta_0);  // == sin(theta_0 - theta) / sin(theta_0)
		    float s1 = (float) (sin_theta / sin_theta_0);

		    return new Quaternion(s0*v0.x + s1*v1.x, 
		    					s0*v0.y + s1*v1.y, 
		    					s0*v0.z + s1*v1.z, 
		    					s0*v0.w + s1*v1.w);
	}
	
	public static float solveBeizer(float a, float b, float c, float t) {
		return (float) (Math.pow(1 - t, 2) * a + 2 * t * (1 - t) * b + t*t * c);
	}
	
	public static float solveLerp(float a, float b, float t) {
		return a + (b-a)*t;
	}
	
	public static double solveLerp(double a, double b, double t) {
		return a + (b-a)*t;
	}
	
	public static Matrix4f beizerInterpolation(Matrix4f a, Matrix4f b, Matrix4f c, float t, boolean doRotation) {
		Matrix4f newMatrix = new Matrix4f();
		
		//System.out.println(b);
		
		if(doRotation) {
			newMatrix.m00 = solveBeizer(a.m00, b.m00, c.m00, t);
			newMatrix.m01 = solveBeizer(a.m01, b.m01, c.m01, t);
			newMatrix.m02 = solveBeizer(a.m02, b.m02, c.m02, t);
			newMatrix.m03 = solveBeizer(a.m03, b.m03, c.m03, t);
			
			newMatrix.m10 = solveBeizer(a.m10, b.m10, c.m10, t);
			newMatrix.m11 = solveBeizer(a.m11, b.m11, c.m11, t);
			newMatrix.m12 = solveBeizer(a.m12, b.m12, c.m12, t);
			newMatrix.m13 = solveBeizer(a.m13, b.m13, c.m13, t);
			
			newMatrix.m20 = solveBeizer(a.m20, b.m20, c.m20, t);
			newMatrix.m21 = solveBeizer(a.m21, b.m21, c.m21, t);
			newMatrix.m22 = solveBeizer(a.m22, b.m22, c.m22, t);
			newMatrix.m23 = solveBeizer(a.m23, b.m23, c.m23, t);
		} else {
			
			float factor = t*t*(3-(2*t));
			
			newMatrix.m00 = solveLerp(a.m00, b.m00, factor);
			newMatrix.m01 = solveLerp(a.m01, b.m01, factor);
			newMatrix.m02 = solveLerp(a.m02, b.m02, factor);
			newMatrix.m03 = solveLerp(a.m03, b.m03, factor);

			newMatrix.m10 =solveLerp(a.m10, b.m10, factor);
			newMatrix.m11 = solveLerp(a.m11, b.m11, factor);
			newMatrix.m12 = solveLerp(a.m12, b.m12, factor);
			newMatrix.m13 = solveLerp(a.m13, b.m13, factor);

			newMatrix.m20 =solveLerp(a.m20, b.m20, factor);
			newMatrix.m21 = solveLerp(a.m21, b.m21, factor);
			newMatrix.m22 = solveLerp(a.m22, b.m22, factor);
			newMatrix.m23 = solveLerp(a.m23, b.m23, factor);
		}
		
		
		newMatrix.m30 = solveBeizer(a.m30, b.m30, c.m30, t);
		newMatrix.m31 = solveBeizer(a.m31, b.m31, c.m31, t);
		newMatrix.m32 = solveBeizer(a.m32, b.m32, c.m32, t);
		newMatrix.m33 = solveBeizer(a.m33, b.m33, c.m33, t);
		
		
		
		
		return newMatrix;
		
	}
	

    public static void applyMatrix(Matrix4f m) { // TODO move out
        if(m == null) {
            return;
        }
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        m.store(buf);
        buf.rewind();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glMultMatrix(buf);
    }

    public static void loadMatrix(Matrix4f m) {
        if(m == null) {
            return;
        }
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        m.store(buf);
        buf.rewind();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadMatrix(buf);
    }

    public static Matrix4f captureMatrix() {
        Matrix4f matrix;
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
        buf.rewind();
        matrix = new Matrix4f();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        matrix.load(buf);
        return matrix;
    }
    
    public static FloatBuffer getModelViewMatrixBuffer() {
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
        buf.rewind();
        return buf;
    }
    
    public Matrix4f interpTwo(Matrix4f one, Matrix4f two, float f) {
Matrix4f result = new Matrix4f();

        
	result.m00 = solveLerp(one.m00, two.m00, f);
	result.m01 = solveLerp(one.m01, two.m01, f);
	result.m02 = solveLerp(one.m02, two.m02, f);
	result.m03 = solveLerp(one.m03, two.m03, f);
	
	result.m10 = solveLerp(one.m10, two.m10, f);
	result.m11 = solveLerp(one.m11, two.m11, f);
	result.m12 = solveLerp(one.m12, two.m12, f);
	result.m13 = solveLerp(one.m13, two.m13, f);
	
	result.m20 = solveLerp(one.m20, two.m20, f);
	result.m21 = solveLerp(one.m21, two.m21, f);
	result.m22 = solveLerp(one.m22, two.m22, f);
	result.m23 = solveLerp(one.m23, two.m23, f);
	
	result.m30 = solveLerp(one.m30, two.m30, f);
	result.m31 = solveLerp(one.m31, two.m31, f);
	result.m32 = solveLerp(one.m32, two.m32, f);
	result.m33 = solveLerp(one.m33, two.m33, f);
	
	


        return result;
    }

    public static Matrix4f interpolateMatrix(Matrix4f m, float factor, Interpolation interp) {
    	//factor = factor*factor*(3-(2*factor));
    	
    	factor = (float) interp.interpolate(factor);
    	//factor = factor*factor;
    	
    	
    	//factor = 1-((1-factor)*(1-factor));
    	
    	//	factor = factor;
    	//factor = Math.round(factor);
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
