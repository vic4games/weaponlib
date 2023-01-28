package com.jimholden.conomy.clans;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.jimholden.conomy.clans.threads.ConcurrentExecutionManager;

import io.netty.util.internal.ConcurrentSet;

public class InvitedPlayers {
	 //Map of Clan ID -> Set of invited players
    private static final Map<UUID, Set<UUID>> INVITED_PLAYERS_CACHE = new ConcurrentHashMap<>();

    public static Collection<UUID> getInvitedPlayers(UUID clanId) {
        INVITED_PLAYERS_CACHE.putIfAbsent(clanId, new ConcurrentSet<>());
        return INVITED_PLAYERS_CACHE.get(clanId);
    }

    public static Collection<UUID> getReceivedInvites(UUID player) {
        return Collections.unmodifiableCollection(PlayerDataStorage.getPlayerData(player).getInvites());
    }

    /**
     * Adds an invite from a clan to a player's data.
     * @return true if the player did not already have an invite pending from that clan, false otherwise.
     */
    public static boolean addInvite(UUID player, UUID clan) {
        cacheInvite(clan, player);
        return PlayerDataStorage.getPlayerData(player).addInvite(clan);
    }
    private static void cacheInvite(UUID clanId, UUID playerId) {
        INVITED_PLAYERS_CACHE.putIfAbsent(clanId, new ConcurrentSet<>());
        INVITED_PLAYERS_CACHE.get(clanId).add(playerId);
    }

    /**
     * Removes an invite from a clan from a player's data.
     * @return true if the invite was removed, or false if they didn't have a pending invite from the specified clan.
     */
    public static boolean removeInvite(UUID player, UUID clan) {
        uncacheInvite(clan, player);
        return PlayerDataStorage.getPlayerData(player).removeInvite(clan);
    }
    private static void uncacheInvite(UUID clanId, UUID playerId) {
        INVITED_PLAYERS_CACHE.putIfAbsent(clanId, new ConcurrentSet<>());
        INVITED_PLAYERS_CACHE.get(clanId).remove(playerId);
    }


    public static void loadInvitedPlayers() {
        ConcurrentExecutionManager.runKillable(() -> {
            File[] files = PlayerDataStorage.PLAYER_DATA_LOCATION.listFiles();
            if(files != null)
                for(File f: files) {
                    try {
                        UUID playerId = UUID.fromString(f.getName().replace(".json", ""));
                        for(UUID clanId: getReceivedInvites(playerId))
                            cacheInvite(clanId, playerId);
                    } catch(IllegalArgumentException ignored) {}
                }
        });
    }

}
