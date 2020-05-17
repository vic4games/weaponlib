package com.vicmatskiv.weaponlib.animation;

import net.minecraft.entity.player.EntityPlayer;

public interface PlayerAnimation {
    
    /**
     * Returns true if the animation cycle is in progress, otherwise false
     * @param player
     * @return
     */
    public void update(EntityPlayer player);
    
    public void reset(EntityPlayer player);
    
    public boolean cycleCompleted();
    
    public static PlayerAnimation NO_ANIMATION = new PlayerAnimation() {

        @Override
        public void update(EntityPlayer player) {
 
        }

        @Override
        public void reset(EntityPlayer player) {            
        }

        @Override
        public int getPriority() {
            return Integer.MIN_VALUE;
        }

        @Override
        public boolean cycleCompleted() {
            return true;
        }
    };

    public int getPriority();
    
}
