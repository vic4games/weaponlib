package com.jimholden.conomy.shaders;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.OpenGlHelper;


public class Shader {
	private int shader;
	private List<Uniform> uniforms = new ArrayList<>(2);
	
	public Shader(int shader) {
		this.shader = shader;
	}
	
	public Shader withUniforms(Uniform...uniforms) {
		for(Uniform u : uniforms) {
			this.uniforms.add(u);
		}
		return this;
	}
	
	public void use() {
		if(!ShaderManager.enableShaders) return;
		GL20.glUseProgram(shader);
		for(Uniform u : uniforms) {
			u.apply(shader);
		}
	}
	
	public void release() {
		GL20.glUseProgram(0);
	}
	
	public int getShaderId() {
		return shader;
	}
	
	public void sendMatrix4AsUniform(String name, boolean transpose, FloatBuffer mat) {
		OpenGlHelper.glUniformMatrix4(GL20.glGetUniformLocation(shader, name),  transpose, mat);
	}

}
