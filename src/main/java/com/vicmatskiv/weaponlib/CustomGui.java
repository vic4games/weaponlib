package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBMultisample;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GLSync;
import org.lwjgl.opengl.NVMultisampleFilterHint;

import com.vicmatskiv.weaponlib.StatusMessageCenter.Message;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.FlaggedAttachment;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.gui.AnimationGUI;
import com.vicmatskiv.weaponlib.animation.gui.Button;
import com.vicmatskiv.weaponlib.animation.jim.BasicStateAnimator;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityEquipmentSlot;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGui;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTessellator;
import com.vicmatskiv.weaponlib.config.BalancePackManager;
import com.vicmatskiv.weaponlib.config.ConfigurationManager.StatusBarPosition;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;
import com.vicmatskiv.weaponlib.electronics.ItemWirelessCamera;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.render.ModificationGUI;
import com.vicmatskiv.weaponlib.render.ModificationGUI.ModificationGroup;
import com.vicmatskiv.weaponlib.render.ScreenRenderer;
import com.vicmatskiv.weaponlib.render.bgl.PostProcessPipeline;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.GearShiftPattern;
import com.vicmatskiv.weaponlib.vehicle.SimpleAnimationTimer;
import com.vicmatskiv.weaponlib.vehicle.VehicleCustomGUI;
import com.vicmatskiv.weaponlib.vehicle.collisions.Test;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Transmission;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CustomGui extends CompatibleGui {

    //    private static final int BUFF_ICON_SIZE = 256;

	private static final int STATUS_BAR_BOTTOM_OFFSET = 15;
	private static final int STATUS_BAR_TOP_OFFSET = 10;

    private Minecraft mc;
	private WeaponAttachmentAspect attachmentAspect;
	private ModContext modContext;
	private StatusBarPosition statusBarPosition;
	
	private FontRenderer niceFont = null;
	
	public static VehicleCustomGUI vehicleGUIOverlay = new VehicleCustomGUI();
	
	public static final ResourceLocation AMMO_COUNTER_TEXTURES = new ResourceLocation("mw:textures/hud/ammoiconsheet.png");

	
	public CustomGui(Minecraft mc, ModContext modContext, WeaponAttachmentAspect attachmentAspect) {
		this.mc = mc;
		this.modContext = modContext;
		this.attachmentAspect = attachmentAspect;
		this.statusBarPosition = modContext.getConfigurationManager().getStatusBarPosition();
		
	}
	

	
	

	@Override
	public void onCompatibleRenderHud(RenderGameOverlayEvent.Pre event) {
		
		
		if(compatibility.getEventType(event) == ElementType.HELMET) {
			
			
			
			
			//PostProcessPipeline.drawRainBuffer();
			//System.out.println("hi");
			//drawString(mc.fontRenderer, "FUCKL", 30, 30, 0xff113a);
		//	System.out.println("hi");
		}
	//	drawGradientRect(0, 0, 30, 30, 0xdfffff, 0xfffff);
		
		//PostProcessPipeline.drawRainBuffer();
		
		
		// animation on
		if(AnimationModeProcessor.getInstance().getFPSMode()) {
			event.setCanceled(true);

			GlStateManager.disableTexture2D();
		
			AnimationGUI.getInstance().render();
			
			
			if(AnimationGUI.getInstance().titleSafe.isState()) {
				DebugRenderer.setupBasicRender();
				ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
				DebugRenderer.renderPoint(new Vec3d(sr.getScaledWidth_double()/2, sr.getScaledHeight_double()/2, 0), new Vec3d(1, 0, 0));
				
				DebugRenderer.destructBasicRender();
			}
			
			
		}
		
		/*
		GlStateManager.disableTexture2D();
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		
		bb.pos(0, 0, 0).endVertex();
		bb.pos(0, 50, 0).endVertex();
		bb.pos(50, 50, 0).endVertex();
		bb.pos(50, 0, 0).endVertex();
		
		t.draw();
		*/
		
		//GlStateManager.color(1f, 0f, 0f, 0f);
		
		
		
		
		if(this.mc.player.isRiding() && this.mc.player.getRidingEntity() instanceof EntityVehicle) {
			EntityVehicle v = (EntityVehicle) this.mc.player.getRidingEntity();
			
			if(!Double.isNaN(v.getSolver().getVelocityVector().lengthSquared()) && v.getSolver().getVelocityVector().lengthSquared() != 0.0) {
				if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) event.setCanceled(true);
				if(event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) event.setCanceled(true);
				if(event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) event.setCanceled(true);
				if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) event.setCanceled(true);
				if(event.getType() == RenderGameOverlayEvent.ElementType.ARMOR) event.setCanceled(true);
				
			}
			
			
		}
	    
		
		if(compatibility.getEventType(event) == RenderGameOverlayEvent.ElementType.HELMET && this.mc.player.isRiding() && this.mc.player.getRidingEntity() instanceof EntityVehicle) {
			
			
			
			EntityVehicle vehicle = (EntityVehicle) this.mc.player.getRidingEntity();
			vehicleGUIOverlay.renderGUI(vehicle);
		}
		
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
		
		
		 //GlStateManager.disableAlpha();
		 
	
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

			    FontRenderer fontRender = compatibility.getFontRenderer();

				mc.entityRenderer.setupOverlayRendering();

				int color = 0xFFFFFF;
			

				//GlStateManager.pushAttrib();
				//GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				//GL11.glDisable(GL11.GL_LIGHTING);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		      //  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableBlend();
                
                this.mc.renderEngine.bindTexture(new ResourceLocation(crosshair));

                
                
                
                if (isInAltModifyingState(weaponInstance)) {

                	//ModificationGUI.getInstance().setGroup(ModificationGroup.MODIFICATION);
                	ModificationGUI.getInstance().render(modContext);
                	
                	/*
                    String changeScopeMessage = compatibility.getLocalizedString(
                            "gui.attachmentMode.changeRailing",
                            Keyboard.getKeyName(KeyBindings.upArrowKey.getKeyCode()));
                    fontRender.drawStringWithShadow(changeScopeMessage, width / 2 - 40, 60, color);
*/
//                    String changeBarrelRigMessage = compatibility.getLocalizedString(
//                            "gui.attachmentMode.changeBarrelRig",
//                            Keyboard.getKeyName(KeyBindings.leftArrowKey.getKeyCode()));
//                    fontRender.drawStringWithShadow(changeBarrelRigMessage, 10, height / 2 - 10, color);
//
//                    String changeCamoMessage = compatibility.getLocalizedString(
//                            "gui.attachmentMode.changeCamo",
//                            Keyboard.getKeyName(KeyBindings.rightArrowKey.getKeyCode()));
//                    fontRender.drawStringWithShadow(changeCamoMessage, width / 2 + 60, height / 2 - 20, color);
//
//                    String changeUnderBarrelRig = compatibility.getLocalizedString(
//                            "gui.attachmentMode.changeUnderBarrelRig",
//                            Keyboard.getKeyName(KeyBindings.downArrowKey.getKeyCode()));
//                    fontRender.drawStringWithShadow(changeUnderBarrelRig, 10, height - 40, color);
//                    
//                    String applyLaser = compatibility.getLocalizedString(
//                            "gui.attachmentMode.applyLaser",
//                            Keyboard.getKeyName(KeyBindings.laserAttachmentKey.getKeyCode()));
//                    fontRender.drawStringWithShadow(applyLaser, 150, height - 100, color);

                } else if(isInModifyingState(weaponInstance) /*Weapon.isModifying(itemStack)*/ /*weaponItem.getState(weapon) == Weapon.STATE_MODIFYING*/) {

                	
                //	ModificationGUI.getInstance().setGroup(ModificationGroup.ATTACHMENT);
                	ModificationGUI.getInstance().render(modContext);
                	/*
                	GlStateManager.pushMatrix();
                	GlStateManager.scale(4, 4, 4);
                	ArrayList<FlaggedAttachment> testList = modContext.getAttachmentAspect().getInventoryAttachments(AttachmentCategory.SCOPE, weaponInstance);
                 	if(testList != null) {
                 		int i = 0;
                 		for(FlaggedAttachment attach : testList) {
                 			RenderHelper.enableGUIStandardItemLighting();
                    		mc.getRenderItem().renderItemIntoGUI(attach.getAttachment().getDefaultInstance(), 30 + i, 30);
                    		RenderHelper.disableStandardItemLighting();
                    		
                    		if(attach.requiresAnyParts()) {
                    			double y = 0;
                    			for(ItemAttachment<Weapon> requirement : attach.getRequiredParts()) {
                    				GlStateManager.pushMatrix();
                        			GlStateManager.translate(37.5 + i, 45 + y, 0);
                        			
                        			GlStateManager.scale(0.2, 0.2, 0.2);
                        			
                        		
                        			
                        			String string = new TextComponentTranslation(requirement.getUnlocalizedName() + ".name", new Object[0]).getFormattedText();
                        			//String string = I18n.translateToLocal(requirement.getUnlocalizedName() + ".name");
                        			
                        			drawCenteredString(mc.fontRenderer, string, 0, 0, 0xfffff);
                        			GlStateManager.popMatrix();
                        			y += 2;
                    			}
                    			
                    		}
                    		
                    		i += 20;
                 		}
                 	}
                 	
                 	
                 	
                 	GlStateManager.popMatrix();
                	*/
               
                	//mc.getRenderItem().renderItemIntoGUI(mc.player.getHeldItemMainhand(), 30, 30);
                	
                	/*
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
					
					String applyLaser = compatibility.getLocalizedString(
                            "gui.attachmentMode.applyLaser",
                            Keyboard.getKeyName(KeyBindings.laserAttachmentKey.getKeyCode()));
                    fontRender.drawStringWithShadow(applyLaser, 150, height - 100, color);
					*/
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


					//fontRender.drawStringWithShadow(messageText, x, y, color);
					
					GlStateManager.enableBlend();
					
					// Set up GUI positioning
					GlStateManager.pushMatrix();
					double scale = 0.15 * scaledResolution.getScaleFactor();
					GlStateManager.translate((scaledResolution.getScaledWidth_double()-256*scale), (scaledResolution.getScaledHeight_double()-128*scale), 0);
					GlStateManager.scale(scale, scale, scale);
					Minecraft.getMinecraft().getTextureManager().bindTexture(AMMO_COUNTER_TEXTURES);
					
					// Figure out the firemode, and assign it an ID
					int firemode = 0;
					if(weaponInstance.getMaxShots() == Integer.MAX_VALUE) {
						firemode = 2;
					} else if(weaponInstance.getMaxShots() == 1) {
						firemode = 0;
					} else {
						firemode = 1;
					}
					
					// Check the total capacity, this allows us to differentiate b/w
					// cartridge based weapons, and allows us to tell if a weapon has no
					// magazine in it.
					ItemMagazine magazine = (ItemMagazine) attachmentAspect.getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance);
					int totalCapacity;
					if(magazine != null) {
						totalCapacity = magazine.getAmmo();
					} else {
						totalCapacity = weaponInstance.getWeapon().getAmmoCapacity();
					}
					
					// If there is no magazine, display two "-"
					String totalCapaString, currentAmmo;
					if(weaponInstance.getWeapon().getAmmoCapacity() == 0 && totalCapacity == 0) {
						totalCapaString = "-";
						currentAmmo = "-";
					} else {
						currentAmmo = weaponInstance.getAmmo() + "";
						totalCapaString = totalCapacity + "";
					}
				
					String keyNameString = "[" + KeyBindings.fireModeKey.getDisplayName() + "]";
					double keyNameOffset = fontRender.getStringWidth(keyNameString);
					
					// Render main ammo counter body
					drawTexturedModalRect(0, 0, 0, 0, 256, 53);
					
					
					// Draw the firemode indicator
					GlStateManager.pushMatrix();
					GlStateManager.translate(256 - 90 - (keyNameOffset*1.75), 60, 0);
					GlStateManager.scale(0.7, 0.7, 0.7);
					drawTexturedModalRect(0, 0, 146 + 39*(2-firemode), 53, 39, 28);
					GlStateManager.popMatrix();
					
					// Get the weapon name from the localization file
					String weaponName = new TextComponentTranslation(weaponItem.getUnlocalizedName() + ".name").getFormattedText();

					
					
					String bottomString = "   " + TextFormatting.GRAY + " | " + TextFormatting.WHITE + "" + totalCapaString;
							double totalLength = 0;
							
							
					
					// Fixes length in cases of minigun		
					if(bottomString.length() > 13) {
						int adjLength = bottomString.length() - 13;
						totalLength = adjLength*8.5;
					}
					
				
					//String keyNameString = "[" + KeyBindings.fireModeKey.getDisplayName() + "]";
							
					// Draw strings
					drawScaledString(fontRender, weaponName, 126 - fontRender.getStringWidth(weaponName), -fontRender.FONT_HEIGHT - 2, 2.0, 0xffea8a);
					drawScaledString(fontRender, currentAmmo, 64 + 20 - fontRender.getStringWidth(currentAmmo)*2 - totalLength, 53/8.0 - 1, 3.5, 0xffea8a);
					drawScaledString(fontRender, bottomString, 64 - totalLength, 53/8.0, 3.0);
					drawScaledString(fontRender, keyNameString, 105 - keyNameOffset, 30, 2.0, 0xffea8a);
					
					
	
					GlStateManager.popMatrix();
				
				}
                
                int x = getStatusBarXPosition(width, "Weapon disabled", fontRender);
				int y = getStatusBarYPosition(height);

                
                if(BalancePackManager.isWeaponDisabled(weaponInstance.getWeapon())) {
                	fontRender.drawStringWithShadow("Weapon disabled", x - 5, y + 10, 0xc23616);
					
                }
                
               //GlStateManager.popAttrib();
                
			//	GL11.glPopAttrib();
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

	public void drawScaledString(FontRenderer fr, String str, double x, double y, double scale, int color) {
		
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, scale);
		
		fr.drawStringWithShadow(str, (float) (x/scale), (float) (y/scale), color);
		GlStateManager.popMatrix();
	}
	
	public void drawScaledString(FontRenderer fr, String str, double x, double y, double scale) {
		drawScaledString(fr, str, x, y, scale, 0xffffff);
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
        
        GlStateManager.disableAlpha();
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


	public static boolean isInModifyingState(PlayerWeaponInstance weaponInstance) {
		return (weaponInstance.getState() == WeaponState.MODIFYING && !weaponInstance.isAltMofificationModeEnabled())
				|| weaponInstance.getState() == WeaponState.MODIFYING_REQUESTED
				|| weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT
				|| weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT_REQUESTED;
	}
	
	public static boolean isInAltModifyingState(PlayerWeaponInstance weaponInstance) {
        return weaponInstance.isAltMofificationModeEnabled()
                && (weaponInstance.getState() == WeaponState.MODIFYING
                || weaponInstance.getState() == WeaponState.MODIFYING_REQUESTED
                || weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT
                || weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT_REQUESTED);
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
