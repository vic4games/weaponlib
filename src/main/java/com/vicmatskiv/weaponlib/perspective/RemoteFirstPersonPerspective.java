package com.vicmatskiv.weaponlib.perspective;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerCreatureWrapper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLivingBase;

public abstract class RemoteFirstPersonPerspective extends Perspective<RenderableState> {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(RemoteFirstPersonPerspective.class);

    private long renderEndNanoTime;
    private RenderGlobal renderGlobal;
    private EffectRenderer effectRenderer;
    
    protected CompatiblePlayerCreatureWrapper watchablePlayer;

    public RemoteFirstPersonPerspective() {
        this.renderEndNanoTime = System.nanoTime();
        this.width = Minecraft.getMinecraft().displayWidth >> 1;
        this.height = Minecraft.getMinecraft().displayHeight >> 1;
        WorldClient world = (WorldClient) compatibility.world(compatibility.clientPlayer());
        this.watchablePlayer = new CompatiblePlayerCreatureWrapper(Minecraft.getMinecraft(), world);
        this.renderGlobal = new RenderGlobal(Minecraft.getMinecraft());
        this.effectRenderer = new EffectRenderer(world, Minecraft.getMinecraft().getTextureManager());
        this.renderGlobal.setWorldAndLoadRenderers(world);
    }

    @Override
    public void update(CompatibleRenderTickEvent event) {

        EntityClientPlayerMP origPlayer = (EntityClientPlayerMP) compatibility.clientPlayer();

        if(origPlayer == null) {
            return;
        }
        
        updateWatchablePlayer();

        RenderGlobal origRenderGlobal = Minecraft.getMinecraft().renderGlobal;
        EffectRenderer origEffectRenderer = Minecraft.getMinecraft().effectRenderer;
        EntityLivingBase origRenderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        int origDisplayWidth = Minecraft.getMinecraft().displayWidth;
        int origDisplayHeight = Minecraft.getMinecraft().displayHeight;
        
        Minecraft.getMinecraft().displayWidth = this.width;
        Minecraft.getMinecraft().displayHeight = this.height;
        
        framebuffer.bindFramebuffer(true);
        
        Minecraft.getMinecraft().renderGlobal = this.renderGlobal;
        Minecraft.getMinecraft().effectRenderer = this.effectRenderer;

        if (watchablePlayer.getEntityLiving() != null) {
            
            //watchablePlayer.setEntityLiving((EntityLivingBase) watchableEntity);
            Minecraft.getMinecraft().renderViewEntity = watchablePlayer.getEntityLiving();
            compatibility.setClientPlayer(watchablePlayer);
            
            modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_PERSPECTIVE);
            long p_78471_2_ = this.renderEndNanoTime + (long) (1000000000 / 60);
            this.entityRenderer.updateRenderer();
            this.entityRenderer.renderWorld(event.getRenderTickTime(), p_78471_2_);
            
            Minecraft.getMinecraft().renderViewEntity = origRenderViewEntity;
            compatibility.setClientPlayer(origPlayer);
        }

        renderOverlay();

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);
       
        Minecraft.getMinecraft().renderGlobal = origRenderGlobal;
        Minecraft.getMinecraft().effectRenderer = origEffectRenderer;
        
        Minecraft.getMinecraft().displayWidth = origDisplayWidth;
        Minecraft.getMinecraft().displayHeight = origDisplayHeight;

        this.renderEndNanoTime = System.nanoTime();
    }
    
    protected abstract void updateWatchablePlayer();
    
    protected void renderOverlay() {
        this.entityRenderer.setupOverlayRendering();
    }
}
