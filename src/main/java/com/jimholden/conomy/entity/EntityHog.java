package com.jimholden.conomy.entity;

import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class EntityHog extends EntityPig {

	public EntityHog(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}
	
	protected void initEntityAI()
    {

        this.tasks.addTask(0, new EntityAIAvoidEntity<>(this, EntityArrow.class, 3, 5, 5));
    }

}
