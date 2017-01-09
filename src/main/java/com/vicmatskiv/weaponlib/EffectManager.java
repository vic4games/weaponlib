package com.vicmatskiv.weaponlib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

final class EffectManager {
	
	private static EffectManager instance = new EffectManager();
	
	private EffectManager() {}
	
	static EffectManager getInstance() {
		return instance;
	}

	void spawnSmokeParticle(EntityPlayer player) {
		
		double motionX = player.world.rand.nextGaussian() * 0.003d;
		double motionY = player.world.rand.nextGaussian() * 0.003d;
		double motionZ = player.world.rand.nextGaussian() * 0.003d;
		
		Vec3d look = player.getLookVec();
		float distance = 0.3f;
		float yOffset = -1.6f;
		float xOffset = 0.0f;
		float scale = 1f;
		float positionRandomizationFactor = 0.01f;
		
		double posX = player.posX + (look.xCoord * distance) + (player.world.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (-look.zCoord * xOffset);
		double posY = player.posY + (look.yCoord * distance) + (player.world.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor - yOffset;
		double posZ = player.posZ + (look.zCoord * distance) + (player.world.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (look.xCoord * xOffset);
		
		Particle smokeParticle = new SmokeFX(
				player.world, 
				posX,
		        posY, 
		        posZ,
		        scale,
		      (float)motionX, 
		      (float)motionY, 
		      (float)motionZ);
		
		Minecraft.getMinecraft().effectRenderer.addEffect(smokeParticle);
	}
	
	void spawnFlashParticle(EntityPlayer player, float flashIntensity, float xOffset) {
		
		float distance = 0.5f;
		
		float yOffset = -1.6f;
		float scale = 0.8f;
		float positionRandomizationFactor = 0.01f;
		
		Vec3d look = player.getLookVec();
		
		float motionX = (float)player.world.rand.nextGaussian() * 0.01f;
		float motionY = (float)player.world.rand.nextGaussian() * 0.01f;
		float motionZ = (float)player.world.rand.nextGaussian() * 0.01f;
		
		double posX = player.posX + (look.xCoord * distance) + (player.world.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (-look.zCoord * xOffset);
		double posY = player.posY + (look.yCoord * distance) + (player.world.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor - yOffset;
		double posZ = player.posZ + (look.zCoord * distance) + (player.world.rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (look.xCoord * xOffset);
		
		Particle flashParticle = new FlashFX(
				player.world, 
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
