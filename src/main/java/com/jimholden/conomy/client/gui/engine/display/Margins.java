package com.jimholden.conomy.client.gui.engine.display;

public class Margins {
	
	public double x = 0.0;
	public double y = 0.0;
	
	public static final Margins NONE = new Margins(0, 0);
	
	public Margins(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getXMargin() {
		return this.x;
	}
	
	public double getYMargin() {
		return this.y;
	}

}
