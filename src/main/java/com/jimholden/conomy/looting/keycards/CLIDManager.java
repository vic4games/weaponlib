package com.jimholden.conomy.looting.keycards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.jimholden.conomy.clans.Clans;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Conomy Looting Identification Manager
 * 
 * @author Jim Holden, 2021
 *
 */
public class CLIDManager {
	
	private static MinecraftServer handler = FMLCommonHandler.instance().getMinecraftServerInstance();
	public static final File clidLocation = new File(handler.getServer().getWorld(0).getSaveHandler().getWorldDirectory(), "CLID/");
    public static boolean isLoaded = false;
	
	
	public static final short TABLE_SIZE = 27;
	
	public static HashMap<Integer, ItemStackHandler> lootTables = new HashMap<>();
	
	
	public static ItemStackHandler getHandlerFromCLID(int clid) {
		if(!isLoaded) load();
		
		if(!lootTables.containsKey(clid)) {
			lootTables.put(clid, new ItemStackHandler(TABLE_SIZE));
			updateTableFile(clid);
		}
		
		return lootTables.get(clid);
	}
	
	public static void save() {
		if(!isLoaded) load();
		for(Entry<Integer, ItemStackHandler> i : lootTables.entrySet()) {
			updateTableFile(i.getKey());
		}
	}
	
	public static void updateTableFile(int clid) {
		File lootFile = new File(clidLocation, clid+".cl");
		
		if(!lootFile.exists()) {
			try {
				lootFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			CompressedStreamTools.safeWrite(lootTables.get(clid).serializeNBT(), lootFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		
		
		
		
		if(!clidLocation.exists())
			clidLocation.mkdirs();
		for(File file: clidLocation.listFiles()) {
			
            try {
                
            	int id = Integer.parseInt(file.getName().split("\\.")[0]);
            
            	
            	NBTTagCompound tag = CompressedStreamTools.read(file);
            	ItemStackHandler handle = new ItemStackHandler(TABLE_SIZE);
            	handle.deserializeNBT(tag);
            	
            	lootTables.put(id, handle);
            	
            	
            	
            	
            	
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		isLoaded = true;
	}

}
