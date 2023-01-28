package com.jimholden.conomy.ai;

import com.jimholden.conomy.entity.EntityBaseZombie;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;

public class EntityAILookIdleGhoul extends EntityAILookIdle {
	
	EntityBaseZombie ghoul;

	public EntityAILookIdleGhoul(EntityBaseZombie entitylivingIn) {
		super(entitylivingIn);
		this.ghoul = entitylivingIn;
	}
	
	@Override
	public void startExecuting() {
		super.startExecuting();
		this.ghoul.setMovementState(0);
		
	}

}
