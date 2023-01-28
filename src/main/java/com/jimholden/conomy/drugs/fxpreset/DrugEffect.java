package com.jimholden.conomy.drugs.fxpreset;

import com.jimholden.conomy.drugs.Drug;

public class DrugEffect {
	
	//HALLUCINOGEN
	public static final Drug HALLUCINOGEN_VIBE = new Drug("vibe", 300, 3000, 300, 0.1F, 0.3F, 0.3F, 0.0F, 0.0F, 0.0F, 0.001F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public static final Drug HALLUCINOGEN_LSD = new Drug("lsd", 300, 3000, 300, 0.0001F, 0.0F, 0.03F, 0.03F, 0.03F, 0.0F, 0.0F, 0.0001F, 0.0F, 0.0F, 1.0F, 0.1F);
	
	
	//OPIOID
	public static final Drug OPIOD_A = new Drug("opiod", 300, 3000, 300, 0.0F, 0.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0001F, 0.0F, 0.0F, 0.0F, 0.0F);
	
	//(String name, int rampUpTime, int holdTime, int releaseTime, float doubleVisionIntensity, float desaturation, float colorIntensity, float quickColRot, float slowColRot, float inverseAmount, float deconvergeAmount, float sharpenAmount, float nVert, float nHor, float nSpeed, float nIntensity) {
	
	//ALC
	public static final Drug ALC = new Drug("alc", 600, 6000, 600, 0.0F, 0.0F, 0.01F, 0.0F, 0.0F, 0.0F, 0.001F, 0.05F, 0.1F, 0.1F, 0.5F, 0.1F);
	
	
	//STIMULANT
	public static final Drug BASIC_STIM = new Drug("stim", 600, 6000, 600, 0.0F, 0.0F, 0.01F, 0.0F, 0.0F, 0.0F, 0.0001F, 0.05F, 0.0F, 0.0F, 0.0F, 0.0F);
	
	//DEPRESSENT
	public static final Drug DEPRESSENT = new Drug("depressent", 600, 6000, 600, 0.0F, 0.03F, 0.01F, 0.0F, 0.0F, 0.0F, 0.01F, 0.01F, 0.0F, 0.0F, 0.0F, 0.0F);
	
	

}
