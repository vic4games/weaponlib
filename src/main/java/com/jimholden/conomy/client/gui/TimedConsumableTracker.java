package com.jimholden.conomy.client.gui;

import com.jimholden.conomy.items.ITimedConsumable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class TimedConsumableTracker {
	
	public static boolean isInUse = false;
	public static int maxTime = 0;
	public static int timer = 0;
	public static ITimedConsumable consumable = null;
	
	// variables
	public static EntityPlayer player;
	public static World world;
	public static EnumHand hand;
	
	public static void setup(ITimedConsumable c, EntityPlayer p, World w, EnumHand h) {
		consumable = c;
		maxTime = c.getDuration();
		isInUse = true;
		timer = 0;
		
		player = p;
		world = w;
		hand = h;
	}
	
	public static void cancel() {
		isInUse = false;
	}
	
	public static boolean isConsuming() {
		return isInUse;
	}
	
	public static void tick() {
		timer += 1;
		if(timer > maxTime) {
			isInUse = false;
			timer = 0;
			maxTime = 0;
			consumable.onComplete(world, player, hand);
			consumable = null;
		}
	}
	
	

}
