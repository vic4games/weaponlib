package com.jimholden.conomy.capabilities;

public class CreditCurrency implements ICredit {

	private int balance = 0;
	
	@Override
	public void add(int currency) {
		// TODO Auto-generated method stub
		this.balance += currency;
		
	}

	@Override
	public void remove(int currency) {
		// TODO Auto-generated method stub
		this.balance -= currency;
		if(this.balance < 0) this.balance = 0;
		
	}

	@Override
	public int getBalance() {
		// TODO Auto-generated method stub
		return this.balance;
	}

	@Override
	public void set(int currency) {
		// TODO Auto-generated method stub
		this.balance = currency;
		if(this.balance < 0) this.balance = 0;
		
	}

}
