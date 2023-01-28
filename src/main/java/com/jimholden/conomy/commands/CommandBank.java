package com.jimholden.conomy.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.economy.banking.BankRegistry;
import com.jimholden.conomy.economy.banking.FinancialPlayer;
import com.jimholden.conomy.economy.banking.Account.Type;
import com.jimholden.conomy.economy.data.EconomyDatabase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;

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





public class CommandBank extends CommandBase {
	
	String usage = "bankadmin <player> <add | remove | set | balance> <value>";
	String tabOpt[] = {"add", "remove", "set", "balance"};
	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bankadmin";
	}
	


	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return usage;
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
				sender.sendMessage(new TextComponentString(TextFormatting.RED + usage));
				return;
			}
			
			//EntityPlayer p = (EntityPlayer) sender;
			EntityPlayerMP p = server.getPlayerList().getPlayerByUsername(args[0]);
			
			if(args[1].equalsIgnoreCase("balance")) {
				int balance = p.getCapability(CreditProvider.CREDIT_CAP, null).getBalance();
				sender.sendMessage(new TextComponentString(TextFormatting.GRAY + p.getName() + TextFormatting.GOLD + " || " + TextFormatting.DARK_GRAY + "Balance: " + TextFormatting.YELLOW + "$" + balance));
				return;
			}
			
			
			
			if(args.length >= 3)
			{
				int value = Integer.parseInt(args[2]);
				ICredit current = p.getCapability(CreditProvider.CREDIT_CAP, null);
				if(p.world.isRemote) {
					return;
				}
				switch(args[1]) {
					case "signup":
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + p.getName() + TextFormatting.GOLD + " || " + TextFormatting.DARK_GRAY + "Added " + TextFormatting.YELLOW + "#" + value));
						
					
						
						break;
					case "add":
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + p.getName() + TextFormatting.GOLD + " || " + TextFormatting.DARK_GRAY + "Added " + TextFormatting.YELLOW + "#" + value));
						current.add(value);
						break;
					case "remove":
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + p.getName() + TextFormatting.GOLD + " || " + TextFormatting.DARK_GRAY + "Remove " + TextFormatting.YELLOW + "#" + value));
						current.remove(value);
						break;
					case "set":
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + p.getName() + TextFormatting.GOLD + " || " + TextFormatting.DARK_GRAY + "Set " + TextFormatting.YELLOW + "$" + value));
						current.set(value);
						break;
				}
				
				if(p instanceof EntityPlayerMP) {
					Main.NETWORK.sendTo(new MessageUpdateCredits(current.getBalance()), (EntityPlayerMP) p);
                }
			}
			else {
				p.sendMessage(new TextComponentString(TextFormatting.RED + usage));
				return;
			}
		}
		
	}

}
