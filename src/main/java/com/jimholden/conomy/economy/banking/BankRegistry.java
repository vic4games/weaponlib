package com.jimholden.conomy.economy.banking;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

public class BankRegistry {
	
	private ArrayList<Bank> banks = new ArrayList<>();
	
	public BankRegistry() {
		
	}
	
	public ArrayList<Bank> getBanks() {
		return banks;
	}
	
	public void getDefaultBanks() {
		
		
		Bank turing = new Bank("Turing Financial", 614, 1, "Take your money", "Martin Shkreli", LocalDate.of(2013, 2, 23), "We love money", "Ron Tilles", 5500000);
		Bank rocico = new Bank("Roci Corporation", 383, 2, "To bring in a new class of wealth", "Naomi Nagata", LocalDate.of(2013, 2, 23), "High profile banking", "Naomi Nata", 55500000);
		Bank sunphoenix = new Bank("Phoenix Financial", 647, 3, "The lower class deserves wealth", "Jatayu Haradas", LocalDate.of(2013, 2, 23), "High profile banking", "John Xina", 55500000);
		
		
		registerBank(turing);
		registerBank(rocico);
		registerBank(sunphoenix);
	}
	
	
	
	public Bank byID(int id) {
		for(Bank b : banks) if(b.getBankID() == id) return b;
		return null;
	}
	
	public Bank getBank(int i) {
		return banks.get(i);
		
	}
	
	public void swap(Bank real, int toReplace) {
		banks.set(banks.indexOf(byID(toReplace)), real);
	}
	
	public void registerBank(Bank b) {
		for(Bank check : banks) {
			if(check.getBankID() == b.getBankID()) {
				return;
			}
		}
		banks.add(b);
	}

}
