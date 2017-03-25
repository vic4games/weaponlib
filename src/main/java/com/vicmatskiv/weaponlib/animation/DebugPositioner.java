package com.vicmatskiv.weaponlib.animation;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ExtendedPlayerProperties;
import com.vicmatskiv.weaponlib.KeyBindings;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.TrackableEntity;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerCreatureWrapper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult.Type;
import com.vicmatskiv.weaponlib.melee.RenderableState;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;

public class DebugPositioner {
    
    private static final Logger logger = LogManager.getLogger(DebugPositioner.class);

    private static final String WEAPONLIB_DEBUG_PROPERTY = "weaponlib.debug";
    
    private static Boolean debugModeEnabled;
    
    private static Part currentPart;
    
    private static Entity watchableEntity;
    
    public static final class TransitionConfiguration {
        private long pause;

        public long getPause() {
            return pause;
        }

        public void setPause(long pause) {
            this.pause = pause;
        }
    }
    
    private static class Position {
        private float xRotation;
        private float yRotation;
        private float zRotation;
        
        private float x;
        private float y;
        private float z;
        
        private float scale = 1f;
    }
    
    private static Map<Part, Position> partPositions = new HashMap<>();
    
    private static Map<Integer, TransitionConfiguration> transitionConfigurations = new HashMap<>();
    
    private static Position getCurrentPartPosition() {
        return partPositions.get(currentPart);
    }
    
    public static void incrementXRotation(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        
        partPosition.xRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", partPosition.xRotation, partPosition.yRotation, partPosition.zRotation);
    }
    
    public static void incrementYRotation(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        partPosition.yRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", partPosition.xRotation, partPosition.yRotation, partPosition.zRotation);
    }
    
    public static void incrementZRotation(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        partPosition.zRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", partPosition.xRotation, partPosition.yRotation, partPosition.zRotation);
    }
    
    public static void incrementXPosition(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        partPosition.x += increment;
        logger.debug("Debug position: ({}, {}, {}) ", partPosition.x, partPosition.y, partPosition.z);
    }
    
    public static void incrementYPosition(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        partPosition.y += increment;
        logger.debug("Debug position: ({}, {}, {}) ", partPosition.x, partPosition.y, partPosition.z);
    }
    
    public static void incrementZPosition(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        partPosition.z += increment;
        logger.debug("Debug position: ({}, {}, {}) ", partPosition.x, partPosition.y, partPosition.z);
    }
    
    public static void setScale(float scale) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        partPosition.scale = scale;
        logger.debug("Scale set to {}", scale);
    }
    
    public static void setDebugMode(boolean enabled) {
        debugModeEnabled = enabled;
        if(debugModeEnabled) {
            KeyBindings.bindDebugKeys();
        }
    }
    
    public static boolean isDebugModeEnabled() {
        if(debugModeEnabled == null) {
            debugModeEnabled = Boolean.getBoolean(WEAPONLIB_DEBUG_PROPERTY);
        }
        return debugModeEnabled;
    }

    public static void reset() {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.clientPlayer().addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        partPosition.x = partPosition.y = partPosition.z 
                = partPosition.xRotation = partPosition.yRotation = partPosition.zRotation = 0f;
        partPosition.scale = 1f;
    }
    
    public static void setDebugPart(Part part) {
        currentPart = part;
        partPositions.computeIfAbsent(part, p -> new Position());
    }
    
    public static Part getDebugPart() {
        return currentPart;
    }

    public static void configureTransitionPause(int transitionNumber, long pause) {
        TransitionConfiguration transitionConfiguration = getTransitionConfiguration(transitionNumber, true);
        transitionConfiguration.pause = pause;
    }
    
    public static TransitionConfiguration getTransitionConfiguration(int transitionNumber, boolean init) {
        return transitionConfigurations.computeIfAbsent(transitionNumber, k -> init ? new TransitionConfiguration() : null);
    }

    public static void position(Part part, RenderContext<RenderableState> renderContext) {
        if(part != currentPart) {
            return;
        }
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            return;
        }
        GL11.glScalef(partPosition.scale, partPosition.scale, partPosition.scale);
        GL11.glRotatef(partPosition.xRotation, 1f, 0f, 0f);
        GL11.glRotatef(partPosition.yRotation, 0f, 1f, 0f);
        GL11.glRotatef(partPosition.zRotation, 0f, 0f, 1f);
        GL11.glTranslatef(partPosition.x, partPosition.y, partPosition.z);
    }

    public static void showCode() {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            return;
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format("GL11.glScalef(%ff, %ff, %ff);\n", partPosition.scale, partPosition.scale, partPosition.scale));
        result.append(String.format("GL11.glRotatef(%ff, 1f, 0f, 0f);\n", partPosition.xRotation));
        result.append(String.format("GL11.glRotatef(%ff, 0f, 1f, 0f);\n", partPosition.yRotation));
        result.append(String.format("GL11.glRotatef(%ff, 0f, 0f, 1f);\n", partPosition.zRotation));
        result.append(String.format("GL11.glTranslatef(%ff, %ff, %ff);", partPosition.x, partPosition.y, partPosition.z));
        logger.debug("Generated positioning code: \n" + result);
    }
    
    public static void watch() {
        CompatibleRayTraceResult objectMouseOver = compatibility.getObjectMouseOver();
        if (objectMouseOver != null && objectMouseOver.getTypeOfHit() == Type.ENTITY
                && objectMouseOver.getEntityHit() instanceof EntityLivingBase) {
            //watchableEntity = objectMouseOver.getEntityHit();
            watchableEntity = (EntityLivingBase) objectMouseOver.getEntityHit();
            logger.debug("Now watching", watchableEntity);
            ExtendedPlayerProperties properties = ExtendedPlayerProperties.getProperties(compatibility.clientPlayer());
            properties.addTrackableEntity(new TrackableEntity(watchableEntity, System.currentTimeMillis()));
        } else {
            ExtendedPlayerProperties properties = ExtendedPlayerProperties.getProperties(compatibility.clientPlayer());
            System.out.println("Trackable entities: " + properties.getTrackableEntitites());
        }
    }
    
    public static Entity getWatchableEntity() {
        return watchableEntity;
    }
}
