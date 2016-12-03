package com.vicmatskiv.weaponlib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

final class EffectManager {
	
	private static EffectManager instance = new EffectManager();
	
	private EffectManager() {}
	
	static EffectManager getInstance() {
		return instance;
	}

	void spawnSmokeParticle(EntityPlayer player) {
		
		double motionX = player.worldObj.rand.nextGaussian() * 0.003D;
		double motionY = player.worldObj.rand.nextGaussian() * 0.003D;
		double motionZ = player.worldObj.rand.nextGaussian() * 0.003D;
		
		Vec3 look = player.getLookVec();
		float distance = 0.3F;
		float yOffset = -1.5F;
		double posX = player.posX + (look.xCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0F - 1) * 0.1f;
		double posY = player.posY + (look.yCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0F - 1) * 0.1f - yOffset;
		double posZ = player.posZ + (look.zCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0F - 1) * 0.1f;
		
		EntityFX smokeParticle = new SmokeFX(
				player.worldObj, 
				posX,
		        posY, 
		        posZ,
		        2.0f,
		      (float)motionX, 
		      (float)motionY, 
		      (float)motionZ);
		
//		EntityFX smokeParticle = new EntityCritFX.Factory().getEntityFX(0,
//				player.worldObj, 
//				posX,
//		        posY, 
//		        posZ,
////		        
//		      motionX, 
//		      motionY, 
//		      motionZ, 1);
		
		//player.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, 0, 0, 0);
		Minecraft.getMinecraft().effectRenderer.addEffect(smokeParticle);
	}
	
	void spawnFlashParticle(EntityPlayer player, float flashIntensity, float xOffset) {
		
		float distance = 0.5f;
		
		float yOffset = -1.5f;
		float scale = 1.5f;
		float positionRandomizationFactor = 0.01f;
		
		Vec3 look = player.getLookVec();
		
		float motionX = (float)player.worldObj.rand.nextGaussian() * 0.01f;
		float motionY = (float)player.worldObj.rand.nextGaussian() * 0.01f;
		float motionZ = (float)player.worldObj.rand.nextGaussian() * 0.01f;
		
		double posX = player.posX + (look.xCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (-look.zCoord * xOffset);
		double posY = player.posY + (look.yCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor - yOffset;
		double posZ = player.posZ + (look.zCoord * distance) + (player.worldObj.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (look.xCoord * xOffset);
		
		EntityFX flashParticle = new FlashFX(
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
