package com.vicmatskiv.weaponlib.animation.gui;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AnimationGUI {
	
	private static AnimationGUI instance = new AnimationGUI();
	
	
	public static final ResourceLocation TEXTURES = new ResourceLocation("mw" + ":" + "textures/hud/animguio.png");
	
	public static AnimationGUI getInstance() {
		
		return instance;
	}

	public ArrayList<Button> buttonList = new ArrayList<>();

	public boolean mouseStatus = false;
	
	public Button axisToggle = new Button("Toggle axis indicator", true, 4, 110, 10, 20);
	public Button forceFlash = new Button("Position muzzle flash", true, 5, 135, 10, 20);
	
	public Button titleSafe = new Button("Center indicator", true, 9, 110, 35, 20);
	public Button editRotButton = new Button("Edit rotation point", true, 10, 35, 35, 20);
	public Button moveForward = new Button("Move axis backwards", true,7,  60, 35, 20);
	public Button leftDrag = new Button("Position drag alignment", true, 11, 135, 35, 20);
	
	
	public AnimationGUI() {
	
		// cam reset 0
		addButton(new Button("Reset Camera", 0, 10, 10, 20));
		
		// transform reset 1
		addButton(new Button("Reset Transforms", 3, 35, 10, 20));
		
		// steve 2
		addButton(new Button("Force steve arms", 1, 60, 10, 20));
		
		// alex 3
		addButton(new Button("Force alex arms", 2, 85, 10, 20));
		
		
		// 4
		addButton(axisToggle);
		
		
		// 5
		addButton(forceFlash);
		
		// 6
		addButton(new Button("Print to console", 6, 10, 35, 20));
		
		// 7
		addButton(editRotButton);
		
		// 8
		addButton(moveForward);
		
		// 9
		addButton(new Button("Switch scopes", 8,  85, 35, 20));
		
		
		// 
		addButton(titleSafe);
		
		// Drag rotation - 10
		addButton(leftDrag);
		
		
				
		
		
	}
	
	public Button but;
	
	public void render() {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		 ScaledResolution scaledresolution = new ScaledResolution(mc);
         final int scaledWidth = scaledresolution.getScaledWidth();
         final int scaledHeight = scaledresolution.getScaledHeight();
         int mouseX = Mouse.getX() * scaledWidth / mc.displayWidth;
         int mouseY = scaledHeight - Mouse.getY() * scaledHeight / mc.displayHeight - 1;
     
		update(mouseX, mouseY);
		
		extraRender();
		
		but = null;
		for(Button b : buttonList) {
			b.renderButton(mouseX, mouseY);
		}
		
		if(but != null) {
		
			AnimationGUI.renderRect(new Color(0x222f3e).darker().darker().darker(), mouseX, mouseY, mc.fontRenderer.getStringWidth(but.tooltip)*0.8f, 10);
			GlStateManager.enableTexture2D();
			AnimationGUI.renderScaledString(but.tooltip, mouseX+2.5, mouseY+2, 0.9f);
			GlStateManager.disableTexture2D();
		}
		
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
		
	}
	
	public void update(int mouseX, int mouseY) {
		
		
		if(Mouse.isButtonDown(0) && !mouseStatus) {
			mouseStatus = true;
			
			
			
			
			onMouseClick(mouseX, mouseY);
			
			
		} else if(!Mouse.isButtonDown(0) && mouseStatus) {
			mouseStatus = false;
		}
	}
	
	public void onMouseClick(int mouseX, int mouseY) {
		
		for(Button b : buttonList) {
			if(b.isMouseOver(mouseX, mouseY)) {
				b.onMouseClick();
				onAction(b.id);
			}
		}
		
	}
	
	public void addButton(Button b) {
		b.id = buttonList.size();
		buttonList.add(b);
	}
	
	public void onAction(int id) {
		switch(id) {
		case 0:
			AnimationModeProcessor.getInstance().rot = Vec3d.ZERO;
			AnimationModeProcessor.getInstance().pan = Vec3d.ZERO;
			
			break;
		case 1:
			AnimationModeProcessor amp = AnimationModeProcessor.getInstance();
			Builder b = amp.getCurrentWeaponRenderBuilder();
			b.firstPersonTransform.set(amp.backupFP);
			b.firstPersonLeftHandTransform.set(amp.backupFPL);
			b.firstPersonRightHandTransform.set(amp.backupFPR);
			
			DebugPositioner.reset();
			
			break;
		case 2:
			forceSkin("default");
			break;
		case 3:
			forceSkin("slim");
			break;
		case 4:
			// do nothing
			break;
		case 5:
			// do nothing
			break;
		case 6:
			
			//instance.setState(WeaponState.MODIFYING);
		//	ClientModContext.getContext().getAttachmentAspect().toggleClientAttachmentSelectionMode(Minecraft.getMinecraft().player);
			//instance.getWeapon().toggleClientAttachmentSelectionMode(Minecraft.getMinecraft().player);
			
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
    		break;
		case 7:
			if(editRotButton.isState()) {
				moveForward.setState(false);
			}
			// do nothing
			break;
		case 8:
			// do nothing
			break;
		case 9:
			PlayerWeaponInstance instance = ClientModContext.getContext().getPlayerItemInstanceRegistry().getMainHandItemInstance(Minecraft.getMinecraft().player, PlayerWeaponInstance.class);
			ClientModContext.getContext().getAttachmentAspect().tryChange(new ChangeAttachmentPermit(AttachmentCategory.SCOPE), instance);
			
			break;
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
