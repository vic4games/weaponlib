package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;
import com.vicmatskiv.weaponlib.particle.ExplosionParticleFX;
import com.vicmatskiv.weaponlib.particle.ExplosionSmokeFX;
import com.vicmatskiv.weaponlib.particle.FlashFX;
import com.vicmatskiv.weaponlib.particle.SmokeFX;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

final class ClientEffectManager implements EffectManager {


	ClientEffectManager() {}


	@Override
    public void spawnSmokeParticle(EntityLivingBase player, float xOffset, float yOffset) {

		double motionX = compatibility.world(player).rand.nextGaussian() * 0.003;
		double motionY = compatibility.world(player).rand.nextGaussian() * 0.003;
		double motionZ = compatibility.world(player).rand.nextGaussian() * 0.003;

		CompatibleVec3 look = compatibility.getLookVec(player);
		float distance = 0.3f;
		float scale = 1f * compatibility.getEffectScaleFactor(); // TODO: check why scale was set to 2.0 in 1.7.10
		float positionRandomizationFactor = 0.01f;

		double posX = player.posX + (look.getXCoord() * distance) + (compatibility.world(player).rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (-look.getZCoord() * xOffset);
		double posY = player.posY + (look.getYCoord() * distance) + (compatibility.world(player).rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor - yOffset;
		double posZ = player.posZ + (look.getZCoord() * distance) + (compatibility.world(player).rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor  + (look.getXCoord() * xOffset);

		SmokeFX smokeParticle = new SmokeFX(
				compatibility.world(player),
				posX,
		        posY,
		        posZ,
		        scale,
		      (float)motionX,
		      (float)motionY,
		      (float)motionZ);

		Minecraft.getMinecraft().effectRenderer.addEffect(smokeParticle);
	}

	/* (non-Javadoc)
     * @see com.vicmatskiv.weaponlib.IEffectManager#spawnFlashParticle(net.minecraft.entity.player.EntityPlayer, float, float, float, float)
     */
	@Override
    public void spawnFlashParticle(EntityLivingBase player, float flashIntensity, float flashScale,
			float xOffset, float yOffset) {

		float distance = 0.5f;

		float scale = 0.8f * compatibility.getEffectScaleFactor() * flashScale;
		float positionRandomizationFactor = 0.003f;

		CompatibleVec3 look = compatibility.getLookVec(player);

		float motionX = (float)compatibility.world(player).rand.nextGaussian() * 0.003f;
		float motionY = (float)compatibility.world(player).rand.nextGaussian() * 0.003f;
		float motionZ = (float)compatibility.world(player).rand.nextGaussian() * 0.003f;

		double posX = player.posX + (look.getXCoord() * distance) + (compatibility.world(player).rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (-look.getZCoord() * xOffset);
		double posY = player.posY + (look.getYCoord() * distance) + (compatibility.world(player).rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor - yOffset;
		double posZ = player.posZ + (look.getZCoord() * distance) + (compatibility.world(player).rand.nextFloat() * 2.0f - 1) * positionRandomizationFactor + (look.getXCoord() * xOffset);

		FlashFX flashParticle = new FlashFX(
				compatibility.world(player),
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

	/* (non-Javadoc)
     * @see com.vicmatskiv.weaponlib.IEffectManager#spawnExplosionSmoke(double, double, double, double, double, double)
     */
	@Override
    public void spawnExplosionSmoke(double posX, double posY, double posZ,
            double motionX, double motionY, double motionZ, float scale,
            int maxAge, ExplosionSmokeFX.Behavior behavior, ResourceLocation textureResource) {
	    World world = compatibility.world(compatibility.clientPlayer());
        ExplosionSmokeFX smokeParticle = new ExplosionSmokeFX(
                world,
                posX,
                posY,
                posZ,
                scale,
                (float)motionX,
                (float)motionY,
                (float)motionZ,
                maxAge,
                ExplosionSmokeFX.Behavior.SMOKE_GRENADE,
                textureResource);

        Minecraft.getMinecraft().effectRenderer.addEffect(smokeParticle);
	}

	@Override
	public void spawnExplosionParticle(double posX, double posY, double posZ,
	        double motionX, double motionY, double motionZ, float scale, int maxAge) {
	    World world = compatibility.world(compatibility.clientPlayer());
	    ExplosionParticleFX explosionParticle = new ExplosionParticleFX(
                world,
                posX,
                posY,
                posZ,
                scale,
                motionX, motionY, motionZ,
                maxAge);

        Minecraft.getMinecraft().effectRenderer.addEffect(explosionParticle);
	}
}
