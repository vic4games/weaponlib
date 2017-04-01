package com.vicmatskiv.weaponlib.perspective;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleParticleManager;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWorldRenderer;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.shader.Framebuffer;

public abstract class Perspective<S> {
    
    protected ClientModContext modContext;
    protected Framebuffer framebuffer;
    
    protected int width;
    protected int height;
    
    protected CompatibleWorldRenderer entityRenderer;
    protected RenderGlobal renderGlobal;
    protected CompatibleParticleManager effectRenderer;

    public void activate(ClientModContext modContext, PerspectiveManager manager) {
        this.modContext = modContext;
        if(framebuffer == null) {
            framebuffer = new Framebuffer(width, height, true);
            framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        }
        this.entityRenderer = manager.getEntityRenderer();
        this.effectRenderer = manager.getEffectRenderer();
        this.renderGlobal = manager.getRenderGlobal();
    }

    public void deactivate(ClientModContext modContext) {
        framebuffer.framebufferClear();
    }
    
    public float getBrightness(RenderContext<S> context) {
        return 1f;
    }

    public int getTexture(RenderContext<S> context) {
        return framebuffer != null ? framebuffer.framebufferTexture : -1;
    }
    
    public Framebuffer getFramebuffer() {
        return framebuffer;
    }
    
    public abstract void update(CompatibleRenderTickEvent event);
    
}
