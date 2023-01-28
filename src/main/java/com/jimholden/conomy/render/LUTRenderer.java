package com.jimholden.conomy.render;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

import com.jimholden.conomy.drugs.Drug;
import com.jimholden.conomy.drugs.DrugCache;
import com.jimholden.conomy.main.ModEventClientHandler;
import com.jimholden.conomy.shaders.Shader;
import com.jimholden.conomy.shaders.ShaderManager;
import com.jimholden.conomy.util.Reference;

import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.math.Vec3d;

public class LUTRenderer {
	
	public static Framebuffer prevFBO = null;
	public static Shader shader = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/lut3d"));
	public static final ResourceLocation BASICLUT = new ResourceLocation(Reference.MOD_ID, "textures/lut/contlut.png");
	
	public static BufferedInputStream reader;
	public static ByteBuffer buffer;
	static int tex = GL11.glGenTextures();
    public static int height = 0;
    public static int width = 0;
    public static Framebuffer buf;
    public static int[] bufferboi;
    public static IntBuffer intBuffer;
    public static int texID = 0;
    public static boolean hasLoaded = false;
    
    
  
    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage;

        try
        {
            bufferedimage = ImageIO.read(imageStream);
        }
        finally
        {
            IOUtils.closeQuietly(imageStream);
        }

        return bufferedimage;
    }
    
    public static ByteBuffer read3DImageFrom2DTexutre(ResourceLocation imageLocation, int sliceWidth, int slices) {
    	IResource iresource;
    	try {
    		
    		
    		
    		iresource = Minecraft.getMinecraft().getResourceManager().getResource(imageLocation);
    		BufferedImage image = readBufferedImage(iresource.getInputStream());
    		
    		ByteBuffer buf = ByteBuffer.allocateDirect(image.getWidth()*image.getHeight()*slices*4);
    		
    		for(int y = 0; y < image.getHeight(); y += sliceWidth) {
    			for(int x = 0; x < image.getWidth(); x += sliceWidth) {
    				
    				BufferedImage subImage = image.getSubimage(x, y, sliceWidth, sliceWidth);
    				buf.put(readByteBuffer3(subImage));
    				
    			}
    		}
    		buf.rewind();
    		return buf;
    		
    	} catch(Exception e) {
    		
    	}
    	
    	return null;
    }
    
    /*
    public static ByteBuffer readByteBuffer4(ResourceLocation imageLocation) {
    	
    	ByteBuffer buf = ByteBuffer.allocateDirect(64*64*64*4);
    	try {
    		for(int x = 0; x < 64; ++x) {
        		ResourceLocation loc = new ResourceLocation(Reference.MOD_ID, "textures/lut/lutfolder/tile_" + x + ".png");
        		
        		buf.put(readByteBuffer3(loc));
        		
        	}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	buf.rewind();
        return buf;
    }*/
    
    public static ByteBuffer readByteBuffer3(BufferedImage image) {
    	
    
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

		for(int h = 0; h < image.getHeight(); h++) {
		    for(int w = 0; w < image.getWidth(); w++) {
		        int pixel = pixels[h * image.getWidth() + w];

		        buffer.put((byte) ((pixel >> 16) & 0xFF));
		        buffer.put((byte) ((pixel >> 8) & 0xFF));
		        buffer.put((byte) (pixel & 0xFF));
		        buffer.put((byte) ((pixel >> 24) & 0xFF));
		    }
		}

		buffer.flip();
		buffer.rewind();
		
		return buffer;

		
        
    }
    
    public static int[] readByteBuffer2(ResourceLocation imageLocation) {
    	int[] aint1 = null;
    	IResource iresource;
		try {
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(imageLocation);
			BufferedImage bufferedimage = readBufferedImage(iresource.getInputStream());
	        int i = bufferedimage.getWidth();
	       // bufferedimage.getRaster().get
	        int j = bufferedimage.getHeight();
	        //bufferedimage.
	        int[] aint = new int[i * j];
	        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
	       
	        
	        aint1 = aint;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aint1;
        
    }
    
    
    public static byte[] readByteBuffer(ResourceLocation imageLocation) {
    	byte[] bits = null;
    	IResource iresource;
		try {
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(imageLocation);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(ImageIO.read(iresource.getInputStream()), "png", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bits;
        
    }
    
    public static byte[] int2byte(int[]src) {
        int srcLength = src.length;
        byte[]dst = new byte[srcLength << 2];
        
        for (int i=0; i<srcLength; i++) {
            int x = src[i];
            int j = i << 2;
            dst[j++] = (byte) ((x >>> 0) & 0xff);           
            dst[j++] = (byte) ((x >>> 8) & 0xff);
            dst[j++] = (byte) ((x >>> 16) & 0xff);
            dst[j++] = (byte) ((x >>> 24) & 0xff);
        }
        return dst;
    }
    
    public static void construct3DLUT() {
    	//Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(BASICLUT);
    	if(reader == null) {
    		InputStream res;
			try {
				res = Minecraft.getMinecraft().getResourceManager().getResource(BASICLUT).getInputStream();
				
				//byte[] byteList = readByteBuffer(BASICLUT);
				//
				int[] intBuf = readByteBuffer2(BASICLUT);
				byte[] byteList = int2byte(intBuf);
				buffer = GLAllocation.createDirectByteBuffer(byteList.length);
				
				//reader.
				//int[] intBuf = readByteBuffer2(BASICLUT);
				//ByteBuffer buf = ByteBuffer.allocate(intBuf.length).
				
				//intBuffer = GLAllocation.createDirectByteBuffer(intBuf.length).asIntBuffer().
				//buffer = ByteBuffer.wrap(IOUtils.toByteArray(res));
				//IOUtils.readFully(res, buffer);
				//reader = new ByteBufInputStream(res);
				//reader = new BufferedReader(new InputStreamReader(res));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	}
    	//BASICLUT.
    	//GL11.glGenTextures(textures);
    	//GL11.glGenTextures()
    	texID = GL11.glGenTextures();
    	GL11.glBindTexture(GL12.GL_TEXTURE_3D, texID);
    	//GL12.glGenTextures(1, &lut_texture);
    	//GL12.clam
       
    	//GL15.glBindBuffer(GL, buffer);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
        //Gl12.gltex
        //GL11.glBindTexture(target, texture);
        GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGBA, 64, 64, 64, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        //GL12.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels);
        hasLoaded = true;
    }
    
    public static void construct3DLUTTiled() {
    	//Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(BASICLUT);
    	if(reader == null) {
    		
    		InputStream res;
			try {
				res = Minecraft.getMinecraft().getResourceManager().getResource(BASICLUT).getInputStream();
				/*
				int[] intBuf = readByteBuffer2(BASICLUT);
				byte[] byteList = int2byte(intBuf);
				
				buffer = GLAllocation.createDirectByteBuffer(byteList.length).put(byteList);
				buffer.rewind();
				*/
				
				buffer = read3DImageFrom2DTexutre(BASICLUT, 64, 64);
				buffer.rewind();
				
				//System.out.println(buffer.)
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	}
    	
    	
    	texID = GL11.glGenTextures();
    	GL11.glBindTexture(GL12.GL_TEXTURE_3D, texID);
        
    	   GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
           GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
           GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
           
    	GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
      //System.out.println("hi");
        GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGBA8, 64, 64, 64, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        
        /*
        GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGB8, 64, 64, 64, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        
        int count = 0;
        for(int y = 0; y < 8; ++y) {
        	for(int z = 0; z < 8; ++z) {
        		System.out.println(count);
            	//GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 64*count);
            	GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, 0, 0, 0, count, count*64, 64, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            	count += 1;
            }
        }
        */
        
        
        /*
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 1024);
        for(int z = 0; z < 8; ++z) {
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 64*z);
            GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, 0, 0, 0, z, 64, 64, 1, GL11.GL_RGBA, GGL11L_UNSIGNED_BYTE, buffer);
        } */
        hasLoaded = true;
    }
    
    
    public static int depthTex = -1;
    public static int depthBuffer = -1;
    public static boolean setupDepthTexture = false;
    
    public static int depthHWMultiplier = 0;
    
    public static int multiplyWH() {
    	return Minecraft.getMinecraft().displayHeight*Minecraft.getMinecraft().displayWidth;
    }
    
    public static Framebuffer depthBro;
    
    public static void blitDepth(){
    	
    	if(height != Minecraft.getMinecraft().displayHeight || width != Minecraft.getMinecraft().displayWidth || depthBuffer == -1){
    		GL11.glDeleteTextures(depthTex);
    		OpenGlHelper.glDeleteFramebuffers(depthTex);
    		
    		depthBuffer = OpenGlHelper.glGenFramebuffers();
    		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, depthBuffer);
    		depthTex = GL11.glGenTextures();
    		GlStateManager.bindTexture(depthTex);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTex, 0);
			int bruh = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
			if(bruh != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE){
				System.out.println("Failed to create depth texture framebuffer! This is an error!");
			}
    	}
    	OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
    	OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, depthBuffer);
    	GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
    	
    	Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
    }
    
    public static final FloatBuffer AUX_GL_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    public static final FloatBuffer AUX_GL_BUFFER2 = GLAllocation.createDirectFloatBuffer(16);
    
    public static float[] inv_ViewProjectionMatrix = new float[16];
    
    public static void createIVPM() {
    	
    	
    	GL11.glPushMatrix();
    	GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, AUX_GL_BUFFER);
    	GL11.glPopMatrix();
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, AUX_GL_BUFFER2);
		Matrix4f view = new Matrix4f();
		Matrix4f proj = new Matrix4f();
		view.load(AUX_GL_BUFFER);
		proj.load(AUX_GL_BUFFER2);
		AUX_GL_BUFFER.rewind();
		AUX_GL_BUFFER2.rewind();
		view.invert();
		proj.invert();
		Matrix4f.mul(view, proj, view);
		view.store(AUX_GL_BUFFER);
		AUX_GL_BUFFER.rewind();
		AUX_GL_BUFFER.get(inv_ViewProjectionMatrix);
		AUX_GL_BUFFER.rewind();
    	
    }
    
    public static void doPostProcess(){
    	//GlStateManager.enableDepth();
    	 //runDepth();
    	 
        if(height != Minecraft.getMinecraft().displayHeight || width != Minecraft.getMinecraft().displayWidth){
            recreateFBOs();
            height = Minecraft.getMinecraft().displayHeight;
            width = Minecraft.getMinecraft().displayWidth;
        }
        
        GL11.glEnable(GL12.GL_TEXTURE_3D);
        
        GL11.glPushMatrix();
        buf.bindFramebuffer(false);
     //   Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(BASICLUT);
//
        if(!hasLoaded) {
        	construct3DLUTTiled();
        }
       // hasLoaded = false;
        //Shader shaderT = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/lut3d"));
        
       
        
        boolean reload = false;
        if(reload) {
        	shader = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/lut3d"));
        }
        shader.use();
        
     
       /*
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/lut/neutrallut.png");
        // System.out.println("hi");
        Minecraft.getMinecraft().getTextureManager().bindTexture(res);
        //GlStateManager.bindTexture(buf.framebufferTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
       
        int tempTexU = GL20.glGetUniformLocation(shader.getShaderId(), "tempTest");
        GL20.glUniform1i(tempTexU, 3);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
        */
        
        
        /*
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buf.depthBuffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTex);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
       */
        
        
        
        
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, texID);
        int lut = GL20.glGetUniformLocation(shader.getShaderId(), "lut");
       // GL20.glUniform1i(location, v0);
        GL20.glUniform1i(lut, 3);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+5);
		//Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID + ":" + "textures/shaders/noise_1.png"));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTex);
		GL20.glUniform1i(GL20.glGetUniformLocation(shader.getShaderId(), "depth"), 5);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+6);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID + ":" + "textures/shaders/noise_1.png"));
		GL20.glUniform1i(GL20.glGetUniformLocation(shader.getShaderId(), "noise1"), 6);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
	
		
		/*
		 GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+6);
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID + ":" + "textures/shaders/noise_1.png"));
		//	GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTex);
			GL20.glUniform1i(GL20.glGetUniformLocation(shader.getShaderId(), "depth"), 6);
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		*/
		
		Vec3d pos = ActiveRenderInfo.getCameraPosition();
		Vec3d look = Minecraft.getMinecraft().player.getLookVec();
		float a = 0.1f;
		float b = 0.00001f;
		double posy = 20;
		float fogAmount =  (float) ((a/b)*Math.pow(-posy*b, 2) * (1.0-Math.pow( -40*look.y*b , 2))/look.y);
		    
		//System.out.println(fogAmount);
		//System.out.println(look);
		
		Vec3d sun = new Vec3d(1, 0, 0);
		
		double doug = Math.toDegrees(Minecraft.getMinecraft().world.getCelestialAngle(0))-45;
		
		
		double wt = Minecraft.getMinecraft().world.getWorldTime();
		double il = (wt-23000)/(39000-23000);
		
		
		sun = sun.rotateYaw((float) ((float) 2*Math.PI*il - Math.PI/1.5));
		sun = sun.rotatePitch((float) ((float) -Math.PI/2));
		
	//	System.out.println(wt);
		
		
		GL20.glUniform3f(GL20.glGetUniformLocation(shader.getShaderId(), "cam"), (float) pos.x, (float) pos.y, (float) pos.z);
		GL20.glUniform3f(GL20.glGetUniformLocation(shader.getShaderId(), "look"), (float) look.x, (float) look.y, (float) look.z);
		GL20.glUniform3f(GL20.glGetUniformLocation(shader.getShaderId(), "sun"), (float) sun.x, (float) sun.y, (float) sun.z);
		GL20.glUniform2f(GL20.glGetUniformLocation(shader.getShaderId(), "windowSize"), (float) Minecraft.getMinecraft().displayWidth, (float) Minecraft.getMinecraft().displayHeight);
        
		GL20.glUniform1f(GL20.glGetUniformLocation(shader.getShaderId(), "yOffset"), (float) Minecraft.getMinecraft().player.posY);
   
	//	System.out.println(pos);
		//System.out.println((float) Minecraft.getMinecraft().player.posY);
		
		AUX_GL_BUFFER.put(inv_ViewProjectionMatrix);
		AUX_GL_BUFFER.rewind();
		shader.sendMatrix4AsUniform("inv_ViewProjectionMatrix", false, AUX_GL_BUFFER);
		
       	
   
        
       // System.out.println("using shader");
        Minecraft.getMinecraft().getFramebuffer().framebufferRender(buf.framebufferWidth, buf.framebufferHeight);
        shader.release();
        
       
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buf.framebufferObject);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
       
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        GL11.glDisable(GL12.GL_TEXTURE_3D);
        GlStateManager.enableDepth();
        GL11.glPopMatrix();
    }
    
    public static void recreateFBOs(){
        if(buf != null)
            buf.deleteFramebuffer();
        buf = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
    }

}
