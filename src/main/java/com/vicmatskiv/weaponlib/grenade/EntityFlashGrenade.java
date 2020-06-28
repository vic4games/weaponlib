package com.vicmatskiv.weaponlib.grenade;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.vicmatskiv.weaponlib.Explosion;
import com.vicmatskiv.weaponlib.LightExposure;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleAxisAlignedBB;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTracing;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFlashGrenade extends AbstractEntityGrenade {

    private static final Logger logger = LogManager.getLogger(EntityFlashGrenade.class);

    public static int MAX_EFFECTIVE_DISTANCE = 15;
    private long explosionTimeout;
    private float explosionStrength;
    private boolean destroyBlocks;
    private long activationTimestamp;

    public int effectiveDistance;

    public static class Builder {

        private long explosionTimeout;
        private float explosionStrength;
        private boolean isDestroyingBlocks = true;
        private long activationTimestamp;
        private EntityLivingBase thrower;
        private ItemGrenade itemGrenade;
        private float velocity = ItemGrenade.DEFAULT_VELOCITY;
        private float gravityVelocity = ItemGrenade.DEFAULT_GRAVITY_VELOCITY;
        private float rotationSlowdownFactor = ItemGrenade.DEFAULT_ROTATION_SLOWDOWN_FACTOR;
        private int effectiveDistance = MAX_EFFECTIVE_DISTANCE;

        public Builder withActivationTimestamp(long activationTimestamp) {
            this.activationTimestamp = activationTimestamp;
            return this;
        }

        public Builder withExplosionTimeout(long explosionTimeout) {
            this.explosionTimeout = explosionTimeout;
            return this;
        }

        public Builder withThrower(EntityLivingBase thrower) {
            this.thrower = thrower;
            return this;
        }

        public Builder withExplosionStrength(float explosionStrength) {
            this.explosionStrength = explosionStrength;
            return this;
        }
        
        public Builder withEffectiveDistance(int effectiveDistance) {
            this.effectiveDistance = effectiveDistance > MAX_EFFECTIVE_DISTANCE ? MAX_EFFECTIVE_DISTANCE : effectiveDistance;
            return this;
        }

        public Builder withGrenade(ItemGrenade itemGrenade) {
            this.itemGrenade = itemGrenade;
            return this;
        }

        public Builder withVelocity(float velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder withGravityVelocity(float gravityVelocity) {
            this.gravityVelocity = gravityVelocity;
            return this;
        }

        public Builder withRotationSlowdownFactor(float rotationSlowdownFactor) {
            this.rotationSlowdownFactor = rotationSlowdownFactor;
            return this;
        }
        
        public Builder withDestroyingBlocks(boolean isDestroyingBlocks) {
            this.isDestroyingBlocks = isDestroyingBlocks;
            return this;
        }

        public EntityFlashGrenade build(ModContext modContext) {
            EntityFlashGrenade entityGrenade = new EntityFlashGrenade(modContext, itemGrenade, thrower, velocity,
                    gravityVelocity, rotationSlowdownFactor);
            entityGrenade.activationTimestamp = activationTimestamp;
            entityGrenade.explosionTimeout = explosionTimeout;
            entityGrenade.explosionStrength = explosionStrength;
            entityGrenade.itemGrenade = itemGrenade;
            entityGrenade.destroyBlocks = isDestroyingBlocks;
            entityGrenade.effectiveDistance = effectiveDistance;

            return entityGrenade;
        }

    }

    private EntityFlashGrenade(ModContext modContext, ItemGrenade itemGrenade, EntityLivingBase thrower, float velocity, float gravityVelocity, float rotationSlowdownFactor) {
        super(modContext, itemGrenade, thrower, velocity, gravityVelocity, rotationSlowdownFactor);
    }

    public EntityFlashGrenade(World world) {
        super(world);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeLong(activationTimestamp);
        buffer.writeLong(explosionTimeout);
        buffer.writeFloat(explosionStrength);
        buffer.writeBoolean(destroyBlocks);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        activationTimestamp = buffer.readLong();
        explosionTimeout = buffer.readLong();
        explosionStrength = buffer.readFloat();
        destroyBlocks = buffer.readBoolean();
    }

    @Override
    public void onGrenadeUpdate() {
        if (!compatibility.world(this).isRemote && explosionTimeout > 0
                && System.currentTimeMillis() > activationTimestamp + explosionTimeout) {
            explode();
            return;
        }
    }

    @Override
    public void onBounce(CompatibleRayTraceResult movingobjectposition) {
//        System.out.println("Bounce");
        if(explosionTimeout == ItemGrenade.EXPLODE_ON_IMPACT && !compatibility.world(this).isRemote) {
            explode();
        } else {
            super.onBounce(movingobjectposition);
        }
    }

    private void explode() {

        logger.debug("Exploding {}", this);
        
        explosionStrength = 0.1f;
        Explosion.createServerSideExplosion(modContext, compatibility.world(this), this,
                this.posX, this.posY, this.posZ, explosionStrength, false, true, false, 1f, 1f, 1.5f, 1f, null, null, 
                modContext.getFlashExplosionSound());


        List<?> nearbyEntities = compatibility.getEntitiesWithinAABBExcludingEntity(compatibility.world(this), this,
                compatibility.getBoundingBox(this).expand(effectiveDistance, effectiveDistance, effectiveDistance));

        Float damageCoefficient = modContext.getConfigurationManager().getExplosions().getDamage();

//        float effectiveRadius = itemGrenade.getEffectiveRadius() * damageCoefficient; // 5 block sphere with this entity as a center

        for(Object nearbyEntityObject: nearbyEntities) {
            Entity nearbyEntity = (Entity)nearbyEntityObject;
            if(nearbyEntity instanceof EntityPlayer) {
                final CompatibleVec3 grenadePos = new CompatibleVec3(this.posX, this.posY, this.posZ);
                BiPredicate<Block, CompatibleBlockState> isCollidable = (block, blockMetadata) -> 
                    block != Blocks.GLASS && block != Blocks.GLASS_PANE && compatibility.canCollideCheck(block, blockMetadata, false);
                
                EntityPlayer player = (EntityPlayer) nearbyEntity;
                Vec3d playerLookVec = player.getLook(1f);
                Vec3d playerEyePosition = player.getPositionEyes(1f);
                Vec3d playerGrenadeVector = playerEyePosition.subtractReverse(new Vec3d(this.posX, this.posY, this.posZ));
                
                double dotProduct = playerLookVec.dotProduct(playerGrenadeVector);
                double cos = dotProduct / 
                        (MathHelper.sqrt(playerLookVec.lengthSquared()) * MathHelper.sqrt(playerGrenadeVector.lengthSquared()));
                
                System.out.println("Cos: " + cos);
                
                float exposureFactor = (float) ((cos + 1f)/ 2f);
                exposureFactor *= exposureFactor;
                
                System.out.println("Exposure factor: " + exposureFactor);
                
                final CompatibleVec3 compatiblePlayerEyePos = new CompatibleVec3(playerEyePosition.x, playerEyePosition.y, playerEyePosition.z);
                CompatibleRayTraceResult rayTraceResult = CompatibleRayTracing.rayTraceBlocks(compatibility.world(this), grenadePos, compatiblePlayerEyePos, isCollidable);

                if(rayTraceResult == null) {
                    float dose = exposureFactor * (1f - (float)playerGrenadeVector.lengthSquared() / (effectiveDistance * effectiveDistance));
                    if(dose < 0) {
                        dose = 0f;
                    }
                    LightExposure exposure = CompatibleExposureCapability.getExposure(nearbyEntity, LightExposure.class);
                    if(exposure == null) {
                        System.out.println("Entity " + nearbyEntity + " exposed to light dose " + dose);
                        exposure = new LightExposure(compatibility.world(nearbyEntity).getTotalWorldTime(), 400, dose);
                        CompatibleExposureCapability.updateExposure(nearbyEntity, exposure);
                    } else {
                        float totalDose = exposure.getTotalDose() + dose;
                        if(totalDose > 1f) {
                            totalDose = 1f;
                        }
                        System.out.println("Entity " + nearbyEntity + " exposed to light dose " + totalDose);
                        exposure.setTotalDose(totalDose);
                        CompatibleExposureCapability.updateExposure(nearbyEntity, exposure);
                    }
                } else {
                    CompatibleVec3 hitVec = rayTraceResult.getHitVec();
                    System.out.println("Hit vec: " + hitVec);
                }
            }
        }
        
        this.setDead();
    }

    public ItemGrenade getItemGrenade() {
        return itemGrenade;
    }
}
