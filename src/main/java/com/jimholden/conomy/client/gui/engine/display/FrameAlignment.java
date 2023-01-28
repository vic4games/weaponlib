package com.jimholden.conomy.client.gui.engine.display;

import javax.vecmath.Vector2d;

import com.jimholden.conomy.client.gui.engine.GuiPage;

public enum FrameAlignment {
	CENTER,
	TOPLEFT,
	TOPRIGHT,
	BOTTOMLEFT,
	BOTTOMRIGHT,
	TOPCENTER,
	BOTTOMCENTER;
	
	private boolean horizontal = true;
	
	private double hBuffer = 0.0;
	private double vBuffer = 0.0;
	
	
	public FrameAlignment addMargins(double width, double height) {
		this.hBuffer = width;
		this.vBuffer = height;
		return this;
	}
	
	
	public double getXMargin() {
		return this.hBuffer;
	}
	
	public double getYMargin() {
		return this.vBuffer;
	}
	
	public FrameAlignment setVertical() {
		this.horizontal = false;
		return this;
	}
	
	public boolean isHorizontal() {
		return horizontal;
	}
	
	
	
	public Vector2d getBaseAlignment(GuiPage frame) {
		switch(this) {
		case CENTER:
			return new Vector2d((frame.getWidth()), (frame.getHeight()));
		case TOPLEFT:
			return new Vector2d(frame.getWidth()/2, (frame.getHeight())/2);
		case TOPRIGHT:
			return new Vector2d(frame.getWidth()*1.5, (frame.getHeight())/2);
		case BOTTOMLEFT:
			return new Vector2d(frame.getWidth()/2, (frame.getHeight())*1.5);
		case BOTTOMRIGHT:
			return new Vector2d((frame.getWidth()*1.5), (frame.getHeight())*1.5);
		case TOPCENTER:
			return new Vector2d((frame.getWidth()), frame.getHeight()/2.0-8);
		case BOTTOMCENTER:
			return new Vector2d((frame.getWidth()), (frame.getHeight()*1.5));
		}
		return null;
	}

}
