package com.vicmatskiv.weaponlib.animation;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class MatrixHelper {

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

    public static Matrix4f interpolateMatrix(Matrix4f m, float factor) {
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
