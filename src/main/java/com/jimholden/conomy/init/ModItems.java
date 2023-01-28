
package com.jimholden.conomy.init;

import java.util.ArrayList;
import java.util.List;

import com.jimholden.conomy.chemistry.ChemInit;
import com.jimholden.conomy.chemistry.items.BaseChemItem;
import com.jimholden.conomy.drugs.DrugBasePreset;
import com.jimholden.conomy.drugs.DrugExtractRecipe;
import com.jimholden.conomy.drugs.components.DrugComponentPreset;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.MedicalItem;
import com.jimholden.conomy.items.MedicalItem.MedicalProperties;
import com.jimholden.conomy.items.BasicFoodItem;
import com.jimholden.conomy.items.BasicSeed;
import com.jimholden.conomy.items.BindingTool;
import com.jimholden.conomy.items.ClimbingAnchorItem;
import com.jimholden.conomy.items.EmptyWaterItem;
import com.jimholden.conomy.items.FacemaskItem;
import com.jimholden.conomy.items.GlassesItem;
import com.jimholden.conomy.items.HeadsetItem;
import com.jimholden.conomy.items.HelmetItem;
import com.jimholden.conomy.items.ItemAccessCard;
import com.jimholden.conomy.items.ItemBase;
import com.jimholden.conomy.items.ItemBaseComponent;
import com.jimholden.conomy.items.ItemBodyArmor;
import com.jimholden.conomy.items.ItemBottle;
import com.jimholden.conomy.items.ItemChemicalBriefcase;
import com.jimholden.conomy.items.ItemDrugBrick;
import com.jimholden.conomy.items.ItemDrugPowder;
import com.jimholden.conomy.items.ItemDrugSyringe;
import com.jimholden.conomy.items.ItemFlag;
import com.jimholden.conomy.items.ItemJacket;
import com.jimholden.conomy.items.ItemPackingMaterial;
import com.jimholden.conomy.items.ItemPants;
import com.jimholden.conomy.items.ItemPill;
import com.jimholden.conomy.items.ItemRock;
import com.jimholden.conomy.items.ItemRope;
import com.jimholden.conomy.items.ItemShirt;
import com.jimholden.conomy.items.ItemShoes;
import com.jimholden.conomy.items.ItemSplint;
import com.jimholden.conomy.items.ItemWater;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.MemoryItem;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.items.OpiumGumItem;
import com.jimholden.conomy.items.Placer;
import com.jimholden.conomy.items.RigItem;
import com.jimholden.conomy.items.SoftwareFlashBase;
import com.jimholden.conomy.items.StockTerminalBase;
import com.jimholden.conomy.items.models.HXSuit.HXVest;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ModItems 
{
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static final Item OPENDIME = new OpenDimeBase("opendime");
	public static final Item LEDGERNANO = new LedgerBase("ledgernano");
	public static final Item STOCKTERMINAL = new StockTerminalBase("stockterminal");
	public static final Item BINDINGTOOL = new BindingTool("bindingtool");
	public static final Item USBSTICKBASE = new MemoryItem("usbstickbase", 32000000);
	public static final Item ROCK = new ItemRock("rock");
	public static final Item CLIMBING_ANCHOR = new ClimbingAnchorItem("climbinganchor");
	
	//
	public static final Item REGULAR_ROPE = new ItemRope("regularrope", 1, 3.0F);
	public static final Item BLUE_ROPE = new ItemRope("bluerope", 2, 4.5F);
	public static final Item STEEL_ROPE = new ItemRope("steelwire", 3, 30.0F);
	public static final Item BUNGEE_ROPE = new ItemRope("bungeerope", 4, 0.5F);
	public static final Item RED_ROPE = new ItemRope("redrope", 5, 5.0F);
	public static final Item SPECTRA_ROPE = new ItemRope("spectrarope", 6,15.0F);
	
	// key cards
	public static final Item ORANGE_KEYCARD = new ItemAccessCard("orangekeycard");
	
	
	//Software flashes
	public static final Item AMEXFLASH = new SoftwareFlashBase("amexflash", 4, 20);
	
	public static final Item POPPYSTRAW = new OpiumGumItem("poppystraw");
	public static final Item OPIUMGUM = new OpiumGumItem("opiumgum");
	
	
	public static final Item MILBANDAGE = new MedicalItem("militarybandage", new MedicalProperties(10, 0, 0, true));
	public static final Item SPONGEBOBBANDAID = new MedicalItem("spongebobbandaid", new MedicalProperties(5, 2, 10, true));
	public static final Item MEDICALBANDAGE = new MedicalItem("medicalbandage", new MedicalProperties(3, 0, 0, true));
	
	public static final Item LEGSPLINT = new ItemSplint("legsplint");
	
	/*
	 * HEADSETS
	 */
	public static final Item HEADSET = new HeadsetItem("headset", ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD);
	public static final Item USMCHEADSET = new HeadsetItem("usmcheadset", ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD);
	public static final Item COMTACS = new HeadsetItem("comtacs", ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD);
	public static final Item PLANTRONICS = new HeadsetItem("plantronics800hd", ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD);
	public static final Item CATEARS = new HeadsetItem("catears", ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD);
	/*
	 * FACE MASKS
	 */
	
	public static final Item GHOSTFACEMASK = new FacemaskItem("ghostmask");
	public static final Item FACEBANDANA = new FacemaskItem("facebandana");
	public static final Item REDSCARF = new FacemaskItem("redscarf");
	public static final Item TOKYOGHOULMASK = new FacemaskItem("tokyoghoulmask");
	
	/*
	 * BACKPACKS
	 */
	public static final Item F5SWITCHBLADE = new BackpackItem("f5switchblade", 24, 2.0);
	public static final Item OAKLEYMECHANISM = new BackpackItem("oakleymechanism", 21, 2.0);
	public static final Item DUFFLEBAG = new BackpackItem("dufflebag", 32, 2.0);
	
	/*
	 * FOOD
	 */
	public static final Item CANNEDPEACHES = new BasicFoodItem("cannedpeachesopened", 4);
	public static final Item CANNEDBAKEDBEANS = new BasicFoodItem("openedbakedbeans", 5);
	public static final Item CANNEDSARDINES = new BasicFoodItem("cannedsardinesopened", 6);
	public static final Item MRE = new BasicFoodItem("mre", 10);
	
	public static final Item CANNEDPEACHESCLOSED = new ItemBase("cannedpeachesclosed");
	public static final Item CANNEDBAKEDBEANSCLOSED  = new ItemBase("cannedbakedbeansclosed");
	public static final Item CANNEDSARDINESCLOSED  = new ItemBase("cannedsardinesclosed");
	
	/*
	 * RIGS
	 */
	public static final Item TRITONRIG = new RigItem("tritonrig", 10, 3.0);
	
	
	public static final Item MOLLEPLATECARRIER = new ItemBodyArmor("molleplatecarier");
	public static final Item ATLAST7 = new ItemBodyArmor("atlast7");
	public static final Item ANAM2TACTICALVEST = new ItemBodyArmor("anam2tacticalvest");
	
	public static final Item BODYARMORIII = new ItemBodyArmor("level3armor");
	public static final Item HXBODY = new ItemBodyArmor("humanityxbodysuit");
	public static final Item HXVEST = new ItemBodyArmor("hx_vest");
	public static final Item HXHELMET = new HelmetItem("hxhelmet", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD);
	
	
	public static final Item LEONSJACKET = new ItemJacket("leonsjacket");
	public static final Item JEEPSPIRITJACKET = new ItemJacket("jeepspiritjacket");
	public static final Item ALPHAFLJACKET = new ItemJacket("alphafljacket");
	public static final Item BLACKHALWOODTUXEDO = new ItemJacket("blackhalwoodtuxedo");
	public static final Item GORKA4JACKET = new ItemJacket("gorka4jacket");
	
	/*
	 * SHIRTS
	 */
	public static final Item BLACKSHIRT = new ItemShirt("blackshirt");
	public static final Item FORMALSHIRT = new ItemShirt("formalshirt");
	public static final Item BLACKMULTICAMOSHIRT = new ItemShirt("blackmulticamoshirt");
	public static final Item FORESTMILITARYSHIRT = new ItemShirt("forestmulticamo");
	public static final Item NAVYBLUESHIRT = new ItemShirt("navyblueshirt");
	public static final Item BLACKFORMALSHIRT = new ItemShirt("blackformalshirt");

	
	
	/*
	 * PANTS
	 */
	public static final Item KHAKIJEANS = new ItemPants("khakijeans");
	public static final Item JEANS = new ItemPants("jeans");
	public static final Item BLACKJEANS = new ItemPants("blackjeans");
	public static final Item GYMPANTS = new ItemPants("gympants");
	public static final Item GORKA4PANTS = new ItemPants("gorka4pants");
	
	/*
	 * SHOES
	 */
	public static final Item BLACKSHOES = new ItemShoes("blackshoes");
	public static final Item GORKA4BOOTS = new ItemShoes("gorka4boots");
	
	
	public static final Item SUNGLASSES = new GlassesItem("sunglasses");
	
	
	public static final Item BOOMMIC = new ItemBase("boommic");
	
	//USELESS ITEMS
	public static final Item BROKENKEYBOARD = new ItemBase("brokenkeyboard");
	public static final Item CASH = new ItemBase("cash");
	public static final Item CHANGE = new ItemBase("change");
	public static final Item CRACKEDPHONE = new ItemBase("crackedphone");
	public static final Item ERASER = new ItemBase("eraser");
	public static final Item GLASSESCASE = new ItemBase("glassescase");
	public static final Item GOLDWATCH = new ItemBase("goldwatch");
	public static final Item GRAPHICSCARD = new ItemBase("graphicscard");
	public static final Item HAMMER = new ItemBase("hammer");
	public static final Item MOTHERBOARD = new ItemBase("motherboard");
	public static final Item NAILS = new ItemBase("nails");
	public static final Item PEN = new ItemBase("pen");
	public static final Item PENCIL = new ItemBase("pencil");
	public static final Item PLIERS = new ItemBase("pliers");
	public static final Item SILVERWATCH = new ItemBase("silverwatch");
	public static final Item WALLET = new ItemBase("wallet");
	
	
	public static final Item FLAG = new ItemFlag("flag");
	
	
	public static final Item VYPERFIRSTCHEMICAL = new BaseChemItem("vyper");
	
	
	/*
	 * DRINKS
	 */
	public static final Item WATERBOTTLE = new ItemWater("waterbottle", 5);
	public static final Item EMPTYWATERBOTTLE = new EmptyWaterItem("emptywaterbottle");
	
	public static final Item BEERBOTTLE = new ItemWater("beerbottle", 5);
	public static final Item EMPTYBEERBOTTLE = new EmptyWaterItem("emptybeerbottle");
	
	public static final Item SODABOTTLE = new ItemWater("sodabottle", 5);
	public static final Item EMPTYSODABOTTLE = new EmptyWaterItem("sodabottleempty");
	
	//SEEDS
	//public static final Item OPIUMPOPPYSEEDS = new BasicSeed("opiumpoppyseeds", ModBlocks.OPIUM_POPPY, Blocks.FARMLAND);
	
	public static final Item BRIEFCASE = new ItemChemicalBriefcase("briefcase");
	
	
	public static final Item DRUGSYRINGE = new ItemDrugSyringe(0, 0, false, "drugsyringe", "Combat Stimulant", 3, 100, 0, false);
	
	//COMPONENTS
	public static final Item POWDER = new ItemDrugPowder(1, 0.5F, true, "basicdrugpowder", "Basic Powder", 1, 0.2F, 100, true);
	public static final Item DRUGBOTTLE = new ItemBottle(1, 0.5F, true, "drugbottle", "Basic Liquid", 1, 0.2F, 100, true);
	public static final Item PILL = new ItemPill(0, 0, false, "basic pill", "Basic Pill", 3, 100, 0, false);
	
	//BASE COMPONENTS
	
	//public static final Item BLEACH = new ItemBaseComponent("bleach", 3000, DrugComponentPreset.SODIUM_HYPOCHLORITE);
	//public static final Item DETERGENT = new ItemBaseComponent("detergent", 3000, DrugBasePreset.BLEACH);
	//public static final Item BAKINGSODA = new ItemBaseComponent("bakingsoda", 3000, DrugBasePreset.BAKING_SODA);
	//public static final Item TALCUM = new ItemBaseComponent("talcum", 3000, DrugBasePreset.BLEACH);
	
	
	
	//BASE COMPONENTS
    
	
	public static final Item BLEACH = new ItemBaseComponent("bleach", 3, DrugComponentPreset.SODIUM_HYPOCHLORITE, 0.2F);
    public static final Item DETERGENT = new ItemBaseComponent("detergent", 2, DrugComponentPreset.BORAX, 0.14F);
    public static final Item BAKINGSODA = new ItemBaseComponent("bakingsoda", 0.2, DrugComponentPreset.SODIUM_BICARBONATE, 0.02F);
    public static final Item TALCUM = new ItemBaseComponent("talcum", 0.4, DrugComponentPreset.TALLCUM, 0.03F);
	public static final Item SUGAR = new ItemBaseComponent("sugar", 2, DrugComponentPreset.SUCROSE, 0.01F);
	public static final Item WINDOW_CLEANER = new ItemBaseComponent("windowcleaner", 1, DrugComponentPreset.AMMONIA, 0.22F);
	public static final Item NAIL_POLISH_REMOVER = new ItemBaseComponent("nailpolishremover", 0.2, DrugComponentPreset.ACETONE, 0.22F);
	public static final Item BLEACHING_POWDER = new ItemBaseComponent("bleachingpowder", 0.25, DrugComponentPreset.CALCIUM_HYPOCHLORITE, 0.16F);
	public static final Item VINEGAR = new ItemBaseComponent("vinegar", 1, DrugComponentPreset.ACETIC_ACID, 0.14F);
	public static final Item SPRAY_PAINT = new ItemBaseComponent("spraypaint", 0.5, DrugComponentPreset.POTASSIUM_HYDROXIDE, 0.15F);
	public static final Item WOOD_CLEANER = new ItemBaseComponent("woodcleaner", 1, DrugComponentPreset.FORMALDEHYDE, 0.2F);
	public static final Item HEAVY_DUTY_CLEANER = new ItemBaseComponent("heavydutycleaner", 4, DrugComponentPreset.PERACETIC_ACID, 0.18F);
	public static final Item ADHD_MEDICINE = new ItemBaseComponent("adhdmedicine", 0.09, DrugComponentPreset.METHYLPHENIDATE, 0.18F);
	public static final Item NARCOLEPSY_MEDICINE = new ItemBaseComponent("narcolepsymedicine", 0.1, DrugComponentPreset.ARMODAFINITLTE, 0.21F);
	public static final Item ENERGY_Tablet = new ItemBaseComponent("energytablet", 0.08, DrugComponentPreset.MODAFINIL, 0.19F);
	public static final Item ADD_MEDICINE = new ItemBaseComponent("addmedicine", 0.09, DrugComponentPreset.DEXTRIANPHETAMINE, 0.19F);
	public static final Item STRONG_ADHD_MEDICINE = new ItemBaseComponent("strongadhdmedicine", 0.2, DrugComponentPreset.DEXMETHYLPHENIDATE, 0.2F);
	public static final Item COLD_MEDICINE = new ItemBaseComponent("coldmedicine", 0.1, DrugComponentPreset.CHLOREPHENIRAMINE, 0.17F);
	public static final Item DECONGESTANT = new ItemBaseComponent("decongestant", 0.1, DrugComponentPreset.PSEUDOEPHEDRINE_GUAIFENESIN, 0.11F);
	public static final Item ALERGY_MEDICINE = new ItemBaseComponent("alergymedicine", 0.095, DrugComponentPreset.LORATADINE, 0.09F);
	public static final Item HEADACHE_MEDICINE = new ItemBaseComponent("headachemedicine", 0.11, DrugComponentPreset.ACETAMINOPHEN, 0.1F);
	public static final Item BOOZE = new ItemBaseComponent("booze", 0.5, DrugComponentPreset.ETHANOL, 0.8F);
	public static final Item NAIL_POLISH = new ItemBaseComponent("nailpolish", 0.25, DrugComponentPreset.PROPANOL, 0.7F);
	public static final Item RUBBING_ALHOHOL = new ItemBaseComponent("rubbingalcohol", 0.7, DrugComponentPreset.ISOPROPYL, 0.9F);
	public static final Item WOOD_VARNISH = new ItemBaseComponent("woodvarnish", 0.9, DrugComponentPreset.CYCLOHEXANOL, 0.10F);
	public static final Item ANTIFREEZE = new ItemBaseComponent("antifreeze", 1, DrugComponentPreset.METHYL_PROPANOL, 0.7F);
	public static final Item WINDSHIELD_WASHER_FLUID = new ItemBaseComponent("windshieldwasherfluid", 0.9, DrugComponentPreset.METHANOL, 0.10F);
	public static final Item SHAMPOO = new ItemBaseComponent("shampoo", 0.75, DrugComponentPreset.GLYCEROL, 0.11F);
	public static final Item HAIR_SPRAY = new ItemBaseComponent("hairspray", 0.5, DrugComponentPreset.BUTANOL, 0.06F);
	public static final Item DOG_SHAMPOO = new ItemBaseComponent("dogshampoo", 0.4, DrugComponentPreset.VOLEMITOL, 0.09F);
	public static final Item ANTI_DEPRESSANTS = new ItemBaseComponent("antidepressants", 0.12, DrugComponentPreset.ALPRAZOLAM, 0.1F);
	public static final Item SEIZURE_MEDICINE = new ItemBaseComponent("seizuremedicine", 0.1, DrugComponentPreset.CLONAZEPAM, 0.09F);
	public static final Item ANXIETY_MEDICINE = new ItemBaseComponent("anxietymedicine", 0.11, DrugComponentPreset.CHLORDIAZEPOXIDE, 0.11F);
	public static final Item SLEEP_AID = new ItemBaseComponent("sleepaid", 0.425, DrugComponentPreset.DIAZEPAM, 0.12F);
	public static final Item STRONG_SLEEP_AID = new ItemBaseComponent("strongsleepaid", 0.18, DrugComponentPreset.LORAZEPAM, 0.10F);
	public static final Item INSOMNIA_MEDICINE = new ItemBaseComponent("insomniamedicine", 0.15, DrugComponentPreset.TEMAZEPAM, 0.09F);
	public static final Item WEAK_SLEEP_AID = new ItemBaseComponent("weaksleepaid", 0.7, DrugComponentPreset.TRIAZOLAM, 0.2F);
	public static final Item STRONG_MAGIC_MUSHROOM = new ItemBaseComponent("strongmagicmushroom", 0.2, DrugComponentPreset.PSILOCYBIN, 0.21F);
	public static final Item PEYOTE = new ItemBaseComponent("peyote", 0.14, DrugComponentPreset.MESCALINE, 0.23F);
	public static final Item AYAHUASCA = new ItemBaseComponent("ayahuasca", 0.11, DrugComponentPreset.AYAHUASCA, 0.17F);
	public static final Item SELTZER = new ItemBaseComponent("seltzer", 0.9, DrugComponentPreset.DEXTROMETHORPHAN, 0.23F);
	public static final Item DIZZINESS_MEDICINE = new ItemBaseComponent("dizzinessmedicine", 0.1, DrugComponentPreset.SALVIA_DIVINORUM, 0.2F);
	public static final Item FROG_VENOM = new ItemBaseComponent("frogvenom", 0.18, DrugComponentPreset.BUFOTENIN, 0.19F);
	public static final Item POTENT_COCA = new ItemBaseComponent("strongcoca", 0.25, DrugComponentPreset.COCA_PYRROLIDINE, 0.2F);
	public static final Item COCA = new ItemBaseComponent("coca", 0.15, DrugComponentPreset.COCA_TROPANE, 0.15F);
	public static final Item POPPY_POD = new ItemBaseComponent("poppypod", 0.25, DrugComponentPreset.POPPY_GUM, 0.1F);
	public static final Item INDUSTRIAL_SOLVENT = new ItemBaseComponent("industrialsolvent", 5, DrugComponentPreset.SULFURIC_ACID, 0.27F);
	public static final Item HIGH_GRADE_GASOLINE = new ItemBaseComponent("highgradegasoline", 2, DrugComponentPreset.OCTANE, 0.24F);
	public static final Item HEART_MEDICINE = new ItemBaseComponent("heartmedicine", 0.5, DrugComponentPreset.NITROGLYCERIN, 0.3F);
	public static final Item LANTERN_FUEL = new ItemBaseComponent("lanternfuel", 1, DrugComponentPreset.KEROSENE, 0.22F);
	public static final Item LIGHTER_FLUID = new ItemBaseComponent("lighterfluid", 0.5, DrugComponentPreset.BUTANE, 0.21F);
	
	
	//PACKING MATERIALS
	public static final Item PRINTER_PAPER = new ItemPackingMaterial("printerpaper", 1);
	public static final Item SARAN_WRAP = new ItemPackingMaterial("saranwrap", 2);
	public static final Item WAX_PAPER = new ItemPackingMaterial("waxpaper", 3);
	
	
	//PACKING RESULTS
	public static final Item DRUGBRICKNOWRAP = new ItemDrugBrick(1, 0.5F, true, "drugbricknowrap", "Basic Drug Brick", 1, 0.2F, 100, true, 0);
	public static final Item DRUGBRICKSARAN = new ItemDrugBrick(1, 0.5F, true, "drugbricksaran", "Saran Basic Drug Brick", 1, 0.2F, 100, true, 1);
	public static final Item DRUGBRICKPAPER = new ItemDrugBrick(1, 0.5F, true, "drugbrickpaper", "Paper Basic Drug Brick", 1, 0.2F, 100, true, 2);
	public static final Item DRUGBRICKWAX = new ItemDrugBrick(1, 0.5F, true, "drugbrickwax", "Wax Drug Brick", 1, 0.2F, 100, true, 3);
	
}
