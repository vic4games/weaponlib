package com.jimholden.conomy.client.gui.engine.elements;

import com.jimholden.conomy.client.gui.engine.GuiElement;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.Margins;

import net.minecraft.client.renderer.GlStateManager;

public class DisplayElement extends GuiElement {
	
	public IInfoDisplay elementInformation;

	public DisplayElement(FrameAlignment fa, IInfoDisplay info, double size, double scale, GuiPage page, Margins m) {
		super(fa, size, scale, page, m);
		elementInformation = info;
		info.setParent(this);
	}
	
	public DisplayElement(FrameAlignment fa, IInfoDisplay info, double size, double scale, GuiPage page) {
		super(fa, size, scale, page);
		elementInformation = info;
		info.setParent(this);
	}
	
	public DisplayElement(double x, double y, IInfoDisplay info, double size, double scale, GuiPage page) {
		super(x, y, size, scale, page);
		elementInformation = info;
		info.setParent(this);
		
		
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void render() {
		
		super.render();
		
		
		elementInformation.renderDisplay(this.triggerTimer.smooth(), 0.0);
	}


}
