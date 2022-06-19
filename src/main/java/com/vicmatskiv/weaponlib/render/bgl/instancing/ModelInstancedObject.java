package com.vicmatskiv.weaponlib.render.bgl.instancing;

import java.awt.List;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import javax.xml.bind.ValidationException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import com.vicmatskiv.weaponlib.config.novel.ModernConfigManager;
import com.vicmatskiv.weaponlib.render.VAOData;
import com.vicmatskiv.weaponlib.render.WavefrontModel;
import com.vicmatskiv.weaponlib.render.bgl.GLCompatible;
import com.vicmatskiv.weaponlib.shader.jim.Attribute;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

public class ModelInstancedObject<K> extends BasicInstancedObject<K> {

	private WavefrontModel model;
	
	public ModelInstancedObject(String shader, WavefrontModel model, int renderMode, int maxCopies, InstancedAttribute... attribs) {
		super(shader, renderMode, maxCopies, attribs);
		this.model = model;
		
		if(!model.usesVAO()) {
			System.err.println("Does not support VAOs!");
		}
		
		VAOData vaoData = new VAOData(model.getVAOID(), model.vertices.size());
		initialize(vaoData);
		setupShader();
	}



	@Override
	public void updateData(K obj) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(int primCount) {
		
	
		preRender();
		
		GLCompatible.glDrawElementsInstanced(getRenderMode(), model.indexBuffer.size(), GL11.GL_UNSIGNED_INT, 0, primCount);
		
		
		postRender();
	}



	@Override
	protected void setupShader() {
		
		
		int pointer = 0;
		Attribute[] array = new Attribute[3+getAttribs().length];
		array[pointer++] = new Attribute("aPos", 0);
		array[pointer++] = new Attribute("aNormal", 1);
		array[pointer++] = new Attribute("aTexCoord", 2);
		for(InstancedAttribute ia : getAttribs()) {
			array[pointer++] = new Attribute(ia.getAttributeName(), ia.getAttributeID());
		}
		
		
		
		Shader shad = ShaderManager.loadVMWShader(this.shaderName, array);
		setRenderShader(shad);
		
	}

	
}
