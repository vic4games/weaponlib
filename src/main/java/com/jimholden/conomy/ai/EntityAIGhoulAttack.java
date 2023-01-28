package com.jimholden.conomy.ai;

import com.jimholden.conomy.entity.EntityBaseZombie;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityZombie;

public class EntityAIGhoulAttack extends EntityAIAttackMelee
{
    private final EntityBaseZombie zombie;
    private int raiseArmTicks;

    public EntityAIGhoulAttack(EntityBaseZombie zombieIn, double speedIn, boolean longMemoryIn)
    {
        super(zombieIn, speedIn, longMemoryIn);
        this.zombie = zombieIn;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
        this.raiseArmTicks = 0;
        this.zombie.setMovementState(2);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        super.resetTask();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        super.updateTask();
    }
}
