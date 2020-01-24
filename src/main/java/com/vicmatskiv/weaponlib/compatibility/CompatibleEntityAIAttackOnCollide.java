package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.ai.EntityCustomMob;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

//
//import net.minecraft.entity.EntityCreature;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.ai.EntityAIBase;
//import net.minecraft.pathfinding.PathEntity;
//import net.minecraft.pathfinding.PathPoint;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.world.World;

public class CompatibleEntityAIAttackOnCollide extends EntityAIBase {

    public CompatibleEntityAIAttackOnCollide(EntityCustomMob e, Class<EntityPlayer> class1, double d, boolean b) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean shouldExecute() {
        // TODO Auto-generated method stub
        return false;
    }



}
