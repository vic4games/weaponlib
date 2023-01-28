package com.jimholden.conomy.economy;


import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.economy.banking.Bank;
import com.jimholden.conomy.economy.banking.FinancialPlayer;
import com.jimholden.conomy.util.packets.economy.FinancialServerPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ClientDataManager {
	
	public static ArrayList<Bank> clientBankInformation = new ArrayList<>();
	public static FinancialPlayer clientPlayer = null;
	
	public static boolean loaded = false;
	
	public static void clear() {
		clientBankInformation = new ArrayList<>();
		//clientPlayer = null;
	}
	
	public static void setStatus(boolean s) {
		loaded = s;
	}
	
	public static Bank getBankFromIconID(int id) {
		for(Bank b : clientBankInformation) {
			if(b.getIconID() == id) return b;
		}
		return null;
	}
	
	public static boolean isLoaded() {
		return loaded;
	}
	
	public static void loadFinancialPlayer(NBTTagCompound comp) {
		clientPlayer = FinancialPlayer.readFromNBT(comp);
		System.out.println("So, just to confirm... " + clientPlayer);
	}
	
	public static Bank byID(int id) {
		for(Bank b : clientBankInformation) if(b.getBankID() == id) return b;
		return null;
	}
	
	
	public static void getUpdate() {
		clear();
		NBTTagCompound comp = new NBTTagCompound();
		comp.setBoolean("bUpdate", false);
		System.out.println("The client is setting the value of the player identification to: " + (Minecraft.getMinecraft().player.getEntityId()));
		comp.setInteger("playerID", Minecraft.getMinecraft().player.getEntityId());
		Main.NETWORK.sendToServer(new FinancialServerPacket(Minecraft.getMinecraft().player.getEntityId(), comp));
	}
	
	public static void loadBanks(NBTTagList nbt) {
		if(!clientBankInformation.isEmpty()) return;
		for(int x = 0; x < nbt.tagCount(); ++x) {
			NBTTagCompound tag = (NBTTagCompound) nbt.get(x);
			
			
		
			
			
			Bank bank = new Bank(tag.getString("name"), tag.getInteger("bankID"), tag.getInteger("iconID"), tag.getString("mission"),
					tag.getString("founder"), LocalDate.parse(tag.getString("date")), tag.getString("description"), tag.getString("ceo"), tag.getDouble("caw"));
		
			
			
			clientBankInformation.add(bank);
			
		}
	}

}
