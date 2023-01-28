package com.jimholden.conomy.commands;

import java.util.List;

import org.apache.commons.logging.Log;

import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;





public class CommandCurrency extends CommandBase {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "stick";
	}
	


	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "stick <add | remove | set> <value>";
	} 

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			
			EntityPlayer p = (EntityPlayer) sender;
			if(!(p.getHeldItemMainhand().getItem() instanceof OpenDimeBase)) return;
			OpenDimeBase item = (OpenDimeBase) p.getHeldItemMainhand().getItem();
			ItemStack is = p.getHeldItemMainhand();
			
			if(args.length == 0)
			{
				p.sendMessage(new TextComponentString(TextFormatting.RED + "stick <add | remove | set> <value>"));
				return;
			}
			
			if(args.length == 2)
			{
				int value = Integer.parseInt(args[1]);
				switch(args[0]) {
					case "add":
						item.setBalance(item.getBalance(is) + value, is);
						break;
					case "remove":
						item.setBalance(item.getBalance(is) - value, is);
						break;
					case "set":
						item.setBalance(value, is);
						break;
						
				}
			}
			else {
				p.sendMessage(new TextComponentString(TextFormatting.RED + "stick <add | remove | set> <value>"));
				return;
			}
		}
		
	}

}
