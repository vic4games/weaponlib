package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.math.Vec3d;

public class RecoilParam {
	
	private double weaponPower, muzzleClimbDivisor, stockLength, powerRecoveryNormalRate, powerRecoveryStockRate, weaponRotationX, weaponRotationY;
	
	
	public RecoilParam() {
		this.weaponPower = 50;
		this.muzzleClimbDivisor = 25;
		this.stockLength = 50;
		this.powerRecoveryNormalRate = 0.7;
		this.powerRecoveryStockRate = 0.8;
		this.weaponRotationX = 0;
		this.weaponRotationY = 0;
	}
	
	public RecoilParam(double weaponPower, double muzzleClimbDivisor, double stockLength, double prnr, double prnsr, double weaponRotX, double weaponRotY) {
		this.weaponPower = weaponPower;
		this.muzzleClimbDivisor = muzzleClimbDivisor;
		this.stockLength = stockLength;
		this.powerRecoveryNormalRate = prnr;
		this.powerRecoveryStockRate = prnsr;
		this.weaponRotationX = weaponRotX;
		this.weaponRotationY = weaponRotY;
	}
	
	public double getWeaponPower() {
		return weaponPower;
	}

	public void setWeaponPower(double weaponPower) {
		this.weaponPower = weaponPower;
	}

	public double getMuzzleClimbDivisor() {
		return muzzleClimbDivisor;
	}

	public void setMuzzleClimbDivisor(double muzzleClimbDivisor) {
		this.muzzleClimbDivisor = muzzleClimbDivisor;
	}

	public double getStockLength() {
		return stockLength;
	}

	public void setStockLength(double stockLength) {
		this.stockLength = stockLength;
	}

	public double getPowerRecoveryNormalRate() {
		return powerRecoveryNormalRate;
	}

	public void setPowerRecoveryNormalRate(double powerRecoveryNormalRate) {
		this.powerRecoveryNormalRate = powerRecoveryNormalRate;
	}

	public double getPowerRecoveryStockRate() {
		return powerRecoveryStockRate;
	}

	public void setPowerRecoveryStockRate(double powerRecoveryStockRate) {
		this.powerRecoveryStockRate = powerRecoveryStockRate;
	}

	public double getWeaponRotationX() {
		return weaponRotationX;
	}

	public void setWeaponRotationX(double weaponRotationX) {
		this.weaponRotationX = weaponRotationX;
	}

	public double getWeaponRotationY() {
		return weaponRotationY;
	}

	public void setWeaponRotationY(double weaponRotationY) {
		this.weaponRotationY = weaponRotationY;
	}

	
	
	

}
