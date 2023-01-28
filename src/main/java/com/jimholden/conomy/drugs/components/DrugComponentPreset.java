package com.jimholden.conomy.drugs.components;

import java.util.ArrayList;

import com.jimholden.conomy.drugs.DrugProperty;

public class DrugComponentPreset {
    
    
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
    11 = cocaine 
    12 = heroin
    13 = hallucinogen
    */
	// (name, weight, potency, drugType, synthetic, isPowder)
	
    //cutting agents
    public static final DrugProperty SODIUM_BICARBONATE = new DrugProperty("Sodium Bicarbonate", 100, -2, 4, true, true);
	public static final DrugProperty SUCROSE = new DrugProperty("Sucrose", 100, -2, 4, true, true);
	public static final DrugProperty TALLCUM = new DrugProperty("Talcum", 100, -2, 4, true, true);
	public static final DrugProperty CREATINE = new DrugProperty("Creatine", 100, -2, 4, true, true);
	public static final DrugProperty SAWDUST = new DrugProperty("Sawdust", 100, -2, 4, true, true);

	//cleaning agent
	public static final DrugProperty SODIUM_HYPOCHLORITE = new DrugProperty("Sodium Hypochlorite", 100, 1, 3, true, false);
	public static final DrugProperty AMMONIA = new DrugProperty("Ammonia", 100, 1, 3, true, false);
	public static final DrugProperty ACETONE = new DrugProperty("Acetone", 100, 1, 3, true, false);
	public static final DrugProperty BORAX = new DrugProperty("Borax", 100, 1, 3, true, true);
	public static final DrugProperty CALCIUM_HYPOCHLORITE = new DrugProperty("Calcium Hypochlorite", 100, 1, 3, true, true);
	public static final DrugProperty ACETIC_ACID = new DrugProperty("Acetic Acid", 100, 1, 3, true, false);
	public static final DrugProperty POTASSIUM_HYDROXIDE = new DrugProperty("Potassium Hydroxide", 100, 1, 3, true, true);
	public static final DrugProperty FORMALDEHYDE = new DrugProperty("Formaldehyde", 100, 1, 3, true, false);
	public static final DrugProperty PERACETIC_ACID = new DrugProperty("Peracetic acid", 100, 1, 3, true, false);
	
	
	//amphetamine
	public static final DrugProperty METHYLPHENIDATE = new DrugProperty("Methylphenidate", 100, 3, 1, true, true);
	public static final DrugProperty ARMODAFINITLTE = new DrugProperty("Armodafinilte", 100, 3, 1, true, true);
	public static final DrugProperty MODAFINIL = new DrugProperty("Modafinil", 100, 3, 1, true, true);
	public static final DrugProperty DEXTRIANPHETAMINE = new DrugProperty("Dextroamphetamine", 100, 3, 1, true, true);
	public static final DrugProperty DEXMETHYLPHENIDATE = new DrugProperty("Dexmethylphenidate", 100, 3, 1, true, true);
	
	//ephedrine
	public static final DrugProperty CHLOREPHENIRAMINE = new DrugProperty("Chlorpheniramine", 100, 3, 2, true, true);
	public static final DrugProperty PSEUDOEPHEDRINE_GUAIFENESIN = new DrugProperty("Pseudoephedrine-Guaifenesin", 100, 3, 2, true, true);
	public static final DrugProperty LORATADINE = new DrugProperty("Loratadine", 100, 3, 2, true, true);
	public static final DrugProperty ACETAMINOPHEN = new DrugProperty("Acetaminophen", 100, 3, 2, true, true);
	
	//alchohol
	public static final DrugProperty ETHANOL = new DrugProperty("Ethanol", 100, 3, 5, true, false);
	public static final DrugProperty PROPANOL = new DrugProperty("Propanol", 100, 3, 5, true, false);
	public static final DrugProperty ISOPROPYL = new DrugProperty("Isopropyl", 100, 3, 5, true, false);
	public static final DrugProperty CYCLOHEXANOL = new DrugProperty("Cyclohexanol", 100, 3, 5, true, false);
	public static final DrugProperty METHYL_PROPANOL = new DrugProperty("Methyl Propanol", 100, 3, 5, true, false);
	public static final DrugProperty METHANOL = new DrugProperty("Methanol", 100, 3, 5, true, false);
	public static final DrugProperty GLYCEROL = new DrugProperty("Glycerol", 100, 3, 5, true, false);
	public static final DrugProperty BUTANOL = new DrugProperty("Butanol", 100, 3, 5, true, false);
	public static final DrugProperty VOLEMITOL = new DrugProperty("Volemitol", 100, 3, 5, true, false);
	
	//benzo
	public static final DrugProperty ALPRAZOLAM = new DrugProperty("Alprazolam", 100, 3, 9, true, true);
	public static final DrugProperty CLONAZEPAM = new DrugProperty("Clonazepam", 100, 3, 9, true, true);
	public static final DrugProperty CHLORDIAZEPOXIDE = new DrugProperty("Chlordiazepoxide", 100, 3, 9, true, true);
	public static final DrugProperty DIAZEPAM = new DrugProperty("Diazepam", 100, 3, 9, true, true);
	public static final DrugProperty LORAZEPAM = new DrugProperty("Lorazepam", 100, 3, 9, true, true);
	public static final DrugProperty TEMAZEPAM = new DrugProperty("Temazepam", 100, 3, 9, true, true);
	public static final DrugProperty TRIAZOLAM = new DrugProperty("Triazolam", 100, 3, 9, true, true);
	
	//hallucinogen
	public static final DrugProperty PSILOCYBIN = new DrugProperty("Psilocybin", 100, 3, 13, true, true);
	public static final DrugProperty MESCALINE = new DrugProperty("Mescaline", 100, 3, 13, true, true);
	public static final DrugProperty AYAHUASCA = new DrugProperty("Ayahuasca", 100, 3, 13, true, true);
	public static final DrugProperty DEXTROMETHORPHAN = new DrugProperty("Dextromethorphan", 100, 3, 13, true, true);
	public static final DrugProperty SALVIA_DIVINORUM = new DrugProperty("Salvia Divinorum", 100, 3, 13, true, true);
	public static final DrugProperty BUFOTENIN = new DrugProperty("Bufotenin", 100, 3, 13, true, true);
	public static final DrugProperty METHOXETAMINE = new DrugProperty("Methoxetamine", 100, 3, 13, true, true);

	//cocaine
	public static final DrugProperty COCAINE = new DrugProperty("Cocaine", 100, 3, 11, true, true);
	
	//morphine
	public static final DrugProperty MORPHINE = new DrugProperty("Morphine", 100, 3, 7, true, true);
	
	//coca alkaloid 
	public static final DrugProperty COCA_PYRROLIDINE = new DrugProperty("Coca Pyrrolidine", 100, 3, 10, true, true);
	public static final DrugProperty COCA_TROPANE = new DrugProperty("Coca Tropane", 100, 3, 10, true, true);
	
	//opium alkaloid
	public static final DrugProperty POPPY_GUM = new DrugProperty("Poppy Gum", 100, 3, 6, true, true);
	
	//amplifier
	public static final DrugProperty SULFURIC_ACID = new DrugProperty("Sulfuric Acid", 100, 3, 8, true, false);
	public static final DrugProperty OCTANE = new DrugProperty("Octane", 100, 3, 8, true, false);
	public static final DrugProperty NITROGLYCERIN = new DrugProperty("Nitroglycerin", 100, 3, 8, true, false);
	public static final DrugProperty TURPENTINE = new DrugProperty("Turpentine", 100, 1, 8, true, false);
	public static final DrugProperty KEROSENE = new DrugProperty("Kerosene", 100, 1, 8, true, false);
	public static final DrugProperty BUTANE = new DrugProperty("Butane", 100, 1, 8, true, false);
	
	
	
	
}