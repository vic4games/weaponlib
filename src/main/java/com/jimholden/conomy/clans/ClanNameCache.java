package com.jimholden.conomy.clans;

import java.awt.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class ClanNameCache {
	private static final Map<String, Clans> clanNames = new ConcurrentHashMap<>();
	
	 @Nullable
	 public static Clans getClanByName(String name){
	     ensureNameCacheLoaded();
	     return clanNames.get(cleanName(name));
	 }
	 
	 
	 
	 
	 private static String cleanName(String name) {
		// TODO Auto-generated method stub
		return name;
	}
	



	public static boolean isClanNameUsed(String name) {
	     return clanNames.containsKey(cleanName(name));
	 }
	 
	 public static void addName(Clans nameClan){
	     ensureNameCacheLoaded();
	     System.out.println(nameClan);
	     clanNames.put((String) cleanName(nameClan.getName()), nameClan);
	 }
	
	 public static Collection<String> getClanNames() {
	     ensureNameCacheLoaded();
	     return Collections.unmodifiableCollection(clanNames.keySet());
	 }
	
	 private static void ensureNameCacheLoaded() {
	     if (clanNames.isEmpty())
	         for (Clans clan : ClanDatabase.getClans())
	             clanNames.put((String) cleanName(clan.getName()), clan);
	 }
	
	 public static void removeName(String name){
	     clanNames.remove(cleanName(name));
	 }
	

}
