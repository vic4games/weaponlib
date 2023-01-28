package com.jimholden.conomy.stocks;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.ConcurrentSet;

public class StockCache {
	
    private static Map<String, Integer> stocks = new ConcurrentHashMap<String, Integer>();
    private static Map<String, Double> initialStockPrices = new ConcurrentHashMap<String, Double>();
    private static Map<String, Boolean> stockTypes = new ConcurrentHashMap<String, Boolean>();
    
    public static void addStock(String symbol, int shares, double initPrice, boolean isBuyOrder) {
    	stocks.put(symbol, shares);
    	initialStockPrices.put(symbol, initPrice);
    	stockTypes.put(symbol, isBuyOrder);
    }
    
    public static void removeStock(String symbol) {
    	stocks.remove(symbol);
    	initialStockPrices.remove(symbol);
    	stockTypes.remove(symbol);
    }
    
    public static int getStockShares(String symbol) {
    	return stocks.get(symbol);
    }
    
    public static double getInitialStockPrice(String symbol) {
    	return initialStockPrices.get(symbol);
    }
    
    public static boolean hasStock(String symbol) {
    	return stocks.containsKey(symbol);
    }
    
    public static boolean getStockType(String symbol) {
    	return stockTypes.get(symbol);
    }

}
