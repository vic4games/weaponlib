package com.vicmatskiv.weaponlib.perspective;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTessellator;
import com.vicmatskiv.weaponlib.electronics.PlayerTabletInstance;
import com.vicmatskiv.weaponlib.electronics.SignalQuality;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;
import com.vicmatskiv.weaponlib.tracking.TrackableEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class WirelessCameraPerspective extends RemoteFirstPersonPerspective {
    
    private static final Logger logger = LogManager.getLogger(WirelessCameraPerspective.class);
    
    private static final String STATIC_TEXTURE = "weaponlib:/com/vicmatskiv/weaponlib/resources/static.png";

    private static final int STATIC_IMAGES_PER_ROW = 8;

    private int tickCounter;
    private int activeWatchIndex;
    private int badSignalTickCounter;
    private int imageIndex;
    
    private Random random = new Random();

    private int totalTrackableEntities;

    private String displayName;

    @Override
    protected void updateWatchablePlayer() {
        
        EntityPlayer entityPlayer = compatibility.clientPlayer();
        PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry()
                .getMainHandItemInstance(entityPlayer);

        if(!(instance instanceof PlayerTabletInstance)) {
            return;
        }

        PlayerTabletInstance tabletInstance = (PlayerTabletInstance) instance;

        PlayerEntityTracker playerEntityTracker = PlayerEntityTracker.getTracker(entityPlayer);

        if(playerEntityTracker == null) {
            return;
        }

        activeWatchIndex = tabletInstance.getActiveWatchIndex();
        totalTrackableEntities = playerEntityTracker.getTrackableEntitites().size();
        TrackableEntity te = playerEntityTracker.getTrackableEntity(activeWatchIndex);

        if(te == null) {
            displayName = "";
            return;
        } else {
            displayName = te.getDisplayName();
        }

        Entity watchableEntity = te.getEntity();

        Entity realEntity = watchableEntity == null ? null : compatibility.world(watchableEntity)
                .getEntityByID(watchableEntity.getEntityId());
        if (realEntity != null && realEntity != watchableEntity) {
            watchableEntity = (EntityLivingBase) realEntity;
        }
        
        if(watchableEntity != null && tickCounter++ %50 == 0) {
            logger.debug("Watching {}, distance: {}  ", 
                    watchableEntity, 
                    Math.sqrt(Math.pow(watchableEntity.posX - Minecraft.getMinecraft().thePlayer.posX, 2)
                            + Math.pow(watchableEntity.posZ - Minecraft.getMinecraft().thePlayer.posZ, 2))
                    );
        }
        
        if(watchableEntity == null || watchableEntity instanceof EntityLivingBase) {
            this.watchablePlayer.setEntityLiving((EntityLivingBase)watchableEntity);
        }
    }
    
    @Override
    protected void renderOverlay() {
        super.renderOverlay();
        
        int maxDistance = 120;
        int displayCameraIndex = activeWatchIndex + 1;
        String message = "Cam " + displayCameraIndex + ": " + displayName;
        EntityLivingBase watchableEntity = watchablePlayer.getEntityLiving();
        if(watchableEntity != null) {
            EntityPlayer origPlayer = compatibility.clientPlayer();
            //origPlayer.getDistanceToEntity(watchableEntity);
            double distance = Math.pow(watchableEntity.posX - origPlayer.posX, 2)
                    + Math.pow(watchableEntity.posY - origPlayer.posY, 2)
                    + Math.pow(watchableEntity.posZ - origPlayer.posZ, 2);
            SignalQuality quality = SignalQuality.getQuality((int)Math.sqrt(distance), maxDistance);
            if(quality.isInterrupted() || (badSignalTickCounter > 0 && badSignalTickCounter < 5)) {
                framebuffer.framebufferClear();
                framebuffer.bindFramebuffer(true);
                message = "Cam " + displayCameraIndex + ": no signal";
                drawStatic();
                badSignalTickCounter++;
            }
            if(badSignalTickCounter == 5) {
                badSignalTickCounter = 0;
            }
        } else if(totalTrackableEntities == 0) {
            message = "Disconnected";
        } else {
            message = "Cam " + displayCameraIndex + ": " + displayName;
            drawStatic();
        }
        
        FontRenderer fontRender = compatibility.getFontRenderer();
        int color =  0xFFFF00;

        GL11.glScalef(3f, 3f, 3f);
        
        fontRender.drawString(message, 40, 60, color, false);
    }
    
    public void drawStatic() {

        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(STATIC_TEXTURE));
        
        imageIndex = random.nextInt(STATIC_IMAGES_PER_ROW);

        /*
         *  (cU, cV)   (bU, bV)
         * 
         *  (dU, dV)   (aU, aV)
         * 
         */
        float uWidth = 1f / STATIC_IMAGES_PER_ROW;
        
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
        double width = this.width;
        double height = this.height;
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
