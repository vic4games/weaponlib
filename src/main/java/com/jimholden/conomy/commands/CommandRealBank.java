package com.jimholden.conomy.commands;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.economy.banking.BankRegistry;
import com.jimholden.conomy.economy.banking.FinancialPlayer;
import com.jimholden.conomy.economy.banking.Account.Type;
import com.jimholden.conomy.economy.data.EconomyDatabase;
import com.jimholden.conomy.economy.record.Transaction;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import scala.actors.threadpool.Arrays;
import scala.tools.nsc.interpreter.ReplConfig.TapMaker;





public class CommandRealBank extends CommandBase {
	

	String tabOpt[] = {"add", "remove", "set", "balance"};
	
	static {
		
	}
	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "banking";
	}
	


	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		String builder = "";
		builder += ChatFormatting.GRAY + "/" + ChatFormatting.DARK_GRAY + getName() + ChatFormatting.GRAY + " (";
		
		for(int x = 0; x < tabOpt.length; ++x) {
			builder += ChatFormatting.GOLD + tabOpt[x] + ChatFormatting.GRAY;
			if(x+1 < tabOpt.length) {
				builder += ", ";
			}
		}
		builder += ")";
		
		return builder;
	} 
	
	public String getPrefix() {
		return ChatFormatting.GRAY + "(" + ChatFormatting.DARK_GRAY + getName().toUpperCase() + ChatFormatting.GRAY + ") ";
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		if (args.length == 2) return getListOfStringsMatchingLastWord(args, tabOpt);
		return super.getTabCompletions(server, sender, args, targetPos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			
			if(args.length == 0)
			{
				sender.sendMessage(new TextComponentString(getUsage(sender)));
				return;
			}
			EntityPlayer player = ((EntityPlayer) sender);
			
			
			switch(args[0]) {
			case "rmself":
				
				break;
			case "reload":
				try {
					EconomyDatabase.load();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sender.sendMessage(new TextComponentString(getPrefix() + "Reloading DB..."));
				
				break;
			case "bruh":
				sender.sendMessage(new TextComponentString(getPrefix() + "Confirmation..."));
				sender.sendMessage(new TextComponentString(getPrefix() + "Economy Database..." + EconomyDatabase.getBankRegistry().getBanks().size()));
				break;
			case "gui":
				
				break;
			
			case "signup":
				
				
				if(EconomyDatabase.hasAccount(player.getPersistentID())) {
					sender.sendMessage(new TextComponentString(getPrefix() + "You already have a bank account!"));
					break;
				}				

				if(EconomyDatabase.doesBIDExist(args[1])) {
					sender.sendMessage(new TextComponentString(getPrefix() + "There is already an account associated with this BID!"));
					break;
				}
				
				sender.sendMessage(new TextComponentString(getPrefix() + "Thank you, " + ChatFormatting.GOLD + args[1] + ChatFormatting.GRAY + " for signing up with " + ChatFormatting.GOLD + EconomyDatabase.getBankRegistry().getBank(0).getName() + ChatFormatting.DARK_GRAY + "!"));
				EconomyDatabase.getBankRegistry().getBank(0).newBasicAccount(player.getName(), args[1], player.getPersistentID());
				
				break;
			case "check":
				sender.sendMessage(new TextComponentString("succesfully signed up lol"));
				//EconomyDatabase.newTransaction(new Transaction("fucker", "lol", Transaction.Type.ADMIN, ZonedDateTime.now(), 25));
				FinancialPlayer fp = EconomyDatabase.getFinancialPlayer(player.getPersistentID());
				
				
				sender.sendMessage(new TextComponentString(fp.getUsername() + " | " + fp.getBankID()));
				for(Account a : fp.getAccounts()) {
					NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
					System.out.println(ChatFormatting.GRAY);
					sender.sendMessage(new TextComponentString(a.getAccountName() + " §7 " + currencyFormatter.format(a.getMoney())));
				}
				break;
			case "addCash":
				sender.sendMessage(new TextComponentString(getPrefix() + "Added funds!"));
				
				FinancialPlayer fp2 = EconomyDatabase.getFinancialPlayer(player.getPersistentID());
				for(Account a : fp2.getAccounts()) {
					if(a.getAccountName().equals(args[1])) {
						a.setMoney(a.getMoney()+Integer.parseInt(args[2]));
					}
				}
				
				
				break;
			}
			
		}
		
	}

}
