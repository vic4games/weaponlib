package com.jimholden.conomy.economy.banking;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.jimholden.conomy.economy.IFinancial;
import com.jimholden.conomy.economy.banking.Account.Type;
import com.jimholden.conomy.economy.data.EconomyDatabase;
import com.jimholden.conomy.economy.record.Transaction;

import akka.io.TcpListener.FailedRegisterIncoming;

public class Bank implements IFinancial {
	
	private String name;
	private String mission;
	private String founder;
	private LocalDate foundingDate;
	private String description;
	private String currentCEO;
	private int iconID;
	private double currentAmassedWealth;
	private int bankID = 614;
	
	private ArrayList<FinancialPlayer> members = new ArrayList<>();
	
	public static JsonSerializer<FinancialPlayer> simpleFinancialPlayerStorage = new JsonSerializer<FinancialPlayer>() {

		@Override
		public JsonElement serialize(FinancialPlayer src, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.add("uuid", new JsonPrimitive(src.getUniqueIdentifier().toString()));
			obj.add("bid", new JsonPrimitive(src.getBankID()));
			obj.add("accounts", new JsonPrimitive(src.getAccounts().size()));
			
			return obj;
		}
		
	};
	
	public static JsonDeserializer<FinancialPlayer> simpleFPDeserializer = new JsonDeserializer<FinancialPlayer>() {

		public FinancialPlayer deserialize(JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
			
			JsonObject obj = (JsonObject) json;
			UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
			
			
			return EconomyDatabase.getFinancialPlayer(uuid);
		};
		
	
		
	};
	
	
	public Bank(String name, int id, int iconID, String mission, String founder, LocalDate fd, String desc, String ceo, double caw) {
		this.name = name;
		this.bankID = id;
		this.mission = mission;
		this.iconID = iconID;
		this.founder = founder;
		this.foundingDate = fd;
		this.description = desc;
		this.currentCEO = ceo;
		this.currentAmassedWealth = caw;
	}
	
	public void addFinancialPlayer(FinancialPlayer fp) {
		this.members.add(fp);
	}
	
	public void newBasicAccount(String name, String bankID, UUID uuid) {
		newBasicAccount(new FinancialPlayer(name, bankID, uuid, this.bankID));
	}
	
	public void newBasicAccount(FinancialPlayer fp) {
		fp.setupNewAccount("Chequeing", Type.CHEQUEING, 500);
		fp.setupNewAccount("Saving", Type.SAVING, 300);
		
		EconomyDatabase.newTransaction(new Transaction(this, fp, Transaction.Type.DEPOSIT, 500, true));
		EconomyDatabase.newTransaction(new Transaction(this, fp,  Transaction.Type.DEPOSIT, 300, true));
		
		EconomyDatabase.newFinancialPlayer(fp);
		this.members.add(fp);
		EconomyDatabase.saveBank(this);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMission() {
		return mission;
	}
	public void setMission(String mission) {
		this.mission = mission;
	}
	public String getFounder() {
		return founder;
	}
	public void setFounder(String founder) {
		this.founder = founder;
	}
	public LocalDate getFoundingDate() {
		return foundingDate;
	}
	public void setFoundingDate(LocalDate foundingDate) {
		this.foundingDate = foundingDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCurrentCEO() {
		return currentCEO;
	}
	public void setCurrentCEO(String currentCEO) {
		this.currentCEO = currentCEO;
	}
	public double getCurrentAmassedWealth() {
		return currentAmassedWealth;
	}
	public void setCurrentAmassedWealth(double currentAmassedWealth) {
		this.currentAmassedWealth = currentAmassedWealth;
	}

	public int getBankID() {
		return bankID;
	}

	public void setBankID(int bankID) {
		this.bankID = bankID;
	}

	public ArrayList<FinancialPlayer> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<FinancialPlayer> members) {
		this.members = members;
	}

	public int getIconID() {
		return iconID;
	}

	public void setIconID(int iconID) {
		this.iconID = iconID;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public void takeMoney(double money) {
		this.currentAmassedWealth -= money;
	}

	@Override
	public double getBalance() {
		return this.getCurrentAmassedWealth();
	}

	@Override
	public void addMoney(double money) {
		this.currentAmassedWealth += money;
		
	}

	@Override
	public void setMoney(double money) {
		this.currentAmassedWealth = money;
		
	}

	@Override
	public String getStringIdentifier() {
		return getName();
	}

	@Override
	public void pushTransaction(Transaction t) {
		// TODO Auto-generated method stub
		
	}
	

}
