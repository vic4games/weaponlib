package com.jimholden.conomy.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.jimholden.conomy.clans.EnumRank;

import net.minecraft.inventory.Container;

public class InventoryVolumeTool {
	public int width;
	public int height;
	private final Map<Integer, List<Integer>> members = new ConcurrentHashMap<>();
	
	
	public InventoryVolumeTool(int width, int height, Container cont) {
		this.width = width;
		this.height = height;
		int[][] bruh;
		
		
		/*
		for(int x = 0; x < height; ++x) {
			ArrayList newList = new ArrayList();
			for(int j = 0; j < width; ++j) {
				newList.add(0);
			}
			
			this.members.put(x, newList);
		}
		*/
		
		
	}
	
	private void constructMap() {
		
	}
	
	

}
