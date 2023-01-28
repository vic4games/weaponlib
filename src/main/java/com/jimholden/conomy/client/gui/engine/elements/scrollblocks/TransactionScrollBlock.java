package com.jimholden.conomy.client.gui.engine.elements.scrollblocks;

import java.awt.Color;
import java.time.ZonedDateTime;

import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.elements.ScrollBlock;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.economy.record.Transaction;
import com.jimholden.conomy.proxy.ClientProxy;

public class TransactionScrollBlock extends ScrollBlock {

	public Transaction a;
	
	public TransactionScrollBlock(Transaction a, double height) {
		super(height);
		this.a = a;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void renderScroll(double x, double y, double width, double yOffset) {
		
		
		
		
		
		double scale = 0.8;
		double money = a.getAmount();
		
		String mString = (money > 0 ? "+" : "-") + Interchange.formatUSD(money);
		
		if((ClientProxy.newFontRenderer.getStringWidth(mString)*scale) > width) {
			//double b = (ClientProxy.newFontRenderer.getStringWidth(mString)*scale)/width;
			scale /= (ClientProxy.newFontRenderer.getStringWidth(mString)*scale)/width;
		}
		
		//System.out.println( + " | " + width);
		
		int color = 0;
		if(money > 0) {
			color = 0x4cd137;
		} else {
			color = 0xe84118;
		}
		
		if(a.getSender().getStringIdentifier().equals(a.getReceiver().getStringIdentifier())) {
			color = 0xf5f6fa;
		}
		
		
		
		GUItil.drawScaledString(ClientProxy.newFontRenderer, a.getSender().getStringIdentifier(), x+1, y+1, 0xfbc531, 0.6f);
		GUItil.drawScaledString(ClientProxy.newFontRenderer, a.getReceiver().getStringIdentifier(), x+0.995, y+5, 0xe84118, 0.5f);
		GUItil.drawScaledString(ClientProxy.newFontRenderer, a.getType().name(), x+0.9, y+9, 0xffffff, 0.4f);
		GUItil.drawScaledString(ClientProxy.newFontRenderer, mString, x+width-((ClientProxy.newFontRenderer.getStringWidth(mString)*scale)), y+13, color, (float) scale);
		
		
		
		
		super.renderScroll(x, y, width, yOffset);
	}
	

}
