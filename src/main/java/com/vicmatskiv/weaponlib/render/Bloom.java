package com.vicmatskiv.weaponlib.render;

import java.nio.IntBuffer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import com.vicmatskiv.weaponlib.WeaponFireAspect;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.render.bgl.GLCompatible;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

/**
 * Bloom implementation based on https://github.com/Drillgon200/Hbm-s-Nuclear-Tech-GIT/blob/30df900f4f7f3827133bc58fb26f922da5f3d909/src/main/java/com/hbm/handler/HbmShaderManager2.java#L285 using
 * the techniques used in COD (Next Generation Post Processing by Jorge Jiminez)
 * @author homer
 *
 */
public class Bloom {
	
	 private static final Logger logger = LogManager.getLogger(Bloom.class);

	
	public static final Minecraft mc = Minecraft.getMinecraft();
	
	public static int width = mc.displayWidth;
	public static int height = mc.displayHeight;
	public static boolean hasLoaded = false;
	
	public static final int LAYERS = 6;
	public static Framebuffer[] buffers;
	public static Framebuffer data;
	
	
	public static void setupBloom() {
	
		
			//logger.info("Creating bloom buffer, MC's Framebuffer is {}, the world is {}", Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().world);
		//logger.log(Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6);
			width = mc.displayWidth;
			height = mc.displayHeight;
			hasLoaded = true;
			
		
			logger.debug("Recreating Bloom FBOs at ({} x {})", width, height);
			
			//System.out.println("Creating a Bloom FX w/ " + width + "x" + height);
			recreateFramebuffers();
			
			
			
			Dloom.height = -1;
			
			// blur
			if(CompatibleClientEventHandler.buf != null)
				CompatibleClientEventHandler.buf.deleteFramebuffer();
			CompatibleClientEventHandler.buf = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
			
			
			
		
	}
	
	public static boolean shouldRecreateFBOs() {
		return mc.displayWidth != width || mc.displayHeight != height || !hasLoaded;
			
	}
	
	public static boolean bloomNotAvaliable() {
		return data == null;
	}
	
	
	public static void doBloom() {
		if(shouldRecreateFBOs()) setupBloom();
		renderHDRToBuffer();
		use();
	
		
	}
	
	
	public static void bindBloomBuffer() {
		/*
		if(data == null) {
			width = mc.displayWidth;
			height = mc.displayHeight;
			recreateFramebuffers();
		}*/
		if(bloomNotAvaliable()) return;
		
		data.bindFramebuffer(false);
	}
	
	public static void recreateFramebuffers() {
		
		int width = mc.displayWidth;
		int height = mc.displayHeight;
		
		
		if(buffers != null) {
			for(Framebuffer f : buffers) {
				f.deleteFramebuffer();
			}
		}
		

		if(data != null) data.deleteFramebuffer();
		
		
		data = new Framebuffer(width, height, true);
		data.bindFramebufferTexture();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GLCompatible.GL_RGBA16F, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_SHORT, (IntBuffer) null);
		data.bindFramebuffer(false);
		

		OpenGlHelper.glBindRenderbuffer(GLCompatible.GL_RENDERBUFFER, mc.getFramebuffer().depthBuffer);
		OpenGlHelper.glFramebufferRenderbuffer(GLCompatible.GL_FRAMEBUFFER, GLCompatible.GL_DEPTH_ATTACHMENT, GLCompatible.GL_RENDERBUFFER, mc.getFramebuffer().depthBuffer);

		data.setFramebufferFilter(GL11.GL_LINEAR);
		data.setFramebufferColor(0, 0, 0, 0);
		data.framebufferClear();

		checkFramebufer(data.framebufferObject);

		buffers = new Framebuffer[LAYERS];
		float bW = width;
		float bH = height;
		
		for(int i = 0; i < LAYERS; ++i) {
			
			buffers[i] = new Framebuffer((int) bW, (int) bH, false);
			buffers[i].bindFramebufferTexture();
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GLCompatible.GL_RGBA16F, (int) bW, (int) bH, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_SHORT, (IntBuffer) null);
			buffers[i].setFramebufferFilter(GL11.GL_LINEAR);
			buffers[i].setFramebufferColor(0, 0, 0, 0);
			if(i < 1){
				bW *= 0.25F;
				bH *= 0.25F;
			} else {
				bW *= 0.5F;
				bH *= 0.5F;
			}
			
		}
		
		logger.debug("Refreshed Bloom buffer succesfully!");
		
		
 	}
	
	public static void checkFramebufer(int buf) {
		int i = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
		//System.out.println("hi " + (i == GL30.GL_FRAMEBUFFER_COMPLETE));
        if (i != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE)
        {
        	
            if (i == OpenGlHelper.GL_FB_INCOMPLETE_ATTACHMENT)
            {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            }
            else if (i == OpenGlHelper.GL_FB_INCOMPLETE_MISS_ATTACH)
            {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            }
            else if (i == OpenGlHelper.GL_FB_INCOMPLETE_DRAW_BUFFER)
            {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            }
            else if (i == OpenGlHelper.GL_FB_INCOMPLETE_READ_BUFFER)
            {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            }
            else
            {
                throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
            }
        }
		//System.out.println("Framebuffer check: " + (GL30.glCheckFramebufferStatus(buf) == GL30.GL_FRAMEBUFFER_COMPLETE));
	}
	
	
	public static void renderHDRToBuffer() {

		
		//data.framebufferClear();
		
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+4);
		GlStateManager.bindTexture(Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		
		data.bindFramebuffer(false);
		GlStateManager.enableBlend();
		
		//Shaders.bloomTest = ShaderManager.loadVMWShader("btest");
		
		Shaders.bloomTest.use();
		Shaders.bloomTest.uniform1i("real", 4);
		renderFboTriangle(data, data.framebufferWidth, data.framebufferHeight);
		Shaders.bloomTest.release();
		GlStateManager.disableBlend();
	}
	
	public static void renderFboTriangle(Framebuffer buf) {
		renderFboTriangle(buf, buf.framebufferWidth, buf.framebufferHeight);
	}
	
	public static void renderFboTriangle(Framebuffer buf, int width, int height){
		GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.viewport(0, 0, width, height);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();

        GlStateManager.enableColorMaterial();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        buf.bindFramebufferTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-1, -1, 0.0D).tex(0, 0).endVertex();
        bufferbuilder.pos(3, -1, 0.0D).tex(2, 0).endVertex();
        bufferbuilder.pos(-1, 3, 0.0D).tex(0, 2).endVertex();
        tessellator.draw();
        buf.unbindFramebufferTexture();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
	}
	
	
	public static void runDownsampler() {
		buffers[0].bindFramebuffer(true);
		Shaders.downsample.use();
		GL20.glUniform2f(GL20.glGetUniformLocation(Shaders.downsample.getShaderId(), "texel"), 1F/(float) data.framebufferTextureWidth, 1F/(float) data.framebufferTextureHeight);
		renderFboTriangle(data, buffers[0].framebufferWidth, buffers[0].framebufferHeight);
		for(int i = 1; i < LAYERS; i++) {
			buffers[i].bindFramebuffer(true);
			GL20.glUniform2f(GL20.glGetUniformLocation(Shaders.downsample.getShaderId(), "texel"), 1F/(float) buffers[i-1].framebufferTextureWidth, 1F/(float) buffers[i-1].framebufferTextureHeight);
			renderFboTriangle(buffers[i-1], buffers[i].framebufferWidth, buffers[i].framebufferHeight);
		}
		
		Shaders.downsample.release();
		
		
	}
	
	public static void use() {
		
		/*
		runDownsampler();
		GlStateManager.enableBlend();
		for(int i = LAYERS-2; i >= 0; --i) {			
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
			buffers[i+1].bindFramebuffer(true);

			upsample.use();
			GL20.glUniform2f(GL20.glGetUniformLocation(upsample.getShaderId(), "fragmentSize"), 1F/(float) buffers[i].framebufferWidth, 1F/(float) buffers[i].framebufferHeight);
			renderFboTriangle(buffers[i], buffers[i+1].framebufferWidth, buffers[i+1].framebufferHeight);
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
			int tWidth, tHeight;
			if(i == 0){
				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
				tWidth = Minecraft.getMinecraft().getFramebuffer().framebufferWidth;
				tHeight = Minecraft.getMinecraft().getFramebuffer().framebufferHeight;
			} else {
				GlStateManager.glBlendEquation(GL14.GL_MAX);
				buffers[(i-1)].bindFramebuffer(true);
				tWidth = buffers[(i-1)].framebufferWidth;
				tHeight = buffers[(i-1)].framebufferHeight;
			}
			renderFboTriangle(buffers[i+1], tWidth, tHeight);
			GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		}
		GL20.glUseProgram(0);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		data.bindFramebuffer(true);
		GlStateManager.clearColor(data.framebufferColor[0], data.framebufferColor[1], data.framebufferColor[2], data.framebufferColor[3]);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
		
		GlStateManager.enableAlpha();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		*/
		
		
		
		runDownsampler();
		GlStateManager.enableBlend();
		for(int i = LAYERS-1; i >= 0; --i) {			
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
			buffers[i].bindFramebuffer(true);

			Shaders.upsample.use();
			/*
			GL20.glUniform2f(GL20.glGetUniformLocation(upsample.getShaderId(), "fragmentSize"), 1F/(float) buffers[i].framebufferWidth, 1F/(float) buffers[i].framebufferHeight);
			renderFboTriangle(buffers[i], buffers[i+1].framebufferWidth, buffers[i+1].framebufferHeight);
			*/
			//GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
			GL20.glUniform2f(GL20.glGetUniformLocation(Shaders.upsample.getShaderId(), "fragmentSize"), 1F/(float) buffers[i].framebufferWidth, 1F/(float) buffers[i].framebufferHeight);
			
			int tWidth, tHeight;
			if(i == 0){
				
				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
				tWidth = Minecraft.getMinecraft().getFramebuffer().framebufferWidth;
				tHeight = Minecraft.getMinecraft().getFramebuffer().framebufferHeight;
			} else {
				GlStateManager.glBlendEquation(GL14.GL_MAX);
				buffers[(i-1)].bindFramebuffer(true);
				tWidth = buffers[(i-1)].framebufferWidth;
				tHeight = buffers[(i-1)].framebufferHeight;
			}
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
			
			renderFboTriangle(buffers[i], tWidth, tHeight);
			GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		}
		GL20.glUseProgram(0);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		data.bindFramebuffer(true);
		GlStateManager.clearColor(data.framebufferColor[0], data.framebufferColor[1], data.framebufferColor[2], data.framebufferColor[3]);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
		
		GlStateManager.enableAlpha();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		
		
	}
	
	/**
	 * MSAA
	 */
	
	public static boolean multisample = false;
	public static int multisampleFBO = 0;
	public static int multiampleTexFBO = 0;
	public static int mRes = 0;
	public static int msaaDepthTex = 0;
	
	public static void bindMultisample() {
		GLCompatible.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, multisampleFBO);
	}

	
	
	public static void bindMinecraft() {
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
	}
	
	public static int getWidthTimesHeight() {
		return Minecraft.getMinecraft().displayWidth*Minecraft.getMinecraft().displayHeight;
	}
	
	public static void setupMultisampleBuffer() {
		if(GLCompatible.isLoaded &&GLCompatible.multisampleType == -1) return;
		if(multisample && getWidthTimesHeight() == mRes) return;
		//System.out.println("Recalculating MSAA buffer...");
		mRes = getWidthTimesHeight();
		
		multisampleFBO  = GLCompatible.glGenFramebuffers();
		GLCompatible.glBindFramebuffer(GLCompatible.GL_FRAMEBUFFER, multisampleFBO);
		multiampleTexFBO = GL11.glGenTextures();
		
		int width = Minecraft.getMinecraft().displayWidth;
		int height = Minecraft.getMinecraft().displayHeight;
		
		GL11.glBindTexture(GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, multiampleTexFBO);
		GLCompatible.glTexImage2DMultisample(GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, 4, GL11.GL_RGBA8, width, height, false);
		GLCompatible.glFramebufferTexture2D(GLCompatible.GL_FRAMEBUFFER, GLCompatible.GL_COLOR_ATTACHMENT0, GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, multiampleTexFBO, 0);
		
		GlStateManager.enableDepth();
		
		// depth
		
		
		msaaDepthTex = GL11.glGenTextures();
		GL11.glBindTexture(GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, msaaDepthTex);
		GLCompatible.glTexImage2DMultisample(GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, 4, GL14.GL_DEPTH_COMPONENT24, width, height, false);
		GLCompatible.glFramebufferTexture2D(GLCompatible.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, msaaDepthTex, 0);
		
		//System.out.println(GL11.glGetError());
		
		multisample = true;
		
	}
	
	
	public static void initializeMultisample(Framebuffer initial) {
		if(GLCompatible.isLoaded && GLCompatible.multisampleType == -1) return;
		int gWidth = initial.framebufferWidth;
		int gHeight = initial.framebufferHeight;
    	setupMultisampleBuffer();
    	GlStateManager.enableDepth();
    	GLCompatible.glBindFramebuffer(GLCompatible.GL_READ_FRAMEBUFFER, initial.framebufferObject);
    	
		//GLCompatible.glBindFramebuffer(GLCompatible.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
		GLCompatible.glBindFramebuffer(GLCompatible.GL_DRAW_FRAMEBUFFER, multisampleFBO);
        GLCompatible.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        
        GLCompatible.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        
        
        
        bindMultisample();
	}
	
	public static void initializeMultisample() {
		initializeMultisample(Minecraft.getMinecraft().getFramebuffer());
	}
	
	public static void unapplyMultisample() {
		unapplyMultisample(Minecraft.getMinecraft().getFramebuffer());
	}
	
	public static void unapplyMultisample(Framebuffer initial) {
		if(GLCompatible.isLoaded && GLCompatible.multisampleType == -1) return;
		
		/*
		int gWidth = Minecraft.getMinecraft().displayWidth;
    	int gHeight = Minecraft.getMinecraft().displayHeight;
    	*/
		
		int gWidth = initial.framebufferWidth;
		int gHeight = initial.framebufferHeight;
		
    	GLCompatible.glBindFramebuffer(GLCompatible.GL_READ_FRAMEBUFFER, multisampleFBO);
    	GLCompatible.glBindFramebuffer(GLCompatible.GL_DRAW_FRAMEBUFFER, initial.framebufferObject);
        GLCompatible.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
       // GLCompatible.glBlitFramebuffer(0, 0, gWidth, gHeight, 0, 0, gWidth, gHeight, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        
	}

}
