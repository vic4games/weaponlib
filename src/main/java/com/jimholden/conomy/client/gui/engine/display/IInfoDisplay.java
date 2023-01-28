package com.jimholden.conomy.client.gui.engine.display;

import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;

public interface IInfoDisplay {
	
	public static final IInfoDisplay NONE = new NoDisplay();

	public void renderDisplay();
	public void renderDisplay(double opacity, double scale);
	public void renderDisplay(double opacity, double scale, double xOverride, double yOverride);
	public void setParent(IHasInfo parent);

}
