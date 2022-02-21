package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.management.modelmbean.ModelMBeanNotificationInfo;

import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL43;
import org.lwjgl.util.glu.Project;

import com.google.common.reflect.Reflection;
import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.ClientModContext;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.WeaponReloadAspect;
import com.vicmatskiv.weaponlib.WeaponRenderer;
import com.vicmatskiv.weaponlib.WeaponState;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.OpenGLSelectionHelper;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData.BlockbenchTransition;
import com.vicmatskiv.weaponlib.animation.jim.BBLoader;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;
import com.vicmatskiv.weaponlib.model.Bullet556;
import com.vicmatskiv.weaponlib.particle.DriftCloudFX;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.render.Dloom;
import com.vicmatskiv.weaponlib.render.GLCompatible;
import com.vicmatskiv.weaponlib.render.ModernSkyRenderer;
import com.vicmatskiv.weaponlib.render.ModernUtil;
import com.vicmatskiv.weaponlib.render.MultisampledFBO;
import com.vicmatskiv.weaponlib.render.ShellRenderer;
import com.vicmatskiv.weaponlib.render.ShellRenderer2;
import com.vicmatskiv.weaponlib.render.VAOData;
import com.vicmatskiv.weaponlib.render.VAOLoader;
import com.vicmatskiv.weaponlib.render.WavefrontLoader;
import com.vicmatskiv.weaponlib.render.WavefrontModel;
import com.vicmatskiv.weaponlib.render.qrender.QRenderer;
import com.vicmatskiv.weaponlib.render.shells.ShellManager;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell;
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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
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

	
	public static Shader shellLight = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/shells"));
	
	public static WavefrontModel bulletShell = WavefrontLoader.loadSubModel("boo6", "casing");
	
//	public static ShellParticleSimulator shells = new ShellParticleSimulator();
	
	public static ShellManager shellManager = new ShellManager();
	
	@SubscribeEvent
	public void renderWorrldLastEvent(RenderWorldLastEvent evt) {
		//System.out.println(shells.getShells().size());
		if(Minecraft.getMinecraft().player.ticksExisted%1 == 0) {
			for(int x = 0; x < 1; ++x) {
				
				//shellManager.enqueueShell(new Shell(new Vec3d(38, 6, -328), Vec3d.ZERO, new Vec3d(-20, 0, 0)));
				//shellManager.enqueueShell(new Shell(new Vec3d(43.856, 5.5, -331.204), new Vec3d(90,0,0), new Vec3d(-30, 0, 0)));
				
			}
		}
		
		
		
		if(ClientModContext.getContext() != null && ClientModContext.getContext().getMainHeldWeapon() != null) {
			PlayerWeaponInstance pwi = ClientModContext.getContext().getMainHeldWeapon();
			
			//System.out.println(pwi.getWeapon().getRenderer().getStateManager(player));
			//System.out.println(pwi.getState());
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
		
		try {
			
			//MultisampledFBO fbo = new MultisampledFBO(Minecraft.getMinecraft().getFramebuffer().framebufferWidth, Minecraft.getMinecraft().getFramebuffer().framebufferHeight, true);
			//System.out.println(fbo);
			Field f = ReflectionHelper.findField(Minecraft.class, "framebufferMc");
		
			f.setAccessible(true);
			
			if(f.get(Minecraft.getMinecraft()) instanceof Framebuffer) {
				
				
				/*
				Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
				if(Minecraft.getMinecraft().player.ticksExisted%30 == 0) {
					
					fbo = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
					
					
					f.set(Minecraft.getMinecraft(), fbo);
				}
				*/
			//	
			//	System.out.println(GL11.glIsTexture(fbo.framebufferTexture));
				
				
				
				/*
				//fbo.framebufferTexture = GL11.glGenTextures();
				GL11.glBindTexture(GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, fbo.framebufferTexture);
				
				System.out.println(GL11.glGetError());
				
				GLCompatible.glTexImage2DMultisample(GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, 4, GL11.GL_RGBA8, fbo.framebufferTextureWidth, fbo.framebufferTextureHeight, false);
				GLCompatible.glFramebufferTexture2D(GLCompatible.GL_FRAMEBUFFER, GLCompatible.GL_COLOR_ATTACHMENT0, GLCompatible.GL_TEXTURE_2D_MULTISAMPLE, fbo.framebufferTexture, 0);
				
				*/
				//MultisampledFBO frameboofer = new MultisampledFBO(Minecraft.getMinecraft().getFramebuffer().framebufferWidth, Minecraft.getMinecraft().getFramebuffer().framebufferHeight, true);
				
				
				
			}
			
			
			
			
		} catch(Exception e) {
			
		}
		
		
		
		
		try {
			
			
			
			
		//	System.out.println(ClientModContext.getContext().getMainHeldWeapon().getState());
		} catch(Exception e) {}
		
		if(Minecraft.getMinecraft().world.provider.getSkyRenderer() == null) {
			Minecraft.getMinecraft().world.provider.setSkyRenderer(new ModernSkyRenderer());
			
		}
		
		
		if(getModContext() != null) {
			if(getModContext().getMainHeldWeapon() != null && getModContext().getMainHeldWeapon().getWeapon().builder.isUsingNewSystem()) {
				AnimationModeProcessor.getInstance().legacyMode = false;
			} else {
				AnimationModeProcessor.getInstance().legacyMode = true;
			}
		}
		
		//System.out.println(Mouse.isButtonDown(0));
	
		if (ClientModContext.getContext().getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE)
			return;

		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
		GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);

		Project.gluUnProject(WeaponRenderer.POSITION.get(0), WeaponRenderer.POSITION.get(1), WeaponRenderer.POSITION.get(2), MODELVIEW, PROJECTION, VIEWPORT,
				NEW_POS);
		
		Vec3d newPV = new Vec3d(NEW_POS.get(0), NEW_POS.get(1), NEW_POS.get(2));
		
		/*
		GL11.glPointSize(10f);
		DebugRenderer.setupBasicRender();
		GlStateManager.pushMatrix();
		GlStateManager.translate(newPV.x, newPV.y, newPV.z);
		GlStateManager.enableDepth();
		AnimationModeProcessor.getInstance().renderCross();
		GlStateManager.popMatrix();
		//DebugRenderer.renderPoint(newPV, new Vec3d(1, 0, 0));
		DebugRenderer.destructBasicRender();
		*/
		
		
		
		if(AnimationModeProcessor.getInstance().getFPSMode()) {
			Minecraft.getMinecraft().setIngameNotInFocus();

			//Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
			AnimationModeProcessor.getInstance().onTick();
			
			Minecraft.getMinecraft().player.inventory.currentItem = 0;
			Shader blackScree = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/black"));
			
			
			
			blackScree.use();
			Bloom.renderFboTriangle(Minecraft.getMinecraft().getFramebuffer());
			blackScree.release();
			
			return;
		}
		

        
		
		
		
		
		/*
		AnimationData test = BBLoader.loadAnimationData("emfour.animation.json", "animation.M4A1.test", "main");
		System.out.println(test.getTransitionList().size());
		*/
	//	BBLoader.load();

		
		
		
		
		// Render shell
		//CompatibleShellRenderer.render(shells.getShells());
		shellManager.render();

		PROJECTION.rewind();
		MODELVIEW.rewind();
		
		/*
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();

		GlStateManager.popMatrix();
		*/
		
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
			
			shellManager.update(0.05);
			
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
