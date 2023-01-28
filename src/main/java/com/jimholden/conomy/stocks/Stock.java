package com.jimholden.conomy.stocks;

public class Stock {
	public String symbol;
	public double regularMarketPrice;
	public String companyName;
	public String exchange;
	public double percentChange;
	public boolean isOpen;
	public String currency;
	
	public Stock(String symbol, double regularMarketPrice, double percentChange, String exchange, String companyName, boolean isOpen, String currency) {
		this.symbol = symbol;
		this.companyName = companyName;
		this.regularMarketPrice = regularMarketPrice;
		this.exchange = exchange;
		this.percentChange = percentChange;
		this.isOpen = isOpen;
		this.currency = currency;
	}

}
