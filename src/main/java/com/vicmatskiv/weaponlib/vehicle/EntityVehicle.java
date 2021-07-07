package com.vicmatskiv.weaponlib.vehicle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.swing.border.MatteBorder;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EFX10;
import org.lwjgl.util.vector.Quaternion;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.realmsclient.dto.UploadInfo;
import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.Configurable;
import com.vicmatskiv.weaponlib.Contextual;
import com.vicmatskiv.weaponlib.EntityClassFactory;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.TryFireMessage;
import com.vicmatskiv.weaponlib.animation.Randomizer;
import com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMovingSound;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePositionedSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;
import com.vicmatskiv.weaponlib.compatibility.RevSound;
import com.vicmatskiv.weaponlib.compatibility.sound.AdvCompatibleMovingSound;
import com.vicmatskiv.weaponlib.compatibility.sound.DriftMovingSound;
import com.vicmatskiv.weaponlib.compatibility.sound.EngineMovingSound;
import com.vicmatskiv.weaponlib.particle.DriftCloudFX;
import com.vicmatskiv.weaponlib.particle.DriftSmokeFX;
import com.vicmatskiv.weaponlib.particle.ParticleExSmoke;
import com.vicmatskiv.weaponlib.particle.vehicle.DriftCloudParticle;
import com.vicmatskiv.weaponlib.particle.vehicle.ExhaustParticle;
import com.vicmatskiv.weaponlib.particle.vehicle.TurbulentSmokeParticle;
import com.vicmatskiv.weaponlib.particle.vehicle.VehicleExhaustFlameParticle;
import com.vicmatskiv.weaponlib.state.ExtendedState;
import com.vicmatskiv.weaponlib.vehicle.VehiclePart.Wheel;
import com.vicmatskiv.weaponlib.vehicle.collisions.AABBTool;
import com.vicmatskiv.weaponlib.vehicle.collisions.GJKResult;
import com.vicmatskiv.weaponlib.vehicle.collisions.IDynamicCollision;
import com.vicmatskiv.weaponlib.vehicle.collisions.OBBCollider;
import com.vicmatskiv.weaponlib.vehicle.collisions.OreintedBB;
import com.vicmatskiv.weaponlib.vehicle.collisions.RigidBody;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Engine;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.QuatUtil;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.Transmission;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.engines.EvoIVEngine;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.solver.VehiclePhysicsSolver;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.solver.WheelSolver;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleClientPacket;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleClientPacketHandler;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleControlPacket;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleControlPacketHandler;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleDataContainer;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleDataSerializer;
import com.vicmatskiv.weaponlib.vehicle.network.VehiclePhysSerializer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockSnowBlock;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.data.PackMetadataSection;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.libraries.SourceLWJGLOpenAL;
import scala.actors.threadpool.Arrays;
import scala.reflect.NameTransformer.OpCodes;
import scala.reflect.internal.Trees.This;

/*   __      __  _     _      _           
	 \ \    / / | |   (_)    | |          
	  \ \  / /__| |__  _  ___| | ___  ___ 
	   \ \/ / _ \ '_ \| |/ __| |/ _ \/ __|
	    \  /  __/ | | | | (__| |  __/\__ \
	     \/ \___|_| |_|_|\___|_|\___||___/       
	     
	   ~ by Victor Matskiv and Jim Holden ~
 * 
 */

public class EntityVehicle extends Entity implements Configurable<EntityVehicleConfiguration>, ExtendedState<VehicleState>, IDynamicCollision, Contextual
{
    

	private static enum DriverInteractionEvent {
        NONE, ENTER, EXIT, DRIVING, OUT
    }
    
	
	
	/*
	 * GENERIC VARIABLES
	 * (misc variables)
	 */
	private EntityVehicleConfiguration configuration;
	private VehicleState vehicleState = VehicleState.IDLE;
    private VehicleDrivingAspect drivingAspect = new VehicleDrivingAspect();
    private DriverInteractionEvent driverInteractionEvent = DriverInteractionEvent.NONE;
    private long stateUpdateTimestamp;
    private Randomizer randomizer;
    public float outOfControlTicks = 0.0F;
    private int lerpSteps;
    private float deltaRotation;
    
    //
    private double boatPitch;
    private double lerpY;
    private double lerpZ;
    private double boatYaw;
    private double lerpXRot;
    
    public static final DataParameter<VehicleDataSerializer> VEHICLE_DAT = EntityDataManager.createKey(EntityVehicle.class, VehicleDataSerializer.SERIALIZER);
    public static final DataParameter<VehiclePhysSerializer> SOLVER_DAT = EntityDataManager.createKey(EntityVehicle.class, VehiclePhysSerializer.SERIALIZER);
    
    public ModContext context;
    
    public float prevLiftOffset = 0.0f;
    public float liftOffset = 0.0f;
    
    /*
     * SOUND DECLARATIONS/VARIABLES
     */
   
    private Supplier<CompatibleVec3> soundPositionProvider = () -> new CompatibleVec3(posX, posY, posZ);
    private Supplier<Boolean> donePlayingSoundProvider = () -> isDead;
    private Supplier<Boolean> isDorifto = () -> !getSolver().isDrifting;
    private Supplier<Float> doriftoSoundProvider = () -> 1.0f;
    
    /*
     * Key Inputs
     */
    private boolean leftInputDown;
    private boolean rightInputDown;
    private boolean forwardInputDown;
    private boolean backInputDown;
    
    /*
     * PHYSICS + COLLISION VARIABLES
     */
    public OreintedBB oreintedBoundingBox;
    public float wheelRotationAngle;
    public VehiclePhysicsSolver solver;
    public double mass = 1352;
    public Engine engine = new EvoIVEngine("Evo IV Engine", "Mitsubishi Motors");
    public double steerangle;
    public double throttle = 0;
    public double angularvelocity = 0;
    public double forwardLean = 0.0;
    public double sideLean = 0.0;
    public double driftTuner = 0.0;
    public boolean isBraking = false;
    
    public double rideOffset = 0.0;
    
    
    public float rotationRoll = 0.0f;
    public float prevRotationRoll = 0.0f;
    
    public float rotationRollH = 0.0f;
    public float prevRotationRollH = 0.0f;
    
    
    public double lastYawDelta = 0.0;
    
    public boolean isReversing = false;
	private float nextStepDistance;
	private float nextFlap;
	private int fire;
	
	/*
	 * Sounds
	 */
	
	public MovingSound drivingSound;
	public MovingSound driftingSound;
	
	
	/*
	 * CONSTRUCTORS
	 */
    
	public EntityVehicle(World worldIn) {
		super(worldIn);
		
		this.setSize(1.375F, 0.5625F);
		this.oreintedBoundingBox = new OreintedBB(getConfiguration().getAABBforOBB());
	}
    
	public EntityVehicle(World worldIn, double x, double y, double z)
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
    
	/**
	 * Initializes the vehicle.
	 */
	@Override
	protected void entityInit() {
		
		getDataManager().register(VEHICLE_DAT, new VehicleDataSerializer(this));
		getDataManager().register(SOLVER_DAT, new VehiclePhysSerializer(getSolver()));
	}
	
	/*
	 * Data manager
	 */
	
	
	/*
	 * IMPORTANT GETTERS
	 */
	
	public int getCurrentRiders() {
		return this.getPassengers().size();
	}
	
	public int getCarMaxPersonnel() {
		return getConfiguration().getSeats().size();
	}
	
	
	public VehiclePhysicsSolver getSolver() {
		if(solver == null) {
    		solver = new VehiclePhysicsSolver(this, 1352);
    	}
		return solver;
	}
	
	@Override
    public EntityVehicleConfiguration getConfiguration() {
        if(configuration == null) {
            configuration = (EntityVehicleConfiguration) EntityClassFactory.getInstance().getConfiguration(getClass());
        }
        return configuration;
    }
	
	
	@Override
	public OreintedBB getOreintedBoundingBox() {
		if(oreintedBoundingBox == null) this.oreintedBoundingBox = new OreintedBB(getConfiguration().getAABBforOBB());
		return this.oreintedBoundingBox;
	}
	
	public float getWheelRotationAngle() {
		return this.wheelRotationAngle;
	}
	
	public double getLastYawDelta() {
		return this.lastYawDelta;
	}	
	
	public double getSpeed() {
		updateSolver();
		return solver.synthAccelFor;
	}
	
	@Override
	public VehicleState getState() {
		return vehicleState;
	}

	@Override
	public long getStateUpdateTimestamp() {
		return stateUpdateTimestamp;
	}

	public Randomizer getRandomizer() {
        if(randomizer == null) {
            randomizer = new Randomizer();
        }
        return randomizer;
    }
	
	public VehicleSuspensionStrategy getSuspensionStrategy() {
        return getConfiguration().getSuspensionStrategy();
    }

	@Nullable
    public Entity getControllingPassenger()
    {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : (Entity)list.get(0);
    }
	
	
	/*
	 * LOGIC
	 */

	
	@Override
	public void updatePassenger(Entity passenger)
	    {
		
			
		
	        if (this.isPassenger(passenger))
	        {
	            float f = 0.0F;
	            float f1 = (float)((this.isDead ? 0.009999999776482582D : this.getMountedYOffset()) + passenger.getYOffset());

	            
	            int i = this.getPassengers().indexOf(passenger);
	            Vec3d seatOffset = getConfiguration().getSeatAtIndex(i).getSeatPosition();
	        
	            
	            if (this.getPassengers().size() > 1)
	            {
	                
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
	            
	            /*
	            float muRoll = (float) ((1 - Math.cos(Minecraft.getMinecraft().getRenderPartialTicks() * Math.PI)) / 2f);
        		float roll = (prevRotationRollH+prevRotationRoll) + ((rotationRoll+rotationRollH)-(prevRotationRoll+prevRotationRollH))*muRoll;
        		
        		rotationYaw = 0;
        		rotationRoll = 45f;
        		rotationRollH = 0f;
        		
        		
        		
        		
        		double interpH = 1.0*(roll/45f);
        		if(!world.isRemote) {
        			System.out.println("Interpolated: " + interpH);
        		}
        		
        		double h = interpH * Math.signum(roll);
        		float aPRX = (float) -(Math.cos(Math.toRadians(roll))*h);
        		float aPRY = (float) -(Math.sin(Math.toRadians(roll))*h);
        		
        		System.out.println(rotationYaw-130);
        		
        		//Vec3d apr = new Vec3d(aPRX/2, aPRY, aPRX);
        		Vec3d apr = new Vec3d(-aPRX, aPRY, 10.0);
        		if(aPRX == 1.0f || aPRX == -1.0f) aPRX = 0.0f;
	          */
	            
	            
	            float mu2 = (float) ((1 - Math.cos(Minecraft.getMinecraft().getRenderPartialTicks() * Math.PI)) / 2f);
        		float interpPitch = prevRotationPitch + (rotationPitch-prevRotationPitch)*mu2;
        		double tA = 1.0 * Math.signum(interpPitch);
        		double o = Math.sin(Math.toRadians(interpPitch))*tA;
        		double a = Math.cos(Math.toRadians(interpPitch))*tA;
        		
        		
	           
	            Vec3d apr = Vec3d.ZERO;
	            passenger.setPosition(this.posX + vec3d.x + seatOffset.x+apr.x, this.posY + (double)f1 + seatOffset.y + apr.y, this.posZ + vec3d.z + seatOffset.z + apr.x);
	           // passenger.setPosition(this.posX + seatOffset.x, this.posY + seatOffset.y, this.posZ + seatOffset.z);
		           
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
	
	protected void applyYawToEntity(Entity entityToUpdate)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }
	
	
	@Override
	protected void removePassenger(Entity passenger) {
		
		Vec3d p = (new Vec3d(2,0,1)).rotatePitch((float) Math.toRadians(rotationPitch)).rotateYaw((float) Math.toRadians(-rotationYaw));
    	passenger.move(MoverType.SELF, p.x, p.y, p.z);
		super.removePassenger(passenger);
		
		
		
	}
	
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (player.isSneaking())
        {
            return false;
        }
        else
        {
            if (!this.world.isRemote && this.outOfControlTicks < 60.0F && canFitPassenger(player))
            {
                player.startRiding(this);
            }

            return true;
        }
    }
	
	protected boolean canFitPassenger(Entity passenger)
    {
        return getCurrentRiders()+1 <= getCarMaxPersonnel();
    }
	
	public boolean isSteeredForward() {
		return !isReversing;
	}

	public boolean isSteeredBackward() {
		return isReversing;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}
	
	@SideOnly(Side.CLIENT)
    public void updateInputs(boolean left, boolean right, boolean forward, boolean back)
    {
        this.leftInputDown = 	left;
        this.rightInputDown = 	right;
        this.forwardInputDown = forward;
        this.backInputDown = back;
    }
	
	@Override
    public boolean setState(VehicleState vehicleState) {
        this.vehicleState = vehicleState;
        System.out.println("State changed to " + vehicleState);
        stateUpdateTimestamp = System.currentTimeMillis();
        return false;
    }
	
	@Override
	public void updateOBB() {
		this.oreintedBoundingBox = this.oreintedBoundingBox.fromAABB(AABBTool.createAABB(new Vec3d(-0.75, 0.2, -2.9), new Vec3d(1.90, 1.2, 3.2)), getPositionVector());
		this.oreintedBoundingBox.setPosition(posX, posY, posZ);
    	this.oreintedBoundingBox.setRotation(Math.toRadians(180-rotationYaw), Math.toRadians(this.rotationPitch), 0.0);
	}
	
	public void updateSolver() {
		if(solver == null) {
    		solver = new VehiclePhysicsSolver(this, 1352);
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
	
	
	
	/*
	 * CONTROLLING
	 */
	/**
	 * Unless you are trying to add self-driven vehicles,
	 * please keep this on client
	 */
	public void updateSteering(EntityPlayer driver) {
		float yaw = driver.rotationYaw;
		float pitch = driver.rotationPitch;
		
		float f = 1.0f;
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
		
	}

	/**
	 * Updates the vehicles controls, like throttle, braking,
	 * handbraking, drift-tuning, etc.
	 */
	@SideOnly(Side.CLIENT)
	public void updateControls() {
		
		Transmission trans = getSolver().transmission;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			
			if(trans.isReverseGear) {
				trans.exitReverse();
			}
			
			if( throttle < 1) throttle += 0.1;
		}  else {
			if(throttle > 0) throttle -= 0.1;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S) && !trans.isReverseGear) {
			
			/*
			if(!trans.isReverseGear && getRealSpeed() == 0.0) {
				trans.enterReverse();
			}*/
			
			
			if( throttle >= 0) throttle -= 0.1;
			isBraking = true;
		} else isBraking = false;
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S) && trans.isReverseGear) {
			
			if( throttle < 1) throttle += 0.1;
		} 
		
		/*
		 * if(trans.isReverseGear) {
				if( throttle < 1) throttle += 0.1;
			}
			
		 */
		
		if(throttle < 0) throttle = 0;
		if(throttle > 1) throttle = 1;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			solver.applyHandbrake();
		} else {
			solver.releaseHandbrake();
		}
		
		
		steerangle *= 0.5;
		int mA = 45;
	
		// Code below fixes bug where players are unable
		// to become Takumi Fujiwara, even when listening to
		// EuroBeat. This allows you to do the C-121, so you
		// will win the Usui Pass Battle.
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
		
		lastYawDelta = Math.toDegrees(steerangle)*0.3;
		wheelRotationAngle -= (float) solver.velocity.lengthVector();
		
		
		
		
		
	}
	
	
	
	/*
	 * COLLISIONS
	 */
	
	@Override
	public void doOBBCollision() {
		
		
		
		
		List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(10.0), EntitySelectors.IS_ALIVE);
		
		list.removeIf((e) -> this.isPassenger(e));
		
		
		for(Entity ent : list) {
			
			AxisAlignedBB cEnt = ent.getEntityBoundingBox();
			AxisAlignedBB aabbEnt = new AxisAlignedBB(cEnt.minX - ent.posX, cEnt.minY - ent.posY, cEnt.minZ - ent.posZ, cEnt.maxX - ent.posX, cEnt.maxY - ent.posY, cEnt.maxZ - ent.posZ);
			
			
			GJKResult result = OBBCollider.areColliding(getOreintedBoundingBox(), OreintedBB.fromAABB(aabbEnt, ent.getPositionVector()));
			if(result.status == GJKResult.Status.COLLIDING) {
				///System.out.println("COLLISION DETECTED!");
				
				Vec3d shoot = result.separationVector.scale(result.penetrationDepth);
				Vec3d p = ent.getPositionVector();
				if(ent instanceof EntityPlayer) {
					if(this.world.isRemote) {
						ent.setPosition(p.x+shoot.x, p.y+shoot.y, p.z+shoot.z);
						
					}
				} else {
					ent.setPosition(p.x+shoot.x, p.y+shoot.y, p.z+shoot.z);
					
				}
				
				/*
				Vec3d shoot = result.separationVector.scale(result.penetrationDepth*5.0);
				ent.addVelocity(shoot.x, shoot.y+1, shoot.z);*/
				
				
			}
			
			
		}
		
		
	}
	
	@Override
	protected void doBlockCollisions() {
		super.doBlockCollisions();
	}
	
	@Override
	public void move(MoverType type, double x, double y, double z)
    {
		//this.setEntityBoundingBox(getEntityBoundingBox().offset(0.0, 0.1, 0.0));
		
		super.move(type, x, y, z);
    }
	
	
	
	public float getInterpolatedLiftOffset() {
		return prevLiftOffset + (liftOffset-prevLiftOffset)*Minecraft.getMinecraft().getRenderPartialTicks();
	}
	
	
	public void handleHillClimbing() {
		
		if(liftOffset == 0 && ticksExisted % 3 == 0) {
			prevLiftOffset = liftOffset;
		} else {
			prevLiftOffset = liftOffset;
		}
		
		if(prevLiftOffset == liftOffset) liftOffset = 0;
		
		double wheelbase = 2.85;
		
		// IF WE ARE BRAKING, WE DO NOT
		// HAVE ANY INTENT TO GO UP A HILL.
				
		
		if(isBraking || getSolver().getVelocityVector().lengthVector() < 0.3 || (throttle == 0 && getSolver().getVelocityVector().lengthVector() < 25)) {
			return;
		}
		
		
		/*
		 * ROLL HANDLER
		 */
		
		double sideScan = 1.0;
		Vec3d rollSA = new Vec3d(sideScan, 2.0, 0.0).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d rollEA = new Vec3d(sideScan, -10, 0.0).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		RayTraceResult rollRA = world.rayTraceBlocks(rollSA, rollEA, false, true, false);
		
		Vec3d rollSB = new Vec3d(-sideScan, 2.0, 0.0).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d rollEB = new Vec3d(-sideScan, -10, 0.0).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		RayTraceResult rollRB = world.rayTraceBlocks(rollSB, rollEB, false, true, false);
		
		
		double rollHA = 0.0;
		double rollHB = 0.0;
		
		
		
		if(rollRA == null) {
			rollHA = 5;
		} else rollHA = rollRA.hitVec.subtract(rollSA).lengthVector();
		if(rollRB == null) {
			rollHB = 5;
		} else rollHB = rollRB.hitVec.subtract(rollSB).lengthVector();
		
		
		
		double sideWidth = 1.847;
		
		double ang = Math.asin((rollHB-rollHA)/sideWidth);
		double rollTarget = (float) Math.toDegrees(-ang);
	
		if(Double.isNaN(rollTarget)) rollTarget = 0.0;
		
		double rollDist = Math.abs(rotationRollH) - Math.abs(rollTarget);
		
		
		
		
		if(rollDist > 0.5) {
			
			if(rollTarget == 0.0 || rollTarget < 0) {
				rollTarget = -rotationRollH;
			}
			
		
			
			
			// DOWN PITCHING //
			if(Math.abs(rollTarget-rotationRollH) < 9) {
	
			} else if(rollTarget < rotationRollH) {
				rotationRollH -= Math.abs(rollTarget)*0.08;
			} else {
				rotationRollH += Math.abs(rollTarget)*0.8;
			}
		} else {
			
			
			
			if(rollTarget == 0.0) {
				rollTarget = -rotationRollH;
			}
			//System.out.println(rollTarget + " | " + rotationRollH);
		
			if(Math.abs(rollTarget)- Math.abs(rotationRollH) < 0.01) {
				
			}else if(rollTarget < rotationRollH) {
			
				rotationRollH -= Math.abs(rollTarget)*0.03;
			} else {
				
				rotationRollH += Math.abs(rollTarget)*0.08;
			}
		}
		
		//rotationRoll = -35f;
		//rotationRoll = (float) ((ticksExisted/75.0)*45f);
		//rotationRoll = (float) (new InterpolationKit()).interpolateValue(-35, 35, ticksExisted/200.0);
		//rotationRollH = 0;
		//rotationRoll = (float) ((ticksExisted/200.0)*35);
		//rotationRoll = (float) rollTarget;
		
		/*
		 * END ROLL HANDLER
		 */
		
		
		
		
		
		
		
		float targetDown = 0.0f;
		float targetUp = 0.0f;
		
		
		/*
		 * DOWNWARDS HILL HANDLING
		 */
		
		/*
		double dist = 0.0f;
		double forwardNess = 2.0;
		Vec3d sDown = new Vec3d(-0.5, 0.0, forwardNess).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d eDown = new Vec3d(-0.5, -10, forwardNess).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		RayTraceResult rayDown = world.rayTraceBlocks(sDown, eDown, false, true, false);
		if(rayDown != null) {
			dist = rayDown.hitVec.subtract(sDown).lengthVector();
			float newPitch = (float) -Math.toDegrees(Math.atan(dist/(2.5)));
			targetDown = newPitch;
		}*/
		
		
		// roll
		
		double dist = 0.0f;
		double forwardNess = 2.0;
		Vec3d sDown = new Vec3d(-0.5, 0.0, forwardNess).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d eDown = new Vec3d(-0.5, -10, forwardNess).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		
		
		
		
		
		
		
		RayTraceResult rayDown = world.rayTraceBlocks(sDown, eDown, false, true, false);
		
		Vec3d sDown2 = new Vec3d(-0.5, 0.0, -forwardNess+0.5).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d eDown2 = new Vec3d(-0.5, -10, -forwardNess+0.5).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		RayTraceResult rayDown2 = world.rayTraceBlocks(sDown2, eDown2, false, true, false);
		double hF = rayDown.hitVec.subtract(sDown).lengthVector();
		double hB = rayDown2.hitVec.subtract(sDown2).lengthVector();
	
		//System.out.println("front vector: " + hF);
		
		
		
		double hillAngle = Math.toDegrees(Math.asin((hF-hB)/wheelbase));
		if(hillAngle != 0.0)
		//System.out.println("Hill Angle: " + hillAngle);
		
		/*
		if(hF-hB != 0.0) {
			System.out.println("FE: " + hF  + " | RE: " + hB + " | DIFF: " + (hF-hB));
		}*/
		
		
		if(rayDown != null && (hF-hB) >= 0) {
			dist = rayDown.hitVec.subtract(sDown).lengthVector();
			
			getSolver().velocity = getSolver().getVelocityVector().scale(1.005);
			float newPitch = (float) -Math.toDegrees(Math.atan(dist/(wheelbase)));
			targetDown = newPitch;
		}
		
		
	
		double baseReach = 8.75;
		
		if(!onGround || rotationPitch > 5) {
			baseReach += 3;
		}
		
		Vec3d start = new Vec3d(-0.5, 0.0, 0.0).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d end = new Vec3d(-0.5, 0.0, baseReach).rotateYaw((float) Math.toRadians(-rotationYaw)).rotateYaw((float) -solver.getSideSlipAngle()).add(getPositionVector());
		RayTraceResult ray = world.rayTraceBlocks(start, end, false, true, false);
		if(ray != null) {
			IBlockState b = this.world.getBlockState(ray.getBlockPos().up());
			double upMag = 0.0;
			if(b.getBlock() instanceof BlockSnow) {
				
				if(b.getValue(BlockSnow.LAYERS).intValue() > 2) return;
				upMag += 0.5;
			} else if(b.causesSuffocation() || b.getBlock() instanceof BlockPane) return;
			Vec3d hitVec = ray.hitVec;
			Vec3d ab = hitVec.subtract(getPositionVector());
			double distance = ab.lengthSquared();
			
			if(distance > 15.5) {
				upMag=  0.25;	
			} else if (distance > 1.0){
				upMag=  0.5;
			} else {
				upMag=  1.5;
				
			}
			double liftPush = 0.0;
			IBlockState bL = this.world.getBlockState(ray.getBlockPos());
			
			AxisAlignedBB ablf = bL.getCollisionBoundingBox(world, ray.getBlockPos());
			double blockHeight = (ablf.maxY - ablf.minY);
			
			if(!bL.isFullBlock()) {
				upMag = blockHeight+0.01;
				liftPush = Math.sqrt((blockHeight*blockHeight)) + 0.55;
				
				
			}
			
			if(bL.getBlock() instanceof BlockSnow || bL.getBlock() instanceof BlockSnowBlock) {
				upMag += 0.45;
			}
			
			
			
			
			
			BlockPos bTC = ray.getBlockPos();
			Vec3d sB = new Vec3d(bTC.getX(), bTC.getY()-1.0, bTC.getZ());
			Vec3d eB = new Vec3d(bTC.getX(), bTC.getY(), bTC.getZ());
			
			RayTraceResult heightRay = this.world.rayTraceBlocks(sB, eB, false, true, false);
			
			double opp = 0.0;
			double adj = 0.0;
			
			
			
			
			
			if(heightRay != null) {
				opp = heightRay.hitVec.y - getPositionVector().y;
				adj = heightRay.hitVec.subtract(ray.hitVec).lengthVector();
			}
			float t = 1.0f;
			float mu2 = (float) ((1 - Math.cos(t * Math.PI)) / 2f);
			System.out.println(liftPush);
			Vec3d lift = new Vec3d(0.0, upMag+0.55, 0.05+liftPush).rotateYaw((float) Math.toRadians(-rotationYaw)).scale(mu2);
			
			
			//getSolver().velocity = getSolver().getVelocityVector().addVector(0.0, lift.y/100, 0.0);
			getSolver().velocity = getSolver().getVelocityVector().scale(0.95);
			
			//System.out.println("lifting");
			// DEBUG // 
			
			liftOffset += (float) lift.y/2;
			this.move(MoverType.SELF, lift.x, lift.y, lift.z);
			float newPitch = (float) Math.toDegrees(Math.atan(upMag/(wheelbase/2)));
			
			// DEBUG //
			newPitch += -hillAngle;
			newPitch /= 2;
			
			targetUp = newPitch;
		
		}
		
		
		
		
		/*
		 * Calculates the pitch target by blending the up & down targets
		 */
		
		float adjT = 0.0f;
		if(targetDown == 0.0) {
			adjT = targetUp;
		} else if(targetUp == 0.0) {
			adjT = targetDown;
		} else {
			adjT = (targetDown + targetUp)/2.0f;
		}
		
		
		// DEBUG //
		//adjT = (float) -hillAngle;
		
		/*
		 * This code actually applies pitch, it ensures the "momentum" of the rotation is actually kept.
		 */
		
		
		
		
		if(dist > 0.5) {
			
			
			// DOWN PITCHING //
			
			if(Math.abs(adjT-rotationPitch) < 0.55) {
				
			} else if(adjT < rotationPitch) {

				rotationPitch -= Math.abs(adjT)*0.05;
			} else {

				rotationPitch += Math.abs(adjT)*0.5;
			}
		} else {
			
			
			// UP PITCHING // 
			
			
			
		
			if(adjT == 0.0) {
				adjT = -rotationPitch;
			}
			if(adjT < rotationPitch) {
				//System.out.println("force adj.");
				rotationPitch -= Math.abs(adjT)*0.3;
			} else {
				rotationPitch += Math.abs(adjT)*0.1;
			}
			
		}
		
		/*
		if(this.rotationPitch < 0) {
			Vec3d pitching = getSolver().getOreintationVector().scale(-1);
			this.move(MoverType.SELF, pitching.x, pitching.y, pitching.z);
		}*/
		
		doBlockCollisions();
		
		
	}
	
	public RayTraceResult castFromEntity(Vec3d direction) {
		Vec3d start = getPositionVector();
		Vec3d end = direction.normalize().add(start);
		return world.rayTraceBlocks(start, end, false, true, false);
	}
	
	public void stabilizeRotation() {
		double mag = 2.5;
		
		double boost = 0.25;
		RayTraceResult r1 = castFromEntity(new Vec3d(1*mag, 0, 1*mag));
		RayTraceResult r2 = castFromEntity(new Vec3d(-1*mag, 0, -1*mag));
		RayTraceResult r3 = castFromEntity(new Vec3d(-1*mag, 0, 1*mag));
		RayTraceResult r4 = castFromEntity(new Vec3d(1*mag, 0, -1*mag));
		RayTraceResult r5 = null;
				
		if(rotationPitch > 5) {
			r5 = castFromEntity(new Vec3d(0, -1*mag, 0));
		}
		
		if(r1 != null || r2 != null || r3 != null || r4 != null || r5 != null) {
			
			if(r1 != null) {
				move(MoverType.SELF, -1*boost, 0.0, -1*boost);
			}
			
			if(r2 != null) {
				move(MoverType.SELF, 1*boost, 0.0, 1*boost);
			}
			
			if(r3 != null) {
				move(MoverType.SELF, 1*boost, 0.0, -1.0*boost);
			}
			
			if(r4 != null) {
				move(MoverType.SELF, -1*boost, 0.0, 1.0*boost);
			}
			
			if(r5 != null) {
				move(MoverType.SELF, 0, 1*boost, 0);
			}
		}
		
		/*
		if(1+1==2) return;
		Quat4d bruh =  QuatUtil.rotate(Math.toRadians(rotationYaw), Math.toRadians(rotationPitch), Math.toRadians(rotationRoll));
		bruh.normalize();
		Vec3d v = QuatUtil.set(bruh, 0.0, 0.0, 0.0);
		
		rotationYaw = (float) Math.toDegrees(v.x);
		rotationPitch = (float)Math.toDegrees(v.y);
		rotationRoll = (float) Math.toDegrees(v.z);
		*/
		
	
	}
	
	public Vec3d[] calculateTerrainPlane() {
		Vec3d one = getWheelPlanePoint(new Vec3d(-1.7, 0.0, 1.75));
		Vec3d two = getWheelPlanePoint(new Vec3d(0.5, 0.0, 1.75));
		Vec3d three = getWheelPlanePoint(new Vec3d(-1.7, 0.0, -1.75));
		Vec3d four = getWheelPlanePoint(new Vec3d(0.5, 0.0, -1.75));
		return new Vec3d[] { one, two, three, four };
	}
	
	public Vec3d getWheelPlanePoint(Vec3d wheelPos) {
		Vec3d realPos = wheelPos.rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d endVec = realPos.subtract(0.0, 2.5, 0.0);
		
		RayTraceResult result = this.world.rayTraceBlocks(realPos, endVec, false, true, false);
		if(result != null) {
			return result.hitVec.subtract(getPositionVector());
		}
		return null;
	}
	
	
	
	
	/*
	 * PARTICLES
	 */
	@SideOnly(Side.CLIENT)
	public void handleDriveParticles() {
    	
    	
    	
    	
    	
		
		doExhaustParticles(true, new Vec3d(0.25, 0.5, -3.25), 0.5);
		doExhaustParticles(true, new Vec3d(-1.45, 0.5, -3.25), 0.5);
    	
		
		
		for(WheelSolver w : getSolver().wheels) {
			doWheelParticles(w);
		}
		
    
    	
    	
    	
    	
    	
	}
	
	
	double dist = 0.0;
	
	public double getRealSpeed() {
		return this.dist*20.0;
	}
	
	
	@SideOnly(Side.CLIENT)
	public void doExhaustParticles(boolean spitFire, Vec3d exhaustPosition, double exhaustWidth) {
		// exhaust particles
		
		
    	Vec3d posExhaust = exhaustPosition.rotatePitch((float) Math.toRadians(rotationPitch)).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
    	Vec3d partDirExhaust = new Vec3d(0.0, 0.3, -1).rotateYaw((float) Math.toRadians(-rotationYaw)).rotatePitch((float) Math.toRadians(rotationPitch)).scale(0.1);
    	for(int x = 0; x < 2+(solver.synthAccelFor/2); ++x) {
    		Minecraft.getMinecraft().effectRenderer.addEffect(new ExhaustParticle(this.world, posExhaust.x, posExhaust.y, posExhaust.z, partDirExhaust.x, partDirExhaust.y, partDirExhaust.z, 2));
       	 
    	}
    	
    	
    	
    	int max = 100;
    	int min = 0;
    	
    	int mult = 20;
    	Vec3d partDirExhaust2 = new Vec3d(0.0, 0.3, -1).rotateYaw((float) Math.toRadians(-rotationYaw)).rotatePitch((float) Math.toRadians(rotationPitch)).scale(2.0);
    	int ran = (int) Math.floor(Math.random()*(max-min+1)+min);
    	if(ran < 1) {
    		
    		
    		PositionedSound ps = new PositionedSoundRecord(getConfiguration().getBackfireSound().getSound(), SoundCategory.MASTER, 1.5f, 1.0f, (float) posX, (float) posY, (float) posZ);
    		Minecraft.getMinecraft().getSoundHandler().playSound(ps);
    		
    		
    		for(int x = 0; x < 20+(solver.synthAccelFor); ++x) {
    			//Vec3d pE = posExhaust.subtract(getPositionVector()).scale(1.0f+(Math.random()*0.5)).add(getPositionVector());
    			Vec3d pE = posExhaust;
    			Minecraft.getMinecraft().effectRenderer.addEffect(new VehicleExhaustFlameParticle(this.world, pE.x, pE.y, pE.z, partDirExhaust2.x*mult, 0, partDirExhaust2.z*mult));
    	       	 
        		//this.world.spawnParticle(EnumParticleTypes.FLAME, posExhaust.x, posExhaust.y, posExhaust.z, partDirExhaust.x, partDirExhaust.y, partDirExhaust.z, Block.getStateId(world.getBlockState(this.getPosition().down())));   
        	
    		}
    	}
    	
    	
    	// PAPAPPAPA
	}
	
	
	
	
	@SideOnly(Side.CLIENT)
	public void doWheelParticles(WheelSolver wSolve) {
		
		Vec3d wheelPosition = wSolve.relativePosition;
		boolean isDriveWheel = wSolve.isDriveWheel();
		float wheelAngle = (float) wSolve.wheelAngle;
		
		Vec3d realPos = wheelPosition.rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector());
		Vec3d direction = new Vec3d(0.0, 0.3, -1.0).rotateYaw(-wheelAngle).rotateYaw((float) Math.toRadians(-rotationYaw)).scale(0.1);
		double variation = 5;
		
		double reducer = 1;
		if(!isDriveWheel) {
			reducer = 2;
		}
		
		for(int x = 0; x < ((int) (solver.synthAccelFor+1)/reducer); ++x) {
    		this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, realPos.x + rand.nextGaussian()/(variation*12), realPos.y+ rand.nextGaussian()/variation, realPos.z+ rand.nextGaussian()/variation, direction.x, direction.y+0.1, direction.z, Block.getStateId(world.getBlockState(this.getPosition().down())));   
		}
		
		
		
		if(!isDriveWheel) return;
		//System.out.println(solver.getSideSlipAngle());
		
		
		if(/*solver.rearAxel.isHandbraking*/ Math.abs(solver.getSideSlipAngle()) > 0.1) {
    		Random rand = new Random();
        	for(int x = 0; x < (4 + ((int) Math.floor(Math.abs(solver.getSideSlipAngle())*4))); ++x) {
        		double gaus = rand.nextGaussian()/2;
        		
        		int id = 2;
        		if(getSolver().materialBelow != Material.ROCK) id = 1;
        		if(getSolver().materialBelow == Material.SAND) id = 4;
        		Minecraft.getMinecraft().effectRenderer.addEffect(new DriftCloudParticle(this.world, realPos.x+gaus, realPos.y+gaus, realPos.z+gaus, direction.x, direction.y, direction.z, id));
            	  
        	}
    	}
		
		
	}
	
	
	/**
	 * Easier to run, lighter load on computer.
	 */
	@SideOnly(Side.CLIENT)
	public void doGeneralParticles() {
		Vec3d partDir = new Vec3d(-steerangle*20, 0.3, -1).rotateYaw((float) Math.toRadians(-rotationYaw)).scale(0.1);
    	Vec3d posDir = new Vec3d(0, 0, -1).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector()); 	
		// drift particles
    	if(solver.rearAxel.isHandbraking) {
    		Random rand = new Random();
        	for(int x = 0; x < 4; ++x) {
        		double gaus = rand.nextGaussian()/2;
        		Minecraft.getMinecraft().effectRenderer.addEffect(new DriftSmokeFX(this.world, posDir.x+gaus, posDir.y+gaus, posDir.z+gaus, partDir.x, partDir.y, partDir.z));
            	  
        	}
    	}
    	
    	// drive dust
    	for(int x = 0; x < ((int) solver.synthAccelFor); ++x) {
    		this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, posDir.x + rand.nextGaussian()/2, posDir.y+ rand.nextGaussian()/15, posDir.z+ rand.nextGaussian()/2, partDir.x, partDir.y+0.1, partDir.z, Block.getStateId(world.getBlockState(this.getPosition().down())));   
    	}
	}
	
	
	public boolean isShifting = false;
	public int tickShiftAnim = 0;
	
	
	
	public void notifyOfShift(int toGear) {
		this.motionX = 0.0;
		this.isShifting = true;
		this.tickShiftAnim = 0;
		//setState(VehicleState.SHIFTING);
	}
	
	public boolean isInShift() {
		return this.isShifting;
	}
	
	public void tickShiftAnim() {
		tickShiftAnim += 1;
	}
	
	public RigidBody body;
	
	
	
	
	public void updateSuspension(WheelSolver solver) {
		Vec3d realPos = solver.relativePosition.rotatePitch((float) Math.toRadians(rotationPitch)).rotateYaw((float) Math.toRadians(-rotationYaw)).add(getPositionVector()).addVector(0.0, this.rideOffset, 0.0);
		RayTraceResult result = world.rayTraceBlocks(realPos.addVector(0.0, 3.0, 0.0), realPos.subtract(0.0, 8.0, 0.0));
		//System.out.println(result.hitVec);
		
		
		if(result != null) {
			
			double h =  Math.min(Math.abs((realPos.y - result.hitVec.y)) - solver.radius+0.4, 1);
			
			//h = -0.2;
			if(solver.actualRideHeight == 0.0) {
				solver.actualRideHeight = h;
			} else {
				solver.rideHeight = h;
			}
			
			
			
			
		}
	
	}
	
    /**
     * onUpdate handles the following: physics, control events, syncing, and overall is the
     * 'nucleus' of the vehicle class. (this is my favorite method!)
     */
	@Override
    public void onUpdate() {
		 
		
		
		try {
			
		
		this.prevRotationRoll = rotationRoll;
		this.prevRotationRollH = rotationRollH;
		
		doOBBCollision();
		
		updateOBB();
		
		
		updateSuspension(getSolver().frontAxel.leftWheel);
		updateSuspension(getSolver().frontAxel.rightWheel);
		updateSuspension(getSolver().rearAxel.leftWheel);
		updateSuspension(getSolver().rearAxel.rightWheel);
		
    	if(this.world.isRemote) {
    		/*
    		 * CLIENT SIDE
    		 */
    		
    		getSolver();
    		
    		// get the controlling passenger
    		if(!this.isBeingRidden()) return;
    		EntityPlayer player = (EntityPlayer) getControllingPassenger();
    		if(player == null) {
    			return;
    		}
    		
    		handleGeneralSound();
    		
    		// do particles
			handleDriveParticles();
    		
    		if(Minecraft.getMinecraft().player == player) {
    			/*
        		 * DRIVER SIDE
        		 */
    			
    			
    			// update steering
    			updateSteering((EntityPlayer) player);
    			
    			// update controls
    			updateControls();
    			
    			/*
    			this.rotationYaw += driftTuner/10;
    			if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
    				Vec3d v = new Vec3d(0, 0, 3.0).rotateYaw((float) Math.toRadians(-rotationYaw));
    				this.move(MoverType.SELF, v.x, v.y, v.z);
    			}*/
     			
    			
    			
    			
    			// run block collisions
    	        tickLerp();
    			super.onUpdate();
    			doBlockCollisions();
    			
    			
    			
    			
    			drivingAspect.onUpdate(this);
    	        getSuspensionStrategy().update(getSpeed(), lastYawDelta);
    			
    	        
    	      
    	        
    	        if(isEntityInsideOpaqueBlock()) {
    	        	System.out.println("REPORT!");
    	        }
    	        
    	        	//System.out.println(isCollided + " | " + isCollidedHorizontally + " | " + isCollidedVertically);
    	    	       
    	        
    	        
    	        doBlockCollisions();
    			
    			
    	     // run the physics solver
   			 Vec3d oldPos = getPositionVector();
   	        try {
   	        	for(int x = 0; x < 5; ++x) {
       				solver.updatePhysics();
       			}
   	        } catch(Exception e) {
   	        	e.printStackTrace();
   	        }
   	        
   	        doBlockCollisions();
    			
    			Vec3d newPos = getPositionVector();
    			this.dist = newPos.subtract(oldPos).lengthVector();
    			
    			 
    			
    			//hil
    			handleHillClimbing();
    			
    			
    			
    			
    			
    			//System.out.println("ID: " + getEntityId());
    			context.getChannel().getChannel().sendToServer(new VehicleControlPacket(new VehicleDataContainer(this)));
    			
    			//doNetworking(false);
    			
    			stabilizeRotation();
    			
    			
    		} else {
    			
    		
    			//doNetworking(true);
    			
    			
    			
    			
    		}
    		
    		
    		
    		
    	} else {
    		//doNetworking(true);
    	}
    	
    	if(this.world.isRemote) {
    		
    		if(this.motionX <= 2.0) {
    			this.motionX += 1.0;
    		} else this.motionX = 0;
    		
    		
    		
    		
    		this.drivingAspect = new VehicleDrivingAspect();
    		
    		if(this.motionX == 3.0) {
    			if(this.isShifting) this.isShifting = false;
    		}
    		
    	}
    	
    	
    	
    	//this.vehicleState = VehicleState.SHIFTING;
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	
	public static Material current;

	
	
	
    
    @SideOnly(Side.CLIENT)
    public void handleGeneralSound() {
    	Material mat = this.world.getBlockState(getPosition().down()).getMaterial();
    	
    	if(isInShift()) {
    		
    		PositionedSound ps = new PositionedSoundRecord(getConfiguration().getShiftSound().getSound(), SoundCategory.MASTER, 1.5f, 1.0f, (float) posX, (float) posY, (float) posZ);
    		Minecraft.getMinecraft().getSoundHandler().playSound(ps);
    	}
    	
    	
    	
    	
    	if(this.drivingSound != null) {
       	 if(this.drivingSound.isDonePlaying()) this.drivingSound = null;
    	}
    	
    	
    	this.driftingSound = null;
    	if(this.driftingSound != null) {
    	 if(this.driftingSound.isDonePlaying()) this.driftingSound = null;
    	 if(current != mat) this.driftingSound = null;
    	 
    	}
    	
    	if(this.drivingSound == null) {
        	this.drivingSound = new EngineMovingSound(getConfiguration().getRunSound(), soundPositionProvider, donePlayingSoundProvider, this, true);
        	Minecraft.getMinecraft().getSoundHandler().playSound(this.drivingSound);
        }
    	
    	
		
    	
    	if(this.driftingSound == null) {
    		
    		CompatibleSound chosen = null;
    		
    		
    		
    		if(mat == Material.GRASS || mat == Material.GROUND || mat == Material.CLAY) {
    			current = mat;
    			chosen = GeneralVehicleSounds.driftGround1;
    		} else if(mat == Material.ROCK) {
    			current = mat;
    			chosen = GeneralVehicleSounds.driftConcrete1;
    		} else {
    			chosen = GeneralVehicleSounds.driftGround1;
    		}
    		
    		chosen = GeneralVehicleSounds.driftConcrete1;
    		
    		this.driftingSound = new DriftMovingSound(GeneralVehicleSounds.driftGround1, soundPositionProvider, isDorifto, this, false);
    		Minecraft.getMinecraft().getSoundHandler().playSound(this.driftingSound);
    	}
    	
    }
    // Anata no haka ni kansei dorifuto
    
    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    


	


	@Override
	public <E extends ExtendedState<VehicleState>> void prepareTransaction(E finallyUpdateToState) {
		// TODO Auto-generated method stub
		
	}


	
	/*
	 * Non Binary Tag Compound
	 * Read & Writing 
	 */


	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	

	/**
	 * The holiest method which allows client comput'rs to 
	 * abs'rb the holy knowledge of the vehicle simulation 
	 *  did create on the mast'r comput'r.  Unf'rtunately, 
	 *  th're art so many issues in h're, despite me being
	 *   a cryptology fanatic! how ironic!

	 * @param slave
	 */
	public void doNetworking(boolean slave) {
		try {
			if(!slave) {
				// master's work uwu :3
				
				VehiclePhysicsSolver s = getSolver();
				
				getDataManager().get(VEHICLE_DAT).setData(getPositionVector(), throttle, driftTuner, isBraking, forwardLean, sideLean, wheelRotationAngle, steerangle);
				getDataManager().get(SOLVER_DAT).setData(s.synthAccelFor, s.synthAccelSide, s.velocity);
				
				getDataManager().setDirty(VEHICLE_DAT);
				getDataManager().setDirty(SOLVER_DAT);	
			} else {
				
				
				//System.out.println("je suis le slave! " + this.world.isRemote);
				
				// master please update me 
				getDataManager().get(VEHICLE_DAT).updateVehicle(this);
				getDataManager().get(SOLVER_DAT).updateSolver(this.solver);
				
				
				
			}
		} catch (Exception e) {};
		
		
		
		
	}

	@Override
	public void setContext(ModContext modContext) {
		this.context = modContext;
		
	}
	

	


	
    
    
}