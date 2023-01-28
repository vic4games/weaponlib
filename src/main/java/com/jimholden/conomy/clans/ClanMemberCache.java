package com.jimholden.conomy.clans;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import io.netty.util.internal.ConcurrentSet;

public class ClanMemberCache {
	private static final Map<UUID, Set<Clans>> PLAYER_CLAN_CACHE = new ConcurrentHashMap<>();

    public static Collection<Clans> getClansPlayerIsIn(@Nullable UUID player) {
        if(player == null)
            return Collections.emptySet();
        ensurePlayerClansCached(player);
        return Collections.unmodifiableSet(PLAYER_CLAN_CACHE.get(player));
    }
    

    public static int countClansPlayerIsIn(@Nullable UUID player) {
        if(player == null)
            return 0;
        ensurePlayerClansCached(player);
        return PLAYER_CLAN_CACHE.get(player).size();
    }
    
    public static boolean hasClan(UUID player)
    {
    	if(player == null) return false;
    	ensurePlayerClansCached(player);
    	if(PLAYER_CLAN_CACHE.get(player).size() == 0) return false;
    	else return true;
    }

    static void cachePlayerClan(UUID player, Clans clan) {
        ensurePlayerClansCached(player);
        PLAYER_CLAN_CACHE.get(player).add(clan);
    }

    static void uncachePlayerClan(UUID player, Clans clan) {
        ensurePlayerClansCached(player);
        PLAYER_CLAN_CACHE.get(player).remove(clan);
    }

    private static void ensurePlayerClansCached(UUID player) {
    	Set<Clans> clansFromDb = new ConcurrentSet<>();
        clansFromDb.addAll(ClanDatabase.lookupPlayerClans(player));
        PLAYER_CLAN_CACHE.put(player, clansFromDb);
    	/*
        if(!PLAYER_CLAN_CACHE.containsKey(player)) {
            Set<Clans> clansFromDb = new ConcurrentSet<>();
            clansFromDb.addAll(ClanDatabase.lookupPlayerClans(player));
            PLAYER_CLAN_CACHE.put(player, clansFromDb);
        }
        */
    }

    public static EnumRank getPlayerRank(UUID player, Clans clan) {
        return clan.getMembers().get(player);
    }

    static void uncacheClan(Clans c) {
        for(UUID player: PLAYER_CLAN_CACHE.keySet())
            uncachePlayerClan(player, c);
    }
}
