package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBMultisample;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.NVMultisampleFilterHint;

import com.vicmatskiv.weaponlib.StatusMessageCenter.Message;
import com.vicmatskiv.weaponlib.animation.jim.BasicStateAnimator;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityEquipmentSlot;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGui;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTessellator;
import com.vicmatskiv.weaponlib.config.ConfigurationManager.StatusBarPosition;
import com.vicmatskiv.weaponlib.electronics.ItemWirelessCamera;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;
import com.vicmatskiv.weaponlib.render.ScreenRenderer;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.GearShiftPattern;
import com.vicmatskiv.weaponlib.vehicle.SimpleAnimationTimer;
import com.vicmatskiv.weaponlib.vehicle.collisions.Test;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Transmission;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CustomGui extends CompatibleGui {

    //    private static final int BUFF_ICON_SIZE = 256;

	private static final int STATUS_BAR_BOTTOM_OFFSET = 15;
	private static final int STATUS_BAR_TOP_OFFSET = 10;

    private Minecraft mc;
	private WeaponAttachmentAspect attachmentAspect;
	private ModContext modContext;
	private StatusBarPosition statusBarPosition;
	
	private FontRenderer niceFont = null;
	
	public static ModelBase keyModel;
	public static ModelBase lockModel;
	
	public static ResourceLocation keyTex;
	public static ResourceLocation lockTex;
	

	public static void setLockAndKeyModels(ModelBase key, ModelBase lock, ResourceLocation keyT, ResourceLocation lockT) {
		keyModel = key;
		lockModel = lock;
		keyTex = lockT;
		lockTex = keyT;
	}
	
	public CustomGui(Minecraft mc, ModContext modContext, WeaponAttachmentAspect attachmentAspect) {
		this.mc = mc;
		this.modContext = modContext;
		this.attachmentAspect = attachmentAspect;
		this.statusBarPosition = modContext.getConfigurationManager().getStatusBarPosition();
		
	}
	

	
	
	public void renderNeedle(Color c, int x, int y, int length, int width, float startAngle, float angle, float prevAngle) {
		float red = c.getRed()/255.0f;
		float blue = c.getBlue()/255.0f;
		float green = c.getGreen()/255.0f;
		float alpha = c.getAlpha()/255.0f;
		
		double tW = width/2.0;
		
		
		GL11.glPushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		
		
		double interpolatedAng = prevAngle + (angle-prevAngle)*Minecraft.getMinecraft().getRenderPartialTicks();
		
		GlStateManager.translate(x, y, 0.0);
		GlStateManager.rotate((float) interpolatedAng, 0, 0, 1);
		GlStateManager.rotate(startAngle, 0, 0, 1);
		GlStateManager.translate(-12, 0.0, 0.0);
		//GlStateManager.translate(-x, -y, 0.0);
		//GlStateManager.rotate(15, 1, 0, 0);
		
		
		//GlStateManager.translate(10, 0.0, 0.0);
		//GlStateManager.rotate((float) angle, 0, 0, 1);
		
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(-length, tW, 0).color(red, green, blue, alpha).endVertex();
		bb.pos(0, tW, 0).color(red, green, blue, alpha).endVertex();
		bb.pos(0, -tW, 0).color(red, green, blue, alpha).endVertex();
		bb.pos(-length, -tW, 0).color(red, green, blue, alpha).endVertex();
		
		
		
		
		t.draw();
		
		
		
		
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GlStateManager.disableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GL11.glPopMatrix();
		
	}
	
	public void renderHalfCircle(Color c, double x, double y, int outerRadius, int innerRadius, double beginAngle, double finishAngle) {
		
		
		
		float red = c.getRed()/255.0f;
		float blue = c.getBlue()/255.0f;
		float green = c.getGreen()/255.0f;
		float alpha = c.getAlpha()/255.0f;
		
		GL11.glPushMatrix();
		
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA_SATURATE, GL11.GL_ONE);
		
		//GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		/*
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glHint(NVMultisampleFilterHint.GL_MULTISAMPLE_FILTER_HINT_NV, GL11.GL_NICEST);
		System.out.println(GL11.glGetInteger(GL13.GL_SAMPLE_BUFFERS)); */
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		double endAng = 0;
		bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		for(double a = beginAngle; a < finishAngle; a += 6) {
			double cos = -Math.cos(Math.toRadians(a))*outerRadius;
			double sin = -Math.sin(Math.toRadians(a))*outerRadius;
			
			double cosI = -Math.cos(Math.toRadians(a))*innerRadius;
			double sinI = -Math.sin(Math.toRadians(a))*innerRadius;
			
			bb.pos(x+cos, y+sin, 0).color(red, green, blue, alpha).endVertex();
			bb.pos(x+cosI, y+sinI, 0).color(red, green, blue, 1.0f).endVertex();
			
			endAng = a;
		}
		
		if(endAng != finishAngle) {
			double cos = -Math.cos(Math.toRadians(finishAngle))*outerRadius;
			double sin = -Math.sin(Math.toRadians(finishAngle))*outerRadius;
			
			double cosI = -Math.cos(Math.toRadians(finishAngle))*innerRadius;
			double sinI = -Math.sin(Math.toRadians(finishAngle))*innerRadius;
			
			bb.pos(x+cos, y+sin, 0).color(red, green, blue, alpha).endVertex();
			bb.pos(x+cosI, y+sinI, 0).color(red, green, blue, 1.0f).endVertex();
		}
		
		
		t.draw();
		
		//GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GlStateManager.disableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GL11.glPopMatrix();
	}
	
	public float prevRPMAngle = 0.0f;
	public SimpleAnimationTimer sat = null;	
	public int oldPOV = 0;
	
	
	public int progess = 0;
	
	public void drawShiftPattern(EntityVehicle vehicle, int x, int y) {
		
		
		Transmission transmission = vehicle.solver.transmission;
		GearShiftPattern pattern = vehicle.getConfiguration().getPattern();
		
		//System.out.println(pattern + " | " + transmission + " | " + transmission.shiftTimer + " | " + transmission.maxShiftTime + " | " + transmission.startGear + " | " + transmission.targetGear);
		
		
		GL11.glPushMatrix();
		GlStateManager.enableAlpha();
		
		int old = 0;
		if(transmission.shiftTimer > 0) old = transmission.shiftTimer-1;
		
		Vec3d on2 = pattern.doAnimation(old, transmission.maxShiftTime, transmission.startGear, transmission.targetGear).scale(30.25);
		
		
		Vec3d oN = pattern.doAnimation(transmission.shiftTimer, transmission.maxShiftTime, transmission.startGear, transmission.targetGear).scale(30.25);
		
		oN = GearShiftPattern.interpVec3d(on2, oN, Minecraft.getMinecraft().getRenderPartialTicks());
		double nX = oN.z;
		double nZ = -oN.x;
		
		
		
		// render pattern
		
		GL11.glPushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		GL11.glTranslated(x+6.5, y+6.5, 0.0);
		GL11.glScaled(30.5, 30.5, 30.5);
		
		pattern.renderPattern(Color.decode("#d2dae2"), x, y);
	
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GL11.glPopMatrix();
		
		// enmd
		
		// render knob
		ResourceLocation loc = new ResourceLocation("mw" + ":" + "textures/gui/caricons.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		GL11.glTranslated(x+nX, y+nZ, 0);
		GL11.glScaled(0.8, 0.8, 0.8);
		drawTexturedModalRect(0, 0, 0, 0, 16, 16);
		
		
		
		
		
		
		
		GlStateManager.disableAlpha();
		GL11.glPopMatrix();
	}
	
	public void drawSpeedometer(EntityVehicle vehicle, int x, int y, int maxRPM, int gear, int rpm, double speed) {
		GL11.glPushMatrix();
		double scale = (new ScaledResolution(Minecraft.getMinecraft())).getScaledWidth()/640.0;
		GL11.glScaled(scale, scale, scale);
		x /= scale;
		y /= scale;
		
		
		
		
		
		
		if(sat == null) {
			sat = new SimpleAnimationTimer(150, false);
		}
		
		
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView != oldPOV) {
			oldPOV = Minecraft.getMinecraft().gameSettings.thirdPersonView;
			if(sat.isComplete()) sat.reset();
		}
		
		
		
		
		
		
		if(!sat.isComplete())sat.tick();
		
		
		
		
		
		
		float lowestAng = -45;
		float maxAng = 180;
		
		
		//rpm = 7000;
		float newRPMAngle = (float) (0.0 + ((maxAng+45)-0.0)*(rpm/(double) maxRPM));
		
		
		float prevRPMAngle = (float) (0.0 + ((maxAng+45)-0.0)*(vehicle.solver.prevRPM/(double) maxRPM));
		
		//float rpmAng = prevRPMAngle + (newRPMAngle-prevRPMAngle)*Minecraft.getMinecraft().getRenderPartialTicks();

		
		
		
		GlStateManager.disableDepth();
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
		
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GL11.glPushMatrix();
		
		
		GL11.glTranslated(x, y, 0);
		GL11.glScaled(0.85, 0.85, 0.85);
		
		
		int thousands = maxRPM/1000;
		double an = 360/(double) thousands;
		double radius = 50;
		double bA = -45;
		double eA = 180;
		for(int n = 0; n <= thousands; ++n) {
			double pA = (n*an)/360;
			double actualAngle = bA + (eA-bA)*pA;
			double cos = -Math.cos(Math.toRadians(actualAngle))*radius;
			double sin = -Math.sin(Math.toRadians(actualAngle))*radius;
			int color = 0;
			if(n < 6) {
				color = 0xFFFFFF;
			} else {
				color = 0xc0392b;
			}
			
			drawCenteredString(Minecraft.getMinecraft().fontRenderer, "" + n, (int) cos, (int) sin-4, color);
		}
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		
		renderHalfCircle(Color.decode("#FFFFFF"), x, y, 50, 48, lowestAng, sat.smoothInterpDouble(-45, 133));
		renderHalfCircle(Color.decode("#c23616"), x, y, 50, 48, 135, sat.smoothInterpDouble(135, maxAng));
		renderHalfCircle(Color.decode("#4cd137"), x, y, 10, 9, 0, sat.smoothInterpDouble(0, 360));
		
		// test
		/*
		progess += 1;
		if(progess > 150) progess = 0;
		
		Vec3d oN = Test.STANDARD_SIX_SHIFT.doAnimation(progess, 150, 1, 3).scale(50);
		
		double nX = oN.z;
		double nZ = -oN.x;
		
		renderHalfCircle(Color.decode("#4cd137"), x-250+nX, y-50+nZ, 5, 0, 0, sat.smoothInterpDouble(0, 360));
		*/
		//
		
		/*
		GL11.glPushMatrix();
		GL11.glScaled(1.0, 1.0, 1.0);
			GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
			drawShiftPattern(vehicle, x-125, y);
			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		GL11.glPopMatrix();
		*/
		
		
		
		Transmission transmission = vehicle.solver.transmission;
		GearShiftPattern pattern = vehicle.getConfiguration().getPattern();
		renderHalfCircle(Color.decode("#7f8fa6"), x, y, 12, 11, lowestAng, sat.smoothInterpDouble(lowestAng, maxAng+4));
		renderNeedle(Color.decode("#FFFFFF"), x, y, 35, 1, lowestAng, newRPMAngle, prevRPMAngle);
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		
		
		
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 0);
		GL11.glScaled(1.5, 1.5, 1.5);
		
		
		if(transmission.isReverseGear) {
			drawCenteredString(Minecraft.getMinecraft().fontRenderer, "R", 0, -4, 0x10ac84);
		} else {
			drawCenteredString(Minecraft.getMinecraft().fontRenderer, "" + gear, 0, -4, 0x4cd137);
		}
		
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		
		GL11.glTranslated(x, y, 0);
		GL11.glScaled(2.0, 2.0, 2.0);
		int fixedSpeed = (int) Math.round(speed*3.6);
		drawCenteredString(Minecraft.getMinecraft().fontRenderer, "" + fixedSpeed, 20, 2, 0xc8d6e5);
		
		GL11.glPushMatrix();
		GL11.glScaled(0.4, 0.4, 0.4);
		drawCenteredString(Minecraft.getMinecraft().fontRenderer, "km/h", 53, 25, 0xc8d6e5);
		
		GL11.glPopMatrix();
		
		GL11.glPopMatrix();
		
		
		
		
		
		GL11.glPopMatrix();
		
		
		
		
		this.prevRPMAngle = newRPMAngle;
		
		
	}
	
	public BasicStateAnimator kA = null;

	@Override
	public void onCompatibleRenderHud(RenderGameOverlayEvent.Pre event) {
	    
		if(compatibility.getEventType(event) == RenderGameOverlayEvent.ElementType.HELMET) {
		
		}
		
		if(compatibility.getEventType(event) == RenderGameOverlayEvent.ElementType.HELMET && this.mc.player.isRiding() && this.mc.player.getRidingEntity() instanceof EntityVehicle) {
			
			
			
			EntityVehicle vehicle = (EntityVehicle) this.mc.player.getRidingEntity();
			
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			int width = sr.getScaledWidth();
			int height = sr.getScaledHeight();
			
			
			GL11.glPushMatrix();

			double pM = Math.sin(vehicle.ticksExisted/2)*3;
			double ppM = Math.sin((vehicle.ticksExisted-1)/2)*3;
			double iPM = InterpolationKit.interpolateValue(ppM, pM, Minecraft.getMinecraft().getRenderPartialTicks());
			
			//kA = null;
			if(kA == null) {
				kA = new BasicStateAnimator();
				kA.transition(new Vec3d(0.0, 0.0, 0.0),
							  new Vec3d(0.0, 0, 0), 1.0);
				
				kA.transition(new Vec3d(20.0, 0.0, 0.0),
						  new Vec3d(0.0, 0, 0), 50.0);
				
				kA.addPause(20);
				
				kA.transition(new Vec3d(20.0, 0.0, 0.0),
						  new Vec3d(0.0, 0, 0.0), 50.0);
			}
			
			
			
			kA.tick();
			
			
			
			//kA.transition(new Vec3d(5.0, 0.0, 0.0),
			//		  new Vec3d(00.0, 0, 30), 200.0);
	
			
			/*
			
			GL11.glTranslated(width-110, height-75, -50.0);
			
			//GL11.glRotated(90, 0, 1, 0);
			GL11.glRotated(-150, 0.0, 1.0, 0.0);
			GL11.glRotated(25, 1.0, 0.0, 0.0);
			GL11.glRotated(25, 0.0, 0.0, 1.0);
			ScreenRenderer.renderModelOnScreen(20.0, 0.0, 0.0, 30, 0f, 0f, 0f, lockModel, keyTex);
			
			
			
			Vec3d iR = kA.getInterpolatedRotation();
			Vec3d iP = kA.getInterpolatedPosition();
			//GL11.glRotated(45, 1, 0, 0);
			ScreenRenderer.renderModelOnScreen(30.0 + iP.x, -5.0+ iP.y, -65.0+ iP.z, 30, 90f + iR.x, 0f + iR.y, 0f + iR.z, keyModel, lockTex);
			
			*/
			
			//GuiInventory.drawEntityOnScreen(320, 169, 30, 30, 30, Minecraft.getMinecraft().player);
			
			GL11.glPopMatrix();

			
			
			
			double h = width/640.0;
			GL11.glPushMatrix();
			
			//GL11.glScaled(h, h, h);
			drawSpeedometer(vehicle, (width-60), (height-60), 7000, vehicle.solver.transmission.getCurrentGear(), vehicle.solver.currentRPM, vehicle.getRealSpeed());
			
			GL11.glPopMatrix();
			//drawCenteredString(Minecraft.getMinecraft().fontRenderer, "RPM: " + vehicle.solver.currentRPM, 50, 50, 49333);
			//drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Gear: " + vehicle.solver.transmission.getCurrentGear(), 50, 60, 49333);
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
				

				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDisable(GL11.GL_BLEND);
                
                this.mc.renderEngine.bindTexture(new ResourceLocation(crosshair));

                if (isInAltModifyingState(weaponInstance)) {

                    String changeScopeMessage = compatibility.getLocalizedString(
                            "gui.attachmentMode.changeRailing",
                            Keyboard.getKeyName(KeyBindings.upArrowKey.getKeyCode()));
                    fontRender.drawStringWithShadow(changeScopeMessage, width / 2 - 40, 60, color);

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
		return (weaponInstance.getState() == WeaponState.MODIFYING && !weaponInstance.isAltMofificationModeEnabled())
				|| weaponInstance.getState() == WeaponState.MODIFYING_REQUESTED
				|| weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT
				|| weaponInstance.getState() == WeaponState.NEXT_ATTACHMENT_REQUESTED;
	}
	
	private boolean isInAltModifyingState(PlayerWeaponInstance weaponInstance) {
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
