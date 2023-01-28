package com.jimholden.conomy.clans;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class ClanDatabase {
	private static ClanDatabase instance = null;
	private static MinecraftServer handler = FMLCommonHandler.instance().getMinecraftServerInstance();
    public static final File clanDataLocation = new File(handler.getServer().getWorld(0).getSaveHandler().getWorldDirectory(), "clans/clan");
    

	
	public static ClanDatabase getInstance() {
        if(instance == null)
            load();
        return instance;
    }
	
	private final ConcurrentMap<UUID, Clans> clans;
	
	private ClanDatabase(){
        clans = new ConcurrentHashMap<>();
    }
	
	@Nullable
    public static Clans getClanById(@Nullable UUID clanId){
        if(clanId == null)
            return null;
        return getInstance().clans.get(clanId);
    }
	
	public static Collection<Clans> getClans(){
        return Collections.unmodifiableCollection(getInstance().clans.values());
    }
	
	static boolean addClan(UUID clanId, Clans clan){
		System.out.println("addClan | " + clanId.toString() + " | " + clan + " | ");
        if(!getInstance().clans.containsKey(clanId)) {
            getInstance().clans.put(clanId, clan);
            System.out.println("pass1");
            ClanNameCache.addName(clan);
            System.out.println("pass2");
            clan.markChanged();
            return true;
        }
        return false;
    }
	
	static boolean removeClan(UUID clanId) {
		Clans clan = getInstance().clans.remove(clanId);
		ClanMemberCache.uncacheClan(clan);
		ClanNameCache.removeName(clan.getName());
		clan.getClanDataFile().delete();
		return true;
	}
	
	private static void load() {
        instance = new ClanDatabase();
        if(!clanDataLocation.exists())
            clanDataLocation.mkdirs();
        for(File file: clanDataLocation.listFiles()) {
            try {
                Clans loadedClan = Clans.load(file);
                System.out.println(file);
                
                if(loadedClan != null)
                    instance.clans.put(loadedClan.getId(), loadedClan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        for(Clans clan: getClans())
            clan.save();
    }

    static List<Clans> lookupPlayerClans(UUID player){
        ArrayList<Clans> clans = Lists.newArrayList();
        for(Clans clan : getInstance().clans.values())
            if(clan.getMembers().containsKey(player))
                clans.add(clan);
        return Collections.unmodifiableList(clans);
    }

}
