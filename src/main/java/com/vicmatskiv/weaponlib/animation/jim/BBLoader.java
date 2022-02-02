package com.vicmatskiv.weaponlib.animation.jim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vicmatskiv.weaponlib.animation.Transform;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData.BlockbenchTransition;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

public class BBLoader {
	
	public static String directory  = "mw" + ":" + "animations/";
	public static Gson gson = (new GsonBuilder()).create();
	public static String version = "1.8.0";
	
	
	
	public static double HANDDIVISOR = 17;
	public static double GENDIVISOR = 5;
	
	
	public static Transform test = new Transform()
			.withPosition(-2, 3, -2)
			.withRotation(0, 0, -0)
			.withScale(3.0, 3.0, 3.0)
			.withRotationPoint(-0.1, 1.0, 0.0);
	
	
	private static String animationSuffix = ".animation.json";
	
	// Stores actual animation files
	private static HashMap<String, AnimationSet> actualAnimations = new HashMap<>();

	
	
	
	public static JsonObject extractPath(JsonObject obj, String...path) {
		JsonObject current = obj;
		for(String s : path) {
			current = current.get(s).getAsJsonObject();
		}
		return current;
		
	}
	
	public static AnimationData getAnimation(String animation, String subName, String bone) {
		
		
		
		
		// If we already have loaded and baked this set of animations,
		// just look it up and return it. This is much faster than trying
		// to reload the animation each time.
		if(actualAnimations.containsKey(animation) && actualAnimations.get(animation) != null) {
			
			AnimationSet set = actualAnimations.get(animation);
			SingleAnimation single = set.getSingleAnimation(subName);
			if(single == null) {
				return null;
			}
			return single.getBone(bone);
		
		} else {
			
			
			
			
			
			AnimationSet set = loadAnimationFile(animation + animationSuffix);
			
			if(set == null) {
				System.err.printf("Could not load animation set for animation name {}", animation);
				return null;
			} else {
				actualAnimations.put(animation, set);
				return getAnimation(animation, subName, bone);
			}
			
			
			
			
		}
		
		
	}
	
	public static AnimationSet loadAnimationFile(String fileName) {
		
		// Create an animation set
		AnimationSet animationSet = new AnimationSet();
		
		// Initialize our buffered reader object
		BufferedReader br = null;
		try {
			ResourceLocation loc = new ResourceLocation(directory + fileName);
			br = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream()));
		} catch(Exception e) {
			System.err.printf("Failed to create reader for file: {}", fileName);
			return null;
		}
		
		// Create a master JSON object
		JsonObject masterJSON = gson.fromJson(br, JsonObject.class);
		
		// Do a basic check to make sure this is valid: has a version key, and the version key
		// lines up. Alert the user if it's a different version so the developer can make adjustments.
		if(!masterJSON.has("format_version")) {
			System.err.printf("Could not locate \"format_version\" key, cannot read file {} ", fileName); 
			return null;
		} else if(!masterJSON.get("format_version").getAsString().equals(version)){
			System.err.printf("Warning, this file is running version {}, and this version of VMW is looking for {}", masterJSON.get("format_version").getAsString(), version);	
		}
		
		
		// Extract animations
		JsonObject animationsJSON = masterJSON.get("animations").getAsJsonObject();
		for(Entry<String, JsonElement> entry : animationsJSON.entrySet()) {
			
			String animationHeader = entry.getKey().split("\\.")[2];
			
		
			
			SingleAnimation anim = new SingleAnimation(animationHeader);
			
			// load all the bone data into the single animation
			JsonObject animJSON = animationsJSON.get(entry.getKey()).getAsJsonObject().get("bones").getAsJsonObject();
			for(Entry<String, JsonElement> subEntry : animJSON.entrySet()) {
				
				JsonObject boneJSON = animJSON.get(subEntry.getKey()).getAsJsonObject();
				anim.addBoneData(subEntry.getKey(), boneJSON);
				
			//	anim.addBoneData(subEntry.getKey(), animJSON.get(subEntry.getKey()).getAsJsonObject());
			}
			
			// Bake the data
			anim.bake();
		
			
			// Add to set
			animationSet.addSingleAnimation(anim);
			
			
			
			
		}
		
	
		
		
		
		
		return animationSet;
		
	}
	
	
	public static AnimationData loadAnimationData(String fileName, String animationName, String realBone) {
		
		ResourceLocation loc = new ResourceLocation(directory + fileName);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonObject obj = gson.fromJson(br, JsonObject.class);
		JsonObject anims = obj.get("animations").getAsJsonObject();
		
		//String animationName = "animation.HKgrip.reload";
		String boneName = "bones";
		
		AnimationData dat = new AnimationData(extractPath(anims, animationName, "bones", realBone));
	
		return dat;
		//bbT.showDebugCode();
		//System.out.println(anims.get(animationName).getAsJsonObject().get(boneName).getAsJsonObject());
		
		//System.out.println(obj.get(animationName).getAsJsonObject().get(boneName));
		
		//System.out.println(anims);
		//System.out.println(obj.get("animations").getAsJsonArray());
	}

	public static String getDirectory() {
		return directory;
	}

	public static void setDirectory(String directory) {
		BBLoader.directory = directory;
	}

	public static Gson getGson() {
		return gson;
	}

	public static void setGson(Gson gson) {
		BBLoader.gson = gson;
	}

	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		BBLoader.version = version;
	}

	public static String getAnimationSuffix() {
		return animationSuffix;
	}

	public static void setAnimationSuffix(String animationSuffix) {
		BBLoader.animationSuffix = animationSuffix;
	}

	public static HashMap<String, AnimationSet> getActualAnimations() {
		return actualAnimations;
	}

	public static void setActualAnimations(HashMap<String, AnimationSet> actualAnimations) {
		BBLoader.actualAnimations = actualAnimations;
	}

}