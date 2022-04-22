package com.vicmatskiv.weaponlib.animation.jim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.print.attribute.HashAttributeSet;

import com.google.gson.JsonObject;

import net.minecraft.util.datafix.fixes.TotemItemRename;

public class SingleAnimation {
	
	private String animationName;
	private HashMap<String, AnimationData> dataMap = new HashMap<>();
	private int timestampCount = 0;
	private ArrayList<Float> timestamps;
	
	
	private float duration;
	
	public SingleAnimation(String name) {
		this.animationName = name;
	}
	
	
	public float getDuration() {
		return duration;
	}
	
	public void setDuration(float duration) {
		this.duration = duration;
	}
	
	
	public ArrayList<Float> getTimestamps() {
		return this.timestamps;
	}
	
	public boolean hasBone(String name) {
		return dataMap.containsKey(name);
	}
	
	public void addBoneData(String name, JsonObject obj) {
		dataMap.put(name, new AnimationData(obj));
	}
	
	public void bake() {
		
		// Collect all keyframes
		timestamps = new ArrayList<>();
		for(Entry<String, AnimationData> i : dataMap.entrySet()) {
			
			//Also assign the time
			i.getValue().setAppointedDuration(getDuration());
			
			ArrayList<Float> subList = i.getValue().getTimestamps();
			for(float f : subList) {
				if(!timestamps.contains(f)) timestamps.add(f);
			}
		}
		Collections.sort(timestamps);
		//System.out.println("[" + this.animationName + "] Created animation w/ " + timestamps.size() + " keyframes.");
		this.timestampCount = timestamps.size();
		
		// Bake keyframes
		for(Entry<String, AnimationData> i : dataMap.entrySet()) {
			for(float f : timestamps) {
				if(!i.getValue().getTimestamps().contains(f)) {
					i.getValue().bakeKeyframes(f);
				}
			}
		}
		
	
		// Fix timestamps
		for(Entry<String, AnimationData> i : dataMap.entrySet()) {
			AnimationData data = i.getValue();
			
			for(int t = 0; t < getTimestamps().size(); ++t) {
				if(t == 0) {
					data.bbTransition.get(getTimestamps().get(t)).setTimestamp(1.0f);
				} else {
					float trueDelta = AnimationData.PACE*(getTimestamps().get(t) - getTimestamps().get(t - 1));
					
					//System.out.println("(" + getTimestamps().get(t-1) + ") -> (" + getTimestamps().get(t) + ") " + trueDelta);
					data.bbTransition.get(getTimestamps().get(t)).setTimestamp(trueDelta);
				}
			}
			
			/*
			for(float f : timestamps) {
				if(!i.getValue().getTimestamps().contains(f)) {
					i.getValue().bakeKeyframes(f);
				}
			}*/
		}
		
		//System.out.println("Total # of transitions: " + timestamps.size());
		
		
		
	}
	
	public AnimationData getBone(String bone) {
		if(!dataMap.containsKey(bone)) {
			
			return new AnimationData(getTimestamps());
		}
 		return dataMap.get(bone);
		
	}



	public String getAnimationName() {
		return animationName;
	}



	public void setAnimationName(String animationName) {
		this.animationName = animationName;
	}



	public HashMap<String, AnimationData> getDataMap() {
		return dataMap;
	}



	public void setDataMap(HashMap<String, AnimationData> dataMap) {
		this.dataMap = dataMap;
	}

}
