package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class CompatiblePlayerCreatureWrapper extends EntityClientPlayerMP {

    private EntityLivingBase entityLiving;
    
    private static Session session = new Session("1", "2", "3", "4");
    
    public CompatiblePlayerCreatureWrapper(Minecraft mc, World world) {
        super(mc, world, session, null, null);
        
    }
    
    public void setEntityLiving(EntityLivingBase entityLiving) {
        this.entityLiving = entityLiving;
        updateCoordinates();
    }
    
    public EntityLivingBase getEntityLiving() {
        return entityLiving;
    }

    public void updateCoordinates() {
        this.posX = entityLiving.posX;
        this.posY = entityLiving.posY;
        this.posZ = entityLiving.posZ;
        
        this.lastTickPosX = entityLiving.lastTickPosX;
        this.lastTickPosY = entityLiving.lastTickPosY;
        this.lastTickPosZ = entityLiving.lastTickPosZ;
        
        this.height = entityLiving.height;
        
        this.cameraYaw = entityLiving.rotationYaw;
        this.cameraPitch = entityLiving.cameraPitch;
        
        this.rotationYaw = entityLiving.rotationYaw;
        this.rotationPitch = entityLiving.rotationPitch;
        this.rotationYawHead = entityLiving.rotationYawHead;
        
        this.motionX = entityLiving.motionX;
        this.motionY = entityLiving.motionY;
        this.motionZ = entityLiving.motionZ;
        
        this.chunkCoordX = entityLiving.chunkCoordX;
        this.chunkCoordY = entityLiving.chunkCoordY;
        this.chunkCoordZ = entityLiving.chunkCoordZ;
        
        this.addedToChunk = entityLiving.addedToChunk;
        this.arrowHitTimer = entityLiving.arrowHitTimer;
        this.attackedAtYaw = entityLiving.attackedAtYaw;
        
        this.attackTime = entityLiving.attackTime;
        this.dimension = entityLiving.dimension;
        this.entityUniqueID = entityLiving.getUniqueID();
        this.limbSwing = entityLiving.limbSwing;
        this.limbSwingAmount = entityLiving.limbSwingAmount;
        this.height = entityLiving.height;
        this.moveForward = entityLiving.moveForward;
        this.ticksExisted = entityLiving.ticksExisted;
    }
    
    @Override
    public float getFOVMultiplier() {
        return 0.5f;
    }
}
