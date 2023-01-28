package com.jimholden.conomy.client.gui.engine.elements;

import java.util.function.Supplier;

import com.jimholden.conomy.client.gui.engine.GuiElement;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class IconElement extends GuiElement {

	public Icon ico;
	public Supplier<Icon> icoSupplier;
	
	public Supplier<Double> xOffset;
	
	public IconElement(FrameAlignment fa, Supplier<Icon> ico, double size, double scale, GuiPage page) {
		super(fa, size*scale, scale, page);
		this.icoSupplier = ico;
		// TODO Auto-generated constructor stub
	}
	
	public IconElement(FrameAlignment fa, Icon ico, double size, double scale, GuiPage page) {
		super(fa, size*scale, scale, page);
		this.ico = ico;
		// TODO Auto-generated constructor stub
	}
	
	public IconElement(double x, double y, Icon ico2, double size, double scale, GuiPage main, Supplier<Double> xOffset) {
		super(x, y, size*scale, scale, main);
		this.ico = ico2;
		this.xOffset = xOffset;
	}
	
	public IconElement(double x, double y, Icon ico2, double size, double scale, GuiPage main) {
		super(x, y, size*scale, scale, main);
		this.ico = ico2;
	}
	
	public Icon getIcon() {
		if(this.icoSupplier == null) {
			return this.ico;
		} else {
			return this.icoSupplier.get();
		}
	}

	@Override
	public void render() {
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		try {
			Minecraft.getMinecraft().getTextureManager().getTexture(getIcon().rl).setBlurMipmap(true, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
		double xOf = 0.0;
		if(this.xOffset != null) xOf = xOffset.get();
		getIcon().render(getX()+xOf, getY(), this.scale);
		super.render();
	}


}
