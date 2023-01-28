package com.jimholden.conomy.clans;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmationCodeCache {
	private static final Map<UUID, String> CONFIRMATION_CACHE = new ConcurrentHashMap<>();
	
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static String randomCode() {
		int count = 6;
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
	
	public static void addCode(UUID player, String code)
	{
		CONFIRMATION_CACHE.put(player, code);
	}
	
	public static void removeCode(UUID player, String code)
	{
		CONFIRMATION_CACHE.remove(player);
	}
	
	public static boolean checkCode(UUID player, String code)
	{
		if(CONFIRMATION_CACHE.get(player).equals(code)) {
			return true;
		}
		else return false;
	}
	
	public static Map<UUID, String> getCache() {
		return CONFIRMATION_CACHE;
	}

}
