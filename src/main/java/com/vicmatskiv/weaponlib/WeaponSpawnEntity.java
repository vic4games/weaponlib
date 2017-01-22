package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class WeaponSpawnEntity extends EntityThrowable implements IEntityAdditionalSpawnData {
	
	static final float DEFAULT_INACCURACY = 1f;
	private float explosionRadius = 0.1F;
	private float damage = 6f;
	private float speed;
	private float gravityVelocity;
	private float inaccuracy;
	private Weapon weapon;
    //private int ticksInGround;
    private int ticksInAir;

	public WeaponSpawnEntity(World world) {
		super(world);
	}

	/**
	 * @param par1World
	 * @param arg1EntityLivingBase
	 */
	public WeaponSpawnEntity(World par1World, EntityLivingBase arg1EntityLivingBase) {
		super(par1World, arg1EntityLivingBase);
	}
	
	public WeaponSpawnEntity(Weapon weapon, 
			World par1World, 
			EntityLivingBase throwerIn, 
			float speed,
			float gravityVelocity,
			float inaccuracy,
			float damage, 
			float explosionRadius,
			Material...damageableBlockMaterials) 
	{
		super(par1World, throwerIn);
		
		this.weapon = weapon;
		this.damage = damage;
		this.speed = speed;
		this.inaccuracy = inaccuracy;
		this.gravityVelocity = gravityVelocity;
		this.explosionRadius = explosionRadius;
		
		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight(), throwerIn.posZ, throwerIn.rotationYaw, throwerIn.rotationPitch);
		this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.posY -= 0.10000000149011612D;
		this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		float f = 0.4F;
		this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
		this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
		float pitchOffset = 0f;
		this.motionY = (double)(-MathHelper.sin((this.rotationPitch + pitchOffset) / 180.0F * (float)Math.PI) * f);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, inaccuracy);
	}

	@Override
	protected float getGravityVelocity() {
		return gravityVelocity;
	};
	
	/**
	 * @see net.minecraft.entity.projectile.EntityThrowable#onImpact(net.minecraft.util.MovingObjectPosition)
	 */
	@Override
	protected void onImpact(RayTraceResult position) {
		if(!this.worldObj.isRemote) {
			if (position.entityHit != null) {
				if(explosionRadius > 0) {
					this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, true);
				}
				//System.out.println(">>>>>>   Damaging entity " + position.entityHit + " >>>>>> !!!");
				position.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
				position.entityHit.hurtResistantTime = 0;
				position.entityHit.prevRotationYaw -= 0.3D;
			} else if(explosionRadius > 0) {
				this.worldObj.createExplosion(this, position.getBlockPos().getX(), 
						position.getBlockPos().getY(), position.getBlockPos().getZ(), explosionRadius, true);
			} else if(position.typeOfHit == RayTraceResult.Type.BLOCK) {
				weapon.onSpawnEntityBlockImpact(worldObj, null, this, position);
			}
			this.setDead();
		}
	}
	
	@Override
	public void setThrowableHeading(double motionX, double motionY, double motionZ, float velocity, float inaccuracy)
    {
        float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
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
        float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(motionY, (double)f3) * 180.0D / Math.PI);
    }

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(Item.getIdFromItem(weapon));
		buffer.writeFloat(speed);
		buffer.writeFloat(gravityVelocity);
		buffer.writeFloat(inaccuracy);
		buffer.writeFloat(damage);
		buffer.writeFloat(explosionRadius);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		weapon = (Weapon) Item.getItemById(buffer.readInt());
		speed = buffer.readFloat();
		gravityVelocity = buffer.readFloat();
		inaccuracy = buffer.readFloat();
		damage = buffer.readFloat();
		explosionRadius = buffer.readFloat();
	}

	Weapon getWeapon() {
		return weapon;
	}

	boolean isDamageableEntity(Entity entity) {
		return false;
	}
	
	public void onUpdate()
    {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        if (this.throwableShake > 0)
        {
            --this.throwableShake;
        }

        if (this.inGround)
        {
            /*if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile)
            {
                ++this.ticksInGround;

                if (this.ticksInGround == 1200)
                {
                    this.setDead();
                }

                return;
            }

            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;*/
        }
        else
        {
            ++this.ticksInAir;
        }

        Vec3d vec3 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec31 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
        vec3 = new Vec3d(this.posX, this.posY, this.posZ);
        vec31 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (movingobjectposition != null)
        {
            vec31 = new Vec3d(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
        }

        if (!this.worldObj.isRemote)
        {
            Entity entity = null;
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            EntityLivingBase entitylivingbase = this.getThrower();

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity1 = (Entity)list.get(j);

                if (entity1.canBeCollidedWith() && (entity1 != entitylivingbase || this.ticksInAir >= 5))
                {
                    float f = 0.3F;
                    net.minecraft.util.math.AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f, (double)f, (double)f);
                    RayTraceResult movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

                    if (movingobjectposition1 != null)
                    {
                        double d1 = vec3.squareDistanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    } /*else {
                    	System.out.println("Vector " + vec3 + " " + vec31 + " does not intercept with " + entity1);
                    }*/
                }
            }

            if (entity != null)
            {
                movingobjectposition = new RayTraceResult(entity);
            }
        }

        if (movingobjectposition != null)
        {
            if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK && this.worldObj.getBlockState(movingobjectposition.getBlockPos()).getBlock() == Blocks.PORTAL)
            {
                this.setPortal(movingobjectposition.getBlockPos());
            }
            else
            {
                this.onImpact(movingobjectposition);
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f1) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
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
        float f3 = this.getGravityVelocity();

        if (this.isInWater())
        {
            for (int i = 0; i < 4; ++i)
            {
                float f4 = 0.25F;
                this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ, new int[0]);
            }

            f2 = 0.8F;
        }

        this.motionX *= (double)f2;
        this.motionY *= (double)f2;
        this.motionZ *= (double)f2;
        this.motionY -= (double)f3;
        this.setPosition(this.posX, this.posY, this.posZ);
    }
}