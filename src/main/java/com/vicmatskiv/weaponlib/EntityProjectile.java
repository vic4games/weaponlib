package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleAxisAlignedBB;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleIEntityAdditionalSpawnData;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTracing;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class EntityProjectile extends Entity implements IProjectile, CompatibleIEntityAdditionalSpawnData {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(EntityProjectile.class);

    private static final String TAG_GRAVITY_VELOCITY = "gravityVelocity";

    private static final int MAX_TICKS = 200;

    private static final int DEFAULT_MAX_LIFETIME = 5000;

    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    //private CompatibleBlockState field_145785_f;
    protected boolean inGround;
    public int throwableShake;

    protected EntityLivingBase thrower;
    private String throwerName;
    //private int ticksInGround;
    private int ticksInAir;

    protected float gravityVelocity;
    protected float velocity;
    protected float inaccuracy;

    private long timestamp;
    
    private double aimTan;

    protected long maxLifetime = DEFAULT_MAX_LIFETIME;

    public EntityProjectile(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.timestamp = System.currentTimeMillis();
    }

    public EntityProjectile(World world, EntityLivingBase thrower, float velocity, float gravityVelocity, float inaccuracy) {
        this(world);
        this.thrower = thrower;
        this.velocity = velocity;
        this.gravityVelocity = gravityVelocity;
        this.inaccuracy = inaccuracy;
//        
//        this.posX = thrower.posX;
//        this.posY = thrower.posY + (double)thrower.getEyeHeight() - 0.10000000149011612D;
//        this.posZ = thrower.posZ;
        
//        if(thrower != null) {
//            RayTraceResult rayTraceResult = thrower.rayTrace(50, 0);
//            if(rayTraceResult != null && rayTraceResult.hitVec != null) {
//                double dx = compatibility.clientPlayer().posX - rayTraceResult.hitVec.x;
//                double dy = compatibility.clientPlayer().posY - rayTraceResult.hitVec.y;
//                double dz = compatibility.clientPlayer().posZ - rayTraceResult.hitVec.z;
//                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
//                this.aimTan = 0.4 / distance;
//            }
//        }
    }
    
//    public void setAim(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy)
//    {
//        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
//        float f1 = -MathHelper.sin(pitch * 0.017453292F);
//        float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
//        this.setThrowableHeading((double)f, (double)f1, (double)f2, velocity, inaccuracy);
//        this.motionX += shooter.motionX;
//        this.motionZ += shooter.motionZ;
//
//        if (!shooter.onGround)
//        {
//            this.motionY += shooter.motionY;
//        }
//    }
//
//    /**
//     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
//     */
//    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
//    {
//        float f = MathHelper.sqrt(x * x + y * y + z * z);
//        x = x / (double)f;
//        y = y / (double)f;
//        z = z / (double)f;
//        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
//        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
//        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
//        x = x * (double)velocity;
//        y = y * (double)velocity;
//        z = z * (double)velocity;
//        this.motionX = x;
//        this.motionY = y;
//        this.motionZ = z;
//        float f1 = MathHelper.sqrt(x * x + z * z);
//        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
//        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
//        this.prevRotationYaw = this.rotationYaw;
//        this.prevRotationPitch = this.rotationPitch;
////        this.ticksInGround = 0;
//    }


    public void setPositionAndDirection() {

        this.setLocationAndAngles(thrower.posX, thrower.posY + (double) thrower.getEyeHeight(),
                thrower.posZ, compatibility.getCompatibleAimingRotationYaw(thrower), thrower.rotationPitch);

        this.posX -= (double) (CompatibleMathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posY = thrower.posY + (double)thrower.getEyeHeight() - 0.10000000149011612D;
        this.posZ -= (double) (CompatibleMathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);

        //this.yOffset = 0.0F; TODO: verify how this works in 1.7.10
        float f = velocity; //0.4F;
        this.motionX = (double) (-CompatibleMathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI)
                * CompatibleMathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
        this.motionZ = (double) (CompatibleMathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI)
                * CompatibleMathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
        this.motionY = (double) (-CompatibleMathHelper
                .sin((this.rotationPitch + this.getPitchOffset()) / 180.0F * (float) Math.PI) * f);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity, inaccuracy);
    }
    
    public void setPositionAndDirection(double x, double y, double z, float rotationYaw, float rotationPitch) {

        this.setLocationAndAngles(x, y + (double) thrower.getEyeHeight(), z, rotationYaw, rotationPitch);

        this.posX -= (double) (CompatibleMathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
//        this.posY -= 0.10000000149011612D;
        this.posY = thrower.posY + (double)thrower.getEyeHeight() - 0.10000000149011612D;
        this.posZ -= (double) (CompatibleMathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);

        //this.yOffset = 0.0F; TODO: verify how this works in 1.7.10
        float f = velocity; //0.4F;
        this.motionX = (double) (-CompatibleMathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI)
                * CompatibleMathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
        this.motionZ = (double) (CompatibleMathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI)
                * CompatibleMathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
        this.motionY = (double) (-CompatibleMathHelper
                .sin((this.rotationPitch + this.getPitchOffset()) / 180.0F * (float) Math.PI) * f);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity, inaccuracy);
    }

    public EntityProjectile(World world, double posX, double posY, double posZ) {
        super(world);
        //this.ticksInGround = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(posX, posY, posZ);
        //this.yOffset = 0.0F; // TODO: verify how it works in 1.7.10
    }

    protected float getPitchOffset() {
        return 0.0F;
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z
     * direction.
     */
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float f2 = CompatibleMathHelper
                .sqrt_double(x * x + y * y + z * z);
        x /= (double) f2;
        y /= (double) f2;
        z /= (double) f2;
        x += this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        y += this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        z += this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        x *= (double) velocity;
        y *= (double) velocity;
        z *= (double) velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f3 = CompatibleMathHelper.sqrt_double(x * x + z * z);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(y, (double) f3) * 180.0D / Math.PI);
        //this.ticksInGround = 0;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    // @SideOnly(Side.CLIENT)
    public void setVelocity(double mX, double mY, double mZ) {
        this.motionX = mX;
        this.motionY = mY;
        this.motionZ = mZ;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = CompatibleMathHelper.sqrt_double(mX * mX + mZ * mZ);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(mX, mZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(mY, (double) f) * 180.0D
                    / Math.PI);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        if(ticksExisted > MAX_TICKS) {
            setDead();
            return;
        }
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();

        if (this.throwableShake > 0) {
            --this.throwableShake;
        }

        if (this.inGround) {
//            CompatibleBlockPos p = new CompatibleBlockPos(this.xTile, this.yTile, this.zTile);
//            if (compatibility.getBlockAtPosition(compatibility.world(this), p) == this.field_145785_f) {
//                ++this.ticksInGround;
//
//                if (this.ticksInGround == 1200) {
//                    this.setDead();
//                }
//
//                return;
//            }

            this.inGround = false;
            this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
            this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
            //this.ticksInGround = 0;
            this.ticksInAir = 0;
        } else {
            ++this.ticksInAir;
        }

        CompatibleVec3 vec3 = new CompatibleVec3(this.posX, this.posY, this.posZ);
        CompatibleVec3 vec31 = new CompatibleVec3(this.posX + this.motionX, this.posY + this.motionY,
                this.posZ + this.motionZ);

        CompatibleRayTraceResult movingobjectposition = CompatibleRayTracing.rayTraceBlocks(compatibility.world(this),
                vec3, vec31,
                (block, blockMetadata) -> canCollideWithBlock(block, blockMetadata));

        vec3 = new CompatibleVec3(this.posX, this.posY, this.posZ);
        vec31 = new CompatibleVec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (movingobjectposition != null) {
            vec31 = CompatibleVec3.fromCompatibleVec3(movingobjectposition.getHitVec());
        }

        if (!compatibility.world(this).isRemote) {
            Entity entity = getRayTraceEntities(vec3, vec31);

            if (entity != null) {
                movingobjectposition = new CompatibleRayTraceResult(entity);
            }
        }

        if (movingobjectposition != null) {
            this.onImpact(movingobjectposition);
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f1 = CompatibleMathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f1) * 180.0D / Math.PI); this.rotationPitch
                - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            ;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f2 = 0.99F;
        float f3 = gravityVelocity; //this.getGravityVelocity();

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f4 = 0.25F;
                compatibility.spawnParticle(compatibility.world(this), "bubble", this.posX - this.motionX * (double) f4,
                        this.posY - this.motionY * (double) f4, this.posZ - this.motionZ * (double) f4, this.motionX,
                        this.motionY, this.motionZ);
            }

            f2 = 0.8F;
        }

        this.motionX *= (double) f2;
        this.motionY *= (double) f2;
        this.motionZ *= (double) f2;
        this.motionY -= (double) f3;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    private Entity getRayTraceEntities(CompatibleVec3 vec3, CompatibleVec3 vec31) {
        Entity entity = null;
        List<?> list = compatibility.getEntitiesWithinAABBExcludingEntity(compatibility.world(this), this,
                compatibility.getBoundingBox(this).addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D,
                        1.0D, 1.0D));
        double d0 = 0.0D;
        EntityLivingBase entitylivingbase = this.getThrower();

        for (int j = 0; j < list.size(); ++j) {
            Entity entity1 = (Entity) list.get(j);

            if (entity1.canBeCollidedWith() && (entity1 != entitylivingbase || this.ticksInAir >= 5)) {
                float f = 0.3F;
                CompatibleAxisAlignedBB axisalignedbb = compatibility.expandEntityBoundingBox(entity1, (double) f,
                        (double) f, (double) f);
                CompatibleRayTraceResult movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

                if (movingobjectposition1 != null) {
                    double d1 = vec3.distanceTo(movingobjectposition1.getHitVec());

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected abstract void onImpact(CompatibleRayTraceResult p_70184_1_);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setLong("timestamp", timestamp);
        tagCompound.setShort("xTile", (short) this.xTile);
        tagCompound.setShort("yTile", (short) this.yTile);
        tagCompound.setShort("zTile", (short) this.zTile);
        //tagCompound.setByte("inTile", (byte) Block.getIdFromBlock(this.field_145785_f));
        tagCompound.setByte("shake", (byte) this.throwableShake);
        tagCompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));

        if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower != null
                && this.thrower instanceof EntityPlayer) {
            this.throwerName = compatibility.getPlayerName((EntityPlayer)this.thrower);
        }

        tagCompound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
        tagCompound.setFloat(TAG_GRAVITY_VELOCITY, gravityVelocity);
        tagCompound.setDouble("aimTan", this.aimTan);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompound) {

        this.xTile = tagCompound.getShort("xTile");
        this.yTile = tagCompound.getShort("yTile");
        this.zTile = tagCompound.getShort("zTile");
        //this.field_145785_f = Block.getBlockById(tagCompound.getByte("inTile") & 255);
        this.throwableShake = tagCompound.getByte("shake") & 255;
        this.inGround = tagCompound.getByte("inGround") == 1;
        this.throwerName = tagCompound.getString("ownerName");

        if (this.throwerName != null && this.throwerName.length() == 0) {
            this.throwerName = null;
        }
        this.gravityVelocity = tagCompound.getFloat(TAG_GRAVITY_VELOCITY);
        this.timestamp = tagCompound.getLong("timestamp");
        this.aimTan = tagCompound.getDouble("aimTan");

        if(System.currentTimeMillis() > timestamp + maxLifetime) {
            setDead();
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(gravityVelocity);
        buffer.writeDouble(aimTan);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        gravityVelocity = buffer.readFloat();
        aimTan = buffer.readDouble();
    }

    // @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }
    
    public double getAimTan() {
        return aimTan;
    }

    public EntityLivingBase getThrower() {
        if (this.thrower == null && this.throwerName != null && this.throwerName.length() > 0) {
            this.thrower = compatibility.world(this).getPlayerEntityByName(this.throwerName);
        }

        return this.thrower;
    }


    protected void entityInit() {
    }

    /**
     * Checks if the entity is in range to render by using the past in distance
     * and comparing it to its average edge length * 64 * renderDistanceWeight
     * Args: distance
     */
    // @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        double d1 = compatibility.getBoundingBox(this).getAverageEdgeLength() * 4.0D;
        d1 *= 64.0D;
        return p_70112_1_ < d1 * d1;
    }

    public boolean canCollideWithBlock(Block block, CompatibleBlockState metadata) {
        return compatibility.canCollideCheck(block, metadata, false);
    }
}