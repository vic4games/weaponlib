package com.vicmatskiv.weaponlib.grenade;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.EntityBounceable;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityGrenade extends EntityBounceable {

    private long explosionTimeout;
    private float explosionStrength;
    private long activationTimestamp;

    public static class Builder {
        private long explosionTimeout;
        private float explosionStrength;
        private long activationTimestamp;
        private EntityLivingBase thrower;

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

        public EntityGrenade build(ModContext modContext) {
            EntityGrenade entityGrenade = new EntityGrenade(modContext, thrower);
            entityGrenade.activationTimestamp = activationTimestamp;
            entityGrenade.explosionTimeout = explosionTimeout;
            entityGrenade.explosionStrength = explosionStrength;
            return entityGrenade;
        }
    }

    private EntityGrenade(ModContext modContext, EntityLivingBase thrower) {
        super(modContext, compatibility.world(thrower), thrower);
    }

    public EntityGrenade(World world) {
        super(world);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeLong(activationTimestamp);
        buffer.writeLong(explosionTimeout);
        buffer.writeFloat(explosionStrength);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        activationTimestamp = buffer.readLong();
        explosionTimeout = buffer.readLong();
        explosionStrength = buffer.readFloat();
    }

    @Override
    public void onUpdate() {
        if (!compatibility.world(this).isRemote && explosionTimeout > 0
                && System.currentTimeMillis() > activationTimestamp + explosionTimeout) {
            explode();
            return;
        } else {
            super.onUpdate();
        }
    }

    @Override
    public void onBounce(CompatibleRayTraceResult movingobjectposition) {
        if(explosionTimeout == ItemGrenade.EXPLODE_ON_IMPACT) {
            explode();
        }
    }

    private void explode() {
        compatibility.world(this).createExplosion(this, this.posX, this.posY, this.posZ, explosionStrength, true);
        this.setDead();
    }
}
