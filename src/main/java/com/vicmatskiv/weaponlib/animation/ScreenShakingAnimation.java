package com.vicmatskiv.weaponlib.animation;

import com.vicmatskiv.weaponlib.animation.ScreenShakeAnimationManager.State;

import net.minecraft.entity.player.EntityPlayer;

public interface ScreenShakingAnimation {
    
    /**
     * Returns true if the animation cycle is in progress, otherwise false
     * @param player
     * @return
     */
    public void update(EntityPlayer player, boolean fadeOut);
    
    public void reset(EntityPlayer player, boolean force);
    
    public boolean isCompleted();
    
    public static ScreenShakingAnimation NO_ANIMATION = new ScreenShakingAnimation() {

        @Override
        public void update(EntityPlayer player, boolean fadeOut) {
 
        }

        @Override
        public void reset(EntityPlayer player, boolean force) {            
        }

        @Override
        public boolean isCompleted() {
            return true;
        }

        @Override
        public State getState() {
            return State.DEFAULT;
        }
    };
    
    public State getState();
    
}
