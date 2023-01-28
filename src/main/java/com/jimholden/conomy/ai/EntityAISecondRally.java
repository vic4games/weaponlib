package com.jimholden.conomy.ai;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.util.packets.ZombieScreamPacket;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;

public class EntityAISecondRally extends EntityAIHurtByTarget {

	public EntityAISecondRally(EntityCreature creatureIn, boolean entityCallsForHelpIn,
			Class<?>[] excludedReinforcementTypes) {
		super(creatureIn, entityCallsForHelpIn, excludedReinforcementTypes);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
	
		if(this.target != null && this.taskOwner.getHealth()/this.taskOwner.getMaxHealth() < 0.5F && this.taskOwner.getHealth()/this.taskOwner.getMaxHealth() > 0.40F) {
			alertOthers();
		}
		return super.shouldExecute();
	}
	
	@Override
	public void startExecuting() {
		// TODO Auto-generated method stub
		super.startExecuting();
	}
	
	@Override
	protected void alertOthers() {
		if(!this.taskOwner.world.isRemote) {
			
			((EntityBaseZombie) this.taskOwner).attemptToRally();
		}
		super.alertOthers();
	}

}
