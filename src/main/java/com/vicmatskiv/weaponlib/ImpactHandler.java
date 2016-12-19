package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@FunctionalInterface
public interface ImpactHandler {

	public void onImpact(World world, EntityPlayer player, WeaponSpawnEntity entity, RayTraceResult position);
}
