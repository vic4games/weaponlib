package com.jimholden.conomy.clans;

import java.util.UUID;

public class StockManager {
	public static void addStock(UUID uuid, String symbol, double initialPrice, int amountToBuy, boolean isBuyOrder)
	{
		PlayerDataStorage.getPlayerData(uuid).addStock(symbol, initialPrice, amountToBuy, isBuyOrder);
	}
	
	public static void removeStock(UUID uuid, String symbol) {
		PlayerDataStorage.getPlayerData(uuid).removeStock(symbol);
	}
	
	public static double getInitialPrice(UUID uuid, String symbol) {
		return PlayerDataStorage.getPlayerData(uuid).getInitialPrice(symbol);
	}
	
	public static int getShares(UUID uuid, String symbol) {
		return PlayerDataStorage.getPlayerData(uuid).getShares(symbol);
	}
	
	public static boolean getStockType(UUID uuid, String symbol) {
		return PlayerDataStorage.getPlayerData(uuid).getStockType(symbol);
		
	}

}
