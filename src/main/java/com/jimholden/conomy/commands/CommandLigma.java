package com.jimholden.conomy.commands;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.main.ModEventHandler;
import com.jimholden.conomy.medical.ConsciousProvider;
import com.jimholden.conomy.util.ImprovedNoise;
import com.jimholden.conomy.util.InventoryUtility;
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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.ItemStackHandler;





public class CommandLigma extends CommandBase {
	
	String usage = "dupem <player> <amount>";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ligma";
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
			
			InventoryUtility.putItemInPlayerInventory((EntityPlayer) sender, new ItemStack(ModItems.ADD_MEDICINE));
			System.out.println("duped em");
			if(args[0].equals("fuckingset")) {
				int slot = Integer.parseInt(args[1]);
				((EntityPlayer) sender).getCapability(InvProvider.EXTRAINV, null).setStackInSlot(slot, ((EntityPlayer) sender).getHeldItemMainhand().copy());
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "set the fucking item: " + ((EntityPlayer) sender).getHeldItemMainhand()));
			}
			if(args[0].equals("fuckingget")) {
				int slot = Integer.parseInt(args[1]);
				ItemStack stackeroo = ((EntityPlayer) sender).getCapability(InvProvider.EXTRAINV, null).getStackInSlot(slot);
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "gotten the fucking item: " + stackeroo.getDisplayName()));
			}
			if(args[0].equals("TECHNE")) {
				ItemStack stack = ((EntityPlayer) sender).getHeldItemMainhand();
				//IInventory inv = new I
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "NBT: " + stack.getTagCompound().toString()));
			}
			if(args[0].equals("TECHNEPRO")) {
				ItemStack stack = ((EntityPlayer) sender).getHeldItemMainhand();
				NBTTagList stackeroo = stack.getTagCompound().getTagList("ItemInventory", 9);
				//IInventory inv = new I
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "NBT: " + stackeroo));
			}
			if(args[0].equals("fuckyouset")) {
				int newVal = Integer.parseInt(args[1]);
				ModEventHandler.reducerValue = newVal;
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "new value is " + ModEventHandler.reducerValue));
			}
			if(args[0].equals("setwater")) {
				int newVal = Integer.parseInt(args[1]);
				((EntityPlayer) sender).getCapability(ConsciousProvider.CONSCIOUS, null).setWaterLevel(newVal);
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "new value is " + ModEventHandler.reducerValue));
			}
			if(args[0].equals("holyshit")) {
			System.out.println("fuck");
			}
		}
		
	}

}
