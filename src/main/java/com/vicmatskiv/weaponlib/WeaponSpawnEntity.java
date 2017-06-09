package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTargetPoint;
import com.vicmatskiv.weaponlib.config.Projectiles;
import com.vicmatskiv.weaponlib.particle.SpawnParticleMessage;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class WeaponSpawnEntity extends EntityProjectile {

    private static final Logger logger = LogManager.getLogger(WeaponSpawnEntity.class);

    private static final String TAG_ENTITY_ITEM = "entityItem";
    private static final String TAG_DAMAGE = "damage";
    private static final String TAG_EXPLOSION_RADIUS = "explosionRadius";

	private float explosionRadius;
	private float damage = 6f;
	private Weapon weapon;

	public WeaponSpawnEntity(World world) {
		super(world);
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
		super(world, player, speed, gravityVelocity, inaccuracy);
		this.weapon = weapon;
		this.damage = damage;
		this.explosionRadius = explosionRadius;
	}

	@Override
	public void onUpdate() {
	    super.onUpdate();
	}

	/**
	 * @see net.minecraft.entity.projectile.EntityThrowable#onImpact(net.minecraft.util.MovingObjectPosition)
	 */
	@Override
	protected void onImpact(CompatibleRayTraceResult position) {

	    if(compatibility.world(this).isRemote) {
	        return;
	    }

	    if(weapon == null) {
	        return;
	    }

	    if(explosionRadius > 0) {
	        Explosion.createServerSideExplosion(weapon.getModContext(), compatibility.world(this), this,
	                position.getHitVec().getXCoord(), position.getHitVec().getYCoord(), position.getHitVec().getZCoord(),
	                explosionRadius, false, true);
	    } else if(position.getEntityHit() != null) {

            Projectiles projectilesConfig = weapon.getModContext().getConfigurationManager().getProjectiles();

	        if(this.getThrower() != null &&
	                (projectilesConfig.isKnockbackOnHit() == null || projectilesConfig.isKnockbackOnHit())) {
	            position.getEntityHit().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
	        } else {
	            position.getEntityHit().attackEntityFrom(compatibility.genericDamageSource(), damage);
	        }

            position.getEntityHit().hurtResistantTime = 0;
            position.getEntityHit().prevRotationYaw -= 0.3D;

            logger.debug("Hit entity {}", position.getEntityHit());

            CompatibleTargetPoint point = new CompatibleTargetPoint(position.getEntityHit().dimension,
                    this.posX, this.posY, this.posZ, 100);

            double magnitude = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) + 2;

            Float bleedingCoefficient = projectilesConfig.getBleedingOnHit();
            if(bleedingCoefficient != null && bleedingCoefficient > 0.0f) {
                int count = (int)(getParticleCount (damage) * bleedingCoefficient);
                logger.debug("Generating {} particle(s) per damage {}", count, damage);
                weapon.getModContext().getChannel().sendToAllAround(new SpawnParticleMessage(
                        SpawnParticleMessage.ParticleType.BLOOD,
                        count,
                        position.getEntityHit().posX - motionX / magnitude,
                        position.getEntityHit().posY - motionY / magnitude,
                        position.getEntityHit().posZ - motionZ / magnitude),
                        point);
            }

	    } else if(position.getTypeOfHit() == CompatibleRayTraceResult.Type.BLOCK) {
	        weapon.onSpawnEntityBlockImpact(compatibility.world(this), null, this, position);
        }

	    this.setDead();
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
	    super.writeSpawnData(buffer);
		buffer.writeInt(Item.getIdFromItem(weapon));
		buffer.writeFloat(damage);
		buffer.writeFloat(explosionRadius);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
	    super.readSpawnData(buffer);
		weapon = (Weapon) Item.getItemById(buffer.readInt());
		damage = buffer.readFloat();
		explosionRadius = buffer.readFloat();
	}

	@Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        Item item = Item.getItemById(tagCompound.getInteger(TAG_ENTITY_ITEM));
        if(item instanceof Weapon) {
            weapon = (Weapon) item;
        }
        damage = tagCompound.getFloat(TAG_DAMAGE);
        explosionRadius = tagCompound.getFloat(TAG_EXPLOSION_RADIUS);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger(TAG_ENTITY_ITEM, Item.getIdFromItem(weapon));
        tagCompound.setFloat(TAG_DAMAGE, damage);
        tagCompound.setFloat(TAG_EXPLOSION_RADIUS, explosionRadius);
    }

	Weapon getWeapon() {
		return weapon;
	}

	boolean isDamageableEntity(Entity entity) {
		return false;
	}

	int getParticleCount(float damage) {
        return (int) (-0.11 * (damage - 30) * (damage - 30) + 100);
    }

	@Override
	public boolean canCollideWithBlock(Block block, CompatibleBlockState metadata) {
	    return !compatibility.isBlockPenetratableByBullets(block) && super.canCollideWithBlock(block, metadata);
	}
}