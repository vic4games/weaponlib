package com.jimholden.conomy.ai;

import com.jimholden.conomy.entity.EntityBaseZombie;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;

public class EntityAIWanderAvoidWaterGhoul extends EntityAIWanderAvoidWater {

	EntityBaseZombie zombie;
	
	public EntityAIWanderAvoidWaterGhoul(EntityBaseZombie zombie, double p_i47301_2_) {
		super(zombie, p_i47301_2_);
		this.zombie = zombie;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void startExecuting() {
		super.startExecuting();
		this.zombie.setMovementState(1);
	}
	
	

}
