package com.vicmatskiv.weaponlib.mission;

import com.vicmatskiv.weaponlib.ai.EntityCustomMob;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityAIBase;

import net.minecraft.entity.player.EntityPlayer;

public class AssignMissionTask extends CompatibleEntityAIBase {
    private final EntityCustomMob merchant;

    public AssignMissionTask(EntityCustomMob merchant) {
        this.merchant = merchant;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (!this.merchant.isEntityAlive()) {
            return false;
        } else if (this.merchant.isInWater()) {
            return false;
        } else if (!this.merchant.onGround) {
            return false;
        } else if (this.merchant.velocityChanged) {
            return false;
            
        } else {
        	return false;
        	/*
            EntityPlayer entityplayer = this.merchant.getCustomer();

            if (entityplayer == null) {
                return false;
            } else if (this.merchant.getDistanceSqToEntity(entityplayer) > 16.0D) {
                return false;
            } else {
                return entityplayer.openContainer != null;
            }*/
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.merchant.getNavigator().clearPathEntity();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by
     * another one
     */
    public void resetTask() {
       // this.merchant.setCustomer((EntityPlayer) null);
    }
}