package com.jimholden.conomy.drugs;

public class DrugNaming {
	
	public static String basicName(float potency) {
		if(potency > 0.8F) {
			return "Strong Powder";
		}
		if(potency < 0.8F) {
			return "Weak Powder";
		}
		return null;
	}
	
	
	public static String getDrugTypeByID(int id) {
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
		switch(id) {
			default:
				return null;
			case 0:
				return "Base";
			case 1:
				return "Amphetamine";
			case 2:
				return "Ephedrine";
			case 3:
				return "Cleaning Agent";
			case 4:
				return "Cutting Agent";
			case 5:
				return "Alcohol";
			case 6:
				return "Opium Alkaloid";
			case 7:
				return "Morphine";
			case 8:
				return "Amplifier";
			case 9:
				return "Benzo";
			case 10:
				return "Coca Alkaloid";
			case 11:
				return "Cocaine";
			case 12:
				return "Heroin";
			case 13:
				return "Hallucinogen";
			case 14:
				return "Psychedelic";
			case 15:
				return "Stimulant";
			case 16:
				return "Depressant";
		}
	}

}
