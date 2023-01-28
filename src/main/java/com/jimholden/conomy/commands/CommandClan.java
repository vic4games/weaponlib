package com.jimholden.conomy.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.clans.ClanDatabase;
import com.jimholden.conomy.clans.ClanMemberCache;
import com.jimholden.conomy.clans.ClanNameCache;
import com.jimholden.conomy.clans.Clans;
import com.jimholden.conomy.clans.ConfirmationCodeCache;
import com.jimholden.conomy.clans.EnumRank;
import com.jimholden.conomy.clans.InvitedPlayers;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.mojang.authlib.GameProfile;

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
import net.minecraftforge.server.permission.PermissionAPI;





public class CommandClan extends CommandBase {
	
	String usage = "Usage: /clan <new> <name>";
	String tabOpt[] = {"new", "details", "invite", "accept", "kick", "leave", "promote", "demote", "disband"};

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "clan";
	}
	
	
	public void getHelp(ICommandSender sender, MinecraftServer server)
	{
		//sender.sendMessage(new TextComponentString(TextFormatting + "));
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
		if (args.length == 2) {
			//System.out.println("Herre are the args: " + args[1]);
			switch(args[0]) {
				case "details":
					return args.length == 2 ? Lists.newArrayList(ClanNameCache.getClanNames()) : Collections.emptyList();
				case "invite":
					return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				case "accept":
					//System.out.println("Accept select!");
					List<String> clanNames = Lists.newArrayList();
					for(UUID c: InvitedPlayers.getReceivedInvites(((EntityPlayerMP) sender).getUniqueID()))
						clanNames.add(Objects.requireNonNull(ClanDatabase.getClanById(c)).getName());
					return getListOfStringsMatchingLastWord(args, clanNames);
				case "kick":
					Clans playerClan = Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayerMP) sender).getUniqueID()), 0);
					return getListOfStringsMatchingLastWord(args, playerClan.getMembersReadable());
			}
		}
		//return args.length == 2 ? Lists.newArrayList(ClanNameCache.getClanNames()) : Collections.emptyList();
		/*
		if (args.length == 2) {
			switch(args[1]) {
				case "details":
					return args.length == 2 ? Lists.newArrayList(ClanNameCache.getClanNames()) : Collections.emptyList();
				case "invite":
					return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				case "accept":
					List<String> clanNames = Lists.newArrayList();
					for(UUID c: InvitedPlayers.getReceivedInvites(((EntityPlayerMP) sender).getUniqueID()))
						clanNames.add(Objects.requireNonNull(ClanDatabase.getClanById(c)).getName());
					return getListOfStringsMatchingLastWord(args, clanNames);
			}
		}
		return super.getTabCompletions(server, sender, args, targetPos);
		*/
		return null;
	}
	
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		System.out.println("shit: " + PermissionAPI.hasPermission((EntityPlayerMP) sender, "conomy.command.clans"));
		if (sender instanceof EntityPlayerMP)
            return PermissionAPI.hasPermission((EntityPlayerMP) sender, "conomy.command.clans");
		return false;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			if(args.length == 0) {
				sender.sendMessage(new TextComponentString(TextFormatting.RED + usage));
				return;
			}
			if(((EntityPlayer) sender).world.isRemote) {
				return;
			}
			switch(args[0]) {
				case "new":
					if(ClanMemberCache.hasClan(((EntityPlayer) sender).getUniqueID())) {
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are in a clan right now!"));
						return;
						
					}
					String description = "";
					for(int x = 2; x < args.length; x++)
					{
						if(!(x == args.length))
						{
							description += args[x] + " ";
						}
						else {
							description += args[x];
						}
						
					}
					
					GameProfile gameProfile = server.getPlayerProfileCache().getGameProfileForUsername(sender.getName());
					UUID leaderUUID = gameProfile.getId();
					
					sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "New Clan Created: " + TextFormatting.YELLOW + args[1]));
					Clans newClan = new Clans(args[1].toString(), leaderUUID, description);
					break;
				case "details":
					System.out.println("details selected");
					if(!ClanNameCache.isClanNameUsed(args[1])) return;
					Clans selectedClan = ClanNameCache.getClanByName(args[1]);
					GameProfile gameProfileTwo = server.getPlayerProfileCache().getProfileByUUID(selectedClan.getLeaderUUID());
					sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "----« " + TextFormatting.GRAY + selectedClan.getName() + TextFormatting.GOLD + " »----"));
					sender.sendMessage(new TextComponentString(TextFormatting.ITALIC + selectedClan.getDescription()));
					sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Leader: " + TextFormatting.DARK_GRAY + gameProfileTwo.getName()));
					double bal = selectedClan.getBalance();
					String formattedBalance = String.format("%,.2f", bal);
					sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Balance: " + TextFormatting.GREEN + "$" + formattedBalance));
					List<String> memberList = Lists.newArrayList();
					List<String> onlineMemberList = Lists.newArrayList();
					for(Map.Entry<EntityPlayerMP, EnumRank> member: selectedClan.getOnlineMembers().entrySet()) {
						onlineMemberList.add(member.getKey().getName());
					}
					for(Map.Entry<UUID, EnumRank> member: selectedClan.getMembers().entrySet()) {
						 memberList.add(server.getPlayerProfileCache().getProfileByUUID(member.getKey()).getName());
						
					}

					sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Online Members (" + TextFormatting.GRAY + selectedClan.getOnlineMemberCount() + TextFormatting.GOLD + "): " + TextFormatting.GREEN + onlineMemberList.toString().replace("[", "").replace("]", "")));
					sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Members (" + TextFormatting.GRAY + selectedClan.getMemberCount() + TextFormatting.GOLD + "): " + TextFormatting.DARK_GRAY + memberList.toString().replace("[", "").replace("]", "")));
					break;
				case "list":
					//ClanNameCache.
					//
					break;
				case "invite":
					UUID uuidSender = server.getPlayerProfileCache().getGameProfileForUsername(sender.getName()).getId();
					if(ClanMemberCache.hasClan(uuidSender)) {
						
						Clans c = Iterables.get(ClanMemberCache.getClansPlayerIsIn(uuidSender), 0);
						EnumRank rank = c.getRankOfMember(uuidSender);
						if(rank == EnumRank.FIRSTOFFICER || rank == EnumRank.LEADER)
						{
							sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "Invite se!"));
							GameProfile target = null;
							target = server.getPlayerProfileCache().getGameProfileForUsername(args[1]);
							InvitedPlayers.addInvite(target.getId(), c.getId());
							if(ArrayUtils.contains(server.getOnlinePlayerProfiles(), target)) {
								EntityPlayerMP targetEntity = server.getPlayerList().getPlayerByUUID(target.getId());
								targetEntity.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You have received an invite to the Clan " + TextFormatting.YELLOW + c.getName() + TextFormatting.DARK_GRAY + "!"));
								
							}
						}
						else
						{
							sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not of rank to invite people!"));
						}
						
						
					}
					else {
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						/*
						List<Clans> clansList = Lists.newArrayList();
						for(String name : ClanNameCache.getClanNames())
						{
							clansList.add(ClanNameCache.getClanByName(name));
						}
						for(Clans cle : clansList)
						{
							List<UUID> IDUU = Lists.newArrayList();
							for(Map.Entry<UUID, EnumRank> le : cle.getMembers().entrySet())
							{
								IDUU.add(le.getKey());
							}
							System.out.println(IDUU.toString());
						}
						
						System.out.println(uuidSender);
						System.out.println(ClanMemberCache.getClansPlayerIsIn(uuidSender));
						System.out.println("No clan :((");
						*/
						
					}
					break;
				case "accept":
					Clans acceptClan = ClanNameCache.getClanByName(args[1]);
					InvitedPlayers.removeInvite(((EntityPlayer) sender).getUniqueID(), acceptClan.getId());
					acceptClan.addMember(((EntityPlayer) sender).getUniqueID());
					sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You have joined " + TextFormatting.YELLOW + acceptClan.getName() + TextFormatting.DARK_GRAY + "!"));
					
					break;
				case "leave":
					UUID uuidSenderTwo = server.getPlayerProfileCache().getGameProfileForUsername(sender.getName()).getId();
					if(!ClanMemberCache.hasClan(uuidSenderTwo))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						return;
					
					}
					Clans c = Iterables.get(ClanMemberCache.getClansPlayerIsIn(uuidSenderTwo), 0);
					if(c.getRankOfMember(uuidSenderTwo) == EnumRank.LEADER) {
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You cannot leave, you are the leader!"));
						return;
					}
					else
					{
						c.removeMember(uuidSenderTwo);
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You have left your clan!"));
					}
					break;
				case "kick":
					UUID kickerID = server.getPlayerProfileCache().getGameProfileForUsername(sender.getName()).getId();
					Clans playerClan = Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayerMP) sender).getUniqueID()), 0);
					UUID targetID = server.getPlayerProfileCache().getGameProfileForUsername(args[1]).getId();
					if(playerClan.getRankOfMember(kickerID) != EnumRank.LEADER && playerClan.getRankOfMember(kickerID) != EnumRank.FIRSTOFFICER)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not of rank to do this!"));
						return;
					}
					if(targetID == null || Iterables.get(ClanMemberCache.getClansPlayerIsIn(targetID), 0) != playerClan)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "This player is not in your clan!"));
						return;
					}
					if(!playerClan.getRankOfMember(kickerID).isGreater(playerClan.getRankOfMember(targetID)))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not above this player in clan rank!"));
						return;
					}
					sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You have kicked " + TextFormatting.YELLOW + server.getPlayerProfileCache().getProfileByUUID(targetID).getName() + TextFormatting.DARK_GRAY + "!"));
					playerClan.removeMember(targetID);
					
					
					//if()
				case "promote":
					if(!ClanMemberCache.hasClan(((EntityPlayer) sender).getUniqueID()))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						return;
					
					}
					Clans promoterClan = Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayerMP) sender).getUniqueID()), 0);
					UUID targetMemberID = server.getPlayerProfileCache().getGameProfileForUsername(args[1]).getId();
					//boolean test = (((EntityPlayer) sender).getUniqueID() == server.getPlayerProfileCache().getGameProfileForUsername(sender.getName()).getId());
					//System.out.println("Does it equal?: " + test);
					if(Iterables.get(ClanMemberCache.getClansPlayerIsIn(targetMemberID), 0) != promoterClan) {
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "This player is not in your clan!"));
						return;
					}
					EnumRank targetRank = ClanMemberCache.getPlayerRank(targetMemberID, promoterClan);
					if(!ClanMemberCache.getPlayerRank(((EntityPlayer) sender).getUniqueID(), promoterClan).isGreater(targetRank))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not above this player in rank!"));
						return;
					}
					promoterClan.promoteMember(targetMemberID);
					
					return;
				case "demote":
					if(!ClanMemberCache.hasClan(((EntityPlayer) sender).getUniqueID()))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						return;
					
					}
					Clans demoterClan = Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayerMP) sender).getUniqueID()), 0);
					UUID targetMember2ID = server.getPlayerProfileCache().getGameProfileForUsername(args[1]).getId();
					//boolean test = (((EntityPlayer) sender).getUniqueID() == server.getPlayerProfileCache().getGameProfileForUsername(sender.getName()).getId());
					//System.out.println("Does it equal?: " + test);
					if(Iterables.get(ClanMemberCache.getClansPlayerIsIn(targetMember2ID), 0) != demoterClan) {
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "This player is not in your clan!"));
						return;
					}
					EnumRank targetRank2 = ClanMemberCache.getPlayerRank(targetMember2ID, demoterClan);
					if(!ClanMemberCache.getPlayerRank(((EntityPlayer) sender).getUniqueID(), demoterClan).isGreater(targetRank2))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not above this player in rank!"));
						return;
					}
					demoterClan.demoteMember(targetMember2ID);
					
					return;
				case "disband":
					if(!ClanMemberCache.hasClan(((EntityPlayer) sender).getUniqueID()))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						return;
					
					}
					Clans disbandClan = Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayer) sender).getUniqueID()), 0);
					UUID leaderTwoUUID = ((EntityPlayer) sender).getUniqueID();
					if(disbandClan.getRankOfMember(leaderTwoUUID) != EnumRank.LEADER) return;
					String confirmationCode = ConfirmationCodeCache.randomCode();
					ConfirmationCodeCache.addCode(leaderTwoUUID, confirmationCode);
					sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "To confirm you'd like to delete the clan " + TextFormatting.YELLOW + disbandClan.getName() + TextFormatting.DARK_GRAY + ", type " + TextFormatting.RED + " /verify " + confirmationCode));
					return;
				case "verify":
					if(!ClanMemberCache.hasClan(((EntityPlayer) sender).getUniqueID()))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						return;
					
					}
					if(ConfirmationCodeCache.checkCode(((EntityPlayer) sender).getUniqueID(), args[1]))
					{
						ConfirmationCodeCache.removeCode(((EntityPlayer) sender).getUniqueID(), args[1]);
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "Succesful deletion of your clan!"));
						Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayer) sender).getUniqueID()), 0).disband();
					} else {
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "This is not a valid confirmation code!"));
					}
				case "deposit":
					if(!ClanMemberCache.hasClan(((EntityPlayer) sender).getUniqueID()))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						return;
					
					}
					Clans clanToDeposit = Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayer) sender).getUniqueID()), 0);
					int playerBalance = ((EntityPlayer) sender).getCapability(CreditProvider.CREDIT_CAP, null).getBalance();
					double depositValue = (Double.parseDouble(args[1]));
					ICredit current = ((EntityPlayerMP) sender).getCapability(CreditProvider.CREDIT_CAP, null);
					if(sender.getEntityWorld().isRemote) return;
					if(((double) playerBalance) < depositValue)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You do not have enough funds for that!"));
						return;
					}
					clanToDeposit.addBalance(depositValue);
					//System.out.println("ammount: " + (int) (playerBalance - depositValue));
					int value = Integer.parseInt(args[1]);
					current.remove(value);
					sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You have deposited " + TextFormatting.YELLOW + " $" + value + TextFormatting.DARK_GRAY + "!"));
					Main.NETWORK.sendTo(new MessageUpdateCredits(current.getBalance()), (EntityPlayerMP) sender);
					return;
				case "withdraw":
					if(!ClanMemberCache.hasClan(((EntityPlayer) sender).getUniqueID()))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not in a clan!"));
						return;
					}
					Clans withdrawClan = Iterables.get(ClanMemberCache.getClansPlayerIsIn(((EntityPlayer) sender).getUniqueID()), 0);
					int val = Integer.parseInt(args[1]);
					if(ClanMemberCache.getPlayerRank(((EntityPlayer) sender).getUniqueID(), withdrawClan) != EnumRank.LEADER) {
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You are not of rank!"));
						return;
					}
					ICredit curr = ((EntityPlayerMP) sender).getCapability(CreditProvider.CREDIT_CAP, null);
					withdrawClan.removeBalance((double) val);
					curr.add(val);
					Main.NETWORK.sendTo(new MessageUpdateCredits(curr.getBalance()), (EntityPlayerMP) sender);
					sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "|| " + TextFormatting.DARK_GRAY + "You have withdrawn " + TextFormatting.YELLOW + " $" + val + TextFormatting.DARK_GRAY + "!"));
					
					
					
					
					
					
			}
			
		
			
		}
		
	}

}
