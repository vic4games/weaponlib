package com.jimholden.conomy.commands;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.medical.ConsciousProvider;
import com.jimholden.conomy.medical.IConscious;
import com.jimholden.conomy.util.ImprovedNoise;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;





public class CommandBlood extends CommandBase {
	
	String usage = "dupem <player> <amount>";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "blood";
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
			if(args[0].equals("get")) {
				int blood = ((EntityPlayer) sender).getCapability(ConsciousProvider.CONSCIOUS, null).getBlood();
				sender.sendMessage(new TextComponentString("ur blood be like: " + blood));
			}
			if(args[0].equals("set")) {
				int bloodSet = Integer.parseInt(args[1]);
				((EntityPlayer) sender).getCapability(ConsciousProvider.CONSCIOUS, null).setBlood(bloodSet);
				
			}
			
			if(args[0].equals("getis")) {
				boolean bloodis = ((EntityPlayer) sender).getCapability(ConsciousProvider.CONSCIOUS, null).isBleeding();
				sender.sendMessage(new TextComponentString("ur blood be like: " + bloodis));
			}
			if(args[0].equals("setis")) {
				boolean bloodSet = Boolean.parseBoolean(args[1]);
				((EntityPlayer) sender).getCapability(ConsciousProvider.CONSCIOUS, null).setIsBleed(bloodSet);
				
			}
		}
		
	}

}
