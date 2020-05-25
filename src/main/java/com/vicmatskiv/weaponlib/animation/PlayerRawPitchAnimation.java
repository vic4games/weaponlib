package com.vicmatskiv.weaponlib.animation;

import java.util.Random;

import com.vicmatskiv.weaponlib.animation.ScreenShakeAnimationManager.State;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerRawPitchAnimation implements ScreenShakingAnimation {

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

//    private EntityPlayer clientPlayer;

    private boolean forceResetYawPitch;
    private State state;
    
    public PlayerRawPitchAnimation(State state) {
        this.state = state;
    }

    public PlayerRawPitchAnimation setMaxYaw(float maxYaw) {
        this.maxYaw = maxYaw;
        return this;
    }

    public PlayerRawPitchAnimation setMaxPitch(float maxPitch) {
        this.maxPitch = maxPitch;
        return this;
    }

    public PlayerRawPitchAnimation setTransitionDuration(long transitionDuration) {
        this.transitionDuration = transitionDuration;
        return this;
    }

//    PlayerRawPitchAnimation setPlayer(EntityPlayer clientPlayer) {
//        this.clientPlayer = clientPlayer;
//        return this;
//    }

    public void update(EntityPlayer player, boolean fadeOut) {
        float progress = (float)(System.currentTimeMillis() - startTime) / transitionDuration;

        if(forceResetYawPitch || rotationPitchChanged(player)) {
            anchoredYaw = player.rotationYaw;
            anchoredPitch = player.rotationPitch;
            forceResetYawPitch = true;
            attenuation = 1f;
        }

        if(forceResetYawPitch || progress > 1f) {
            progress = 0f;
            startTime = System.currentTimeMillis();

            startYaw = player.rotationYaw;
            startPitch = player.rotationPitch;

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

        player.rotationYaw = startYaw + (targetYaw - startYaw) * progress;
        player.rotationPitch = startPitch + (targetPitch - startPitch) * progress;

        lastYaw = player.rotationYaw;
        lastPitch = player.rotationPitch;
    }

    public void reset(EntityPlayer player, boolean force) {
        if(force) {
            forceResetYawPitch = true;
        }
    }
    
    @Override
    public boolean isCompleted() {
        // TODO Auto-generated method stub
        return true;
    }

    private boolean rotationPitchChanged(EntityPlayer clientPlayer) {
        return !(lastYaw == clientPlayer.rotationYaw && lastPitch == clientPlayer.rotationPitch);
    }

    @Override
    public State getState() {
        return state;
    }
}
