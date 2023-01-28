package com.jimholden.conomy.client.gui.engine.buttons;

import javax.vecmath.Vector2d;

import com.jimholden.conomy.client.gui.engine.BasicAnimationTimer;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IVisibilityGUI;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.ITrigger;
import com.jimholden.conomy.client.gui.engine.display.Margins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ConomyButton extends GuiButton implements IHasInfo, IVisibilityGUI, ITrigger {
	
	public IInfoDisplay infoTag;
	public double x;
	public double y;
	public double width;
	public double height;
	
	
	// margins
	public Margins margins = Margins.NONE;
	
	
	
	private FrameAlignment alignment;
	
	
	public GuiPage parentPage;

	public boolean pageVisibility = false;
	
	public boolean trigger = false;
	public BasicAnimationTimer triggerTimer = new BasicAnimationTimer(25, 0);
	
	public BasicAnimationTimer failTimer = new BasicAnimationTimer(25, 0);
	
	public ConomyButton(int buttonId, FrameAlignment x, IInfoDisplay display, GuiPage parentPage) {
		super(buttonId, (int) 0, (int) 0, "");
		
		
		
		
		this.parentPage = parentPage;
		parentPage.registerButton(this);
		if(parentPage != null) {
			this.x += parentPage.getX();
			this.y += parentPage.getY();
		}
		
		this.infoTag = display;
		this.infoTag.setParent(this);
		//System.out.println("Clearly Unpro: " + this.x + " | " + this.y);
	}
	
	public void setupAlignment(FrameAlignment x) {
		
	//	System.out.println("C1: " + this.x + " | " + this.y);
		Vector2d vO = parentPage.getAlignment(x, this);
		
		this.x += vO.x;
		this.y += vO.y;
		
		this.alignment = alignment;
	}
	
	
	public ConomyButton(int buttonId, double x, double y, IInfoDisplay display, GuiPage parentPage) {
		super(buttonId, (int) x, (int) y, "");
		
		this.x = x;
		this.y = y;
		
		//System.out.println("Professional (1): " + this.x + " | " + this.y);
		
		
		if(parentPage != null) {
			this.parentPage = parentPage;
			parentPage.registerButton(this);
			this.x += parentPage.getX();
			this.y += parentPage.getY();
		}
		
		//System.out.println("Professional (2): " + this.x + " | " + this.y);
		
		
		this.infoTag = display;
		this.infoTag.setParent(this);
		
		//System.out.println("Professional (3): " + this.x + " | " + this.y);
	}
	
	public double getFailOffset() {
		if(this.failTimer.atMin()) return 0.0;
		return Math.sin((16*Math.PI/5)*this.failTimer.mcInterp())*2;
	}
	
	public void fail() {
		this.failTimer.tick();
		
	}
	
	

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
	
		if(failTimer.atMax()) failTimer.reset();
		
		if(!failTimer.atMin()) {
			
			failTimer.tick();
			
		}
		
		if(triggered()) {
			triggerTimer.tick();
		}
		infoTag.renderDisplay();
	}
	
	@Override
	public IInfoDisplay getDisplayElement() {
		return this.infoTag;
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getNWidth() {
		return this.width;
	}

	@Override
	public double getNHeight() {
		return this.height;
	}

	@Override
	public void updateVisibility() {
		
		if(this.parentPage == null) {
			
			this.visible = true;
			this.pageVisibility = true;
			return;
		}
		if(!this.parentPage.getVisibility()) {
			
			this.pageVisibility = false;
		}
		else this.pageVisibility = true;
		this.visible = true;
	
	}

	@Override
	public void setParentPage(GuiPage gp) {
		this.parentPage = gp;
		
	}
	
	@Override
	public boolean getPageVisibiity() {
		if(this.parentPage == null) return true;
		return this.pageVisibility;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public void trigger() {
		trigger = true;
		
	}
	@Override
	public boolean triggered() {
		return trigger;
	}
	

	
	@Override
	public Margins getMarginHandler() {
		// TODO Auto-generated method stub
		return this.margins;
	}

}
