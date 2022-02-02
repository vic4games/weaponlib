package com.vicmatskiv.weaponlib.render;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.util.vector.Matrix4f;

import com.vicmatskiv.weaponlib.ClientEventHandler;
import com.vicmatskiv.weaponlib.model.Bullet556;
import com.vicmatskiv.weaponlib.render.ModelRenderTool.VertexData;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

import net.minecraft.client.model.TexturedQuad;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

public class ShellRenderer2 {
	
	public static final int MAX = 10;
	public static final float[] VERTICES = { 0f, 0f, 0f,
			0f, 1f, 0f,
			0f, 0f, 1f,
			1f, 0f, 0f,
			1f, 1f, 0f,
			1f, 0f, 1f};
	public static final int INSTANCE_DATA_LENGTH = 3;
	
	public static int vbo;
	
	public static boolean made =false;
	
	public static VAOData data;
	
	public static FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX*INSTANCE_DATA_LENGTH);
	
	public static int pointer = 0;
	
	public static Shader shader = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/instanced"));
	
	
	public static void init() {
		if(made) return;
		made = true;
		
		
	
		vbo = createEmptyVBO(INSTANCE_DATA_LENGTH*MAX);
		
		Triangle[] tris = ModelRenderTool.triangulate((new Bullet556()).boxList.get(0).cubeList.get(0), new Matrix4f());
		VertexData vd = ModelRenderTool.compress(tris);
		float[] vets = vd.vertexArray();
		
		data = VAOLoader.loadToVAO(VERTICES);
		addInstancedAttribute(data.getVaoID(), vbo, 1, 3, INSTANCE_DATA_LENGTH, 0);
	}
	
	public static void realRender() {
		//shader = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/instanced"));
		
		Triangle[] tris = ModelRenderTool.triangulate((new Bullet556()).boxList.get(0).cubeList.get(0), new Matrix4f());
		VertexData vd = ModelRenderTool.compress(tris);
		float[] vets = vd.vertexArray();
		
		for(int x = 0; x < vets.length; x++) {
			vets[x] *= 0.2f;
		}
		System.out.println(Arrays.toString(vets));
		data = VAOLoader.loadToVAO(vets);
		addInstancedAttribute(data.getVaoID(), vbo, 1, 3, INSTANCE_DATA_LENGTH, 0);
		
		shader.use();
		bindAttribute(shader.getShaderId(), 1, "posy");
		
		shader.sendMatrix4AsUniform("projection", false, ClientEventHandler.PROJECTION);
		shader.sendMatrix4AsUniform("modelView", false, ClientEventHandler.MODELVIEW);
		
		// prepare
		GL30.glBindVertexArray(data.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		
		pointer = 0;
		float[] vboData = new float[MAX*INSTANCE_DATA_LENGTH];
		fillData(vboData);
		
		VAOLoader.updateVBO(vbo, vboData, buffer);
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, data.getVertexCount(), MAX);
		
		
		
		// post
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		
		shader.release();
		
	}
	
	public static void fillData(float[] data) {
		for(int i = 0; i < (data.length/3); ++i) {
			data[pointer++] = 0;
			data[pointer++] = pointer;
			data[pointer++] = 0;
		}
	}
	
	public static void bindAttribute(int shaderID, int attribID, String variableName) {
		GL20.glBindAttribLocation(shaderID, attribID, variableName);
	}
	
	public static void render() {
		GL30.glBindVertexArray(data.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, data.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	
	public static int createEmptyVBO(int floatCount) {
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public static void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength,
			int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4); 
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

}
