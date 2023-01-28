package com.jimholden.conomy.economy.record;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.jimholden.conomy.economy.FinancialDummy;
import com.jimholden.conomy.economy.IFinancial;

import net.minecraft.nbt.NBTTagCompound;

public class Transaction {
	
	public enum Type {
		GIFT,
		PAYMENT,
		TRANSFER,
		DEPOSIT,
		WITHDRAWL,
		ADMIN,
	}
	
	private IFinancial receiver;
	private IFinancial sender;
	private String date;
	private String timezone;
	private double amount;
	private Type type;
	
	public boolean complete = false;
	
	public NBTTagCompound writeToNBT() {
		NBTTagCompound transaction = new NBTTagCompound();
		transaction.setString("sender", sender.getStringIdentifier());
		transaction.setString("receiver", receiver.getStringIdentifier());
		transaction.setString("date", date);
		transaction.setDouble("amount", amount);
		transaction.setString("type", type.name());
		return transaction;
	}
	
	public static Transaction readNBT(NBTTagCompound comp) {
		FinancialDummy sender = new FinancialDummy(comp.getString("sender"));
		FinancialDummy receiver = new FinancialDummy(comp.getString("receiver"));
		double amount = comp.getDouble("amount");
		String date  = comp.getString("date");
		Transaction.Type type = Transaction.Type.valueOf(comp.getString("type"));
		Transaction t = new Transaction(sender, receiver, type, amount);
		return t;
	
	}
	
	public static JsonDeserializer<Transaction> transactionDeserializer = new JsonDeserializer<Transaction>() {

		public Transaction deserialize(JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
			
			
			JsonObject  obj = (JsonObject) json;
			FinancialDummy sender = new FinancialDummy(obj.get("sender").getAsString());
			FinancialDummy receiver = new FinancialDummy(obj.get("receiver").getAsString());
			double amount = obj.get("amount").getAsDouble();
			String date  = obj.get("date").getAsString();
			Transaction.Type type = Transaction.Type.valueOf(obj.get("type").getAsString());
			Transaction t = new Transaction(sender, receiver, type, amount);
			t.setDate(date);
			return t;
			
			
		};
		

		
	};
	
	
	public static JsonSerializer<Transaction> transactionSerializer = new JsonSerializer<Transaction>() {

		@Override
		public JsonElement serialize(Transaction src, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			
			JsonObject transact = new JsonObject();
			transact.add("sender", new JsonPrimitive(src.getSender().getStringIdentifier()));
			transact.add("receiver", new JsonPrimitive(src.getReceiver().getStringIdentifier()));
			transact.add("amount", new JsonPrimitive(src.getAmount()));
			transact.add("date", new JsonPrimitive(src.getDate()));
			transact.add("type", new JsonPrimitive(src.type.toString()));
			
			
			
			return transact;
		}
		
	};
		
	
	public Transaction(IFinancial sender, IFinancial recv, Type type, double amt) {
		ZonedDateTime tz = ZonedDateTime.now();
		this.receiver = recv;
		this.sender = sender;
		this.date = tz.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:ms"));
		this.timezone = tz.getZone().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		this.amount = amt;
		this.type = type;

	}
	
	public Transaction(IFinancial sender, IFinancial recv, Type type, double amt, boolean alreadyFactored) {
		ZonedDateTime tz = ZonedDateTime.now();
		this.receiver = recv;
		this.sender = sender;
		this.date = tz.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:ms"));
		this.timezone = tz.getZone().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		this.amount = amt;
		this.type = type;
		this.complete = alreadyFactored;
	}
	
	public Transaction(IFinancial sender,IFinancial recv, Type type, ZonedDateTime tz, double amt) {
		this.receiver = recv;
		this.sender = sender;
		this.date = tz.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:ms"));
		this.timezone = tz.getZone().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		this.amount = amt;
		this.type = type;
		
	}

	public IFinancial getReceiver() {
		return receiver;
	}

	public void setReceiver(IFinancial receiver) {
		this.receiver = receiver;
	}

	public IFinancial getSender() {
		return sender;
	}

	public void setSender(IFinancial sender) {
		this.sender = sender;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
