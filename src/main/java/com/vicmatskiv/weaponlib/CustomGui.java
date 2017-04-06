package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleGui;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTessellator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CustomGui extends CompatibleGui {
	private Minecraft mc;
	private AttachmentManager attachmentManager;

	public CustomGui(Minecraft mc, AttachmentManager attachmentManager) {
		this.mc = mc;
		this.attachmentManager = attachmentManager;
	}
	private static final int BUFF_ICON_SIZE = 256;
	
	
	//@SubscribeEvent defined in CompatibleGui
	@Override
	public void onCompatibleRenderHud(RenderGameOverlayEvent.Pre event) {
		
		if(compatibility.getEventType(event) == RenderGameOverlayEvent.ElementType.HELMET) {
			ItemStack helmet = compatibility.getHelmet();
			if(helmet != null && mc.gameSettings.thirdPersonView == 0 && helmet.getItem() instanceof CustomArmor) {
				// Texture must be Width: 427, height: 240
				String hudTexture = ((CustomArmor)helmet.getItem()).getHudTexture();
				if(hudTexture != null) {
					ScaledResolution scaledResolution = compatibility.getResolution(event);
					int width = scaledResolution.getScaledWidth();
				    int height = scaledResolution.getScaledHeight();
				    
				    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_LIGHTING);
			        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_BLEND);
					
					
					this.mc.renderEngine.bindTexture(new ResourceLocation(hudTexture));
					
					drawTexturedQuadFit(0, 0, width, height, 0);
					
					GL11.glPopAttrib();
					
					event.setCanceled(true);
				}
			}
		}
	}

	
	//@SubscribeEvent defined in CompatibleGui
	@Override
	public void onCompatibleRenderCrosshair(RenderGameOverlayEvent.Pre event) {
		if (compatibility.getEventType(event) != RenderGameOverlayEvent.ElementType.CROSSHAIRS ) {
			return;
		}
		
		ItemStack itemStack = compatibility.getHeldItemMainHand(compatibility.clientPlayer());
		if(itemStack == null) {
			return;
		}
		
		if(itemStack.getItem() instanceof Weapon) {
			Weapon weaponItem = (Weapon) itemStack.getItem();
			String crosshair = weaponItem != null ? weaponItem.getCrosshair(itemStack, compatibility.clientPlayer()) : null;
			if(crosshair != null) {
				ScaledResolution scaledResolution = compatibility.getResolution(event);
				int width = scaledResolution.getScaledWidth();
			    int height = scaledResolution.getScaledHeight();
			    
			    int xPos = width / 2 - BUFF_ICON_SIZE / 2;
				int yPos = height / 2 - BUFF_ICON_SIZE / 2;
				
			    FontRenderer fontRender = compatibility.getFontRenderer();

				mc.entityRenderer.setupOverlayRendering();
				
				int color = 0xFFFFFF;
				
				this.mc.renderEngine.bindTexture(new ResourceLocation(crosshair));

				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_BLEND);
				
				if(weaponItem.isCrosshairFullScreen(itemStack))	 {
					drawTexturedQuadFit(0, 0, width, height, 0);
				} else {
					drawTexturedModalRect(xPos, yPos, 0, 0, BUFF_ICON_SIZE, BUFF_ICON_SIZE);
				}
				
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
						text = "Ammo: " + weaponItem.getCurrentAmmo(compatibility.clientPlayer()) + "/" + totalCapacity;
					}
					
					int x = width - 80;
					int y = 10;

					fontRender.drawStringWithShadow(text, x, y, color);
				}
				GL11.glPopAttrib();
				
				event.setCanceled(true);
			}
		} else if(itemStack.getItem() instanceof ItemMagazine) {
			ScaledResolution scaledResolution = compatibility.getResolution(event);
			int width = scaledResolution.getScaledWidth();
			FontRenderer fontRender = compatibility.getFontRenderer();
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
		CompatibleTessellator tessellator = CompatibleTessellator.getInstance();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
		tessellator.draw();
	}
}
