package com.jimholden.conomy.ai;

import com.jimholden.conomy.entity.EntityBaseZombie;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIHurtByTargetGhoul extends EntityAIHurtByTarget {

	public EntityBaseZombie zombie;
	public boolean willRally;
	private int revengeTimerOld;
	
	public EntityAIHurtByTargetGhoul(EntityBaseZombie creatureIn) {
		super(creatureIn, false, new Class[] {EntityPlayer.class});
		this.zombie = creatureIn;
		// TODO Auto-generated constructor stub
	}
	
	/**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        int i = this.taskOwner.getRevengeTimer();
        EntityLivingBase entitylivingbase = this.taskOwner.getRevengeTarget();
        return i != this.revengeTimerOld && entitylivingbase != null && this.isSuitableTarget(entitylivingbase, false);
    }
	
	@Override
	public void startExecuting() {
		this.zombie.makeAngry();
	}

}
