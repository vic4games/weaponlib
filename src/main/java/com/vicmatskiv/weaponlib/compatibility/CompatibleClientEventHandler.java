package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.glu.Project;

import com.vicmatskiv.weaponlib.ClientModContext;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.WeaponRenderer;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;
import com.vicmatskiv.weaponlib.model.Bullet556;
import com.vicmatskiv.weaponlib.particle.DriftCloudFX;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.render.Dloom;
import com.vicmatskiv.weaponlib.render.ShellRenderer;
import com.vicmatskiv.weaponlib.render.ShellRenderer2;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;
import com.vicmatskiv.weaponlib.sound.JSoundEngine;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.collisions.OBBCollider;
import com.vicmatskiv.weaponlib.vehicle.collisions.OreintedBB;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleControlPacket;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleDataContainer;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleInteractPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CompatibleClientEventHandler {

	private Entity origRenderVeiwEntity;

	public static boolean freecamEnabled = false;
	public static boolean freecamLock = false;
	public static boolean muzzlePositioner = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void updateFOV(FOVUpdateEvent e) {

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null || !player.isRiding() || !(player.getRidingEntity() instanceof EntityVehicle))
			return;
		EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();

		double fA = (vehicle.getSolver().getSyntheticAcceleration() / 55 + (vehicle.getRealSpeed() / 120)) * 0.2;
		// System.out.println(fA);

		e.setNewfov((float) (e.getFov() + fA));

	}

	@SubscribeEvent
	public void keyInputEvent(KeyboardInputEvent kie) {
		
		if(Keyboard.isKeyDown(Keyboard.KEY_HOME)) {
			System.out.println("bro");
			freecamLock = !freecamLock;
		}
	}

	@SubscribeEvent
	public final void properCameraSetup(EntityViewRenderEvent.CameraSetup e) {
		EntityPlayer player = compatibility.getClientPlayer();

		if (player.isRiding() && player.getRidingEntity() instanceof EntityVehicle
				&& Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
			// vehicle.rotationPitch = 30f;

			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {

				// GL11.glRotated(-45, 1.0, 0.0, 0.0);
				// GL11.glTranslated(player.posX, player.posY, player.posZ);

				// GL11.glTranslated(-player.posX, -player.posY, -player.posZ);

				// e.setRoll(-(vehicle.rotationRoll + vehicle.rotationRollH));
				// e.setPitch(-vehicle.rotationPitch);
				// GL11.glTranslated(0.0, -0.9, -.8);
			}

		}
		// System.out.println("hi");
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && freecamEnabled && !freecamLock) {
			// System.out.println("hi");
			e.setYaw((float) (freeYaw));
			e.setPitch((float) (freePitch));
		} else {
			freeYaw = 0;
			freePitch = 0;
		}
		freeYaw += yawDelta;
		freePitch += pitchDelta;

		yawDelta = 0;
		pitchDelta = 0;
	}

	public static double freeYaw = 0;
	public static double freePitch = 0;

	public static double yawDelta = 0;
	public static double pitchDelta = 0;

	public static Vec3d debugmuzzlePosition = new Vec3d(0, -1, -6.5);

	public static HashMap<Integer, Stack<Boolean>> muzzleFlashMap = new HashMap<>();

	public static boolean checkShot(int entityID) {
		if (muzzleFlashMap.isEmpty() || !muzzleFlashMap.containsKey(entityID)
				|| (muzzleFlashMap.get(entityID).isEmpty())) {
			return false;
		} else {
			muzzleFlashMap.get(entityID).pop();
			return true;

		}
	}

	public static void uploadFlash(int entityID) {
		if (muzzleFlashMap.containsKey(entityID)) {
			muzzleFlashMap.get(entityID).push(true);
		} else {
			Stack<Boolean> stack = new Stack<>();
			stack.push(true);
			muzzleFlashMap.put(entityID, stack);
		}

	}

	public static Stack<MuzzleFlash> muzzleFlashStack = new Stack<>();
	public static ResourceLocation FLASH = new ResourceLocation("mw" + ":" + "textures/flashes/flash1.png");
	public static ResourceLocation FLASHF = new ResourceLocation("mw" + ":" + "textures/flashes/flashfront2.png");

	public static class MuzzleFlash {
		public Vec3d pos;
		public float yaw;
		public Vec3d thirdPersonPos;
		public float pitch;
		public double scale;

		public MuzzleFlash(Vec3d pos, Vec3d thirdPersonPos, float yaw, float pitch, double scale) {
			this.pos = pos;
			this.thirdPersonPos = thirdPersonPos;
			this.yaw = yaw;
			this.pitch = pitch;
			this.scale = scale;

		}
	}

	@SubscribeEvent
	public void mouseMove(MouseEvent me) {

	}

	public static Shader blur = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/blur"));
	public static Framebuffer buf;

	public static void postBlur() {
		// Dloom.bloom_h = ShaderManager.loadShader(new ResourceLocation("mw" + ":" +
		// "shaders/bloom_h"));
		// Dloom.bloom_v = ShaderManager.loadShader(new ResourceLocation("mw" + ":" +
		// "shaders/bloom_v"));

		// blur = ShaderManager.loadShader(new ResourceLocation("mw" + ":" +
		// "shaders/blur"));

		int width = Minecraft.getMinecraft().displayWidth;
		int height = Minecraft.getMinecraft().displayHeight;
		if (buf == null) {
			// blur
			if (CompatibleClientEventHandler.buf != null)
				CompatibleClientEventHandler.buf.deleteFramebuffer();
			CompatibleClientEventHandler.buf = new Framebuffer(Minecraft.getMinecraft().displayWidth,
					Minecraft.getMinecraft().displayHeight, false);

		}

		GL11.glPushMatrix();

		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		/*
		 * blur.use(); GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+5);
		 * //Minecraft.getMinecraft().getTextureManager().bindTexture(new
		 * ResourceLocation(Reference.MOD_ID + ":" + "textures/shaders/noise_1.png"));
		 * GL11.glBindTexture(GL11.GL_TEXTURE_2D, Dloom.depthTexture);
		 * GL20.glUniform1i(GL20.glGetUniformLocation(blur.getShaderId(), "depth"), 5);
		 * GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		 * 
		 * blur.uniform2f("res", Minecraft.getMinecraft().displayWidth,
		 * Minecraft.getMinecraft().displayHeight);
		 * 
		 * Bloom.renderFboTriangle(Minecraft.getMinecraft().getFramebuffer(), width,
		 * height); //Minecraft.getMinecraft().getFramebuffer().framebufferRender(buf.
		 * framebufferWidth, buf.framebufferHeight); blur.release();
		 */

		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + 5);
		// Minecraft.getMinecraft().getTextureManager().bindTexture(new
		// ResourceLocation(Reference.MOD_ID + ":" + "textures/shaders/noise_1.png"));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Dloom.depthTexture);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

		for (int x = 0; x < 3; ++x) {
			Dloom.bloom_h.use();
			GL20.glUniform1i(GL20.glGetUniformLocation(Dloom.bloom_h.getShaderId(), "depth"), 5);

			GL20.glUniform1f(GL20.glGetUniformLocation(Dloom.bloom_h.getShaderId(), "frag_width"),
					1F / Minecraft.getMinecraft().getFramebuffer().framebufferWidth);
			Bloom.renderFboTriangle(Minecraft.getMinecraft().getFramebuffer(), width, height);

			Dloom.bloom_v.use();
			GL20.glUniform1i(GL20.glGetUniformLocation(Dloom.bloom_v.getShaderId(), "depth"), 5);

			GL20.glUniform1f(GL20.glGetUniformLocation(Dloom.bloom_v.getShaderId(), "frag_height"),
					1F / Minecraft.getMinecraft().getFramebuffer().framebufferHeight);
			Bloom.renderFboTriangle(Minecraft.getMinecraft().getFramebuffer(), width, height);

		}

		Dloom.bloom_v.release();
		Dloom.bloom_h.release();

		// GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buf.framebufferObject);
		// GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,
		// Minecraft.getMinecraft().getFramebuffer().framebufferObject);
//		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		GlStateManager.enableDepth();
		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void livingUpdateEvent(LivingUpdateEvent evt) {
		if (evt.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) evt.getEntityLiving();

		}
	}

	public static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
	public static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
	public static final FloatBuffer NEW_POS = GLAllocation.createDirectFloatBuffer(4);

	@SubscribeEvent
	public void renderWorrldLastEvent(RenderWorldLastEvent evt) {

		if (ClientModContext.getContext().getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE)
			return;

		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
		GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);

		Project.gluUnProject(WeaponRenderer.POSITION.get(0), WeaponRenderer.POSITION.get(1), WeaponRenderer.POSITION.get(2), MODELVIEW, PROJECTION, VIEWPORT,
				NEW_POS);
		
		Vec3d newPV = new Vec3d(NEW_POS.get(0), NEW_POS.get(1), NEW_POS.get(2));
		
		DebugRenderer.setupBasicRender();
		//DebugRenderer.renderPoint(newPV, new Vec3d(1, 0, 0));
		DebugRenderer.destructBasicRender();
		
		
		GlStateManager.pushMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		int i = Minecraft.getMinecraft().player.world.getCombinedLight(new BlockPos(NEW_POS.get(0), NEW_POS.get(1), NEW_POS.get(2)), 0);
        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 125, 125);
		//GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mw" + ":" + "textures/models/bullet556.png"));
		GlStateManager.translate(newPV.x, newPV.y, newPV.z);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)f, (float)f1);
		GlStateManager.disableTexture2D();
		
		/*
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
		
		bb.pos(0, 0, 0).endVertex();
		bb.pos(0, 1, 0).endVertex();
		bb.pos(0, 0, 1).endVertex();
		
		t.draw();*/
		
		PROJECTION.rewind();
		MODELVIEW.rewind();
		
		GlStateManager.disableTexture2D();
		//GL11.glClearColor(1, 0, 0, 1);
		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	//	GlStateManager.scale(0.1, 0.1, 0.1);
		/*
		ShellRenderer2.init();
		GlStateManager.color(1.0f, 0.0f, 0.0f);
		ShellRenderer2.realRender();
		*/
	
		//ShellRenderer.init();
		//ShellRenderer.use();
		/*
		ShellRenderer.init();
		ShellRenderer.use();
		*/
		//(new Bullet556()).render(null, 0f, 0f, 0f, 0f, 0f, 0.05f);
		GlStateManager.popMatrix();

		//System.out.println(NEW_POS.get(0) + " | " + NEW_POS.get(1) + " | " + NEW_POS.get(2));

		ClientValueRepo.update();

		try {
			Bloom.doBloom();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// JSoundEngine jse = JSoundEngine.getInstance();

		// JSoundEngine.getInstance().killALData();

		// GlStateManager.enableDepth();
		//

		// Dloom.doPost();

		// GlStateManager.enableDepth();
		// Bloom.use();
		/*
		 * if(1+1==2) return; Minecraft mc = Minecraft.getMinecraft(); EntityPlayer p =
		 * mc.player; GL11.glPointSize(2f); // GlStateManager.disableTexture2D();
		 * GlStateManager.enableBlend(); GlStateManager.enableAlpha();
		 * 
		 * 
		 * Vec3d bruh = new Vec3d(-0.13, 0, 1.5).rotatePitch((float)
		 * Math.toRadians(-p.rotationPitch)).rotateYaw((float)
		 * Math.toRadians(-p.rotationYaw)); //bruh = Vec3d.ZERO;
		 * //GlStateManager.disableTexture2D(); GL11.glBegin(GL11.GL_POINTS);
		 * GL11.glVertex3d(bruh.x, bruh.y+1.5, bruh.z); GL11.glEnd();
		 * 
		 * 
		 * // if(p.ticksExisted%10 != 0) return;
		 * 
		 * // GlStateManager.enableDepth(); GlStateManager.disableCull();
		 * //GlStateManager.disableDepth();
		 * 
		 * double ix = p.lastTickPosX + (p.posX - p.lastTickPosX) *
		 * mc.getRenderPartialTicks(); double iy = p.lastTickPosY + (p.posY -
		 * p.lastTickPosY) * mc.getRenderPartialTicks(); double iz = p.lastTickPosZ +
		 * (p.posZ - p.lastTickPosZ) * mc.getRenderPartialTicks();
		 * 
		 * GlStateManager.color(1f, 1f, 1f, 0.99f); GlStateManager.translate(-ix, -iy,
		 * -iz);
		 * 
		 * // GlStateManager.translate(-20, 5, 29);
		 * 
		 * //GlStateManager.translate(0, 1.5, 0);
		 * GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		 * mc.getTextureManager().bindTexture(FLASH);
		 * 
		 * double xOffset = -0.05; double yOffset = -0.06; double distance = 3.0; double
		 * fposX = ix + (p.getLookVec().x * distance) +
		 * (compatibility.world(p).rand.nextFloat() * 2.0f - 1) * 0 + (-p.getLookVec().z
		 * * xOffset); double fposY = iy + (p.getLookVec().y * distance) +
		 * (compatibility.world(p).rand.nextFloat() * 2.0f - 1) * 0 - yOffset; double
		 * fposZ = iz + (p.getLookVec().z * distance) +
		 * (compatibility.world(p).rand.nextFloat() * 2.0f - 1) * 0 + (p.getLookVec().x
		 * * xOffset);
		 * 
		 * MuzzleFlash flash = new MuzzleFlash(new Vec3d(fposX, fposY, fposZ),
		 * Vec3d.ZERO, p.rotationYaw, p.rotationPitch, 1.0);
		 * 
		 * //muzzleFlashStack.push(flash);
		 * //System.out.println(muzzleFlashStack.size());
		 * GlStateManager.depthMask(false); while(!muzzleFlashStack.isEmpty()) {
		 * renderMuzzleFlash(muzzleFlashStack.pop()); } GlStateManager.depthMask(true);
		 * 
		 * 
		 * GlStateManager.enableCull(); GlStateManager.blendFunc(GL11.GL_SRC_ALPHA,
		 * GL11.GL_ONE_MINUS_SRC_ALPHA); GlStateManager.enableDepth();
		 * GlStateManager.disableBlend(); GlStateManager.enableTexture2D();
		 */
	}

	public static void renderMuzzleFlash(MuzzleFlash flash) {

		Minecraft.getMinecraft().getTextureManager().bindTexture(FLASH);
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			GlStateManager.translate(flash.pos.x, flash.pos.y, flash.pos.z);
		} else {
			GlStateManager.translate(flash.thirdPersonPos.x, flash.thirdPersonPos.y, flash.thirdPersonPos.z);
		}

		GlStateManager.rotate(-flash.yaw + 0f, 0f, 1f, 0f);
		GlStateManager.rotate(flash.pitch, 1f, 0f, 0f);
		// renderFlashPlane(0, 0, 0, 0.7, 0.4, 0);
		double scale = flash.scale;
		renderFlashPlane(0, 0, 0.23 * scale, 0, 0.6 * scale, 0.8 * scale, 0);

		Minecraft.getMinecraft().getTextureManager().bindTexture(FLASHF);

		renderFlashPlane(0, 0, 0, 0.7 * scale, 0.4 * scale, 0, 0);
		renderFlashPlane(0, 0, -0.2 * scale, 1 * scale, 0.6 * scale, 0, 0);
		renderFlashPlane(0, 0, -0.47 * scale, 1 * scale, 0.6 * scale, 0, 0);
	}

	public static float scopeVelX = 0;
	public static float scopeVelY = 0;

	public static void renderFlashPlane(double x, double y, double z, double sizeX, double sizeY, double sizeZ,
			double rot) {

		GlStateManager.pushMatrix();
		GlStateManager.rotate((float) rot, 0.0f, 0.0f, 1.0f);
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bb.pos(-sizeX + x, -sizeY + y, 0 + z).tex(0, 0).endVertex();
		bb.pos(-sizeX + x, sizeY + y, 0 + z).tex(0, 1).endVertex();
		bb.pos(sizeX + x, sizeY + y, sizeZ + z).tex(1, 1).endVertex();
		bb.pos(sizeX + x, -sizeY + y, sizeZ + z).tex(1, 0).endVertex();
		t.draw();
		GlStateManager.popMatrix();

	}

	public static void renderSparks(double x, double y, double z, double sizeX, double sizeY, double sizeZ, double rot,
			int age) {
		/*
		 * GlStateManager.pushMatrix(); GlStateManager.rotate((float) rot, 0.0f, 0.0f,
		 * 1.0f); Tessellator t = Tessellator.getInstance(); BufferBuilder bb =
		 * t.getBuffer(); bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		 * bb.pos(-sizeX+x, -sizeY + y, 0 + z).tex(0, 0).endVertex(); bb.pos(-sizeX+x,
		 * sizeY + y, 0 + z).tex(0, 1).endVertex(); bb.pos(sizeX + x, sizeY + y, sizeZ +
		 * z).tex(1, 1).endVertex(); bb.pos(sizeX + x, -sizeY + y, sizeZ + z).tex(1,
		 * 0).endVertex(); t.draw(); GlStateManager.popMatrix();
		 */
		age = Minecraft.getMinecraft().player.ticksExisted % 6;
		int max = 6;
		int index = MathHelper.clamp((int) (((age) / (float) max) * max), 0, max - 1);
		float size = 1 / 2F;
		float u = (index % 2) * 0.5f;
		float v = (index / 2) * 0.33f;

		GlStateManager.pushMatrix();
		GlStateManager.rotate((float) rot, 0.0f, 0.0f, 1.0f);
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bb.pos(-sizeX + x, -sizeY + y, 0 + z).tex(u, v).endVertex();
		bb.pos(-sizeX + x, sizeY + y, 0 + z).tex(u, v + 0.33f).endVertex();
		bb.pos(sizeX + x, sizeY + y, sizeZ + z).tex(u + (0.5f), v + 0.33f).endVertex();
		bb.pos(sizeX + x, -sizeY + y, sizeZ + z).tex(u + 0.5f, v).endVertex();
		t.draw();
		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	public void fallEvent(LivingFallEvent event) {

		if (event.getEntity() instanceof EntityPlayer && event.getEntity().world.isRemote) {
			ClientValueRepo.shock += event.getDistance();
		}
	}

	@SubscribeEvent
	public void jumpEvent(LivingJumpEvent event) {
		if (event.getEntity() instanceof EntityPlayer && event.getEntity().world.isRemote) {
			// ClientValueRepo.rise += 0.1;

		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onClientTick(TickEvent.ClientTickEvent event) {
		onCompatibleClientTick(new CompatibleClientTickEvent(event));
		if(event.phase  == Phase.START && Minecraft.getMinecraft().player != null) {
			ClientValueRepo.ticker.update(Minecraft.getMinecraft().player.ticksExisted);
		}
		// if(Minecraft.getMinecraft().player.isSprinting()) {
		// RayTraceResult rtr = Minecraft.getMinec
		// }

		// if(Minecraft.getMinecraft().player.onGround && ClientValueRepo.rise > 0.01) {
		// ClientValueRepo.rise = 0;

		// ClientValueRepo.shock += 5;

		// }

		/*
		 * if(ClientModContext.getContext() != null) { PlayerWeaponInstance pwi =
		 * ClientModContext.getContext().getMainHeldWeapon(); if(pwi != null &&
		 * pwi.isAimed()) {
		 * KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.
		 * keyBindSprint.getKeyCode(), false);
		 * 
		 * } }
		 */

		// System.out.println(KeyBinding.setKeyBindState(keyCode, pressed););
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onRenderTickEvent(TickEvent.RenderTickEvent event) {
		onCompatibleRenderTickEvent(new CompatibleRenderTickEvent(event));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onPreRenderPlayer(RenderPlayerEvent.Pre event) {

		if (event.getEntityPlayer().isRiding() && event.getEntityPlayer().getRidingEntity() instanceof EntityVehicle
				&& event.getEntityPlayer().limbSwing != 39) {
			event.setCanceled(true);
		}

		ClientModContext modContext = (ClientModContext) getModContext();
		if (modContext.getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE
				&& event.getEntityPlayer() instanceof EntityPlayerSP) {
			/*
			 * This is a hack to allow player to view him/herself in remote perspective. By
			 * default EntityPlayerSP ("user" playing the game) cannot see himself unless
			 * player == renderViewEntity. So, before rendering EntityPlayerSP, setting
			 * renderViewEntity to player temporarily.
			 */
			origRenderVeiwEntity = event.getRenderer().getRenderManager().renderViewEntity;
			event.getRenderer().getRenderManager().renderViewEntity = event.getEntityPlayer();
		}

		onCompatibleRenderPlayerPreEvent(new CompatibleRenderPlayerPreEvent(event));
	}

	protected abstract ModContext getModContext();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onPostRenderPlayer(RenderPlayerEvent.Post event) {
		ClientModContext modContext = (ClientModContext) getModContext();
		if (modContext.getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE
				&& event.getEntityPlayer() instanceof EntityPlayerSP) {
			/*
			 * This is a hack to allow player to view him/herself in remote perspective. By
			 * default EntityPlayerSP ("user" playing the game) cannot see himself unless
			 * player == renderViewEntity. So, before rendering EntityPlayerSP, setting
			 * renderViewEntity to player temporarily. After rendering EntityPlayerSP,
			 * restoring the original renderViewEntity.
			 */
			event.getRenderer().getRenderManager().renderViewEntity = origRenderVeiwEntity;
		}
	}

	@SubscribeEvent
	public void onRightHandEmpty(PlayerInteractEvent.RightClickEmpty evt) {

		ClientModContext context = (ClientModContext) getModContext();
		EntityPlayer player = Minecraft.getMinecraft().player;

		List<EntityVehicle> i = player.world.getEntitiesWithinAABB(EntityVehicle.class,
				new AxisAlignedBB(player.getPosition()).grow(10));

		if (i == null || i.isEmpty()) {
			return;
		} else {

			for (EntityVehicle v : i) {

				OreintedBB bb = v.getOreintedBoundingBox();

				// bb.move(v.posX, v.posY, v.posZ);
				Vec3d start = player.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());
				Vec3d endVec = start.add(player.getLookVec().scale(7));

				bb.updateInverse();
				RayTraceResult rtr = bb.doRayTrace(start, endVec);
				if (rtr != null) {
					// System.out.println("sending");
					context.getChannel().getChannel()
							.sendToServer(new VehicleInteractPacket(true, v.getEntityId(), player.getEntityId()));
					return;
				}

			}

		}

	}

	@SubscribeEvent
	public void onLeftHandEmpty(PlayerInteractEvent.LeftClickEmpty evt) {

		ClientModContext context = (ClientModContext) getModContext();
		EntityPlayer player = Minecraft.getMinecraft().player;

		List<EntityVehicle> i = player.world.getEntitiesWithinAABB(EntityVehicle.class,
				new AxisAlignedBB(player.getPosition()).grow(3));

		if (i == null || i.isEmpty()) {
			return;
		} else {

			for (EntityVehicle v : i) {

				OreintedBB bb = v.getOreintedBoundingBox();

				// bb.move(v.posX, v.posY, v.posZ);
				Vec3d start = player.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());
				Vec3d endVec = start.add(player.getLookVec().scale(4));

				// bb.updateInverse();
				RayTraceResult rtr = bb.doRayTrace(start, endVec);

				if (rtr != null) {

					context.getChannel().getChannel()
							.sendToServer(new VehicleInteractPacket(false, v.getEntityId(), player.getEntityId()));

					// v.onKillCommand();
					// v.setDead();
					return;
				}

			}

		}
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {

		onCompatibleRenderHand(new CompatibleRenderHandEvent(event));

	}

	public static TextureAtlasSprite carParticles;
	public static TextureAtlasSprite smoke1;

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(getModContext()
				.getNamedResource(CompatibleParticle.CompatibleParticleBreaking.TEXTURE_BLOOD_PARTICLES));
		carParticles = event.getMap().registerSprite(new ResourceLocation("mw" + ":" + "particle/carparticle"));
		smoke1 = event.getMap().registerSprite(new ResourceLocation("mw" + ":" + "smokes/smokesheet"));
	}

	protected abstract void onCompatibleRenderTickEvent(CompatibleRenderTickEvent compatibleRenderTickEvent);

	protected abstract void onCompatibleClientTick(CompatibleClientTickEvent compatibleClientTickEvent);

	protected abstract void onCompatibleRenderHand(CompatibleRenderHandEvent event);

	protected abstract void onCompatibleRenderPlayerPreEvent(CompatibleRenderPlayerPreEvent event);

}
