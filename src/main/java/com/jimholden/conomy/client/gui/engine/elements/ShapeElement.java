package com.jimholden.conomy.client.gui.engine.elements;

import com.jimholden.conomy.client.gui.engine.GuiElement;
import com.jimholden.conomy.client.gui.engine.GuiPage;

public class ShapeElement extends GuiElement {
	
	public enum Shape {
		CIRCLE,
		ROUNDEDSQUARE;
	}

	public ShapeElement(double x, double y, double width, double height, double scale, GuiPage page) {
		super(x, y, width, height, scale, page);
		
	}
	
	@Override
	public void render() {
		// TODO Auto-generated method stub
		super.render();
	}

}
