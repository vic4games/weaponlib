package com.jimholden.conomy.drugs;
/** CC-BY-NC-ND-3.0-IGO
 * 
 *
 */
public class Drug {
	public String name;
	public int rampUpTime;
	public int holdTime;
	public int releaseTime;
	public float doubleVisionIntensity;
	public float desaturation;
	public float colorIntensity;
	public float quickColRot;
	public float slowColRot;
	public int ticker = 0;
	public int stage = 0;
	public int holdTicker = 0;
	public float intensity;
	public boolean forRemoval = false;
	public float inverseAmount;
	public float deconvergeAmount;
	public float sharpenAmount;
	
	public float nVert;
	public float nHor;
	public float nSpeed;
	public float nIntensity;
	
	
	public Drug(String name, int rampUpTime, int holdTime, int releaseTime, float doubleVisionIntensity, float desaturation, float colorIntensity, float quickColRot, float slowColRot, float inverseAmount, float deconvergeAmount, float sharpenAmount, float nVert, float nHor, float nSpeed, float nIntensity) {
		this.name = name;
		this.rampUpTime = rampUpTime;
		this.holdTime = holdTime;
		this.releaseTime = releaseTime;
		this.colorIntensity = colorIntensity;
		this.doubleVisionIntensity = doubleVisionIntensity;
		this.desaturation = desaturation;
		this.quickColRot = quickColRot;
		this.slowColRot = slowColRot;
		this.inverseAmount = inverseAmount;
		this.deconvergeAmount = deconvergeAmount;
		this.sharpenAmount = sharpenAmount;
		this.nVert = nVert;
		this.nHor = nHor;
		this.nSpeed = nSpeed;
		this.nIntensity = nIntensity;
		this.ticker = 0;
	}
	
	public void tickDrug() {
		if(stage == 0) {
			this.ticker++;
			if(ticker > rampUpTime) {
				stage = 1;
			}
		}
		if(stage == 1) {
			this.holdTicker++;
			if(holdTicker > holdTime) {
				stage = 2;
			}
		}
		if(stage == 2) {
			this.ticker--;
			if(ticker <= 0) {
				this.forRemoval = true;
			}
		}
		
	}
	
	public float intensity() {
		if(stage == 0) {
			return ((float) ticker)/(float) rampUpTime;
		}
		if(stage == 1) {
			return 1.0F;
		}
		if(stage == 2) {
			return ((float) ticker)/(float) releaseTime;
		}
		return 0.0F;
	}
	

}
