package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.StatusMessageCenter.Message;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityEquipmentSlot;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGui;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
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
		        
			ItemStack helmetStack = compatibility.getHelmet();
			if(helmetStack != null && mc.gameSettings.thirdPersonView == 0 && helmetStack.getItem() instanceof CustomArmor) {
			    	            
				// Texture must be Width: 427, height: 240
				String hudTexture = ((CustomArmor)helmetStack.getItem()).getHudTexture();
				if(hudTexture != null) {
				    ScaledResolution scaledResolution = compatibility.getResolution(event);
	                int screenWidth = scaledResolution.getScaledWidth();
	                int screenHeight = scaledResolution.getScaledHeight();

	                ItemStack chestStack = compatibility.getItemStackFromSlot(CompatibleEntityEquipmentSlot.CHEST);
	                ItemStack feetStack = compatibility.getItemStackFromSlot(CompatibleEntityEquipmentSlot.FEET);

				    if (chestStack != null && helmetStack != null && feetStack != null
	                        && chestStack.getItem() instanceof CustomArmor
	                        && helmetStack.getItem() instanceof CustomArmor
	                        && feetStack.getItem() instanceof CustomArmor
	                        && ((CustomArmor)chestStack.getItem()).getUnlocalizedArmorSetName()
	                        .equals(((CustomArmor) helmetStack.getItem()).getUnlocalizedArmorSetName())
	                        && ((CustomArmor)chestStack.getItem()).getUnlocalizedArmorSetName()
	                        .equals(((CustomArmor) feetStack.getItem()).getUnlocalizedArmorSetName())
	                        ) {
	                    CustomArmor armor = (CustomArmor) chestStack.getItem();
	                    double maxShieldCapacity = armor.getMaxShieldCapacity();
	                    if(maxShieldCapacity > 0) {
	                        double currentShieldCapacity = armor.getShieldCapacity(chestStack);
	                        drawShieldIndicator(armor, CompatibleMathHelper.clamp_double(currentShieldCapacity / maxShieldCapacity, 0.0, 1.0), 
	                                screenWidth, screenHeight);
	                    }
	                }

				    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_LIGHTING);
			        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_BLEND);
//
//
					this.mc.renderEngine.bindTexture(new ResourceLocation(hudTexture));
//
					drawTexturedQuadFit(0, 0, screenWidth, screenHeight, -100);

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
				

				
//				GL11.glEnable(GL11.GL_ALPHA_TEST);
//				GL11.glEnable(GL11.GL_STENCIL_TEST);
//                
//                String shieldIndicatorTexture = "weaponlib:/com/vicmatskiv/weaponlib/resources/shield-indicator.png";
//                this.mc.renderEngine.bindTexture(new ResourceLocation(shieldIndicatorTexture));
//                
//                GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
//                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
//                GL11.glStencilMask(0xFF);
//                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                GL11.glEnable(GL11.GL_BLEND);
//
//                GL11.glColorMask(false, false, false, false);
//                GL11.glDepthMask(false);
//                GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)
//                
////                GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
//                drawTexturedQuadFit(15, 20, 100, 15, 0);
//
//                GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
//                GL11.glStencilMask(0x00);
//                GL11.glDepthMask(true);
//                GL11.glColorMask(true, true, true, true);
//
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDisable(GL11.GL_BLEND);
//				
//				GL11.glDisable(GL11.GL_ALPHA_TEST);
//				drawTexturedQuadFit(15, 20, 50, 15, 0);

//                GL11.glEnable(GL11.GL_ALPHA_TEST);
//                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                GL11.glEnable(GL11.GL_BLEND);
                
                this.mc.renderEngine.bindTexture(new ResourceLocation(crosshair));

//				if(weaponItem.isCrosshairFullScreen(itemStack))	 {
//					drawTexturedQuadFit(0, 0, width, height, 0);
//				} else {
//					drawTexturedModalRect(xPos, yPos, 0, 0, BUFF_ICON_SIZE, BUFF_ICON_SIZE);
//				}

				if(isInModifyingState(weaponInstance) /*Weapon.isModifying(itemStack)*/ /*weaponItem.getState(weapon) == Weapon.STATE_MODIFYING*/) {

				    String changeScopeMessage = compatibility.getLocalizedString(
				            "gui.attachmentMode.changeScope",
				            Keyboard.getKeyName(KeyBindings.upArrowKey.getKeyCode()));
				    fontRender.drawStringWithShadow(changeScopeMessage, width / 2 - 40, 60, color);

				    String changeBarrelRigMessage = compatibility.getLocalizedString(
                            "gui.attachmentMode.changeBarrelRig",
                            Keyboard.getKeyName(KeyBindings.leftArrowKey.getKeyCode()));
					fontRender.drawStringWithShadow(changeBarrelRigMessage, 10, height / 2 - 10, color);

					String changeCamoMessage = compatibility.getLocalizedString(
                            "gui.attachmentMode.changeCamo",
                            Keyboard.getKeyName(KeyBindings.rightArrowKey.getKeyCode()));
					fontRender.drawStringWithShadow(changeCamoMessage, width / 2 + 60, height / 2 - 20, color);

					String changeUnderBarrelRig = compatibility.getLocalizedString(
                            "gui.attachmentMode.changeUnderBarrelRig",
                            Keyboard.getKeyName(KeyBindings.downArrowKey.getKeyCode()));
					fontRender.drawStringWithShadow(changeUnderBarrelRig, 10, height - 40, color);

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
//				GL11.glDisable(GL11.GL_STENCIL_TEST);
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


    private void drawShieldIndicator(CustomArmor armor, double capacity, double screenWidth, double screenHeight) {
        
        if(!compatibility.isStencilEnabled(this.mc.getFramebuffer())) {
            compatibility.enableStencil(this.mc.getFramebuffer());
        }
        
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glStencilMask(0xFF);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(false);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear stencil buffer (0 by default)
        
        this.mc.renderEngine.bindTexture(new ResourceLocation(armor.getShieldIndicatorMaskTextureName()));
        
        // 640:328 
        // 427:240

        drawTexturedQuadFit((armor.getShieldIndicatorPositionX() - 1) * (screenWidth / 640.0), armor.getShieldIndicatorPositionY() * (screenHeight / 328.0), 
                armor.getShieldIndicatorWidth() * (screenWidth / 640.0) + 2, (armor.getShieldIndicatorHeight() + 1) * (screenHeight / 328.0), -101);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00);
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        
        this.mc.renderEngine.bindTexture(new ResourceLocation(armor.getShieldIndicatorProgressBarTextureName()));

        drawTexturedQuadFit((armor.getShieldIndicatorPositionX() - 1) * (screenWidth / 640.0), armor.getShieldIndicatorPositionY() * (screenHeight / 328.0), 
                ((armor.getShieldIndicatorWidth() + 2) * (screenWidth / 640.0)) * capacity, (armor.getShieldIndicatorHeight() + 1) * (screenHeight / 328.0), -101);
        
        GL11.glPopAttrib();
        
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
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

		String ammoCounterMessage = compatibility.getLocalizedString(
                "gui.ammoCounter", Tags.getAmmo(itemStack) + "/" + magazine.getAmmo());
		return ammoCounterMessage;
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
			text = compatibility.getLocalizedString("gui.noMagazine");
		} else {
			text = compatibility.getLocalizedString(
	                "gui.ammoCounter", weaponInstance.getWeapon().getCurrentAmmo(compatibility.clientPlayer()) + "/" + totalCapacity);
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
        //tessellator.startDrawingParticles();
        //tessellator.setColorRgba(1f, 1f, 1f, 1f);
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
		tessellator.draw();
	}
}
