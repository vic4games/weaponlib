package com.jimholden.conomy.medical;

import net.minecraft.entity.player.EntityPlayer;

public class PainUtility {
	
	public static double getPainFactor(double maxHealth, double currentHealth) {
		return Math.abs(currentHealth-maxHealth);
	}
	
	public static double getPainFromDamage(double painFactor, double damage) {
		return painFactor*damage;
	}
	
	public static double getDrugApplicator(double drugEfficiency, double painFactor, double mL) {
		
		return (20*drugEfficiency)/mL;
	}
	
	public static double getNetPain(EntityPlayer player) {
		IConscious conscious = player.getCapability(ConsciousProvider.CONSCIOUS, null);
		return Math.max(conscious.getPainLevel()-conscious.getApplicator(),0);
	}

}
