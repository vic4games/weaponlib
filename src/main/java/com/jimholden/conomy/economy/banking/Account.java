package com.jimholden.conomy.economy.banking;

import net.minecraft.nbt.NBTTagCompound;

public class Account {

	public enum Type {
		CHEQUEING, SAVING;
	}

	private String accountName;
	private Type accountType;
	private double money;

	public Account(String name, Type type, double money) {
		this.accountName = name;
		this.accountType = type;
		this.money = money;
	}
	
	public static Account readNBT(NBTTagCompound comp) {
		return new Account(comp.getString("accountName"), Type.valueOf(comp.getString("type")), comp.getDouble("money"));
	}
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound comp = new NBTTagCompound();
		comp.setString("accountName", accountName);
		comp.setString("type", accountType.name());
		comp.setDouble("money", money);
		return comp;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Type getAccountType() {
		return accountType;
	}

	public void setAccountType(Type accountType) {
		this.accountType = accountType;
	}
	
	public void takeMoney(double v) {
		setMoney(this.getMoney() - v);
	}
	
	public void addMoney(double v) {
		setMoney(this.getMoney() + v);
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

}
