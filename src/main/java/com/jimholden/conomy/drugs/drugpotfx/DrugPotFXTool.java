package com.jimholden.conomy.drugs.drugpotfx;

import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class DrugPotFXTool {
	
	/*
	 speed = 1
slowness = 2
strength = 3
instant health = 4
instant damage = 5
jump boost = 6
regeneration = 7
resistance = 8
hunger = 9
weakness = 10
	 */
	
	public static Potion getPotionFXFromID(int id) {
		switch(id) {
		default:
			return MobEffects.SPEED;
		case 1:
			return MobEffects.SPEED;
		case 2:
			return MobEffects.SLOWNESS;
		case 3:
			return MobEffects.STRENGTH;
		case 4:
			return MobEffects.INSTANT_HEALTH;
		case 5:
			return MobEffects.INSTANT_DAMAGE;
		case 6:
			return MobEffects.JUMP_BOOST;
		case 7:
			return MobEffects.REGENERATION;
		case 8:
			return MobEffects.RESISTANCE;
		case 9:
			return MobEffects.HUNGER;
		case 10:
			return MobEffects.WEAKNESS;
		}
	}

}
