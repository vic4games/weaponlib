package com.vicmatskiv.weaponlib;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CustomGui extends Gui {
	private Minecraft mc;
	private AttachmentManager attachmentManager;

	public CustomGui(Minecraft mc, AttachmentManager attachmentManager) {
		this.mc = mc;
		this.attachmentManager = attachmentManager;
	}

	private static final int BUFF_ICON_SIZE = 256;
	
	
//	@SubscribeEvent
//	public void renderHandEvent(RenderHandEvent par1RenderHandEvent)
//	{
//	    System.out.println("Rendering player hand");
////	    GL11.glPushMatrix();
////	    Render render = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
////        RenderPlayer renderplayer = (RenderPlayer)render;
////        float f10 = 1.0F;
////        //GL11.glScalef(f10, f10, f10);
////        GL11.glTranslatef(-1f, -1f, -1f);
////        renderplayer.renderFirstPersonArm(this.mc.thePlayer);
////        GL11.glPopMatrix();
//	}
	
	@SubscribeEvent
	public void onRenderHud(RenderGameOverlayEvent.Pre event) {
		
		if(event.type == RenderGameOverlayEvent.ElementType.HELMET) {
			ItemStack helmet = mc.thePlayer.getEquipmentInSlot(4);
			if(helmet != null && mc.gameSettings.thirdPersonView == 0 && helmet.getItem() instanceof CustomArmor) {
				// Texture must be Width: 427, height: 240
				String hudTexture = ((CustomArmor)helmet.getItem()).getHudTexture();
				if(hudTexture != null) {
					ScaledResolution scaledResolution = event.resolution;
					int width = scaledResolution.getScaledWidth();
				    int height = scaledResolution.getScaledHeight();
				    
				    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_LIGHTING);
					
					this.mc.renderEngine.bindTexture(new ResourceLocation(hudTexture));
					
					drawTexturedQuadFit(0, 0, width, height, 0);
					
					event.setCanceled(true);
				}
			}
		}
	}

	//
	// This event is called by GuiIngameForge during each frame by
	// GuiIngameForge.pre() and GuiIngameForce.post().
	//
	@SubscribeEvent
	public void onRenderCrosshair(RenderGameOverlayEvent.Pre event) {
		
		if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS ) {
			return;
		}
		
		ItemStack itemStack = mc.thePlayer.getHeldItem();
		if(itemStack == null) {
			return;
		}
		
		if(itemStack.getItem() instanceof Weapon) {
			Weapon weaponItem = (Weapon) itemStack.getItem();
			String crosshair = weaponItem != null ? weaponItem.getCrosshair(itemStack, mc.thePlayer) : null;
			if(crosshair != null) {
				ScaledResolution scaledResolution = event.resolution;
				int width = scaledResolution.getScaledWidth();
			    int height = scaledResolution.getScaledHeight();
			    
				int xPos = width / 2 - BUFF_ICON_SIZE / 2;
				int yPos = height / 2 - BUFF_ICON_SIZE / 2;
//				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//				GL11.glDisable(GL11.GL_LIGHTING);
				
				this.mc.renderEngine.bindTexture(new ResourceLocation(crosshair));

				if(weaponItem.isCrosshairFullScreen(itemStack))	 {
					drawTexturedQuadFit(0, 0, width, height, 0);
				} else {
					drawTexturedModalRect(xPos, yPos, 0, 0, BUFF_ICON_SIZE, BUFF_ICON_SIZE);
				}
				
				FontRenderer fontRender = mc.fontRenderer;

				mc.entityRenderer.setupOverlayRendering();
				
				int color = 0xFFFFFF;
				
				if(Weapon.isModifying(itemStack) /*weaponItem.getState(weapon) == Weapon.STATE_MODIFYING*/) {
					fontRender.drawStringWithShadow("Attachment selection mode. Press [f] to exit.", 10, 10, color);
					fontRender.drawStringWithShadow("Press [up] to add optic", width / 2 - 40, 60, color);
					fontRender.drawStringWithShadow("Press [left] to add barrel rig", 10, height / 2 - 10, color);
					fontRender.drawStringWithShadow("Press [right] to change camo", width / 2 + 60, height / 2 - 20, color);
					fontRender.drawStringWithShadow("Press [down] to add under-barrel rig", 10, height - 40, color);
				} else {
					ItemMagazine magazine = (ItemMagazine) attachmentManager.getActiveAttachment(itemStack, AttachmentCategory.MAGAZINE);
					int totalCapacity;
					if(magazine != null) {
						totalCapacity = magazine.getAmmo();
					} else {
						totalCapacity = weaponItem.getAmmoCapacity();
					}
					
					String text;
					if(weaponItem.getAmmoCapacity() == 0 && totalCapacity == 0) {
						text = "No magazine";
					} else {
						text = "Ammo: " + weaponItem.getCurrentAmmo(mc.thePlayer) + "/" + totalCapacity;
					}
					
					int x = width - 80;
					int y = 10;

					fontRender.drawStringWithShadow(text, x, y, color);
				}
				event.setCanceled(true);
			}
		} else if(itemStack.getItem() instanceof ItemMagazine) {
			ScaledResolution scaledResolution = event.resolution;
			int width = scaledResolution.getScaledWidth();
			FontRenderer fontRender = mc.fontRenderer;
			mc.entityRenderer.setupOverlayRendering();
			int color = 0xFFFFFF;
			
			ItemMagazine magazine = (ItemMagazine) itemStack.getItem();
			
			String text = "Ammo: " + Tags.getAmmo(itemStack) + "/" + magazine.getAmmo();
			int x = width - 80;
			int y = 10;

			fontRender.drawStringWithShadow(text, x, y, color);
			event.setCanceled(true);
		}
	}
	
	private static void drawTexturedQuadFit(double x, double y, double width, double height, double zLevel){
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
        tessellator.draw();
	}
}
