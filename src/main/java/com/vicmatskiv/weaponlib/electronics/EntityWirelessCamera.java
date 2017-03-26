package com.vicmatskiv.weaponlib.electronics;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleThrowableEntity;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;
import com.vicmatskiv.weaponlib.tracking.SyncPlayerEntityTrackerMessage;
import com.vicmatskiv.weaponlib.tracking.TrackableEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class EntityWirelessCamera extends CompatibleThrowableEntity {
    private ModContext modContext;
    private long trackingDuration = 10 * 1000 * 60;
    
    public EntityWirelessCamera(ModContext modContext, World world, EntityPlayer player) {
        super(world, player);
        this.modContext = modContext;
    }

    public EntityWirelessCamera(World world, EntityLivingBase player) {
        super(world, player);
    }
    
    public EntityWirelessCamera(World world) {
        super(world);
    }

    protected void onImpact(CompatibleRayTraceResult rayTraceResult) {
        // if (rayTraceResult.getEntityHit() != null) {
        // byte b0 = 0;
        //
        // if (rayTraceResult.getEntityHit() instanceof EntityBlaze)
        // {
        // b0 = 3;
        // }
        //
        // rayTraceResult.getEntityHit().attackEntityFrom(DamageSource.causeThrownDamage(this,
        // this.getThrower()), (float)b0);
        // }

        Entity entityHit = rayTraceResult.getEntityHit();
        System.out.println("Player " + getThrower() + " hit entity: " + rayTraceResult.getEntityHit());

        if (entityHit != null && getThrower() instanceof EntityPlayer) {
            if (!this.worldObj.isRemote) {
                System.out.println("Server hit entity uuid " + rayTraceResult.getEntityHit().getPersistentID());
                PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) getThrower());
                if(tracker != null) {
                    tracker.addTrackableEntity(new TrackableEntity(entityHit, System.currentTimeMillis(),
                            trackingDuration));
                    modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                            (EntityPlayerMP)getThrower());
                }
            }
        } else if (getThrower() instanceof EntityPlayer) {
//            ExtendedPlayerProperties properties = ExtendedPlayerProperties.getProperties((EntityPlayer) getThrower());
//            System.out.println("Currently tracking " + properties.getTrackableEntitites());
        }

        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setCompatibleThrowableHeading(double motionX, double motionY, double motionZ, float velocity,
            float inaccuracy) {
        float f2 = compatibility.getMathHelper().sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= (double) f2;
        motionY /= (double) f2;
        motionZ /= (double) f2;
        motionX += this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        motionY += this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        motionZ += this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        motionX *= (double) velocity;
        motionY *= (double) velocity;
        motionZ *= (double) velocity;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        float f3 = compatibility.getMathHelper().sqrt_double(motionX * motionX + motionZ * motionZ);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(motionY, (double) f3) * 180.0D / Math.PI);

    }

    @Override
    protected float getInaccuracy() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected float getVelocity() {
        return 0.5f;
    }
}