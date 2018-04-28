package com.vicmatskiv.weaponlib.animation;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerRawPitchAnimation {

    private static final float ATTENUATION_COEFFICIENT = 0.5f;
    private float lastYaw;
    private float lastPitch;
    private float anchoredYaw;
    private float anchoredPitch;
    private float startYaw;
    private float startPitch;
    private float targetYaw;
    private float targetPitch;

    private float maxYaw = 2f;
    private float maxPitch = 2f;

    private float attenuation = ATTENUATION_COEFFICIENT;

    private Random rand = new Random();

    private long transitionDuration = 2000;

    private long startTime;

    private EntityPlayer clientPlayer;

    private boolean forceResetYawPitch;

    PlayerRawPitchAnimation setMaxYaw(float maxYaw) {
        this.maxYaw = maxYaw;
        return this;
    }

    PlayerRawPitchAnimation setMaxPitch(float maxPitch) {
        this.maxPitch = maxPitch;
        return this;
    }

    PlayerRawPitchAnimation setTransitionDuration(long transitionDuration) {
        this.transitionDuration = transitionDuration;
        return this;
    }

    PlayerRawPitchAnimation setPlayer(EntityPlayer clientPlayer) {
        this.clientPlayer = clientPlayer;
        return this;
    }

    public void update() {
        float progress = (float)(System.currentTimeMillis() - startTime) / transitionDuration;

        if(forceResetYawPitch || rotationPitchChanged(clientPlayer)) {
            anchoredYaw = clientPlayer.rotationYaw;
            anchoredPitch = clientPlayer.rotationPitch;
            forceResetYawPitch = true;
            attenuation = 1f;
        }

        if(forceResetYawPitch || progress > 1f) {
            progress = 0f;
            startTime = System.currentTimeMillis();

            startYaw = clientPlayer.rotationYaw;
            startPitch = clientPlayer.rotationPitch;

            targetYaw = anchoredYaw + (rand.nextFloat() - 0.5f) * 2f * maxYaw * attenuation;
            //float yawChange = targetYaw - startYaw;
            targetPitch = anchoredPitch + (rand.nextFloat() - 0.5f) * 2f * maxPitch * attenuation;

            attenuation *= ATTENUATION_COEFFICIENT;
            if(attenuation < 0.1f) {
                attenuation = 0.1f;
            }
        }

        if(forceResetYawPitch) {
            forceResetYawPitch = false;
        }

        clientPlayer.rotationYaw = startYaw + (targetYaw - startYaw) * progress;
        clientPlayer.rotationPitch = startPitch + (targetPitch - startPitch) * progress;

        lastYaw = clientPlayer.rotationYaw;
        lastPitch = clientPlayer.rotationPitch;
    }

    public void reset() {
        forceResetYawPitch = true;
    }

    private boolean rotationPitchChanged(EntityPlayer clientPlayer) {
        return !(lastYaw == clientPlayer.rotationYaw && lastPitch == clientPlayer.rotationPitch);
    }
}