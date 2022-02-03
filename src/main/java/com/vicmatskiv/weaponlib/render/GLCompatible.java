package com.vicmatskiv.weaponlib.render;

import org.lwjgl.opengl.APPLEFloatPixels;
import org.lwjgl.opengl.ARBColorBufferFloat;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.ARBTextureMultisample;
import org.lwjgl.opengl.ATITextureFloat;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferMultisample;
import org.lwjgl.opengl.EXTFramebufferMultisampleBlitScaled;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferSRGB;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.NVTextureMultisample;

public class GLCompatible {

	public static final int NORMAL = 0;
	public static final int ARB = 1;
	public static final int EXT = 2;

	public static int fboType = -1;
	public static int msaaType = -1;
	public static int multisampleType = -1;

	public static int GL_READ_FRAMEBUFFER;
	public static int GL_DRAW_FRAMEBUFFER;
	public static int GL_FRAMEBUFFER;
	public static int GL_COLOR_ATTACHMENT0;
	public static int GL_DEPTH_ATTACHMENT;
	public static int GL_RENDERBUFFER;
	public static int GL_RGBA16F;

	public static int GL_TEXTURE_2D_MULTISAMPLE;

	public static boolean isLoaded = false;

	public static void init() {
		if(isLoaded) return;
		isLoaded = true;
		
		
		ContextCapabilities cap = GLContext.getCapabilities();

		if (cap.OpenGL30) {
			fboType = 0;
			GL_READ_FRAMEBUFFER = GL30.GL_READ_FRAMEBUFFER;
			GL_DRAW_FRAMEBUFFER = GL30.GL_DRAW_FRAMEBUFFER;
			GL_FRAMEBUFFER = GL30.GL_FRAMEBUFFER;
			GL_RENDERBUFFER = GL30.GL_RENDERBUFFER;
			GL_DEPTH_ATTACHMENT = GL30.GL_DEPTH_ATTACHMENT;
			GL_COLOR_ATTACHMENT0 = GL30.GL_COLOR_ATTACHMENT0;
		} else if (cap.GL_ARB_framebuffer_object) {
			fboType = 1;
			GL_READ_FRAMEBUFFER = ARBFramebufferObject.GL_READ_FRAMEBUFFER;
			GL_DRAW_FRAMEBUFFER = ARBFramebufferObject.GL_DRAW_FRAMEBUFFER;
			GL_FRAMEBUFFER = ARBFramebufferObject.GL_FRAMEBUFFER;
			GL_RENDERBUFFER = ARBFramebufferObject.GL_RENDERBUFFER;
			GL_COLOR_ATTACHMENT0 = ARBFramebufferObject.GL_COLOR_ATTACHMENT0;
			GL_DEPTH_ATTACHMENT = ARBFramebufferObject.GL_DEPTH_ATTACHMENT;
		} else if (cap.GL_EXT_framebuffer_object && cap.GL_EXT_framebuffer_blit) {
			GL_READ_FRAMEBUFFER = EXTFramebufferBlit.GL_READ_FRAMEBUFFER_EXT;
			GL_DRAW_FRAMEBUFFER = EXTFramebufferBlit.GL_DRAW_FRAMEBUFFER_EXT;
			GL_FRAMEBUFFER = EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
			GL_COLOR_ATTACHMENT0 = EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
			GL_DEPTH_ATTACHMENT = EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
			GL_RENDERBUFFER = EXTFramebufferObject.GL_RENDERBUFFER_EXT;
			fboType = 2;
		} else
			System.out.println("Framebuffer objects not supported");

		if (cap.OpenGL30) {
			GL_RGBA16F = GL30.GL_RGBA16F;
		} else if (cap.GL_APPLE_float_pixels) {
			GL_RGBA16F = APPLEFloatPixels.GL_RGBA_FLOAT16_APPLE;
		} else if (cap.GL_ARB_texture_float) {
			GL_RGBA16F = ARBTextureFloat.GL_RGB16F_ARB;
		} else if (cap.GL_ATI_texture_float) {
			GL_RGBA16F = ATITextureFloat.GL_RGBA_FLOAT16_ATI;
		} else {
			System.out.println("Floating point texture component not supported");
		}

		if (cap.OpenGL32) {
			multisampleType = 0;
			GL_TEXTURE_2D_MULTISAMPLE = GL32.GL_TEXTURE_2D_MULTISAMPLE;
		} else if (cap.GL_ARB_texture_multisample) {
			multisampleType = 1;
			GL_TEXTURE_2D_MULTISAMPLE = ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE;
		}

	}

	public static void glTexImage2DMultisample(int target, int samples, int internalformat, int width, int height,
			boolean fixedsamplelocations) {

		init();
		
		switch (multisampleType) {
		case NORMAL:
			GL32.glTexImage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
			break;
		case ARB:
			ARBTextureMultisample.glTexImage2DMultisample(target, samples, internalformat, width, height,
					fixedsamplelocations);
			break;
		}

	}

	public static int glGenFramebuffers() {
		init();
		switch (fboType) {
		case NORMAL:
			return GL30.glGenFramebuffers();
		case ARB:

			return ARBFramebufferObject.glGenFramebuffers();

		case EXT:
			return EXTFramebufferObject.glGenFramebuffersEXT();

		}
		return -1;
	}

	public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		init();
		switch (fboType) {
		case NORMAL:
			GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
			break;
		case ARB:
			ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textarget, texture, level);
			break;
		case EXT:
			EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
			break;
		}
	}

	public static void glBindFramebuffer(int target, int framebuffer) {
		init();
		switch (fboType) {
		case NORMAL:
			GL30.glBindFramebuffer(target, framebuffer);
			break;
		case ARB:
			ARBFramebufferObject.glBindFramebuffer(target, framebuffer);
			break;
		case EXT:
			EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer);
			break;
		}
	}

	public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1,
			int dstY1, int mask, int filter) {
		init();
		switch (fboType) {
		case NORMAL:
			GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
			break;
		case ARB:
			ARBFramebufferObject.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask,
					filter);
			break;
		case EXT:
			EXTFramebufferBlit.glBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask,
					filter);
			break;
		}
	}

}
