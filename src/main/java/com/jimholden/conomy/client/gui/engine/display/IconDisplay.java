package com.jimholden.conomy.client.gui.engine.display;

import java.util.function.Supplier;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.client.gui.engine.CustomFontRenderer;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.client.gui.engine.buttons.Alignment;
import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;
import com.jimholden.conomy.client.gui.engine.fields.ConomyTextField;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class IconDisplay implements IInfoDisplay {
	
	public FontRenderer fr = ClientProxy.newFontRenderer;
	
	
	public Supplier<Icon> icoSupplier;
	public Icon ico;
	public double scale;
	public int color;
	
	public IHasInfo parent;
	
	public Alignment align;
	
	public boolean shadow = true;
	
	public IconDisplay(Icon ico, Alignment align, int color, double scale, IHasInfo parent) {
		this.ico = ico;
		this.scale = scale;
		this.color = color;
		this.align = align;
		setParent(parent);
	}
	

	
	public IconDisplay(Supplier<Icon> icoSupplier, Alignment align, int color, double scale) {
		this.icoSupplier = icoSupplier;
		this.scale = scale;
		this.color = color;
		this.align = align;
	}
	
	public IconDisplay(Icon ico, Alignment align, int color, double scale) {
		this.ico = ico;
		this.scale = scale;
		this.color = color;
		this.align = align;
	}

	@Override
	public void setParent(IHasInfo parent) {
		this.parent = parent;
		
	}
	
	public Icon getIcon() {
		if(this.icoSupplier == null) {
			return this.ico;
		} else {
			return this.icoSupplier.get();
		}
	}
	
	@Override
	public void renderDisplay() {
		renderDisplay(1.0, 1.0);
	}

	@Override
	public void renderDisplay(double sc, double opacity) {
		renderDisplay(sc, opacity, -1, -1);
	}
	
	
	@Override
	public void renderDisplay(double sc, double opacity, double xOverride, double yOverride) {
		
		double aScale = scale*sc;
		
		if(this.parent instanceof ConomyTextField) {
			if(this.align == Alignment.ABOVE) {
				
			}
		}
		
		
		
		
		double x = parent.getX() + (align.getXOffset(parent.getNWidth(), -2.0));
		double y = parent.getY() + (align.getYOffset(parent.getNHeight(), -2.0+(fr.FONT_HEIGHT/2*aScale)));
	
		if(yOverride != -1) {
			y = yOverride;
		}
		if(xOverride != -1) {
			x = xOverride;
		}
		


		//System.out.println(x + " | " + y);
		
		double height = (fr.FONT_HEIGHT*aScale)/2.0;
		
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		getIcon().render(x, y, aScale);
		/*
		if(this.align.isCenteredVertically()) {
			GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, getString(), x, y-(height)+0.5, color, (float) aScale, this.shadow);
		} else {
			GUItil.drawScaledString(ClientProxy.newFontRenderer, getString(), x, y-(height)+0.5, color, (float) aScale, this.shadow);
		}*/
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		
	}

}
