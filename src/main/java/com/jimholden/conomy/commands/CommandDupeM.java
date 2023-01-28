package com.jimholden.conomy.commands;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
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





public class CommandDupeM extends CommandBase {
	
	String usage = "dupem <player> <amount>";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "dupem";
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
			ItemStack stack = p.getHeldItemMainhand();
			System.out.println("Player is holding: " + stack);
			System.out.println("The NBT is: " + stack.getTagCompound().toString());
			ItemStack newStack = stack.copy();
			p.inventory.addItemStackToInventory(newStack);
			System.out.println("New Stack NBT: " + newStack.getTagCompound().toString());
		}
		
	}

}
