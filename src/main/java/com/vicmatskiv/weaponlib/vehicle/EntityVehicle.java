package com.vicmatskiv.weaponlib.vehicle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.vicmatskiv.weaponlib.Configurable;
import com.vicmatskiv.weaponlib.EntityClassFactory;
import com.vicmatskiv.weaponlib.animation.Randomizer;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMovingSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;
import com.vicmatskiv.weaponlib.state.ExtendedState;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
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
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityVehicle extends Entity implements Configurable<EntityVehicleConfiguration>, ExtendedState<VehicleState>
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
    private EntityVehicle.Status status;
    private EntityVehicle.Status previousStatus;
    private double lastYd;
    
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

    public EntityVehicle(World worldIn)
    {
        super(worldIn);
        this.paddlePositions = new float[2];
        this.preventEntitySpawning = true;
        this.setSize(1.375F, 0.5625F);
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
                        this.dropItemWithOffset(this.getItemBoat(), 1, 0.0F);
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

    public Item getItemBoat()
    {
        switch (this.getBoatType())
        {
            case OAK:
            default:
                return Items.BOAT;
            case SPRUCE:
                return Items.SPRUCE_BOAT;
            case BIRCH:
                return Items.BIRCH_BOAT;
            case JUNGLE:
                return Items.JUNGLE_BOAT;
            case ACACIA:
                return Items.ACACIA_BOAT;
            case DARK_OAK:
                return Items.DARK_OAK_BOAT;
        }
    }

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

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        updateDriverInteractionEvent();
        
        this.previousStatus = this.status;
        this.status = this.getBoatStatus();

        if (this.status != EntityVehicle.Status.UNDER_WATER && this.status != EntityVehicle.Status.UNDER_FLOWING_WATER)
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
        this.tickLerp();

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
//                this.world.sendPacketToServer(new CPacketSteerBoat(this.getPaddleState(0), this.getPaddleState(1)));
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
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
//                        this.applyEntityCollision(entity);
                    }
                }
            }
        }
        
        if(this.world.isRemote) {
            drivingAspect.onUpdate(this);
            getSuspensionStrategy().update(speed, lastYawDelta);
            handleLoopingSoundEffects();
        } else {
            handleServerSounds();
        }
    }
    
    public void handleLoopingSoundEffects() {

        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
        CompatibleSound drivingSound = getConfiguration().getRunSound();
        CompatibleSound idleSound = getConfiguration().getRunSound();

        if (this.drivingSound == null && driverInteractionEvent == DriverInteractionEvent.DRIVING 
                && getState() != VehicleState.IDLE
                && drivingSound != null && soundHandler != null) {
            this.drivingSound = new CompatibleMovingSound(drivingSound, soundPositionProvider, drivingSoundVolumeProvider, donePlayingSoundProvider);
            soundHandler.playSound(this.drivingSound);
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
            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpXRot - (double)this.rotationPitch) / (double)this.lerpSteps);
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
    private EntityVehicle.Status getBoatStatus()
    {
        EntityVehicle.Status entityboat$status = this.getUnderwaterStatus();

        if (entityboat$status != null)
        {
            this.waterLevel = this.getEntityBoundingBox().maxY;
            return entityboat$status;
        }
        else if (this.checkInWater())
        {
//            this.waterLevel = this.getEntityBoundingBox().maxY; // TODO: handle water level
            return EntityVehicle.Status.IN_WATER;
        }
        else
        {
            float f = this.getBoatGlide();

            if (f > 0.0F)
            {
                this.boatGlide = f;
                return EntityVehicle.Status.ON_LAND;
            }
            else
            {
                return EntityVehicle.Status.IN_AIR;
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
    private EntityVehicle.Status getUnderwaterStatus()
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
                                EntityVehicle.Status entityboat$status = EntityVehicle.Status.UNDER_FLOWING_WATER;
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

        return flag ? EntityVehicle.Status.UNDER_WATER : null;
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

        if (this.previousStatus == EntityVehicle.Status.IN_AIR && this.status != EntityVehicle.Status.IN_AIR && this.status != EntityVehicle.Status.ON_LAND)
        {
            this.waterLevel = this.getEntityBoundingBox().minY + (double)this.height;
            this.setPosition(this.posX, (double)(this.getWaterLevelAbove() - this.height) + 0.101D, this.posZ);
            this.motionY = 0.0D;
            this.lastYd = 0.0D;
            this.status = EntityVehicle.Status.IN_WATER;
        }
        else
        {
            if (this.status == EntityVehicle.Status.IN_WATER)
            {
                //d2 = (this.waterLevel - this.getEntityBoundingBox().minY) / (double)this.height;
                d2 = (4.5 /*this.waterLevel*/ - this.getEntityBoundingBox().minY) / (double)this.height;
                this.momentum = 0.9F;
            }
            else if (this.status == EntityVehicle.Status.UNDER_FLOWING_WATER)
            {
                d1 = -7.0E-4D;
                this.momentum = 0.9F;
            }
            else if (this.status == EntityVehicle.Status.UNDER_WATER)
            {
                d2 = 0.009999999776482582D;
                this.momentum = 0.45F;
            }
            else if (this.status == EntityVehicle.Status.IN_AIR)
            {
                this.momentum = 0.9F;
            }
            else if (this.status == EntityVehicle.Status.ON_LAND)
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
        
        speed = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
        
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
            passenger.setPosition(this.posX + vec3d.x, this.posY + (double)f1, this.posZ + vec3d.z);
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
            this.setBoatType(EntityVehicle.Type.getTypeFromString(compound.getString("Type")));
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
                    if (this.status != EntityVehicle.Status.ON_LAND)
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

    public void setBoatType(EntityVehicle.Type boatType)
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
        public static EntityVehicle.Type byId(int id)
        {
            if (id < 0 || id >= values().length)
            {
                id = 0;
            }

            return values()[id];
        }

        public static EntityVehicle.Type getTypeFromString(String nameIn)
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
    
    
    @Override
    public void move(MoverType type, double x, double y, double z)
    {
        if (this.noClip)
        {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        }
        else
        {
            this.world.profiler.startSection("move");

            double requestedXOffset = x;
            double requestedYOffset = y;
            double requestedZOffset = z;

            List<AxisAlignedBB> list1 = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(x, y, z));
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();

            if (y != 0.0D)
            {
                int k = 0;

                for (int l = list1.size(); k < l; ++k)
                {
                    y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(this.getEntityBoundingBox(), y);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            }

            if (x != 0.0D)
            {
                int j5 = 0;

                for (int l5 = list1.size(); j5 < l5; ++j5)
                {
                    x = ((AxisAlignedBB)list1.get(j5)).calculateXOffset(this.getEntityBoundingBox(), x);
                }

                if (x != 0.0D)
                {
                    this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
                }
            }

            if (z != 0.0D)
            {
                int k5 = 0;

                for (int i6 = list1.size(); k5 < i6; ++k5)
                {
                    z = ((AxisAlignedBB)list1.get(k5)).calculateZOffset(this.getEntityBoundingBox(), z);
                }

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
             */

            boolean flag = this.onGround || requestedYOffset != y && requestedYOffset < 0.0D;

            this.stepHeight = 1f;
            if (this.stepHeight > 0.0F && (flag || true)  && (requestedXOffset != x || requestedZOffset != z))
            {
                double originalX = x;
                double originalY = y;
                double originalZ = z;
                AxisAlignedBB axisalignedbb1 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(axisalignedbb);
                y = (double)this.stepHeight;
                List<AxisAlignedBB> list = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(requestedXOffset, y, requestedZOffset));
                AxisAlignedBB axisalignedbb2 = this.getEntityBoundingBox();
                AxisAlignedBB axisalignedbb3 = axisalignedbb2.expand(requestedXOffset, 0.0D, requestedZOffset);
                double anotherYOffset = y;
                int j1 = 0;

                for (int k1 = list.size(); j1 < k1; ++j1)
                {
                    anotherYOffset = ((AxisAlignedBB)list.get(j1)).calculateYOffset(axisalignedbb3, anotherYOffset);
                }

                axisalignedbb2 = axisalignedbb2.offset(0.0D, anotherYOffset, 0.0D);
                double anotherXOffset = requestedXOffset;
                int l1 = 0;

                for (int i2 = list.size(); l1 < i2; ++l1)
                {
                    anotherXOffset = ((AxisAlignedBB)list.get(l1)).calculateXOffset(axisalignedbb2, anotherXOffset);
                }

                axisalignedbb2 = axisalignedbb2.offset(anotherXOffset, 0.0D, 0.0D);
                double anotherZOffset = requestedZOffset;
                int j2 = 0;

                for (int k2 = list.size(); j2 < k2; ++j2)
                {
                    anotherZOffset = ((AxisAlignedBB)list.get(j2)).calculateZOffset(axisalignedbb2, anotherZOffset);
                }

                axisalignedbb2 = axisalignedbb2.offset(0.0D, 0.0D, anotherZOffset);
                AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
                double calcYOffset = y;
                int l2 = 0;

                for (int i3 = list.size(); l2 < i3; ++l2)
                {
                    calcYOffset = ((AxisAlignedBB)list.get(l2)).calculateYOffset(axisalignedbb4, calcYOffset);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, calcYOffset, 0.0D);
                double calcXOffset = requestedXOffset;
                int j3 = 0;

                for (int k3 = list.size(); j3 < k3; ++j3)
                {
                    calcXOffset = ((AxisAlignedBB)list.get(j3)).calculateXOffset(axisalignedbb4, calcXOffset);
                }

                axisalignedbb4 = axisalignedbb4.offset(calcXOffset, 0.0D, 0.0D);
                double calcZOffset = requestedZOffset;
                int l3 = 0;

                for (int i4 = list.size(); l3 < i4; ++l3)
                {
                    calcZOffset = ((AxisAlignedBB)list.get(l3)).calculateZOffset(axisalignedbb4, calcZOffset);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, calcZOffset);
                double anotherSqDistance = anotherXOffset * anotherXOffset + anotherZOffset * anotherZOffset;
                double calcSqDistance = calcXOffset * calcXOffset + calcZOffset * calcZOffset;

                if (anotherSqDistance > calcSqDistance)
                {
                    x = anotherXOffset;
                    z = anotherZOffset;
                    y = -anotherYOffset;
                    this.setEntityBoundingBox(axisalignedbb2);
                }
                else
                {
                    x = calcXOffset;
                    z = calcZOffset;
                    y = -calcYOffset;
                    this.setEntityBoundingBox(axisalignedbb4);
                }

                int j4 = 0;

                for (int k4 = list.size(); j4 < k4; ++j4)
                {
                    y = ((AxisAlignedBB)list.get(j4)).calculateYOffset(this.getEntityBoundingBox(), y);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

                if (originalX * originalX + originalZ * originalZ >= x * x + z * z)
                {
                    x = originalX;
                    y = originalY;
                    z = originalZ;
                    this.setEntityBoundingBox(axisalignedbb1);
                }
            }

            this.world.profiler.endSection();
            this.world.profiler.startSection("rest");
            this.resetPositionToBB();
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

            if (requestedXOffset != x)
            {
                this.motionX = 0.0D;
            }

            if (requestedZOffset != z)
            {
                this.motionZ = 0.0D;
            }

            Block block = iblockstate.getBlock();

            if (requestedYOffset != y)
            {
                block.onLanded(this.world, this);
            }

            try
            {
                this.doBlockCollisions();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            this.world.profiler.endSection();
        }
    }
}