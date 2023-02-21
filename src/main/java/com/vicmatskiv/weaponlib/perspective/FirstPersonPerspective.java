package com.vicmatskiv.weaponlib.perspective;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;
import com.vicmatskiv.weaponlib.config.novel.ModernConfigManager;
import com.vicmatskiv.weaponlib.render.DepthTexture;
import com.vicmatskiv.weaponlib.render.Shaders;
import com.vicmatskiv.weaponlib.render.bgl.PostProcessPipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;

public class FirstPersonPerspective<S> extends Perspective<S> {

    private long renderEndNanoTime;

    public FirstPersonPerspective() {
        this.renderEndNanoTime = System.nanoTime();
        this.width = Minecraft.getMinecraft().displayWidth;
        this.height = Minecraft.getMinecraft().displayHeight;
    }
    
    protected void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(CompatibleRenderTickEvent event) {
    	
    	
    	//if(true) return;
    
        modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_PERSPECTIVE);
        long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
        int origDisplayWidth = Minecraft.getMinecraft().displayWidth;
        int origDisplayHeight = Minecraft.getMinecraft().displayHeight;

        //RenderGlobal origRenderGlobal = Minecraft.getMinecraft().renderGlobal;
        EntityRenderer origEntityRenderer = Minecraft.getMinecraft().entityRenderer;

        framebuffer.bindFramebuffer(true);

        
       
        Minecraft.getMinecraft().displayWidth = width;
        Minecraft.getMinecraft().displayHeight = height;

        Minecraft.getMinecraft().entityRenderer = this.entityRenderer;

        //Minecraft.getMinecraft().renderGlobal = this.renderGlobal;
        //Minecraft.getMinecraft().effectRenderer = this.effectRenderer;

        this.entityRenderer.setPrepareTerrain(false);
        this.entityRenderer.updateRenderer();

        
       
        
        prepareRenderWorld(event);
       
        
       
        this.entityRenderer.renderWorld(event.getRenderTickTime(), p_78471_2_);
     
        if(PostProcessPipeline.shouldDoFog()) {
        	// Blits onto custom scope depth texture
        	// TO-DO: Just use a depth-texture compatible framebuffer w/ the scope. more efficient.
        	PostProcessPipeline.blitScopeDepthTexture(framebuffer);
        }
        
        
//PostProcessPipeline.blitDepth();
		
	//	PostProcessPipeline.setupDistortionBufferEffects();

		//PostProcessPipeline.doWorldProcessing();
		
        
        postRenderWorld(event);
        
        
       
       

       /*
        GlStateManager.pushMatrix();
        EntityPlayer p = Minecraft.getMinecraft().player;
        GlStateManager.translate(-p.posX, -p.posY, -p.posZ);
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
//       / GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
       
        Tessellator t = Tessellator.getInstance();
        BufferBuilder bb = t.getBuffer();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        double nX = 0;
        double nY = 0;
        double nZ = 0;
        
        bb.pos(0+nX, 0+nY, nZ).endVertex();
        bb.pos(0+nX, 10+nY, nZ).endVertex();
        bb.pos(10+nX, 10+nY, nZ).endVertex();
        bb.pos(10+nX, 0+nY, nZ).endVertex();
        t.draw();
        GlStateManager.popMatrix();
        */
        
        //Minecraft.getMinecraft().renderGlobal = origRenderGlobal;
        //Minecraft.getMinecraft().effectRenderer = origEffectRenderer;
        Minecraft.getMinecraft().entityRenderer = origEntityRenderer;

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        Minecraft.getMinecraft().displayWidth = origDisplayWidth;
        Minecraft.getMinecraft().displayHeight = origDisplayHeight;
        this.renderEndNanoTime = System.nanoTime();
        
       
        
        modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);
    }

    protected void prepareRenderWorld(CompatibleRenderTickEvent event) {}

    protected void postRenderWorld(CompatibleRenderTickEvent event) {}

}
