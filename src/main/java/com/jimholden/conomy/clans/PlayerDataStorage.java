package com.jimholden.conomy.clans;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.ibm.icu.math.BigDecimal;
import com.jimholden.conomy.clans.threads.ThreadedSaveHandler;
import com.jimholden.conomy.clans.threads.ThreadedSaveable;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayerDataStorage {
	private static final Map<UUID, PlayerStoredData> PLAYER_DATA = new ConcurrentHashMap<>();
	private static MinecraftServer handler = FMLCommonHandler.instance().getMinecraftServerInstance();
    static final File PLAYER_DATA_LOCATION = new File(handler.getServer().getWorld(0).getSaveHandler().getWorldDirectory(), "clans/player");

    public static void setShouldDisposeReferences(UUID player, boolean shouldDisposeReferences) {
        getPlayerData(player).shouldDisposeReferences = shouldDisposeReferences;
    }

    static PlayerStoredData getPlayerData(UUID player) {
        if(!PLAYER_DATA.containsKey(player))
            PLAYER_DATA.put(player, new PlayerStoredData(player));
        return PLAYER_DATA.get(player);
    }

    public static void save() {
        for(Map.Entry<UUID, PlayerStoredData> entry : PLAYER_DATA.entrySet()) {
            entry.getValue().save();
            if(entry.getValue().shouldDisposeReferences) {
                PLAYER_DATA.remove(entry.getKey());
                entry.getValue().getSaveHandler().disposeReferences();
            }
        }
    }

    static class PlayerStoredData implements ThreadedSaveable, JsonWriteable {
        private final File playerDataFile;
        private final ThreadedSaveHandler<PlayerStoredData> saveHandler = ThreadedSaveHandler.create(this);
        private boolean shouldDisposeReferences = false;

        @Nullable
        private UUID defaultClan;
        private final Set<UUID> invites = new ConcurrentSet<>(), blockedClans = new ConcurrentSet<>();
        private final Map<String, Integer> stocks = new ConcurrentHashMap<String, Integer>();
        private final Map<String, Double> initialStockPrices = new ConcurrentHashMap<String, Double>();
        private final Map<String, Boolean> stockType = new ConcurrentHashMap<String, Boolean>();
        private long lastSeen;

        private Map<String, Object> addonData = new ConcurrentHashMap<>();

        private PlayerStoredData(UUID playerId) {
            playerDataFile = new File(PLAYER_DATA_LOCATION, playerId.toString()+".json");
            if(!playerDataFile.exists())
    		{
    			if(!playerDataFile.getParentFile().exists())
    			{
    				playerDataFile.getParentFile().mkdirs();
    			}
    			
    			try {
    				playerDataFile.createNewFile();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
            if(!load()) {
                lastSeen = System.currentTimeMillis();
                //PlayerEventLogic.onFirstLogin(playerId);
            }
            if(handler.getServer().getPlayerList().getPlayerByUUID(playerId) == null) {
                shouldDisposeReferences = true;
                saveHandler.disposeReferences();
            }
            blockingSave();
        }
        
        public static Collection<UUID> uuidsFromJsonArray(JsonArray arr) {
            Collection<UUID> uuids = new HashSet<>();
            for(JsonElement elem : arr) {
                uuids.add(UUID.fromString(elem.getAsString()));
            }
            return Collections.unmodifiableCollection(uuids);
        }

        /**
         * @return true if it loaded from a file successfully, false otherwise.
         */
        private boolean load() {
            JsonObject obj = ClanFileToJSON.readJsonFile(playerDataFile);
            if(obj == null)
                return false;
            invites.addAll(uuidsFromJsonArray(obj.get("invites").getAsJsonArray()));
            
            for(JsonElement entry: obj.get("stocks").getAsJsonArray()) {
            	stocks.put(entry.getAsJsonObject().get("symbol").getAsString(), entry.getAsJsonObject().get("shares").getAsInt());
            	initialStockPrices.put(entry.getAsJsonObject().get("symbol").getAsString(), entry.getAsJsonObject().get("initialValue").getAsDouble());
            	stockType.put(entry.getAsJsonObject().get("symbol").getAsString(), entry.getAsJsonObject().get("isBuyOrder").getAsBoolean());
            }
    			
            
            //stocks.addAll(obj.get("stocks").getAsJsonArray().get(0).getAsJsonObject());
            //initialStockPrices.addAll(uuidsFromJsonArray(obj.get("initialPrices").getAsJsonArray()));
            lastSeen = obj.get("lastSeen").getAsLong();
            return true;
        }

        @Override
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            if (defaultClan != null)
                obj.addProperty("defaultClan", defaultClan.toString());
            
            
            JsonArray invite = new JsonArray();
    		for(UUID entry : invites) {
                invite.add(entry.toString());
            }
    		obj.add("invites", invite);
    		
    		JsonArray stocks = new JsonArray();
    		for(Entry<String, Integer> entry : this.stocks.entrySet()) {
                JsonObject newEntry = new JsonObject();
                newEntry.addProperty("symbol", entry.getKey().toString());
                newEntry.addProperty("shares", entry.getValue().toString());
                newEntry.addProperty("initialValue", initialStockPrices.get(entry.getKey()));
                newEntry.addProperty("isBuyOrder", stockType.get(entry.getKey()));
                stocks.add(newEntry);
            }
            obj.add("stocks", stocks);
            
            /*
            JsonArray initialStockPrices = new JsonArray();
    		for(Entry<String, Double> entry : this.initialStockPrices.entrySet()) {
                JsonObject newEntry = new JsonObject();
                newEntry.addProperty("symbol", entry.getKey().toString());
                
                initialStockPrices.add(newEntry);
            }
            obj.add("initialStockPrices", initialStockPrices);
            */
            
            
            
            obj.addProperty("lastSeen", lastSeen);
            return obj;
        }

        @Override
        public void blockingSave() {
            writeToJson(playerDataFile);
        }

        @Override
        public ThreadedSaveHandler<?> getSaveHandler() {
            return saveHandler;
        }

        void setDefaultClan(@Nullable UUID defaultClan) {
            if(!Objects.equals(this.defaultClan, defaultClan)) {
                this.defaultClan = defaultClan;
                markChanged();
            }
        }

        boolean addInvite(UUID clan) {
            boolean ret = invites.add(clan);
            System.out.println("invite sent");
            if(ret)
            	blockingSave();
                markChanged();
            return ret;
        }
        
        void removeStock(String symbol) {
        	this.stocks.remove(symbol);
        	this.stockType.remove(symbol);
        	this.initialStockPrices.remove(symbol);
        	blockingSave();
        }
        
        void addStock(String symbol, double initialPrice, int shares, boolean isBuyOrder) {
        	System.out.println("| " + initialPrice + " | " + shares);
        	this.stocks.put(symbol, shares);
        	this.initialStockPrices.put(symbol, initialPrice);
        	this.stockType.put(symbol, isBuyOrder);
        	blockingSave();
        }
        
        double getInitialPrice(String symbol) {
        	return this.initialStockPrices.get(symbol);
        }
        
        int getShares(String symbol) {
        	return this.stocks.get(symbol);
        }
        
        boolean getStockType(String symbol) {
        	return this.stockType.get(symbol);
        }

        boolean removeInvite(UUID clan) {
            boolean ret = invites.remove(clan);
            if(ret)
                markChanged();
            return ret;
        }



        Set<UUID> getInvites() {
            return invites;
        }


        @Nullable
        UUID getDefaultClan() {
            return defaultClan;
        }

        long getLastSeen() {
            return lastSeen;
        }


        void updateLastSeen() {
            lastSeen = System.currentTimeMillis();
            markChanged();
        }

        /**
         * Sets addon data for this player
         * @param key
         * The key you are giving this data. It should be unique
         * @param value
         * The data itself. This should be a primitive, string, a list or map containg only lists/maps/primitives/strings, or a JsonElement. If not, your data may not save/load properly. All lists will be loaded as ArrayLists. All maps will be loaded as HashMaps.
         */


        private boolean isUnserializable(Object value) {
            return !value.getClass().isPrimitive()
                && !value.getClass().isAssignableFrom(BigDecimal.class)
                && !value.getClass().isAssignableFrom(List.class)
                && !value.getClass().isAssignableFrom(Map.class)
                && !value.getClass().isAssignableFrom(JsonElement.class);
        }

        @Nullable
        public Object getCustomData(String key) {
            return addonData.get(key);
        }
    }

}
