package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.particle.ExplosionSmokeFX.Behavior;

import net.minecraft.entity.player.EntityPlayer;

public interface EffectManager {

    void spawnSmokeParticle(EntityPlayer player, float xOffset, float yOffset);

    void spawnFlashParticle(EntityPlayer player, float flashIntensity, float flashScale, float xOffset, float yOffset);

    void spawnExplosionSmoke(double posX, double posY, double posZ, double motionX, double motionY, double motionZ,
            float scale, int maxAge, Behavior behavior);

    void spawnExplosionParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ,
            float scale, int maxAge);

}