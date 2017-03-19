package com.vicmatskiv.weaponlib.animation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class DebugPositioner {
    
    private static final Logger logger = LogManager.getLogger(DebugPositioner.class);

    private static final String WEAPONLIB_DEBUG_PROPERTY = "weaponlib.debug";
    
    private static float xRotation;
    private static float yRotation;
    private static float zRotation;
    
    public static float x;
    public static float y;
    public static float z;
    
    private static Boolean debugModeEnabled;
    
    public static void incrementXRotation(float increment) {
        xRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", xRotation, yRotation, zRotation);
    }
    
    public static void incrementYRotation(float increment) {
        yRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", xRotation, yRotation, zRotation);
    }
    
    public static void incrementZRotation(float increment) {
        zRotation += increment;
        logger.debug("Debug rotations: ({}, {}, {}) ", xRotation, yRotation, zRotation);
    }
    
    public static void incrementXPosition(float increment) {
        x += increment;
        logger.debug("Debug position: ({}, {}, {}) ", x, y, z);
    }
    
    public static void incrementYPosition(float increment) {
        y += increment;
        logger.debug("Debug position: ({}, {}, {}) ", x, y, z);
    }
    
    public static void incrementZPosition(float increment) {
        z += increment;
        logger.debug("Debug position: ({}, {}, {}) ", x, y, z);
    }
    
    public static boolean isDebugModeEnabled() {
        if(debugModeEnabled == null) {
            debugModeEnabled = Boolean.getBoolean(WEAPONLIB_DEBUG_PROPERTY);
        }
        return debugModeEnabled;
    }

    public static void position() {
        GL11.glRotatef(xRotation, 1f, 0f, 0f);
        GL11.glRotatef(yRotation, 0f, 1f, 0f);
        GL11.glRotatef(zRotation, 0f, 0f, 1f);
        GL11.glTranslatef(x, y, z);
    }

    public static void reset() {
        x = y = z = xRotation = yRotation = zRotation = 0f;
    }
}
