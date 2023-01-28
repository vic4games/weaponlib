package com.jimholden.conomy.client.gui.engine.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.Supplier;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiElement;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IMouseClick;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.math.MathHelper;

public class AdvancedScroll extends GuiElement implements IMouseClick {

	public Supplier<ArrayList<ScrollBlock>> arraySupplier;
	public ArrayList<ScrollBlock> array;

	public double velocity = 0.0;
	public double friction = 0.97;

	public double prevScrollPos = 0.0;
	public double scrollPos = 0;

	public int selectedIndex = -1;
	
	public double height = 20;

	public AdvancedScroll(Supplier<ArrayList<ScrollBlock>> arraySupplier, double x, double y, double width,
			double height, double scale, GuiPage page) {
		super(x, y, width, height, scale, page);
		this.arraySupplier = arraySupplier;
		this.height = height;
	}

	public AdvancedScroll(ArrayList<ScrollBlock> stringArray, double x, double y, double width, double height,
			double scale, GuiPage page) {
		super(x, y, width, height, scale, page);
		this.array = stringArray;
		this.height = height;
	}

	public ArrayList<ScrollBlock> getArray() {
		if (arraySupplier != null) {
			return arraySupplier.get();
		} else {

			return array;
		}
	}

	public boolean hasSelected() {
		return selectedIndex != -1;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	@Override
	public void render() {
		super.render();

		// get mouse coordinates
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int i1 = scaledresolution.getScaledWidth();
		int j1 = scaledresolution.getScaledHeight();
		int mouseX = Mouse.getX() * i1 / Minecraft.getMinecraft().displayWidth;
		int mouseY = j1 - Mouse.getY() * j1 / Minecraft.getMinecraft().displayHeight - 1;

		double room = 0;


		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());


		Color gray = new Color(0x2f3640).darker();
		this.prevScrollPos = scrollPos;

		double mu = 0;
		double size = 45;
		
		

		boolean mouseInScrollBox = mouseX >= this.getX() && mouseX <= this.getX() + width && mouseY >= getY()
				&& mouseY <= getY() + height;
		if (mouseInScrollBox) {
			this.velocity += (org.lwjgl.input.Mouse.getDWheel()) / 60.0;
			velocity *= friction;

		}
		
		

		double relativeMX = mouseX - this.getX();
		double relativeMY = mouseY - this.getY();
		// System.out.println(relativeMY);
		int spacing = 20;
		int index = (int) Math.floor((relativeMY - this.scrollPos) / 20);
		index = MathHelper.clamp(index, 0, getArray().size() - 1);

		// if(!mouseInScrollBox) index = -1;

		// System.out.println(mouseInScrollBox);

		this.scrollPos += velocity * 0.1;

		if (this.scrollPos > 0) {
			this.scrollPos = 0;
		}
		

		int pR = getArray().size() * spacing;

		if (pR > (height + 5)) {
			if (this.scrollPos < -pR + (20)) {
				this.scrollPos = -pR + (20);
			}

		} else {
			this.scrollPos = 0;
		}

		if (failTimer.atMax())
			failTimer.reset();

		if (!failTimer.atMin()) {

			failTimer.tick();

		}
		
		

		GUItil.renderRectangle(gray.darker(), 1.0, getX(), getY(), width, height);

		// System.out.println(this.getFailOffset());
		if (this.getFailOffset() != 0.0) {
			GUItil.renderRectangle(Color.gray.darker(), getFailOffset(), getX(), getY(), width, height);
		}
		GlStateManager.enableTexture2D();

		// GL11.glRectd(getX(), getY()+height, getX()+width, getY());

		Minecraft mc = Minecraft.getMinecraft();

		// https://forums.minecraftforge.net/topic/19745-172-scaling-glscissor/

		/*
		int intY = (int) getY();
		int intX = (int) getX();

		int intW = (int) this.width;
		int intH = (int) this.height;

		int scaleFactor = 1;

		int k = mc.gameSettings.guiScale;

		if (k == 0) {
			k = 1000;
		}

		while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
				&& mc.displayHeight / (scaleFactor + 1) >= 240) {
			++scaleFactor;
		}

		double interp = this.prevScrollPos
				+ (this.scrollPos - this.prevScrollPos) * Minecraft.getMinecraft().getRenderPartialTicks();
		
		GL11.glScissor(0, mc.displayHeight - (intY + intH) * scaleFactor, (intW + intX) * scaleFactor,
				intH * scaleFactor);
		

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		*/
		
		double interp = this.prevScrollPos
				+ (this.scrollPos - this.prevScrollPos) * Minecraft.getMinecraft().getRenderPartialTicks();

		GUItil.performScissor(getX(), getY(), this.width, this.height);
		
		GlStateManager.pushMatrix();

		GlStateManager.translate(0.0, interp, 0.0);
		;

		
		
		if (index != -1 && mouseInScrollBox) {
			GUItil.renderRectangle(gray.darker().darker(), 1.0, getX(), getY() + index * spacing, width,  getArray().get(index).height);

			GlStateManager.enableTexture2D();
		}

		if (hasSelected())
			GUItil.renderRoundedRectangle(gray.brighter().brighter().brighter(), 1.0, getX(),
					getY() + selectedIndex * spacing, getX() + width, getY() + (selectedIndex * spacing) +  getArray().get(index).height, 0,
					0.5);
		
		

		// GUItil.renderCircle(Color.red, 1.0, getX(), getY(), 59);
		for (int n = 0; n < getArray().size(); ++n) {
			/*
			GUItil.renderRoundedRectangle(gray.darker().darker().darker(), 1.0, getX(),
					getY() + room, getX() + width, getY()+20+room, 0,
					0.5);*/
			getArray().get(n).renderScroll(getX(), getY() + room, width, interp);

			room += 20;
		}
		// System.out.println((Math.abs(this.scrollPos)) + " | " + room);

		GlStateManager.popMatrix();
		//GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GUItil.endScissor();
		// GUItil.renderRoundedRectangle(gray.darker(), 1.0, getX()-1, getY()-1,
		// getX()+width+1, getY()+height+1, 4, 1);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean mouseInScrollBox = mouseX >= this.getX() && mouseX <= this.getX() + width && mouseY >= getY()
				&& mouseY <= getY() + height;

		if (!mouseInScrollBox)
			return;
		double relativeMY = mouseY - this.getY();

		int index = (int) Math.floor((relativeMY - this.scrollPos) / 20);
		index = MathHelper.clamp(index, 0, getArray().size() - 1);
		// if(!mouseInScrollBox) index = -1;

		selectedIndex = index;
		
		for(ScrollBlock sb : getArray()) {
			sb.mouseClicked(mouseX, mouseY, mouseButton);
		}

	}

}
