package com.jimholden.conomy.client.gui.networking;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.client.gui.engine.GUINetworkHandler;
import com.jimholden.conomy.economy.banking.Bank;
import com.jimholden.conomy.economy.banking.FinancialPlayer;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.economy.banking.Account.Type;
import com.jimholden.conomy.economy.data.EconomyDatabase;
import com.jimholden.conomy.economy.record.Transaction;
import com.jimholden.conomy.util.packets.AdvGUIClientPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GUINetworkBank extends GUINetworkHandler {
	
	public static final int GET_BANKS = 0x01B;
	public static final int VERIFY_ID = 0x02B;
	public static final int ATTEMPT_REGISTER = 0x03B;
	public static final int TRANSFER_VERIFY = 0x04B;
	public static final int SEND_TRANSFER = 0x05B;

	
	/**
	 *
	 */
	@Override
	public void runServer(NBTTagCompound nbt) {
		int op = nbt.getInteger("op");
		EntityPlayerMP eMP = (EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getEntityByID(nbt.getInteger("playerID"));
		
		
		if(nbt.getInteger("op") == GET_BANKS) {
			//EntityPlayerMP eMP = (EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getEntityByID(nbt.getInteger("playerID"));
			
			NBTTagCompound response = new NBTTagCompound();
			response.setInteger("op", GET_BANKS);
			NBTTagList list = new NBTTagList();
			for(Bank b : EconomyDatabase.getInstance().getBankRegistry().getBanks()) {
				NBTTagCompound bank = new NBTTagCompound();
				bank.setString("name", b.getName());
				bank.setString("date", b.getFoundingDate().toString());
				bank.setString("description", b.getDescription());
				bank.setString("ceo", b.getCurrentCEO());
				bank.setString("founder", b.getFounder());
				bank.setString("mission", b.getMission());
				bank.setInteger("iconID", b.getIconID());
				bank.setInteger("bankID", b.getBankID());
				bank.setDouble("caw", b.getCurrentAmassedWealth());
				list.appendTag(bank);
			}
			
			response.setTag("banks", list);

			
			NBTTagCompound playerData = new NBTTagCompound();
			FinancialPlayer financialPlayer = EconomyDatabase.getFinancialPlayer(eMP.getUniqueID());
			if(financialPlayer == null) {
				playerData.setBoolean("exists", false);
			} else {
				playerData.setBoolean("exists", true);
				financialPlayer.writeToNBT(playerData);
				
			}
			response.setTag("fp", playerData);
			
			
			Main.NETWORK.sendTo(new AdvGUIClientPacket(response), eMP);
		} else if(op == VERIFY_ID) {
			String id = nbt.getString("bankID");
			NBTTagCompound response = writePacket(VERIFY_ID);
			if(EconomyDatabase.doesBIDExist(id)) {
				response.setBoolean("taken", true);
			} else {
				response.setBoolean("taken", false);
			}
			sendPacket(eMP, response);
		} else if(op == ATTEMPT_REGISTER) {
			String id = nbt.getString("bid");
			int bankID = nbt.getInteger("bank");
		
			NBTTagCompound response = writePacket(ATTEMPT_REGISTER);
			if(EconomyDatabase.doesBIDExist(id)) {
				response.setBoolean("result", false);
			} else {
				FinancialPlayer player = new FinancialPlayer(eMP.getName(), id, eMP.getUniqueID(), bankID);
				Bank bank = EconomyDatabase.getBankFromID(bankID);
				bank.newBasicAccount(player);
				
				response.setBoolean("result", true);
			}
			sendPacket(eMP, response);
		} else if(op == TRANSFER_VERIFY) {
			
			String tBID = nbt.getString("tBID").toLowerCase();
			NBTTagCompound response = writePacket(TRANSFER_VERIFY);
			if(!EconomyDatabase.doesBIDExist(tBID)) {
				response.setBoolean("result", false);
			} else {
				response.setBoolean("result", true);
				
				NBTTagList list = new NBTTagList();
				FinancialPlayer transfer = EconomyDatabase.getFinancialPlayer(tBID);
				for(Account acc : transfer.getAccounts()) {
					list.appendTag(acc.writeNBT());
				}
				response.setTag("accounts", list);
				
			}
			sendPacket(eMP, response);
		} else if(op == SEND_TRANSFER) {
			FinancialPlayer sender = EconomyDatabase.getFinancialPlayer(nbt.getString("sender"));
			FinancialPlayer receiver = EconomyDatabase.getFinancialPlayer(nbt.getString("receiver"));
			if(sender == null || receiver == null) return;
			
			double credits = nbt.getDouble("amount");
			Account senderAccount = sender.getAccountByName(nbt.getString("senderAccount"));
			if(credits > senderAccount.getMoney()) return;
			
			Account recvAcc = receiver.getAccountByName(nbt.getString("receiverAccount"));
			System.out.println(senderAccount.getAccountName() + " | " + recvAcc.getAccountName());
			
			senderAccount.takeMoney(credits);
			receiver.getAccountByName(nbt.getString("receiverAccount")).addMoney(credits);
			
			EconomyDatabase.newTransaction(new Transaction(sender, receiver, Transaction.Type.TRANSFER, credits, true));
			
			sendPacket(eMP, writePacket(SEND_TRANSFER));
			
		}
		
		
		
		super.runServer(nbt);
	}

}
