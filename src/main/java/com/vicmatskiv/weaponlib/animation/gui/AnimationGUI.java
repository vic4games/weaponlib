package com.vicmatskiv.weaponlib.animation.gui;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.ChangeAttachmentPermit;
import com.vicmatskiv.weaponlib.WeaponState;
import com.vicmatskiv.weaponlib.WeaponRenderer.Builder;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.Arcball;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.OpenGLSelectionHelper;
import com.vicmatskiv.weaponlib.animation.DebugPositioner.Position;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.SystemToast.Type;
import net.minecraft.client.gui.toasts.IToast.Visibility;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.gui.toasts.TutorialToast.Icons;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AnimationGUI {
	
	private static AnimationGUI instance = new AnimationGUI();
	
	
	public static final ResourceLocation TEXTURES = new ResourceLocation("mw" + ":" + "textures/hud/animguio.png");
	
	

	public ArrayList<Panel> panels = new ArrayList<>();

	public boolean mouseStatus = false;
	public boolean grabStatus = false;
	public boolean guiHoverStatus = false;
	
	public Button resetCamera = new Button("Reset Camera", 0, 10, 10, 20);
	public Button resetTransforms = new Button("Reset Transforms", 3, 35, 10, 20);
	public Button forceSteveArms = new Button("Force steve arms", 1, 60, 10, 20);
	public Button forceAlexArms = new Button("Force alex arms", 2, 85, 10, 20);
	public Button printConsole = new Button("Print to console", 6, 10, 35, 20);
	public Button switchScopes = new Button("Switch scopes", 8,  85, 35, 20);
	
	public Button axisToggle = new Button("Toggle axis indicator", true, 4, 110, 10, 20);
	public Button forceFlash = new Button("Position muzzle flash", true, 5, 135, 10, 20);
	
	public Button titleSafe = new Button("Center indicator", true, 9, 110, 35, 20);
	public Button editRotButton = new Button("Edit rotation point", true, 10, 35, 35, 20);
	public Button moveForward = new Button("Move axis backwards", true,7,  60, 35, 20);
	public Button leftDrag = new Button("Position drag alignment", true, 11, 135, 35, 20);
	public Button magEdit = new Button("Edit magazine rotation point", true, 12, 10, 80, 20);
	
	
	public static AnimationGUI getInstance() {
		
		return instance;
	}
	
	public AnimationGUI() {
	
		
		Panel cameraPanel = new Panel(this, "Functionality", 10, 10, 20);
		
		// cam reset 0
		cameraPanel.addButtons(resetCamera, resetTransforms, forceSteveArms, forceAlexArms, switchScopes, magEdit, leftDrag, printConsole);
		
		Panel renderPanel = new Panel(this, "Rendering", 10, 35, 20);
		
		renderPanel.addButtons(axisToggle, forceFlash, editRotButton, moveForward, titleSafe);
		
		

		this.axisToggle.setState(true);
		
		
		this.panels.add(cameraPanel);
		this.panels.add(renderPanel);
		
		
	}
	
	public Button but;
	
	public void render() {
		
		
		
		
		Minecraft mc = Minecraft.getMinecraft();
		
		 ScaledResolution scaledresolution = new ScaledResolution(mc);
         final int scaledWidth = scaledresolution.getScaledWidth();
         final int scaledHeight = scaledresolution.getScaledHeight();
         int mouseX = Mouse.getX() * scaledWidth / mc.displayWidth;
         int mouseY = scaledHeight - Mouse.getY() * scaledHeight / mc.displayHeight - 1;
         but = null;
		update(mouseX, mouseY);
		
		extraRender();
		
		
		for(Panel p : panels) {
			p.render(mouseX, mouseY);
		}
		
		/*
		but = null;
		for(Button b : buttonList) {
			b.renderButton(mouseX, mouseY);
		}
		*/
		
		if(but != null) {
		
			AnimationGUI.renderRect(new Color(0x222f3e).darker().darker().darker(), mouseX, mouseY, mc.fontRenderer.getStringWidth(but.tooltip)*0.8f, 10);
			GlStateManager.enableTexture2D();
			AnimationGUI.renderScaledString(but.tooltip, mouseX+2.5, mouseY+2, 0.9f);
			GlStateManager.disableTexture2D();
		}
		
		GlStateManager.enableTexture2D();
		
	}
	
	public Textbar position = new Textbar("Position", 40, 40, 90, 15);
	public Textbar rotation = new Textbar("Rotation", 10, 75, 90, 15);
	
	public void extraRender() {

		
		
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		position.x = (int) (sr.getScaledWidth_double()-100);
		rotation.x = (int) (sr.getScaledWidth_double()-100);
		
		
		Position p = DebugPositioner.getCurrentPartPosition();
		if(p == null) {
			position.setStrings(0, 0, 0);
			rotation.setStrings(0, 0, 0);
		} else {
			position.setStrings(p.x, p.y, p.z);
			rotation.setStrings(p.xRotation, p.yRotation, p.zRotation);
		}
		
		position.renderButton(0, 0);
		rotation.renderButton(0, 0);
		//tb.renderButton(0, 0);
		
		GlStateManager.enableTexture2D();
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
		renderScaledString(time, sr.getScaledWidth_double()-40, 5, 1);
		String fps = "FPS: " + Minecraft.getMinecraft().getDebugFPS();
		renderScaledString(fps, sr.getScaledWidth_double()-45, 15, 1);
		
		
		String itemPos = "";
		
		if(magEdit.isState()) {
			itemPos = "Mag Rotation Point";
		} else if(forceFlash.isState()) {
			itemPos = "Muzzle Flash";
		} else if(editRotButton.isState())  {
			 itemPos = "Weapon Rotation Point";
		} else if(OpenGLSelectionHelper.selectID == 3) {
			itemPos = "Weapon";
		} else if(OpenGLSelectionHelper.selectID == 1) {
			itemPos = "Left Hand";
		} else if(OpenGLSelectionHelper.selectID == 2) {
			itemPos = "Right Hand";
		}
		
		String currentlyPositioning = TextFormatting.WHITE + "Positioning " + TextFormatting.GOLD + itemPos;
		
		renderScaledString(currentlyPositioning, 5, 5, 0.5);
		
		
		GlStateManager.color(1, 1, 1);
	
		
		GlStateManager.disableTexture2D();
		//GlStateManager.enableTexture2D();
	}
	
	public void update(int mouseX, int mouseY) {
		
		
		grabStatus = false;
		for(Panel p : this.panels) {
			
			
			if(checkIn2DBox(mouseX, mouseY, p.getPositionX(), p.getPositionY(), p.getWidth(), p.getHeight())) grabStatus = true;
		}
		
		if(Mouse.isButtonDown(0) && !mouseStatus) {
			mouseStatus = true;
			
			
			
			
			onMouseClick(mouseX, mouseY);
			
			
		} else if(!Mouse.isButtonDown(0) && mouseStatus) {
			onMouseReleased(mouseX, mouseY);
			mouseStatus = false;
		}
	}
	
	public boolean checkIn2DBox(double a, double b, double x, double y, double width, double height) {
		return a >= x && a <= x+width && b >= y && b <= y+height;
	}
	
	public void onMouseClick(int mouseX, int mouseY) {
		
		for(Panel panel : this.panels) {
			panel.handleButtonClicks(mouseX, mouseY);
		}
		
	}
	
	public void onMouseReleased(int mouseX, int mouseY) {
		for(Panel panel : this.panels) {
			panel.onMouseReleased(mouseX, mouseY);
		}
	}
	
	
	public void onAction(Button id) {
		
		
		if(id == resetCamera) {
			AnimationModeProcessor.getInstance().rot = Vec3d.ZERO;
			AnimationModeProcessor.getInstance().pan = Vec3d.ZERO;
		} else if(id == resetTransforms) {
			AnimationModeProcessor amp = AnimationModeProcessor.getInstance();
			Builder b = amp.getCurrentWeaponRenderBuilder();
			b.firstPersonTransform.set(amp.backupFP);
			b.firstPersonLeftHandTransform.set(amp.backupFPL);
			b.firstPersonRightHandTransform.set(amp.backupFPR);
			
			DebugPositioner.reset();
		} else if(id == forceSteveArms) {
			forceSkin("default");
		} else if(id == forceAlexArms) {
			forceSkin("slim");
		} else if(id == printConsole) {
			
		
			
			if(CompatibleClientEventHandler.muzzlePositioner) {
				System.out.println("(" + CompatibleClientEventHandler.debugmuzzlePosition.x + ", " + CompatibleClientEventHandler.debugmuzzlePosition.y  + ", " + CompatibleClientEventHandler.debugmuzzlePosition.z + ")");
				
				return;
			}

			if(magEdit.isState()) {
				System.out.println("(" + CompatibleClientEventHandler.magRotPositioner.x + ", " + CompatibleClientEventHandler.magRotPositioner.y  + ", " + CompatibleClientEventHandler.magRotPositioner.z + ")");
				return;
			}
			
			int selectID = OpenGLSelectionHelper.selectID;
    		if(ClientModContext.getContext() == null || ClientModContext.getContext().getMainHeldWeapon() == null) {
    		
    			return;
    		}
    		Builder i = ClientModContext.getContext().getMainHeldWeapon().getWeapon().getRenderer().getWeaponRendererBuilder();
    		switch(selectID) {
    		case 1:
    			i.firstPersonLeftHandTransform.printTransform();
    			break;
    		case 2:
    			i.firstPersonRightHandTransform.printTransform();
    			break;
    		case 3:
    			i.firstPersonTransform.printTransform();
    			break;
    			
    		}
		} else if(id == switchScopes) {
			PlayerWeaponInstance instance = ClientModContext.getContext().getPlayerItemInstanceRegistry().getMainHandItemInstance(Minecraft.getMinecraft().player, PlayerWeaponInstance.class);
			ClientModContext.getContext().getAttachmentAspect().tryChange(new ChangeAttachmentPermit(AttachmentCategory.SCOPE), instance);
			
		} else if(id == editRotButton) {
			if(editRotButton.isState()) {
				moveForward.setState(false);
			}
		} else if(id == forceFlash) {
			if(forceFlash.isState()) {
				CompatibleClientEventHandler.muzzlePositioner = true;
				
			} else {
				CompatibleClientEventHandler.muzzlePositioner = false;
			}
		} else if(id == magEdit) {
			DebugPositioner.setDebugMode(true);
		}
		
	
	}
	
	
	public static void forceSkin(String type) {
		Method f = ReflectionHelper.findMethod(AbstractClientPlayer.class, "getPlayerInfo", "getPlayerInfo", null);
		NetworkPlayerInfo npi = null;
		try {
			npi = (NetworkPlayerInfo) f.invoke((AbstractClientPlayer) Minecraft.getMinecraft().player, null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(npi != null) {
			try {
				Field f2 = ReflectionHelper.findField(NetworkPlayerInfo.class, "skinType");
				f2.setAccessible(true);
				f2.set(npi, type);
				
			} catch(Exception e) {
				
			}
		}
		CompatibleWeaponRenderer.acp = null;
	}
	
	public static void renderScaledString(String str, double x, double y, double scale) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, scale);
		
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(str, 0, 0, 0xffffff);
		
		GlStateManager.popMatrix();
	}
	
	
	// tools
	
	public static void renderRect(Color c, double x, double y, double w, double h) {
		
		float r = (float) c.getRed()/255f;
		
		float g = (float) c.getGreen()/255f;
		float b = (float) c.getBlue()/255f;
		
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		float grad = 0.8f;
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		bb.pos(x, y, 0).color(r, g, b, 1).endVertex();
		bb.pos(x, y+h, 0).color(r*grad, g*grad, b*grad, 1).endVertex();
		bb.pos(x+w, y+h, 0).color(r*grad, g*grad, b*grad, 1).endVertex();
		bb.pos(x+w, y, 0).color(r, g, b, 1).endVertex();
		
		
	
		
		t.draw();
	}
	
	public static void renderTexturedRect(int id, double x, double y, double w, double h) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mw" + ":" + "textures/hud/animguio.png"));
		
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		float width = 64;
		float icoSize = 16;
		float height = 64;
		
	
		float u = ((id*icoSize)%width)/width;
		float v = (float) (Math.floor((id*icoSize)/width))*icoSize/height;
		float m = icoSize/width;
		float n = icoSize/height;
		
		
		
	
		bb.pos(x, y, 0).tex(u, v).endVertex();
		bb.pos(x, y+h, 0).tex(u, v+n).endVertex();
		bb.pos(x+w, y+h, 0).tex(u+m, v+n).endVertex();
		bb.pos(x+w, y, 0).tex(u+m, v).endVertex();
		
		
	
		
		t.draw();
		
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.disableTexture2D();
		
	}
	
	
	
}
