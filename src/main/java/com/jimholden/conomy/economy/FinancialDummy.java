package com.jimholden.conomy.economy;

import com.jimholden.conomy.economy.record.Transaction;

public class FinancialDummy implements IFinancial {
	
	public String name;
	
	public FinancialDummy(String name) {
		this.name = name;
	}

	@Override
	public boolean isPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void takeMoney(double money) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getBalance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addMoney(double money) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMoney(double money) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStringIdentifier() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void pushTransaction(Transaction t) {
		// TODO Auto-generated method stub
		
	}

}
