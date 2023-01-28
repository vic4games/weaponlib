package com.jimholden.conomy.economy.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.util.CharsTrie.Iterator;
import com.jimholden.conomy.clans.ClanDatabase;
import com.jimholden.conomy.clans.threads.ConcurrentExecutionManager;
import com.jimholden.conomy.economy.banking.Bank;
import com.jimholden.conomy.economy.banking.BankRegistry;
import com.jimholden.conomy.economy.banking.FinancialPlayer;
import com.jimholden.conomy.economy.record.Transaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class EconomyDatabase {

	public static EconomyDatabase instance = null;
	private static MinecraftServer handler = FMLCommonHandler.instance().getMinecraftServerInstance();
	public static final File economyDataLocation = new File(
			handler.getServer().getWorld(0).getSaveHandler().getWorldDirectory(), "/economy");
	
	
	private static HashMap<UUID, FinancialPlayer> player = new HashMap<>();
	
	private static BankRegistry bankRegistrar = new BankRegistry();
	
	public static final long TOTAL_WEALTH = 1000000;
	
	public static double SYSTEM_MONEY = 0;
	public static double PLAYER_MONEY = 0;

	
	
	public static EconomyDatabase getInstance() {
		if (instance == null) {
			try {
				load();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return instance;
	}
	
	public static BankRegistry getBankRegistry() {
		getInstance();
		return bankRegistrar;
	}
	
	public static void newFinancialPlayer(FinancialPlayer p) {
		if(player == null) {
			player = new HashMap<>();
		}
		if(p == null) return;
		if(hasAccount(p.getUniqueIdentifier())) return;
		player.put(p.getUniqueIdentifier(), p);
		savePlayerIndex();
		
	}
	
	public static FinancialPlayer getFinancialPlayer(String bid) {
		if(player.isEmpty()) return null;
		for(Entry<UUID, FinancialPlayer> set : player.entrySet()) {
			if(set.getValue().getBankID().equals(bid)) return set.getValue();
		}
		return null;
	}
	
	public static FinancialPlayer getFinancialPlayer(UUID uuid) {
		if(!hasAccount(uuid)) return null;
		return player.get(uuid);
	}
	
	public static Bank getBankFromID(int id) {
		return bankRegistrar.byID(id);
	}
	
	public static Bank getBank(UUID uuid) {
		if(!hasAccount(uuid)) return null;
		return getBankFromID(player.get(uuid).getBank());
	}
	
	public static boolean hasAccount(UUID uuid) {
		
		return player != null && !player.isEmpty() && player.containsKey(uuid);
	}
	
	public static Bank getBankFromBID(String bankID) {
	
		if(player == null || player.isEmpty()) return null;
		for(Entry<UUID, FinancialPlayer> fp : player.entrySet()) {
			
			if(fp.getValue().getBankID().equals(bankID)) return getBankRegistry().byID(fp.getValue().getBank());
		}
		return null;
	}
	 
	public static boolean doesBIDExist(String bid) {
		return getBankFromBID(bid) != null;
	}
	
	
	
	public static void saveBank(Bank b) {

		Gson g = new GsonBuilder().registerTypeAdapter(FinancialPlayer.class, Bank.simpleFinancialPlayerStorage).setPrettyPrinting().create();
	
		try {
			FileWriter fw = new FileWriter(new File(economyDataLocation, "/banking/" + b.getName() + ".json"));
			g.toJson(b, fw);
			fw.close();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	public static void savePlayerIndex() {
		Gson g = new GsonBuilder().registerTypeAdapter(Transaction.class, Transaction.transactionSerializer).registerTypeAdapter(FinancialPlayer.class, Bank.simpleFPDeserializer).setPrettyPrinting().create();
		try {
			File playerIndex = new File(economyDataLocation, "playerfinance.json");
			FileWriter fw = new FileWriter(playerIndex);
			g.toJson(player, fw);
			fw.close();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveDistributionRecord() {
		
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		try {
			File playerIndex = new File(economyDataLocation, "distribution.json");
			FileWriter fw = new FileWriter(playerIndex);
			JsonObject obj = new JsonObject();
			obj.add("totalWealth", new JsonPrimitive(TOTAL_WEALTH));
			obj.add("playerWealth", new JsonPrimitive(PLAYER_MONEY));
			obj.add("systemMoney", new JsonPrimitive(SYSTEM_MONEY));
			g.toJson(obj, fw);
			fw.close();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void load() throws Exception {
		//saveDistributionRecord();
		instance = new EconomyDatabase();
		if (!economyDataLocation.exists()) {

			economyDataLocation.mkdirs();
		}

		File records = new File(economyDataLocation, "/records");
		File banking = new File(economyDataLocation, "/banking");
		if (!records.exists()) {
			records.mkdirs();
		}
		if (!banking.exists()) {
			banking.mkdirs();
		}
		
		File playerIndex = new File(economyDataLocation, "playerfinance.json");
		if(!playerIndex.exists()) {
			try {
				playerIndex.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(playerIndex));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Type listType = new TypeToken<HashMap<UUID, FinancialPlayer>>(){}.getType();
			player = (new GsonBuilder().registerTypeAdapter(Transaction.class, Transaction.transactionDeserializer).setPrettyPrinting().create()).fromJson(br, listType);
		}
		
		File[] bankFiles = banking.listFiles();
		for(File f : bankFiles) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(f));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			Gson parser = new GsonBuilder().registerTypeAdapter(FinancialPlayer.class, Bank.simpleFPDeserializer).setPrettyPrinting().create();
			
			Bank bank = parser.fromJson(br, Bank.class);
			getBankRegistry().registerBank(bank);
			//bankRegistrar.swap(bank, bank.getBankID());
		}
		// if there are no banks to load, just insert the default ones.
		if(getBankRegistry().getBanks().isEmpty()) getBankRegistry().getDefaultBanks();
		
		// load the distribution
		File distributionFile = new File(economyDataLocation + "/distribution.json");
		if(distributionFile.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(distributionFile));
			JsonObject distributionJSON = new Gson().fromJson(br, JsonObject.class);
		
			PLAYER_MONEY = distributionJSON.get("playerWealth").getAsDouble();
			SYSTEM_MONEY = distributionJSON.get("systemMoney").getAsDouble();
		} else {
			SYSTEM_MONEY = TOTAL_WEALTH;
		}
		
		
		
		

	}
	
	private static ArrayList<Transaction> transactionRecord = new ArrayList<Transaction>();
	
	/*
	 * Record keeping
	 */
	
	public static void newTransaction(Transaction t) {
		
		// add to the record
		transactionRecord.add(t);
		
		System.out.println("pusheed!");
		
		t.getSender().pushTransaction(t);
		t.getReceiver().pushTransaction(t);
		
		// process transaction
		
		
		if(!t.complete) {
			t.getSender().takeMoney(t.getAmount());
			t.getReceiver().addMoney(t.getAmount());
		}
		
		
		if(!t.getSender().isPlayer() && t.getReceiver().isPlayer()) {
			PLAYER_MONEY += t.getAmount();
			SYSTEM_MONEY -= t.getAmount();
		} else if(t.getSender().isPlayer() && !t.getReceiver().isPlayer()) {
			PLAYER_MONEY -= t.getAmount();
			SYSTEM_MONEY += t.getAmount();
		}
	}
	
	public static void saveEconomyDatabase() {
		Runnable r = () -> {
			savePlayerIndex();
			saveTransaction();
			saveDistributionRecord();
			for(Bank b : getBankRegistry().getBanks()) {
				saveBank(b);
			}
 			
			
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				System.err.println("Difficulty shutting down economy save thread!");
				e.printStackTrace();
			}
		};
		
		Thread t = new Thread(r);
		t.start();
		
	}
	

	
	
	@SuppressWarnings("unchecked")
	public static void saveTransaction() {
		File records = new File(economyDataLocation, "/records");
		
		LocalDate date = LocalDate.now();
		
		
		Calendar cal = Calendar.getInstance();
		String month = new SimpleDateFormat("MMMM").format(cal.getTime());

		File timePath = new File(records, "/" + date.getYear() + "/" + month);
		if(!timePath.exists()) {
			timePath.mkdirs();
		}
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
		String today = dtf.format(date) + ".json";
		
		
		File todayFile = new File(timePath, today);
		if(!todayFile.exists()) {
			try {
				todayFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(todayFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Transaction> transaction = new ArrayList<>();
		
		
		Gson masterBuilder = new GsonBuilder().registerTypeAdapter(Transaction.class, Transaction.transactionDeserializer).registerTypeAdapter(Transaction.class, Transaction.transactionSerializer).setPrettyPrinting().create();
		
		transaction = masterBuilder.fromJson(br, ArrayList.class);
		if(transaction == null) transaction = new ArrayList<>();
		transaction.addAll(transactionRecord);
		transactionRecord.clear();
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(todayFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		masterBuilder.toJson(transaction, fw);
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
	}

}
