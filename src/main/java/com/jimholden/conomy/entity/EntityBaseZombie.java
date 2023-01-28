package com.jimholden.conomy.entity;

import java.util.List;
import java.util.Random;

import com.google.common.base.Function;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.ai.AILOSAttack;
import com.jimholden.conomy.ai.EntityAIGhoulAttack;
import com.jimholden.conomy.ai.EntityAIGhoulSleep;
import com.jimholden.conomy.ai.EntityAIHurtByTargetGhoul;
import com.jimholden.conomy.ai.EntityAILOSTarget;
import com.jimholden.conomy.ai.EntityAILookAtTarget;
import com.jimholden.conomy.ai.EntityAILookIdleGhoul;
import com.jimholden.conomy.ai.EntityAISecondRally;
import com.jimholden.conomy.ai.EntityAIWanderAvoidWaterGhoul;
import com.jimholden.conomy.ai.EntityAIWanderToEntity;
import com.jimholden.conomy.items.ISaveableItem;
import com.jimholden.conomy.util.BoxUtil;
import com.jimholden.conomy.util.RandomNumberTool;
import com.jimholden.conomy.util.VectorUtil;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.BloodExplosionPacket;
import com.jimholden.conomy.util.packets.ZombieScreamPacket;
import com.jimholden.conomy.util.packets.ZombieSleepSurvey;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import scala.tools.nsc.transform.patmat.Interface;

public class EntityBaseZombie extends EntityMob {


	int tick = 0;
	int rallyTimer = 0;
	boolean shouldTickRally = false;
	int investigationTimer = 0;
	int angerTimer = 0;
	int deathTimer = 0;
	boolean isRunningDeathTimer = false;
	
	boolean isSleeping = false;
	
	/*
	 * STALKING
	 */
	public EntityLivingBase watcher;
	public int losTimer = 0;
	
	
	public Vec3d investigatePos;
	public static final Random rand = new Random();
	public boolean queuedScream = false;
	public boolean hasAlreadyCalledRally = false;
	//private static final DataParameter<Boolean> IS_SLEEPING = EntityDataManager.<Boolean>createKey(EntityBaseZombie.class, DataSerializers.VARINT);
	
	private static final DataParameter<Boolean> IS_ANGRY = EntityDataManager.<Boolean>createKey(EntityBaseZombie.class, DataSerializers.BOOLEAN);
	
	private static final DataParameter<Boolean> IS_SLEEPING = EntityDataManager.<Boolean>createKey(EntityBaseZombie.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> MOVE_STATE = EntityDataManager.<Integer>createKey(EntityBaseZombie.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DEATH_ANIM_SELECTOR = EntityDataManager.<Integer>createKey(EntityBaseZombie.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_CORPSE = EntityDataManager.<Boolean>createKey(EntityBaseZombie.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_HEAD_BOOM = EntityDataManager.<Boolean>createKey(EntityBaseZombie.class, DataSerializers.BOOLEAN);
	private static final DataParameter<EnumFacing> DEATH_DIRECTION = EntityDataManager.<EnumFacing>createKey(EntityBaseZombie.class, DataSerializers.FACING);
	private static final DataParameter<Integer> DECOMPOSE_TIME = EntityDataManager.<Integer>createKey(EntityBaseZombie.class, DataSerializers.VARINT);

	public double previousPos =0.0;
	
	public boolean isWalking = false;
	
	public EntityBaseZombie(World worldIn) {
		super(worldIn);
		//EnumHelper.
		//dama
		// TODO Auto-generated constructor stub
	}
	
	public static enum AlertType {
		FOOTSTEP,
		GUNSHOT,
		SCREAM;
	}

	
	
	@Override
	protected void initEntityAI()
    {
	//	System.out.println("yo");
		//this.tasks.addTask(0, new EntityAIGhoulSleep(this));
		this.tasks.addTask(0, new EntityAISwimming(this));
		//this.tasks.addTask(1, new EntityAIInvestigateGhoul(this, 0.8D));
		this.tasks.addTask(2, new EntityAIGhoulAttack(this, 1.2D, false));
		
		//this.tasks.addTask(3, new EntityAILookAtTarget(this, EntityPlayer.class));
		
		this.tasks.addTask(7, new EntityAIWanderAvoidWaterGhoul(this, 1.0D));
		//this.tasks.addTask(3, new EntityAIWanderToEntity<>(this, this.getClass(), 3, 1, 1));
		//System.out.println("yo bruv");
		//this.tasks.addTask(0, new EntityAIAvoidEntity<>(this, EntityPlayer.class, 3, 1, 1));
		//this.tasks.addTask(0, new EntityAILeapAtTarget(this, 2.0F));
        this.applyEntityAI();
    }
	
	

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		super.entityInit();
		this.getDataManager().register(IS_ANGRY, Boolean.valueOf(false));
		this.getDataManager().register(IS_SLEEPING, Boolean.valueOf(false));
		//System.out.println("SETTER");
		this.getDataManager().register(MOVE_STATE, Integer.valueOf(0));
		this.getDataManager().register(IS_CORPSE, Boolean.valueOf(false));
		this.getDataManager().register(IS_HEAD_BOOM, Boolean.valueOf(false));
		this.getDataManager().register(DEATH_DIRECTION, EnumFacing.NORTH);
		this.getDataManager().register(DEATH_ANIM_SELECTOR, Integer.valueOf(0));
		this.getDataManager().register(DECOMPOSE_TIME, Integer.valueOf(240));
		setSize(0.6F, 2.0F);
		
	}
	
    protected void applyEntityAI()
    {
		this.targetTasks.addTask(1, new EntityAIHurtByTargetGhoul(this));
		//this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityAnimal.class, true));
		
		//this.targetTasks.addTask(2, new AILOSAttack<EntityPlayer>(this, EntityPlayer.class, true));
		//this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
		//this.targetTasks.addTask(4, new EntityAISecondRally(this, true, new Class[] {EntityPlayer.class}));
		//this.targetTasks.addTask(2, new EntityAILOSTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }
	
    public boolean isWalking() {
    	return this.isWalking;
    }
    
	@Override
	protected void applyEntityAttributes() {
	        super.applyEntityAttributes();
	        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
	        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.5D);
	        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
	        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(10.0D);
	       // this.getAttributeMap().registerAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.rand.nextDouble() * net.minecraftforge.common.ForgeModContainer.zombieSummonBaseChance);
	}
	
	
	
	public void makeAngry() {
		getDataManager().set(IS_ANGRY, true);
		
	}
	
	public void calmDown() {
		getDataManager().set(IS_ANGRY, false);
	
	}
	
	/*
	 * VARIOUS SETTERS AND GETTERS
	 */
	
 	public boolean isAngry() {
		return this.dataManager.get(IS_ANGRY).booleanValue();
	}

 	public boolean isCorpse() {
 		return this.dataManager.get(IS_CORPSE).booleanValue();
 	}
 	
 	public void setIsCorpse(boolean state) {
 		this.dataManager.set(IS_CORPSE, state);
 	}
 	
 	
 	public void setDeathDirection(EnumFacing face) {
 		this.dataManager.set(DEATH_DIRECTION, face);
 	}
 	
 	public EnumFacing getDeathDirection() {
 		return this.dataManager.get(DEATH_DIRECTION);
 	}
 	
 	public int getMovementState() {
 		return ((Integer) this.getDataManager().get(MOVE_STATE)).intValue();
 	}
 	
 	public void setMovementState(int state) {
 		this.getDataManager().set(MOVE_STATE, state);
 	}
 	
 	public int getDeathAnimationSelector() {
 		return ((Integer) this.getDataManager().get(DEATH_ANIM_SELECTOR)).intValue();
 	}
 	
 	public void setDeathAnimationSelector(int state) {
 		this.getDataManager().set(DEATH_ANIM_SELECTOR, state);
 	}
 	
 	public boolean isSleeping() {
 		return ((Boolean)this.getDataManager().get(IS_SLEEPING)).booleanValue();
 	}
 	
 	public void setSleeping(boolean state) {
 		this.getDataManager().set(IS_SLEEPING, state);
 	}
 	
 	public void setDecomposeTime(int time) {
 		this.getDataManager().set(DECOMPOSE_TIME, time);
 	}
 	
 	public int getDecomposeTime() {
 		return this.getDataManager().get(DECOMPOSE_TIME);
 	}
 	
 	public void tickDecomposition() {
 		setDecomposeTime(getDecomposeTime()-1);
 		this.dataManager.setDirty(DECOMPOSE_TIME);
 	}
 	
 	
 	/*
 	 * END SETTERS AND GETTERS
 	 */
 	
 	public void attemptToRally() {
 		
 		if(!shouldTickRally) {
 			startTickingRallyTimer();
 		}
 	}
 	
 	public void startTickingRallyTimer() {
 		this.shouldTickRally = true;
 	}
 	
 	public void checkSight() {
 		EntityPlayer ent = this.world.getNearestAttackablePlayer(this.getPosition(), 10, 10);
 		if(ent == null) return;
 		Vec3d resultantVector = ent.getPositionVector().subtract(this.getPositionVector()).normalize();
    	double vecAngle = Math.toDegrees(VectorUtil.angleBetweenVec(this.getLookVec(), resultantVector));
    	
    	if(vecAngle < 80 && this.watcher == null) {
    		alertNoise(ent.getPositionVector(), ent);
    	//	((EntityBaseZombie) this).startStalking(ent);
    		this.watcher = ent;
        }
 	}
 	
 	public void rallyAround(EntityLivingBase targetEntity) {
 		
 		Main.NETWORK.sendToAllTracking(new ZombieScreamPacket(this.getEntityId(), 1, 1), this);
    	List<EntityBaseZombie> zombieList = this.world.getEntities(EntityBaseZombie.class, EntitySelectors.IS_ALIVE);
    	for(int x = 0; x < zombieList.size(); ++x) {
    		EntityBaseZombie zombie= zombieList.get(0);
    		if(zombie.getPositionVector().distanceTo(getPositionVector()) > 50) continue;
    		if(zombie.getAttackTarget() == null) {
    			zombieList.get(x).setAttackTarget(targetEntity);
    		}
    	
    		
    	}
 	}
 	
 	
 	
 	
 	public void checkSleep() {
 		if(!this.world.isRemote) {
			if(this.getBrightness() >  0.55F && !this.isSleeping() && this.getAttackTarget() == null) {
				this.setSleeping(true);
				this.setNoAI(true);
				int r = RandomNumberTool.getRandomIntBetween(3, 6);
				setMovementState(r);
			} else if(this.isSleeping() && this.getBrightness() < 0.55) {
				this.setSleeping(false);
				this.setNoAI(false);
				setMovementState(0);
			}
		}
 	}

	
 	public void alertNoise(Vec3d positionOfNoise, EntityPlayer target) {
 		// WARNING: Server side only.
 		
 		if(getAttackTarget() != null) {
 			System.out.println("Cancelled! Zombie is already targeting " + getAttackTarget());
 			return;
 		}
 		
 		
 		makeAngry();
 		getDataManager().setDirty(IS_ANGRY);
 		
 		setAttackTarget(target);
 		startStalking(target);
 		
 	}
 	
 	
 	public void startStalking(EntityLivingBase ent) {
 		//System.out.println("hi " + ent);
 		this.watcher = ent;
 	}
 	
 	public void updateStalking() {
		
 		if(this.getAttackTarget() != null) {
 			this.watcher = null;
 			return;
 		}
 		
		if(this.watcher != null) {
			//System.out.println(getEntitySenses().canSee(this.watcher));
			/*
			if(!getEntitySenses().canSee(this.watcher)) {
				this.losTimer = 0;
				this.watcher = null;
				return;
			}*/
			
			this.losTimer += 1;
			if(this.losTimer > 40) {
				//System.ou
				//System.out.println("activated");
				if(!this.world.isRemote) {
					this.setAttackTarget(this.watcher);
					//setAttackTarget(this.watcher);
					//System.out.println(this.getAttackTarget());
				}
				
				
				
				this.losTimer = 0;
				this.watcher = null;
				if(getAttackTarget() == null) {
        			attemptToRally();
        		}
				//setAttackTarget(this.watcher);
            	calmDown();
			}
		}
 	}
 	
 
 	
 	public void checkIsInVision() {
 		
 	}
 	@Override
 	public void setDead() {
 		super.setDead();
 	}
 	
 	@Override
 	public boolean canBeCollidedWith() {
 		if(isHeadBlownOff()) return false;
 		return !isCorpse();
 	}
 	
 
 	
 	public void putToDeathState(EnumFacing facing) {
 		int ran = 0;
 		if(facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
 			ran = RandomNumberTool.getRandomIntBetween(0, 2);
 		} else {
 			ran = RandomNumberTool.getRandomIntBetween(0, 1);
 		}
 		
 		setDeathAnimationSelector(ran);
 		setDeathDirection(facing);
 		setEntityInvulnerable(true);
 		setNoAI(true);
 		setIsCorpse(true);
 		this.dataManager.setDirty(IS_CORPSE);
 		this.dataManager.setDirty(DEATH_DIRECTION);
 		this.dataManager.setDirty(DEATH_ANIM_SELECTOR);
 	}
 	
 	/*
 	 public boolean isCorpse() {
 		return this.dataManager.get(IS_CORPSE).booleanValue();
 	}
 	
 	public void setIsCorpse(boolean state) {
 		this.dataManager.set(IS_CORPSE, state);
 	}
 	 */
 	public boolean isHeadBlownOff() {
		return this.dataManager.get(IS_HEAD_BOOM).booleanValue();
 		
 	}
 	
 	public void boomHead() {
 		
 		if(world.isRemote) return;
 		
 		setHeadBlownOff(true);
 		this.playSound(SoundsHandler.GHOULHEADSHOT, 2.0F, 1);
 		this.dataManager.setDirty(IS_HEAD_BOOM);
 		Main.NETWORK.sendToAllTracking(new BloodExplosionPacket(this.getEntityId(), posX, posY+getEyeHeight(), posZ, 2.0), this);
 	}
 	
 	@Override
 	public EntityLivingBase getAttackTarget() {
 		// TODO Auto-generated method stub
 		return super.getAttackTarget();
 	}
 	
 	
 	
 	public void setHeadBlownOff(boolean state) {
 		this.dataManager.set(IS_HEAD_BOOM, state);
 	}
 	
 	
 	@Override
 	public void writeEntityToNBT(NBTTagCompound compound) {
 		compound.setBoolean("isCorpse", isCorpse());
 		compound.setBoolean("isHeadBlownOff", isHeadBlownOff());
 	
 		super.writeEntityToNBT(compound);
 	}
 	
 	@Override
 	public void readEntityFromNBT(NBTTagCompound compound) {
 		setIsCorpse(compound.getBoolean("isCorpse"));
 		
 		setHeadBlownOff(compound.getBoolean("isHeadBlownOff"));
 		super.readEntityFromNBT(compound);
 	}
 	
 	
 	/*
 	 * SOUNDS
 	 */
 	
 	@Override
 	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
 		return SoundsHandler.GHOULHURT;
 	}
 	
 	@Override
 	protected SoundEvent getAmbientSound() {
 		int ran = RandomNumberTool.getRandomIntBetween(0, 2);
 		switch(ran) {
	 		case 0:
	 			return SoundsHandler.GHOULMOAN1;
	 		case 1: 
	 			return SoundsHandler.GHOULMOAN1;
	 		case 2:
	 			return SoundsHandler.GHOULMOAN1;
 		}
		return null;
 	}
 	
 	public void updateWalking() {
 		double lenSq = this.getPositionVector().lengthSquared();
		if(lenSq != this.previousPos) {
			isWalking = true;
		} else {
			isWalking = false;
		}
		this.previousPos = lenSq;
 	}
 	
 	
 	
	@Override
	public void onUpdate() {
		
		// common code
		this.rotationYaw = this.rotationYawHead;
		updateWalking();
		checkSight();
		if(watcher != null) {
			if(!watcher.isEntityAlive()) {
				watcher = null;
			}
		//	updateStalking();
		}
		
		
		
		
		
		// Run serverside code
		if(!world.isRemote) {
			
			// tick the decomposition
			if(isCorpse()) {
				tickDecomposition();
			}
			
			// if they are done decomposing, kill them
			if(getDecomposeTime() <= 0) {
				setDead();
			}
		}
		
		
		//System.out.println(getAttackTarget());
		
		
		
		
		

		
		// Run living code
		if(isCorpse()) return;
		
		checkSleep();
		
		
		if(this.world.isRemote) {
			
			if(queuedScream) {
				this.playSound(SoundsHandler.ZOMBIESCREAM, 1, 1);
				queuedScream = false;
			}
			
		}
		
		
	
		/*
		if(shouldTickRally && !this.world.isRemote) {
			this.rallyTimer += 1;
			if(this.rallyTimer > 50) {
				this.rallyTimer = 0;
				shouldTickRally = false;
				rallyAround(getAttackTarget());
			}
		}*/
		
		
		super.onUpdate();
	
		
		if(this.watcher != null) {
			this.getLookHelper().setLookPosition(this.watcher.posX, this.watcher.posY + (double)this.watcher.getEyeHeight(), this.watcher.posZ, (float)this.getHorizontalFaceSpeed(), (float)this.getVerticalFaceSpeed());
		}
		   
		
		if(isAngry()) {
			tick++;
			angerTimer++;
		}

		if(angerTimer > 40) {
			angerTimer = 0;
			tick = 0;
			if(getAttackTarget() != null && !world.isRemote) rallyAround(getAttackTarget());
			calmDown();
		}
	}



	

	

}
