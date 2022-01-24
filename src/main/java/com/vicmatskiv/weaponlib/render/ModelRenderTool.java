package com.vicmatskiv.weaponlib.render;

import java.lang.reflect.Field;

import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ModelRenderTool {
	
	public static Field quadListField = null;
	
	public static class VertexData {
		public Vec3d[] positions;
		public int[] positionsIndices;
		public float[] texCoords;
		
		public float[] vertexArray() {
			return texCoords;
			
		}
	}
	

	
	public static Triangle[] triangulate(ModelBox b, Matrix4f transform) {
		if(quadListField == null){
			quadListField = ReflectionHelper.findField(ModelBox.class, "quadList", "field_78254_i");
		}
		TexturedQuad[] quadList;
		Triangle[] tris = new Triangle[12];
		try {
			quadList = (TexturedQuad[]) quadListField.get(b);
			int i = 0;
			for(TexturedQuad t : quadList){
				Vec3d v0 = transformViaMatrix(t.vertexPositions[0].vector3D, transform);
				Vec3d v1 = transformViaMatrix(t.vertexPositions[1].vector3D, transform);
				Vec3d v2 = transformViaMatrix(t.vertexPositions[2].vector3D, transform);
				Vec3d v3 = transformViaMatrix(t.vertexPositions[3].vector3D, transform);
				float[] tex = new float[6];
				tex[0] = t.vertexPositions[0].texturePositionX;
				tex[1] = t.vertexPositions[0].texturePositionY;
				tex[2] = t.vertexPositions[1].texturePositionX;
				tex[3] = t.vertexPositions[1].texturePositionY;
				tex[4] = t.vertexPositions[2].texturePositionX;
				tex[5] = t.vertexPositions[2].texturePositionY;
				tris[i++] = new Triangle(v0, v1, v2, tex);
				tex = new float[6];
				tex[0] = t.vertexPositions[2].texturePositionX;
				tex[1] = t.vertexPositions[2].texturePositionY;
				tex[2] = t.vertexPositions[3].texturePositionX;
				tex[3] = t.vertexPositions[3].texturePositionY;
				tex[4] = t.vertexPositions[0].texturePositionX;
				tex[5] = t.vertexPositions[0].texturePositionY;
				tris[i++] = new Triangle(v2, v3, v0, tex);
			}
			return tris;
		} catch(IllegalArgumentException | IllegalAccessException e) {
		}
		throw new RuntimeException("Failed to get quads!");
		
	}
	
	public static Vec3d transformViaMatrix(Vec3d vec, Matrix4f mat){
		if(mat != null){
			double x = mat.m00 * vec.x + mat.m10 * vec.y + mat.m20 * vec.z + mat.m30;
			double y = mat.m01 * vec.x + mat.m11 * vec.y + mat.m21 * vec.z + mat.m31;
			double z = mat.m02 * vec.x + mat.m12 * vec.y + mat.m22 * vec.z + mat.m32;
			return new Vec3d(x, y, z);
		}
		return vec;
	}
	
	
}
