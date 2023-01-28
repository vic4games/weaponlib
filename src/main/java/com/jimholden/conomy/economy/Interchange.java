package com.jimholden.conomy.economy;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.text.NumberFormatter;

public class Interchange {
	
	public static final NumberFormat USD_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
	
	static {
		USD_FORMATTER.setMaximumFractionDigits(2);
	}
	
	public static double getMRCtoUSDRatio() {
		return 0.5;
	}
	
	public static double currencyRound(double amt) {
		return Math.round(amt*100)/100.0;
	}
	
	public static String formatUSD(double amt) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
		return nf.format(amt);
		
	}

}
