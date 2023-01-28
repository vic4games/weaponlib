package com.jimholden.conomy.ai;

import com.jimholden.conomy.entity.EntityBaseZombie;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIGhoulSleep extends EntityAIBase
{
    /** The entity that is looking idle. */
    private final EntityBaseZombie idleEntity;
    /** X offset to look at */
    private double lookX;
    /** Z offset to look at */
    private double lookZ;
    /** A decrementing tick that stops the entity from being idle once it reaches 0. */
    private int idleTime;

    public EntityAIGhoulSleep(EntityBaseZombie entitylivingIn)
    {
        this.idleEntity = entitylivingIn;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if(!this.idleEntity.world.isRemote) {
			if(this.idleEntity.getBrightness() >  0.5F && !this.idleEntity.isSleeping() && this.idleEntity.getAttackTarget() == null) {
				this.idleEntity.setSleeping(true);
				this.idleEntity.setNoAI(true);
				return true;
			} else if(this.idleEntity.isSleeping() && this.idleEntity.getBrightness() < 0.5) {
				this.idleEntity.setSleeping(false);
				this.idleEntity.setNoAI(false);
				return true;
			}
		}
		return false;
    }
    

}
