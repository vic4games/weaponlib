package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

public class CompatibleRayTraceResult {
	
	public static enum Type {
		MISS,
		BLOCK,
		ENTITY;
	};

	private MovingObjectPosition position;

	public CompatibleRayTraceResult(MovingObjectPosition position) {
		this.position = position;
	}

	protected MovingObjectPosition getPosition() {
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
		return position.blockX;
	}
	
	public int getBlockPosY() {
		return position.blockY;
	}
	
	public int getBlockPosZ() {
		return position.blockZ;
	}
	
	public int getSideHit() {
	    return position.sideHit;
	}

    public CompatibleBlockPos getBlockPos() {
        return new CompatibleBlockPos(position.blockX, position.blockY, position.blockZ);
    }
}
