package com.vicmatskiv.weaponlib.perspective;

import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;

import net.minecraft.client.Minecraft;

public class FirstPersonPerspective<S> extends Perspective<S> {
    
    private long renderEndNanoTime;
    
    public FirstPersonPerspective() {
        this.renderEndNanoTime = System.nanoTime();
        this.width = Minecraft.getMinecraft().displayWidth;
        this.height = Minecraft.getMinecraft().displayHeight;
    }
    
    @Override
    public void update(CompatibleRenderTickEvent event) {
        modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_PERSPECTIVE);
        long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
        int origDisplayWidth = Minecraft.getMinecraft().displayWidth;
        int origDisplayHeight = Minecraft.getMinecraft().displayHeight;
        framebuffer.bindFramebuffer(true);
        Minecraft.getMinecraft().displayWidth = width;
        Minecraft.getMinecraft().displayHeight = height;
        this.entityRenderer.updateRenderer();
        this.entityRenderer.renderWorld(event.getRenderTickTime(), p_78471_2_);
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        Minecraft.getMinecraft().displayWidth = origDisplayWidth;
        Minecraft.getMinecraft().displayHeight = origDisplayHeight;
        this.renderEndNanoTime = System.nanoTime();
        modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);
    }
}
