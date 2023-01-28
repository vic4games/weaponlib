package com.jimholden.conomy.capabilities;

public interface ICredit {
	
	public void add(int currency);
	public void remove(int currency);
	public void set(int currency);
	public int getBalance();
	

}
