package com.jimholden.conomy.entity.rope;

import java.util.ArrayList;
import java.util.HashMap;

import com.jimholden.conomy.entity.EntityRope;
import com.jimholden.conomy.util.rope.RopeSlice;

public class RopeCache {
	
	public static HashMap<EntityRope, ArrayList<RopeSlice>> map = new HashMap<EntityRope, ArrayList<RopeSlice>>();
	public static HashMap<EntityRope, ArrayList<Float>> lenMap = new HashMap<EntityRope, ArrayList<Float>>();
	
	public static void addEntry(EntityRope rope, ArrayList<RopeSlice> sList, ArrayList<Float> len) {
		map.put(rope, sList);
		lenMap.put(rope, len);
	}
	
	public static void removeEntry(EntityRope rope) {
		map.remove(rope);
		lenMap.remove(rope);
	}
	
	public static ArrayList<RopeSlice> getSliceEntry(EntityRope rope) {
		return map.get(rope);
	}
	
	public static ArrayList<Float> getLengthEntry(EntityRope rope) {
		return lenMap.get(rope);
	}
	
	public static boolean isCached(EntityRope rope) {
		return map.containsKey(rope);
	}

}
