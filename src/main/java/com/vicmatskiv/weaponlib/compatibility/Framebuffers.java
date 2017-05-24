package com.vicmatskiv.weaponlib.compatibility;

import org.lwjgl.opengl.ARBFramebufferObject;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public class Framebuffers {

    public static int getCurrentFramebuffer() {
        return GlStateManager.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);
    }

    public static void unbindFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
        }
    }

    public static void bindFramebuffer(int framebufferId, boolean depthEnabled, int width, int height) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebufferId);
            if(depthEnabled) {
                GlStateManager.viewport(0, 0, width,height);
            }
        }
    }
}
