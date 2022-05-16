package com.vicmatskiv.weaponlib.ai;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicates;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityAIBase;
import com.vicmatskiv.weaponlib.render.modern.RayTraceUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class EntityAIAttackRangedWeapon extends CompatibleEntityAIBase
{
    private static final float DEFAULT_SECONDARY_EQUIPMENT_USE_CHANCE = 0.25f;
    
    private final EntityCustomMob entity;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistanceSquared;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private Set<Class<?>> attackWithItemType;
    private float secondaryEquipmentUseChance;
    
 
    private boolean openFire;
    private long lastShot;
    
    private int ammo;
    private int reloadTime;
    
    private boolean lookingForCover;
    private Vec3d coverLocation;
    
    private long coverTime;
   
    
    private long reloadStartTime;
    
    public EntityAIAttackRangedWeapon(EntityCustomMob customMob,
            double speedAmplifier, int delay, float maxDistance,
            Class<?> ...attackWithItemType) {
        this(customMob, speedAmplifier, delay, maxDistance, DEFAULT_SECONDARY_EQUIPMENT_USE_CHANCE, attackWithItemType);
    }

    public EntityAIAttackRangedWeapon(EntityCustomMob customMob,
            double speedAmplifier, int delay, float maxDistance, float secondaryEquipmentUseChance, 
            Class<?> ...attackWithItemType)
    {
        this.attackWithItemType = new HashSet<>();
        for(Class<?> c: attackWithItemType) {
            this.attackWithItemType.add(c);
        }
        
        this.entity = customMob;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistanceSquared = maxDistance * maxDistance;
        this.secondaryEquipmentUseChance = secondaryEquipmentUseChance;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if(entity.getAttackTarget() == null) {
            return false;
        }
        
       
        return isItemTypeInMainHand(); // || entity.getSecondaryEquipment() != null;
    }

    protected boolean isItemTypeInMainHand() {
        return compatibility.getHeldItemMainHand(entity) != null
                && (attackWithItemType.isEmpty() 
                        || attackWithItemType.stream().anyMatch(a -> a.isInstance(compatibility.getHeldItemMainHand(entity).getItem())));
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting() {
        return (shouldExecute() || !this.entity.getNavigator().noPath());
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        super.startExecuting();
        entity.setSwingingArms(true);
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        super.resetTask();
        entity.setSwingingArms(false);
        seeTime = 0;
        attackTime = -1;
        entity.resetActiveHand();
    }
    
    public void lookForCover(Vec3d coverFrom, boolean closerToPlayer) {
    	
    	double searchRadius = 10;
    	double halfSearchRadius = 2.5;
    	
    	
    	ArrayList<Vec3d> options = new ArrayList<>();
    	for(int i = 0; i < 10; ++i) {
    		Vec3d blockPosToCheck = new Vec3d(this.entity.posX + (Math.random()*searchRadius - halfSearchRadius),
    				this.entity.posY + this.entity.getEyeHeight(),
    				this.entity.posZ + (Math.random()*searchRadius - halfSearchRadius));
    	
    		RayTraceResult rtr = this.entity.world.rayTraceBlocks(blockPosToCheck, coverFrom, false, true, false);
    		if(rtr != null) {
    			
    			options.add(rtr.hitVec.add(blockPosToCheck.subtract(coverFrom).normalize()));
    			
    		}
    		
    	
    	}
    	
    	if(!options.isEmpty()) {
    		
    		if(options.size() > 1) {
    			if(!closerToPlayer) {
    				options.sort((a, b) -> (int) b.distanceTo(coverFrom) - (int) a.distanceTo(coverFrom));
    			} else {
    				options.sort((a, b) -> (int) a.distanceTo(coverFrom) - (int) b.distanceTo(coverFrom));
        			
    			}
    		}
    		
    		Vec3d idealLoc = options.get(0);
    		lookingForCover = true;
    		this.coverLocation = idealLoc;
    		this.entity.getNavigator().tryMoveToXYZ(idealLoc.x, idealLoc.y, idealLoc.z, 1.3D);
    		
    	}
    	
    	/*
    	break;
		*/
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        EntityLivingBase attackTarget = this.entity.getAttackTarget();

     
        int fireRate = 200;
        // If the AI is currently opening fire, shoot
        if(entity.getAmmo() > 0 && openFire && (lastShot == 0 || System.currentTimeMillis()-lastShot > fireRate)) {
        	lastShot = System.currentTimeMillis();
        	
        	double inaccuracy = 0.3;
        	
        	
        		this.entity.attackEntityWithRangedAttack(attackTarget, 0);
        	
        	
        	entity.setAmmo(entity.getAmmo() - 1);
            
        }
        
        
        
        if(openFire && attackTarget != null) {
        	
        	RayTraceResult rtr = this.entity.world.rayTraceBlocks(attackTarget.getPositionVector().addVector(0, attackTarget.getEyeHeight(), 0), this.entity.getPositionVector().addVector(0, this.entity.getEyeHeight(), 0), false, true, false);
        	if(rtr != null) {
        		openFire = false;
        		lookForCover(attackTarget.getPositionVector().addVector(0, attackTarget.getEyeHeight(), 0), true);
        		
        	}
        }
        
        if(attackTarget != null) {
        	RayTraceResult rtr = this.entity.world.rayTraceBlocks(attackTarget.getPositionVector().addVector(0, attackTarget.getEyeHeight(), 0), this.entity.getPositionVector().addVector(0, this.entity.getEyeHeight(), 0), false, true, false);
        	if(rtr == null) {
        		coverTime = -1;
        	} else {
        		if(coverTime == -1) coverTime = System.currentTimeMillis();
        	}
        }
        
        if(attackTarget != null) {
        	if(entity.getLastReload() == -1 && coverTime != -1 && System.currentTimeMillis() - coverTime > 3000) {
        		System.out.println("seeking out");
        		this.entity.getNavigator().tryMoveToEntityLiving(attackTarget, 1.3D);
        	}
        }
        
        // If reloading, seek shelter away from the player
        if(entity.getLastReload() != -1 && attackTarget != null) {
        	//System.out.println("In reload");
        	RayTraceResult rtr = this.entity.world.rayTraceBlocks(attackTarget.getPositionVector().addVector(0, attackTarget.getEyeHeight(), 0), this.entity.getPositionVector().addVector(0, this.entity.getEyeHeight(), 0), false, true, false);
        	if(rtr == null) {
        		lookForCover(attackTarget.getPositionVector().addVector(0, attackTarget.getEyeHeight(), 0), false);
        		
        	}
        }
      
        //System.out.println(lookingForCover);
      
        if(coverLocation != null && lookingForCover && (this.entity.getPositionVector().distanceTo(coverLocation) < 2 || (this.entity.getNavigator().getPath() != null && entity.getNavigator().getPath().isFinished()))) {
        	lookingForCover = false;
        	System.out.println("Found cover!");
        	if(attackTarget != null) {
        		System.out.println("Current entity position: " + this.entity.getPositionVector());
            	Path path = this.entity.getNavigator().getPathToEntityLiving(attackTarget);
            	if(path != null) {
            		for(int p = 0; p < path.getCurrentPathLength(); ++p) {
                		//System.out.println("(" + p + ") -> " + path.getPathPointFromIndex(p));
                	}
            	}
            	
        	}
        	
        }
        
        
        if(!lookingForCover && attackTarget != null) {
        	
        }
        
        List<WeaponSpawnEntity> entityList = this.entity.getEntityWorld().getEntities(WeaponSpawnEntity.class, Predicates.alwaysTrue());
        
        //if(entityList.size() > 15) entityList.clear();
        for(WeaponSpawnEntity wse : entityList) {
        //	System.out.println(attackTarget);
        	//wse.setDead();
        	
        	if(System.currentTimeMillis() - wse.birthStamp > 1500) {
        		wse.setDead();
        	}
        	
        	if(wse.getThrower() != attackTarget) continue;
        	if(wse.origin == Vec3d.ZERO) continue;
        	AxisAlignedBB aabb = entity.getEntityBoundingBox().grow(2);
        	RayTraceResult rtr = aabb.calculateIntercept(wse.origin, wse.origin.add(new Vec3d(wse.motionX, wse.motionY, wse.motionZ).scale(2)));
        	//System.out.println(rtr);
        	//System.out.println(wse.motionX + " | " + wse.motionY + " | " + wse.motionZ);
        	//System.out.println(wse.origin.subtract(new Vec3d(wse.posX, wse.posY, wse.posZ)));
        	if(rtr != null && !lookingForCover) {
        		System.out.println("Hit @ " + rtr);
        		lookForCover(attackTarget.getPositionVector().addVector(0, attackTarget.getEyeHeight(), 0), false);
        		break;
        		
        	}
        	
        	//if(wse.origin)
        	
        	
        	
        }
        
        if (attackTarget != null) {
            
            double d0 = this.entity.getDistanceSq(attackTarget.posX, 
                    compatibility.getBoundingBox(attackTarget).getMinY(), attackTarget.posZ);
            boolean canSeeTarget = this.entity.getEntitySenses().canSee(attackTarget);
            boolean flag1 = this.seeTime > 0;

            
            if (canSeeTarget != flag1) {
                this.seeTime = 0;
            }

            if (canSeeTarget) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            
            if(attackTarget != null) {
            	//this.entity.getNavigator().tryMoveToEntityLiving(attackTarget, 0.5);
                
            }
            
            
            this.entity.faceEntity(attackTarget, 30.0F, 30.0F);
            
            
            this.entity.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
            //compatibility.strafe(this.entity, this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            
            
            /*
            if (d0 <= (double)this.maxAttackDistanceSquared && this.seeTime >= 20) {
                this.entity.getNavigator().clearPathEntity();
                ++this.strafingTime;
            } else {
                this.entity.getNavigator().tryMoveToEntityLiving(attackTarget, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20)
            {
                if ((double)this.entity.getRNG().nextFloat() < 0.3D)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.entity.getRNG().nextFloat() < 0.3D)
                {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (compatibility.isStrafingSupported() && this.strafingTime > -1) {
                if (d0 > (double)(this.maxAttackDistanceSquared * 0.75F))
                {
                    this.strafingBackwards = false;
                }
                else if (d0 < (double)(this.maxAttackDistanceSquared * 0.25F))
                {
                    this.strafingBackwards = true;
                }

                compatibility.strafe(this.entity, this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.faceEntity(attackTarget, 30.0F, 30.0F);
            } else {
                this.entity.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
            }
			*/
            if (this.entity.isHandActive()) {
                if (!canSeeTarget && this.seeTime < -60) {
                    this.entity.resetActiveHand();
                } else if (canSeeTarget) {
                    if(entity.isFacingEntity(attackTarget)) {
                        this.entity.resetActiveHand();
                        
                        if(entity.getRNG().nextFloat() > secondaryEquipmentUseChance) {
                        	openFire = true;
                        }
                        /*
                        if(entity.getSecondaryEquipment() != null && entity.getRNG().nextFloat() < secondaryEquipmentUseChance) {
                        	for(int i = 0; i < 0; ++i) {
                        		 this.entity.attackWithSecondaryEquipment(attackTarget, 0); // TODO: set some distance factor
                        	}
                           
                        } else {
                        	
                        	openFire = !openFire;
	                        for(int i = 0; i < 0; ++i) {
	                        	this.entity.attackEntityWithRangedAttack(attackTarget, 0);
	                            
	                        }
                            // TODO: set some distance factor
                        }
                        */
                        this.attackTime = (this.attackCooldown >> 1) + this.entity.getRNG().nextInt(this.attackCooldown << 1);
                    }
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                this.entity.setActiveMainHand();
            }
        }
    }
}