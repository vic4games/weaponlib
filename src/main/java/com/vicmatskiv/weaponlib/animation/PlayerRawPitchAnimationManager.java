package com.vicmatskiv.weaponlib.animation;

import java.util.HashMap;
import java.util.Map;

import com.vicmatskiv.weaponlib.RenderableState;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerRawPitchAnimationManager {
    
    private static enum State { SHOOTING, AIMING, DEFAULT }
    
    private static class Key {
        EntityPlayer player;
        State state;
        
        public Key(EntityPlayer player, State state) {
            this.player = player;
            this.state = state;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((player == null) ? 0 : player.hashCode());
            result = prime * result + ((state == null) ? 0 : state.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Key other = (Key) obj;
            if (player == null) {
                if (other.player != null)
                    return false;
            } else if (!player.equals(other.player))
                return false;
            if (state != other.state)
                return false;
            return true;
        }
        
    }

    private Map<Key, PlayerAnimation> allPlayerAnimations = new HashMap<>();
    private Map<EntityPlayer, PlayerAnimation> activeAnimations = new HashMap<>();
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

    protected PlayerAnimation getActiveAnimation(EntityPlayer player, RenderableState weaponState) {
        State managedState = toManagedState(weaponState);
        PlayerAnimation activeAnimation = activeAnimations.get(player);
        if(activeAnimation == null) {
            activeAnimation = getAnimationForManagedState(player, managedState);
            activeAnimations.put(player, activeAnimation);
        } else if (activeAnimation.getPriority() < getManagedStatePriority(managedState)){
            activeAnimation = getAnimationForManagedState(player, managedState);
            activeAnimations.put(player, activeAnimation);
        } else if (activeAnimation.getPriority() > getManagedStatePriority(managedState) && activeAnimation.cycleCompleted()) {
            activeAnimation = getAnimationForManagedState(player, managedState);
            activeAnimations.put(player, activeAnimation);
        }
        return activeAnimation;
    }

    private int getManagedStatePriority(State managedState) {
        int priority;
        switch(managedState) {
        case SHOOTING:
            priority = 0;
            break;
        case AIMING:
            priority = -1;
            break;
        default:
            priority = Integer.MIN_VALUE;
        }
        return priority;
    }
    
    public void update(EntityPlayer player, RenderableState weaponState) {
        /*
         * Activate animation can be remove only if there is a higher priority state 
         * or a lower priority state AND the current animation completed
         */
        PlayerAnimation activeAnimation = getActiveAnimation(player, weaponState);
        activeAnimation.update(player);
    }

    public void reset(EntityPlayer player, RenderableState weaponState) {
        PlayerAnimation activeAnimation = getActiveAnimation(player, weaponState);
        activeAnimation.reset(player);
    }
    
    private State toManagedState(RenderableState weaponState) {
        if(weaponState == null) {
            return State.DEFAULT;
        }
        State managedState;
        switch(weaponState) {
        case SHOOTING: case RECOILED: case ZOOMING_SHOOTING: case ZOOMING_RECOILED:
            managedState = State.SHOOTING;
            break;
        case ZOOMING:
            managedState = State.AIMING;
            break;
        default:
            managedState = State.DEFAULT;
        }
        return managedState;
    }
    
    private PlayerAnimation createAnimationForManagedState(EntityPlayer player, State managedState) {
        PlayerAnimation animation;
        int priority = getManagedStatePriority(managedState);
        switch(managedState) {
        case AIMING:
            animation = new PlayerRawPitchAnimation()
                    .setPriority(priority)
                    .setMaxPitch(maxPitch)
                    .setMaxYaw(maxYaw)
                    .setPlayer(player)
                    .setTransitionDuration(transitionDuration);
            break;
        case SHOOTING:
            animation = new PlayerRawPitchAnimation()
                    .setPriority(priority)
                    .setMaxPitch(maxPitch * 3)
                    .setMaxYaw(maxYaw * 3)
                    .setPlayer(player)
                    .setTransitionDuration(150);
            break;
        case DEFAULT: default:
            animation = PlayerAnimation.NO_ANIMATION;
            break;
        }
        return animation;
    }

    private PlayerAnimation getAnimationForManagedState(EntityPlayer player, State managedState) {
        return allPlayerAnimations.computeIfAbsent(new Key(player, managedState), 
                k -> createAnimationForManagedState(k.player, k.state));
    }

}
