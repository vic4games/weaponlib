package com.jimholden.conomy.drugs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrugCache {
	
	public static ArrayList<Drug> drugs = new ArrayList();
	
	public static void addDrug(Drug drug) {
		drugs.add(drug);
	}
	
	public static void removeDrug(Drug drug) {
		drugs.remove(drug);
	}
	
	public static boolean hasDrugs() {
		if(drugs == null) return false;
		if(drugs.isEmpty()) return false;
		else return true;
	}
	
	
	public static Nausea getTotalNausea() {
		float nSpeed = 0;
		float nVert = 0;
		float nHor = 0;
		float nIntensity = 0;
		for(Drug drugs : DrugCache.drugs) {
			nSpeed += (drugs.nSpeed*drugs.intensity());
			nVert += (drugs.nVert*drugs.intensity());
			nHor += (drugs.nHor*drugs.intensity());
			nIntensity += (drugs.nIntensity*drugs.intensity());
		}
		return new Nausea(nSpeed, nVert, nHor, nIntensity);
		
	}
	
	public static boolean hasNauseatingDrug() {
		if(drugs == null) return false;
		if(drugs.isEmpty()) return false;
		for(int x = 0; x < drugs.size(); x++) {
			if(drugs.get(x).nSpeed != 0) {
				return true;
			}
		}
		return false;
	}
	
	public static void tickDrugs() {
		if(drugs.isEmpty()) return;
		for(int x = 0; x < drugs.size(); x++) {
			Drug drug = drugs.get(x);
			if(drug != null) {
				drug.tickDrug();
				//System.out.println(drugs.toArray());
				if(drug.forRemoval) {
					removeDrug(drug);
				}
			}
		}
	}
	/*
	public static Map<String, Float> deSat;
	public static Map<String, Integer> ticker;
	public static Map<String, Integer> timeToEffect;
	
	public static void addDrug(String name, float desaturation, int time) {
		deSat.put(name, desaturation);
		ticker.put(name, 0);
		timeToEffect.put(name, time);
	}
	
	public static void tickDrug(String name) {
		int originalVal = ticker.get(name);
		ticker.put(name, originalVal++);
	}
	
	public static int getDrugTick(String name) {
		return ticker.get(name);
	}
	
	public static boolean hasDrug() {
		if(ticker == null) return false;
		if(!ticker.isEmpty()) return true;
		else return false;
	}
	*/
}
