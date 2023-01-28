package com.vicmatskiv.weaponlib.perspective;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.compatibility.CompatibleParticleManager;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerCreatureWrapper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public abstract class RemoteFirstPersonPerspective extends Perspective<RenderableState> {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(RemoteFirstPersonPerspective.class);

    private long renderEndNanoTime;

    protected CompatiblePlayerCreatureWrapper watchablePlayer;

    public RemoteFirstPersonPerspective() {
        this.renderEndNanoTime = System.nanoTime();
        this.width = 427; //Minecraft.getMinecraft().displayWidth >> 1;
        this.height = 240; //Minecraft.getMinecraft().displayHeight >> 1;
        WorldClient world = (WorldClient) compatibility.world(compatibility.clientPlayer());
        this.watchablePlayer = new CompatiblePlayerCreatureWrapper(Minecraft.getMinecraft(), world);
    }

    @Override
    public void update(CompatibleRenderTickEvent event) {
        
        if(Minecraft.getMinecraft().isGamePaused()) {
            return;
        }

        EntityPlayer origPlayer = compatibility.clientPlayer();

        if(origPlayer == null) {
            return;
        }

        updateWatchablePlayer();

        RenderGlobal origRenderGlobal = Minecraft.getMinecraft().renderGlobal;
        CompatibleParticleManager origEffectRenderer = compatibility.getCompatibleParticleManager();
        Entity origRenderViewEntity = compatibility.getRenderViewEntity();
        EntityRenderer origEntityRenderer = Minecraft.getMinecraft().entityRenderer;
        int origDisplayWidth = Minecraft.getMinecraft().displayWidth;
        int origDisplayHeight = Minecraft.getMinecraft().displayHeight;

        Minecraft.getMinecraft().displayWidth = this.width;
        Minecraft.getMinecraft().displayHeight = this.height;

        framebuffer.bindFramebuffer(true);

        Minecraft.getMinecraft().renderGlobal = this.renderGlobal;
        Minecraft.getMinecraft().effectRenderer = this.effectRenderer.getParticleManager();

        Minecraft.getMinecraft().entityRenderer = this.entityRenderer;
        if (watchablePlayer.getEntityLiving() != null && !watchablePlayer.getEntityLiving().isDead) {

            compatibility.setRenderViewEntity(watchablePlayer.getEntityLiving());
            compatibility.setClientPlayer(watchablePlayer);

            modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_PERSPECTIVE);

            this.entityRenderer.setPrepareTerrain(true);
            this.entityRenderer.updateRenderer();
            long p_78471_2_ = this.renderEndNanoTime + (long) (1000000000 / 60);
            this.entityRenderer.renderWorld(event.getRenderTickTime(), p_78471_2_);

            modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);

            compatibility.setRenderViewEntity(origRenderViewEntity);
            compatibility.setClientPlayer(origPlayer);
        }

        renderOverlay();

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);

        Minecraft.getMinecraft().renderGlobal = origRenderGlobal;
        Minecraft.getMinecraft().effectRenderer = origEffectRenderer.getParticleManager();

        Minecraft.getMinecraft().displayWidth = origDisplayWidth;
        Minecraft.getMinecraft().displayHeight = origDisplayHeight;
        Minecraft.getMinecraft().entityRenderer = origEntityRenderer;

        this.renderEndNanoTime = System.nanoTime();
    }

    protected abstract void updateWatchablePlayer();

    protected void renderOverlay() {
        this.entityRenderer.setupOverlayRendering();
    }
}
