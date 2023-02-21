package com.vicmatskiv.weaponlib.perspective;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;
import com.vicmatskiv.weaponlib.compatibility.Framebuffers;

public abstract class ScreenPerspective extends Perspective<RenderableState> {

    public ScreenPerspective() {
        this.width = 427; //Minecraft.getMinecraft().displayWidth >> 1;
        this.height = 240; //Minecraft.getMinecraft().displayHeight >> 1;
    }

    @Override
    public void update(CompatibleRenderTickEvent event) {
    	//if(true) return;
        int originalFramebufferId = Framebuffers.getCurrentFramebuffer();
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        //compatibility.disableLightMap();
        enable2DRenderingMode(427, 240);
        
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        
        drawScreen();

        restoreRenderingMode();
  
        //compatibility.enableLightMap();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        
        Minecraft mc = Minecraft.getMinecraft();
        Framebuffers.bindFramebuffer(originalFramebufferId, true,
                mc.getFramebuffer().framebufferWidth,
                mc.getFramebuffer().framebufferHeight);
    }

    protected abstract void drawScreen();

    
    private void enable2DRenderingMode(double projectionWidth, double projectionHeight) {
        //GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, projectionWidth, projectionHeight, 0, -1, 1); //0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        //GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    private void restoreRenderingMode() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();   
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();

    }
}
