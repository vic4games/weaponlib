package com.vicmatskiv.weaponlib.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleCommand;
import com.vicmatskiv.weaponlib.config.BalancePackManager;
import com.vicmatskiv.weaponlib.config.BalancePackManager.BalancePack;
import com.vicmatskiv.weaponlib.network.packets.BalancePackClient;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class BalancePackCommand extends CompatibleCommand {

	@Override
	protected void execCommand(ICommandSender sender, String[] args) {
	//	CommonModContext.getContext().getChannel().getChannel().sendToAll(new BalancePackClient(new BalancePack("fuck", "1", 2.5, 1, 1)));
		if(args.length == 0) {
			sender.sendMessage(new TextComponentString(getHeader() + " Arguments:"));
			sender.sendMessage(new TextComponentString("info - " + TextFormatting.ITALIC + "Provides info about currently loaded pack"));
			sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "list - " + TextFormatting.ITALIC + "Displays all balance packs in directory"));
			sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "load [file name]- " + TextFormatting.ITALIC + "Loads a balance pack"));
			sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "unload - " + TextFormatting.ITALIC + "Unloads the current balance pack"));
			sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "generate - " + TextFormatting.ITALIC + "Generates the default balance pack"));
			sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "download [link]- " + TextFormatting.ITALIC + "Downloads a balance pack online from Pastebin or any other raw text viewer"));
			sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "download pastebin [code]- " + TextFormatting.ITALIC + "Downloads a balance pack online from Pastebin"));
			
			
			return;
		}
		
		
		
		
		File directory = BalancePackManager.getDirectory();
		switch(args[0]) {
	
		case "list":
			
			sender.sendMessage(new TextComponentString(getHeader() + " Listing balance packs:"));
			int counter = 1;
			for(File file : directory.listFiles()) {
				if(file.getName().equals("index.json")) continue;
				sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "(" + (counter++) + ") " + TextFormatting.GREEN + file.getName()));
			}
			return;
		case "info":
			if(BalancePackManager.hasActiveBalancePack()) {
				BalancePack bp = BalancePackManager.getActiveBalancePack();
				sender.sendMessage(new TextComponentString(getHeader() + " Currently loaded " + TextFormatting.RED + bp.getName() + " (" + bp.getVersion() + ")"));
			} else {
				sender.sendMessage(new TextComponentString(getHeader() + " No active balance pack. Load one with " + TextFormatting.RED + "/balancepack load <filename>" + TextFormatting.WHITE + "."));
			}
			return;
		case "unload":
			if(!BalancePackManager.hasActiveBalancePack()) {
				sender.sendMessage(new TextComponentString(getHeader() + " No active balance pack. Load one with " + TextFormatting.RED + "/balancepack load <filename>" + TextFormatting.WHITE + "."));
				return;
			}
			BalancePackManager.unloadBalancePack();
			sender.sendMessage(new TextComponentString(getHeader() + " Succesfully unloaded balance pack."));
			CommonModContext.getContext().getChannel().getChannel().sendToAll(new BalancePackClient(BalancePackManager.getActiveBalancePack()));
			return;
		case "load":
			if(args.length < 2) {
				sender.sendMessage(new TextComponentString(getHeader() + " You must specify a file name!"));
				return;
			}
			
			for(File f : directory.listFiles()) {
				if(f.getName().equals(args[1])) {
					sender.sendMessage(new TextComponentString(getHeader() + " Loading balance pack " + TextFormatting.RED + f.getName()));
					BalancePackManager.loadBalancePack(sender, f.getName());
					CommonModContext.getContext().getChannel().getChannel().sendToAll(new BalancePackClient(BalancePackManager.getActiveBalancePack()));
					
					return;
				}
			}
			
			sender.sendMessage(new TextComponentString(getHeader() + " Could not find balance pack " + TextFormatting.RED + args[1]));
			return;
		case "generate":
			BalancePackManager.createDefaultBalancePack();
			sender.sendMessage(new TextComponentString(getHeader() + " Generated default balance pack with name " + TextFormatting.RED + "default_pack.json" + TextFormatting.WHITE + "."));
			return;
		case "download":
			
			if(args.length < 2) {
				sender.sendMessage(new TextComponentString(getHeader() + " You must specify a link!"));
				return;
			}
			
			String link = "";
			if(args.length == 2) {
				link = args[1];
				if(link.contains("pastebin") && !link.contains("raw")) {
					sender.sendMessage(new TextComponentString(getHeader() + " Detected pastebin link... but you forgot to link us to the raw data!"));
					String[] split = link.split("/");
					link = "https://pastebin.com/raw/" + split[split.length - 1];
					sender.sendMessage(new TextComponentString(getHeader() + " Fixed pastebin link: " + TextFormatting.RED + link));
				}
				
			} 
			
			if(args.length > 2 && args[1].equals("pastebin")) {
				link = "https://pastebin.com/raw/" + args[2];
				
			}
			
			
			
			
			sender.sendMessage(new TextComponentString(getHeader() + " Fetching balance pack from link... this could take a minute."));
			
			 try {
			   String result = IOUtils.toString(new URL(link), "UTF-8");
			   BalancePackManager.loadBalancePackFromString(sender, result);
			   CommonModContext.getContext().getChannel().getChannel().sendToAll(new BalancePackClient(BalancePackManager.getActiveBalancePack()));
				
			 } catch (MalformedURLException e) {
				sender.sendMessage(new TextComponentString(getHeader() + " Failed to open URL. Malformed URL exception."));
			} catch (IOException e) {
				sender.sendMessage(new TextComponentString(getHeader() + " Failed to process URL. IOException."));
			}
			
			return;
		} 
		
		
	}
	
	public String getHeader() {
		return TextFormatting.GOLD + "(Balance Pack Manager " + BalancePackManager.getPackManagerVersion() + ")" + TextFormatting.WHITE;
	}

	@Override
	public String getCompatibleName() {
		// TODO Auto-generated method stub
		return "balancepack";
	}

	@Override
	public String getCompatibleUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/balancepack <list, info, load, unload>";
	}

}
