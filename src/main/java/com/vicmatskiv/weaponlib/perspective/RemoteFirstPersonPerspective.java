package com.vicmatskiv.weaponlib.perspective;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.ExtendedPlayerProperties;
import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.TrackableEntity;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerCreatureWrapper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTessellator;
import com.vicmatskiv.weaponlib.electronics.PlayerTabletInstance;
import com.vicmatskiv.weaponlib.electronics.SignalQuality;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

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

        EntityClientPlayerMP origPlayer = (EntityClientPlayerMP) compatibility.clientPlayer();

        if(origPlayer == null) {
            return;
        }
        PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(origPlayer);

        if(!(instance instanceof PlayerTabletInstance)) {
            return;
        }

        PlayerTabletInstance tabletInstance = (PlayerTabletInstance) instance;

        ExtendedPlayerProperties extendedProperties = ExtendedPlayerProperties.getProperties(origPlayer);

        if(extendedProperties == null) {
            return;
        }

        int activeWatchIndex = tabletInstance.getActiveWatchIndex();
        TrackableEntity te = extendedProperties.getTrackableEntity(activeWatchIndex);

        if(te == null) {
            return;
        }

        Entity watchableEntity = te.getEntity();

        //        if(watchableEntity == null) {
        //            return;
        //        }

        if(watchableEntity != null && tickCounter++ %50 == 0) {
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
        Entity realEntity = watchableEntity == null ? null : renderInfo.world.getEntityByID(watchableEntity.getEntityId());
        if (realEntity != null && realEntity != watchableEntity) {
            watchableEntity = (EntityLivingBase) realEntity;
        }

        RenderGlobal origRenderGlobal = Minecraft.getMinecraft().renderGlobal;
        EffectRenderer origEffectRenderer = Minecraft.getMinecraft().effectRenderer;
        EntityLivingBase origRenderViewEntity = Minecraft.getMinecraft().renderViewEntity;

        if (watchableEntity != null) {
            renderInfo.watchablePlayer.setEntityLiving((EntityLivingBase) watchableEntity);
            Minecraft.getMinecraft().renderGlobal = renderInfo.renderGlobal;
            Minecraft.getMinecraft().effectRenderer = renderInfo.effectRenderer;
            Minecraft.getMinecraft().renderViewEntity = (EntityLivingBase) watchableEntity;
            Minecraft.getMinecraft().thePlayer = renderInfo.watchablePlayer;
            modContext.getFramebuffer().bindFramebuffer(true);
            modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_PERSPECTIVE);
            long p_78471_2_ = this.renderEndNanoTime + (long) (1000000000 / 60);
            modContext.getSecondWorldRenderer().updateRenderer();
            modContext.getSecondWorldRenderer().renderWorld(event.getRenderTickTime(), p_78471_2_);
        }

        renderOverlay(watchableEntity, origPlayer, activeWatchIndex);

        if (watchableEntity != null) {
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
    
    private int badSignalTickCounter = 0;

    private void renderOverlay(Entity watchableEntity, EntityClientPlayerMP origPlayer, int activeWatchIndex) {
        int maxDistance = 120;
        
        modContext.getSecondWorldRenderer().setupOverlayRendering();
        
        String message = "Comm " + activeWatchIndex;
        if(watchableEntity != null) {
            double distance = Math.pow(watchableEntity.posX - origPlayer.posX, 2)
                    + Math.pow(watchableEntity.posY - origPlayer.posY, 2)
                    + Math.pow(watchableEntity.posZ - origPlayer.posZ, 2);
            SignalQuality quality = SignalQuality.getQuality((int)Math.sqrt(distance), maxDistance);
            if(quality.isInterrupted() || (badSignalTickCounter > 0 && badSignalTickCounter < 5)) {
                modContext.getFramebuffer().framebufferClear();
                modContext.getFramebuffer().bindFramebuffer(true);
                message = "Comm " + activeWatchIndex + ": no signal";
                drawStatic();
                badSignalTickCounter++;
            }
            if(badSignalTickCounter == 5) {
                badSignalTickCounter = 0;
            }
        }
        
        FontRenderer fontRender = compatibility.getFontRenderer();
        int color =  0xFFFF00;
        //fontRender.drawStringWithShadow("Comm " + activeWatchIndex, 50, 60, color);

        
        GL11.glScalef(3f, 3f, 3f);
        
        fontRender.drawString(message, 40, 60, color, false);
        
        
    }
    
    
    private static final String SMOKE_TEXTURE = "weaponlib:/com/vicmatskiv/weaponlib/resources/static.png";
    
    private int imageIndex;
    
    private static final int imagesPerRow = 4;
    
    Random random = new Random();
    
    public void drawStatic() {

        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(SMOKE_TEXTURE));
        
        imageIndex = random.nextInt(4);

        /*
         *  (cU, cV)   (bU, bV)
         * 
         *  (dU, dV)   (aU, aV)
         * 
         */
        float uWidth = 1f / imagesPerRow;
        
        float aU = (imageIndex + 1) * uWidth; // imageIndex = 0, imagesPerRow = 2, aU = 0.5; imageIndex = 1, aU = 1
            // imagesPerRow = 4; imageIndex = 1; aU = 2/4 = 0.5
        float aV = 1f;
        
        float bU = (imageIndex + 1) * uWidth;
        float bV = 0f;
        
        float cU = imageIndex * uWidth; // imageIndex = 0, imagesPerRow = 2, cU = 0; imageIndex = 1, cU = 0.5
        float cV = 0f;
        
        float dU = imageIndex * uWidth;
        float dV = 1f;
        
        double x = 0;
        double y = 0;
        double width = 500;
        double height = 500;
        double zLevel = 0;

        CompatibleTessellator tessellator = CompatibleTessellator.getInstance();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, aU, aV);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, bU, bV);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, cU, cV);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, dU, dV);
        tessellator.draw();
    }
}
