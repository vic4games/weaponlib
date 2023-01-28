package com.jimholden.conomy.client.gui.engine.display;

import com.jimholden.conomy.client.gui.engine.buttons.Alignment;

public class DisplayElement {
	
	public Alignment alignment;
	public IInfoDisplay info;
	
	public DisplayElement(IInfoDisplay info, Alignment align) {
		this.info = info;
		this.alignment = align;
	}

	
	
	
	public void renderDisplayElement() {
		info.renderDisplay();
	}




	public Alignment getAlignment() {
		return alignment;
	}




	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}




	public IInfoDisplay getInfo() {
		return info;
	}




	public void setInfo(IInfoDisplay info) {
		this.info = info;
	}
 
}
