package com.vicmatskiv.weaponlib.vehicle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EFX10;

import com.google.common.collect.Lists;
import com.vicmatskiv.weaponlib.Configurable;
import com.vicmatskiv.weaponlib.EntityClassFactory;
import com.vicmatskiv.weaponlib.animation.Randomizer;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMovingSound;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePositionedSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;
import com.vicmatskiv.weaponlib.compatibility.RevSound;
import com.vicmatskiv.weaponlib.compatibility.sound.AdvCompatibleMovingSound;
import com.vicmatskiv.weaponlib.particle.DriftSmokeFX;
import com.vicmatskiv.weaponlib.state.ExtendedState;
import com.vicmatskiv.weaponlib.vehicle.collisions.GJKResult;
import com.vicmatskiv.weaponlib.vehicle.collisions.OBBCollider;
import com.vicmatskiv.weaponlib.vehicle.collisions.OreintedBB;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Engine;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.engines.EvoIVEngine;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.solver.VehiclePhysicsSolver;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.libraries.SourceLWJGLOpenAL;
import scala.reflect.internal.Trees.This;

public class EntityVehicleOld extends Entity implements Configurable<EntityVehicleConfiguration>, ExtendedState<VehicleState>
{
    private static enum DriverInteractionEvent {
        NONE, ENTER, EXIT, DRIVING, OUT
    }
    
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.<Integer>createKey(EntityBoat.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.<Integer>createKey(EntityBoat.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.<Float>createKey(EntityBoat.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> BOAT_TYPE = EntityDataManager.<Integer>createKey(EntityBoat.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean>[] DATA_ID_PADDLE = new DataParameter[] {EntityDataManager.createKey(EntityBoat.class, DataSerializers.BOOLEAN), EntityDataManager.createKey(EntityBoat.class, DataSerializers.BOOLEAN)};
    private final float[] paddlePositions;
    /** How much of current speed to retain. Value zero to one. */
    private float momentum;
    private float outOfControlTicks;
    private float deltaRotation;
    private int lerpSteps;
    private double boatPitch;
    private double lerpY;
    private double lerpZ;
    private double boatYaw;
    private double lerpXRot;
    private boolean leftInputDown;
    private boolean rightInputDown;
    private boolean forwardInputDown;
    private boolean backInputDown;
    private double waterLevel;
    /**
     * How much the boat should glide given the slippery blocks it's currently gliding over.
     * Halved every tick.
     */
    private float boatGlide;
    private EntityVehicleOld.Status status;
    private EntityVehicleOld.Status previousStatus;
    private double lastYd;
    
    private double inclineX;
    private double inclineZ;
    
    private double speed;
    
    private EntityVehicleConfiguration configuration;
    
    private VehicleState vehicleState = VehicleState.IDLE;
    private VehicleDrivingAspect drivingAspect = new VehicleDrivingAspect();
    private long stateUpdateTimestamp;
    private double lastYawDelta;
    private Randomizer randomizer;
    
    private DriverInteractionEvent driverInteractionEvent = DriverInteractionEvent.NONE;
    private boolean wasPreviouslyRidden;
    
    private MovingSound drivingSound;
    private MovingSound idleSound;
    private MovingSound constantRev;
    
    private MovingSound rev1;
    private MovingSound rev2;
    private MovingSound rev3;
    private MovingSound rev4;
    private MovingSound rev5;
    private MovingSound rev6;
    
    
    public OreintedBB obb;
    
    private int enterSoundDelay = this.getSoundLoopDelay();
    
    private float wheelRotationAngle;
    
    private Supplier<CompatibleVec3> soundPositionProvider = () -> new CompatibleVec3(posX, posY, posZ);
    
    private Supplier<Float> drivingSoundVolumeProvider = () -> {
        float volume;
        if (isBeingRidden() && enterSoundDelay <= 0) {
            //double vehicleSpeed = (double) MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            
            double averageSpeed = speed; //interpolatedSpeed.getAverage();
            if(averageSpeed > 0) {
                averageSpeed = Math.log10(averageSpeed * 20) / 2;
            }
            
            volume = (float) averageSpeed * 0.9F * ((float) Math.abs(enterSoundDelay) / 20f);
//            System.out.println("Sound volume: " + volume + ", speed: " + speed);
        } else {
            volume = 0.0f;
        }
        return volume;
    };

    private Supplier<Boolean> donePlayingSoundProvider = () -> this.isDead;
    
    private Supplier<Float> idleSoundVolumeProvider = () -> {
        float volume;
        //if (riddenByEntity != null && enterSoundDelay <= 0) {
        volume = (float) (speed < 0.09 ? 0.07f : 0f) * ((float) Math.abs(enterSoundDelay) / 20f);
        return volume;
    };

    public EntityVehicleOld(World worldIn)
    {
        super(worldIn);
        this.paddlePositions = new float[2];
        this.preventEntitySpawning = true;
        this.setSize(1.375F, 0.5625F);
        this.obb = new OreintedBB(getConfiguration().getAABBforOBB());
    }

    public EntityVehicleOld(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }
    
    
    
    @Override
    public EntityVehicleConfiguration getConfiguration() {
        if(configuration == null) {
            configuration = (EntityVehicleConfiguration) EntityClassFactory.getInstance().getConfiguration(getClass());
        }
        return configuration;
    }
    
    protected int getSoundLoopDelay() {
        return 10;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataManager.register(TIME_SINCE_HIT, Integer.valueOf(0));
        this.dataManager.register(FORWARD_DIRECTION, Integer.valueOf(1));
        this.dataManager.register(DAMAGE_TAKEN, Float.valueOf(0.0F));
        this.dataManager.register(BOAT_TYPE, Integer.valueOf(EntityBoat.Type.OAK.ordinal()));

        for (DataParameter<Boolean> dataparameter : DATA_ID_PADDLE)
        {
            this.dataManager.register(dataparameter, Boolean.valueOf(false));
        }
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
    }

    /**
     * Returns the collision bounding box for this entity
     */
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return 0.3; //-0.1D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (!this.world.isRemote && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource()))
            {
                return false;
            }
            else
            {
                this.setForwardDirection(-this.getForwardDirection());
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
                this.setBeenAttacked();
                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode;

                if (flag || this.getDamageTaken() > 40.0F)
                {
                    if (!flag && this.world.getGameRules().getBoolean("doEntityDrops"))
                    {
//                        this.dropItemWithOffset(this.getItemBoat(), 1, 0.0F);
                    }

                    this.setDead();
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    public void applyEntityCollision(Entity entityIn)
    {
        if (entityIn instanceof EntityBoat)
        {
            if (entityIn.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY)
            {
                super.applyEntityCollision(entityIn);
            }
        }
        else if (entityIn.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY)
        {
            super.applyEntityCollision(entityIn);
        }
    }

//    public Item getItemBoat()
//    {
//        switch (this.getBoatType())
//        {
//            case OAK:
//            default:
//                return Items.BOAT;
//            case SPRUCE:
//                return Items.SPRUCE_BOAT;
//            case BIRCH:
//                return Items.BIRCH_BOAT;
//            case JUNGLE:
//                return Items.JUNGLE_BOAT;
//            case ACACIA:
//                return Items.ACACIA_BOAT;
//            case DARK_OAK:
//                return Items.DARK_OAK_BOAT;
//        }
//    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @SideOnly(Side.CLIENT)
    public void performHurtAnimation()
    {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Set the position and rotation values directly without any clamping.
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.boatPitch = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.boatYaw = (double)yaw;
        this.lerpXRot = (double)pitch;
        this.lerpSteps = 10;
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into
     * account.
     */
    public EnumFacing getAdjustedHorizontalFacing()
    {
        return this.getHorizontalFacing().rotateY();
    }
    //
    public VehiclePhysicsSolver solver;
    public double mass = 1352;
    
    int secondStepper = 0;
    
    // marco implementation
    public double forwardLean = 0.0;
    public double sideLean = 0.0;
    
    public double driftTuner = 0.0;
   
    
    
    public Vector2d velocity_wc = new Vector2d();
	public Vector2d position_wc = new Vector2d();
	public double angularvelocity = 0;
	public double angle = 0;
    public boolean isBraking = false;
    public double throttle = 0;
    public int brake = 0;
    public double steerangle;
    public Engine engine = new EvoIVEngine("Evo IV Engine", "Mitsubishi Motors");
    
    public float baseFOV = -1;
    public float oldFOV = 0;
    
    

    
    
    
    /**
     * FOR JIM IMPLEMENTATION
     */
    
    @Override
    public void onUpdate() {
    	//realonUpdate();
    	
    	//if(1+1==2) return;

    	updateOBB();
    	
    	// test collision
    	List<EntityPlayer> entList = this.world.playerEntities;
    	EntityPlayer p = entList.get(0);
    	
    	OreintedBB pB = new OreintedBB(p.getEntityBoundingBox());
    	pB.setPosition(p.posX, p.posY, p.posZ);
    	
    	if(this.obb != null && pB != null) {
    		//System.out.println("hi");
    		try {
    			
    			
    			AxisAlignedBB abst = new AxisAlignedBB(-1, -1, -1, 1, 1, 1);
    			AxisAlignedBB vehicle = new AxisAlignedBB(-3.5, -2.5, -1.75, 3.5, 2.5, 1.75);
    			OreintedBB o1 = new OreintedBB(abst);
    			OreintedBB o2 = new OreintedBB(vehicle);
    			
    			o2.setRotation(0.0, 0.0, Math.toRadians(45));
    			
    			o1.setPosition(p.posX, p.posY, p.posZ);
    			o2.setPosition(posX, posY, posZ);
    			
    			GJKResult result = OBBCollider.areColliding(o1, this.obb);
    			//GJKResult result = OBBCollider.areColliding(this.obb, pB);
    			if(result.status == GJKResult.Status.COLLIDING) {
    				Vec3d sep = result.separationVector.scale(result.penetrationDepth).scale(-1.1);
    				//p.move(MoverType.SELF, sep.x, sep.y, sep.z);
    			}
    			
    			
    			System.out.println(result.status);
    			
    			//System.out.println(this.obb.c + " | " + pB.c);
    			//System.out.println(this.getPositionVector() + " | " + p.getPositionVector());
    			
    			//System.out.println(result.status);
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.out.println("fuck");
    		}
    		//
        	
    	}
    	
    	
    	
    	
    	// end test
    	
    	
    	
    	
  
    	
    	
    	if(!this.world.isRemote) return;
    	
    	//setState(VehicleState.DRIVING);
    	
    	if(solver == null) {
    		//solver = new VehiclePhysicsSolver(this, 1352);
    	}
    	
    	// update steering
    	if(!this.isBeingRidden()) return;
		Entity player = getPassengers().get(0);
		if(player == null) {
			return;
		}
    	
    	
    	// ONLY ON CLIENT
    	float currentFOV = Minecraft.getMinecraft().gameSettings.fovSetting;
    	if(baseFOV == -1) {
    		baseFOV = currentFOV;
    	}
    	
    	
    	
    	float newFOV = (float) (baseFOV + (2.5*solver.getSyntheticAcceleration()));
    	
    	float interpFOV = currentFOV + (newFOV-currentFOV)*Minecraft.getMinecraft().getRenderPartialTicks();
    	
    	Minecraft.getMinecraft().gameSettings.fovSetting = interpFOV;
    	oldFOV = newFOV;
    	
    	
    	
    	//
    	
    	updateDriverInteractionEvent();
    	this.previousStatus = this.status;
    	
    	
    	
    	
		
		
		
		
		
		//setState(VehicleState.DRIVING);
		
		
		float yaw = player.rotationYaw;
		float pitch = player.rotationPitch;
		float f = 1.0F;
		double motionX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
		double motionY = (double)(-MathHelper.sin((pitch) / 180.0F * (float)Math.PI) * f);
		Vec3d dirVec = new Vec3d(motionX, 0, motionZ);
		
		
		Vec3d oreintVec = Vec3d.fromPitchYaw(this.rotationPitch, this.rotationYaw);
		
		
		double det = dirVec.crossProduct(oreintVec).y;
		if(det > 0) {
			det = 1;
		} else {
			det = -1;
		}
		Vector3d dir = new Vector3d(dirVec.x, dirVec.y, dirVec.z);
		Vector3d ore = new Vector3d(oreintVec.x, oreintVec.y, oreintVec.z);
		double aT = Math.toDegrees(dir.angle(ore))/2;
		double steeringAngle = aT*det*-1;
		if(aT < -45.0F) {
			aT = -45.0F;
		}
		if(aT > 45.0F) {
			aT = 45.0F;
		}
		
		
		steerangle = Math.toRadians(-steeringAngle);
    	
		
    	
    	
    	
    	
    	
    	//
    	if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if( throttle < 1) throttle += 0.1;
		}  else {
			if(throttle > 0) throttle -= 0.1;
		}
    	
    	super.onUpdate();
    	tickLerp();
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if( throttle >= 0) throttle -= 0.1;
			isBraking = true;
		} else isBraking = false;
		if(throttle < 0) throttle = 0;
		if(throttle > 1) throttle = 1;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			solver.applyHandbrake();
		} else {
			solver.releaseHandbrake();
		}
		
		steerangle *= 0.5;
		
		
		int mA = 45;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			 driftTuner -= 5;
		      if(driftTuner < -mA) driftTuner = -mA;
		} else if( Keyboard.isKeyDown(Keyboard.KEY_D) )
	    {
	       driftTuner += 5;
	       if(driftTuner > mA) driftTuner = mA;
	    } else {
	    	double newTune = ((Math.abs(driftTuner) - 5))*Math.signum(driftTuner);
			driftTuner = newTune;
			 if(driftTuner < -mA) driftTuner = -mA;
			
	    }
		
		
		/*
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
	     if( steerangle > - Math.PI/5.0 ) steerangle -= Math.PI/64.0;
		} else if( Keyboard.isKeyDown(Keyboard.KEY_D) )
	    {
	       if( steerangle <  Math.PI/5.0 ) steerangle += Math.PI/64.0;
	    } */
		
		wheelRotationAngle -= (float) solver.velocity.lengthVector();
		
		lastYawDelta = Math.toDegrees(steerangle)*0.3;
		
		this.doBlockCollisions();
		
    	//
    	
		//drivingAspect.onUpdate(this);
        getSuspensionStrategy().update(speed, lastYawDelta);
        handleEngineSound();
        //handleLoopingSoundEffects();
		//handleServerSounds();
        for(int x = 0; x < 5; ++x) {
        	solver.updatePhysics();
        	
        	
        }
        
        //setState(VehicleState.STARTING_TO_DRIVE);
        
        secondStepper += 1;
    	if(secondStepper > 4) {
    		secondStepper = 0;
    	}
    
    	
    	doDriveParticles();
    	
    	
    } 
    
    public void notifyStopRevSound() {
    	this.drivingSound = null;
    }
    
    public void notifyConstantRevStop() {
    	this.constantRev = null;
    }
    
    
    
    private static int reverb0 = -69;
    private static int auxFXSlot0 = -69;
    public void handleEngineSound() {
    	
    	// openAl stuff
    	if(reverb0 == -69) {
    		reverb0 = EFX10.alGenEffects();
        	EFX10.alEffecti(reverb0, EFX10.AL_EFFECT_TYPE, EFX10.AL_EFFECT_EAXREVERB);
    	}
    	
    	if(auxFXSlot0 == -69) {
    		auxFXSlot0 = EFX10.alGenAuxiliaryEffectSlots();
    		EFX10.alAuxiliaryEffectSloti(auxFXSlot0, EFX10.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL10.AL_TRUE);
    	}
    	
    	//System.out.println(reverb0 + " | " + auxFXSlot0);
    	float decayTime = 4.15f;
    	float density = 0.0f;
    	float diffusion = 1.0f;
    	float gain = 0.2f * 0.85f;
    	float gainHF = 0.99f;
    	float decayHFRatio = 0.6f;
    	float reflectionsGain = 2.5f;
    	float reflectionsDelay = 0.001f;
    	float lateReverbGain = 1.26f;
    	float lateReverbDelay = 0.011f;
    	float airAbsorptionGainHF = 0.994f;
    	float roomRolloffFactor = 0.16f;
    	float echoTime = 0.090f;
    	float echoDepth = 0.1f;
		
    	EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_DENSITY, density);
    	
    	EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_DIFFUSION, diffusion);
    	
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_GAIN, gain);
		
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_GAINHF, gainHF);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_DECAY_TIME, decayTime);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_DECAY_HFRATIO, decayHFRatio);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_REFLECTIONS_GAIN, reflectionsGain);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_LATE_REVERB_GAIN, lateReverbGain);
		
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_LATE_REVERB_DELAY, lateReverbDelay);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, airAbsorptionGainHF);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_ROOM_ROLLOFF_FACTOR, roomRolloffFactor);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_ECHO_TIME, echoTime);
		EFX10.alEffectf(reverb0, EFX10.AL_EAXREVERB_ECHO_DEPTH, echoDepth);


    	EFX10.alAuxiliaryEffectSloti(auxFXSlot0, EFX10.AL_EFFECTSLOT_EFFECT, reverb0);
    	
    	//
    	
    	
    	SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
        CompatibleSound drivingSound = getConfiguration().getRunSound();
        CompatibleSound idleSound = getConfiguration().getRunSound();
        CompatibleSound constantRev = getConfiguration().getConstantRev();
        
        /*
        CompatibleSound rev1 = getConfiguration().getRevSound(1);
        CompatibleSound rev2 = getConfiguration().getRevSound(2);
        CompatibleSound rev3 = getConfiguration().getRevSound(3);
        CompatibleSound rev4 = getConfiguration().getRevSound(4);
        CompatibleSound rev5 = getConfiguration().getRevSound(5);
        CompatibleSound rev6 = getConfiguration().getRevSound(6);
       	*/
        /*
        if (this.idleSound == null && driverInteractionEvent == DriverInteractionEvent.DRIVING 
                && getState() == VehicleState.IDLE
                && idleSound != null && soundHandler != null) {
        	
            this.idleSound = new AdvCompatibleMovingSound(idleSound, soundPositionProvider, idleSoundVolumeProvider, donePlayingSoundProvider, this);
            
            soundHandler.playSound(this.idleSound);
        }*/
        
        int rpm = solver.currentRPM;
        
        /*
       
        if(this.rev1 != null) if(this.rev1.isDonePlaying()) this.rev1 = null;
        if(this.rev2 != null) if(this.rev2.isDonePlaying()) this.rev2 = null;
        if(this.rev3 != null) if(this.rev3.isDonePlaying()) this.rev3 = null;
        if(this.rev4 != null) if(this.rev4.isDonePlaying()) this.rev4 = null;
        if(this.rev5 != null) if(this.rev5.isDonePlaying()) this.rev5 = null;
        if(this.rev6 != null) if(this.rev6.isDonePlaying()) this.rev6 = null;
        */
        
        
        if(this.rev1 != null) if(this.rev1.isDonePlaying()) this.rev1 = null;
        if(this.rev2 != null) if(this.rev2.isDonePlaying()) this.rev2 = null;
        if(this.drivingSound != null) {
        	 if(this.drivingSound.isDonePlaying()) this.drivingSound = null;
        }
        /*
        if(this.constantRev != null) {
        	if(this.constantRev.isDonePlaying()) this.constantRev = null;
        }
        
        if(this.idleSound != null) {
        	if(this.idleSound.isDonePlaying()) this.idleSound = null;
        } */
        
        //this.idleSound = null;
        //this.drivingSound = null;
        
        /*
        
        if(this.rev1 == null && rpm > 1000 && rpm <= 2000) {
        	this.rev1 = new AdvCompatibleMovingSound(rev1, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 1000, 2000);
            soundHandler.playSound(this.rev1);
        }
        
        if(this.rev2 == null && rpm > 2000 && rpm <= 3000) {
        	this.rev2 = new AdvCompatibleMovingSound(rev2, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 2000, 3000);
            soundHandler.playSound(this.rev2);
        }
        
        if(this.rev3 == null && rpm > 3000 && rpm <= 4000) {
        	this.rev3 = new AdvCompatibleMovingSound(rev3, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 3000, 4000);
            soundHandler.playSound(this.rev3);
        }
        
        if(this.rev4 == null && rpm > 4000 && rpm <= 5000) {
        	this.rev4 = new AdvCompatibleMovingSound(rev4, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 4000, 5000);
            soundHandler.playSound(this.rev4);
        }
        
        if(this.rev5 == null && rpm > 5000 && rpm <= 6000) {
        	this.rev5 = new AdvCompatibleMovingSound(rev5, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 5000, 6000);
            soundHandler.playSound(this.rev5);
        }
        
        if(this.rev6 == null && rpm > 6000 && rpm <= 7000) {
        	this.rev6 = new AdvCompatibleMovingSound(rev6, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 6000, 7000);
            soundHandler.playSound(this.rev6);
        }
        */
       // System.out.println(rpm);
        if(this.drivingSound == null) {
        //	this.drivingSound = new AdvCompatibleMovingSound(drivingSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 1000, 7000);
            soundHandler.playSound(this.drivingSound);
        }
        
        if(this.rev1 == null && rpm > 3500) {
        //	this.rev1 = new AdvCompatibleMovingSound(drivingSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 3500, 7000);
            soundHandler.playSound(this.rev1);
        }
        
        if(this.rev2 == null && rpm > 4500) {
        //	this.rev2 = new AdvCompatibleMovingSound(drivingSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 4500, 7000);
            soundHandler.playSound(this.rev2);
        }
        
        
       /*
        
        if(this.idleSound == null && rpm <= 2000 && throttle == 0) {
        	this.idleSound = new AdvCompatibleMovingSound(idleSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 2);
            soundHandler.playSound(this.idleSound);
        }
        
        if(this.drivingSound == null && rpm > 2000 && rpm < 6950 && throttle != 0) {
        	this.drivingSound = new AdvCompatibleMovingSound(drivingSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 0);
            soundHandler.playSound(this.drivingSound);
        }
        
        if(this.constantRev == null && rpm > 2000 && rpm > 6950) {
        	
        	this.constantRev = new AdvCompatibleMovingSound(constantRev, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this, 1);
            soundHandler.playSound(this.constantRev);
        }
        */
        
       //soundHandler.playSound(new PositionedSoundRecord(drivingSound.getSound(), SoundCategory.BLOCKS, 1.0F, 1.0F, this.getPosition()));
        /*
        if (this.drivingSound == null && driverInteractionEvent == DriverInteractionEvent.DRIVING 
                && drivingSound != null && soundHandler != null && rpm < 6500) {
        	
            this.drivingSound = new AdvCompatibleMovingSound(drivingSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider, this);
            soundHandler.playSound(this.drivingSound);
        }
        */
        if(rpm > 6500) {
        	
        }
        
        if (isBeingRidden() && this.enterSoundDelay > -20) {
            --this.enterSoundDelay;
        }

        if (!isBeingRidden() && this.enterSoundDelay != this.getSoundLoopDelay()) {
            this.enterSoundDelay = this.getSoundLoopDelay();
        }
    }
    
    
    /**
     * Called to update the entity's position/logic.
     */
    public void realonUpdate()
    {
        
        updateDriverInteractionEvent();
        
        this.previousStatus = this.status;
        this.status = this.getBoatStatus();

        if (this.status != EntityVehicleOld.Status.UNDER_WATER && this.status != EntityVehicleOld.Status.UNDER_FLOWING_WATER)
        {
            this.outOfControlTicks = 0.0F;
        }
        else
        {
            ++this.outOfControlTicks;
        }

        if (!this.world.isRemote && this.outOfControlTicks >= 60.0F)
        {
            this.removePassengers();
        }

        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        super.onUpdate();
        
//        System.out.println("Rendering pitch post super.onUpdate " + rotationPitch);
        
        this.tickLerp();
        
//        System.out.println("Rendering pitch post tickLerp " + rotationPitch);


        if (this.canPassengerSteer())
        {
            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof EntityPlayer))
            {
                this.setPaddleState(false, false);
            }

            this.updateMotion();

            if (this.world.isRemote)
            {
                this.controlBoat();
              // this.world.sendPacketToServer(new CPacketSteerBoat(this.getPaddleState(0), this.getPaddleState(1)));
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
//            System.out.println("Rendering pitch post move " + rotationPitch);
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
        }

        for (int i = 0; i <= 1; ++i)
        {
            if (this.getPaddleState(i))
            {
                if (!this.isSilent() && (double)(this.paddlePositions[i] % ((float)Math.PI * 2F)) <= (Math.PI / 4D) && ((double)this.paddlePositions[i] + 0.39269909262657166D) % (Math.PI * 2D) >= (Math.PI / 4D))
                {
                    SoundEvent soundevent = this.getPaddleSound();

                    if (soundevent != null)
                    {
                        Vec3d vec3d = this.getLook(1.0F);
                        double d0 = i == 1 ? -vec3d.z : vec3d.z;
                        double d1 = i == 1 ? vec3d.x : -vec3d.x;
                        this.world.playSound((EntityPlayer)null, this.posX + d0, this.posY, this.posZ + d1, soundevent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.rand.nextFloat());
                    }
                }

                this.paddlePositions[i] = (float)((double)this.paddlePositions[i] + 0.39269909262657166D);
            }
            else
            {
                this.paddlePositions[i] = 0.0F;
            }
        }

        this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.getTeamCollisionPredicate(this));

        if (!list.isEmpty())
        {
            boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity = list.get(j);

                if (!entity.isPassenger(this))
                {
                    if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer))
                    {
                        entity.startRiding(this);
                    }
                    else
                    {
                        //this.applyEntityCollision(entity);
                    }
                }
            }
        }
        
        if(this.world.isRemote) {
          //  drivingAspect.onUpdate(this);
            getSuspensionStrategy().update(speed, lastYawDelta);
            handleLoopingSoundEffects();
            
//            Entity driver = this.getControllingPassenger();
//            if (driver instanceof EntityPlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
//                driver.rotationPitch = this.rotationPitch;
//            }
        } else {
            handleServerSounds();
        }
        
//        System.out.println("Rendering pitch post onUpdate " + rotationPitch);

    }
    
    @Override
    protected void doBlockCollisions() {
    	
    	super.doBlockCollisions();
    }
    
    
    public void doDriveParticles() {
    	
    	
    	Vec3d partDir = new Vec3d(-steerangle*20, 0.3, -1).rotateYaw((float) Math.toRadians(-rotationYaw)).scale(0.1);
    	Vec3d posDir = new Vec3d(0, 0, -1).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
//    	/System.out.println(partDir);
    	
    	
    	// immersion particles
    	
    	
    	
    	
    	
    	
    	// exhaust particles
    	Vec3d posExhaust = new Vec3d(0.2, 0.2, -2.5).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
    	Vec3d partDirExhaust = new Vec3d(0.0, 0.3, -1).rotateYaw((float) Math.toRadians(-rotationYaw)).scale(0.1);
    	for(int x = 0; x < 2+(solver.synthAccelFor); ++x) {
    		this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posExhaust.x, posExhaust.y, posExhaust.z, partDirExhaust.x, partDirExhaust.y, partDirExhaust.z, Block.getStateId(world.getBlockState(this.getPosition().down())));   
    	}
    	
    	/* pop particles
    	int ranChance = (int) Math.floor(Math.random()*(100-0+1)+0);
    	if(ranChance < 5) {
    		for(int x = 0; x < 2+(solver.synthAccelFor); ++x) {
    			double gaus = rand.nextGaussian()/2;
        		this.world.spawnParticle(EnumParticleTypes.FLAME, posExhaust.x, posExhaust.y, posExhaust.z, partDirExhaust.x+gaus, partDirExhaust.y+gaus, partDirExhaust.z+gaus, Block.getStateId(world.getBlockState(this.getPosition().down())));   
        	}
    	}*/
    	
    	
    	// drift particles
    	if(solver.rearAxel.isHandbraking) {
    		Random rand = new Random();
        	for(int x = 0; x < 4; ++x) {
        		double gaus = rand.nextGaussian()/2;
        		//this.world.spawnParticle(EnumParticleTypes.CLOUD, posDir.x+gaus,  posDir.y+gaus,  posDir.z+gaus, partDir.x, partDir.y, partDir.z, Block.getStateId(world.getBlockState(this.getPosition().down())));   
        		Minecraft.getMinecraft().effectRenderer.addEffect(new DriftSmokeFX(this.world, posDir.x+gaus, posDir.y+gaus, posDir.z+gaus, partDir.x, partDir.y, partDir.z));
            	  
        	}
    	}
    	
    	// drive dust
    	for(int x = 0; x < ((int) solver.synthAccelFor); ++x) {
    		this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, posDir.x + rand.nextGaussian()/2, posDir.y+ rand.nextGaussian()/15, posDir.z+ rand.nextGaussian()/2, partDir.x, partDir.y+0.1, partDir.z, Block.getStateId(world.getBlockState(this.getPosition().down())));   
    	}
    	
    }
    
    
    public void handleLoopingSoundEffects() {

        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
        CompatibleSound drivingSound = getConfiguration().getRunSound();
        CompatibleSound idleSound = getConfiguration().getRunSound();

        if (this.drivingSound == null && driverInteractionEvent == DriverInteractionEvent.DRIVING 
                && getState() != VehicleState.IDLE
                && drivingSound != null && soundHandler != null) {
            //this.drivingSound = new CompatibleMovingSound(drivingSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider);
            soundHandler.playSound(this.drivingSound);
            soundHandler.playDelayedSound(this.drivingSound, 10);
        }
        
        if (this.idleSound == null && driverInteractionEvent == DriverInteractionEvent.DRIVING 
                && getState() == VehicleState.IDLE
                && idleSound != null && soundHandler != null) {
            this.idleSound = new CompatibleMovingSound(idleSound, soundPositionProvider, idleSoundVolumeProvider, donePlayingSoundProvider);
            soundHandler.playSound(this.idleSound);
        }
        
        if (isBeingRidden() && this.enterSoundDelay > -20) {
            --this.enterSoundDelay;
        }

        if (!isBeingRidden() && this.enterSoundDelay != this.getSoundLoopDelay()) {
            this.enterSoundDelay = this.getSoundLoopDelay();
        }
    }
    
    private void updateDriverInteractionEvent() {
        boolean isBeingRidden = isBeingRidden();
        if (isBeingRidden) {
            if(!wasPreviouslyRidden) {
                driverInteractionEvent = DriverInteractionEvent.ENTER;
            } else {
                driverInteractionEvent = DriverInteractionEvent.DRIVING;
            }
        } else {
            if(!wasPreviouslyRidden) {
                driverInteractionEvent = DriverInteractionEvent.OUT;
            } else {
                driverInteractionEvent = DriverInteractionEvent.EXIT;
            }
        }
        wasPreviouslyRidden = isBeingRidden;
    }
    
    private void handleServerSounds() {
        switch (driverInteractionEvent) {
        case ENTER:
            compatibility.playSoundAtEntity(this, getConfiguration().getEnterSound(), 1f, 1f);
            break;
        case EXIT:
            compatibility.playSoundAtEntity(this, getConfiguration().getExitSound(), 1f, 1f);
            break;
        default:
        }
    }

    @Nullable
    protected SoundEvent getPaddleSound()
    {
        switch (this.getBoatStatus())
        {
            case IN_WATER:
            case UNDER_WATER:
            case UNDER_FLOWING_WATER:
                return SoundEvents.ENTITY_BOAT_PADDLE_WATER;
            case ON_LAND:
                return SoundEvents.ENTITY_BOAT_PADDLE_LAND;
            case IN_AIR:
            default:
                return null;
        }
    }

    private void tickLerp()
    {
        if (this.lerpSteps > 0 && !this.canPassengerSteer())
        {
            double d0 = this.posX + (this.boatPitch - this.posX) / (double)this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.boatYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
//            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpXRot - (double)this.rotationPitch) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    public void setPaddleState(boolean left, boolean right)
    {
        this.dataManager.set(DATA_ID_PADDLE[0], Boolean.valueOf(left));
        this.dataManager.set(DATA_ID_PADDLE[1], Boolean.valueOf(right));
    }

    @SideOnly(Side.CLIENT)
    public float getRowingTime(int side, float limbSwing)
    {
        return this.getPaddleState(side) ? (float)MathHelper.clampedLerp((double)this.paddlePositions[side] - 0.39269909262657166D, (double)this.paddlePositions[side], (double)limbSwing) : 0.0F;
    }

    /**
     * Determines whether the boat is in water, gliding on land, or in air
     */
    private EntityVehicleOld.Status getBoatStatus()
    {
        EntityVehicleOld.Status entityboat$status = this.getUnderwaterStatus();

        if (entityboat$status != null)
        {
            this.waterLevel = this.getEntityBoundingBox().maxY;
            return entityboat$status;
        }
        else if (this.checkInWater())
        {
//            this.waterLevel = this.getEntityBoundingBox().maxY; // TODO: handle water level
            return EntityVehicleOld.Status.IN_WATER;
        }
        else
        {
            float f = this.getBoatGlide();

            if (f > 0.0F)
            {
                this.boatGlide = f;
                return EntityVehicleOld.Status.ON_LAND;
            }
            else
            {
                return EntityVehicleOld.Status.IN_AIR;
            }
        }
    }

    public float getWaterLevelAbove()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            label108:

            for (int k1 = k; k1 < l; ++k1)
            {
                float f = 0.0F;
                int l1 = i;

                while (true)
                {
                    if (l1 >= j)
                    {
                        if (f < 1.0F)
                        {
                            float f2 = (float)blockpos$pooledmutableblockpos.getY() + f;
                            return f2;
                        }

                        break;
                    }

                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        blockpos$pooledmutableblockpos.setPos(l1, k1, i2);
                        IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);

                        if (iblockstate.getMaterial() == Material.WATER)
                        {
                            f = Math.max(f, BlockLiquid.getBlockLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos));
                        }

                        if (f >= 1.0F)
                        {
                            continue label108;
                        }
                    }

                    ++l1;
                }
            }

            float f1 = (float)(l + 1);
            return f1;
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }
    }

    /**
     * Decides how much the boat should be gliding on the land (based on any slippery blocks)
     */
    public float getBoatGlide()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = MathHelper.floor(axisalignedbb1.minX) - 1;
        int j = MathHelper.ceil(axisalignedbb1.maxX) + 1;
        int k = MathHelper.floor(axisalignedbb1.minY) - 1;
        int l = MathHelper.ceil(axisalignedbb1.maxY) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
        int j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1;
        List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        float f = 0.0F;
        int k1 = 0;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int l1 = i; l1 < j; ++l1)
            {
                for (int i2 = i1; i2 < j1; ++i2)
                {
                    int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

                    if (j2 != 2)
                    {
                        for (int k2 = k; k2 < l; ++k2)
                        {
                            if (j2 <= 0 || k2 != k && k2 != l - 1)
                            {
                                blockpos$pooledmutableblockpos.setPos(l1, k2, i2);
                                IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);
                                iblockstate.addCollisionBoxToList(this.world, blockpos$pooledmutableblockpos, axisalignedbb1, list, this, false);

                                if (!list.isEmpty())
                                {
                                    f += iblockstate.getBlock().getSlipperiness(iblockstate, this.world, blockpos$pooledmutableblockpos, this);
                                    ++k1;
                                }

                                list.clear();
                            }
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }

        return f / (float)k1;
    }

    private boolean checkInWater()
    {
        if(true) return true;
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);

                        if (iblockstate.getMaterial() == Material.WATER)
                        {
                            float f = BlockLiquid.getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos);
                            this.waterLevel = Math.max((double)f, this.waterLevel);
                            flag |= axisalignedbb.minY < (double)f;
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }

        return flag;
    }

    /**
     * Decides whether the boat is currently underwater.
     */
    @Nullable
    private EntityVehicleOld.Status getUnderwaterStatus()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);

                        if (iblockstate.getMaterial() == Material.WATER && d0 < (double)BlockLiquid.getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos))
                        {
                            if (((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() != 0)
                            {
                                EntityVehicleOld.Status entityboat$status = EntityVehicleOld.Status.UNDER_FLOWING_WATER;
                                return entityboat$status;
                            }

                            flag = true;
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }

        return flag ? EntityVehicleOld.Status.UNDER_WATER : null;
    }

    /**
     * Update the boat's speed, based on momentum.
     */
    private void updateMotion()
    {
        double d0 = -0.03999999910593033D;
        double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;
        this.momentum = 0.05F;

        if (this.previousStatus == EntityVehicleOld.Status.IN_AIR && this.status != EntityVehicleOld.Status.IN_AIR && this.status != EntityVehicleOld.Status.ON_LAND)
        {
            this.waterLevel = this.getEntityBoundingBox().minY + (double)this.height;
            this.setPosition(this.posX, (double)(this.getWaterLevelAbove() - this.height) + 0.101D, this.posZ);
            this.motionY = 0.0D;
            this.lastYd = 0.0D;
            this.status = EntityVehicleOld.Status.IN_WATER;
        }
        else
        {
            if (this.status == EntityVehicleOld.Status.IN_WATER)
            {
                //d2 = (this.waterLevel - this.getEntityBoundingBox().minY) / (double)this.height;
                d2 = (4.5 /*this.waterLevel*/ - this.getEntityBoundingBox().minY) / (double)this.height;
                this.momentum = 0.9F;
            }
            else if (this.status == EntityVehicleOld.Status.UNDER_FLOWING_WATER)
            {
                d1 = -7.0E-4D;
                this.momentum = 0.9F;
            }
            else if (this.status == EntityVehicleOld.Status.UNDER_WATER)
            {
                d2 = 0.009999999776482582D;
                this.momentum = 0.45F;
            }
            else if (this.status == EntityVehicleOld.Status.IN_AIR)
            {
                this.momentum = 0.9F;
            }
            else if (this.status == EntityVehicleOld.Status.ON_LAND)
            {
                this.momentum = this.boatGlide;

                if (this.getControllingPassenger() instanceof EntityPlayer)
                {
                    this.boatGlide /= 2.0F;
                }
            }

//            System.out.println("Momentum: " + this.momentum);
            this.motionX *= (double)this.momentum;
            this.motionZ *= (double)this.momentum;
            this.deltaRotation *= this.momentum;
            this.motionY += d1;

            if (d2 > 0.0D)
            {
                double d3 = 0.65D;
                this.motionY += d2 * 0.06153846016296973D;
                double d4 = 0.75D;
                this.motionY *= 0.75D;
            }
            
            
        }
        
        // Added motionY to speed, see what happens
        speed = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ) + (this.motionY * this.motionY));
        
        wheelRotationAngle += -(float)speed /* Math.signum(getVelocity())*/ * 1.4f * 50f;
        wheelRotationAngle = wheelRotationAngle >= 360 ? wheelRotationAngle - 360 : wheelRotationAngle;
        wheelRotationAngle = wheelRotationAngle < 0 ? wheelRotationAngle + 360 : wheelRotationAngle;
//        System.out.println("Wheel rotation angle: " + wheelRotationAngle);
    }

    private void controlBoat()
    {
        if (this.isBeingRidden())
        {
            float f = 0.0F;

//            if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown)
//            {
//                f += 0.005F;
//            }
            /*
             * forwardInputDown -> accelerate 
             *   v = v0 + at
             * a = F/m = (F_engine - F_friction)/m = (F_engine - F_gravity * M_friction)/m 
             *  = (a_engine * m - m * g * M_friction)/m = a_engine - g * M_friction
             * 
             * a = a_engine - g * M_friction
             * 
             * a = (F_engine - m * g * M_friction)/m
             * 
             */

            if (this.forwardInputDown)
            {
//                f += 0.04F;
                f += 0.1F;
            }

            if (this.backInputDown)
            {
                f -= 0.01F;
            }
            
            double sqVelocity = motionX * motionX + motionZ * motionZ;
            
            Entity driver = this.getControllingPassenger();
            
            double riderYawDelta = 0.0;
            if(driver instanceof EntityPlayer) {
                riderYawDelta = CompatibleMathHelper.wrapAngleTo180Double(driver.rotationYaw - rotationYaw);
            }
            
            double handlingFactor = onGround ? getConfiguration().getHandling().apply(speed)
                    : getConfiguration().getOffGroundHandling().apply(speed);
            
            riderYawDelta *= handlingFactor;
            
//            if (this.leftInputDown)
//            {
//                this.deltaRotation -= 3.0f * sqVelocity;
//            }
//
//            if (this.rightInputDown)
//            {
//                this.deltaRotation += 3.0f * sqVelocity;
//            }
            
//            System.out.println("Motion x: " + motionX + ", motion z: " + motionZ);
            
//            this.rotationYaw += this.deltaRotation;
            
            rotationYaw += riderYawDelta;

            this.lastYawDelta = this.lastYawDelta + (riderYawDelta - this.lastYawDelta) * 0.1;
            
//            System.out.println("F: " + f);

            this.motionX += (double)(MathHelper.sin(-this.rotationYaw * 0.017453292F) * f);
            this.motionZ += (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * f);
//            this.setPaddleState(this.rightInputDown && !this.leftInputDown || this.forwardInputDown, this.leftInputDown && !this.rightInputDown || this.forwardInputDown);
        }
    }

    public void updatePassenger(Entity passenger)
    {
        if (this.isPassenger(passenger))
        {
            float f = 0.0F;
            float f1 = (float)((this.isDead ? 0.009999999776482582D : this.getMountedYOffset()) + passenger.getYOffset());

            if (this.getPassengers().size() > 1)
            {
                int i = this.getPassengers().indexOf(passenger);

                if (i == 0)
                {
                    f = 0.2F;
                }
                else
                {
                    f = -0.6F;
                }

                if (passenger instanceof EntityAnimal)
                {
                    f = (float)((double)f + 0.2D);
                }
            }

            Vec3d vec3d = (new Vec3d((double)f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + (double)f1-0.1, this.posZ + vec3d.z);
            passenger.rotationYaw += this.deltaRotation;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + this.deltaRotation);
            this.applyYawToEntity(passenger);

            if (passenger instanceof EntityAnimal && this.getPassengers().size() > 1)
            {
                int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
                passenger.setRenderYawOffset(((EntityAnimal)passenger).renderYawOffset + (float)j);
                passenger.setRotationYawHead(passenger.getRotationYawHead() + (float)j);
            }
        }
    }

    /**
     * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
     */
    protected void applyYawToEntity(Entity entityToUpdate)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    /**
     * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
     */
    @SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
        this.applyYawToEntity(entityToUpdate);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setString("Type", this.getBoatType().getName());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("Type", 8))
        {
            this.setBoatType(EntityVehicleOld.Type.getTypeFromString(compound.getString("Type")));
        }
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (player.isSneaking())
        {
            return false;
        }
        else
        {
            if (!this.world.isRemote && this.outOfControlTicks < 60.0F)
            {
                player.startRiding(this);
            }

            return true;
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
        this.lastYd = this.motionY;

        if (!this.isRiding())
        {
            if (onGroundIn)
            {
                if (this.fallDistance > 3.0F)
                {
                    if (this.status != EntityVehicleOld.Status.ON_LAND)
                    {
                        this.fallDistance = 0.0F;
                        return;
                    }

                    this.fall(this.fallDistance, 1.0F);

                    if (!this.world.isRemote && !this.isDead)
                    {
                        this.setDead();

                        if (this.world.getGameRules().getBoolean("doEntityDrops"))
                        {
                            for (int i = 0; i < 3; ++i)
                            {
                                this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.PLANKS), 1, this.getBoatType().getMetadata()), 0.0F);
                            }

                            for (int j = 0; j < 2; ++j)
                            {
                                this.dropItemWithOffset(Items.STICK, 1, 0.0F);
                            }
                        }
                    }
                }

                this.fallDistance = 0.0F;
            }
            else if (this.world.getBlockState((new BlockPos(this)).down()).getMaterial() != Material.WATER && y < 0.0D)
            {
                this.fallDistance = (float)((double)this.fallDistance - y);
            }
        }
    }

    public boolean getPaddleState(int side)
    {
        return ((Boolean)this.dataManager.get(DATA_ID_PADDLE[side])).booleanValue() && this.getControllingPassenger() != null;
    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setDamageTaken(float damageTaken)
    {
        this.dataManager.set(DAMAGE_TAKEN, Float.valueOf(damageTaken));
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken()
    {
        return ((Float)this.dataManager.get(DAMAGE_TAKEN)).floatValue();
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int timeSinceHit)
    {
        this.dataManager.set(TIME_SINCE_HIT, Integer.valueOf(timeSinceHit));
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return ((Integer)this.dataManager.get(TIME_SINCE_HIT)).intValue();
    }

    /**
     * Sets the forward direction of the entity.
     */
    public void setForwardDirection(int forwardDirection)
    {
        this.dataManager.set(FORWARD_DIRECTION, Integer.valueOf(forwardDirection));
    }

    /**
     * Gets the forward direction of the entity.
     */
    public int getForwardDirection()
    {
        return ((Integer)this.dataManager.get(FORWARD_DIRECTION)).intValue();
    }

    public void setBoatType(EntityVehicleOld.Type boatType)
    {
        this.dataManager.set(BOAT_TYPE, Integer.valueOf(boatType.ordinal()));
    }

    public EntityBoat.Type getBoatType()
    {
        return EntityBoat.Type.byId(((Integer)this.dataManager.get(BOAT_TYPE)).intValue());
    }

    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 2;
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Nullable
    public Entity getControllingPassenger()
    {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : (Entity)list.get(0);
    }

    @SideOnly(Side.CLIENT)
    public void updateInputs(boolean p_184442_1_, boolean p_184442_2_, boolean p_184442_3_, boolean p_184442_4_)
    {
        this.leftInputDown = p_184442_1_;
        this.rightInputDown = p_184442_2_;
        this.forwardInputDown = p_184442_3_;
        this.backInputDown = p_184442_4_;
    }

    public static enum Status
    {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;
    }

    public static enum Type
    {
        OAK(BlockPlanks.EnumType.OAK.getMetadata(), "oak"),
        SPRUCE(BlockPlanks.EnumType.SPRUCE.getMetadata(), "spruce"),
        BIRCH(BlockPlanks.EnumType.BIRCH.getMetadata(), "birch"),
        JUNGLE(BlockPlanks.EnumType.JUNGLE.getMetadata(), "jungle"),
        ACACIA(BlockPlanks.EnumType.ACACIA.getMetadata(), "acacia"),
        DARK_OAK(BlockPlanks.EnumType.DARK_OAK.getMetadata(), "dark_oak");

        private final String name;
        private final int metadata;

        private Type(int metadataIn, String nameIn)
        {
            this.name = nameIn;
            this.metadata = metadataIn;
        }

        public String getName()
        {
            return this.name;
        }

        public int getMetadata()
        {
            return this.metadata;
        }

        public String toString()
        {
            return this.name;
        }

        /**
         * Get a boat type by it's enum ordinal
         */
        public static EntityVehicleOld.Type byId(int id)
        {
            if (id < 0 || id >= values().length)
            {
                id = 0;
            }

            return values()[id];
        }

        public static EntityVehicleOld.Type getTypeFromString(String nameIn)
        {
            for (int i = 0; i < values().length; ++i)
            {
                if (values()[i].getName().equals(nameIn))
                {
                    return values()[i];
                }
            }

            return values()[0];
        }
    }

    // Forge: Fix MC-119811 by instantly completing lerp on board
    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.canPassengerSteer() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.posX = this.boatPitch;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float)this.boatYaw;
            this.rotationPitch = (float)this.lerpXRot;
        }
    }

    public float getWheelRotationAngle() {
        return wheelRotationAngle; //throw new UnsupportedOperationException("Implement me");
    }

    public double getLastYawDelta() {
        return lastYawDelta;
    }

    @Override
    public VehicleState getState() {
        return vehicleState;
    }

    public float getRotatePassengerFrame() {
        // TODO Auto-generated method stub
        return 0f;
    }

    @Override
    public boolean setState(VehicleState vehicleState) {
    	
        this.vehicleState = vehicleState;
        System.out.println("State changed to " + vehicleState);
        stateUpdateTimestamp = System.currentTimeMillis();
        return false;
    }

    @Override
    public long getStateUpdateTimestamp() {
        return stateUpdateTimestamp;
    }

    @Override
    public <E extends ExtendedState<VehicleState>> void prepareTransaction(E finallyUpdateToState) {
        
    }

    public double getSpeed() {
        return speed;
    }
    
    protected boolean isSteeredForward() {
        return forwardInputDown;
    }
    
    protected boolean isSteeredBackward() {
        return backInputDown;
    }

    public VehicleSuspensionStrategy getSuspensionStrategy() {
        return getConfiguration().getSuspensionStrategy();
    }
    
    public Randomizer getRandomizer() {
        if(randomizer == null) {
            randomizer = new Randomizer();
        }
        return randomizer;
    }
    
    /*
//    @Override
    public void move(MoverType type, double x, double y, double z)
    {
//        System.out.println("Rendering pitch pre move " + rotationPitch);
        if (this.noClip)
        {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        }
        else
        {
            this.world.profiler.startSection("move");
            
//            System.out.println("Yaw: " + this.rotationYaw);

            double requestedXOffset = x;
            double requestedYOffset = y;
            double requestedZOffset = z;

            List<AxisAlignedBB> expandedCollisionBoxes = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(x, y, z));
            AxisAlignedBB preMoveEntityBoundingBox = this.getEntityBoundingBox();

            if (y != 0.0D)
            {
//                int k = 0;
                
                for(AxisAlignedBB expandedCollisionBox: expandedCollisionBoxes) {
                    y = expandedCollisionBox.calculateYOffset(this.getEntityBoundingBox(), y);
                }

//                for (int l = expandedCollisionBoxes.size(); k < l; ++k)
//                {
//                    y = ((AxisAlignedBB)expandedCollisionBoxes.get(k)).calculateYOffset(this.getEntityBoundingBox(), y);
//                }
                
//                System.out.println("Adjusted pre-step y: " + y + ", requested: " + requestedYOffset);

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            }

            if (x != 0.0D)
            {
//                int j5 = 0;

                for(AxisAlignedBB expandedCollisionBox: expandedCollisionBoxes) {
                    x = expandedCollisionBox.calculateXOffset(this.getEntityBoundingBox(), x);
                }
                
//                for (int l5 = expandedCollisionBoxes.size(); j5 < l5; ++j5)
//                {
//                    x = ((AxisAlignedBB)expandedCollisionBoxes.get(j5)).calculateXOffset(this.getEntityBoundingBox(), x);
//                }

                if (x != 0.0D)
                {
                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
                }
            }

            if (z != 0.0D)
            {
//                int k5 = 0;
                
                for(AxisAlignedBB expandedCollisionBox: expandedCollisionBoxes) {
                    z = expandedCollisionBox.calculateZOffset(this.getEntityBoundingBox(), z);
                }

//                for (int i6 = expandedCollisionBoxes.size(); k5 < i6; ++k5)
//                {
//                    z = ((AxisAlignedBB)expandedCollisionBoxes.get(k5)).calculateZOffset(this.getEntityBoundingBox(), z);
//                }

                if (z != 0.0D)
                {
                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));
                }
            }
            
//            double debugX = requestedXOffset - x;
//            double debugY = requestedYOffset - y;
//            double debugZ = requestedZOffset - z;
//            System.out.println("Adjusted deltas: " + debugX + ", " + debugY + ", " + debugZ);
//            System.out.println("Requested Y offset: " + requestedYOffset);
            /*
             * At this point this entity bounding box has been adjusted to a "projected" shape based on collisions.
             

            boolean flag = this.onGround || requestedYOffset != y && requestedYOffset < 0.0D;

            this.stepHeight = 1f;
            if (this.isSteeredForward() && this.stepHeight > 0.0F && (flag || true)  && (requestedXOffset != x || requestedZOffset != z))
            {
                double originalX = x;
                double originalY = y;
                double originalZ = z;
                AxisAlignedBB preStepEntityBoundingBox = this.getEntityBoundingBox();
                
                this.setEntityBoundingBox(preMoveEntityBoundingBox);
//                System.out.println("Pre-move box: " + this.getEntityBoundingBox());
                y = (double)this.stepHeight;
                
                AxisAlignedBB upExpandedBoundingBox = this.getEntityBoundingBox().expand(requestedXOffset, y, requestedZOffset);
//                System.out.println("Pre-move expanded box: " + upExpandedBoundingBox);                
              
                List<AxisAlignedBB> stepExpandedCollisionBoxes = this.world.getCollisionBoxes(this, upExpandedBoundingBox);
                
                AxisAlignedBB downOnlyExpandedBoundingBox = this.getEntityBoundingBox().expand(0, -this.stepHeight, 0);
                List<AxisAlignedBB> downOnlyExpandedCollisionBoxes = this.world.getCollisionBoxes(this, downOnlyExpandedBoundingBox);

                /*
                 * Compare step expanded collision boxes and XZ-expanded original bounding box.
                 * 
                 * If collisions were created as a result of the projected move,
                 * it's likely calculateOffset methods will have to compare the projected move
                 * and offsets in the respective directions.
                 * 
                 * For example, if the projected XZ-move results in collisions,
                 * calculateYOffset will likely return a minimum y-offset to jump a step (potentially smaller than step height).
                 * This is required not to overshoot the step: if the entity y-position, say, is already 1/2 block up,
                 * calculateYOffset will be at the top of the block instead of top + 1/2.
                 * 
                 
                AxisAlignedBB axisalignedbb2 = this.getEntityBoundingBox();
                
                AxisAlignedBB requestedXZExpandedBoundingBox = axisalignedbb2.expand(requestedXOffset, 0.0D, requestedZOffset);

                double anotherYOffset = y;
                int j1 = 0;

                for (int k1 = stepExpandedCollisionBoxes.size(); j1 < k1; ++j1)
                {
                    anotherYOffset = ((AxisAlignedBB)stepExpandedCollisionBoxes.get(j1)).calculateYOffset(requestedXZExpandedBoundingBox, anotherYOffset);
                }
                
//                System.out.println("Alt y offset: " + anotherYOffset);

                axisalignedbb2 = axisalignedbb2.offset(0.0D, anotherYOffset, 0.0D);
                
                double yDownOverlap = 0;
                for(AxisAlignedBB downOnlyExpandedCollisionBox: downOnlyExpandedCollisionBoxes) {
                    double newYOverlap = calculateYOverlap(downOnlyExpandedCollisionBox, downOnlyExpandedBoundingBox);
                    if(Math.abs(newYOverlap) > yDownOverlap) {
                        yDownOverlap = Math.abs(newYOverlap);
                    }
                }
                
                double yGap = yDownOverlap - this.stepHeight + 0.13;
                System.out.println("Y gap: " + yGap);
                
                double anotherXOffset = requestedXOffset;
                int l1 = 0;

                for (int i2 = stepExpandedCollisionBoxes.size(); l1 < i2; ++l1)
                {
                    anotherXOffset = ((AxisAlignedBB)stepExpandedCollisionBoxes.get(l1)).calculateXOffset(axisalignedbb2, anotherXOffset);
                }
                
//                System.out.println("Alt x offset: " + anotherXOffset + ", requested: " + requestedXOffset);

                axisalignedbb2 = axisalignedbb2.offset(anotherXOffset, 0.0D, 0.0D);
                
                
                double anotherZOffset = requestedZOffset;
                int j2 = 0;

                for (int k2 = stepExpandedCollisionBoxes.size(); j2 < k2; ++j2)
                {
                    anotherZOffset = ((AxisAlignedBB)stepExpandedCollisionBoxes.get(j2)).calculateZOffset(axisalignedbb2, anotherZOffset);
                }
                
//                System.out.println("Alt z offset: " + anotherZOffset + ", requested: " + requestedZOffset);

                axisalignedbb2 = axisalignedbb2.offset(0.0D, 0.0D, anotherZOffset);
                
                /*
                 * Now compare the step expanded collision boxes and the original bounding box.
                 * 
                 * If collisions were created only as a result of the projected move,
                 * all calculateOffset methods should return the second argument, essentially:
                 *   calcYOffset = step
                 *   calcXOffset = requestedXOffset
                 *   calcZOffset = requestedZOffset
                 
                AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
                double calcYOffset = y;
                int l2 = 0;

                for (int i3 = stepExpandedCollisionBoxes.size(); l2 < i3; ++l2)
                {
                    calcYOffset = ((AxisAlignedBB)stepExpandedCollisionBoxes.get(l2)).calculateYOffset(axisalignedbb4, calcYOffset);
                }

//                System.out.println("Alt2 y offset: " + calcYOffset);
                axisalignedbb4 = axisalignedbb4.offset(0.0D, calcYOffset, 0.0D);
                
                
                double calcXOffset = requestedXOffset;
                int j3 = 0;

                for (int k3 = stepExpandedCollisionBoxes.size(); j3 < k3; ++j3)
                {
                    calcXOffset = ((AxisAlignedBB)stepExpandedCollisionBoxes.get(j3)).calculateXOffset(axisalignedbb4, calcXOffset);
                }

//                System.out.println("Alt2 x offset: " + calcXOffset + ", requested: " + requestedXOffset);
                axisalignedbb4 = axisalignedbb4.offset(calcXOffset, 0.0D, 0.0D);
                
                
                double calcZOffset = requestedZOffset;
                int l3 = 0;

                for (int i4 = stepExpandedCollisionBoxes.size(); l3 < i4; ++l3)
                {
                    calcZOffset = ((AxisAlignedBB)stepExpandedCollisionBoxes.get(l3)).calculateZOffset(axisalignedbb4, calcZOffset);
                }

//                System.out.println("Alt2 z offset: " + calcZOffset + ", requested: " + requestedZOffset);
                axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, calcZOffset);
                
                
                double anotherSqDistance = anotherXOffset * anotherXOffset + anotherZOffset * anotherZOffset;
                double calcSqDistance = calcXOffset * calcXOffset + calcZOffset * calcZOffset;

                /*
                 * Give priority to a farthest horizontal move.
                 
                if (anotherSqDistance > calcSqDistance)
                {
//                    System.out.println("Option 1");
                    x = anotherXOffset;
                    z = anotherZOffset;
                    y = -anotherYOffset;
                    this.setEntityBoundingBox(axisalignedbb2);
                }
                else
                {
//                    System.out.println("Option 2");
                    x = calcXOffset;
                    z = calcZOffset;
                    y = -calcYOffset;
                    this.setEntityBoundingBox(axisalignedbb4);
                }

                int j4 = 0;

                /*
                 * Now again make sure we aren't overshooting the step.
                 
                for (int k4 = stepExpandedCollisionBoxes.size(); j4 < k4; ++j4)
                {
                    y = ((AxisAlignedBB)stepExpandedCollisionBoxes.get(j4)).calculateYOffset(this.getEntityBoundingBox(), y);
                }
                
                y *= 0.5;
//                System.out.println("Final projected offset y: " + y);

//                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
                
                x = originalX * 0.99;
                z = originalZ * 0.99;
                
//                motionX = x;
//                motionZ = z;
                
                double climbStep = 0.1;
                this.setEntityBoundingBox(preMoveEntityBoundingBox.offset(x, climbStep /*-y, z));
                
                double boxHeight = preMoveEntityBoundingBox.maxY - preMoveEntityBoundingBox.minY;
                double boxLength = preMoveEntityBoundingBox.maxX - preMoveEntityBoundingBox.minX;
                
//                double incline = 360 / Math.PI * Math.asin((-2 * yGap + boxHeight) / boxLength);
                double inclineCos = (-2 * yGap + boxHeight) / boxLength;
                if(inclineCos > 1) {
                    inclineCos = 1;
                }
                double incline = 180 / Math.PI * Math.acos(inclineCos);
                double adjustement = 180 / Math.PI * Math.atan(boxHeight / boxLength);
                double finalIncline = 90 - incline - adjustement;
                this.rotationPitch = (float) finalIncline * 0.25f;
                System.out.println("Final incline: " + finalIncline + ", incline: " + incline + ", adjustment: " + adjustement);
                

//                
//                motionX = 0;
//                motionZ = 0;
                
//                /*
//                 * Compare step and step-less horizontal move.
//                 * Select overall 3-d move based on a farthest horizontal move
//                 
//                if (originalX * originalX + originalZ * originalZ >= x * x + z * z)
//                {
//                    System.out.println("Restore original pre-step bounding box");
//                    x = originalX;
//                    y = originalY;
//                    z = originalZ;
//                    this.setEntityBoundingBox(preStepEntityBoundingBox);
//                }
            } else {
                
                double vehicleLength = 1.5;
                double offset = vehicleLength / 2;
                double offsetX = -offset * MathHelper.sin((float)(rotationYaw / 360f * Math.PI * 2.0));
                double offsetZ = offset * MathHelper.cos((float)(rotationYaw / 360f * Math.PI * 2.0));

                double frontBoxPosX = posX + offsetX;
                double frontBoxPosZ = posZ + offsetZ;
                double frontBoxPosY = posY;
                
                boolean frontBoxOnGround = world.getBlockState((new BlockPos(frontBoxPosX, frontBoxPosY, frontBoxPosZ)).down()).getMaterial() != Material.AIR;
                if(!frontBoxOnGround) {
                    System.out.println("Front box is in the air! ");
                }
                
                double rearBoxPosX = posX - offsetX;
                double rearBoxPosZ = posZ - offsetZ;
                double rearBoxPosY = posY;
                
                boolean rearBoxOnGround = world.getBlockState((new BlockPos(rearBoxPosX, rearBoxPosY, rearBoxPosZ)).down()).getMaterial() != Material.AIR;
                if(!rearBoxOnGround) {
                    System.out.println("Rear box is in the air! ");
                }
                
                if(!frontBoxOnGround && rearBoxOnGround && (requestedXOffset * requestedXOffset + requestedZOffset * requestedZOffset) > 0.01) {
                    rotationPitch -= 5f;
                } else if(rotationPitch > 0f) {
                    rotationPitch -= rotationPitch * 0.2f;
                } else {
                    rotationPitch = 0f;
                }
                
            }

            this.world.profiler.endSection();
            this.world.profiler.startSection("rest");
            this.resetPositionToBB();
//            System.out.println("Rendering pitch post resetPositionToBB " + rotationPitch);
            this.isCollidedHorizontally = requestedXOffset != x || requestedZOffset != z;
            this.isCollidedVertically = requestedYOffset != y;
            this.onGround = this.isCollidedVertically && requestedYOffset < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            int j6 = MathHelper.floor(this.posX);
            int i1 = MathHelper.floor(this.posY - 0.20000000298023224D);
            int k6 = MathHelper.floor(this.posZ);
            BlockPos blockpos = new BlockPos(j6, i1, k6);
            IBlockState iblockstate = this.world.getBlockState(blockpos);

            if (iblockstate.getMaterial() == Material.AIR)
            {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
                Block block1 = iblockstate1.getBlock();

                if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate)
                {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }

            this.updateFallState(y, this.onGround, iblockstate, blockpos);

            if (requestedXOffset != x && rotationPitch == 0)
            {
                this.motionX = 0.0D;
//                System.out.println("Cancelling motionX");
            }

            if (requestedZOffset != z && rotationPitch == 0)
            {
                this.motionZ = 0.0D;
//                System.out.println("Cancelling motionZ");
            }

            Block block = iblockstate.getBlock();

            if (requestedYOffset != y)
            {
                block.onLanded(this.world, this);
            }

//            try
//            {
//                this.doBlockCollisions();
//            }
//            catch (Throwable throwable)
//            {
//                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
//                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
//                this.addEntityCrashInfo(crashreportcategory);
//                throw new ReportedException(crashreport);
//            }

            this.world.profiler.endSection();
        }
    }*/
    
    public static double calculateYOverlap(AxisAlignedBB box1, AxisAlignedBB box2) {
        double overlap = 0;
        double maxDiff = box1.maxY - box2.maxY;
        if(maxDiff >= 0 && box2.maxY >= box1.minY) {
            overlap = box2.maxY - box1.minY;
        } else if(maxDiff <= 0 && box1.maxY >= box2.minY) {
            overlap = -(box1.maxY - box2.minY);
        }
        
        return overlap;

    }
    
    public void updateOBB() {
    	
    	this.obb.setPosition(posX, posY, posZ);
    	this.obb.setRotation(Math.toRadians(180-rotationYaw), Math.toRadians(this.rotationPitch), 0.0);
    	
    }
    
    // MC Heli try
    
    public static List<AxisAlignedBB> getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB) {
        ArrayList<AxisAlignedBB> collidingBoundingBoxes = new ArrayList<>();
        collidingBoundingBoxes.clear();
        int i = MathHelper.floor(par2AxisAlignedBB.minX);
        int j = MathHelper.floor(par2AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor(par2AxisAlignedBB.minY);
        int l = MathHelper.floor(par2AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor(par2AxisAlignedBB.minZ);
        int j1 = MathHelper.floor(par2AxisAlignedBB.maxZ + 1.0D);

        MutableBlockPos mbs = new MutableBlockPos();
        for (int d0 = i; d0 < j; ++d0) {
            for (int l1 = i1; l1 < j1; ++l1) {
                
                if (par1Entity.world.isBlockLoaded(mbs.setPos(d0, 64, l1))) {
                    for (int list = k - 1; list < l; ++list) {
//                        compatibility.getBlockAtPosition(world, blockPos)
                        
                        mbs.setPos(d0, list, l1);
                        IBlockState blockState = par1Entity.world.getBlockState(mbs);
//                        Block block = par1Entity.world.getBlock(par1Entity.world, d0, list, l1);
                        if (blockState != null) {
                            blockState.addCollisionBoxToList(par1Entity.world, 
                                    mbs, par2AxisAlignedBB, collidingBoundingBoxes, par1Entity, false);
//                            block.addCollisionBoxesToList(par1Entity.world, d0, list, l1, par2AxisAlignedBB,
//                                    collidingBoundingBoxes, par1Entity);
                        }
                    }
                }
            }
        }

        double arg14 = 0.25D;
        List<Entity> arg15 = par1Entity.world.getEntitiesWithinAABBExcludingEntity(par1Entity,
                par2AxisAlignedBB.expand(arg14, arg14, arg14));

        for (int arg16 = 0; arg16 < arg15.size(); ++arg16) {
            Entity entity = (Entity) arg15.get(arg16);
            if (!(entity instanceof EntityLivingBase) /*&& !(entity instanceof MCH_EntitySeat)
                    && !(entity instanceof MCH_EntityHitBox)*/) {
                AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();
                if (axisalignedbb1 != null && axisalignedbb1.intersects(par2AxisAlignedBB)) {
                    collidingBoundingBoxes.add(axisalignedbb1);
                }

                axisalignedbb1 = par1Entity.getCollisionBox(entity);
                if (axisalignedbb1 != null && axisalignedbb1.intersects(par2AxisAlignedBB)) {
                    collidingBoundingBoxes.add(axisalignedbb1);
                }
            }
        }

        return collidingBoundingBoxes;
      
    }
    
//    @Override
    public void moveNew(MoverType type, double moveX, double moveY, double moveZ) {
        if (true /*this.getAcInfo() != null*/) {
            
            if(true) return;
            
            this.world.profiler.startSection("move");
            this.height *= 0.4F;
            
            double originalPosX = this.posX;
            double originalPosY = this.posY;
            double originalPosZ = this.posZ;
            
            double requestedMoveX = moveX;
            double requestedMoveY = moveY;
            double requestedMoveZ = moveZ;
            
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().offset(0, 0, 0); //.copy();
            List<AxisAlignedBB> collidingBoxes = getCollidingBoundingBoxes(this, 
                    this.getEntityBoundingBox().offset(moveX, moveY, moveZ));

            for (int i = 0; i < collidingBoxes.size(); ++i) {
                moveY = ((AxisAlignedBB) collidingBoxes.get(i)).calculateYOffset(this.getEntityBoundingBox(), moveY);
            }

//            this.boundingBox.offset(0.0D, moveY, 0.0D);
            this.setEntityBoundingBox(getEntityBoundingBox().offset(0.0D, moveY, 0.0D));
//            if (!this.field_70135_K && requestedMoveY != moveY) {
//                moveZ = 0.0D;
//                moveY = 0.0D;
//                moveX = 0.0D;
//            }

            boolean yAdjustmentRequired = this.onGround || requestedMoveY != moveY && requestedMoveY < 0.0D;

            for (int j = 0; j < collidingBoxes.size(); ++j) {
                moveX = ((AxisAlignedBB) collidingBoxes.get(j)).calculateXOffset(this.getEntityBoundingBox(), moveX);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(moveX, 0.0D, 0.0D));
//            if (!this.field_70135_K && requestedMoveX != moveX) {
//                moveZ = 0.0D;
//                moveY = 0.0D;
//                moveX = 0.0D;
//            }

            for (int j = 0; j < collidingBoxes.size(); ++j) {
                moveZ = ((AxisAlignedBB) collidingBoxes.get(j)).calculateZOffset(this.getEntityBoundingBox(), moveZ);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, moveZ));
//            if (!this.field_70135_K && requestedMoveZ != moveZ) {
//                moveZ = 0.0D;
//                moveY = 0.0D;
//                moveX = 0.0D;
//            }

            if (this.stepHeight > 0.0F && yAdjustmentRequired && this.height < 0.05F && (requestedMoveX != moveX || requestedMoveZ != moveZ)) {
                double preStepMoveX = moveX;
                double preStepMoveY = moveY;
                double preStepMoveZ = moveZ;
                moveX = requestedMoveX;
                moveY = (double) this.stepHeight;
                moveZ = requestedMoveZ;
                AxisAlignedBB throwable = this.getEntityBoundingBox().offset(0, 0, 0); //this.boundingBox.copy();
//                this.boundingBox.setBB(axisalignedbb);
                this.setEntityBoundingBox(axisalignedbb);
                collidingBoxes = getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(requestedMoveX, moveY, requestedMoveZ));

                for (int k = 0; k < collidingBoxes.size(); ++k) {
                    moveY = ((AxisAlignedBB) collidingBoxes.get(k)).calculateYOffset(this.getEntityBoundingBox(), moveY);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, moveY, 0.0D));
//                if (!this.field_70135_K && requestedMoveY != moveY) {
//                    moveZ = 0.0D;
//                    moveY = 0.0D;
//                    moveX = 0.0D;
//                }

                for (int k = 0; k < collidingBoxes.size(); ++k) {
                    moveX = ((AxisAlignedBB) collidingBoxes.get(k)).calculateXOffset(this.getEntityBoundingBox(), moveX);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(moveX, 0.0D, 0.0D));
//                if (!this.field_70135_K && requestedMoveX != moveX) {
//                    moveZ = 0.0D;
//                    moveY = 0.0D;
//                    moveX = 0.0D;
//                }

                for (int k = 0; k < collidingBoxes.size(); ++k) {
                    moveZ = ((AxisAlignedBB) collidingBoxes.get(k)).calculateZOffset(this.getEntityBoundingBox(), moveZ);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, moveZ));
//                if (!this.field_70135_K && requestedMoveZ != moveZ) {
//                    moveZ = 0.0D;
//                    moveY = 0.0D;
//                    moveX = 0.0D;
//                }

//                if (!this.field_70135_K && requestedMoveY != moveY) {
//                    moveZ = 0.0D;
//                    moveY = 0.0D;
//                    moveX = 0.0D;
//                } else 
                
                {
                    moveY = (double) (-this.stepHeight);

                    for (int k = 0; k < collidingBoxes.size(); ++k) {
                        moveY = ((AxisAlignedBB) collidingBoxes.get(k)).calculateYOffset(this.getEntityBoundingBox(), moveY);
                    }

                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, moveY, 0.0D));
                }

                if (preStepMoveX * preStepMoveX + preStepMoveZ * preStepMoveZ >= moveX * moveX + moveZ * moveZ) {
                    moveX = preStepMoveX;
                    moveY = preStepMoveY;
                    moveZ = preStepMoveZ;
//                    this.boundingBox.setBB(throwable);
                    this.setEntityBoundingBox(throwable);
                }
            }

            this.world.profiler.endSection();
            this.world.profiler.startSection("rest");
            this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
            this.posY = this.getEntityBoundingBox().minY + (double) this.getYOffset()
                    - (double) this.height;
            this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
            this.isCollidedHorizontally = requestedMoveX != moveX || requestedMoveZ != moveZ;
            this.isCollidedVertically = requestedMoveY != moveY;
            this.onGround = requestedMoveY != moveY && requestedMoveY < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            
            int j6 = MathHelper.floor(this.posX);
            int i1 = MathHelper.floor(this.posY - 0.20000000298023224D);
            int k6 = MathHelper.floor(this.posZ);
            BlockPos blockpos = new BlockPos(j6, i1, k6);
            IBlockState iblockstate = this.world.getBlockState(blockpos);

            if (iblockstate.getMaterial() == Material.AIR)
            {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
                Block block1 = iblockstate1.getBlock();

                if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate)
                {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }

//            this.updateFallState(y, this.onGround, iblockstate, blockpos);
            
            this.updateFallState(moveY, this.onGround, iblockstate, blockpos);
            
            if (requestedMoveX != moveX) {
                this.motionX = 0.0D;
            }

            if (requestedMoveY != moveY) {
                this.motionY = 0.0D;
            }

            if (requestedMoveZ != moveZ) {
                this.motionZ = 0.0D;
            }

            // Unused code?
//            double arg9999 = this.posX - originalPosX;
//            arg9999 = this.posY - originalPosY;
//            arg9999 = this.posZ - originalPosZ;

            try {
                this.doBlockCollisions();
            } catch (Throwable arg32) {
                CrashReport crashreport = CrashReport.makeCrashReport(arg32, "Checking entity tile collision");
                CrashReportCategory crashreportcategory = crashreport
                        .makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            this.world.profiler.endSection();
        }
    }

    private Object getAcInfo() {
        // TODO Auto-generated method stub
        return null;
    }
}