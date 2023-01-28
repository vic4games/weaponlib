package com.jimholden.conomy.economy;

import com.jimholden.conomy.economy.record.Transaction;

public interface IFinancial {
	
	/**
	 * Returns true if this is a player, and not an entity such as a bank.
	 * 
	 * @return
	 */
	public boolean isPlayer();
	
	public void takeMoney(double money);
	public double getBalance();
	public void addMoney(double money);
	public void setMoney(double money);
	
	public String getStringIdentifier();
	
	public void pushTransaction(Transaction t);

}
