package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.management.modelmbean.ModelMBeanNotificationInfo;

import java.util.Stack;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLSync;
import org.lwjgl.util.glu.Project;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Reflection;
import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.BulletHoleRenderer;
import com.vicmatskiv.weaponlib.ClassInfo;
import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ItemSkin;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.RopeSimulation;
import com.vicmatskiv.weaponlib.RopeSimulation.Stick;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponReloadAspect;
import com.vicmatskiv.weaponlib.WeaponRenderer;
import com.vicmatskiv.weaponlib.WeaponState;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.OpenGLSelectionHelper;
import com.vicmatskiv.weaponlib.animation.gui.AnimationGUI;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData.BlockbenchTransition;
import com.vicmatskiv.weaponlib.command.DebugCommand;
import com.vicmatskiv.weaponlib.compatibility.graph.CompatibilityClassGenerator;
import com.vicmatskiv.weaponlib.animation.jim.BBLoader;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;
import com.vicmatskiv.weaponlib.debug.SysOutController;
import com.vicmatskiv.weaponlib.model.Bullet556;
import com.vicmatskiv.weaponlib.particle.DriftCloudFX;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.render.Dloom;
import com.vicmatskiv.weaponlib.render.HDRFramebuffer;
import com.vicmatskiv.weaponlib.render.InstancedRender;
import com.vicmatskiv.weaponlib.render.InstancedShellObject;
import com.vicmatskiv.weaponlib.render.ModernSkyRenderer;
import com.vicmatskiv.weaponlib.render.MultisampledFBO;
import com.vicmatskiv.weaponlib.render.MuzzleFlashRenderer;
import com.vicmatskiv.weaponlib.render.Shaders;
import com.vicmatskiv.weaponlib.render.ShellRenderer;
import com.vicmatskiv.weaponlib.render.ShellRenderer2;
import com.vicmatskiv.weaponlib.render.VAOData;
import com.vicmatskiv.weaponlib.render.VAOLoader;
import com.vicmatskiv.weaponlib.render.VMWFrameTimer;
import com.vicmatskiv.weaponlib.render.WavefrontLoader;
import com.vicmatskiv.weaponlib.render.WavefrontModel;
import com.vicmatskiv.weaponlib.render.bgl.GLCompatible;
import com.vicmatskiv.weaponlib.render.bgl.ModernUtil;
import com.vicmatskiv.weaponlib.render.bgl.PostProcessPipeline;
import com.vicmatskiv.weaponlib.render.bgl.instancing.InstancedAttribute;
import com.vicmatskiv.weaponlib.render.bgl.math.AngleKit;
import com.vicmatskiv.weaponlib.render.qrender.QRenderer;
import com.vicmatskiv.weaponlib.render.shells.ShellManager;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell.Type;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;
import com.vicmatskiv.weaponlib.sound.JSoundEngine;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.collisions.OBBCollider;
import com.vicmatskiv.weaponlib.vehicle.collisions.OreintedBB;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleControlPacket;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleDataContainer;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleInteractPacket;

import akka.japi.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public abstract class CompatibleClientEventHandler {

	private Entity origRenderVeiwEntity;

	public static boolean freecamEnabled = false;
	public static boolean freecamLock = false;
	public static boolean muzzlePositioner = false;
	
	
	public static Vec3d magRotPositioner = Vec3d.ZERO;

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
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && freecamEnabled) {
			freeYaw += yawDelta;
			freePitch += pitchDelta;
		} else if(!freecamLock) {
			freeYaw = 0;
			freePitch = 0;
		}
		if(freecamEnabled) {
			
			e.setYaw((float) (freeYaw));
			e.setPitch((float) (freePitch));
		}
		
		/*
		// System.out.println("hi");
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && freecamEnabled) {
			// System.out.println("hi");
			freeYaw += yawDelta;
			freePitch += pitchDelta;
			
		} else if(!freecamLock) {
			freeYaw = 0;
			freePitch = 0;
		}
		
		if(freecamEnabled) {
			e.setYaw((float) (freeYaw));
			e.setPitch((float) (freePitch));
		}*/
		

		yawDelta = 0;
		pitchDelta = 0;
	}

	public static double freeYaw = 0;
	public static double freePitch = 0;

	public static double yawDelta = 0;
	public static double pitchDelta = 0;

	public static Vec3d debugmuzzlePosition = new Vec3d(0, -1, -6.5);

	public static HashMap<Integer, Stack<Long>> muzzleFlashMap = new HashMap<>();

	public static boolean checkShot(int entityID) {
		//muzzleFlashMap.clear();
		//System.out.println(muzzleFlashMap);
		if (muzzleFlashMap.isEmpty() || !muzzleFlashMap.containsKey(entityID)
				|| (muzzleFlashMap.get(entityID).isEmpty())) {
			return false;
		} else {
			if(System.currentTimeMillis()-muzzleFlashMap.get(entityID).peek() > 25) {
				muzzleFlashMap.get(entityID).pop();
			}
			return true;
			//return true;

		}
	}

	public static void uploadFlash(int entityID) {
		if (muzzleFlashMap.containsKey(entityID)) {
			muzzleFlashMap.get(entityID).push(System.currentTimeMillis());
		} else {
			Stack<Long> stack = new Stack<>();
			stack.push(System.currentTimeMillis());
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
	public void playerTick(PlayerTickEvent evt) {
		//Minecraft.getMinecraft().player.inventory.currentItem = 0;
	}

	@SubscribeEvent
	public void mouseMove(MouseEvent me) {
		
		if(AnimationModeProcessor.getInstance().getFPSMode()) {
			AnimationModeProcessor amp = AnimationModeProcessor.getInstance();
			
			
			double pan = Math.max(0.01, Math.abs(amp.pan.z)/10000f);
			
			
		
			amp.pan = amp.pan.addVector(0, 0, (me.getDwheel())*pan);
			
			Field f = ReflectionHelper.findField(Mouse.class, "event_dwheel");
			try {
				f.set(null, 0);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		
	}

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

	
	
	public static WavefrontModel bulletShell = WavefrontLoader.loadSubModel("9mmshell", "casing");
	
//	public static ShellParticleSimulator shells = new ShellParticleSimulator();
	
	public static ShellManager shellManager = new ShellManager();
	public static BulletHoleRenderer bhr = new BulletHoleRenderer();
	//public static InstancedRender ir = new InstancedRender();
	
	
	public static Vec3d testPos = Vec3d.ZERO;

	
	public static CompatibilityClassGenerator ccg = new CompatibilityClassGenerator();
	public static InstancedShellObject iso;
	
	public static Vec3d getInterpolatedPlayerCoords() {
		EntityPlayer p = Minecraft.getMinecraft().player;
		float interpX = (float) MatrixHelper.solveLerp(p.prevPosX, p.posX,
				Minecraft.getMinecraft().getRenderPartialTicks());
		float interpY = (float) MatrixHelper.solveLerp(p.prevPosY, p.posY,
				Minecraft.getMinecraft().getRenderPartialTicks());
		float interpZ = (float) MatrixHelper.solveLerp(p.prevPosZ, p.posZ,
				Minecraft.getMinecraft().getRenderPartialTicks());

		
		return new Vec3d(interpX, interpY, interpZ);
	}
	
	public VMWFrameTimer frametimer = new VMWFrameTimer();
	
	@SubscribeEvent
	public void renderWorrldLastEvent(RenderWorldLastEvent evt) {
		PostProcessPipeline.captureMatricesIntoBuffers();
		frametimer.markFrame();
		/*
		System.out.println("DEOBF: " + Launch.blackboard.get("fml.deobfuscatedEnvironment"));
		System.out.println();
		*/
		
	
		
		
		ClientValueRepo.renderUpdate(getModContext());
		//System.out.println(Minecraft.getMinecraft().getFramebuffer() instanceof HDRFramebuffer);

		PostProcessPipeline.setWorldElements();
		
		double divisor = 120/frametimer.getFramerate()*0.05;
		divisor = Math.min(0.08, divisor);
		Interceptors.nsm.update();
		
		//EntityPlayer p = Minecraft.getMinecraft().player;
		//PostProcessPipeline.createDistortionPoint((float) p.posX,(float)  p.posY, (float) p.posZ, 15f, 5f);
		
		/*
		
		GlStateManager.pushMatrix();
		
		
		Shader shad = ShaderManager.loadVMWShader("cloud");
		shad.use();
		for(int i = 0; i < 5; ++i) {
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + 3 + i);
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mw:textures/environment/fbm" + (i+1) + ".png"));
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
			//System.out.println("mw:textures/environment/fbm" + i + ".png");
			shad.uniform1i("fbm[" + i + "]", 3 + i);
		}
		
		
		
		
		GlStateManager.enableBlend();
		shad.uniform1i("cloud", 3);
		shad.uniform1f("timer", ClientValueRepo.ticker.getLerpedFloat());
		Vec3d pti = getInterpolatedPlayerCoords();
		//GlStateManager.translate(-pti.x, -pti.y, -pti.z);
		double size = 30;
		GlStateManager.translate(0, 10, 0);
		//GlStateManager.disableTexture2D();
		GlStateManager.enableTexture2D();
		
		//Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mw:textures/environment/cloud.png"));
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		bb.pos(-1*size, 0, -1*size).tex(0, 0).endVertex();
		bb.pos(1*size, 0, -1*size).tex(1, 0).endVertex();
		bb.pos(1*size, 0, 1*size).tex(1, 1).endVertex();
		bb.pos(-1*size, 0, size).tex(0, 1).endVertex();
		
		t.draw();
		shad.release();
		GlStateManager.popMatrix();
		*/
		//float light = Minecraft.getMinecraft().world.getLight(Minecraft.getMinecraft().player.getPosition()) * Minecraft.getMinecraft().world.getSunBrightness(1.0f);
		
		//System.out.println(light);
		
		
		//Interceptors.nsm.update(1/divisor);
	
		
		try {
			//AnimationData susData = BBLoader.getAnimation("mp5", "inspect", "main");
			//System.out.println(susData.getTimestamps());
			
			/*
			System.out.println("===== START =====");
			for(Entry<Float, BlockbenchTransition> bbt : susData.bbTransition.entrySet()) {
				System.out.println("(" + bbt.getKey() + ") -> " + bbt.getValue().getTimestamp());
			}
			System.out.println("===== END =====");
			*/
			//System.out.println(susData.timestamps.size());
		
		} catch(Exception e) {
			//e.printStackTrace();
		}
		//System.out.println(susData);
		
		//System.out.println("hi");
		/*
		if(ClientModContext.getContext() != null && ClientModContext.getContext().getMainHeldWeapon() != null) {
			GlStateManager.pushMatrix();
			
			Vec3d playI = getInterpolatedPlayerCoords();
			GlStateManager.translate(-playI.x, -playI.y, -playI.z);
			
			GlStateManager.translate(-1447, 6, 327);
			//DebugRenderer.setupBasicRender();
			//GL11.glPointSize(10f);
			//DebugRenderer.renderPoint(Vec3d.ZERO, Vec3d.ZERO);
			//DebugRenderer.destructBasicRender();
			
			
			MuzzleFlashRenderer mfr = new MuzzleFlashRenderer();
			ItemStack weaponItemStack = ClientModContext.getContext().getMainHeldWeapon().getItemStack();
			
			
			//Bloom.bindBloomBuffer();
			//mfr.renderFlash(weaponItemStack, true);
			GlStateManager.enableTexture2D();
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
			mfr.renderFlash(weaponItemStack, false);
			
			GlStateManager.popMatrix();
		}*/
		
	/*
		GlStateManager.pushMatrix();
		Vec3d iP = getInterpolatedPlayerCoords();
		
		Quaternion q = MatrixHelper.fromEulerAngles(Math.toRadians(0), Math.toRadians(0), Math.toRadians(45));
		double[] angles = MatrixHelper.toEulerAngles(q);
		
		FloatBuffer boof = BufferUtils.createFloatBuffer(16);
		GlStateManager.quatToGlMatrix(boof, q);
		
		
		//float[] axisAngle = new AngleKit().convertEulerToAxisAngle((float) Math.toRadians(45), 0, 0);
		
		//System.out.println(Math.toDegrees(axisAngle[0]) + " -> (" +  axisAngle[1] + ", " + axisAngle[2] + ", " + axisAngle[3] + ")");
		
		//GlStateManager.multMatrix(boof);
		//angles[0] -= Math.PI;
		//angles[2] *= -1;
		
		//System.out.println(Math.toDegrees(angles[0]) + " | " + Math.toDegrees(angles[1]) +  " | " + Math.toDegrees(angles[2]));
		
		
		GlStateManager.rotate(45f, 0, 1, 0);
		new ModelChest().renderAll();
		
		
		
		GlStateManager.popMatrix();
		
		*/
		if(ClientModContext.getContext().getMainHeldWeapon() != null) {
			
			GlStateManager.pushMatrix();
			Vec3d iP2 = getInterpolatedPlayerCoords();
			GlStateManager.translate(-iP2.x, -iP2.y, -iP2.z);
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
			GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
			GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);

			Project.gluUnProject(WeaponRenderer.POSITION.get(0), WeaponRenderer.POSITION.get(1), WeaponRenderer.POSITION.get(2), MODELVIEW, PROJECTION, VIEWPORT,
					NEW_POS);
			GlStateManager.popMatrix();
			/*
			PlayerWeaponInstance playerWeaponInstance = ClientModContext.getContext().getMainHeldWeapon();
			Vec3d newPos = Vec3d.ZERO;
	    	Vec3d offset = new Vec3d(-0.5, -0.25, 0.5);
			if(playerWeaponInstance.isAimed()) {
			//	offset = new Vec3d(0.1, -0.2, 0.5);
			}
			Vec3d posAdd = offset.rotatePitch((float) -Math.toRadians(Minecraft.getMinecraft().player.rotationPitch)).rotateYaw((float) -Math.toRadians(Minecraft.getMinecraft().player.rotationYaw));
			
			Vec3d outwardPos = newPos.subtract(Minecraft.getMinecraft().player.getPositionEyes(1.0f)).normalize().rotatePitch((float) -Math.toRadians(Minecraft.getMinecraft().player.rotationPitch)).rotateYaw((float) Math.toRadians(-Minecraft.getMinecraft().player.rotationYaw)).scale(0.5).add(Minecraft.getMinecraft().player.getPositionEyes(1.0f));
			testPos = outwardPos.add(posAdd);
			*/
			
			
			/*
			testPos = new Vec3d(CompatibleClientEventHandler.NEW_POS.get(0), 
        			CompatibleClientEventHandler.NEW_POS.get(1),
        			CompatibleClientEventHandler.NEW_POS.get(2));
			
			
			double distance = 0.5;
			Vec3d eyePos = Minecraft.getMinecraft().player.getPositionEyes(1.0f);
			Vec3d newVec = testPos.subtract(eyePos).normalize().scale(distance).add(eyePos);
			//testPos = testPos.add(Minecraft.getMinecraft().player.getPositionVector());
			GlStateManager.pushMatrix();
			Vec3d iP = getInterpolatedPlayerCoords();
			GlStateManager.translate(-iP.x, -iP.y, -iP.z);
			GlStateManager.disableTexture2D();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb2 = t.getBuffer();
			GL11.glPointSize(10f);
			bb2.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION);
			bb2.pos(newVec.x, newVec.y, newVec.z).endVertex();
			t.draw();
			
			//System.out.println(testPos);
			GlStateManager.popMatrix();
			
			
			shellManager.enqueueShell(new Shell(Type.ASSAULT, newVec, Vec3d.ZERO, new Vec3d(0.2, 0 , 0)));
			*/
		}
		
		
		
		//if(true) return;
		//System.out.println("Start");

		/* Generate methods for glCommands
		ArrayList<Pair<Class<?>, Method>> ar = ccg.findStandardOpenGLMethod("glGenVertexArrays");
		for(Pair<Class<?>, Method> pair : ar) {
			System.out.println("\n" + ccg.buildOutMethod(pair, "glGenVertexArrays"));
		}
		\
		
		
		//System.out.println("end");
		if(true) return;
		
		
		
		//SysOutController.revealCallLocations();
		
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
		
		GlStateManager.enableTexture2D();
		
		Minecraft.getMinecraft().getTextureManager()
		.bindTexture(new ResourceLocation("mw:textures/models/assaultshell.png"));
		
		
		MODELVIEW.rewind();
		PROJECTION.rewind();
		
		/*
		if(shr2 == null) {
			shr2 = new ShellRenderer2();
			shr2.init();
		}
		shr2.realRender();
		*/
		
		/*
		if(iso == null) {
			WavefrontModel model = WavefrontLoader.loadSubModel("assaultshell", "casing", true);
			iso = new InstancedShellObject("instanced", model, GL11.GL_TRIANGLES, 100000,
					new InstancedAttribute("inPosition", 3, InstancedAttribute.Type.VEC3),
					new InstancedAttribute("inQuat", 4, InstancedAttribute.Type.VEC4),
					new InstancedAttribute("inLightmapCoords", 5, InstancedAttribute.Type.VEC2));
		}
		*/
		
		/*
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		
		CompatibleShellRenderer.setupLightmapCoords(Minecraft.getMinecraft().player.getPositionVector());
		GlStateManager.enableLighting();
		GlStateManager.color(1, 1, 1);
		GlStateManager.pushMatrix();
		
		EntityPlayer p = Minecraft.getMinecraft().player;
		GlStateManager.translate(-p.posX, -p.posY, -p.posZ);
		
		iso.updateData(shellManager);
		iso.render(shellManager.getShells().size());
		GlStateManager.popMatrix();
		*/
		//if(true) return;
		//ir.render();
		
		//System.out.println(shells.getShells().size());
		
		//SysOutController.reset();
		
		
		bhr.render();
		
		
		
		/*
		GL11.glPushMatrix();
		
		Minecraft.getMinecraft().getTextureManager()
		.bindTexture(new ResourceLocation("mw:textures/models/assaultshell.png"));
		GlStateManager.translate(0, 1.5, 0.2);
		GlStateManager.rotate(45, 0, 0, 0);
		GlStateManager.enableLighting();
		Shader shad = ShaderManager.loadVMWShader("shells");
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		CompatibleShellRenderer.setupLightmapCoords(Minecraft.getMinecraft().player.getPositionVector());
		
		
		shad.use();
		shad.uniform1i("lightmap", 1);
		
		WavefrontModel model  = WavefrontLoader.loadSubModel("assaultshell", "casing");
		model.render();
		shad.release();
		*/
		/*
		GlStateManager.disableTexture2D();
		
		
		
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb2 = t.getBuffer();
		bb2.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
		GlStateManager.disableCull();
		GlStateManager.color(0.8f, 0.7f, 0, 1);
		GlStateManager.disableTexture2D();
		//GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		double shScaleX = 0.02;
		double shScaleY = 0.12;
		double shScaleZ = 0.02;
		for(int i = 0; i < g_vertex_buffer_data.length-3; i += 3) {
			//System.out.println(i);
			bb2.pos(g_vertex_buffer_data[i]*shScaleX, g_vertex_buffer_data[i+1]*shScaleY, g_vertex_buffer_data[i+2]*shScaleZ).endVertex();
		}
		
		t.draw();
		*/
		
		
		
		//CompatibleShellRenderer.renderDegenerateModel();
		
		

		
		
		//GL11.glPopMatrix();
		
		
		/*
		GlStateManager.pushMatrix();
		EntityPlayer pla = Minecraft.getMinecraft().player;
		
		double iPosX = pla.prevPosX + (pla.posX - pla.prevPosX)*Minecraft.getMinecraft().getRenderPartialTicks();
		double iPosY = pla.prevPosY + (pla.posY - pla.prevPosY)*Minecraft.getMinecraft().getRenderPartialTicks();
		double iPosZ = pla.prevPosZ + (pla.posZ - pla.prevPosZ)*Minecraft.getMinecraft().getRenderPartialTicks();
		GlStateManager.translate(-iPosX, -iPosY, -iPosZ);
		GlStateManager.disableTexture2D();
		GL11.glPointSize(10f);
		Tessellator tes  = Tessellator.getInstance();
		BufferBuilder bb = tes.getBuffer();
		GlStateManager.disableCull();
		Vec3d lol = new Vec3d(120.7, 4.01, -343);
		
		GlStateManager.enableTexture2D();
		ResourceLocation rl = new ResourceLocation("mw:textures/entity/bullethole.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
		
		
		GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);
		GlStateManager.enableBlend();
		double size = 0.05;
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bb.pos(lol.x+size, lol.y, lol.z+size).tex(0, 0).endVertex();
		bb.pos(lol.x-size, lol.y, lol.z+size).tex(1, 0).endVertex();
		bb.pos(lol.x-size, lol.y, lol.z-size).tex(1, 1).endVertex();
		bb.pos(lol.x+size, lol.y, lol.z-size).tex(0, 1).endVertex();
		tes.draw();
		
		GlStateManager.popMatrix();
		
		*/
		
		if(ClientModContext.getContext() != null && ClientModContext.getContext().getMainHeldWeapon() != null) {
			PlayerWeaponInstance pwi = ClientModContext.getContext().getMainHeldWeapon();
			
			//if(pwi.getState() != WeaponState.READY) System.out.println(pwi.getState());
			
			//System.out.println(pwi.getState());
			
			if(pwi.getState() == WeaponState.READY) {
				pwi.setDelayCompoundEnd(true);
				pwi.getWeapon().getRenderer().setShouldDoEmptyVariant(false);
			}
			
			ItemAttachment<Weapon> i = ClientModContext.getContext().getAttachmentAspect().getActiveAttachment(AttachmentCategory.MAGAZINE, pwi);
			
			
			//System.out.println(i);
			//System.out.println(pwi.getWeapon().getRenderer().getStateManager(player));
			
		//	pwi.getWeapon().getCompatibleAttachments(AttachmentCategory.MAGAZINE).forEach(c -> System.out.println(I18n.format(c.getAttachment().getUnlocalizedName() + ".name")));
			//String unloc = pwi.getItemStack().getUnlocalizedName();
		//	System.out.println(I18n.format(pwi.getItemStack().getUnlocalizedName() + ".name"));
			//Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale().tra
			//System.out.println(new TextComponentTranslation(unloc, new Object[0]).getFormattedText());
			//System.out.println(NEW_POS.get(0));
			/*
			Vec3d newPos = new Vec3d(CompatibleClientEventHandler.NEW_POS.get(0), 
	    			CompatibleClientEventHandler.NEW_POS.get(1),
	    			CompatibleClientEventHandler.NEW_POS.get(2));
	    	float rotate = (float) Math.toRadians(-Minecraft.getMinecraft().player.rotationYaw);
	    	Vec3d vec = (new Vec3d(-20, -0, 0)).rotateYaw(rotate);
	    	Shell shell = new Shell(newPos.add(Minecraft.getMinecraft().player.getPositionVector()), new Vec3d(-90, 0, 0).rotateYaw(rotate), vec);
	    	CompatibleClientEventHandler.shellManager.enqueueShell(shell);
			*/
	    	//System.out.println(pwi.isMagSwapDone());
			
			
			
			//System.out.println(pwi.getWeapon().getTotalReloadingDuration());
			
			boolean var = Math.abs((System.currentTimeMillis()-(pwi.getReloadTimestamp()))/((double) pwi.getWeapon().getTotalReloadingDuration()*0.5)-0.5) < 0.01;
		//	System.out.println(var);
			//System.out.println(ClientModContext.getContext().getMainHeldWeapon().getState());
			
		}
		
		
		// Hot swaps the Minecraft framebuffer
		
	
		// for an HDR one.
		
		try {
			Framebuffer current = Minecraft.getMinecraft().getFramebuffer();
			if(!(current instanceof HDRFramebuffer)) {
				Field f = CompatibleReflection.findField(Minecraft.class,"framebufferMc", "field_147124_at");
				//System.out.println("FOUND");
				f.setAccessible(true);
				Framebuffer fbo = new HDRFramebuffer(current.framebufferWidth, current.framebufferHeight, true);
				f.set(Minecraft.getMinecraft(), fbo);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		

		
		
		if(getModContext() != null) {
			if(getModContext().getMainHeldWeapon() != null && getModContext().getMainHeldWeapon().getWeapon().builder.isUsingNewSystem()) {
				AnimationModeProcessor.getInstance().legacyMode = false;
			} else {
				AnimationModeProcessor.getInstance().legacyMode = true;
			}
		}

	
		if (ClientModContext.getContext().getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE)
			return;
		
		

		shellManager.render();

		PROJECTION.rewind();
		MODELVIEW.rewind();

		
		if(AnimationModeProcessor.getInstance().getFPSMode()) {
			Minecraft.getMinecraft().setIngameNotInFocus();

			//Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
			AnimationModeProcessor.getInstance().onTick();
			
			Minecraft.getMinecraft().player.inventory.currentItem = 0;
		
			
			
			Shaders.blackScreen.use();
			Bloom.renderFboTriangle(Minecraft.getMinecraft().getFramebuffer());
			Shaders.blackScreen.release();
			
			return;
		}
	
		
		
		PostProcessPipeline.blitDepth();
		PostProcessPipeline.setupDistortionBufferEffects();

		PostProcessPipeline.doWorldProcessing();
		
		
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

	}

	@SubscribeEvent
	public void jumpEvent(LivingJumpEvent event) {
		if (event.getEntity() instanceof EntityPlayer && event.getEntity().world.isRemote) {
			// ClientValueRepo.rise += 0.1;

		}
	}
	
	public static ArrayList<RopeSimulation> ropeSIm = new ArrayList<>();

	

	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onClientTick(TickEvent.ClientTickEvent event) {
		onCompatibleClientTick(new CompatibleClientTickEvent(event));
		
		
		
 		
		
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null && event.phase == Phase.END) {
			
			
			
			double yAmount = ClientValueRepo.recoilWoundY * 0.2;
			player.rotationPitch += yAmount;
			ClientValueRepo.recoilWoundY -= yAmount;
			
		}
		
		// Run recalculations for the weather renderer
		if(event.phase == Phase.START && PostProcessPipeline.getWeatherRenderer() != null && Minecraft.getMinecraft().player != null && PostProcessPipeline.getWeatherRenderer().shouldRecalculateRainVectors(Minecraft.getMinecraft().player)) PostProcessPipeline.getWeatherRenderer().recalculateRainVectors(Minecraft.getMinecraft().player, getInterpolatedPlayerCoords());
						
		
		
		
		if(event.phase == Phase.START && Minecraft.getMinecraft().player != null && getModContext() != null && getModContext().getMainHeldWeapon() != null) {
			ClientValueRepo.update(getModContext());
			
			CompatibleWeaponRenderer.wrh.strafingAnimation.update(0.08f);
			CompatibleWeaponRenderer.wrh.runningAnimation.update(0.08f);
			CompatibleWeaponRenderer.wrh.walkingAnimation.update(0.08f);
		}
		
		
		if(event.phase == Phase.START) {
			//Interceptors.nsm.update();
			int ticksRequired = (int) Math.round(AnimationGUI.getInstance().debugFireRate.getValue());
			
		//	ticksRequired = 2000;
			
			if(DebugCommand.isWorkingOnScreenShake() && Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.ticksExisted%20 == 0 && getModContext().getMainHeldWeapon() != null) {
				CompatibleClientEventHandler.uploadFlash(Minecraft.getMinecraft().player.getEntityId());
				ClientValueRepo.fireWeapon(getModContext().getMainHeldWeapon());
			}
			
			if(Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.ticksExisted%ticksRequired == 0 && AnimationModeProcessor.getInstance().getFPSMode() && !AnimationGUI.getInstance().isPanelClosed("Recoil")) {
				
				ClientValueRepo.fireWeapon(getModContext().getMainHeldWeapon());
			}
			
		}
		
		if(event.phase  == Phase.START && Minecraft.getMinecraft().player != null) {
			
			ClientValueRepo.ticker.update(Minecraft.getMinecraft().player.ticksExisted);

			shellManager.update(0.05);
			
			
		}
		//System.out.println(shellManager.getShells().size());
		if(Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.ticksExisted%1 == 0 && event.phase == Phase.START) {
			for(int i = 0; i < 0; ++i) {
				
				shellManager.enqueueShell(new Shell(Type.ASSAULT, new Vec3d(188, 6, -395.45), new Vec3d(90,0,0), new Vec3d(Math.random()/2-0.25, Math.random()/2-0.25, Math.random()/2-0.25)));
				
			}
			
		}
		
		if(Minecraft.getMinecraft().world != null && event.phase == Phase.START) {
			//ropeSIm.clear();
			
			if(Minecraft.getMinecraft().player.ticksExisted%100 == 0) {
				ropeSIm.clear();
			}
			
			if(ropeSIm.isEmpty()) {
				RopeSimulation rs = new RopeSimulation();
				rs.newPoint(new Vec3d(90.5, 5.81, -359.6), false);
				rs.newPoint(new Vec3d(90.4, 5.6, -359.6), false);
				rs.connect(rs.points.get(0), rs.points.get(1));
				ropeSIm.add(rs);
			}
			
			
			for(RopeSimulation rope : ropeSIm) {
				rope.simulate(0.1);
			}
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
