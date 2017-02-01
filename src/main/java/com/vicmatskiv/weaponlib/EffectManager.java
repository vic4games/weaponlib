package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

final class EffectManager {
	
	private static EffectManager instance = new EffectManager();
	
	private EffectManager() {}
	
	static EffectManager getInstance() {
		return instance;
	}

	void spawnSmokeParticle(EntityPlayer player, float xOffset, float yOffset) {
		
		double motionX = player.worldObj.rand.nextGaussian() * 0.003;
		double motionY = player.worldObj.rand.nextGaussian() * 0.003;
		double motionZ = player.worldObj.rand.nextGaussian() * 0.003;
		
		CompatibleVec3 look = compatibility.getLookVec(player);
		float distance = 0.3f;
		float scale = 1f * compatibility.getEffectScaleFactor(); // TODO: check why scale was set to 2.0 in 1.7.10
		float positionRandomizationFactor = 0.01f;
		
		double posX = player.posX + (look.getVec().xCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (-look.getVec().zCoord * xOffset);
		double posY = player.posY + (look.getVec().yCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor - yOffset;
		double posZ = player.posZ + (look.getVec().zCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor  + (look.getVec().xCoord * xOffset);
		
		SmokeFX smokeParticle = new SmokeFX(
				player.worldObj, 
				posX,
		        posY, 
		        posZ,
		        scale,
		      (float)motionX, 
		      (float)motionY, 
		      (float)motionZ);
		
		Minecraft.getMinecraft().effectRenderer.addEffect(smokeParticle);
	}
	
	void spawnFlashParticle(EntityPlayer player, float flashIntensity, float xOffset, float yOffset) {
		
		float distance = 0.5f;
		
		float scale = 0.8f * compatibility.getEffectScaleFactor();
		float positionRandomizationFactor = 0.01f;
		
		CompatibleVec3 look = compatibility.getLookVec(player);
		
		float motionX = (float)player.worldObj.rand.nextGaussian() * 0.01f;
		float motionY = (float)player.worldObj.rand.nextGaussian() * 0.01f;
		float motionZ = (float)player.worldObj.rand.nextGaussian() * 0.01f;
		
		double posX = player.posX + (look.getVec().xCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (-look.getVec().zCoord * xOffset);
		double posY = player.posY + (look.getVec().yCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor - yOffset;
		double posZ = player.posZ + (look.getVec().zCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (look.getVec().xCoord * xOffset);
		
		FlashFX flashParticle = new FlashFX(
				player.worldObj, 
				posX,
				posY,
				posZ,
				scale,
				flashIntensity,
				motionX, 
				motionY, 
				motionZ);
		
		Minecraft.getMinecraft().effectRenderer.addEffect(flashParticle);
	}
}
