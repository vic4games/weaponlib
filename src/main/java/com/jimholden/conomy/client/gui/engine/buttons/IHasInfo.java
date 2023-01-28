package com.jimholden.conomy.client.gui.engine.buttons;

import com.jimholden.conomy.client.gui.engine.display.DisplayElement;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.Margins;

public interface IHasInfo {
	
	public IInfoDisplay getDisplayElement();
	
	public double getX();
	public double getY();
	
	public double getNWidth();
	public double getNHeight();
	
	public void setX(double x);
	public void setY(double y);
	
	public Margins getMarginHandler();

}
