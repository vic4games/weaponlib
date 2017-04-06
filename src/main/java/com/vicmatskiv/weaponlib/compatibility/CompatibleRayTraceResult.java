package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

public class CompatibleRayTraceResult {
	public static enum Type {
		MISS,
		BLOCK,
		ENTITY;
	};

	private RayTraceResult position;

	public CompatibleRayTraceResult(RayTraceResult position) {
		this.position = position;
	}

	protected RayTraceResult getPosition() {
		return position;
	}

	public Entity getEntityHit() {
		return position.entityHit;
	}

	public Type getTypeOfHit() {
		Type result = null;
		switch(position.typeOfHit) {
		case BLOCK: result = Type.BLOCK; break;
		case ENTITY: result = Type.ENTITY; break;
		case MISS: result = Type.MISS; break;
		}
		return result;
	}
	
	public int getBlockPosX() {
		return position.getBlockPos().getX();
	}
	
	public int getBlockPosY() {
		return position.getBlockPos().getY();
	}
	
	public int getBlockPosZ() {
		return position.getBlockPos().getZ();
	}
	
	
}
