package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class WeaponSpawnEntity extends EntityThrowable implements IEntityAdditionalSpawnData {
	
	private float explosionRadius = 0.1F;
	private float damage = 6f;
	private float speed;
	private float gravityVelocity;
	private Weapon weapon;

	public WeaponSpawnEntity(World world) {
		super(world);
//		setSize(0.5f, 0.5f);
	}


	/**
	 * @param par1World
	 * @param arg1EntityLivingBase
	 */
	public WeaponSpawnEntity(World par1World, EntityLivingBase arg1EntityLivingBase) {
		super(par1World, arg1EntityLivingBase);
//		setSize(0.5f, 0.5f);
	}
	
//	public WeaponSpawnEntity(World par1World, EntityLivingBase arg1EntityLivingBase, float damage, float gravityVelocity,
//			float explosionRadius) {
//		this(par1World, arg1EntityLivingBase, damage, gravityVelocity, explosionRadius, DEFAULT_SPEED);
//	}
	
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
	
	@Override
	protected float func_70182_d() {
		return speed;
	};

	/**
	 * @see net.minecraft.entity.projectile.EntityThrowable#onImpact(net.minecraft.util.MovingObjectPosition)
	 */
	@Override
	protected void onImpact(MovingObjectPosition position) {
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
				this.worldObj.createExplosion(this, position.blockX, position.blockY, position.blockZ, explosionRadius, true);
			} else if(position.typeOfHit == MovingObjectType.BLOCK) {
				weapon.onSpawnEntityBlockImpact(worldObj, null, this, position);
			}
			this.setDead();
		}
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
	
	Weapon getWeapon() {
		return weapon;
	}

	boolean isDamageableEntity(Entity entity) {
		return false;
	}
}