package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class WeaponSpawnEntity extends EntityThrowable implements IEntityAdditionalSpawnData {
	
	static final float DEFAULT_INACCURACY = 1f;
	private float explosionRadius = 0.1F;
	private float damage = 6f;
	private float speed;
	private float gravityVelocity;
	private Weapon weapon;

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
			EntityLivingBase arg1EntityLivingBase, 
			float speed, 
			float gravityVelocity,
			float damage, 
			float explosionRadius,
			Material...damageableBlockMaterials) 
	{
		super(par1World, arg1EntityLivingBase);
		this.weapon = weapon;
		this.damage = damage;
		this.explosionRadius = explosionRadius;
		this.speed = speed;
		this.gravityVelocity = gravityVelocity;
	
	}

	@Override
	protected float getGravityVelocity() {
		return gravityVelocity;
	};
	
	
//	@Override
	
//  TODO: FIXME!!!!!!!!!
	
	protected float getVelocity() {
		return speed;
	}

	/**
	 * @see net.minecraft.entity.projectile.EntityThrowable#onImpact(net.minecraft.util.MovingObjectPosition)
	 */
	@Override
	protected void onImpact(RayTraceResult position) {
		//this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius, true);
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
	public void setThrowableHeading(double motionX, double motionY, double motionZ, float velocity, float ignoredInaccuracy)
    {
        float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= (double)f2;
        motionY /= (double)f2;
        motionZ /= (double)f2;
        float inaccuracy = getInaccuracy();
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
		buffer.writeFloat(damage);
		buffer.writeFloat(explosionRadius);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		weapon = (Weapon) Item.getItemById(buffer.readInt());
		speed = buffer.readFloat();
		gravityVelocity = buffer.readFloat();
		damage = buffer.readFloat();
		explosionRadius = buffer.readFloat();
	}
	
	protected float getInaccuracy() {
		return DEFAULT_INACCURACY;
	}
	
	Weapon getWeapon() {
		return weapon;
	}

	boolean isDamageableEntity(Entity entity) {
		return false;
	}
}