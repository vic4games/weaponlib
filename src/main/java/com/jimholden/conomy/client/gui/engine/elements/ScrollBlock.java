package com.jimholden.conomy.client.gui.engine.elements;

import com.jimholden.conomy.client.gui.engine.IMouseClick;

public class ScrollBlock implements IMouseClick {

	public double height;
	
	public ScrollBlock(double height) {

		this.height = height;
	}
	
	public void renderScroll(double x, double y, double width, double yOffset) {
		
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		// TODO Auto-generated method stub
		
	}

}
