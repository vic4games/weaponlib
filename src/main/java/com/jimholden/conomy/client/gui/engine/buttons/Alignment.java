package com.jimholden.conomy.client.gui.engine.buttons;

public enum Alignment {
	CENTER,
	LEFT,
	RIGHT,
	BELOW,
	ABOVE,
	BOTTOM,
	TOP,
	TOPLEFT,
	BOTTOMLEFT;
	
	public boolean isCenteredVertically() {
		switch(this) {
		case CENTER:
			return true;
		case LEFT:
			return false;
		case RIGHT:
			return false;
		case BELOW:
			return true;
		case ABOVE:
			return true;
		case BOTTOM:
			return true;
		case TOP:
			return true;
		case TOPLEFT:
			return false;
		case BOTTOMLEFT:
			return false;
		}
		return false;
	}
	
	
	public double getXOffset(double width, double buffer) {
		
		switch(this) {
		case CENTER:
			return 0.0;
		case LEFT:
			return -width/2.0 - buffer;
		case RIGHT:
			return width/2.0 + buffer;
		case BELOW:
			return 0.0;
		case ABOVE:
			return 0.0;
		case BOTTOM:
			return 0.0;
		case TOP:
			return 0.0;
		case TOPLEFT:
			return LEFT.getXOffset(width, buffer);
		case BOTTOMLEFT:
			return LEFT.getXOffset(width, buffer);
		}
		
		return width;
		
	}
	
	public double getYOffset(double height, double buffer) {
		switch(this) {
		case CENTER:
			return 0.0;
		case LEFT:
			return 0.0;
		case RIGHT:
			return 0.0;
		case BELOW:
			return height+buffer;
		case ABOVE:
			return -height-buffer;
		case BOTTOM:
			return height + buffer*8;
		case TOP:
			return -height + buffer*8;
		case TOPLEFT:
			return ABOVE.getYOffset(height, buffer);
		case BOTTOMLEFT:
			return BELOW.getYOffset(height, buffer);
		}
		return height;
		
	}

}
