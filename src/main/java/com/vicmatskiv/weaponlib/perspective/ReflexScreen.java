// Made with Blockbench 4.1.1
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports
package com.vicmatskiv.weaponlib.perspective;



import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.vicmatskiv.weaponlib.CustomRenderer;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.render.Dloom;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class ReflexScreen extends ModelBase implements CustomRenderer<RenderableState>{
	private final ModelRenderer bb_main;

	public BiConsumer<EntityLivingBase, ItemStack> positioning;
	public float texScale;
	public ResourceLocation reticle;
	public float radius;
	public Vec3d background;
	
	public ReflexScreen(BiConsumer<EntityLivingBase, ItemStack> pos, float texScale, float radius, Vec3d background, ResourceLocation reticle) {
		textureWidth = 16;
		textureHeight = 16;
		
		this.texScale = texScale;
		this.background = background;
		this.reticle = reticle;
		this.radius = radius;
		this.positioning = pos;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -3.0F, -2.0F, 0.0F, 5, 4, 0, 0.0F, false));
	}

	public static Shader reflexReticle = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/reflex"));
	//https://vazgriz.com/158/reflex-sight-shader-in-unity3d/
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		if(true) {
			reflexReticle = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/reflex"));
			
		}
		
		
		
		
		
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		//GlStateManager.enableAlpha();
		reflexReticle.use();
		
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+4);
		ResourceLocation loc = new ResourceLocation("mw" + ":" + "textures/crosshairs/okp.png");
	
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		GL20.glUniform1i(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "ret"), 4);
		
		GL20.glUniform1f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "texScale"), 0.08f);
		GL20.glUniform1f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "radius"), 0.1f);
		
		
		Vec3d bg = new Vec3d(0.0, 0.7, 0.5);
		GL20.glUniform3f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "background"), (float) bg.x, (float) bg.y, (float) bg.z);
		
		GlStateManager.enableCull();
		
		/* eo tech
		bb_main.offsetY = -2.5f;
		bb_main.offsetX = -0.075f;
		bb_main.offsetZ = 0.275f;
		*/
		GlStateManager.pushMatrix();
		
		/* reflex
		bb_main.offsetY = -2.60f;
		bb_main.offsetX = -0.05f;
		bb_main.offsetZ = 0.0f;
		*/
		
		
		GlStateManager.translate(0.25, -5.05, -0.1);
		GlStateManager.scale(2.0, 2.0, 1.2);
		bb_main.render(f5);
		
		
		GlStateManager.popMatrix();
		reflexReticle.release();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void render(RenderContext<RenderableState> renderContext) {
		
		reflexReticle = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/reflex"));
		
		//first
	//	Dloom.bloomData.bindFramebuffer(true);
		
		if(renderContext.getCompatibleTransformType() != CompatibleTransformType.FIRST_PERSON_RIGHT_HAND) return;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		reflexReticle.use();
		
		// upload uniforms
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+4);
		ResourceLocation loc = new ResourceLocation("mw" + ":" + "textures/crosshairs/holo.png");
	
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		GL20.glUniform1i(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "ret"), 4);
		
		GL20.glUniform1f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "texScale"), this.texScale);
		GL20.glUniform1f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "radius"), this.radius);
		
		
		
		GL20.glUniform3f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "background"), (float) background.x, (float) background.y, (float) background.z);
		GlStateManager.enableCull();
		
		GlStateManager.pushMatrix();
		positioning.accept(renderContext.getPlayer(), renderContext.getWeapon());
		bb_main.render(0.065f);
		
		
		GlStateManager.popMatrix();
		reflexReticle.release();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		
		
		
		
		
		
		
		// second\
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
		if(renderContext.getCompatibleTransformType() != CompatibleTransformType.FIRST_PERSON_RIGHT_HAND) return;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		reflexReticle.use();
		
		// upload uniforms
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+4);
		loc = new ResourceLocation("mw" + ":" + "textures/crosshairs/holo.png");
	
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		GL20.glUniform1i(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "ret"), 4);
		
		GL20.glUniform1f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "texScale"), this.texScale);
		GL20.glUniform1f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "radius"), this.radius);
		
		
		
		GL20.glUniform3f(GL20.glGetUniformLocation(reflexReticle.getShaderId(), "background"), (float) background.x, (float) background.y, (float) background.z);
		GlStateManager.enableCull();
		
		GlStateManager.pushMatrix();
		positioning.accept(renderContext.getPlayer(), renderContext.getWeapon());
		bb_main.render(0.065f);
		
		
		GlStateManager.popMatrix();
		reflexReticle.release();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		
		
	}
}