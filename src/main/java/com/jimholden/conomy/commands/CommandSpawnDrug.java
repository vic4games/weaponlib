package com.jimholden.conomy.commands;

import java.util.List;

import org.apache.commons.logging.Log;

import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemDrugPowder;
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





public class CommandSpawnDrug extends CommandBase {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "spawndrug";
	}
	


	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "spawndrug <drugType>";
	} 

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			ItemStack newStack = new ItemStack(ModItems.POWDER);
			((ItemDrugPowder) newStack.getItem()).setDrugType(newStack, Integer.parseInt(args[0]));
			((EntityPlayer) sender).addItemStackToInventory(newStack);
		}
		
	}

}
