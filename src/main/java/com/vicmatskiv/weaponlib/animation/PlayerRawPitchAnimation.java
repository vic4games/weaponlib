package com.vicmatskiv.weaponlib.animation;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerRawPitchAnimation implements PlayerAnimation {

    private static final float ATTENUATION_COEFFICIENT = 0.5f;
    private float lastYaw;
    private float lastPitch;
    private float anchoredYaw;
    private float anchoredPitch;
    private float startYaw;
    private float startPitch;
    private float targetYaw;
    private float targetPitch;
    
    private float targetScaleX = 1f;
    private float targetScaleY = 1f;
    private float targetScaleZ = 1f;
    
    private float targetRotateX = 0f;
    private float targetRotateY = 0f;
    private float targetRotateZ = 0f;

    private float maxYaw = 2f;
    private float maxPitch = 2f;

    private float attenuation = ATTENUATION_COEFFICIENT;

    private Random rand = new Random();

    private long transitionDuration = 2000;

    private long startTime;
    
    private int priority;

    private EntityPlayer clientPlayer;

    private boolean forceResetYawPitch;
    
    private boolean cycleCompleted = true;

    PlayerRawPitchAnimation setMaxYaw(float maxYaw) {
        this.maxYaw = maxYaw;
        return this;
    }

    PlayerRawPitchAnimation setPriority(int priority) {
        this.priority = priority;
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

    public void update(EntityPlayer player) {
        float progress = (float)(System.currentTimeMillis() - startTime) / transitionDuration;
        
        if(progress >= 1f) {
            cycleCompleted = true;
        } else {
            cycleCompleted = false;
        }

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

            targetScaleZ = 1 + rand.nextFloat() / 5f;
            
            targetRotateZ = (rand.nextFloat() - 0.5f) * 2f;
            
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
        
        float currentScaleX = 1f + (targetScaleX - 1) * progress;
        float currentScaleY = 1f + (targetScaleY - 1) * progress;
        float currentScaleZ = 1f + (targetScaleZ - 1) * progress;
        GL11.glScalef(currentScaleX, currentScaleY, targetScaleZ);
        System.out.println("Scale z: " + currentScaleZ);

        GL11.glRotatef(2, 0f, 0f, targetScaleZ * progress);
    }

    public void reset(EntityPlayer player) {
        forceResetYawPitch = true;
    }

    private boolean rotationPitchChanged(EntityPlayer clientPlayer) {
        return !(lastYaw == clientPlayer.rotationYaw && lastPitch == clientPlayer.rotationPitch);
    }
    
    @Override
    public boolean cycleCompleted() {
        return cycleCompleted;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
