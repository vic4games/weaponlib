package com.jimholden.conomy.client.gui.engine.elements.scrollblocks;

import java.awt.Color;

import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.elements.ScrollBlock;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.proxy.ClientProxy;

public class AccountScrollBlock extends ScrollBlock {

	public Account a;
	
	public AccountScrollBlock(Account a, double height) {
		super(height);
		this.a = a;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void renderScroll(double x, double y, double width, double yOffset) {
		
		
		double scale = 0.8;
		double money = a.getMoney();
		String mString = Interchange.formatUSD(money);
		
		if((ClientProxy.newFontRenderer.getStringWidth(mString)*scale) > width) {
			//double b = (ClientProxy.newFontRenderer.getStringWidth(mString)*scale)/width;
			scale /= (ClientProxy.newFontRenderer.getStringWidth(mString)*scale)/width;
		}
		
		//System.out.println( + " | " + width);
		
		
		
		
		GUItil.drawScaledString(ClientProxy.newFontRenderer, "\"" + a.getAccountName() + "\"", x+1, y+1, 0xfbc531, 0.5f);
		GUItil.drawScaledString(ClientProxy.newFontRenderer, a.getAccountType().toString(), x+1.3, y+5, 0xe84118, 0.4f);
		GUItil.drawScaledString(ClientProxy.newFontRenderer, mString, x+width-((ClientProxy.newFontRenderer.getStringWidth(mString)*scale)), y+13, 0x4cd137, (float) scale);
		
		
		
		
		super.renderScroll(x, y, width, yOffset);
	}
	

}
