package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTargetPoint;
import com.vicmatskiv.weaponlib.compatibility.CompatibleThrowableEntity;
import com.vicmatskiv.weaponlib.particle.SpawnParticleMessage;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class WeaponSpawnEntity extends CompatibleThrowableEntity {
	
	static final float DEFAULT_INACCURACY = 1f;
	private float explosionRadius = 0.1F;
	private float damage = 6f;
	private float speed;
	private float gravityVelocity;
	private float inaccuracy;
	private Weapon weapon;

	public WeaponSpawnEntity(World world) {
		super(world);
	}

	public WeaponSpawnEntity(World par1World, EntityLivingBase arg1EntityLivingBase) {
		super(par1World, arg1EntityLivingBase);
	}
	
	public WeaponSpawnEntity(Weapon weapon, 
			World world, 
			EntityLivingBase player, 
			float speed,
			float gravityVelocity,
			float inaccuracy,
			float damage, 
			float explosionRadius,
			Material...damageableBlockMaterials) 
	{
		super(world, player);
		this.weapon = weapon;
		this.damage = damage;
		this.speed = speed;
		this.explosionRadius = explosionRadius;
		this.inaccuracy = inaccuracy;
		this.gravityVelocity = gravityVelocity;

		// TODO: validate for 1.7.10 the code below
		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
		this.posX -= (double)(compatibility.getMathHelper().cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.posY -= 0.10000000149011612D;
		this.posZ -= (double)(compatibility.getMathHelper().sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		float f = 0.4F;
		this.motionX = (double)(-compatibility.getMathHelper().sin(this.rotationYaw / 180.0F * (float)Math.PI) * compatibility.getMathHelper().cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
		this.motionZ = (double)(compatibility.getMathHelper().cos(this.rotationYaw / 180.0F * (float)Math.PI) * compatibility.getMathHelper().cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
		float pitchOffset = 0f;
		this.motionY = (double)(-compatibility.getMathHelper().sin((this.rotationPitch + pitchOffset) / 180.0F * (float)Math.PI) * f);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, inaccuracy);
	}
	
	@Override
	public void onUpdate() {
	    System.out.println("Entity updated to " + this);
	    super.onUpdate();
	}

	@Override
	protected float getGravityVelocity() {
		return gravityVelocity;
	};
	
	@Override 
	protected float getVelocity() {
		return speed;
	}

	@Override
	protected float getInaccuracy() {
		return DEFAULT_INACCURACY;
	}

	/**
	 * @see net.minecraft.entity.projectile.EntityThrowable#onImpact(net.minecraft.util.MovingObjectPosition)
	 */
	@Override
	protected void onImpact(CompatibleRayTraceResult position) {
		//if(!compatibility.world(this).isRemote) {
			if (position.getEntityHit() != null && position.getEntityHit() != this.getThrower()) {
				if(explosionRadius > 0) {
					compatibility.world(this).createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, true);
				}

				position.getEntityHit().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
				position.getEntityHit().hurtResistantTime = 0;
				position.getEntityHit().prevRotationYaw -= 0.3D;
				
				System.out.println("Hit entity: " + position.getEntityHit());
				
	            CompatibleTargetPoint point = new CompatibleTargetPoint(position.getEntityHit().dimension, 
	                    this.posX, this.posY, this.posZ, 100);

	            System.out.printf("Last tick pos at %.2f %.2f %.2f\n", 
                        lastTickPosX, lastTickPosY, lastTickPosZ);
	            
	            double magnitude = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) + 2;
	            
				weapon.getModContext().getChannel().sendToAllAround(new SpawnParticleMessage(
				        position.getEntityHit().posX - motionX / magnitude, 
				        position.getEntityHit().posY - motionY / magnitude, 
				        position.getEntityHit().posZ - motionZ / magnitude),
				        point);
				
//				this.worldObj.spawnParticle("snowballpoof", position.getEntityHit().posX - motionX / magnitude, 
//                        position.getEntityHit().posY - motionY / magnitude, 
//                        position.getEntityHit().posZ - motionZ / magnitude, 0.0D, 0.0D, 0.0D);
//				
//				for (int i = 0; i < 10; ++i) {
//                    this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
//                }
				
//				for (int i = 0; i < 20; ++i) {
//	                this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY - 1, this.posZ + 1, 0.0D, 0.0D, 0.0D);
//	            }
				
			} else if(explosionRadius > 0) {
				compatibility.world(this).createExplosion(this, position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ(), explosionRadius, true);
			} else if(position.getTypeOfHit() == CompatibleRayTraceResult.Type.BLOCK) {
				weapon.onSpawnEntityBlockImpact(compatibility.world(this), null, this, position);
			}
			
			
        
			if (!compatibility.world(this).isRemote) {
			    this.setDead();
			}
			
		//}
//		if(position.getEntityHit() != null) {
//		    for (int i = 0; i < 20; ++i) {
//		        this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
//		    }
//		}
       
	}
	
	@Override
	public void setCompatibleThrowableHeading(double motionX, double motionY, double motionZ, float velocity, float inaccuracy)
    {
        float f2 = compatibility.getMathHelper().sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
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
        float f3 = compatibility.getMathHelper().sqrt_double(motionX * motionX + motionZ * motionZ);
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
}