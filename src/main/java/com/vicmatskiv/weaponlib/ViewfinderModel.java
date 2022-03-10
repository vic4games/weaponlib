package com.vicmatskiv.weaponlib;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.FlatSurfaceModelBox;
import com.vicmatskiv.weaponlib.perspective.OpticalScopePerspective;
import com.vicmatskiv.weaponlib.render.scopes.Reticle;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ViewfinderModel extends ModelBase {

	private ModelRenderer surfaceRenderer;
	private FlatSurfaceModelBox box;

	public ViewfinderModel() {
		textureWidth = 128;
		textureHeight = 64;

		surfaceRenderer = new ModelRenderer(this, 0, 0);
		box = new FlatSurfaceModelBox(surfaceRenderer, 0, 0, 0f, 0f, 0f, 3, 3, 0, 0.0F);
		surfaceRenderer.cubeList.add(box);
		surfaceRenderer.mirror = true;
		surfaceRenderer.setRotationPoint(0F, -10F, 0F);
		surfaceRenderer.setTextureSize(100, 100);
		
		setRotation(surfaceRenderer, 0F, 0F, 0F);
	}

	public void render(Reticle ret, Entity entity, float f5) {
		
		
		//super.render(entity, f, f1, f2, f3, f4, f5);
		//setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		
		
		OpticalScopePerspective.scope = ShaderManager.loadVMWShader("vignette");
		
		OpticalScopePerspective.scope.use();
		
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+4);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ret.getReticleTexture());
		//Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mw" + ":" + "textures/hud/reticle1.png"));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    	GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
    	
    	
    	GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+6);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mw" + ":" + "textures/hud/scopedirt.png"));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    	GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
    	
    	GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+7);
		ResourceLocation loc = new ResourceLocation("mw" + ":" + "textures/crosshairs/reflexret.png");
	
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
    	
	//	System.out.println(ClientModContext.getContext().getMainHeldWeapon().state);
    	
    	if(ClientModContext.getContext() != null && ClientModContext.getContext().getMainHeldWeapon() != null) {
    		float pwi = ClientModContext.getContext().getMainHeldWeapon().getZoom();
    		///System.out.println(ClientModContext.getContext().getMainHeldWeapon().state);
    		if(ClientModContext.getContext().getMainHeldWeapon().state != WeaponState.READY && ClientModContext.getContext().getMainHeldWeapon().state != WeaponState.PAUSED && ClientModContext.getContext().getMainHeldWeapon().state != WeaponState.EJECT_REQUIRED && ClientModContext.getContext().getMainHeldWeapon().state != WeaponState.ALERT) {
        		ClientValueRepo.scopeY = 1;
        	}
    		//System.out.println(pwi);
    		OpticalScopePerspective.scope.uniform1f("reticleZoom", (pwi+0.86f));
    	    
    		//OpticalScopePerspective.scope.uniform1i("cancel", ClientModContext.getContext().getMainHeldWeapon().isAimed() ? 0 : 1);
        	
    	}
    	
    	ClientValueRepo.scopeX = 0;
    	ClientValueRepo.scopeY = 0;
    	    	
    	GL20.glUniform1i(GL20.glGetUniformLocation(OpticalScopePerspective.scope.getShaderId(), "reticle"), 4);
     	GL20.glUniform1i(GL20.glGetUniformLocation(OpticalScopePerspective.scope.getShaderId(), "dirt"), 6);
     	GL20.glUniform1i(GL20.glGetUniformLocation(OpticalScopePerspective.scope.getShaderId(), "holo"), 7);
    	GL20.glUniform2f(GL20.glGetUniformLocation(OpticalScopePerspective.scope.getShaderId(), "Velocity"),
    			(float) ClientValueRepo.scopeX, (float) ClientValueRepo.scopeY);
    	OpticalScopePerspective.scope.uniform2f("resolution", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    	//	GL20.glUniform1f(GL20.glGetUniformLocation(OpticalScopePerspective.scope.getShaderId(), "Radius"),
    //			0.55f);
    	
    	GlStateManager.enableBlend();
		surfaceRenderer.render(f5);
		OpticalScopePerspective.scope.release();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
