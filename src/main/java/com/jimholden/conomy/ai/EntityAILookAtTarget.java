package com.jimholden.conomy.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.util.VectorUtil;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.ZombieScreamPacket;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

public class EntityAILookAtTarget extends EntityAIWatchClosest {

	private int lookTime;

	public EntityAILookAtTarget(EntityLiving entityIn, Class<? extends Entity> watchTargetClass) {
		super(entityIn, watchTargetClass, 10.0F, 100.0F);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean shouldExecute() {
		if (this.entity.getAttackTarget() != null)
        {
            this.closestEntity = this.entity.getAttackTarget();
            return true;
        } else {
        	return false;
        }
		
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		//System.out.println("hi");
		if (!this.closestEntity.isEntityAlive())
        {
            return false;
        } 
		else
        {
            return this.lookTime > 0;
        }
	}

}