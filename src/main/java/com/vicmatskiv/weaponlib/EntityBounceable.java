package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleAxisAlignedBB;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockPos;
import com.vicmatskiv.weaponlib.compatibility.CompatibleIEntityAdditionalSpawnData;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult.Type;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityBounceable extends Entity implements CompatibleIEntityAdditionalSpawnData {

    private static final int MAX_TICKS = 200;

    private static final Logger logger = LogManager.getLogger(EntityBounceable.class);

    @SuppressWarnings("unused")
    private ModContext modContext;

    private float gravityVelocity = 0.06f;

    private float slowdownFactor = 0.6f;
    private int ticksInAir;

    private EntityLivingBase thrower;
    protected int bounceCount;

    private float initialYaw;
    private float initialPitch;
    private float xRotation;
    private float yRotation;
    private float zRotation;
    private float xRotationChange;
    private float yRotationChange;
    private float zRotationChange;

    private float rotationSlowdownFactor = 0.99f;
    private float maxRotationChange = 30f;

    public EntityBounceable(ModContext modContext, World world, EntityLivingBase thrower) {
        super(world);

        this.thrower = thrower;
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(thrower.posX, thrower.posY + (double)thrower.getEyeHeight(), thrower.posZ, thrower.rotationYaw, thrower.rotationPitch);
        this.posX -= (double)(CompatibleMathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(CompatibleMathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        //this.yOffset = 0.0F;
        float f = 0.4F;
        this.motionX = (double)(-CompatibleMathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * CompatibleMathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionZ = (double)(CompatibleMathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * CompatibleMathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
        this.motionY = (double)(-CompatibleMathHelper.sin((this.rotationPitch + 0 /*this.func_70183_g()*/) / 180.0F * (float)Math.PI) * f);

        this.initialYaw = this.rotationYaw;
        this.initialPitch = this.rotationPitch;
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 1.3f, 10.0F);

        logger.debug("Throwing with rotation pitch {}, velocity {}, {}, {}", this.rotationPitch,
                this.motionX, this.motionY, this.motionZ);
    }

    public void setThrowableHeading(double motionX, double motionY, double motionZ, float velocity, float inaccuracy) {
        float f2 = CompatibleMathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= (double)f2;
        motionY /= (double)f2;
        motionZ /= (double)f2;
        motionX += this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        motionY += this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        motionZ += this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        motionX *= (double)velocity;
        motionY *= (double)velocity;
        motionZ *= (double)velocity;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        float f3 = CompatibleMathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(motionY, (double)f3) * 180.0D / Math.PI);
    }

    public EntityBounceable(World world) {
        super(world);
        setRotations();
    }

    private void setRotations() {
        xRotationChange = maxRotationChange * (float)rand.nextGaussian();
        yRotationChange = maxRotationChange * (float)rand.nextGaussian();
        zRotationChange = maxRotationChange * (float)rand.nextGaussian();
    }


    private EntityLivingBase getThrower() {
        return thrower;
    }

    @Override
    public void onUpdate() {
        if (!compatibility.world(this).isRemote && ticksExisted > MAX_TICKS) {
            this.setDead();
            return;
        }

        xRotation += xRotationChange;
        yRotation += yRotationChange;
        zRotation += zRotationChange;

        xRotationChange *= rotationSlowdownFactor;
        yRotationChange *= rotationSlowdownFactor;
        zRotationChange *= rotationSlowdownFactor;

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();

        ++this.ticksInAir;

        CompatibleVec3 vec3 = new CompatibleVec3(this.posX, this.posY, this.posZ);
        CompatibleVec3 vec31 = new CompatibleVec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        CompatibleRayTraceResult movingobjectposition = compatibility.rayTraceBlocks(this, vec3, vec31);

        vec3 = new CompatibleVec3(this.posX, this.posY, this.posZ);
        vec31 = new CompatibleVec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (movingobjectposition != null) {
            vec31 = CompatibleVec3.fromCompatibleVec3(movingobjectposition.getHitVec());
        }

        if (thrower != null) { //if(!this.worldObj.isRemote)
            Entity entity = null;
            List<?> list = compatibility.getEntitiesWithinAABBExcludingEntity(compatibility.world(this), this,
                    compatibility.getBoundingBox(this).addCoord(this.motionX, this.motionY, this.motionZ)
                    .expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            EntityLivingBase entitylivingbase = this.getThrower();

            CompatibleRayTraceResult entityMovingObjectPosition = null;
            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity)list.get(j);

                if (entity1.canBeCollidedWith() && (entity1 != entitylivingbase || this.ticksInAir >= 5)) {
                    float f = 0.3F;
                    CompatibleAxisAlignedBB axisalignedbb = compatibility.expandEntityBoundingBox(entity1, f, f, f);
                    CompatibleRayTraceResult movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3.distanceTo(movingobjectposition1.getHitVec()); //hitVec

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            entityMovingObjectPosition = movingobjectposition1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new CompatibleRayTraceResult(entity);
                movingobjectposition.setSideHit(entityMovingObjectPosition.getSideHit());
                movingobjectposition.setHitVec(entityMovingObjectPosition.getHitVec());
            }
        }

        logger.trace("Ori position to {}, {}, {}, ", this.posX, this.posY, this.posZ);

        if(movingobjectposition != null && (movingobjectposition.getTypeOfHit() == Type.BLOCK
                    || (movingobjectposition.getTypeOfHit() == Type.ENTITY))) {

            //TODO: remove logging since it's creating wrapper objects
            logger.trace("Hit {}, vec set to {}, {}, {}", movingobjectposition.getTypeOfHit(),
                    movingobjectposition.getHitVec().getXCoord(),
                    movingobjectposition.getHitVec().getYCoord(),
                    movingobjectposition.getHitVec().getZCoord());

            logger.trace("Before bouncing {}, side {}, motion set to {}, {}, {}", bounceCount,
                    movingobjectposition.getSideHit(),
                    motionX, motionY, motionZ);

            this.posX = movingobjectposition.getHitVec().getXCoord();
            this.posY = movingobjectposition.getHitVec().getYCoord();
            this.posZ = movingobjectposition.getHitVec().getZCoord();

            switch(movingobjectposition.getSideHit()) {
            case DOWN:
                this.motionY = -this.motionY;
                //this.posY += motionY;
                break;
            case UP:
                this.motionY = -this.motionY;
                break;
            case NORTH:
                this.motionZ = -this.motionZ;
                //this.posZ += motionZ;
                break;
            case SOUTH:
                this.motionZ = -this.motionZ;
                break;
            case WEST:
                this.motionX = -this.motionX;
                //this.posX += motionX;
                break;
            case EAST:
                this.motionX = -this.motionX;
                break;
            }

            if(movingobjectposition.getTypeOfHit() == Type.ENTITY) {
                avoidEntityCollisionAfterBounce(movingobjectposition);
            } else if(movingobjectposition.getTypeOfHit() == Type.BLOCK) {
                avoidBlockCollisionAfterBounce(movingobjectposition);
            }

            bounceCount++;
            logger.trace("After bouncing {}  motion set to {}, {}, {}", bounceCount, motionX, motionY, motionZ);
            onBounce(movingobjectposition);
            if(this.isDead) {
                return;
            }
        } else {
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
        }

        float motionSquared = CompatibleMathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)motionSquared) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f2 = 0.99F;
        float currentGravityVelocity = this.getGravityVelocity();

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f4 = 0.25F;
                compatibility.spawnParticle(compatibility.world(this),
                        "bubble", this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
            }

            f2 = 0.8F;
        }

        if(movingobjectposition != null &&
                (movingobjectposition.getTypeOfHit() == Type.BLOCK || movingobjectposition.getTypeOfHit() == Type.ENTITY)) {
            f2 = slowdownFactor;
            rotationSlowdownFactor = rotationSlowdownFactor * (slowdownFactor * 1.5f);
        }

        this.motionX *= (double)f2;
        this.motionY *= (double)f2;
        this.motionZ *= (double)f2;

        this.motionY -=  (double)currentGravityVelocity;

        this.setPosition(this.posX, this.posY, this.posZ);
        logger.trace("Set position to {}, {}, {}, ", this.posX, this.posY, this.posZ);
    }

    public void onBounce(CompatibleRayTraceResult movingobjectposition) {}

    private void avoidBlockCollisionAfterBounce(CompatibleRayTraceResult movingobjectposition) {
        if(movingobjectposition.getTypeOfHit() != Type.BLOCK) {
            return;
        }

        double dX = Math.signum(motionX) * 0.05;
        double dY = Math.signum(motionY) * 0.05;
        double dZ = Math.signum(motionZ) * 0.05;

        for(int i = 0; i < 10; i++) {
            CompatibleVec3 projectedPos = new CompatibleVec3(this.posX + dX * i, this.posY + dY * i, this.posZ + dZ * i);

            CompatibleBlockPos blockPos = new CompatibleBlockPos(projectedPos);

            CompatibleAxisAlignedBB projectedEntityBoundingBox = compatibility.getBoundingBox(this)
                    .offset(dX * i, dY * i, dZ * i);

            if(compatibility.isAirBlock(compatibility.world(this), blockPos) ||
                    !new CompatibleAxisAlignedBB(blockPos).intersectsWith(projectedEntityBoundingBox) ) {
                this.posX = projectedPos.getXCoord();
                this.posY = projectedPos.getYCoord();
                this.posZ = projectedPos.getZCoord();
                //logger.debug("Found non-intercepting post-bounce position on iteration {}", i);
                break;
            }
        }
    }

    private void avoidEntityCollisionAfterBounce(CompatibleRayTraceResult movingobjectposition) {

        if(movingobjectposition.getEntityHit() == null) {
            return;
        }

        slowdownFactor = 0.3f;
        double dX = Math.signum(motionX) * 0.05;
        double dY = Math.signum(motionY) * 0.05;
        double dZ = Math.signum(motionZ) * 0.05;

        float f = 0.3F;
        CompatibleAxisAlignedBB axisalignedbb = compatibility.getBoundingBox(movingobjectposition.getEntityHit())
                .expand((double)f, (double)f, (double)f);
        CompatibleRayTraceResult intercept = movingobjectposition;
        for(int i = 0; i < 10; i++) {
            CompatibleVec3 currentPos = new CompatibleVec3(this.posX + dX * i, this.posY + dY * i, this.posZ + dY * i);
            CompatibleVec3 projectedPos = new CompatibleVec3(this.posX + dX * (i + 1), this.posY + dY * (i + 1), this.posZ + dZ * (i + 1));
            intercept = axisalignedbb.calculateIntercept(currentPos, projectedPos);
            if(intercept == null) {
                //logger.debug("Found no-intercept after bounce with offsets {}, {}, {}", dX, dY, dZ);

                Block block;

                CompatibleBlockPos blockPos = new CompatibleBlockPos(projectedPos);
                if((block = compatibility.getBlockAtPosition(compatibility.world(this), blockPos)) != null
                        && !compatibility.isAirBlock(compatibility.world(this), blockPos)) {
                    logger.debug("Found non-intercept position colliding with block {}", block);
                    intercept = movingobjectposition;
                } else {
                    this.posX = projectedPos.getXCoord();
                    this.posY = projectedPos.getYCoord();
                    this.posZ = projectedPos.getZCoord();
                }

                break;
            }

            //logger.debug("Still intercepting after bounce, adjusting offsets to {}, {}, {}", dX, dY, dZ);
        }

        if(intercept != null) {
            logger.debug("Could not find non-intercept position after bounce");
        }
    }

    //@Override
    protected float getGravityVelocity() {
        return gravityVelocity;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(thrower != null ? thrower.getEntityId() : -1);
        buffer.writeFloat(gravityVelocity);
        buffer.writeFloat(initialYaw);
        buffer.writeFloat(initialPitch);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        int entityId = buffer.readInt();
        if(thrower == null && entityId >= 0) {
            Entity entity = compatibility.world(this).getEntityByID(entityId);
            if(entity instanceof EntityLivingBase) {
                this.thrower = (EntityPlayer) entity;
            }
        }
        gravityVelocity = buffer.readFloat();
        initialYaw = buffer.readFloat();
        initialPitch = buffer.readFloat();
    }

    public float getXRotation() {
        return xRotation;
    }

    public float getYRotation() {
        return yRotation - initialYaw - 90f;
    }

    public float getZRotation() {
        return zRotation;
    }

}