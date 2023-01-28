package com.jimholden.conomy.drugs;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.jimholden.conomy.shaders.Shader;
import com.jimholden.conomy.shaders.ShaderManager;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.ParticleSpell.MobFactory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

public class DrugRenderer {
	public static final Shader shader = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/shadersimpleeffects"));
	
	public static int height = 0;
    public static int width = 0;
    public static Framebuffer buf;
    
    public static int ticker = 0;
    public static int tickerHolder = 0;
    public static int rampTime = 500;
    public static int holdTime = 1000;
    public static int releaseTime = 500;
    
    
    public static Drug getTotalEffects() {
    	int rampUpTime = 0;
    	int holdTime = 0;
    	int releaseTime = 0;
    	float doubleVisionIntensity = 0;
    	float desaturation = 0;
    	float colorIntensity = 0;
    	float quickColRot = 0;
    	float slowColRot = 0;
    	float inverseAmount = 0;
    	float deconvergeAmount = 0;
    	float sharpenAmount = 0;
    	for(Drug drug : DrugCache.drugs) {
    		//System.out.println(drug.slowColRot);
    		colorIntensity += (drug.colorIntensity*drug.intensity());
    		desaturation += (drug.desaturation*drug.intensity());
    		quickColRot += (drug.quickColRot*drug.intensity());
    		slowColRot += (drug.slowColRot*drug.intensity());
    		doubleVisionIntensity += (drug.doubleVisionIntensity*drug.intensity());
    		releaseTime += (drug.releaseTime*drug.intensity());
    		rampUpTime += (drug.rampUpTime*drug.intensity());
    		holdTime += (drug.holdTime*drug.intensity());
    		inverseAmount += (drug.inverseAmount*drug.intensity());
    		deconvergeAmount += (drug.deconvergeAmount*drug.intensity());
    		sharpenAmount += (drug.sharpenAmount*drug.intensity());
    	}
    	
    	if(rampUpTime > 1) rampUpTime = 1;
    	if(holdTime > 1) holdTime = 1;
    	if(releaseTime > 1) releaseTime = 1;
    	if(doubleVisionIntensity > 1.0F) doubleVisionIntensity = 1;
    	if(desaturation > 1.0F) desaturation = 1;
    	if(colorIntensity > 1.0F) colorIntensity = 1.0F;
    	if(quickColRot > 1.0F) quickColRot = 1;
    	if(slowColRot > 1.0F) slowColRot = 1;
    	if(inverseAmount > 1.0F) inverseAmount = 1.0F;
    	if(deconvergeAmount > 1.0F) inverseAmount = 1.0F;
    	if(sharpenAmount > 1.0F) sharpenAmount = 1.0F;
    	
    	//System.out.println(colorIntensity);
    	
    	return new Drug("total", rampUpTime, holdTime, releaseTime, doubleVisionIntensity, desaturation, colorIntensity, quickColRot, slowColRot, inverseAmount, deconvergeAmount, sharpenAmount, 0, 0, 0, 0);
    	
    }
    
    //public static ResourceLocation prevFBO = null;
    
    public static Framebuffer prevFBO = null;
    static int tex = GL11.glGenTextures();
    
    public static void doPostProcess(){
    	
        if(height != Minecraft.getMinecraft().displayHeight || width != Minecraft.getMinecraft().displayWidth){
            recreateFBOs();
            height = Minecraft.getMinecraft().displayHeight;
            width = Minecraft.getMinecraft().displayWidth;
        }
        DrugCache.tickDrugs();
        GL11.glPushMatrix();
        buf.bindFramebuffer(false);
        

        shader.use();
        
        
        int deSat = GL20.glGetUniformLocation(shader.getShaderId(), "desaturation");
        int colorIntensity = GL20.glGetUniformLocation(shader.getShaderId(), "colorIntensification");
        
        
        int stretch = GL20.glGetUniformLocation(shader.getShaderId(), "stretch");
        int distance = GL20.glGetUniformLocation(shader.getShaderId(), "distance");
        int totalAlpha = GL20.glGetUniformLocation(shader.getShaderId(), "totalAlpha");
        
        int tickVal = GL20.glGetUniformLocation(shader.getShaderId(), "ticks");
        int slowColorRotation = GL20.glGetUniformLocation(shader.getShaderId(), "slowColorRotation");
        int quickColorRotation = GL20.glGetUniformLocation(shader.getShaderId(), "quickColorRotation");
        int inverseAmount = GL20.glGetUniformLocation(shader.getShaderId(), "inverseAmount");
        int sharpenAmount = GL20.glGetUniformLocation(shader.getShaderId(), "sharpenAmount");
        
        int deconvergeAmount = GL20.glGetUniformLocation(shader.getShaderId(), "deconvergeAmount");
        
        int screenRes = GL20.glGetUniformLocation(shader.getShaderId(), "screenRes");
        
        
        
        
        Drug totalDrug = getTotalEffects();
        
        ticker++;
        if(ticker > 100) {
        	ticker = 0;
        }
        
        /*
        if(ticker < rampTime) {
        	ticker++;
        } else {
        	if(tickerHolder < holdTime) {
        		tickerHolder++;
        	}
        	else {
        		ticker--;
        	}
      
        }
        */
        
        
        
        /*
        if(prevFBO == null) {
        	prevFBO = Minecraft.getMinecraft().getFramebuffer();
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);

            // Specify a texture image
        	GL13.glActiveTexture(GL13.GL_TEXTURE26);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 26, GL11.GL_RGBA8, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
        	//GL13.glActiveTexture(GL13.GL_TEXTURE26);
            //GlStateManager.bindTexture(prevFBO.framebufferTexture);
            //GL20.glUniform1i(GL20.glGetUniformLocation(shader.getShaderId(), "PrevSampler"), 26);
            //GL13.glActiveTexture(GL13.GL_TEXTURE0);
        }
        
       */
    
        
        
        
        
        
        //Minecraft.getMinecraft().getRenderManager().renderEntity(new EntitySheep(Minecraft.getMinecraft().world), 0, 0, 0, 0, 0, true);
        //System.out.println(intensity);
        
        
        
        GL20.glUniform1f(deSat, totalDrug.desaturation);
        GL20.glUniform1f(colorIntensity, totalDrug.colorIntensity);
        GL20.glUniform1f(distance, totalDrug.doubleVisionIntensity);
        GL20.glUniform1f(stretch, 1.0F);
        GL20.glUniform1f(totalAlpha, 0.5F);
        GL20.glUniform1f(tickVal, ticker);
        //System.out.println(totalDrug.slowColRot);
        GL20.glUniform1f(slowColorRotation, totalDrug.slowColRot);
        GL20.glUniform1f(quickColorRotation, totalDrug.quickColRot);
        GL20.glUniform1f(inverseAmount, totalDrug.inverseAmount);
        GL20.glUniform1f(deconvergeAmount, totalDrug.deconvergeAmount);
        GL20.glUniform1f(sharpenAmount, totalDrug.sharpenAmount);
        GL20.glUniform2f(screenRes, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        
        
        
        Minecraft.getMinecraft().getFramebuffer().framebufferRender(buf.framebufferWidth, buf.framebufferHeight);
        shader.release();
        
       
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buf.framebufferObject);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

        /*
        GL13.glActiveTexture(GL13.GL_TEXTURE26);
        GlStateManager.bindTexture(prevFBO.framebufferTexture);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader.getShaderId(), "PrevSampler"), 26);
        
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        */
        
        //GL13.glActiveTexture(GL13.GL_TEXTURE26);
        //GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 26, GL11.GL_RGBA8, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
        //GL13.glActiveTexture(GL13.GL_TEXTURE0);
        
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        GlStateManager.enableDepth();
        GL11.glPopMatrix();
    }
    
    public static void recreateFBOs(){
        if(buf != null)
            buf.deleteFramebuffer();
        buf = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
    }

}
