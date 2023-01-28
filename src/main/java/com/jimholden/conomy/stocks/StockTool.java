package com.jimholden.conomy.stocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class StockTool {
	
	public static Stock getStockData(String symbol) {
		String urlToSearch = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=" + symbol;
		URL github = null;
		try {
			github = new URL(urlToSearch);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(github.openStream()));
		} catch (IOException e) {
			return null;
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		JsonObject obj = new Gson().fromJson(in, JsonObject.class);
		
		obj = obj.get("quoteResponse").getAsJsonObject();
		obj = obj.get("result").getAsJsonArray().get(0).getAsJsonObject();
		
		String quoteType = obj.get("quoteType").getAsString();
		String companyName;
		if(quoteType.equals("CRYPTOCURRENCY"))
		{
			companyName = obj.get("shortName").getAsString();
		}
		else {
			companyName = obj.get("longName").getAsString();
		}
		
		double price = obj.get("regularMarketPrice").getAsDouble();
		
		boolean marketState;
		String state = obj.get("marketState").getAsString();
		if(state.equals("CLOSED")) {
			marketState = false;
		} else marketState = true;
		
		//String companyName = obj.get("longName").getAsString();
		
		double percentChangeRegular = obj.get("regularMarketChangePercent").getAsDouble();
		String exchangeName = obj.get("fullExchangeName").getAsString();
		String symbolOut = obj.get("symbol").getAsString();
		String currency = obj.get("currency").getAsString();
		return new Stock(symbolOut, price, percentChangeRegular, exchangeName, companyName, marketState, currency);

	}
	
	public static String getFormatStockPercent(double iPrice, double nPrice, boolean isBuyOrder) {
		double percentDifference;
		if(isBuyOrder) {
			percentDifference = Math.round(((nPrice-iPrice)/iPrice)*100)/100;
		} else {
			percentDifference = Math.round(((iPrice-nPrice)/nPrice)*100)/100;
		}
		if(percentDifference > 0) {
			return TextFormatting.GREEN + "+%" + Math.abs(percentDifference);
		} else {
			return TextFormatting.RED + "-%" + Math.abs(percentDifference);
		}
	}
	
	public static int calculateProfit(double iPrice, double nPrice, int shares, boolean isBuyOrder) {
		double initialTotal = iPrice*shares;
		double newTotal = nPrice*shares;
		if(isBuyOrder) {
			return (int) ((iPrice*shares) + (newTotal-initialTotal));
			
		} else {
			return (int) ((iPrice*shares) + (initialTotal-newTotal));
		}
		
	}
	
}
