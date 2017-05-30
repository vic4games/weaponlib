package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.StatusMessageCenter.Message;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGui;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTessellator;
import com.vicmatskiv.weaponlib.config.ConfigurationManager.StatusBarPosition;
import com.vicmatskiv.weaponlib.electronics.ItemWirelessCamera;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CustomGui extends CompatibleGui {

    //    private static final int BUFF_ICON_SIZE = 256;

	private static final int STATUS_BAR_BOTTOM_OFFSET = 15;
	private static final int STATUS_BAR_TOP_OFFSET = 10;

    private Minecraft mc;
	private WeaponAttachmentAspect attachmentAspect;
	private ModContext modContext;
	private StatusBarPosition statusBarPosition;

	public CustomGui(Minecraft mc, ModContext modContext, WeaponAttachmentAspect attachmentAspect) {
		this.mc = mc;
		this.modContext = modContext;
		this.attachmentAspect = attachmentAspect;
		this.statusBarPosition = modContext.getConfigurationManager().getStatusBarPosition();
	}


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

//			    int xPos = width / 2 - BUFF_ICON_SIZE / 2;
//				int yPos = height / 2 - BUFF_ICON_SIZE / 2;

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
					Message message = modContext.getStatusMessageCenter().nextMessage();
					String messageText;
					if(message != null) {
						messageText = message.getMessage();
						if(message.isAlert()) {
							color = 0xFFFF00;
						}
					} else {
						messageText = getDefaultWeaponMessage(weaponInstance);
					}

					int x = getStatusBarXPosition(width, messageText, fontRender);
					int y = getStatusBarYPosition(height);

//
//					int stringWidth = fontRender.getStringWidth(messageText);
//					if(stringWidth > 80 ) {
//						x = width - stringWidth - 5;
//					}

					fontRender.drawStringWithShadow(messageText, x, y, color);
				}
				GL11.glPopAttrib();

				event.setCanceled(true);
			}
		} else if(itemStack.getItem() instanceof ItemMagazine) {
			ScaledResolution scaledResolution = compatibility.getResolution(event);
			int width = scaledResolution.getScaledWidth();
			int height = scaledResolution.getScaledHeight();
			FontRenderer fontRender = compatibility.getFontRenderer();
			mc.entityRenderer.setupOverlayRendering();
			int color = 0xFFFFFF;

			Message message = modContext.getStatusMessageCenter().nextMessage();
			String messageText;
			if(message != null) {
				messageText = message.getMessage();
				if(message.isAlert()) {
					color = 0xFF0000;
				}
			} else {
				messageText = getDefaultMagazineMessage(itemStack);
			}

			int x = getStatusBarXPosition(width, messageText, fontRender);
			int y = getStatusBarYPosition(height);

//			int stringWidth = fontRender.getStringWidth(messageText);
//			if(stringWidth > 80 ) {
//				x = width - stringWidth - 5;
//			}

			fontRender.drawStringWithShadow(messageText, x, y, color);
			event.setCanceled(true);
		} else if(itemStack.getItem() instanceof ItemWirelessCamera) {
		    ScaledResolution scaledResolution = compatibility.getResolution(event);
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();
            FontRenderer fontRender = compatibility.getFontRenderer();
            mc.entityRenderer.setupOverlayRendering();
            int color = 0xFFFFFF;

            Message message = modContext.getStatusMessageCenter().nextMessage();
            String messageText;
            if(message != null) {
                messageText = message.getMessage();
                if(message.isAlert()) {
                    color = 0xFF0000;
                }

                int x = getStatusBarXPosition(width, messageText, fontRender);
                int y = getStatusBarYPosition(height);

                int stringWidth = fontRender.getStringWidth(messageText);
                if(stringWidth > 80 ) {
                    x = width - stringWidth - 5;
                }

                fontRender.drawStringWithShadow(messageText, x, y, color);
                event.setCanceled(true);
            }
		} else if(itemStack.getItem() instanceof ItemGrenade) {
		    ScaledResolution scaledResolution = compatibility.getResolution(event);
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();
            FontRenderer fontRender = compatibility.getFontRenderer();
            mc.entityRenderer.setupOverlayRendering();
            int color = 0xFFFFFF;

            Message message = modContext.getStatusMessageCenter().nextMessage();
            String messageText;
            if(message != null) {
                messageText = message.getMessage();
                if(message.isAlert()) {
                    color = 0xFFFF00;
                }

                int x = getStatusBarXPosition(width, messageText, fontRender);
                int y = getStatusBarYPosition(height);

                int stringWidth = fontRender.getStringWidth(messageText);
                if(stringWidth > 80 ) {
                    x = width - stringWidth - 5;
                }

                fontRender.drawStringWithShadow(messageText, x, y, color);
                event.setCanceled(true);
            }
		}
	}


    private int getStatusBarXPosition(int width, String text, FontRenderer fontRender) {
        int x;
        if(statusBarPosition == StatusBarPosition.BOTTOM_RIGHT || statusBarPosition == StatusBarPosition.TOP_RIGHT) {
            x = width - 80;
            int stringWidth = fontRender.getStringWidth(text);
            if(stringWidth > 80 ) {
                x = width - stringWidth - 5;
            }
        } else {
            x = 10;
        }

        return x;
    }

    private int getStatusBarYPosition(int height) {
        int yPos;
        switch(statusBarPosition) {
        case TOP_RIGHT: case TOP_LEFT:
            yPos = STATUS_BAR_TOP_OFFSET;
            break;
        case BOTTOM_RIGHT: case BOTTOM_LEFT:
            yPos = height - STATUS_BAR_BOTTOM_OFFSET;
            break;
        default:
            yPos = STATUS_BAR_TOP_OFFSET;
        }
        return yPos;
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
