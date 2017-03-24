package com.vicmatskiv.weaponlib.perspective;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerCreatureWrapper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class RemoteFirstPersonPerspective implements Perspective<RenderableState> {
    
    private static final Logger logger = LogManager.getLogger(RemoteFirstPersonPerspective.class);

    private static class RenderInfo {
        private RenderGlobal renderGlobal;
        private EffectRenderer effectRenderer;
        private CompatiblePlayerCreatureWrapper watchablePlayer;
        private WorldClient world;

        public RenderInfo(WorldClient world) {
            this.world = world;
            this.renderGlobal = new RenderGlobal(Minecraft.getMinecraft());
            this.effectRenderer = new EffectRenderer(world, Minecraft.getMinecraft().getTextureManager());
            this.watchablePlayer = new CompatiblePlayerCreatureWrapper(Minecraft.getMinecraft(), world);
            this.renderGlobal.setWorldAndLoadRenderers(world);
        }
    }

    private ClientModContext modContext;
    private long renderEndNanoTime;
    
    private RenderInfo renderInfo;
    private int tickCounter;
    
    public RemoteFirstPersonPerspective() {
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
    public float getBrightness(RenderContext<RenderableState> renderContext) {
        return 1f;
    }

    @Override
    public int getTexture(RenderContext<RenderableState> context) {
        return modContext.getFramebuffer() != null ? modContext.getFramebuffer().framebufferTexture : -1;
    }

    @Override
    public void update(CompatibleRenderTickEvent event) {
        
        Entity watchableEntity = DebugPositioner.getWatchableEntity();
        if(watchableEntity == null) {
            return;
        }
        
        if(tickCounter++ %20 == 0) {
            logger.debug("Watching {}, distance: {}  ", 
                    watchableEntity, 
                    Math.sqrt(Math.pow(watchableEntity.posX - Minecraft.getMinecraft().thePlayer.posX, 2)
                            + Math.pow(watchableEntity.posZ - Minecraft.getMinecraft().thePlayer.posZ, 2))
                    );
        }
        
//        WorldClient origWorld = Minecraft.getMinecraft().theWorld;
        if (renderInfo == null) {
//            WorldClient world = Minecraft.getMinecraft().theWorld;// new CompatibleWorldWrapper(Minecraft.getMinecraft().theWorld.provider.dimensionId);
            renderInfo = new RenderInfo(Minecraft.getMinecraft().theWorld);
        }
        Entity realEntity = renderInfo.world.getEntityByID(watchableEntity.getEntityId());
        if (realEntity != null && realEntity != watchableEntity) {
            watchableEntity = (EntityLivingBase) realEntity;
        }

        if (watchableEntity != null) {
            EntityLivingBase origRenderViewEntity = Minecraft.getMinecraft().renderViewEntity;
            EntityClientPlayerMP origPlayer = Minecraft.getMinecraft().thePlayer;

            RenderGlobal origRenderGlobal = Minecraft.getMinecraft().renderGlobal;
            EffectRenderer origEffectRenderer = Minecraft.getMinecraft().effectRenderer;
            renderInfo.watchablePlayer.setEntityLiving((EntityLivingBase) watchableEntity);
            Minecraft.getMinecraft().renderGlobal = renderInfo.renderGlobal;
            Minecraft.getMinecraft().effectRenderer = renderInfo.effectRenderer;
            Minecraft.getMinecraft().renderViewEntity = (EntityLivingBase) watchableEntity;
            Minecraft.getMinecraft().thePlayer = renderInfo.watchablePlayer;
            //Minecraft.getMinecraft().theWorld = renderInfo.world;
            //RenderManager.instance.set(renderInfo.world);

            modContext.getFramebuffer().bindFramebuffer(true);
            modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_VIEWFINDER);
            long p_78471_2_ = this.renderEndNanoTime + (long) (1000000000 / 60);
            modContext.getSecondWorldRenderer().updateRenderer();
            modContext.getSecondWorldRenderer().renderWorld(event.getRenderTickTime(), p_78471_2_);
            modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);

            Minecraft.getMinecraft().renderViewEntity = origRenderViewEntity;
            Minecraft.getMinecraft().thePlayer = origPlayer;
            //Minecraft.getMinecraft().theWorld = origWorld;
            Minecraft.getMinecraft().renderGlobal = origRenderGlobal;
            Minecraft.getMinecraft().effectRenderer = origEffectRenderer;
            //RenderManager.instance.set(origWorld);
        }

        this.renderEndNanoTime = System.nanoTime();
    }
}
