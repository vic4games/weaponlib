package com.vicmatskiv.weaponlib.animation;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerRawPitchAnimationManager {

    private Map<EntityPlayer, PlayerRawPitchAnimation> animations = new HashMap<>();
    private float maxYaw = 2f;
    private float maxPitch = 2f;
    private long transitionDuration = 2000;

    public PlayerRawPitchAnimationManager setMaxYaw(float maxYaw) {
        this.maxYaw = maxYaw;
        return this;
    }

    public PlayerRawPitchAnimationManager setMaxPitch(float maxPitch) {
        this.maxPitch = maxPitch;
        return this;
    }

    public PlayerRawPitchAnimationManager setTransitionDuration(long transitionDuration) {
        this.transitionDuration = transitionDuration;
        return this;
    }

    public void update(EntityPlayer player) {
        getAnimation(player).update();
    }

    public void reset(EntityPlayer player) {
        getAnimation(player).reset();
    }

    private PlayerRawPitchAnimation getAnimation(EntityPlayer player) {
        return animations.computeIfAbsent(player, p -> new PlayerRawPitchAnimation()
                .setMaxPitch(maxPitch)
                .setMaxYaw(maxYaw)
                .setPlayer(player)
                .setTransitionDuration(transitionDuration));
    }

}
