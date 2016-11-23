package com.vicmatskiv.weaponlib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeaponEventHandler {

	static final float DEFAULT_ZOOM = 0.75f;

	static int tmp = 0;
	
	private SafeGlobals safeGlobals;

	public WeaponEventHandler(SafeGlobals safeGlobals) {
		this.safeGlobals = safeGlobals;
	}
	
	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		//System.out.println("Gui event: " + event.gui);
		safeGlobals.guiOpen.set(event.gui != null);
	}
	

	@SubscribeEvent
	public void zoom(FOVUpdateEvent event) {
		// System.out.println("Using item " + event.entity.getHeldItem());

		ItemStack stack = event.entity.getHeldItem();
		if (stack != null) {
			if (stack.getItem() instanceof Weapon) {
				if (stack.stackTagCompound != null) {
					float zoom = stack.stackTagCompound
							.getFloat(Weapon.ZOOM_TAG);
					event.newfov = zoom;
				}

			}
		}

	}


	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleRenderLivingEvent(RenderLivingEvent.Pre event) {

		if ((event.isCanceled()) || (!(event.entity instanceof EntityPlayer)))
			return;

		ItemStack itemStack = event.entity.getHeldItem();

		if (itemStack != null && itemStack.getItem() instanceof Weapon) {
			RenderPlayer rp = (RenderPlayer) event.renderer;

			if (itemStack.stackTagCompound != null) {
				rp.modelBipedMain.aimedBow = itemStack.stackTagCompound
						.getBoolean("Aimed");
			}
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event) {
		//System.out.println("Interacting with " + event.entity);
	}
	
	//@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event)
	{
		RenderGlobal context = event.context;
	    
	    Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.thePlayer;
	    

	    ItemStack currentItem = player.inventory.getCurrentItem();
	    float partialTick = event.partialTicks;

	    if (currentItem != null && currentItem.getItem() instanceof Weapon) {
	    	
	    	
	    	EntityLivingBase cameraEntity = minecraft.renderViewEntity;
	    	
	    	Vec3 cameraPosition = cameraEntity.getPosition(partialTick);
	    	RenderManager rm = RenderManager.instance;
	    	
	    	Vec3 playerPosition = player.getPosition(partialTick);
	    	GL11.glPushMatrix();
	    	//GL11.glTranslated(-playerPosition.xCoord, -playerPosition.yCoord, -playerPosition.zCoord);
			MovingObjectPosition targetPosition = player.rayTrace(1000, partialTick);
			
			if(targetPosition != null) {
//				System.out.println("Target position x: " + targetPosition.blockX
//						+ ", y: " + targetPosition.blockY + ", z: " + targetPosition.blockZ
//						);
//				System.out.println("Player position: " + player.posX + ", " + player.posY + ", " + player.posZ);
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			    GL11.glDisable(GL11.GL_CULL_FACE);
			    GL11.glDisable(GL11.GL_LIGHTING);
			    GL11.glDisable(GL11.GL_TEXTURE_2D);

			    GL11.glEnable(GL11.GL_BLEND);
			    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			    GL11.glColor4f(1f, 0f, 0f, 0.5f); 
			    GL11.glLineWidth(2.0F);
			    GL11.glDepthMask(false);

			    Tessellator tessellator = Tessellator.instance;
			    tessellator.startDrawing(GL11.GL_LINES);
			    //tessellator.addVertex(playerPosition.xCoord, playerPosition.yCoord, playerPosition.zCoord);
//			    tessellator.addVertex(
//			    		targetPosition.blockX, 
//			    		targetPosition.blockY,
//			    		targetPosition.blockZ);
//
//			    
//			    tessellator.addVertex(rm.viewerPosX, rm.viewerPosY, rm.viewerPosZ + 1);
			    
//			    Vec3 lookVec = player.getLook(partialTick);
			    
//			    tessellator.addVertex(
//			    		lookVec.xCoord, 
//			    		lookVec.yCoord,
//			    		lookVec.zCoord - 1);
//			    
//			    tessellator.addVertex(
//			    		lookVec.xCoord, 
//			    		lookVec.yCoord,
//			    		lookVec.zCoord - 1);
			    
//			    tessellator.addVertex(
//			    		targetPosition.blockX, 
//			    		targetPosition.blockY + 3,
//			    		targetPosition.blockZ);
			    
			    
//			    tessellator.addVertex(
//			    		playerPosition.xCoord, 
//			    		playerPosition.yCoord,
//			    		playerPosition.zCoord);
//			    
//			    tessellator.addVertex(
//			    		playerPosition.xCoord - 3, 
//			    		playerPosition.yCoord,
//			    		playerPosition.zCoord);
			    
//			    tessellator.addVertex(rm.viewerPosX, rm.viewerPosY, rm.viewerPosZ);
//
//			    tessellator.addVertex(rm.viewerPosX, rm.viewerPosY + 1, rm.viewerPosZ);
			    
//			    tessellator.addVertex(
//			    		playerPosition.xCoord, 
//			    		playerPosition.yCoord,
//			    		playerPosition.zCoord);
			    
			    Entity theRenderViewEntity = minecraft.renderViewEntity;
			    
			    
			    tessellator.addVertex(2, 0, 0);
			    
			    tessellator.addVertex(
			    		targetPosition.blockX - playerPosition.xCoord, 
			    		targetPosition.blockY - playerPosition.yCoord,
			    		targetPosition.blockZ - playerPosition.zCoord);
			    
//			    tessellator.addVertex(
//			    		0, 
//			    		0,
//			    		0);
//			    
//			    tessellator.addVertex(
//			    		0 + 3, 
//			    		0 + 3,
//			    		0 + 3);

			    tessellator.draw();

			    GL11.glDepthMask(true);
			    GL11.glPopAttrib();
			}
			GL11.glPopMatrix();
	    	
	    	
	      //MovingObjectPosition target = currentItem.getItem().getMovingObjectPositionFromPlayer(player.worldObj, player, true);

	      // check target to see if it's a block, if so take the coordinates and subtract the EntityPlayer.getPosition and draw to 0,0,0
	    }
	}
}