package com.vicmatskiv.weaponlib.animation;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.vicmatskiv.weaponlib.KeyBindings;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;

public class DebugPositioner {

    private static final Logger logger = LogManager.getLogger(DebugPositioner.class);

    private static final String WEAPONLIB_DEBUG_PROPERTY = "weaponlib.debug";

    private static Boolean debugModeEnabled;

    private static Part currentPart;
    
    private static Set<Part> debugParts = new HashSet<>();

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
        private float step = 0.025f;

    }

    private static Map<Part, Position> partPositions = new HashMap<>();

    private static Map<Integer, TransitionConfiguration> transitionConfigurations = new HashMap<>();

    private static Position getCurrentPartPosition() {
        return partPositions.get(currentPart);
    }
    
    private static Position getDebugPartPosition(Part part) {
        return partPositions.get(part);
    }

    public static void incrementXRotation(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }

        partPosition.xRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", partPosition.xRotation, partPosition.yRotation, partPosition.zRotation);
    }

    public static void incrementYRotation(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }
        partPosition.yRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", partPosition.xRotation, partPosition.yRotation, partPosition.zRotation);
    }

    public static void incrementZRotation(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }
        partPosition.zRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", partPosition.xRotation, partPosition.yRotation, partPosition.zRotation);
    }

    public static void incrementXPosition(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }
        partPosition.x += partPosition.step * increment;
        logger.debug("Debug position: ({}, {}, {}) ", partPosition.x, partPosition.y, partPosition.z);
    }

    public static void incrementYPosition(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }
        partPosition.y += partPosition.step * increment;
        logger.debug("Debug position: ({}, {}, {}) ", partPosition.x, partPosition.y, partPosition.z);
    }

    public static void incrementZPosition(float increment) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }
        partPosition.z += partPosition.step * increment;
        logger.debug("Debug position: ({}, {}, {}) ", partPosition.x, partPosition.y, partPosition.z);
    }

    public static void setScale(float scale) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }
        partPosition.scale = scale;
        logger.debug("Scale set to {}", scale);
    }


    public static void setStep(float step) {
        Position partPosition = getCurrentPartPosition();
        if(partPosition == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }
        partPosition.step = step;
        logger.debug("Step set to {}", step);
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

//    public static void reset() {
//        Position partPosition = getCurrentPartPosition();
//        if(partPosition == null) {
//            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
//            return;
//        }
//        transitionConfigurations.clear();
//        partPosition.x = partPosition.y = partPosition.z
//                = partPosition.xRotation = partPosition.yRotation = partPosition.zRotation = 0f;
//        partPosition.scale = 1f;
//        partPosition.step = 0.025f;
//    }
    
    public static void reset() {
        for(Part debugPart: debugParts) {
            Position partPosition = getDebugPartPosition(debugPart);
            if(partPosition == null) {
                compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
                return;
            }
            transitionConfigurations.clear();
            partPosition.x = partPosition.y = partPosition.z
                    = partPosition.xRotation = partPosition.yRotation = partPosition.zRotation = 0f;
            partPosition.scale = 1f;
            partPosition.step = 0.025f;
        }
        
    }

    public static void setDebugPart(Part part) {
        currentPart = part;
        debugParts.add(part);
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

    public static void position(Part part, RenderContext<?> renderContext) {
//        if(part != currentPart) {
//            return;
//        }
        if(!debugParts.contains(part)) {
            return;
        }
        Position partPosition = getDebugPartPosition(part);
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
        System.out.println("\n" + result);
    }

    public static void watch() {
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker(compatibility.clientPlayer());
        System.out.println("Trackable entities: " + tracker.getTrackableEntitites());
    }

    public static Entity getWatchableEntity() {
        return watchableEntity;
    }

    public static void showCurrentMatrix(String message) {
        showCurrentMatrix(null, message);
    }

    public static void showCurrentMatrix(Object part, String message) {
        if(part != null && part != DebugPositioner.currentPart) {
            return;
        }
        Matrix4f preparedPositionMatrix = MatrixHelper.captureMatrix();
        logger.trace("Current matrix: {} {}", message, formatMatrix(preparedPositionMatrix));
    }

    public static String formatMatrix(Matrix4f m) {
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        buf.append(String.format("%4.2f %4.2f %4.2f %4.2f\n", m.m00, m.m10, m.m20, m.m30));
        buf.append(String.format("%4.2f %4.2f %4.2f %4.2f\n", m.m01, m.m11, m.m21, m.m31));
        buf.append(String.format("%4.2f %4.2f %4.2f %4.2f\n", m.m02, m.m12, m.m22, m.m32));
        buf.append(String.format("%4.2f %4.2f %4.2f %4.2f\n", m.m03, m.m13, m.m23, m.m33));
        return buf.toString();
    }

}
