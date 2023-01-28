package com.jimholden.conomy.main;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import org.objectweb.asm.Type;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.client.gui.NewInventory;
import com.jimholden.conomy.client.gui.TimedConsumableTracker;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.client.gui.player.CustomDeathScreen;
import com.jimholden.conomy.client.gui.player.GuiTrader;
import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.containers.InventoryInjectorClient;
import com.jimholden.conomy.containers.slots.ISaveableSlot;
import com.jimholden.conomy.containers.slots.SlotDisabled;
import com.jimholden.conomy.drugs.DrugCache;
import com.jimholden.conomy.drugs.DrugRenderer;
import com.jimholden.conomy.drugs.Nausea;
import com.jimholden.conomy.entity.EntityRope;
import com.jimholden.conomy.entity.EntityTestVes;
import com.jimholden.conomy.entity.render.RenderEntityRope;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemBottle;
import com.jimholden.conomy.items.ItemDrugBrick;
import com.jimholden.conomy.items.ItemDrugPowder;
import com.jimholden.conomy.medical.ConsciousCapability;
import com.jimholden.conomy.medical.ConsciousProvider;
import com.jimholden.conomy.medical.IConscious;
import com.jimholden.conomy.medical.PainUtility;
import com.jimholden.conomy.modelHUD.ModelPlane;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.render.CustomHotbar;
import com.jimholden.conomy.render.LUTRenderer;
import com.jimholden.conomy.render.Ragdoll;
import com.jimholden.conomy.render.RenderTool;
import com.jimholden.conomy.render.tesr.PistolStandTESR;
import com.jimholden.conomy.util.Keybinds;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.OpenInventoryServerPacket;
import com.jimholden.conomy.util.packets.RopeDismountPacket;
import com.jimholden.conomy.util.packets.RopeKeyPacket;
import com.mojang.realmsclient.gui.ChatFormatting;

//import net.java.games.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModEventClientHandler {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static InventoryInjectorClient injector = new InventoryInjectorClient();
	public static List<Item> itemModelSwapList;

	public static ArrayList<Ragdoll> ragdolls = new ArrayList<>();

	@SubscribeEvent
	public void pickupEvent(ItemPickupEvent event) {
		CustomHotbar.downTimer = 0.0F;
		CustomHotbar.staticTimer = 0.0F;
	}

	@SubscribeEvent
	public void modelBake(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> reg = event.getModelRegistry();

		ClientProxy.swapModels(ModItems.CLIMBING_ANCHOR, reg);

		ClientProxy.swapModels(ModItems.HEADSET, reg);
		ClientProxy.swapModels(ModItems.USMCHEADSET, reg);
		ClientProxy.swapModels(ModItems.COMTACS, reg);
		ClientProxy.swapModels(ModItems.PLANTRONICS, reg);
		ClientProxy.swapModels(ModItems.GHOSTFACEMASK, reg);
		ClientProxy.swapModels(ModItems.SUNGLASSES, reg);
		ClientProxy.swapModels(ModItems.LEONSJACKET, reg);
		ClientProxy.swapModels(ModItems.TOKYOGHOULMASK, reg);
		// ClientProxy.swapModels(ModItems.MOLLEPLATECARRIER, reg);
		ClientProxy.swapModels(ModItems.REDSCARF, reg);

		ClientProxy.swapModels(ModItems.ALPHAFLJACKET, reg);
		ClientProxy.swapModels(ModItems.JEEPSPIRITJACKET, reg);
		ClientProxy.swapModels(ModItems.HXBODY, reg);
		ClientProxy.swapModels(ModItems.HXVEST, reg);
		ClientProxy.swapModels(ModItems.HXHELMET, reg);
		ClientProxy.swapModels(ModItems.MOLLEPLATECARRIER, reg);
		ClientProxy.swapModels(ModItems.ATLAST7, reg);
		ClientProxy.swapModels(ModItems.GYMPANTS, reg);
		ClientProxy.swapModels(ModItems.JEANS, reg);
		ClientProxy.swapModels(ModItems.KHAKIJEANS, reg);
		ClientProxy.swapModels(ModItems.BLACKHALWOODTUXEDO, reg);
		ClientProxy.swapModels(ModItems.BLACKJEANS, reg);
		ClientProxy.swapModels(ModItems.BLACKFORMALSHIRT, reg);
		ClientProxy.swapModels(ModItems.BLACKMULTICAMOSHIRT, reg);
		ClientProxy.swapModels(ModItems.BLACKSHIRT, reg);
		ClientProxy.swapModels(ModItems.FORESTMILITARYSHIRT, reg);
		ClientProxy.swapModels(ModItems.FORMALSHIRT, reg);
		ClientProxy.swapModels(ModItems.NAVYBLUESHIRT, reg);
		ClientProxy.swapModels(ModItems.TRITONRIG, reg);
		ClientProxy.swapModels(ModItems.BODYARMORIII, reg);

		ClientProxy.swapModels(ModItems.F5SWITCHBLADE, reg);
		ClientProxy.swapModels(ModItems.OAKLEYMECHANISM, reg);
		ClientProxy.swapModels(ModItems.DUFFLEBAG, reg);

		ClientProxy.swapModels(ModItems.FACEBANDANA, reg);
		ClientProxy.swapModels(ModItems.CATEARS, reg);

		ClientProxy.swapModels(ModItems.BLACKSHOES, reg);

		/*
		 * for(int x = 0; x < this.itemModelSwapList.size(); ++x) {
		 * ClientProxy.swapModels(this.itemModelSwapList.get(x), reg); }
		 */

	}

	// public static final Random rand;

	public static Vec3d startVec = new Vec3d(0, 0, 0);
	public static Vec3d endVec = new Vec3d(1, 1, 1);
	public static Vec3d pointVec = new Vec3d(1, 1, 1);

	public boolean displayBlur = false;

	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {

		if (Minecraft.getMinecraft().currentScreen instanceof GuiTrader) {
			if (!displayBlur) {
				displayBlur = true;
				Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));

			}
		} else {
			if (displayBlur) {
				Minecraft.getMinecraft().entityRenderer.stopUseShader();
				displayBlur = false;
			}
		}

		// Minecraft.getMinecraft().entityRenderer.loadShader(new
		// ResourceLocation("shaders/post/blur.json"));

		/*
		 * RENDERS ROPES
		 * 
		 */

		World world = mc.player.world;
		List<Entity> entityList = world.getLoadedEntityList();
		List sortedList;

		/*
		 * mc.entityRenderer.enableLightmap(); float f = entity.prevRotationYaw +
		 * (entity.rotationYaw - entity.prevRotationYaw) * mc.getRenderPartialTicks();
		 * int i = entity.getBrightnessForRender();
		 * 
		 * int j = i % 65536; int k = i / 65536;
		 * OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j,
		 * (float)k);
		 * 
		 */

		long f1 = System.nanoTime();
		for (Entity ent : entityList) {
			if (ent instanceof EntityRope) {
				EntityRope rope = (EntityRope) ent;
				if (rope.shouldRenderRope() || rope.isHangingRope()) {
					Entity entity = ent;
					// RenderHelper.bindTexture(ResourceManager.universal);
					RenderEntityRope.renderRope(rope);
				}
			}

		}
		long f2 = System.nanoTime();
		// System.out.println((f2-f1) + "ns");

		/*
		 * END ROPE RENDER
		 * 
		 */

		// Render ragdolls
		GlStateManager.enableLighting();
		mc.entityRenderer.enableLightmap();
		for (Ragdoll rd : this.ragdolls) {

			rd.render();
		}
		mc.entityRenderer.disableLightmap();

		/*
		 * RENDER LOOKVEC
		 * 
		 * GL11.glPushMatrix(); GL11.glTranslated(0, mc.player.eyeHeight, 0);
		 * GL11.glTranslated(-mc.player.posX, -mc.player.posY, -mc.player.posZ);
		 * GL11.glPushMatrix(); GL11.glPopMatrix();
		 * 
		 * GlStateManager.disableCull(); GlStateManager.disableTexture2D();
		 * GlStateManager.color(1, 0, 0); GL11.glBegin(GL11.GL_LINE_STRIP);
		 * //mc.player.getLook(partialTicks)
		 * //System.out.println(mc.player.getLookVec());
		 * //System.out.println(mc.player.getPositionVector()); Vec3d fixVec =
		 * mc.player.getLookVec().add(mc.player.getPositionVector());
		 * System.out.println(fixVec); GL11.glVertex3d(fixVec.x, fixVec.y, fixVec.z);
		 * GL11.glVertex3d(mc.player.getPositionVector().x,
		 * mc.player.getPositionVector().y, mc.player.getPositionVector().z);
		 * //GL11.glVertex3d(0, 0, 0); GL11.glEnd(); GL11.glTranslated(-mc.player.posX,
		 * -mc.player.posY, -mc.player.posZ); GlStateManager.enableCull();
		 * GlStateManager.enableTexture2D(); GL11.glPopMatrix();
		 * 
		 * 
		 * 
		 */

		// FORTNIE FUN

		/*
		 * GL11.glPushMatrix(); //GL11.glTranslated(-mc.player.posX, -mc.player.posY,
		 * -mc.player.posZ);
		 * //Minecraft.getMinecraft().renderEngine.bindTexture(NewInventory.
		 * INVENTORY_BACKGROUND);
		 * 
		 * Vec3d test1 = new Vec3d(2, 3, 0); Vec3d test2 = new Vec3d(0, 0, 0);
		 * //GlStateManager.enableLighting(); //GlStateManager.rotate(45, 1.0F, 0.0F,
		 * 0.0F); RopeSegment seg = RopeUtil.getRopeSegment(test1, test2);
		 * ResourceLocation resl = new ResourceLocation(Reference.MOD_ID +
		 * ":textures/entity/rope.png");
		 * Minecraft.getMinecraft().getTextureManager().bindTexture(resl); Tessellator t
		 * = Tessellator.getInstance(); BufferBuilder buf = t.getBuffer();
		 * buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		 * 
		 * float scalar = 1.0F; double xO = 0; double yO = 0; double zO = 0;
		 * //AxisAlignedBB aabb = new AxisAlignedBB(0, 0, 0, 1, 1, 1); // Green //TOP
		 * double ax = 1.0F; double bx = -1.0F;
		 * 
		 * double ay = 1.0F; double by = -1.0F;
		 * 
		 * double az = 1.0F; double bz = 1.0F;
		 * 
		 * buf.pos(ax, ay, by).tex(0, 0).endVertex(); buf.pos(bx, ay, by).tex(0,
		 * 1*scalar).endVertex(); buf.pos(bx, ay, ay).tex(1*scalar,
		 * 1*scalar).endVertex(); buf.pos(ax, ay, ay).tex(1*scalar, 0).endVertex();
		 * 
		 * //BOT buf.pos(ax, by, ay).tex(0, 0).endVertex(); buf.pos(bx, by, ay).tex(0,
		 * 1).endVertex(); buf.pos(bx, by, by).tex(1, 1).endVertex(); buf.pos(ax, by,
		 * -1.0f).tex(1, 0).endVertex();
		 * 
		 * // Front face (z = 1.0f) //glColor3f(1.0f, 0.0f, 0.0f); // Red buf.pos(ax,
		 * ay, 1.0f).tex(0, 0).endVertex(); buf.pos(bx, ay, 1.0f).tex(0, 1).endVertex();
		 * buf.pos(bx, by, 1.0f).tex(1, 1).endVertex(); buf.pos(ax, by, 1.0f).tex(1,
		 * 0).endVertex();
		 * 
		 * // Back face (z = -1.0f) //glColor3f(1.0f, 1.0f, 0.0f); // Yellow buf.pos(ax,
		 * by, -1.0f).tex(0, 0).endVertex(); buf.pos(bx, by, -1.0f).tex(0,
		 * 1).endVertex(); buf.pos(bx, ay, -1.0f).tex(1,1).endVertex(); buf.pos(ax, ay,
		 * -1.0f).tex(1, 0).endVertex();
		 * 
		 * // Left face (x = -1.0f) //glColor3f(0.0f, 0.0f, 1.0f); // Blue buf.pos(bx,
		 * ay, 1.0f).tex(0, 0).endVertex(); buf.pos(bx, ay, -1.0f).tex(0,
		 * 1).endVertex(); buf.pos(bx, by, -1.0f).tex(1,1).endVertex(); buf.pos(bx, by,
		 * 1.0f).tex(1, 0).endVertex();
		 * 
		 * // Right face (x = 1.0f) //glColor3f(1.0f, 0.0f, 1.0f); // Magenta
		 * buf.pos(ax, ay, -1.0f).tex(0, 0).endVertex(); buf.pos(ax, ay, 1.0f).tex(0,
		 * 1).endVertex(); buf.pos(ax, by, 1.0f).tex(1,1).endVertex(); buf.pos(ax, by,
		 * -1.0f).tex(1, 0).endVertex();
		 * 
		 * t.draw(); //glEnd(); // End of drawing color-cube
		 * 
		 * GL11.glPopMatrix();
		 */
		// end gamer time

		// COMMENTED OUT FOR DEBUG
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 0);
		// System.out.println(startVec + " | " + endVec);
		GL11.glTranslated(-mc.player.posX, -mc.player.posY, -mc.player.posZ);
		// GL11.glTranslatef(0.5f, 1.0f, 0.5f);

		GL11.glPushMatrix();
		GL11.glPopMatrix();
		// GL11.glTranslatef(0, -0.5F, 0);
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0, 0, 0);

		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(startVec.x, startVec.y, startVec.z);
		GL11.glVertex3d(endVec.x, endVec.y, endVec.z);
		GL11.glEnd();

		// System.out.println(pointVec);
		//
		GL11.glPointSize(20.0F);
		GlStateManager.color(1, 0, 0);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex3d(pointVec.x, pointVec.y, pointVec.z);
		GL11.glEnd();
		//
		GL11.glTranslated(mc.player.posX, mc.player.posY, mc.player.posZ);
		GlStateManager.enableCull();
		GlStateManager.enableTexture2D();
		GL11.glPopMatrix();

		// GL11.glPopMatrix();

		/*
		 * for(int x = 0; x < this.mc.world.playerEntities.size(); ++x) {
		 * 
		 * //System.out.println(ent); //ent.render
		 * 
		 * EntityPlayer ent = this.mc.world.playerEntities.get(x);
		 * 
		 * 
		 * //System.out.println("IS?: " + this.mc.player == ent);
		 * 
		 * if(this.mc.player != ent) { //ent.posX += 0.1;
		 * //System.out.println(ent.isInRangeToRenderDist(64.0D));
		 * //System.out.println("taking over render!"); //Entity ent2 = new Entity
		 * this.mc.getRenderManager().renderEntityStatic(ent,
		 * this.mc.getRenderPartialTicks(), true); }
		 * 
		 * //this.mc.getRenderManager().renderEntity(ent2, ent.posX, ent.posY+1,
		 * ent.posZ, ent.rotationYaw, this.mc.getRenderPartialTicks(), false);
		 * 
		 * }
		 */

		/*
		 * EntityPlayer player = world.getPlayerEntityByName("fuck");
		 * player.shouldRenderInPass(pass)
		 */

		// for(int x = 0; x < event.)

	}

	public static EntityTestVes etV = null;

	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {

		// TICK TIMED CONSUMABLES
		if (TimedConsumableTracker.isConsuming()) {
			TimedConsumableTracker.tick();

		}

		if (Minecraft.getMinecraft().player != null
				&& Minecraft.getMinecraft().player.hasCapability(ConsciousProvider.CONSCIOUS, null)) {
			if (Minecraft.getMinecraft().player.getCapability(ConsciousProvider.CONSCIOUS, null).hasSplint()) {
				if (Minecraft.getMinecraft().player.isSprinting()) {
					Minecraft.getMinecraft().player.setSprinting(false);

				}
			}
		}

		if (Keybinds.ropeUp.isKeyDown()) {
			Main.NETWORK.sendToServer(new RopeKeyPacket(mc.player.getEntityId(), -0.1F));
		}
		if (Keybinds.ropeDown.isKeyDown()) {
			Main.NETWORK.sendToServer(new RopeKeyPacket(mc.player.getEntityId(), 0.1F));
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_W) && etV != null) {
			etV.isRevving = true;
		} else if (etV != null) {
			etV.isRevving = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S) && etV != null) {
			etV.isBraking = true;
		} else if (etV != null) {
			etV.isBraking = false;
		}
		// System.out.println(this.mc.currentScreen);
		// tick += 1;

		// System.out.println(5*Math.sin(tick*5));
		/*
		 * if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
		 * this.mc.world.spawnParticle(EnumParticleTypes.FLAME, this.mc.player.posX,
		 * this.mc.player.posY, this.mc.player.posZ, 0.7, 0.3*Math.sin(tick), 0, null);
		 * }
		 */

		// this.mc.setIngameNotInFocus();
		// System.out.println(this.mc.inGameHasFocus);

		/*
		 * World world = this.mc.world; System.out.println("yes"); for(int x = 0; x <
		 * world.loadedEntityList.size(); ++x) { Entity ent =
		 * world.loadedEntityList.get(x);
		 * 
		 * this.mc.getRenderManager().renderEntity(ent, ent.posX, ent.posY+1, ent.posZ,
		 * ent.rotationYaw, this.mc.getRenderPartialTicks(), true);
		 * //world.loadedEntityList.get(x).render }
		 */

		// ReflectionHelper.findMethod(Entity.class, methodName, methodObfName,
		// parameterTypes)

		/*
		 * if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
		 * 
		 * EntityPlayer playerIn = mc.player;
		 * 
		 * if(event.phase == Phase.END) { playerIn.limbSwing = 0;
		 * playerIn.prevLimbSwingAmount = 0; playerIn.limbSwingAmount = 0;
		 * 
		 * }
		 * 
		 * float yaw = playerIn.rotationYaw; float pitch = playerIn.rotationPitch; float
		 * f = 0.3F; double motionX = (double)(-MathHelper.sin(yaw / 180.0F *
		 * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		 * double motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) *
		 * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f); double motionY =
		 * (double)(-MathHelper.sin((pitch) / 180.0F * (float)Math.PI) * f);
		 * playerIn.addVelocity(motionX, motionY, motionZ); //this.mc.world.spawnp
		 * //this.mc.world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed,
		 * ySpeed, zSpeed, parameters); //WorldServer worldserver = ((WorldServer)
		 * this.mc.world); //worldserver.spawnpa
		 * //worldserver.spawnParticle(EnumParticleTypes.FLAME, true, playerIn.posX,
		 * playerIn.posY, playerIn.posZ, 10, -motionX, -motionY, -motionZ, 0.5F, 0);
		 * for(int x = 0; x < 10; x++) { //rand.nextInt();
		 * this.mc.world.spawnParticle(EnumParticleTypes.FLAME, playerIn.posX,
		 * playerIn.posY, playerIn.posZ, -motionX*2, -motionY*2, -motionZ*2, 20);
		 * 
		 * }
		 * 
		 * //return par1ItemStack; //playerIn.addVelocity(0.1F, 0.5F, 0.1F);
		 * //PacketDispatcher.wrapper.sendToServer(new AuxButtonPacket(0, 0, 0, 999,
		 * 0)); }
		 */
	}

	/*
	 * private void addInventoryOverlaySlot(Container cont, Slot slotIn) {
	 * slotIn.slotNumber = cont.inventorySlots.size(); int diff = cont. slotIn.xPos
	 * -= cont.gui cont.inventorySlots. }
	 */

	public static final ScaledResolution res = new ScaledResolution(mc);
	public static ContainerInvExtend overlayCont = null;

	@SubscribeEvent
	public void foregroundDrawEvent(GuiScreenEvent.DrawScreenEvent event) {
		if (event.getGui() instanceof GuiContainer && !(event.getGui() instanceof GuiContainerCreative)) {
			if (CompatibilityChecker.shouldNotInjectGUI())
				return;
			this.injector.drawForeground(event);
		}

	}

	@SubscribeEvent
	public void guiOpen(GuiOpenEvent event) {

		if (this.mc.player != null) {
			if (this.mc.player.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1
					&& !(event.getGui() instanceof GuiIngameMenu) && !(event.getGui() instanceof GuiMainMenu)
					&& !(event.getGui() instanceof CustomDeathScreen) && !(event.getGui() instanceof GuiChat)) {
				event.setCanceled(true);
				return;
			}
		}

		if (event.getGui() instanceof GuiGameOver && !isAllowedToShowGameOver) {
			event.setCanceled(true);
		}

		/*
		 * try { System.out.println("lady");
		 * 
		 * ReflectionHelper.findField(Entity.class,
		 * "renderDistanceWeight").setAccessible(true); Field field =
		 * ReflectionHelper.findField(Entity.class, "renderDistanceWeight");
		 * field.set(null, 300.0D);
		 * 
		 * 
		 * //System.i } catch(Exception e) { e.printStackTrace(); }
		 */

		// System.out.println("sup");
		if (CompatibilityChecker.shouldNotInjectGUI())
			return;
		if (overlayCont == null && this.mc.player != null) {
			overlayCont = new ContainerInvExtend(this.mc.player.inventory, this.mc.player.world.isRemote,
					this.mc.player);
		}
		if (event.getGui() instanceof GuiInventory) {

			if (mc.player.isCreative())
				return;
			event.setCanceled(true);
			// mc.displayGuiScreen(new NewInventory(mc.player, mc.world.isRemote));

			Main.NETWORK.sendToServer(new OpenInventoryServerPacket(mc.player.getEntityId()));
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.OPEN_ONE, 1.0F));

			// event.setCanceled(true);
			// mc.displayGuiScreen(new StockGui(mc.player));
		}

		/*
		 * if(this.mc.player != null) { if(event.getGui() instanceof GuiContainer) {
		 * GuiContainer gc = (GuiContainer) event.getGui(); Container c =
		 * gc.inventorySlots; IInvCapa capa =
		 * this.mc.player.getCapability(InvProvider.EXTRAINV, null); if(event.getGui()
		 * instanceof GuiContainer && !(event.getGui() instanceof GuiContainerCreative)
		 * && !(event.getGui() instanceof NewInventory)) { for(int x = 0; x <
		 * overlayCont.inventorySlots.size(); ++x) { addInventorySlot(gc, c,
		 * overlayCont.inventorySlots.get(x)); } } }
		 * 
		 * }
		 */

		// System.out.println("flag0");

		/*
		 * DEBUG CODE if(event.getGui() instanceof GuiContainer && !(event.getGui()
		 * instanceof GuiContainerCreative)) { if(!(event.getGui() instanceof
		 * NewInventory)) { Container c = ((GuiContainer)
		 * event.getGui()).inventorySlots; ItemStackHandler handler = new
		 * ItemStackHandler(1); addSlotToContainer(c, new SlotItemHandler(handler, 0, 0,
		 * 150)); //c.inventorySlots.add(new SlotItemHandler(handler, 0, 0, 150)); }
		 * 
		 * }
		 */

		if (this.mc.player != null && event.getGui() instanceof GuiContainer
				&& !(event.getGui() instanceof GuiContainerCreative) && !(event.getGui() instanceof NewInventory)
				&& !(event.getGui() instanceof GuiInventory)) {
			System.out.println("client injecting into " + event.getGui());
			injector.slotInjection(event);
		}

		if (event.getGui() instanceof GuiContainer && !(event.getGui() instanceof GuiContainerCreative)) {
			try {
				Container c = ((GuiContainer) event.getGui()).inventorySlots;
				for (int i = 0; i < c.inventorySlots.size(); ++i) {
					Slot slot = c.inventorySlots.get(i);
					if (c.inventorySlots.get(i).getSlotIndex() >= 9 && c.inventorySlots.get(i).getSlotIndex() < 36
							&& c.inventorySlots.get(i).inventory == this.mc.player.inventory) {

						c.inventorySlots.remove(i);
						c.inventorySlots.add(i, new SlotDisabled(slot));

					}

				}
			} catch (Exception e) {

			}
		}

		/*
		 * if((event.getGui() instanceof GuiContainer)) { GuiContainer gc =
		 * (GuiContainer) event.getGui(); if ((gc instanceof GuiInventory || gc
		 * instanceof GuiContainerCreative) &&
		 * Minecraft.getMinecraft().player.capabilities.isCreativeMode) { return; }
		 * 
		 * Container c = gc.inventorySlots; for (int i = 0; i < c.inventorySlots.size();
		 * ++i) { Slot s = c.inventorySlots.get(i); if (s instanceof BackpackSlots &&
		 * !(s instanceof AdvancedSlot)) { AdvancedSlot wrapper = new AdvancedSlot(s,
		 * c); c.inventorySlots.remove(i); c.inventorySlots.add(i, wrapper); } }
		 * 
		 * }
		 */

	}

	public static final ResourceLocation BACKPACK_SLOT = new ResourceLocation(
			Reference.MOD_ID + ":textures/gui/customslots.png");

	@SubscribeEvent
	public void keyboard(KeyInputEvent event) {
		System.out.println("hi");

		if (Keyboard.isKeyDown(Keyboard.KEY_BACKSLASH)) {
			PistolStandTESR.crs = null;
			PistolStandTESR.simulation = null;
			this.ragdolls.clear();

			// 233, 69, 229.7
			Vec3d pos = Minecraft.getMinecraft().player.getPositionVector()
					.add(Minecraft.getMinecraft().player.getLookVec().scale(3));

			this.ragdolls.add(new Ragdoll(pos.x, pos.y + 1.0, pos.z, 0.05, -0.2, 0.1));

		}

		if (Keybinds.ropeDismount.isPressed()) {
			// System.out.println("yo");
			Main.NETWORK.sendToServer(new RopeDismountPacket(mc.player.getEntityId()));
		}
	}

	/*
	 * @SubscribeEvent public void guiTool(GuiScreenEvent.InitGuiEvent evt) {
	 * if(evt.getGui() instanceof GuiContainer) { GuiContainer gc = (GuiContainer)
	 * evt.getGui(); GuiWrenchTool.setGuiContainerLeft(gc, gc.getGuiLeft()+125);
	 * 
	 * } }
	 */
	public static NewInventory inv = null;

	private boolean isAllowedToShowGameOver = false;

	/*
	 * @SubscribeEvent public void guiForegroundLayer(GuiScreenEvent.DrawScreenEvent
	 * event) { if(this.mc.player == null) return; if(inv == null) { inv = new
	 * NewInventory(mc.player, mc.player.world.isRemote, true); } if(event.getGui()
	 * instanceof GuiContainer) { //GuiTestContainer cont =
	 * GuiTestContainer(((GuiContainer) event.getGui()).inventorySlots)
	 * 
	 * ScaledResolution scaledresolution = new ScaledResolution(this.mc); int i =
	 * scaledresolution.getScaledWidth(); int j =
	 * scaledresolution.getScaledHeight(); inv.setWorldAndResolution(this.mc, i, j);
	 * inv.drawScreen(this.mc.mouseHelper.deltaX, this.mc.mouseHelper.deltaY,
	 * this.mc.getRenderPartialTicks()); }
	 * 
	 * }
	 */

	/*
	 * @SubscribeEvent public void
	 * guiRenderEvent(GuiScreenEvent.BackgroundDrawnEvent event) { if(event.getGui()
	 * instanceof GuiContainer) { GuiContainer gc = (GuiContainer) event.getGui();
	 * GuiWrenchTool.setGuiContainerLeft(gc, gc.getGuiLeft()+100); } }
	 */

	@SubscribeEvent
	public void drawGui(GuiScreenEvent.BackgroundDrawnEvent event) {

		// System.out.println(event.getGui());

		if (CompatibilityChecker.shouldNotInjectGUI())
			return;
		// System.out.println("hi");

		this.mc.getTextureManager().bindTexture(BACKPACK_SLOT);

		// this.drawTexturedModalRect(24, 142, 0, 0, 16, 16);

		if (event.getGui() instanceof GuiContainer && !(event.getGui() instanceof GuiContainerCreative)
				&& !(event.getGui() instanceof NewInventory)) {
			if (!(event.getGui() instanceof GuiTrader))
				injector.drawInventory(event);

			GuiContainer gc = ((GuiContainer) event.getGui());
			Container c = ((GuiContainer) event.getGui()).inventorySlots;
			for (Slot slotIn : c.inventorySlots) {
				if (slotIn instanceof ISaveableSlot) {
					// GlStateManager.disableLighting();
					// GlStateManager.disableDepth();
					// GlStateManager.enableTexture2D();

					// System.out.println("yo: " + slotIn.xPos + " | " + slotIn.yPos);
					this.mc.getTextureManager().bindTexture(BACKPACK_SLOT);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					this.drawTexturedModalRect(gc.getGuiLeft() + slotIn.xPos, gc.getGuiTop() + slotIn.yPos, 0, 0, 16,
							16);
					// this.drawTexturedModalRect(i, j, 0, 0, 16, 16);
					// GlStateManager.disableTexture2D();
					// GlStateManager.enableDepth();
					// GlStateManager.enableLighting();
				}
			}

		}

		/*
		 * if(event.getGui() instanceof GuiContainer) { GuiContainer gc = (GuiContainer)
		 * event.getGui();
		 * 
		 * drawTexturedModalRect(gc.getGuiLeft(), gc.getGuiTop(), 0, 0, gc.getXSize(),
		 * 45); }
		 */

		// GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		/*
		 * if(event.getGui() instanceof GuiContainer && !(event.getGui() instanceof
		 * GuiContainerCreative)) {
		 * 
		 * int guiLeft = ((GuiContainer) event.getGui()).getGuiLeft(); int guiTop =
		 * ((GuiContainer) event.getGui()).getGuiTop(); Container c = ((GuiContainer)
		 * event.getGui()).inventorySlots;
		 * 
		 * for(int m = 0; m < c.inventorySlots.size(); ++m) { Slot slot =
		 * c.inventorySlots.get(m); if(slot instanceof SlotDisabled) {
		 * GlStateManager.enableTexture2D();
		 * 
		 * 
		 * 
		 * this.mc.getTextureManager().bindTexture(BACKPACK_SLOT);
		 * GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		 * 
		 * this.drawTexturedModalRectWithZ(guiLeft + slot.xPos, guiTop + slot.yPos, 32,
		 * 0, 16, 16, 200.0F); //this.drawTexturedModalRect(i, j, 0, 0, 16, 16);
		 * GlStateManager.enableTexture2D(); } }
		 * 
		 * }
		 */

		/*
		 * ---------- THE GOOD DEBUG CODE --------------- Works w/ chests
		 * if(event.getGui() instanceof GuiContainer && !(event.getGui() instanceof
		 * GuiContainerCreative)) {
		 * 
		 * int guiLeft = ((GuiContainer) event.getGui()).getGuiLeft(); int guiTop =
		 * ((GuiContainer) event.getGui()).getGuiTop(); Container c = ((GuiContainer)
		 * event.getGui()).inventorySlots;
		 * this.mc.getTextureManager().bindTexture(NewInventory.INVENTORY_BACKGROUND);
		 * drawTexturedModalRectWithZ(guiLeft+7, guiTop+83, 0, 173, 162, 55, 750.0F);
		 */
		/*
		 * for(int m = 0; m < c.inventorySlots.size(); ++m) { Slot slot =
		 * c.inventorySlots.get(m); if(slot instanceof SlotDisabled) {
		 * if(!list.containsKey(0) && !list.containsKey(1)) { //topLeft = Pair<>;
		 * Map<Integer, Integer> newMap = null; newMap.put(slot.xPos, slot.yPos);
		 * 
		 * 
		 * list.put(0, newMap); list.put(1, newMap); } else { int topLeftX =
		 * list.get(0).get(0); int topLeftY = list.get(0).get(1); int rightX =
		 * list.get(1).get(0); int rightY = list.get(1).get(1);
		 * 
		 * if(slot.xPos < topLeftX && slot.yPos < topLeftY) { Map<Integer, Integer>
		 * newMap = null; newMap.put(slot.xPos, slot.yPos);
		 * 
		 * list.put(0, newMap); } if(slot.xPos > rightX && slot.yPos > rightY) {
		 * Map<Integer, Integer> newMap = null; newMap.put(slot.xPos, slot.yPos);
		 * 
		 * list.put(1, newMap); } } }
		 * 
		 * 
		 * }
		 */

		// System.out.println("FUCKR: " + topRight.second());

	}

	/*
	 * 
	 * @SubscribeEvent public void bakeEvent(ModelBakeEvent event) { Object obj =
	 * event.getModelRegistry().getObject(event.getRe); if(obj instanceof
	 * IBakedModel) { IBakedModel model = (IBakedModel) obj;
	 * BackpackTESIR.INSTANCE.itemModel = model;
	 * event.getModelRegistry().putObject(RedstoneSword.rsModel, new
	 * BackpackTESIR()); } }
	 * 
	 * public static void swapModels(Item item, IRegistry<ModelResourceLocation,
	 * IBakedModel> reg) { ModelResourceLocation loc = new
	 * ModelResourceLocation(item.getRegistryName(), "inventory"); IBakedModel model
	 * = reg.getObject(loc); TileEntityItemStackRenderer render =
	 * item.getTileEntityItemStackRenderer(); if(render instanceof TEISRBase) {
	 * ((TEISRBase) render).itemModel = model; reg.putObject(loc, new
	 * BakedModelCustom((TEISRBase) render)); }
	 * 
	 * }
	 */

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.phase == Phase.START) {
			return;
		}
		// return;
		/*
		 * if(Keyboard.isKeyDown(Keyboard.KEY_NUMLOCK)) { AxisAlignedBB axisalignedbb =
		 * event.player.getEntityBoundingBox(); double width = 0.6; double height = 1.8;
		 * 
		 * event.player.setEntityBoundingBox(BoxUtil.rotate(new
		 * AxisAlignedBB(event.player.posX - height, event.player.posY - width / 2D,
		 * event.player.posZ - width / 2D, event.player.posX, event.player.posY + width
		 * / 2D, event.player.posZ + width / 2D), EnumFacing.SOUTH));
		 * 
		 * }
		 */
		/*
		 * AxisAlignedBB axisalignedbb = event.player.getEntityBoundingBox(); double
		 * width = 0.6; double height = 1.8;
		 * 
		 * event.player.setEntityBoundingBox(new AxisAlignedBB(event.player.posX -
		 * height, event.player.posY - width / 2D, event.player.posZ - width / 2D,
		 * event.player.posX, event.player.posY + width / 2D, event.player.posZ + width
		 * / 2D));
		 */
	}

	public static Field r_ticksElytraFlying;

	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Pre event) {

		event.getEntityPlayer().hurtTime = 0;
		EntityPlayer player = event.getEntityPlayer();
		/*
		 * RENDERPLAYER EntityPlayer player = event.getEntityPlayer(); Vec3d look =
		 * player.getLook(this.mc.getRenderPartialTicks()); GL11.glPushMatrix();
		 * 
		 * //GL11.glRotated(angle, x, y, z); GlStateManager.rotate(180.0F -
		 * player.renderYawOffset, 0.0F, 1.0F, 0.0F);
		 * GL11.glRotated((-player.rotationPitch-90), 1, 0, 0); Vector2f lookXZ = new
		 * Vector2f((float)look.x, (float)look.z); Vector2f rotXZ = new
		 * Vector2f(MathHelper.cos((float)
		 * Math.toRadians(player.renderYawOffset+90)),MathHelper.sin((float)
		 * Math.toRadians(player.renderYawOffset+90))); if(lookXZ.lengthSquared() != 0
		 * && rotXZ.lengthSquared() != 0){ lookXZ = (Vector2f) lookXZ.normalise(); rotXZ
		 * = (Vector2f) rotXZ.normalise(); float angle = (float)
		 * Math.acos(Math.max(Vector2f.dot(lookXZ, rotXZ), 0)); //Apparently a Vector2f
		 * doesn't have a cross product function float cross =
		 * lookXZ.y*rotXZ.x-rotXZ.y*lookXZ.x;
		 * GL11.glRotated(Math.toDegrees(angle)*Math.signum(cross), 0, 1, 0); }
		 * GlStateManager.rotate(-(180.0F - player.renderYawOffset), 0.0F, 1.0F, 0.0F);
		 * 
		 * if(r_ticksElytraFlying == null) r_ticksElytraFlying =
		 * ReflectionHelper.findField(EntityLivingBase.class, "ticksElytraFlying",
		 * "field_184629_bo"); try { r_ticksElytraFlying.setInt(player, 1); }
		 * catch(IllegalArgumentException | IllegalAccessException e) {
		 * e.printStackTrace(); }
		 */

		// GL11.glRotated(lookVec.x*360, 1, 0, 0);
		// GL11.glRotated(lookVec.y*360, 0, 1, 0);
		// GL11.glRotated(lookVec.z*360, 0, 0, 1);

		if (player.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
			GL11.glPushMatrix();

			EntityPlayer entity = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();

			/*
			 * entity.prevRotationYaw = -900; entity.rotationYaw = -900;
			 * entity.prevRotationPitch = 0; entity.rotationPitch = 0;
			 */
			// entity.limbSwing = 0;
			// entity.limbSwingAmount = 0;
			// entity.prevLimbSwingAmount = 0;
			// entity.rotationYawHead = 0;
			// entity.prevRotationYawHead = 0;

			// entity.getBedOrientationInDegrees()
			// entity.cameraYaw = 0;
			// entity.rotationYawHead = 30;

			// entity.
			// System.out.println(entity.rotationYaw + " | " + entity.rotationPitch + " |
			// ");
			float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
			double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
			double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
			double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
			double x = d0 - d3;
			double y = d1 - d4;
			double z = d2 - d5;
			GL11.glTranslated(x, y, z);
			GL11.glRotated(90, 1, 0, 0);
			GL11.glTranslated(-x, -y, -z);

			// GL11.glTranslated(event.getEntityPlayer().posX, event.getEntityPlayer().posY,
			// event.getEntityPlayer().posZ);
		}

	}

	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Post event) {
		EntityPlayer player = event.getEntityPlayer();
		// GL11.glPopMatrix();

		if (player.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
			GL11.glPopMatrix();
		}

	}

	@SubscribeEvent
	public void itemColorsEvent(ColorHandlerEvent.Item evt) {
		evt.getItemColors().registerItemColorHandler((ItemStack stack, int color) -> {
			int color1 = ((ItemDrugPowder) stack.getItem()).getColor(stack);
			return color1;
		}, ModItems.POWDER);

		evt.getItemColors().registerItemColorHandler((ItemStack stack, int color) -> {
			if (color == 1) {
				int color1 = ((ItemBottle) stack.getItem()).getColor(stack);
				return color1;
			}
			return 0xFFFFFF;

		}, ModItems.DRUGBOTTLE);

		evt.getItemColors().registerItemColorHandler((ItemStack stack, int color) -> {
			if (color == 0) {
				int color1 = ((ItemDrugBrick) stack.getItem()).getColor(stack);
				return color1;
			}
			return 0xFFFFFF;

		}, ModItems.DRUGBRICKNOWRAP);

		evt.getItemColors().registerItemColorHandler((ItemStack stack, int color) -> {
			if (color == 0) {
				int color1 = ((ItemDrugBrick) stack.getItem()).getColor(stack);
				return color1;
			}
			return 0xFFFFFF;

		}, ModItems.DRUGBRICKPAPER);

		evt.getItemColors().registerItemColorHandler((ItemStack stack, int color) -> {
			if (color == 0) {
				int color1 = ((ItemDrugBrick) stack.getItem()).getColor(stack);
				return color1;
			}
			return 0xFFFFFF;

		}, ModItems.DRUGBRICKSARAN);

		evt.getItemColors().registerItemColorHandler((ItemStack stack, int color) -> {
			if (color == 0) {
				int color1 = ((ItemDrugBrick) stack.getItem()).getColor(stack);
				return color1;
			}
			return 0xFFFFFF;

		}, ModItems.DRUGBRICKWAX);

	}

	@SubscribeEvent
	public void inputUpdate(InputUpdateEvent event) {
		EntityPlayer p = mc.player;
		if (p == null)
			return;

		/*
		 * try { //System.out.println(this.getClass().getName());
		 * //System.out.println(ReflectionHelper.findField(ClassLo, "balance"));
		 * //System.out.println(com.google.common.reflect.Reflection.(
		 * "com.jimholden.conomy.main.CreditCurrency"));
		 * //System.out.println(CreditCurrency.class.getName());
		 * //System.out.println(Class.forName(
		 * "com.vicmatskiv.weaponlib.inventory.CustomPlayerInventory")); /* Method m =
		 * ReflectionHelper.findMethod(Class.forName(
		 * "com.vicmatskiv.weaponlib.compatibility.CompatibleCustomPlayerInventoryCapability"
		 * ), "getInventory", null, EntityLivingBase.class); Object inventory =
		 * m.invoke(null, p); ItemStack[] test = null; if(inventory != null){ test =
		 * (ItemStack[]) ReflectionHelper.findField(Class.forName(
		 * "com.vicmatskiv.weaponlib.inventory.CustomPlayerInventory"),
		 * "inventory").get(inventory); } ItemStack stack = test[0];
		 * 
		 * 
		 * System.out.println(stack.getTagCompound().toString());
		 * 
		 * //System.out.println(ReflectionHelper.findField(Class.forName(
		 * "com.vicmatskiv.weaponlib.inventory.CustomPlayerInventory"), "inventory"));
		 * 
		 * //System.out.println(ReflectionHelper.findField(Class.forName(
		 * "com.jimholden.conomy.capabilities.CreditCurrency"), "balance"));
		 * //System.out.println(ReflectionHelper.findField(Class.forName(
		 * "com.jimholden.conomy.capabilities.CreditCurrency", true,
		 * LaunchClassLoader.getSystemClassLoader()), "balance"));
		 * //System.out.println(CreditCurrency.class.getClassLoader());
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// System.out.println("YeP: " + p.getCapability(ConsciousProvider.CONSCIOUS,
		// null).isDowned());
		if (p.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
			// mc.setIngameNotInFocus();
			MovementInput m = event.getMovementInput();
			// this.mc.mouseHelper.deltaX = 0;
			m.moveForward = 0;
			m.moveStrafe = 0;
			m.jump = false;
			m.sneak = false;

		}
	}

	/*
	 * @SubscribeEvent public void renderWorld(RenderWorldLastEvent event) {
	 * GL11.glPushMatrix(); Tessellator t = Tessellator.getInstance(); BufferBuilder
	 * buf = t.getBuffer(); buf.begin(GL11.GL_QUADS,
	 * DefaultVertexFormats.POSITION_TEX);
	 * 
	 * buf.pos(0, 20, 0).tex(0, 1).endVertex(); buf.pos(20, 0, 0).tex(0,
	 * 0).endVertex(); buf.pos(20, 20, 0).tex(1, 0).endVertex(); buf.pos(0, 0,
	 * 0).tex(1, 1).endVertex(); t.draw();
	 * 
	 * 
	 * GL11.glPopMatrix(); }
	 */

	@SubscribeEvent
	public void offsetFOV(FOVUpdateEvent fov) {

		double pain = PainUtility.getNetPain(Minecraft.getMinecraft().player);
		// System.out.println(Minecraft.getMinecraft().player.getCapability(ConsciousProvider.CONSCIOUS,
		// null).getApplicator());
		if (pain > 45) {

			float fac = (float) ((float) 1f - (pain / 500f));

			// System.out.println(fac);

			fov.setNewfov(fov.getFov() * fac);
		}

	}

	float cameraTicker = 0;
	boolean hasStarted = false;
	public static boolean startMenuTimer = false;
	public static int startMenuTimerTime = 0;
//	public static boolean lock = false;
	int deathAnimTicker = 0;
	float deathAnimTime = 55.0F;
	public static boolean blackLock = false;
	public static int blackOutTime;

	@SubscribeEvent
	public void cameraSetup(CameraSetup event) {
		EntityPlayer p = mc.player;
		if (p == null)
			return;

		IConscious cDat = p.getCapability(ConsciousProvider.CONSCIOUS, null);

		if (hasStarted && p.getHealth() > 0) {
			hasStarted = false;
			deathAnimTicker = 0;
		}

		if (p.getHealth() == 0.0F && !hasStarted) {
			hasStarted = true;
		}

		if (p.getHealth() == 0.0F && hasStarted) {
			GlStateManager.translate(0, 1 * ((float) deathAnimTicker / deathAnimTime),
					1.8 * ((float) deathAnimTicker / deathAnimTime));
			event.setRoll(35F * ((float) deathAnimTicker / deathAnimTime));
			event.setPitch(25F * ((float) deathAnimTicker / deathAnimTime));

			if (deathAnimTicker < deathAnimTime) {
				deathAnimTicker += 1;
			} else if (!startMenuTimer && hasStarted && !(this.mc.currentScreen instanceof CustomDeathScreen)) {
				// System.out.println("hey!");
				hasStarted = false;
				startMenuTimer = true;
				blackLock = true;
			}
		}

		if (startMenuTimer) {
			// System.out.println(startMenuTimerTime);
			startMenuTimerTime += 1;
			blackOutTime += 1;
			if (startMenuTimerTime > 60) {
				// System.out.println("hi");
				startMenuTimer = false;
				if (!(this.mc.currentScreen instanceof CustomDeathScreen)) {
					this.mc.displayGuiScreen(new CustomDeathScreen(new TextComponentString("LIGMA")));
					startMenuTimerTime = 0;
				}

			}

		}

		if (DrugCache.hasNauseatingDrug() || cDat.getPainLevel() > 35) {
			Nausea n = DrugCache.getTotalNausea();

			cameraTicker += n.speed;

			float f1 = 0.3F + n.hor;

			float f2 = (n.vert + 5.0F) / (f1 * f1 + 5.0F) - f1 * 0.04F;
			f2 = f2 * f2;
			GlStateManager.rotate(((float) cameraTicker + cameraTicker) * n.intensity, 0.0F, 1.0F, 1.0F);
			// System.out.println(n.intensity);
			GlStateManager.scale(1.0F / f2, 1.0F, 1.0F);
			GlStateManager.rotate(-((float) cameraTicker + cameraTicker) * n.intensity, 0.0F, 1.0F, 1.0F);
		} else {
			cameraTicker = 0;
		}

		if (p.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
			// this.mc.player.respawnPlayer();
			GlStateManager.translate(0, 0, 1.8);
			event.setRoll(35F);
			event.setPitch(25F);

		}
		double painLevel = PainUtility.getNetPain(Minecraft.getMinecraft().player);

		if (painLevel > 35) {
			double s = 0.1 * painLevel / 500.0;
			GlStateManager.rotate((float) s * 20, (float) Math.random(), (float) Math.random(), (float) Math.random());
			GlStateManager.translate(Math.random() * s, Math.random() * s, Math.random() * s);
		}

	}

	@SubscribeEvent
	public void mouseEvent(MouseEvent event) {
		EntityPlayer p = mc.player;
		if (p == null)
			return;

		if (p.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
			this.mc.mouseHelper.deltaX = 0;
			this.mc.mouseHelper.deltaY = 0;
		}

	}

	/*
	 * @SubscribeEvent public void onGameRenderOverlay(RenderGameOverlayEvent event)
	 * { if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
	 * 
	 * } }
	 */

	public static void line(float x, float y, float width, float height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		// bufferbuilder.color(0F, 0F, 1F, 1F);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).endVertex();
		tessellator.draw();
	}

	public static void drawGuiRect(float x, float y, float width, float height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		// bufferbuilder.color(0F, 0F, 1F, 1F);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).endVertex();
		tessellator.draw();
	}

	public void drawTexturedModalRectWithZ(int x, int y, int textureX, int textureY, int width, int height,
			float zLevel) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) zLevel)
				.tex((double) ((float) (textureX + 0) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) zLevel)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) zLevel)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) zLevel)
				.tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		tessellator.draw();
	}

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float zLevel = 0.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) zLevel)
				.tex((double) ((float) (textureX + 0) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) zLevel)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) zLevel)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) zLevel)
				.tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		tessellator.draw();
	}

	public static Vec3d divide(Vec3d vec) {
		return new Vec3d(vec.x / 2, vec.y / 2, vec.z / 2);
	}

	public void drawTexturedModalRectScaled(int x, int y, int textureX, int textureY, int width, int height,
			float scaled) {
		GlStateManager.pushMatrix();
		float zLevel = -5.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

		x = (int) ((float) x / scaled);
		y = (int) ((float) y / scaled);
		/*
		 * Vec3d vec1 = new Vec3d((double)(x + 0), (double)(y + height),
		 * (double)zLevel); vec1 = divide(vec1).scale(scaled); Vec3d vec2 = new
		 * Vec3d((double)(x + width), (double)(y + height), (double)zLevel); vec2 =
		 * divide(vec2).scale(scaled); Vec3d vec3 = new Vec3d((double)(x + width),
		 * (double)(y + 0), (double)zLevel); vec3 = divide(vec3).scale(scaled); Vec3d
		 * vec4 = new Vec3d((double)(x + 0), (double)(y + 0), (double)zLevel); vec4 =
		 * divide(vec4).scale(scaled);
		 * 
		 * 
		 * 
		 * bufferbuilder.pos(vec1.x, vec1.y, vec1.z).tex((double)((float)(textureX + 0)
		 * * 0.00390625F), (double)((float)(textureY + height) *
		 * 0.00390625F)).endVertex(); bufferbuilder.pos(vec2.x, vec2.y,
		 * vec2.z).tex((double)((float)(textureX + width) * 0.00390625F),
		 * (double)((float)(textureY + height) * 0.00390625F)).endVertex();
		 * bufferbuilder.pos(vec3.x, vec3.y, vec3.z).tex((double)((float)(textureX +
		 * width) * 0.00390625F), (double)((float)(textureY + 0) *
		 * 0.00390625F)).endVertex(); bufferbuilder.pos(vec4.x, vec4.y,
		 * vec4.z).tex((double)((float)(textureX + 0) * 0.00390625F),
		 * (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
		 */

		GlStateManager.scale(scaled, scaled, scaled);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) zLevel)
				.tex((double) ((float) (textureX + 0) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) zLevel)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + height) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) zLevel)
				.tex((double) ((float) (textureX + width) * 0.00390625F),
						(double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) zLevel)
				.tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F))
				.endVertex();
		tessellator.draw();
		GlStateManager.scale(1, 1, 1);
		GlStateManager.popMatrix();

	}

	public static long prevBlockpos = 0L;

	public static void drawCenteredString(FontRenderer fontRendererIn, String text, float f, float g, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) (f - fontRendererIn.getStringWidth(text) / 2), (float) g,
				color);
	}

	/**
	 * Draws an entity on the screen looking toward the cursor.
	 */
	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY,
			EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) posX, (float) posY, 50.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

		ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
		ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
		ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void drawSpecialHUD(int x, int y) {
		// 64 23

		ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/specialhud.png");
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();

		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();

		// GlStateManager.rotate(-60, 0, 1, 0);

		// GL11.glMatrixMode(GL11.GL_PROJECTION);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		GlStateManager.enableAlpha();

		GlStateManager.enableRescaleNormal();
		drawTexturedModalRectScaled(x, (int) (y + mc.player.motionY * 10), 0, 0, 64, 23, 1.5F);
		GlStateManager.disableRescaleNormal();

		GlStateManager.disableAlpha();

		Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
	}

	float ticker = 0.0F;

	public void testShit() {
		ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation(
				"textures/gui/container/enchanting_table.png");
		/**
		 * The ResourceLocation containing the texture for the Book rendered above the
		 * enchantment table
		 */
		// ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new
		// ResourceLocation("textures/entity/enchanting_table_book.png");
		ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/specialhud.png");
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		// GlStateManager.enableAlpha();
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		// GlStateManager.viewport((scaledresolution.getScaledWidth() - 320) / 2 *
		// scaledresolution.getScaleFactor(), (scaledresolution.getScaledHeight() - 240)
		// / 2 * scaledresolution.getScaleFactor(), 320 *
		// scaledresolution.getScaleFactor(), 240 * scaledresolution.getScaleFactor());
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GlStateManager.translate(-0.34F, 0.23F, 0.0F);
		Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
		float f = 1.0F;
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.translate(0.0F, 3.3F, -16.0F);
		GlStateManager.scale(1.0F, 1.0F, 1.0F);
		float f1 = 5.0F;
		GlStateManager.scale(5.0F, 5.0F, 5.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		GlStateManager.rotate(330, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate((1.0F - 2) * 0.2F, (1.0F - 2) * 0.1F, (1.0F - 2) * 0.25F);
		GlStateManager.rotate(-(1.0F - 2) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		ModelBook MODEL_BOOK = new ModelBook();
		GlStateManager.enableRescaleNormal();

		// drawEntityOnScreen(0, 0, 250, 64, 32, mc.player);

		ModelRenderer coverRight = (new ModelRenderer(new ModelPlane())).setTextureOffset(0, 0).addBox(-14.0F, -5.0F,
				0.0F, 14, 5, 0);

		coverRight.render(0.3F);

		// drawTexturedModalRectScaled(32, 32, 0, 0, 64, 23, 1.5F);
		// MODEL_BOOK.render((Entity)null, 0.0F, 3, 3, 3, 0.0F, 0.0625F);
		// GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.matrixMode(5889);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	}

	public static final ResourceLocation HEALTH_GUI = new ResourceLocation(
			Reference.MOD_ID + ":textures/gui/healthgui.png");

	public void renderHealthGUI(float scale) {

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();

		// GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
		ScaledResolution res = new ScaledResolution(mc);
		float y = res.getScaledHeight();
		float posX = (float) (20.0F);
		float posY = (float) ((y - 45));
		this.mc.getTextureManager().bindTexture(HEALTH_GUI);
		IConscious con = mc.player.getCapability(ConsciousProvider.CONSCIOUS, null);
		/*
		 * RenderTool.drawTexturedModalRectScaledFloat(posX, posY, 0, 0, 80, 8, scale);
		 * RenderTool.drawTexturedModalRectScaledFloat(posX, posY, 0, 8,
		 * (80*(mc.player.getHealth()/mc.player.getMaxHealth())), 8, scale);
		 * RenderTool.drawTexturedModalRectScaledFloat(posX+2, posY+1, 0, 16, 6, 6,
		 * scale);
		 */

		RenderTool.renderProgress(posX, posY, scale, mc.player.getHealth(), mc.player.getMaxHealth(), 0, 1.0F, 1.0F,
				1.0F);
		RenderTool.renderProgress(posX, posY + 14, scale, mc.player.getFoodStats().getFoodLevel(), 20.0F, 12, 0.0F,
				0.0F, 1.0F);

		RenderTool.renderProgress(posX, posY + 28, scale, con.getWaterLevel(), 20, 6, 0.0F, 1.0F, 0.0F);

		// drawCenteredString(this.mc.fontRenderer, mc.player.getHealth() + "/" +
		// mc.player.getMaxHealth(), posX + 10, posY, 0xFFFFFFFF);
		this.mc.getTextureManager().bindTexture(Gui.ICONS);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	float spinningTicker = 1F;
	// new ResourceLocation(Reference.MOD_ID + ":textures/gui/notifications.png");
	public static final ResourceLocation MODDED_HOTBAR = new ResourceLocation(
			Reference.MOD_ID + ":textures/gui/widgetsmodified.png");

	public static void renderTimedConsumables() {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		float x = (float) (sr.getScaledWidth_double() / 2f);
		float y = (float) (sr.getScaledHeight_double() / 2f);

		GlStateManager.pushMatrix();

		int timer = TimedConsumableTracker.timer;
		double interp = (timer - 1) + (timer - (timer - 1)) * Minecraft.getMinecraft().getRenderPartialTicks();
		interp /= (double) TimedConsumableTracker.maxTime;

		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		GUItil.renderHalfCircle(new Color(0xb33939), 1.0, x, y, 25, 20, 0, 360 * interp);
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		drawCenteredString(Minecraft.getMinecraft().fontRenderer, "%" + Math.round(100 * interp), x, y - 3, 0xb33939);
		GlStateManager.popMatrix();
		System.out.println("yo");
	}

	@SubscribeEvent
	public void onGameRenderOverlay(RenderGameOverlayEvent.Pre event) {

		if (CompatibilityChecker.shouldNotInjectGUI())
			return;

		if (TimedConsumableTracker.isConsuming() && event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			event.setCanceled(true);
		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

			if (TimedConsumableTracker.isConsuming()) {

				renderTimedConsumables();
			}
		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR
				&& Minecraft.getMinecraft().currentScreen instanceof GuiTrader) {
			event.setCanceled(true);

		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH
				|| event.getType() == RenderGameOverlayEvent.ElementType.FOOD
				|| event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
			event.setCanceled(true);
		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && !mc.player.isSpectator()
				&& !mc.player.isCreative()) {
			// System.out.println(MODDED_HOTBAR.getResourceDomain());
			// ResourceLocation MODDED_HOTBAR = new ResourceLocation(Reference.MOD_ID +
			// ":textures/gui/widgetsmodified.png");
			event.setCanceled(true);
			CustomHotbar.renderHotbar(new ScaledResolution(mc), Minecraft.getMinecraft().getRenderPartialTicks());
			// Minecraft.getMinecraft().renderEngine.bindTexture(MODDED_HOTBAR);
		}

		// long f1 = System.nanoTime();
		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

			IConscious con = Minecraft.getMinecraft().player.getCapability(ConsciousProvider.CONSCIOUS, null);
			// GUItil.drawCenteredString(ClientProxy.newFontRenderer,
			// ""+PainUtility.getNetPain(Minecraft.getMinecraft().player), 200, 200,
			// 0xaeff33);

			ScaledResolution res = event.getResolution();
			float x = res.getScaledWidth();
			float y = res.getScaledHeight();

			if (blackLock) {
				// System.out.println("hi!~");
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.color(0.0F, 0.0F, 0.0F, (1.0F * ((float) blackOutTime / 60.0F)));

				drawGuiRect(0, 0, x, y);
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			/*
			 * DRAW THE ICON STUFF ResourceLocation loc = new
			 * ResourceLocation(Reference.MOD_ID + ":textures/gui/blood.png");
			 * Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
			 * //drawTexturedModalRect(0, 0, 0, 0, 16, 16);
			 * Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);
			 */

			// GL11.glPushMatrix();

			if (!this.mc.player.isSpectator() && !this.mc.player.isCreative()
					&& !(this.mc.currentScreen instanceof CustomDeathScreen)) {

				renderHealthGUI(1.2F);
			}

			// GL11.glPushMatrix();
			// drawSpecialHUD(0, 0);
			// GL11.glPopMatrix();
			// drawSpecialHUD((int) x/2, (int) y/2);
			// testShit();

			if (!(this.mc.currentScreen instanceof CustomDeathScreen)) {
				ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/notifications.png");
				GlStateManager.pushMatrix();
				this.mc.getTextureManager().bindTexture(TEXTURES);
				GlStateManager.enableAlpha();

				IConscious conscious = mc.player.getCapability(ConsciousProvider.CONSCIOUS, null);

				if (conscious.isBleeding() && conscious.getBlood() < ConsciousCapability.MAX_BLOOD / 4) {
					IconSheet.getIcon(TEXTURES, 256, 16, 19).render(x - 25, 26, 1.0f);
				} else if (conscious.isBleeding() && conscious.getBlood() < ConsciousCapability.MAX_BLOOD) {
					IconSheet.getIcon(TEXTURES, 256, 16, 17).render(x - 25, 26, 1.0f);
				} else if (!conscious.isBleeding() && conscious.getBlood() > ConsciousCapability.MAX_BLOOD * 0.9
						&& conscious.getBlood() < ConsciousCapability.MAX_BLOOD) {
					IconSheet.getIcon(TEXTURES, 256, 16, 21).render(x - 25, 26, 1.0f);
				} else if (!conscious.isBleeding() && conscious.getBlood() > ConsciousCapability.MAX_BLOOD * 0.75
						&& conscious.getBlood() < ConsciousCapability.MAX_BLOOD) {
					IconSheet.getIcon(TEXTURES, 256, 16, 20).render(x - 25, 26, 1.0f);
				} else if (!conscious.isBleeding() && conscious.getBlood() < ConsciousCapability.MAX_BLOOD) {
					IconSheet.getIcon(TEXTURES, 256, 16, 18).render(x - 25, 26, 1.0f);
				}

				if (conscious.getWaterLevel() < 4) {
					IconSheet.getIcon(TEXTURES, 256, 16, 3).render(x - 28, 26 + 35, 1.0f);
				} else if (conscious.getWaterLevel() < 8) {
					IconSheet.getIcon(TEXTURES, 256, 16, 2).render(x - 28, 26 + 35, 1.0f);
				} else if (conscious.getWaterLevel() < 12) {
					IconSheet.getIcon(TEXTURES, 256, 16, 1).render(x - 28, 26 + 35, 1.0f);
				}

				/*
				 * if(conscious.isBleeding() && conscious.getBlood() <
				 * ConsciousCapability.MAX_BLOOD) { drawTexturedModalRectScaled((int) (x-25),
				 * 16, 32, 16, 16, 16, 1.5F); } if(conscious.isBleeding() &&
				 * conscious.getBlood() > 3000) { drawTexturedModalRectScaled((int) (x-25), 16,
				 * 0, 16, 16, 16, 1.5F); } if(!conscious.isBleeding() && conscious.getBlood() >
				 * 3000) { drawTexturedModalRectScaled((int) (x-25), 16, 16, 16, 16, 16, 1.5F);
				 * }
				 */
				// Gui.drawScaledCustomSizeModalRect(32, 32, 0, 0, 16, 16, 16, 16, 2, 2);

				// spinningTicker += 2;
				// GlStateManager.rotate(spinningTicker, 0, 1, 0);

				// drawTexturedModalRectScaled(-8, -8, 16, 16, 16, 16, 1.5F);

				// drawEntityOnScreen(128, 128, 30, 64, 32, mc.player);
				// drawTexturedModalRect(32, 32, 0, 0, 16, 16);
				GlStateManager.disableAlpha();
				GlStateManager.popMatrix();
				Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);

			}

			// GL11.glPopMatrix();
			// GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (mc.player.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
				// System.out.println("isdowned");
				GL11.glPushMatrix();
				// System.out.println("d");
				// WHITE OVERLAY
				GlStateManager.color(0.3F, 0.3F, 0.3F, 0.5F);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
				// System.out.println("drawing");
				drawGuiRect(0, 0, x, y);
				GlStateManager.disableBlend();

				int timerConscious = mc.player.getCapability(ConsciousProvider.CONSCIOUS, null).getDownTimer();
				drawCenteredString(this.mc.fontRenderer, ChatFormatting.RED + "YOU ARE UNCONSCIOUS!", x / 2F, y / 2F,
						0xFFFFFFFF);
				drawCenteredString(this.mc.fontRenderer, ChatFormatting.RED + "" + timerConscious + "s", x / 2F,
						y / 2F + 8, 0xFFFFFFFF);
				Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);
				GL11.glPopMatrix();
			}

		}
		// long f2 = System.nanoTime();

		// System.out.println(f2-f1 + "ns");

	}

	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent event) {

		LUTRenderer.createIVPM();

		LUTRenderer.blitDepth();

		LUTRenderer.doPostProcess();

		if (DrugCache.hasDrugs()) {

			DrugRenderer.doPostProcess();
		}

	}

	@SubscribeEvent
	public void tick(RenderTickEvent event) {

		/*
		 * GL11.glEnable(GL11.GL_FOG); GL11.glFogf(GL11.GL_FOG_START, 300f);
		 * GL11.glFogf(GL11.GL_FOG_END, 5000); GL11.glDisable(GL11.GL_FOG);
		 */

		EntityPlayer p = mc.player;
		/*
		 * if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
		 * ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft()); double
		 * cX = sr.getScaledWidth_double()-75; double cY =
		 * sr.getScaledHeight_double()-45; GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		 * GUItil.renderHalfCircle(Color.YELLOW, 1.0, cX, cY, 15, 4, -135, 135);
		 * GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		 * GUItil.drawScaledString(ClientProxy.newFontRenderer, "onomy", cX-5, cY-7,
		 * 0xffffff, 2.0f); }
		 */

		if (p == null)
			return;

		if (Minecraft.getMinecraft().player.getPosition().toLong() != prevBlockpos) {
			prevBlockpos = Minecraft.getMinecraft().player.getPosition().toLong();

			TimedConsumableTracker.cancel();
		}

		if (event.phase == Phase.START && p.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
			this.mc.mouseHelper.mouseXYChange();
			this.mc.mouseHelper.deltaX = 0;
		}

		/*
		 * if(event.phase == Phase.END) {
		 * if(p.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1 &&
		 * !mc.entityRenderer.isShaderActive()) { Main.proxy.showDeathGUI(p);
		 * //Minecraft.getMinecraft().displayGuiScreen(new GuiIngameMenu());
		 * mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
		 * } if(p.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 0) {
		 * mc.entityRenderer.stopUseShader(); } }
		 * 
		 */

	}

}
