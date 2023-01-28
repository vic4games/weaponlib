package com.jimholden.conomy.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.clans.ClanDatabase;
import com.jimholden.conomy.clans.ClanMemberCache;
import com.jimholden.conomy.clans.ClanNameCache;
import com.jimholden.conomy.clans.Clans;
import com.jimholden.conomy.clans.InvitedPlayers;
import com.jimholden.conomy.clans.PlayerDataStorage;
import com.jimholden.conomy.clans.StockManager;
import com.jimholden.conomy.clans.threads.ConcurrentExecutionManager;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;





public class CommandStock extends CommandBase {
	
	String usage = "stock <type> <symbol> <quantity>";
	String tabOpt[] = {"get", "buy"};

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "stock";
	}
	

	
	public double getStockPrice(String symbol) {
		String urlToSearch = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=" + symbol;
		URL github = null;
		try {
			github = new URL(urlToSearch);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(github.openStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject obj = new Gson().fromJson(in, JsonObject.class);
		obj = obj.get("quoteResponse").getAsJsonObject();
		obj = obj.get("result").getAsJsonArray().get(0).getAsJsonObject();
		return obj.get("regularMarketPrice").getAsDouble();
	}
	
	
	public void sendStockResults(MinecraftServer server, String[] args, ICommandSender sender) {
		String urlToSearch = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=" + args[1];
		URL github = null;
		try {
			github = new URL(urlToSearch);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(github.openStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObject obj = new Gson().fromJson(in, JsonObject.class);
		
		obj = obj.get("quoteResponse").getAsJsonObject();
		obj = obj.get("result").getAsJsonArray().get(0).getAsJsonObject();
		
		String quoteType = obj.get("quoteType").getAsString();
		String companyName;
		if(quoteType.equals("CRYPTOCURRENCY"))
		{
			companyName = obj.get("shortName").getAsString();
		}
		else {
			companyName = obj.get("longName").getAsString();
		}
		
		double price = obj.get("regularMarketPrice").getAsDouble();
		
		//String companyName = obj.get("longName").getAsString();
		
		
		String exchangeName = obj.get("exchange").getAsString();
		String symbol = obj.get("symbol").getAsString();
		
		sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "|| " + TextFormatting.DARK_GRAY + companyName + " (" + TextFormatting.YELLOW + symbol + TextFormatting.DARK_GRAY + ")"));
		sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "|| " + TextFormatting.DARK_GRAY + "Exchange: " + TextFormatting.YELLOW + exchangeName));
		sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "|| " + TextFormatting.DARK_GRAY + "Price: " + TextFormatting.YELLOW + price));
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return usage;
	} 
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		
		//System.out.println(EntityList.getEntityNameList());
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, tabOpt);
		return super.getTabCompletions(server, sender, args, targetPos);
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			if(args[0].equals("get")) {
				ConcurrentExecutionManager.run(() -> {
					sendStockResults(server, args, sender);
				});
			}
			
			if(args[0].equals("buy")) {
				double price = getStockPrice(args[1]);
				int amountToBuy = Integer.parseInt(args[2]);
				double totalCost = price*amountToBuy;
				UUID senderUUID = ((EntityPlayer) sender).getUniqueID();
				StockManager.addStock(senderUUID, args[1], price, amountToBuy, true);
				sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "|| " + TextFormatting.DARK_GRAY + "Total Cost: $" + TextFormatting.YELLOW + totalCost + TextFormatting.GREEN + " [BOUGHT]"));
				
				
				
				
				
			}
			
			
		}
		
	}

}
