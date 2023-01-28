package com.jimholden.conomy.clans;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.clans.threads.ThreadedSaveHandler;
import com.jimholden.conomy.clans.threads.ThreadedSaveable;
import com.sun.jna.Library.Handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Clans implements JsonWriteable, ThreadedSaveable {
	private final ThreadedSaveHandler<Clans> saveHandler = ThreadedSaveHandler.create(this);
	private UUID leader;
	private String clanName;
	private UUID clanID;
	private File clanDataFile;
	private double balance;
	private String descString;
	private static MinecraftServer handler = FMLCommonHandler.instance().getMinecraftServerInstance();
	public static final File clanDataLocation = new File(handler.getServer().getWorld(0).getSaveHandler().getWorldDirectory(), "clans/clan");
	private final Map<UUID, EnumRank> members = new ConcurrentHashMap<>();
	
	
	public Clans(String name, UUID leader, String description) {
		this.clanName = name;
		this.leader = leader;
		this.clanID = UUID.randomUUID();
		this.members.put(leader, EnumRank.LEADER);
		this.descString = description;
		System.out.println("Clan init");
		this.clanDataFile = new File(clanDataLocation, this.clanID.toString()+".json");
		System.out.println(clanDataFile.exists());
		if(!clanDataFile.exists())
		{
			if(!clanDataFile.getParentFile().exists())
			{
				clanDataFile.getParentFile().mkdirs();
			}
			
			try {
				clanDataFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(handler.getServer().getWorld(0).getSaveHandler().getWorldDirectory());
		System.out.println(clanDataLocation);
		
		System.out.println(clanDataFile);
		this.balance = 10000;
		blockingSave();
		ClanDatabase.addClan(this.clanID, this);
		markChanged();
		
	}
	
	public boolean promoteMember(UUID player) {
		if(members.containsKey(player)) {
			EnumRank newRank = members.get(player).getRankAbove();
			members.put(player, newRank);
			blockingSave();
			return true;
		}
		return false;
	}
	
	public boolean demoteMember(UUID player) {
		if(members.containsKey(player)) {
			EnumRank newRank = members.get(player).getRankBelow();
			members.put(player, newRank);
			blockingSave();
			return true;
		}
		return false;
	}
	
	public void addBalance(double count) {
		this.balance += count;
		blockingSave();
	}
	public void removeBalance(double count) {
		this.balance -= count;
		blockingSave();
	}
	
	@Override
	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("leaderUUID", this.leader.toString());
		obj.addProperty("clanName", this.clanName);
		obj.addProperty("clanID", this.clanID.toString());
		obj.addProperty("balance", this.balance);
		obj.addProperty("desc", this.descString);
		JsonArray members = new JsonArray();
		for(Map.Entry<UUID, EnumRank> entry : this.members.entrySet()) {
            JsonObject newEntry = new JsonObject();
            newEntry.addProperty("key", entry.getKey().toString());
            newEntry.addProperty("value", entry.getValue().toString());
            members.add(newEntry);
        }
        obj.add("members", members);
		return obj;
	}
	
	public Clans(JsonObject obj) {
		this.leader = UUID.fromString(obj.get("leaderUUID").getAsString());
		this.clanName = obj.get("clanName").getAsString();
		this.clanID = UUID.fromString(obj.get("clanID").getAsString());
		this.balance = obj.get("balance").getAsDouble();
		this.descString = obj.get("desc").getAsString();
		for(JsonElement entry: obj.get("members").getAsJsonArray())
			members.put(UUID.fromString(entry.getAsJsonObject().get("key").getAsString()), EnumRank.valueOf(entry.getAsJsonObject().get("value").getAsString()));
		System.out.println("clan dat loc: " + clanDataLocation);
		System.out.println(this.clanID);
		System.out.println(this.clanID.toString()+".json");
		this.clanDataFile = new File(clanDataLocation, this.clanID.toString()+".json");
		
	}
	
	public Map<EntityPlayerMP, EnumRank> getOnlineMembers() {
        Map<EntityPlayerMP, EnumRank> online = new HashMap<>();
        for(Map.Entry<UUID, EnumRank> member: getMembers().entrySet()) {
            EntityPlayerMP player = handler.getServer().getPlayerList().getPlayerByUUID(member.getKey());
            if(player != null)
                online.put(player, member.getValue());
        }
        return Collections.unmodifiableMap(online);
    }
	
	public String getDescription() {
		return this.descString;
	}
	
	@Nullable
    public static Clans load(File file) {
        JsonObject obj = ClanFileToJSON.readJsonFile(file);
        System.out.println("Clans class!: " + obj);
        if(obj != null)
            return new Clans(obj);
        return null;
    }
	
	public File getClanDataFile() {
		return clanDataFile;
	}

	public double getBalance()
	{
		return this.balance;
	}
	
	@Override
	public void blockingSave() {
		//System.out.println("HERE'S MY NAME: " + clanDataFile);
		//System.out.println("EQUALS NULL: " + (clanDataFile == null));
		//System.out.println("LOC: " + clanDataLocation);
		writeToJson(clanDataFile);
		
	}
	
	
	@Override
	public ThreadedSaveHandler<?> getSaveHandler() {
		// TODO Auto-generated method stub
		return saveHandler;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return this.clanName;
	}
	
	public UUID getLeaderUUID() {
		return this.leader;
	}

	public UUID getId() {
		// TODO Auto-generated method stub
		return this.clanID;
	}
	
	public int getMemberCount(){
        return members.size();
    }
	
	public int getOnlineMemberCount() {
		return getOnlineMembers().size();
	}
	
	public void removeMember(UUID player) {
		if(!this.members.containsKey(player)) return;
		this.members.remove(player);
		ClanMemberCache.uncachePlayerClan(player, this);
       // ClanMemberCache.cachePlayerClan(player, this);
        //InvitedPlayers.removeInvite(player, getId());
        /*
        if(!ClansModContainer.getConfig().isAllowMultiClanMembership() && !isServer())
            for(Clan clan: ClanDatabase.getClans())
                InvitedPlayers.removeInvite(player, clan.getId());
        */
        
        blockingSave();
       
        markChanged();
    }
	
	public void addMember(UUID player) {
        this.members.put(player, EnumRank.RECRUIT);
        ClanMemberCache.cachePlayerClan(player, this);
        /*
        if(this.clanDataFile == null) {
        	File clanDataFile = new File(clanDataLocation, this.clanID.toString()+".json");
        }
        */
        //InvitedPlayers.removeInvite(player, getId());
        /*
        if(!ClansModContainer.getConfig().isAllowMultiClanMembership() && !isServer())
            for(Clan clan: ClanDatabase.getClans())
                InvitedPlayers.removeInvite(player, clan.getId());
        */
        
        blockingSave();
       
        markChanged();
    }
	@Nullable
	public EnumRank getRankOfMember(UUID player)
	{
		if(members.containsKey(player)) {
			return members.get(player);
		}
		else return null;
	}
	
	public void disband() {
		ClanDatabase.removeClan(getId());
	}

	
	public Map<UUID, EnumRank> getMembers() {
        return Collections.unmodifiableMap(members);
    }
	
	public List<String> getMembersReadable() {
		List<String> memberList = Lists.newArrayList();
		for(Map.Entry<UUID, EnumRank> member : members.entrySet())
		{
			memberList.add(handler.getServer().getPlayerProfileCache().getProfileByUUID(member.getKey()).getName());
		}
		return memberList;
	}

}
