package com.jimholden.conomy.economy.banking;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

import com.jimholden.conomy.economy.IFinancial;
import com.jimholden.conomy.economy.record.Transaction;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class FinancialPlayer implements IFinancial {
	
	private UUID uniqueIdentifier;
	private String username;
	private int bank;
	private String bankID;
	
	private ArrayList<Account> accounts = new ArrayList<>();
	
	private LinkedList<Transaction> recentTransactions = new LinkedList<>();
	
	public NBTTagCompound writeToNBT(NBTTagCompound comp) {
		comp.setUniqueId("UUID", uniqueIdentifier);
		comp.setString("username", username);
		comp.setInteger("bank", bank);
		comp.setString("BID", bankID);
		
		NBTTagList list = new NBTTagList();
		for(Account a : accounts) {
			list.appendTag(a.writeNBT());
		}
		comp.setTag("accounts", list);
		
		
		NBTTagList recents = new NBTTagList();
		for(Transaction t : recentTransactions) {
			recents.appendTag(t.writeToNBT());
		}
		comp.setTag("recents", recents);
		
		
		
		return comp;
		
		
	}
	
	public LinkedList<Transaction> getRecentTransactions() {
		return this.recentTransactions;
	}
	
	public void pushTransaction(Transaction t) {
		this.recentTransactions.push(t);
		if(recentTransactions.size() >= 20) {
			this.recentTransactions.removeLast();
		}
		
		
	}
	
	
	public static FinancialPlayer readFromNBT(NBTTagCompound comp) {
		FinancialPlayer player = new FinancialPlayer(comp.getString("username"), comp.getString("BID"), comp.getUniqueId("UUID"), comp.getInteger("bank"));
		ArrayList<Account> accounts = new ArrayList<>();
		
		System.out.println(comp);
		
		NBTTagList list = comp.getTagList("accounts", NBT.TAG_COMPOUND);
		for(int x = 0; x < list.tagCount(); ++x) {
			accounts.add(Account.readNBT(list.getCompoundTagAt(x)));
		}
		player.setAccounts(accounts);
		
		NBTTagList recents = comp.getTagList("recents", NBT.TAG_COMPOUND);
		for(int x = 0; x < recents.tagCount(); ++x) {
			player.pushTransaction(Transaction.readNBT((NBTTagCompound) recents.get(x)));
		}
		
		
		
		return player;
	}
	 
	public FinancialPlayer(String name, String bankID, UUID uuid, int bank) {
		this.username = name;
		this.uniqueIdentifier = uuid;
		this.bank = bank;
		this.bankID = bankID;
	}
	

	public Account getAccountByName(String name) {
		for(Account a : this.accounts) {
			if(a.getAccountName().equals(name)) return a;
		}
		return null;
	}
	

	public boolean setupNewAccount(String name, Account.Type type, double startingBalance) {
		accounts.add(new Account(name, type, startingBalance));
		return true;
	}

	public UUID getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(UUID uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBankID() {
		return bankID;
	}

	public void setBankID(String bankID) {
		this.bankID = bankID;
	}

	public int getBank() {
		return bank;
	}

	public void setBank(int bank) {
		this.bank = bank;
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(ArrayList<Account> accounts) {
		this.accounts = accounts;
	}



	@Override
	public boolean isPlayer() {
		return true;
	}



	@Override
	public void takeMoney(double money) {
		this.accounts.get(0).takeMoney(money);
	}



	@Override
	public double getBalance() {
		return this.accounts.get(0).getMoney();
	}



	@Override
	public void addMoney(double money) {
		this.accounts.get(0).addMoney(money);
	}



	@Override
	public void setMoney(double money) {
		this.accounts.get(0).setMoney(money);
		
	}



	@Override
	public String getStringIdentifier() {
		return getBankID();
	}
	

}
