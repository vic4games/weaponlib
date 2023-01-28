package com.vicmatskiv.weaponlib.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Quaternion;

import com.google.gson.JsonObject;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.render.bgl.GLCompatible;
import com.vicmatskiv.weaponlib.render.bgl.ModernUtil;
import com.vicmatskiv.weaponlib.render.bgl.instancing.InstancedAttribute;
import com.vicmatskiv.weaponlib.render.bgl.instancing.ModelInstancedObject;
import com.vicmatskiv.weaponlib.render.shells.ShellManager;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell.Type;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;
import com.vicmatskiv.weaponlib.shader.jim.Uniform;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class InstancedShellObject extends ModelInstancedObject<ShellManager> {
	private Type type;

	public InstancedShellObject(Shell.Type type, String shader, WavefrontModel model, int renderMode, int maxCopies, InstancedAttribute... attribs) {
		super(shader, model, renderMode, maxCopies, attribs);
		this.type = type;
	}
	
	@Override
	protected void setupShader() {
		// TODO Auto-generated method stub
		super.setupShader();
		getRenderShader().withUniforms(new Uniform() {

			@Override
			public void apply(int shader) {
				GL20.glUniform1i(GL20.glGetUniformLocation(shader, "lightmap"), 1);
			}
			
		});
	}
	

	public void updateData(ArrayList<Shell> shells) {
		
		/*
		arrayPointer = 0;
		float[] data = new float[getInstanceDataLength()*getMaxObjects()];
		for(int i = 0; i < (getInstanceDataLength()*getMaxObjects()/getInstanceDataLength()); ++i) {
			
			data[arrayPointer++] = arrayPointer/1000f;
			data[arrayPointer++] = 0;
			data[arrayPointer++] = 0;
			
			Quaternion quat = MatrixHelper.fromEulerAngles(Math.toRadians(90), Math.toRadians(0), 0);
			
			data[arrayPointer++] = quat.w;
			data[arrayPointer++] = quat.x;
			data[arrayPointer++] = quat.y;
			data[arrayPointer++] = quat.z;
			
			
		}
		uploadToBuffer(data);
		*/
		arrayPointer = 0;
		float[] data = new float[getInstanceDataLength()*getMaxObjects()];
		for(Shell sh : shells) {
			if(sh.getType() != type) continue;
			float iX = (float) MatrixHelper.solveLerp(sh.prevPos.x, sh.pos.x, Minecraft.getMinecraft().getRenderPartialTicks());
			float iY = (float) MatrixHelper.solveLerp(sh.prevPos.y, sh.pos.y, Minecraft.getMinecraft().getRenderPartialTicks());
			float iZ = (float) MatrixHelper.solveLerp(sh.prevPos.z, sh.pos.z, Minecraft.getMinecraft().getRenderPartialTicks());
			
			data[arrayPointer++] = (float) iX;
			data[arrayPointer++] = (float) iY;
			data[arrayPointer++] = (float) iZ;
			
			double rX = MatrixHelper.solveLerp(sh.prevRot.x, sh.rot.x, Minecraft.getMinecraft().getRenderPartialTicks());
			double rY = MatrixHelper.solveLerp(sh.prevRot.y, sh.rot.y, Minecraft.getMinecraft().getRenderPartialTicks());
			double rZ = MatrixHelper.solveLerp(sh.prevRot.z, sh.rot.z, Minecraft.getMinecraft().getRenderPartialTicks());
			
			
			Quaternion quat = MatrixHelper.fromEulerAngles(Math.toRadians(rX), Math.toRadians(rY), Math.toRadians(rZ));
			
			data[arrayPointer++] = quat.w;
			data[arrayPointer++] = quat.x;
			data[arrayPointer++] = quat.y;
			data[arrayPointer++] = quat.z;
			
			int i = Minecraft.getMinecraft().world.getCombinedLight(new BlockPos(sh.pos.x, sh.pos.y, sh.pos.z), 0);
			float f = (float) (i & 65535);
			float f1 = (float) (i >> 16);
			
			
			
			data[arrayPointer++] = (f+8)/256f;
			data[arrayPointer++] = (f1+8)/256f;
			
			
			
			//System.out.println((f/255f) + " | " + (f1/255f));
		}
		uploadToBuffer(data);
		
	}

}
