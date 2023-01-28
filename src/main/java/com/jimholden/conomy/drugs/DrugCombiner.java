package com.jimholden.conomy.drugs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

public class DrugCombiner {
	
	/*
	 1 = amphetamine
	 2 = ephedrine
	3 = cleaning agent
	4 = cutting agent
	5 = alchohol
	6 = opium alkaloid
	7 = morphine
	8 = amplifier
	9 = benzo
	10 = coca alkaloid 
	11 = cocain 
	12 = heroin
	13 = hallucinogen
	14 = psychedelic
	15 = stimulant
	16 = depressant
*/
	
	public static String combineName(int one, int two, int three) {
		

		List<Integer> list = Arrays.asList(one, two, three); 
		
		if(list.contains(1) && list.contains(2) && list.contains(3)) {
			return "Methamphetamine";
		}
		else if(list.contains(1) && list.contains(2) && list.contains(4)) {
			return "Methadone";
		}
		else if(list.contains(1) && list.contains(5) && list.contains(0)) {
			return "MDMA";
		}
		else if(list.contains(1) && list.contains(8) && list.contains(0)) {
			return "Speed";
		}
		else if(list.contains(2) && list.contains(8) && list.contains(0)) {
			return "Adderall";
		}
		else if(list.contains(6) && list.contains(5) && list.contains(0)) {
			return "Oxycodone";
		}
		else if(list.contains(6) && list.contains(8) && list.contains(3)) {
			return "Morphine";
		}
		else if(list.contains(6) && list.contains(8) && list.contains(0)) {
			return "Opium";
		}
		else if(list.contains(7) && list.contains(3) && list.contains(0)) {
			return "Heroin";
		}
		else if(list.contains(7) && list.contains(6) && list.contains(8)) {
			return "Fentanyl";
		}
		else if(list.contains(7) && list.contains(3) && list.contains(8)) {
			return "Dilaudid";
		}
		else if(list.contains(7) && list.contains(4) && list.contains(0)) {
			return "Buprenex";
		}
		else if(list.contains(6) && list.contains(2) && list.contains(0)) {
			return "Codeine";
		}
		else if(list.contains(10) && list.contains(8) && list.contains(3)) {
			return "Cocaine";
		}
		else if(list.contains(11) && list.contains(12) && list.contains(0)) {
			return "Krokodil";
		}
		else if(list.contains(8) && list.contains(4) && list.contains(0)) {
			return "Xanax";
		}
		else if(list.contains(9) && list.contains(1) && list.contains(0)) {
			return "Legal-X";
		}
		else if(list.contains(13) && list.contains(1) && list.contains(8)) {
			return "PCP";
		}
		else if(list.contains(13) && list.contains(5) && list.contains(3)) {
			return "LSD";
		}
		else if(list.contains(13) && list.contains(2) && list.contains(5)) {
			return "AMT";
		}
		else if(list.contains(13) && list.contains(7) && list.contains(5)) {
			return "Ketamine";
		} else {
			return "Compound";
		}
		
	}
	
	
	public static int combineType(int one, int two, int three) {


		List<Integer> list = Arrays.asList(one, two, three); 
		
		if(list.contains(1) && list.contains(2) && list.contains(3)) {
			return 15;
		}
		if(list.contains(1) && list.contains(2) && list.contains(4)) {
			return 15;
		}
		if(list.contains(1) && list.contains(5) && list.contains(0)) {
			return 15;
		}
		if(list.contains(1) && list.contains(8) && list.contains(0)) {
			return 15;
		}
		if(list.contains(2) && list.contains(8) && list.contains(0)) {
			return 15;
		}
		if(list.contains(6) && list.contains(5) && list.contains(0)) {
			return 16;
		}
		if(list.contains(6) && list.contains(8) && list.contains(3)) {
			return 7;
		}
		if(list.contains(6) && list.contains(8) && list.contains(0)) {
			return 16;
		}
		if(list.contains(7) && list.contains(3) && list.contains(0)) {
			return 12;
		}
		if(list.contains(7) && list.contains(6) && list.contains(8)) {
			return 16;
		}
		if(list.contains(7) && list.contains(3) && list.contains(8)) {
			return 16;
		}
		if(list.contains(7) && list.contains(4) && list.contains(0)) {
			return 16;
		}
		if(list.contains(6) && list.contains(2) && list.contains(0)) {
			return 16;
		}
		if(list.contains(10) && list.contains(8) && list.contains(3)) {
			return 15;
		}
		if(list.contains(11) && list.contains(12) && list.contains(0)) {
			return 15;
		}
		if(list.contains(8) && list.contains(4) && list.contains(0)) {
			return 16;
		}
		if(list.contains(9) && list.contains(1) && list.contains(0)) {
			return 15;
		}
		if(list.contains(13) && list.contains(1) && list.contains(8)) {
			return 14;
		}
		if(list.contains(13) && list.contains(5) && list.contains(3)) {
			return 14;
		}
		if(list.contains(13) && list.contains(2) && list.contains(5)) {
			return 14;
		}
		if(list.contains(13) && list.contains(7) && list.contains(5)) {
			return 14;
		}
		
		
		return 0;
	}
	
	
}
