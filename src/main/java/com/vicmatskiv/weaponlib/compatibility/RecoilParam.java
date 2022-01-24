package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.math.Vec3d;

public class RecoilParam {
	/**
	 * Assault Rifle = 0
	 * Pistol = 1
	 * Shotgun/Sniper = 2
	 */
	public int recoilGroup = 0;
	public double gunPower = 50;
	public double muzzleClimbMultiplier = 1;
	public Vec3d translationMultipliers = new Vec3d(1, 1, 1);
	public double recoveryModifier = 0.00;
	
	public RecoilParam() {}
	
	public RecoilParam(int group, int power, int muzzleClimbMult, Vec3d tMult, double recovModifier) {
		this.recoilGroup = group;
		this.gunPower = power;
		this.muzzleClimbMultiplier = muzzleClimbMult;
		this.translationMultipliers = tMult;
		this.recoveryModifier = recovModifier;
		
	}

	public int getRecoilGroup() {
		return recoilGroup;
	}

	public void setRecoilGroup(int recoilGroup) {
		this.recoilGroup = recoilGroup;
	}

	public double getGunPower() {
		return gunPower;
	}

	public void setGunPower(double gunPower) {
		this.gunPower = gunPower;
	}

	public double getMuzzleClimbMultiplier() {
		return muzzleClimbMultiplier;
	}

	public void setMuzzleClimbMultiplier(double muzzleClimbMultiplier) {
		this.muzzleClimbMultiplier = muzzleClimbMultiplier;
	}

	public Vec3d getTranslationMultipliers() {
		return translationMultipliers;
	}

	public void setTranslationMultipliers(Vec3d translationMultipliers) {
		this.translationMultipliers = translationMultipliers;
	}

	public double getRecoveryModifier() {
		return recoveryModifier;
	}

	public void setRecoveryModifier(double recoveryModifier) {
		this.recoveryModifier = recoveryModifier;
	}
	
	

}
