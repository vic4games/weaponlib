package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent.Phase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.shader.Framebuffer;

public class ClientEventHandler extends CompatibleClientEventHandler {

	private Lock mainLoopLock = new ReentrantLock();
	private SafeGlobals safeGlobals;
	private Queue<Runnable> runInClientThreadQueue;
	//private Framebuffer framebuffer;
	private long renderEndNanoTime;
	
	private MyEntityRenderer entityRenderer;
	
	private boolean currentViewBobbing;
	
	private ClientModContext modContext;

	public ClientEventHandler(ClientModContext modContext, Lock mainLoopLock, SafeGlobals safeGlobals, Queue<Runnable> runInClientThreadQueue) {
		this.modContext = modContext;
		this.mainLoopLock = mainLoopLock;
		this.safeGlobals = safeGlobals;
		this.runInClientThreadQueue = runInClientThreadQueue;
		
//        this.framebuffer = new Framebuffer(400, 300, true);
//        this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.renderEndNanoTime = System.nanoTime();
        
	}
	
	private MyEntityRenderer getEntityRenderer() {
		if(this.entityRenderer == null) {
			this.entityRenderer = new MyEntityRenderer(Minecraft.getMinecraft(), 
	        		Minecraft.getMinecraft().getResourceManager());
		}
		return this.entityRenderer;
	}

	public void onCompatibleClientTick(CompatibleClientTickEvent event) {		
		if(event.getPhase() == Phase.START) {
			mainLoopLock.lock();
		} else if(event.getPhase() == Phase.END) {
			mainLoopLock.unlock();
			processRunInClientThreadQueue();
			safeGlobals.objectMouseOver.set(compatibility.getObjectMouseOver());
			if(Minecraft.getMinecraft().thePlayer != null) {
				safeGlobals.currentItemIndex.set(Minecraft.getMinecraft().thePlayer.inventory.currentItem);
			}
		}
	}

	private void processRunInClientThreadQueue() {
		Runnable r;
		while((r = runInClientThreadQueue.poll()) != null) {
			r.run();
		}
	}
	
	static int counter = 0;
	
	@SubscribeEvent
	public final void onRenderTickEvent(TickEvent.RenderTickEvent event) {
		//System.out.println("Done rendering");
		if(event.phase == TickEvent.Phase.START) {
//			currentViewBobbing = Minecraft.getMinecraft().gameSettings.viewBobbing;
//			Minecraft.getMinecraft().gameSettings.viewBobbing = false;
		} else if(event.phase == TickEvent.Phase.END) {
//			Minecraft.getMinecraft().gameSettings.viewBobbing = currentViewBobbing;
		}
		
		if(event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().renderViewEntity != null) {
			//System.out.println("Done rendering");
			
			
			modContext.getFramebuffer().bindFramebuffer(true);
			
			//WeaponEventHandler.overridenFov = 1.5f;
			
			//GL11.glEnable(GL11.GL_TEXTURE_2D);
			//Minecraft.getMinecraft().entityRenderer
			
			long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
//			EntityRenderer minecracftEntityRenderer = Minecraft.getMinecraft().entityRenderer;
//			minecracftEntityRenderer.renderWorld(event.renderTickTime, 
//					p_78471_2_);
			
			
			if(/*counter++ % 2 == 0 &&*/ Weapon.isZoomed(null, Minecraft.getMinecraft().thePlayer.getHeldItem())) {
				//getEntityRenderer().updateLightmap(p_78471_2_);
				//getEntityRenderer().enableLightmap(p_78471_2_);
				getEntityRenderer().updateRenderer();
				getEntityRenderer().renderWorld(event.renderTickTime, p_78471_2_);
			} else {
				//modContext.getFramebuffer().framebufferClear();
			}
			
			this.renderEndNanoTime = System.nanoTime();
			
			//WeaponEventHandler.overridenFov = 1;
			
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
		}
		
		if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().renderViewEntity != null) {
//			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
//			if(Weapon.isZoomed(null, Minecraft.getMinecraft().thePlayer.getHeldItem())) {
//				framebufferRender(400, 300);
//			}
			
		}
		
	}
	
	public void framebufferRender(int width, int height)
    {
        if (OpenGlHelper.isFramebufferEnabled()) { 
        	
        	//GL11.glEnable(GL11.GL_STENCIL_TEST);
        	
            GL11.glPushMatrix();
    		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    		
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, modContext.getFramebuffer().framebufferTexture);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0D, (double)width, (double)height, 0.0D, 0.0D, 10.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
						
			GL11.glLoadIdentity();


			float f2 = (float)modContext.getFramebuffer().framebufferWidth / (float)modContext.getFramebuffer().framebufferTextureWidth;
			float f3 = (float)modContext.getFramebuffer().framebufferHeight / (float)modContext.getFramebuffer().framebufferTextureHeight;
//
			GL11.glViewport(0, 100, width, height);
//			
			GL11.glTranslatef(-100F, 0.0F, 0F); //-1000.0F);
//			
	
			GL11.glEnable(GL11.GL_TEXTURE_2D);
//			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            //tessellator.setColorOpaque_I(-1);
            tessellator.addVertexWithUV(0.0D, (double)height, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV((double)width, (double)height, 0.0D, (double)f2, 0.0D);
            tessellator.addVertexWithUV((double)width, 0.0D, 0.0D, (double)f2, (double)f3);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)f3);
            tessellator.draw();

			
            GL11.glPopAttrib();
            GL11.glPopMatrix();  
            
            //GL11.glDisable(GL11.GL_STENCIL_TEST);
            
            //GL11.glFlush();
        }
        
    }
	
	public void framebufferRenderOrig(int width, int height)
    {
        if (OpenGlHelper.isFramebufferEnabled()) { 
        	
        	GL11.glEnable(GL11.GL_STENCIL_TEST);
        	
            GL11.glPushMatrix();
    		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    		
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, modContext.getFramebuffer().framebufferTexture);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0D, (double)width, (double)height, 0.0D, 1000.0D, 3000.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
						
			GL11.glLoadIdentity();

//			float width = (float)width;
//			float height = (float)height;
			float f2 = (float)modContext.getFramebuffer().framebufferWidth / (float)modContext.getFramebuffer().framebufferTextureWidth;
			float f3 = (float)modContext.getFramebuffer().framebufferHeight / (float)modContext.getFramebuffer().framebufferTextureHeight;

			GL11.glViewport(0, 100, width, height);
			
			GL11.glTranslatef(-50F, 0.0F, -2000.0F);
			
			{
    			
				GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT );
				//GL11.glEnable(GL11.GL_STENCIL_TEST);
				//GL11.glEnable(GL11.GL_DEPTH_TEST);
				//GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glColorMask(false, false, false, false);
				GL11.glDepthMask(false);
				GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
				GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);   // draw 1s on test fail (always)

	    		GL11.glStencilMask(0xFF);
	    		//GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
	    		//GL11.glClearStencil(0);
	    		
				float items = 30;
	    		float SECTORS = 10;
	    		float RADIUS = 150;
	 			GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				float x = 200; 
				float y = 150;
				GL11.glVertex2f(x, y);
				for(int i = 0; i <= items; i++)
				{
					float t = (float) (2 * Math.PI * (float) i / (float) items);
					GL11.glVertex2d(x + Math.sin(t) * RADIUS, y + Math.cos(t) * RADIUS);
				}
				GL11.glEnd();
				
				GL11.glEnable(GL11.GL_DEPTH_TEST);
		        GL11.glColorMask(true, true, true, true);
		        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 255); // We Draw Only Where The Stencil Is 1
		                                                            // (I.E. Where The Floor Was Drawn)
		        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
				
	        }
			
	
			GL11.glEnable(GL11.GL_TEXTURE_2D);
//			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(-1);
            tessellator.addVertexWithUV(0.0D, (double)height, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV((double)width, (double)height, 0.0D, (double)f2, 0.0D);
            tessellator.addVertexWithUV((double)width, 0.0D, 0.0D, (double)f2, (double)f3);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)f3);
            tessellator.draw();

			
            GL11.glPopAttrib();
            GL11.glPopMatrix();  
            
            GL11.glDisable(GL11.GL_STENCIL_TEST);
            
            //GL11.glFlush();
        }
        
    }
	
}
