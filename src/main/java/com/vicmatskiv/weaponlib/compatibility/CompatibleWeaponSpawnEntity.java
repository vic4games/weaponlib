package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class CompatibleWeaponSpawnEntity extends EntityThrowable implements IEntityAdditionalSpawnData {
	
	public CompatibleWeaponSpawnEntity(World world) {
		super(world);
	}

	public CompatibleWeaponSpawnEntity(World par1World, EntityLivingBase player) {
		super(par1World, player);
	}

	@Override
	protected final float func_70182_d() {
		return getVelocity();
	};


	@Override
	protected final void onImpact(MovingObjectPosition position) {
		onImpact(new CompatibleRayTraceResult(position));
	}
	
	protected abstract void onImpact(CompatibleRayTraceResult rayTraceResult);
	
	@Override
	public final void setThrowableHeading(double motionX, double motionY, double motionZ, float velocity, float ignoredInaccuracy) {
		setCompatibleThrowableHeading(motionX, motionY, motionZ, velocity, getInaccuracy());
	}
	
	protected abstract void setCompatibleThrowableHeading(double motionX, double motionY, double motionZ, float velocity, float ignoredInaccuracy);
	
	protected abstract float getInaccuracy();

	protected abstract float getVelocity();
}