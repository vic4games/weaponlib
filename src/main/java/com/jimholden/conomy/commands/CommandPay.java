package com.jimholden.conomy.commands;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.MessageTransfer;
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





public class CommandPay extends CommandBase {
	
	String usage = "pay <player> <amount>";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "pay";
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
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
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
			
			EntityPlayer p = (EntityPlayer) sender;
			
			
			if(args.length == 0)
			{
				p.sendMessage(new TextComponentString(TextFormatting.RED + usage));
				return;
			}
			
			int balance = p.getCapability(CreditProvider.CREDIT_CAP, null).getBalance();
			
			// Make sure that they are actually passing an integer.
			try {
			    Integer.parseInt(args[1]);
			} catch(Exception e) {
				p.sendMessage(new TextComponentString(TextFormatting.RED + "The value must be an integer."));
				return;
			}
			
			// Is this integer negative?
			if(Integer.parseInt(args[1]) < 0)
			{
				p.sendMessage(new TextComponentString(TextFormatting.RED + "The value cannot be negative."));
				return;
			}
			
			
			if(Integer.parseInt(args[1]) > balance) {
				p.sendMessage(new TextComponentString(TextFormatting.GOLD + "||" + TextFormatting.DARK_GRAY + " You do not have the money for this!"));
				return;
			}
			
			EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
			
			
			
			
			if(args.length == 2)
			{
				
				int value = Integer.parseInt(args[1]);
				ICredit current = p.getCapability(CreditProvider.CREDIT_CAP, null);
				ICredit tCurrent = target.getCapability(CreditProvider.CREDIT_CAP, null);
				if(p.world.isRemote) {
					return;
				}
				
				current.remove(value);
				tCurrent.add(value);
				
				target.sendMessage(new TextComponentString(TextFormatting.GOLD + "||" + TextFormatting.DARK_GRAY + "You have been transfered " + TextFormatting.YELLOW + "$" + value + TextFormatting.DARK_GRAY + " by " + sender.getName()));
				sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "||" + TextFormatting.DARK_GRAY + "You have transfered " + TextFormatting.YELLOW + "$" + value + TextFormatting.DARK_GRAY + " to " + target.getName()));
				
				System.out.println("DEBUG LOG | " + sender.getEntityWorld().isRemote + " | " + sender.getName() + " | " + current.getBalance() + " | " + target.getName() + " | " + tCurrent.getBalance());
				if(p instanceof EntityPlayerMP) {
					Main.NETWORK.sendToServer(new MessageTransfer(value, p.getEntityId(), target.getEntityId()));
					/*
					Main.NETWORK.sendTo(new MessageUpdateCredits(current.getBalance()), (EntityPlayerMP) p);
					Main.NETWORK.sendTo(new MessageUpdateCredits(tCurrent.getBalance()), target);
					*/
					System.out.println(current.getBalance() + " " + tCurrent.getBalance());
                }
			}
			else {
				p.sendMessage(new TextComponentString(TextFormatting.RED + usage));
				return;
			}
		}
		
	}

}
