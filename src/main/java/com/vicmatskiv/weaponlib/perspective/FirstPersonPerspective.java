package com.vicmatskiv.weaponlib.perspective;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;

import net.minecraft.client.Minecraft;

public class FirstPersonPerspective<S> implements Perspective<S> {
    
    protected ClientModContext modContext;
    private long renderEndNanoTime;
    
    public FirstPersonPerspective() {
        this.renderEndNanoTime = System.nanoTime();
    }
    
    @Override
    public void activate(ClientModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public void deactivate(ClientModContext modContext) {
    }
    
    @Override
    public float getBrightness(RenderContext<S> context) {
        return 1f;
    }

    @Override
    public int getTexture(RenderContext<S> context) {
        return modContext.getFramebuffer() != null ? modContext.getFramebuffer().framebufferTexture : -1;
    }

    @Override
    public void update(CompatibleRenderTickEvent event) {
        modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_VIEWFINDER);
        long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
        modContext.getFramebuffer().bindFramebuffer(true);
        modContext.getSecondWorldRenderer().updateRenderer();
        modContext.getSecondWorldRenderer().renderWorld(event.getRenderTickTime(), p_78471_2_);
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        this.renderEndNanoTime = System.nanoTime();
        modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);
    }
}
