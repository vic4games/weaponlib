package com.jimholden.conomy.economy;

public class TradeUtility {
	
	public double getCurrentPrice(double base, int original, int current, double interest) {
		return base*(1+(interest*((double) (original-current))));
	}

}
