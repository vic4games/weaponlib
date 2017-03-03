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
	private WeaponAttachmentAspect attachmentAspect;
	private ModContext modContext;

	public CustomGui(Minecraft mc, ModContext modContext, WeaponAttachmentAspect attachmentAspect) {
		this.mc = mc;
		this.modContext = modContext;
		this.attachmentAspect = attachmentAspect;
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
		
		PlayerWeaponInstance weaponInstance = modContext.getMainHeldWeapon();
		
		if(weaponInstance != null) {
			Weapon weaponItem = (Weapon) itemStack.getItem();
			
			String crosshair = weaponItem != null ? weaponItem.getCrosshair(weaponInstance) : null;
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
				
//				if(weaponItem.isCrosshairFullScreen(itemStack))	 {
//					drawTexturedQuadFit(0, 0, width, height, 0);
//				} else {
//					drawTexturedModalRect(xPos, yPos, 0, 0, BUFF_ICON_SIZE, BUFF_ICON_SIZE);
//				}
				
				if(isInModifyingState(weaponInstance) /*Weapon.isModifying(itemStack)*/ /*weaponItem.getState(weapon) == Weapon.STATE_MODIFYING*/) {
					fontRender.drawStringWithShadow("Press [up] to add optic", width / 2 - 40, 60, color);
					fontRender.drawStringWithShadow("Press [left] to add barrel rig", 10, height / 2 - 10, color);
					fontRender.drawStringWithShadow("Press [right] to change camo", width / 2 + 60, height / 2 - 20, color);
					fontRender.drawStringWithShadow("Press [down] to add under-barrel rig", 10, height - 40, color);
				} else {
					String nextMessage = modContext.getStatusMessageCenter().nextMessage();
					if(nextMessage == null) {
						nextMessage = getDefaultWeaponMessage(weaponInstance);
					}
					
					int x = width - 80;
					int y = 10;
					

					int stringWidth = fontRender.getStringWidth(nextMessage);
					if(stringWidth > 80 ) {
						x = width - stringWidth - 5;
					}

					fontRender.drawStringWithShadow(nextMessage, x, y, color);
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
			
			String nextMessage = modContext.getStatusMessageCenter().nextMessage();
			if(nextMessage == null) {
				nextMessage = getDefaultMagazineMessage(itemStack);
			}
			
			int x = width - 80;
			int y = 10;

			int stringWidth = fontRender.getStringWidth(nextMessage);
			if(stringWidth > 80 ) {
				x = width - stringWidth - 5;
			}
			
			fontRender.drawStringWithShadow(nextMessage, x, y, color);
			event.setCanceled(true);
		}
	}


	private String getDefaultMagazineMessage(ItemStack itemStack) {
		ItemMagazine magazine = (ItemMagazine) itemStack.getItem();
		
		String text = "Ammo: " + Tags.getAmmo(itemStack) + "/" + magazine.getAmmo();
		return text;
	}


	private String getDefaultWeaponMessage(PlayerWeaponInstance weaponInstance) {
		@SuppressWarnings("static-access")
		ItemMagazine magazine = (ItemMagazine) attachmentAspect.getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance);
		int totalCapacity;
		if(magazine != null) {
			totalCapacity = magazine.getAmmo();
		} else {
			totalCapacity = weaponInstance.getWeapon().getAmmoCapacity();
		}
		
		String text;
		if(weaponInstance.getWeapon().getAmmoCapacity() == 0 && totalCapacity == 0) {
			text = "No magazine";
		} else {
			text = "Ammo: " + weaponInstance.getWeapon().getCurrentAmmo(compatibility.clientPlayer()) + "/" + totalCapacity;
		}
		return text;
	}


	private boolean isInModifyingState(PlayerWeaponInstance weaponInstance) {
		return weaponInstance.getState() == WeaponState.MODIFYING
				|| weaponInstance.getState() == WeaponState.MODIFYING_REQUESTED
				|| weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT
				|| weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT_REQUESTED;
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
