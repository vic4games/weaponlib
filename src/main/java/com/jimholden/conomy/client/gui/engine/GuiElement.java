package com.jimholden.conomy.client.gui.engine;

import javax.vecmath.Vector2d;

import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.ITrigger;
import com.jimholden.conomy.client.gui.engine.display.Margins;

public abstract class GuiElement implements IHasInfo, ITrigger {
	
	public double posX = 0;
	public double posY = 0;
	public double width = 0;
	public double height = 0;
	public double scale = 1;
	
	public boolean isHidden = false;
	
	public GuiPage parent;
	
	public boolean trigger = false;
	public BasicAnimationTimer triggerTimer = new BasicAnimationTimer(25, 0);
	public BasicAnimationTimer failTimer = new BasicAnimationTimer(25, 0);
	
	public Margins margins = Margins.NONE;
	
	public double getFailOffset() {
		if(this.failTimer.atMin()) return 0.0;
		return Math.sin((16*Math.PI/5)*this.failTimer.mcInterp())*2;
	}
	
	public void fail() {
		this.failTimer.tick();
		
	}
	
	public GuiElement(FrameAlignment fa, double size, double scale, GuiPage page) {

		this.width = size;
		this.height = size;
		this.scale = scale;
		this.parent = page;
		
		Vector2d vO = page.getAlignment(fa, this);
		
		this.posX = vO.x + page.getX();
		this.posY = vO.y + page.getY();
	}
	
	public GuiElement(FrameAlignment fa, double size, double scale, GuiPage page, Margins m) {

		this.width = size;
		this.height = size;
		this.scale = scale;
		this.parent = page;
		
		this.margins = m;
		
		Vector2d vO = page.getAlignment(fa, this);
		
		this.posX = vO.x + page.getX();
		this.posY = vO.y + page.getY();
	}
	
	public GuiElement(double x, double y, double size, double scale, GuiPage page) {
		this.posX = x;
		this.posY = y;
		this.width = size;
		this.height = size;
		this.scale = scale;
		this.parent = page;
		
		if(page != null) {
			this.posX += page.getX();
			this.posY += page.getY();
		}
		
	}
	
	public GuiElement(double x, double y, double width, double height, double scale, GuiPage page) {
		this.posX = x;
		this.posY = y;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.parent = page;
		
		if(page != null) {
			this.posX += page.getX();
			this.posY += page.getY();
		}
		
	}
	
	
	public void render() {
		if(triggered()) {
			triggerTimer.tick();
		}
	}

	public double getX() {
		return posX;
	}

	
	
	
	public void setX(double posX) {
		this.posX = posX;
	}

	public double getY() {
		return posY;
	}

	public void setY(double posY) {
		this.posY = posY;
	}

	public double getNWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getNHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public GuiPage getParent() {
		return parent;
	}

	public void setParent(GuiPage parent) {
		this.parent = parent;
	}
	
	@Override
	public IInfoDisplay getDisplayElement() {
		return null;
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
		return this.margins;
	}

}
